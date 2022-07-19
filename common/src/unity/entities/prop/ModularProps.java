package unity.entities.prop;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.entities.type.PUUnitTypeCommon.*;
import unity.gen.entities.*;
import unity.parts.*;
import unity.parts.types.*;
import unity.util.*;

public class ModularProps extends Props{
    public final UnitType parent;
    public final Seq<String> templates = new Seq<>();
    public TextureRegion payloadCellRegion;

    public ModularProps(UnitType parent, String... templates){
        this.parent = parent;
        this.templates.add(templates);
    }

    @Override
    public void load(){
        payloadCellRegion = Core.atlas.find(parent.name + "-cell-payload", parent.cellRegion);
    }

    @Override
    public void drawCell(Unit unit){
        if(unit.isAdded()){
            if(unit instanceof Modularc){
                drawModularCell((Unit & Modularc)unit);
                return;
            }
            parent.applyColor(unit);

            Draw.color(parent.cellColor(unit));
            Draw.rect(parent.cellRegion, unit.x, unit.y, unit.rotation - 90);
            Draw.reset();
        }else{
            //As payload.
            if(unit instanceof Modularc){
                drawModularBody((Unit & Modularc)unit);
                drawModularCell((Unit & Modularc)unit);
                parent.drawWeapons(unit);
                return;
            }
            parent.applyColor(unit);

            Draw.color(parent.cellColor(unit));
            Draw.rect(payloadCellRegion, unit.x, unit.y, unit.rotation - 90);
            Draw.reset();
        }
    }

    public <T extends Unit&Modularc> void drawModularCell(T unit){
        parent.applyColor(unit);
        Draw.color(parent.cellColor(unit));
        DrawTransform dt = new DrawTransform(new Vec2(unit.x, unit.y), unit.rotation);
        var construct = unit.construct();
        if(construct != null){
            ModularWheelType.rollDistance = unit.driveDist();
            construct.hasCustomDraw.each((p) -> {
                p.type.drawCell(dt, p);
            });
        }
        Draw.reset();
    }

    public <T extends Unit&Modularc> void drawModularBody(T unit){
        parent.applyColor(unit);
        DrawTransform dt = new DrawTransform(new Vec2(unit.x, unit.y), unit.rotation);
        var construct = unit.construct();
        if(construct != null){
            ModularWheelType.rollDistance = unit.driveDist();
            unit.doodadlist().each(d -> {
                d.drawOutline(dt);
            });
            construct.hasCustomDraw.each((p) -> {
                p.type.drawOutline(dt, p);
            });
            construct.hasCustomDraw.each((p) -> {
                p.type.draw(dt, p);
            });
            unit.doodadlist().each(d -> {
                d.drawTop(dt);
            });
            construct.hasCustomDraw.each((p) -> {
                p.type.drawTop(dt, p);
            });
        }else{
            if(unit.constructdata() != null && unit.constructdata().length > 0){
                unit.construct(new ModularConstruct(unit.constructdata()));
                UnitDoodadGenerator.initDoodads(unit.construct().parts.length, unit.doodadlist(), unit.construct());
            }
        }
        Draw.reset();
    }
}

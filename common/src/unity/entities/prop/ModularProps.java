package unity.entities.prop;

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

    public ModularProps(UnitType parent, String... templates){
        super(true);
        this.parent = parent;
        this.templates.add(templates);
    }

    @Override
    public boolean drawCell(Unit unit){
        if(unit.isAdded()){
            if(unit instanceof Modularc){
                drawModularCell((Unit & Modularc)unit);
                return false;
            }
        }else{
            //As payload.
            if(unit instanceof Modularc){
                drawModularBody((Unit & Modularc)unit);
                drawModularCell((Unit & Modularc)unit);
                parent.drawWeapons(unit);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean drawBody(Unit unit){
        if(unit instanceof Modularc){
            drawModularBody((Unit & Modularc)unit);
            return false;
        }
        return true;
    }

    @Override
    public boolean drawSoftShadow(Unit unit, float alpha){
        if(unit instanceof Modularc){
            drawModularBodySoftShadow((Unit & Modularc)unit, alpha);
            return false;
        }
        return true;
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

    public <T extends Unit&Modularc> void drawModularBodySoftShadow(T unit, float alpha){
        Draw.color(0, 0, 0, 0.4f * alpha);
        float rad = 1.6f;
        float size = unit.hitSize;
        Draw.rect(parent.softShadowRegion, unit, size * rad * Draw.xscl, size * rad * Draw.yscl, unit.rotation - 90);
        Draw.color();
    }
}

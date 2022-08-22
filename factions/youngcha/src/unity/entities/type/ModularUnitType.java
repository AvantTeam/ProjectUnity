package unity.entities.type;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import mindustry.gen.*;
import unity.gen.entities.*;
import unity.util.*;

public class ModularUnitType extends PUUnitType{
    public ModularUnitType(String name){
        super(name);
    }

    @Override
    public void drawCell(Unit unit){
        if(unit.isAdded()){
            if(unit instanceof Modularc){
                drawModularCell((Unit & Modularc)unit);
                return;
            }
        }else{
            //As payload.
            if(unit instanceof Modularc){
                drawModularBody((Unit & Modularc)unit);
                drawModularCell((Unit & Modularc)unit);
                drawWeapons(unit);
                return;
            }
        }
        super.drawCell(unit);
    }

    @Override
    public void drawBody(Unit unit){
        if(unit instanceof Modularc) drawModularBody((Unit & Modularc)unit);
        else super.drawBody(unit);
    }

    @Override
    public void drawSoftShadow(Unit unit, float alpha){
        if(unit instanceof Modularc) drawModularBodySoftShadow((Unit & Modularc)unit, alpha);
        else super.drawSoftShadow(unit, alpha);
    }

    public <T extends Unit&Modularc> void drawModularCell(T unit){
        super.applyColor(unit);
        Draw.color(super.cellColor(unit));
        DrawTransform dt = new DrawTransform(new Vec2(unit.x, unit.y), unit.rotation);
        var construct = unit.construct();
        if(construct != null){
            construct.hasCustomDraw.each(p -> p.type.drawCell(dt, p));
        }
        Draw.reset();
    }

    public <T extends Unit&Modularc> void drawModularBody(T unit){
        super.applyColor(unit);
        DrawTransform dt = new DrawTransform(new Vec2(unit.x, unit.y), unit.rotation);
        var construct = unit.construct();
        if(construct != null){
            construct.doodads.each(d -> d.drawOutline(dt));
            construct.hasCustomDraw.each(p -> {
                p.type.drawOutline(dt, p);
                p.type.draw(dt, p, unit);
            });
            construct.doodads.each(d -> d.drawTop(dt));
            construct.hasCustomDraw.each(p -> p.type.drawTop(dt, p));
        }
        Draw.reset();
    }

    public <T extends Unit&Modularc> void drawModularBodySoftShadow(T unit, float alpha){
        Draw.color(0, 0, 0, 0.4f * alpha);
        float rad = 1.6f;
        float size = unit.hitSize;
        Draw.rect(softShadowRegion, unit, size * rad * Draw.xscl, size * rad * Draw.yscl, unit.rotation - 90);
        Draw.color();
    }
}

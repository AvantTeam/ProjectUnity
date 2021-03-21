package unity.type;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.entities.units.*;

public class InvisibleUnitType extends UnityUnitType{
    public Color tint = Color.red;

    public InvisibleUnitType(String name){
        super(name);
    }

    protected float fade(EndInvisibleUnit unit){
        return Mathf.clamp(1f - unit.alphaLerp, 0.1f, 1f);
    }

    @Override
    public void drawOutline(Unit unit){
        if(!(unit instanceof EndInvisibleUnit e)){
            super.drawOutline(unit);
            return;
        }
        Tmp.c1.set(Color.white).lerp(tint, Mathf.lerp(0f, 0.5f, e.alphaLerp));
        Draw.color(Tmp.c1);
        Draw.alpha(1f - e.alphaLerp);

        if(Core.atlas.isFound(outlineRegion)){
            Draw.rect(outlineRegion, unit.x, unit.y, unit.rotation - 90);
        }
    }

    @Override
    public Color cellColor(Unit unit){
        if(unit instanceof EndInvisibleUnit e) return super.cellColor(unit).a(fade(e));
        return super.cellColor(unit);
    }

    @Override
    public void drawEngine(Unit unit){
        if(!unit.isFlying()) return;

        float scale = unit.elevation;
        float offset = engineOffset/2f + engineOffset/2f*scale;

        if(unit instanceof Trailc){
            Trail trail = ((Trailc)unit).trail();
            trail.draw(unit.team.color, (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * scale) * trailScl);
        }

        Draw.color(unit.team.color);
        if(unit instanceof EndInvisibleUnit e) Draw.alpha(fade(e));
        Fill.circle(
        unit.x + Angles.trnsx(unit.rotation + 180, offset),
        unit.y + Angles.trnsy(unit.rotation + 180, offset),
        (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f)) * scale
        );
        Draw.color(Color.white);
        if(unit instanceof EndInvisibleUnit e) Draw.alpha(fade(e));
        Fill.circle(
        unit.x + Angles.trnsx(unit.rotation + 180, offset - 1f),
        unit.y + Angles.trnsy(unit.rotation + 180, offset - 1f),
        (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f)) / 2f  * scale
        );
        Draw.color();
    }

    @Override
    public void drawSoftShadow(Unit unit){
        if(!(unit instanceof EndInvisibleUnit e)){
            super.drawSoftShadow(unit);
            return;
        }
        Draw.color(0, 0, 0, 0.4f * fade(e));
        float rad = 1.6f;
        float size = Math.max(region.width, region.height) * Draw.scl;
        Draw.rect(softShadowRegion, unit, size * rad, size * rad);
        Draw.color();
    }

    @Override
    public void drawShadow(Unit unit){
        if(!(unit instanceof EndInvisibleUnit e)){
            super.drawShadow(unit);
            return;
        }

        Draw.color(Pal.shadow);
        Draw.alpha(Pal.shadow.a * fade(e));
        float el = Math.max(unit.elevation, visualElevation);
        Draw.rect(shadowRegion, unit.x + shadowTX * el, unit.y + shadowTY * el, unit.rotation - 90);
        Draw.color();
    }

    @Override
    public void drawLight(Unit unit){
        if(!(unit instanceof EndInvisibleUnit e)){
            super.drawLight(unit);
            return;
        }
        if(lightRadius > 0){
            Drawf.light(unit.team, unit.x, unit.y, lightRadius, lightColor, lightOpacity * (1f - e.alphaLerp));
        }
    }

    @Override
    public void applyColor(Unit unit){
        if(!(unit instanceof EndInvisibleUnit e)){
            super.applyColor(unit);
            return;
        }
        float lerp = Mathf.lerp(1f, 0.1f, e.alphaLerp);
        Tmp.c1.set(Color.white).lerp(tint, Mathf.lerp(0f, 0.5f, e.alphaLerp));
        Draw.color(Tmp.c1);
        Draw.alpha(lerp);
        Draw.mixcol(Color.white, unit.hitTime);
        if(unit.drownTime > 0 && unit.floorOn().isDeep()){
            Draw.mixcol(unit.floorOn().mapColor, unit.drownTime * 0.8f);
        }
    }
}

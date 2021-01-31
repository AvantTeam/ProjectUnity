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

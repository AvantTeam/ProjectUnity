package unity.type.decal;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import unity.content.effects.*;

import static arc.graphics.g2d.Draw.*;

public class CapeDecorationType extends UnitDecorationType{
    public String name;
    public TextureRegion region;

    public Floatf<Unit> swayExt = Unit::elevation;
    public Effect trailEffect = TrailFx.capeTrail;
    public float
        x, y,
        swayAmount = -45f, swaySpeed = 0.1f,
        shakeAmount = 2.5f, shakeScl = 5f,
        alphaFrom = 0f, alphaTo = 0.8f, alphaShake = 0.2f, alphaScl = 2f,
        rotCone = 60f, rotSpeed = 0.1f;

    public CapeDecorationType(String name){
        this.name = name;

        top = true;
        decalType = CapeDecoration::new;
    }

    @Override
    public void load(){
        region = Core.atlas.find(name);
    }

    @Override
    public void update(Unit unit, UnitDecoration deco){
        if(!(deco instanceof CapeDecoration cape)) return;

        float val = swayExt.get(unit);
        float sway = Mathf.lerpDelta(cape.sway, val * swayAmount, swaySpeed);
        sway += Mathf.sin(shakeScl, shakeAmount * val);

        cape.alpha = Mathf.clamp(alphaFrom + val * (alphaTo - alphaFrom) + Mathf.sin(alphaScl, alphaShake * val));
        cape.sway = sway;
        cape.rotation = Angles.clampRange(Angles.moveToward(cape.rotation, unit.rotation, rotSpeed), unit.rotation, rotCone / 2f);

        trailEffect.at(unit.x, unit.y, cape.rotation, new CapeEffectData(this, cape.alpha * 0.25f, cape.sway));
    }

    @Override
    public void draw(Unit unit, UnitDecoration deco){
        if(!(deco instanceof CapeDecoration cape)) return;

        unit.type.applyColor(unit);
        Draw.alpha(cape.alpha);
        Draw.blend(Blending.additive);

        for(int sign : Mathf.signs){
            Tmp.v1.trns(cape.rotation - 90f, x * sign, y);
            Draw.rect(
                region,
                unit.x + Tmp.v1.x, unit.y + Tmp.v1.y,
                region.width * scl * sign,
                region.height * scl,
                cape.rotation + cape.sway * sign - 90f
            );
        }

        Draw.blend(Blending.normal);
    }

    @Override
    public void added(Unit unit, UnitDecoration deco){
        if(!(deco instanceof CapeDecoration cape)) return;
        cape.rotation = unit.rotation;
    }

    public static class CapeDecoration extends UnitDecoration{
        public float alpha, sway, rotation;

        public CapeDecoration(UnitDecorationType type){
            super(type);
        }
    }

    public static class CapeEffectData{
        public CapeDecorationType type;
        public float alpha, sway;

        public CapeEffectData(CapeDecorationType type, float alpha, float sway){
            this.type = type;
            this.alpha = alpha;
            this.sway = sway;
        }
    }
}

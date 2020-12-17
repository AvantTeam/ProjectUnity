package unity.entities.abilities;

import arc.*;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;

import static arc.Core.*;

public class LightningSpawnAbility extends Ability{
    protected float timerTarget = 48f, rotateSpeed = 2f, phase, phaseSpeed = 0.05f,
        lightningRange = 120f, lightningOffset = 56f, lightningDamage = 100f, lightningRadius = 18f;
    protected int lightningCount = 8;
    protected Sound lightningSound = Sounds.spark;
    protected Color lightningColor = Pal.lancerLaser, backColor = Pal.lancerLaser.cpy().a(0.5f), frontColor = Color.white.cpy().a(0.8f);
    protected Effect shootEffect;

    protected final Interval timer = new Interval(1);

    {
        shootEffect = UnityFx.lightningSpawnShoot;
        //reset
        timer.get(timerTarget);
    }

    @Override
    public void update(Unit unit){
        if(timer.get(timerTarget)){
            if(phase > 0f){
                for(int i = 0; i < lightningCount; i++){
                    Tmp.v1.trns(
                        (Time.time + rotateSpeed + 360f * i / (float)lightningCount + Mathf.randomSeed(unit.id)) * Mathf.signs[unit.id % 2],
                        lightningOffset * phase
                    ).add(unit);;
                    Teamc u = Units.closestTarget(unit.team, Tmp.v1.x, Tmp.v1.y, lightningRange);
                    if(u != null){
                        float angle = Tmp.v2.set(u).sub(unit).angle();
                        Lightning.create(unit.team, lightningColor, lightningDamage, Tmp.v1.x, Tmp.v1.y, angle, (int)(lightningRange / 6f));
                        lightningSound.at(Tmp.v1.x, Tmp.v1.y, Mathf.random(0.8f, 1.2f));
                        shootEffect.at(Tmp.v1.x, Tmp.v1.y, angle);
                    }
                }
            }
        }
        phase = Mathf.lerpDelta(phase, unit.ammof(), phaseSpeed);
        if(phase < 0.01f) phase = 0f;
    }

    @Override
    public void draw(Unit unit){
        float z = Draw.z();
        Draw.z(unit.type.groundLayer + Mathf.clamp(unit.type.hitSize / 4000f, 0f, 0.01f) - 0.015f);
        for(int i = 0; i < lightningCount; i++){
            Tmp.v1.trns(
                (Time.time + rotateSpeed + 360f * i / (float)lightningCount + Mathf.randomSeed(unit.id)) * Mathf.signs[unit.id % 2],
                lightningOffset * phase
            ).add(unit);
            TextureRegion region = atlas.find("circle-shadow");
            float r = phase * region.width * Draw.scl + lightningRadius + Mathf.sin(6f, 4f) * phase;
            float x = Tmp.v1.x, y = Tmp.v1.y;
            Draw.color(backColor);
            Draw.rect(region, x, y, r, r);
            Draw.color(frontColor);
            Draw.rect(region, x, y, r / 2f, r / 2f);
            Draw.color(Color.white);
            Draw.rect(region, x, y, r / 3f, r / 3f);
        }
        Draw.z(z);
    }

    @Override
    public String localized(){
        return bundle.get("ability.lightning-spawn-ability");
    }
}

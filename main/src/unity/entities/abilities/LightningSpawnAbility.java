package unity.entities.abilities;

import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import unity.content.*;
import unity.graphics.*;

import static arc.Core.*;
import static mindustry.Vars.*;

/** @author GlennFolker */
public class LightningSpawnAbility extends Ability{
    public float rotateSpeed;

    public int sectors = 5;
    public float phase;
    public float phaseSpeed,
        lightningRange, lightningOffset, lightningDamage, lightningRadius = 18f;

    protected int lightningCount;
    protected boolean useAmmo = true;
    protected Sound shootSound = Sounds.spark;
    protected Color backColor = UnityPal.monolithDark, frontColor = UnityPal.monolithLight;
    protected Effect damageEffect = UnityFx.lightningSpawnShoot, hitEffect = Fx.hitLaserBlast;

    protected float timer, reload;

    public LightningSpawnAbility(int lightningCount, float reload, float rotateSpeed, float phaseSpeed, float lightningRange, float lightningOffset, float lightningDamage){
        this.reload = reload;
        this.rotateSpeed = rotateSpeed;
        this.phaseSpeed = phaseSpeed;

        this.lightningCount = lightningCount;
        this.lightningRange = lightningRange;
        this.lightningOffset = lightningOffset;
        this.lightningDamage = lightningDamage;
    }

    @Override
    public void update(Unit unit){
        if((timer += Time.delta) >= reload){
            if(phase > 0.01f){
                for(int i = 0; i < lightningCount; i++){
                    Tmp.v1.trns(
                        (Time.time + rotateSpeed + 360f * i / (float)lightningCount + Mathf.randomSeed(unit.id)) * Mathf.signs[unit.id % 2],
                        lightningOffset * phase
                    ).add(unit);

                    float x = Tmp.v1.x, y = Tmp.v1.y;
                    var t = Units.closestTarget(unit.team, x, y, lightningRange);
                    if(t instanceof Healthc h){
                        h.damage(lightningDamage);

                        hitEffect.at(h.x(), h.y(), unit.angleTo(h), backColor);
                        damageEffect.at(x, y, 0f, frontColor, h);
                        hitEffect.at(x, y, unit.angleTo(h), backColor);

                        shootSound.at(x, y, Mathf.random(0.8f, 1.2f));

                        if(useAmmo && state.rules.unitAmmo) unit.ammo--;
                    }
                }

                timer = 0f;
            }
        }

        phase = Mathf.lerpDelta(phase, useAmmo && state.rules.unitAmmo ? unit.ammof() : 1f, phaseSpeed);
    }

    @Override
    public void draw(Unit unit){
        float z = Draw.z();
        Draw.z(unit.type.groundLayer + Mathf.clamp(unit.type.hitSize / 4000f, 0f, 0.01f) - 0.015f);

        var back = atlas.find("circle-shadow");
        for(int i = 0; i < lightningCount; i++){
            Tmp.v1.trns(
                (Time.time + rotateSpeed + 360f * i / (float)lightningCount + Mathf.randomSeed(unit.id)) * Mathf.signs[unit.id % 2],
                lightningOffset * phase
            ).add(unit);

            float r = phase * back.width * Draw.scl + lightningRadius + Mathf.sin(6f, 4f) * phase;
            float x = Tmp.v1.x, y = Tmp.v1.y;

            Draw.color(backColor);
            Draw.rect(back, x, y, r, r);

            Draw.color(frontColor);
            Draw.rect(back, x, y, r / 2f, r / 2f);

            Lines.stroke((0.7f + Mathf.absin(15f, 0.7f)), frontColor);
            for(int s = 0; s < sectors; s++){
                Lines.swirl(x, y, r, 0.14f, unit.rotation + s * 360f / sectors - Time.time * rotateSpeed);
            }
        }

        Draw.z(z);
    }

    @Override
    public String localized(){
        return bundle.get("ability.lightning-spawn-ability");
    }
}

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
import mindustry.graphics.*;
import unity.content.effects.*;
import unity.graphics.*;

import static arc.Core.*;
import static mindustry.Vars.*;

/** @author GlennFolker */
public class LightningSpawnAbility extends Ability{
    public float rotateSpeed;

    public int sectors = 6;
    public float phase;
    public float phaseSpeed,
        lightningRange, lightningOffset, lightningDamage,
        lightningRadius = tilesize, lightningOuterRadius = tilesize * 6f,
        trailChance = 0.3f;

    protected int lightningCount;
    protected boolean useAmmo = true;
    protected Sound shootSound = Sounds.spark;
    protected Color backColor = UnityPal.monolithDark.cpy().a(0.5f), frontColor = UnityPal.monolithLight.cpy().a(0.5f);
    protected Effect damageEffect = SpecialFx.chainLightningActive, hitEffect = Fx.hitLaserBlast, trailEffect = ParticleFx.monolithSpark;

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
        timer += Time.delta;
        boolean can = timer >= reload;

        for(int i = 0; i < lightningCount; i++){
            Tmp.v1.trns(
                (Time.time + rotateSpeed + 360f * i / (float)lightningCount + Mathf.randomSeed(unit.id)) * Mathf.signs[unit.id % 2],
                lightningOffset * phase
            ).add(unit);
            float x = Tmp.v1.x, y = Tmp.v1.y;

            if(can){
                timer = 0f;

                Teamc t = Units.closestTarget(unit.team, x, y, lightningRange);
                if(t instanceof Healthc h){
                    h.damage(lightningDamage);

                    hitEffect.at(h.x(), h.y(), unit.angleTo(h), backColor);
                    damageEffect.at(x, y, 2f, frontColor, h);
                    hitEffect.at(x, y, unit.angleTo(h), backColor);

                    shootSound.at(x, y, Mathf.random(0.8f, 1.2f));

                    if(useAmmo && state.rules.unitAmmo) unit.ammo--;
                }
            }

            if(Mathf.chanceDelta(trailChance)) trailEffect.at(x, y, lightningRadius);
        }

        phase = Mathf.lerpDelta(phase, useAmmo && state.rules.unitAmmo ? unit.ammof() : 1f, phaseSpeed);
    }

    @Override
    public void draw(Unit unit){
        float z = Draw.z();
        Draw.z(Layer.bullet);

        TextureRegion shade = atlas.find("circle-shadow");
        for(int i = 0; i < lightningCount; i++){
            Tmp.v1.trns(
                (Time.time + rotateSpeed + 360f * i / (float)lightningCount + Mathf.randomSeed(unit.id)) * Mathf.signs[unit.id % 2],
                lightningOffset * phase
            ).add(unit);

            float out = lightningOuterRadius + Mathf.absin(8f, 0.5f);
            float in = lightningRadius + Mathf.absin(6f, 0.4f);
            float bet = Mathf.lerp(in, out, 0.2f);

            float x = Tmp.v1.x, y = Tmp.v1.y;

            Draw.color(backColor);
            Draw.rect(shade, x, y, lightningOuterRadius * 2f, lightningOuterRadius * 1.8f);
            Draw.color(frontColor);
            Fill.circle(x, y, lightningRadius);

            Lines.stroke((2f + Mathf.absin(15f, 0.7f)), Tmp.c1.set(backColor).lerp(frontColor, 0.7f));
            for(int s = 0; s < sectors; s++){
                Lines.arc(x, y, bet - 2f, 0.1f, s * 360f / sectors + Time.time * rotateSpeed * Mathf.signs[unit.id % 2]);
            }

            Lines.stroke(Lines.getStroke() - 1f, frontColor);
            for(int s = 0; s < sectors; s++){
                Lines.arc(x, y, bet, 0.14f, s * 360f / sectors + Time.time * rotateSpeed * Mathf.signs[(unit.id + 1) % 2]);
            }

            Drawf.light(x, y, lightningOuterRadius * 2f, frontColor, phase);
        }

        Draw.z(z);
    }

    @Override
    public String localized(){
        return bundle.get("ability.lightningspawn");
    }
}

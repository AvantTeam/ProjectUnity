package unity.entities.bullet;

import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import unity.graphics.*;
import unity.util.*;

public class LightningTurretBulletType extends BulletType{
    public float range = 100f, reload = 15f, duration = 120f, size = 9f;
    public Effect lightningEffect = Fx.chainLightning;
    public Sound lightningSound = Sounds.none;
    public Color color = Pal.lancerLaser;
    private static Healthc tmp;

    public LightningTurretBulletType(float speed, float damage){
        super(speed, damage);
        scaleVelocity = true;
    }

    @Override
    public void update(Bullet b){
        if(b.fdata <= 0f){
            super.update(b);
        }else{
            if(b.timer(1, reload)){
                Seq<Healthc> seq = Utils.nearbyEnemySorted(b.team, b.x, b.y, range, 1f);
                for(int i = 0; i < Math.min(seq.size, lightning); i++){
                    tmp = seq.get(i);
                    Vars.world.raycastEachWorld(b.x, b.y, tmp.x(), tmp.y(), (cx, cy) -> {
                        Building bl = Vars.world.build(cx, cy);
                        if(bl != null && bl.block.absorbLasers){
                            tmp = bl;
                            return true;
                        }
                        return false;
                    });
                    lightningSound.at(b.x, b.y, Mathf.random(0.9f, 1.1f));
                    lightningEffect.at(b.x, b.y, 0f, lightningColor, tmp);
                    tmp.damage(lightningDamage);
                    hit(b, tmp.x(), tmp.y());
                    if(tmp instanceof Unit u){
                        u.apply(status, statusDuration);
                    }
                }
                seq.clear();
            }
        }
    }

    @Override
    public void despawned(Bullet b){
        if(b.fdata > 0f){
            super.despawned(b);
        }else{
            hit(b, b.x, b.y);
        }
    }

    @Override
    public void hit(Bullet b, float x, float y){
        hitEffect.at(x, y, b.rotation(), hitColor);
        hitSound.at(x, y, hitSoundPitch, hitSoundVolume);

        Effect.shake(hitShake, hitShake, b);

        if(b.fdata > 0f){
            if(fragBullet != null){
                for(int i = 0; i < fragBullets; i++){
                    float len = Mathf.random(1f, 7f);
                    float a = b.rotation() + Mathf.range(fragCone / 2) + fragAngle;
                    fragBullet.create(b, x + Angles.trnsx(a, len), y + Angles.trnsy(a, len), a, Mathf.random(fragVelocityMin, fragVelocityMax), Mathf.random(fragLifeMin, fragLifeMax));
                }
            }

            if(puddleLiquid != null && puddles > 0){
                for(int i = 0; i < puddles; i++){
                    Tile tile = Vars.world.tileWorld(x + Mathf.range(puddleRange), y + Mathf.range(puddleRange));
                    Puddles.deposit(tile, puddleLiquid, puddleAmount);
                }
            }

            if(incendChance > 0 && Mathf.chance(incendChance)){
                Damage.createIncend(x, y, incendSpread, incendAmount);
            }

            if(splashDamageRadius > 0 && !b.absorbed){
                Damage.damage(b.team, x, y, splashDamageRadius, splashDamage * b.damageMultiplier(), collidesAir, collidesGround);

                if(status != StatusEffects.none){
                    Damage.status(b.team, x, y, splashDamageRadius, status, statusDuration, collidesAir, collidesGround);
                }

                if(healPercent > 0f){
                    Vars.indexer.eachBlock(b.team, x, y, splashDamageRadius, Building::damaged, other -> {
                        Fx.healBlockFull.at(other.x, other.y, other.block.size, Pal.heal);
                        other.heal(healPercent / 100f * other.maxHealth());
                    });
                }

                if(makeFire){
                    Vars.indexer.eachBlock(null, x, y, splashDamageRadius, other -> other.team != b.team, other -> Fires.create(other.tile));
                }
            }
        }else{
            Bullet n = create(b, b.x, b.y, b.rotation());
            n.vel.setZero();
            n.fdata = 1f;
            n.lifetime = duration;
        }
    }

    @Override
    public void draw(Bullet b){
        super.draw(b);
        Draw.color(color);
        Fill.circle(b.x, b.y, size);
        if(b.fdata <= 0f){
            Draw.color(Color.white);
            Fill.circle(b.x, b.y, size / 2f);
        }else{
            float in = Mathf.clamp(b.time / 15f) * range,
            fin = ((b.time % reload) / reload) * size;
            Lines.stroke(1.5f);
            UnityDrawf.dashCircleAngle(b.x, b.y, in, (b.time / 20f) * Mathf.signs[Mathf.randomSeed(b.id, 0, 1)]);

            Draw.color(Color.white);
            Lines.circle(b.x, b.y, fin);
            Fill.circle(b.x, b.y, size * 0.05f);
        }
    }
}

package unity.type.weapons.monolith;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.audio.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.gen.*;
import unity.util.*;

import static mindustry.Vars.*;

/**
 * Charges up {@linkplain #shots some} bullets over time, and shoots them all at once when signaled.
 * {@link #continuous} isn't supported (yet).
 * @author GlennFolker
 */
public class ChargeShotgunWeapon extends Weapon{
    private static final Vec2 tmp = new Vec2();

    public float addSequenceTime = 38f;
    public float weaveScale = 24f;
    public float weaveAmount = 30f;
    public float angleStrideScale = 10f;

    public Effect addEffect = Fx.lancerLaserCharge;
    public Effect addedEffect = Fx.lightningShoot;
    public Effect releaseEffect = Fx.none;

    public ChargeShotgunWeapon(String name){
        super(name);
        mountType = ChargeShotgunMount::new;
    }

    protected Vec2 chargePos(Vec2 local, Unit unit, ChargeShotgunMount mount){
        float
            weaponRotation = unit.rotation - 90f + (rotate ? mount.rotation : 0f),
            mountX = unit.x + Angles.trnsx(unit.rotation - 90f, x, y),
            mountY = unit.y + Angles.trnsy(unit.rotation - 90f, x, y);

        return tmp.trns(weaponRotation, local.x, local.y).add(mountX, mountY);
    }

    @Override
    public void draw(Unit unit, WeaponMount mount){
        super.draw(unit, mount);

        ChargeShotgunMount m = (ChargeShotgunMount)mount;
        float
            weaponRotation = unit.rotation - 90f + (rotate ? m.rotation : 0f),
            mountX = unit.x + Angles.trnsx(unit.rotation - 90f, x, y),
            mountY = unit.y + Angles.trnsy(unit.rotation - 90f, x, y),
            bulletX = mountX + Angles.trnsx(weaponRotation, shootX, shootY),
            bulletY = mountY + Angles.trnsy(weaponRotation, shootX, shootY),
            shootAngle = rotate ? weaponRotation + 90f : Angles.angle(bulletX, bulletY, m.aimX, m.aimY) + (unit.rotation - unit.angleTo(m.aimX, m.aimY));

        for(int i = 0; i < m.added.size - 1; i += 2){
            Vec2 current = m.added.get(i);
            if(!Float.isNaN(current.x) && !Float.isNaN(current.y)){
                Vec2 pos = chargePos(current, unit, m);
                float rot = shootAngle + Utils.angleDistSigned(shootAngle, Angles.angle(mountX, mountY, pos.x, pos.y)) / angleStrideScale;

                drawCharge(pos.x, pos.y, weaponRotation, rot, unit, m);
            }
        }
    }

    public void drawCharge(float x, float y, float rotation, float shootAngle, Unit unit, ChargeShotgunMount mount){}

    @Override
    public void update(Unit unit, WeaponMount mount){
        ChargeShotgunMount m = (ChargeShotgunMount)mount;
        m.transform.parent = unit;
        m.transform.offsetRot = rotate ? m.rotation : 0f;
        m.transform.update();

        float
            weaponRotation = unit.rotation - 90f + (rotate ? m.rotation : 0f),
            mountX = unit.x + Angles.trnsx(unit.rotation - 90f, x, y),
            mountY = unit.y + Angles.trnsy(unit.rotation - 90f, x, y),
            bulletX = mountX + Angles.trnsx(weaponRotation, shootX, shootY),
            bulletY = mountY + Angles.trnsy(weaponRotation, shootX, shootY),
            shootAngle = rotate ? weaponRotation + 90f : Angles.angle(bulletX, bulletY, m.aimX, m.aimY) + (unit.rotation - unit.angleTo(m.aimX, m.aimY)),

            addTime = Math.max(reload - addSequenceTime, 0f);

        if(!m.releasing){
            if(!m.adding){
                if(m.loaded() < shots && (m.add += Time.delta * unit.reloadMultiplier) >= addTime){
                    m.adding = true;

                    m.added.add(new Vec2(Float.NaN, Float.NaN), new Vec2(shootX, shootY));
                    m.addSequence = m.add % addTime;
                    m.add = 0f;

                    addEffect.at(bulletX, bulletY, weaponRotation, m.transform);
                }
            }else if((m.addSequence += Time.delta) >= addSequenceTime && m.added.any()){ //TODO `m.added.any()` shouldn't even be necessary here, but i get out of bounds error anyway. and i'm too lazy to fix it.
                m.adding = false;

                m.added.get(m.added.size - 2).set(shootX, shootY);
                m.add = m.addSequence % addSequenceTime;
                m.addSequence = 0f;

                addedEffect.at(bulletX, bulletY, weaponRotation, m.transform);
            }
        }else{
            m.adding = false;
            m.add = m.addSequence = 0f;
        }

        for(int i = 0; i < m.added.size - 1; i += 2){
            Vec2 current = m.added.get(i), target = m.added.get(i + 1);
            if(!m.releasing){
                target.setAngle(Mathf.sin(Time.time + Mathf.randomSeed(unit.id, Mathf.pi * 2f * weaveScale) + (Mathf.pi * 2f * weaveScale) * ((float) i / m.added.size), weaveScale, weaveAmount / 2f * m.loaded()) + 90f);
            }

            if(!Float.isNaN(current.x) && !Float.isNaN(current.y)){
                current.setAngle(Mathf.slerpDelta(current.angle(), target.angle(), 0.08f));
            }
        }

        boolean can = unit.canShoot();
        m.recoil = Mathf.approachDelta(m.recoil, 0, (Math.abs(recoil) * unit.reloadMultiplier) / recoilTime);

        if(rotate && (m.rotate || m.shoot) && can){
            float
                axisX = unit.x + Angles.trnsx(unit.rotation - 90f, x, y),
                axisY = unit.y + Angles.trnsy(unit.rotation - 90f, x, y);

            m.targetRotation = Angles.angle(axisX, axisY, m.aimX, m.aimY) - unit.rotation;
            m.rotation = Angles.moveToward(m.rotation, m.targetRotation, rotateSpeed * Time.delta);
        }else if(!rotate){
            m.rotation = 0;
            m.targetRotation = unit.angleTo(m.aimX, m.aimY);
        }

        if(!controllable && autoTarget){
            if((m.retarget -= Time.delta) <= 0f){
                m.target = findTarget(unit, mountX, mountY, bullet.range(), bullet.collidesAir, bullet.collidesGround);
                m.retarget = m.target == null ? targetInterval : targetSwitchInterval;
            }

            if(m.target != null && checkTarget(unit, m.target, mountX, mountY, bullet.range())){
                m.target = null;
            }

            boolean shoot = false;
            if(m.target != null){
                shoot = m.target.within(mountX, mountY, bullet.range() + Math.abs(shootY) + (m.target instanceof Sized s ? s.hitSize() / 2f : 0f)) && can;

                if(predictTarget){
                    Vec2 to = Predict.intercept(unit, m.target, bullet.speed);
                    m.aimX = to.x;
                    m.aimY = to.y;
                }else{
                    m.aimX = m.target.x();
                    m.aimY = m.target.y();
                }
            }

            m.shoot = m.rotate = shoot;
        }

        if(continuous && m.bullet != null){
            if(!m.bullet.isAdded() || m.bullet.time >= m.bullet.lifetime || m.bullet.type != bullet){
                m.bullet = null;
            }else{
                m.bullet.rotation(weaponRotation + 90);
                m.bullet.set(bulletX, bulletY);
                m.recoil = recoil;
                unit.vel.add(Tmp.v1.trns(unit.rotation + 180f, m.bullet.type.recoil));
                if(shootSound != Sounds.none && !headless){
                    if(m.sound == null) m.sound = new SoundLoop(shootSound, 1f);
                    m.sound.update(bulletX, bulletY, true);
                }
            }
        }else{
            m.heat = Math.max(m.heat - Time.delta * unit.reloadMultiplier / cooldownTime, 0);
            if(m.sound != null) m.sound.update(bulletX, bulletY, false);
        }

        if(m.shoot &&
            can &&
            (!useAmmo || unit.ammo > 0 || !state.rules.unitAmmo || unit.team.rules().infiniteAmmo) &&
            (!alternate || m.side == flipSprite) &&
            unit.vel.len() >= minShootVelocity &&
            m.loaded() > 0 &&
            !m.releasing &&
            Angles.within(rotate ? m.rotation : unit.rotation, m.targetRotation, shootCone)
        ){
            shoot(unit, m, bulletX, bulletY, m.aimX, m.aimY, mountX, mountY, shootAngle, Mathf.sign(x));
            if(otherSide != -1 && alternate && m.side == flipSprite){
                unit.mounts[otherSide].side = !unit.mounts[otherSide].side;
                m.side = !m.side;
            }

            if(useAmmo){
                unit.ammo--;
                if(unit.ammo < 0) unit.ammo = 0;
            }
        }
    }

    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float aimX, float aimY, float mountX, float mountY, float rotation, int side){
        ChargeShotgunMount m = (ChargeShotgunMount)mount;

        float baseX = unit.x, baseY = unit.y;
        boolean delay = firstShotDelay + shotDelay > 0f;

        BulletType ammo = bullet;
        boolean parentize = ammo.keepVelocity || parentizeEffects;

        if(delay){
            m.releasing = true;
            if(m.adding){
                m.added.removeRange(m.added.size - 2, m.added.size - 1);
                m.adding = false;
            }

            for(int i = 0; i < m.added.size - 1; i += 2){
                Vec2 current = m.added.get(i);
                Time.run(i / 2f * shotDelay + firstShotDelay, () -> {
                    if(!unit.isAdded()) return;

                    Vec2 pos = chargePos(current, unit, m);
                    float rot = rotation + Utils.angleDistSigned(rotation, Angles.angle(mountX, mountY, pos.x, pos.y)) / angleStrideScale;

                    bullet(
                        unit, pos.x, pos.y,
                        rot + Mathf.range(inaccuracy),
                        ammo.scaleVelocity ? Mathf.clamp(Mathf.dst(pos.x, pos.y, aimX, aimY) / ammo.range()) : 1f
                    );

                    shootSound.at(pos.x, pos.y, Mathf.random(soundPitchMin, soundPitchMax));

                    unit.vel.add(Tmp.v1.trns(rotation + 180f, ammo.recoil));
                    Effect.shake(shake, shake, pos.x, pos.y);
                    ammo.shootEffect.at(pos.x, pos.y, rot, parentize ? unit : null);
                    ammo.smokeEffect.at(pos.x, pos.y, rot, parentize ? unit : null);

                    mount.recoil = recoil;
                    mount.heat = 1f;

                    current.x = current.y = Float.NaN;
                });

                Vec2 pos = chargePos(current, unit, m);
                float rot = rotation + Utils.angleDistSigned(rotation, Angles.angle(mountX, mountY, pos.x, pos.y)) / angleStrideScale;

                releaseEffect.at(pos.x, pos.y, rot, parentize ? m.transform : null);
            }

            Time.run((m.loaded() - 1) * shotDelay + firstShotDelay, () -> {
                m.releasing = false;
                m.added.clear();
            });

            Time.run(firstShotDelay, () -> {
                if(!unit.isAdded()) return;
                ammo.chargeShootEffect.at(shootX + unit.x - baseX, shootY + unit.y - baseY, rotation, parentize ? unit : null);
            });
        }else{
            for(int i = 0; i < m.added.size - 1; i += 2){
                Vec2 current = m.added.get(i);
                if(!Float.isNaN(current.x) && !Float.isNaN(current.y)){
                    Vec2 pos = chargePos(current, unit, m);
                    float rot = rotation + Utils.angleDistSigned(rotation, Angles.angle(mountX, mountY, pos.x, pos.y)) / angleStrideScale;

                    bullet(
                        unit, pos.x, pos.y,
                        rot + Mathf.range(inaccuracy),
                        ammo.scaleVelocity ? Mathf.clamp(Mathf.dst(pos.x, pos.y, aimX, aimY) / ammo.range()) : 1f
                    );

                    shootSound.at(pos.x, pos.y, Mathf.random(soundPitchMin, soundPitchMax));

                    Effect.shake(shake, shake, pos.x, pos.y);
                    ammo.shootEffect.at(pos.x, pos.y, rot, parentize ? unit : null);
                    ammo.smokeEffect.at(pos.x, pos.y, rot, parentize ? unit : null);
                }
            }

            unit.vel.add(Tmp.v1.trns(rotation + 180f, ammo.recoil));
            mount.recoil = recoil;
            mount.heat = 1f;

            m.added.clear();
        }

        ejectEffect.at(mountX, mountY, rotation * side);
        unit.apply(shootStatus, shootStatusDuration);
    }

    public static class ChargeShotgunMount extends WeaponMount{
        public Trns transform = Trns.create();
        public boolean adding;

        public Seq<Vec2> added = new Seq<>();
        public float add;
        public float addSequence;

        public boolean releasing;

        public ChargeShotgunMount(Weapon weapon){
            super(weapon);
            transform.offsetX = weapon.x;
            transform.offsetY = weapon.y;
        }

        public int loaded(){
            return added.size / 2 - (adding ? 1 : 0);
        }
    }
}

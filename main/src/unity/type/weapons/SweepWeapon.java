package unity.type.weapons;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.audio.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;

public class SweepWeapon extends Weapon{
    public float sweepTime = 120f;
    public float sweepAngle = 60f;

    public SweepWeapon(String name){
        super(name);
        rotate = true;
        continuous = true;
        mountType = SweepWeaponMount::new;
    }

    @Override
    public void update(Unit unit, WeaponMount mount){
        SweepWeaponMount m = (SweepWeaponMount)mount;
        boolean can = unit.canShoot();
        boolean sweep = m.sweep2 != m.sweep;
        float lastReload = mount.reload;
        mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0);
        mount.recoil = Mathf.approachDelta(mount.recoil, 0, (Math.abs(recoil) * unit.reloadMultiplier) / recoilTime);

        if(sweep){
            float direction = sweepAngle * Mathf.sign(m.sweep);
            m.angle = Mathf.approachDelta(m.angle, direction, (sweepAngle * 2f) / sweepTime);
            if(Mathf.equal(m.angle, direction)){
                m.sweep2 = m.sweep;
            }
        }

        //rotate if applicable
        if(rotate && (mount.rotate || mount.shoot) && can){
            float axisX = unit.x + Angles.trnsx(unit.rotation - 90,  x, y),
            axisY = unit.y + Angles.trnsy(unit.rotation - 90,  x, y);

            mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
            mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation + m.angle, rotateSpeed * Time.delta);
        }else if(!rotate){
            mount.rotation = 0;
            mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
        }
        if(sweep && !(mount.rotate || mount.shoot)){
            mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation + m.angle, rotateSpeed * Time.delta);
        }

        float
        weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : 0),
        mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
        mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y),
        bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY),
        bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY),
        shootAngle = rotate ? weaponRotation + 90 : Angles.angle(bulletX, bulletY, mount.aimX, mount.aimY) + (unit.rotation - unit.angleTo(mount.aimX, mount.aimY));

        //find a new target
        if(!controllable && autoTarget){
            if((mount.retarget -= Time.delta) <= 0f){
                mount.target = findTarget(unit, mountX, mountY, bullet.range, bullet.collidesAir, bullet.collidesGround);
                mount.retarget = mount.target == null ? targetInterval : targetSwitchInterval;
            }

            if(mount.target != null && checkTarget(unit, mount.target, mountX, mountY, bullet.range)){
                mount.target = null;
            }

            boolean shoot = false;

            if(mount.target != null){
                shoot = mount.target.within(mountX, mountY, bullet.range + Math.abs(shootY) + (mount.target instanceof Sized s ? s.hitSize()/2f : 0f)) && can;

                if(predictTarget){
                    Vec2 to = Predict.intercept(unit, mount.target, bullet.speed);
                    mount.aimX = to.x;
                    mount.aimY = to.y;
                }else{
                    mount.aimX = mount.target.x();
                    mount.aimY = mount.target.y();
                }
            }

            mount.shoot = mount.rotate = shoot;

            //note that shooting state is not affected, as these cannot be controlled
            //logic will return shooting as false even if these return true, which is fine
        }

        //update continuous state
        if(continuous && mount.bullet != null){
            if(!mount.bullet.isAdded() || mount.bullet.time >= mount.bullet.lifetime || mount.bullet.type != bullet){
                mount.bullet = null;
            }else{
                mount.bullet.rotation(weaponRotation + 90);
                mount.bullet.set(bulletX, bulletY);
                mount.reload = reload;
                mount.recoil = recoil;
                unit.vel.add(Tmp.v1.trns(unit.rotation + 180f, mount.bullet.type.recoil));
                if(shootSound != Sounds.none && !Vars.headless){
                    if(mount.sound == null) mount.sound = new SoundLoop(shootSound, 1f);
                    mount.sound.update(bulletX, bulletY, true);
                }
            }
        }else{
            //heat decreases when not firing
            mount.heat = Math.max(mount.heat - Time.delta * unit.reloadMultiplier / cooldownTime, 0);

            if(mount.sound != null){
                mount.sound.update(bulletX, bulletY, false);
            }
        }

        //flip weapon shoot side for alternating weapons
        boolean wasFlipped = mount.side;
        if(otherSide != -1 && alternate && mount.side == flipSprite && mount.reload <= reload / 2f && lastReload > reload / 2f){
            unit.mounts[otherSide].side = !unit.mounts[otherSide].side;
            mount.side = !mount.side;
        }

        //shoot if applicable
        if(mount.shoot && //must be shooting
        can && //must be able to shoot
        (!useAmmo || unit.ammo > 0 || !Vars.state.rules.unitAmmo || unit.team.rules().infiniteAmmo) && //check ammo
        (!alternate || wasFlipped == flipSprite) &&
        unit.vel.len() >= minShootVelocity && //check velocity requirements
        mount.reload <= 0.0001f && //reload has to be 0
        Angles.within(rotate ? (mount.rotation - m.angle) : unit.rotation, mount.targetRotation, shootCone) //has to be within the cone
        ){
            shoot(unit, mount, bulletX, bulletY, shootAngle);

            mount.reload = reload;

            if(useAmmo){
                unit.ammo--;
                if(unit.ammo < 0) unit.ammo = 0;
            }
        }
    }

    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation){
        SweepWeaponMount m = (SweepWeaponMount)mount;
        m.sweep = !m.sweep;
        super.shoot(unit, mount, shootX, shootY, rotation);
    }

    static class SweepWeaponMount extends WeaponMount{
        float angle;
        boolean sweep, sweep2;

        SweepWeaponMount(Weapon weapon){
            super(weapon);
            SweepWeapon w = (SweepWeapon)weapon;
            angle = w.sweepAngle * Mathf.sign(weapon.flipSprite);
            sweep = sweep2 = weapon.flipSprite;
        }
    }
}

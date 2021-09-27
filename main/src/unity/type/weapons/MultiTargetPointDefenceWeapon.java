package unity.type.weapons;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.util.*;

public class MultiTargetPointDefenceWeapon extends Weapon{
    private static float f;
    private final static Seq<Bullet> tmp = new Seq<>(Bullet.class);
    public static boolean testing = false;

    public Effect beamEffect = Fx.pointBeam;
    public Effect absorbEffect = Fx.absorb;
    public Color color = Color.white;
    public boolean absorb = true;
    /** if the bullets angle to the weapon is greater than last one, it creates a separate target */
    public float splitCone = 35f;
    public float shootConeAlt = 5f;
    /** To prevent the weapon from consistently turning if the target's score is similar */
    public float decideTime = 20f;

    public MultiTargetPointDefenceWeapon(String name){
        super(name);
        mountType = MultiTargetPointDefenceMount::new;
        predictTarget = false;
        controllable = false;
        autoTarget = true;
        rotate = true;
        useAmmo = false;
        shots = 5;
    }

    @Override
    public void draw(Unit unit, WeaponMount mount){
        super.draw(unit, mount);

        if(testing){
            MultiTargetPointDefenceMount cm = (MultiTargetPointDefenceMount)mount;
            float
            mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
            mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

            for(int i = 0; i < cm.available.size; i += 2){
                float h = (i / 2f) * (360f / (cm.available.size / 2f));
                float len = 30f + cm.available.items[i], ang = cm.available.items[i + 1];
                Draw.color(Tmp.c1.set(Color.green).shiftHue(h));
                Lines.stroke(2f);
                Lines.lineAngle(mountX, mountY, ang, len, false);
                Draw.alpha(0.3f);
                Lines.lineAngle(mountX, mountY, ang - splitCone, bullet.range(), false);
                Lines.lineAngle(mountX, mountY, ang + splitCone, bullet.range(), false);
            }
            Draw.reset();
        }
    }

    @Override
    public void update(Unit unit, WeaponMount mount){
        MultiTargetPointDefenceMount cm = (MultiTargetPointDefenceMount)mount;
        boolean can = unit.canShoot(), ret = (mount.retarget += Time.delta) >= 5f;
        mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0);

        float
        weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : 0),
        mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
        mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y),
        bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY),
        bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY),
        bulletRotation = weaponRotation + 90f;

        if(otherSide != -1 && alternate && mount.side == flipSprite &&
        mount.reload + Time.delta * unit.reloadMultiplier > reload/2f && mount.reload <= reload/2f){
            unit.mounts[otherSide].side = !unit.mounts[otherSide].side;
            mount.side = !mount.side;
        }

        if(ret){
            mount.retarget = 0f;
            cm.targets.clear();
            Rect r = Tmp.r1.setCentered(mountX, mountY, bullet.range() * 2f);
            Groups.bullet.intersect(r.x, r.y, r.x + r.width, r.y + r.height, b -> {
                if(b.team != unit.team && b.type.hittable && b.within(mountX, mountY, bullet.range() + (b.hitSize / 2f))){
                    cm.targets.add(b);
                }
            });
            cm.targets.sort(b -> Utils.angleDistSigned(bulletRotation, b.angleTo(mountX, mountY) + 180f));
        }

        cm.available.clear();
        if(!cm.targets.isEmpty()){
            //tmpf.clear();
            tmp.clear();
            f = -360f;
            cm.targets.removeAll(b -> {
                boolean invalid = !b.isAdded() || Units.invalidateTarget(b, unit.team, mountX, mountY, bullet.range());
                if(!invalid){
                    //tmpf.add(Utils.angleDistSigned(bulletRotation, b.angleTo(mountX, mountY) + 180f));
                    //tmpf.add(b.x, b.y);
                    tmp.add(b);
                    float ang = Angles.angle(mountX, mountY, b.x, b.y);
                    if(f != -360f && Utils.angleDist(f, ang) > splitCone){
                        updateScore(cm, bulletRotation, mountX, mountY);
                    }
                    f = ang;
                }
                return invalid;
            });
            updateScore(cm, bulletRotation, mountX, mountY);
            boolean changed = cm.targetIdx + 1 >= cm.available.size;
            if(cm.decideTime <= 0f || changed){
                float ls = -1f;
                for(int i = 0; i < cm.available.size; i += 2){
                    float s = cm.available.items[i];
                    if(s > ls){
                        cm.targetIdx = i;
                        ls = s;
                    }
                }
                if(cm.decideTime <= 0f) cm.decideTime = decideTime;
            }
        }
        cm.decideTime = Math.max(cm.decideTime - Time.delta, 0f);

        if((mount.rotate = mount.shoot = !cm.targets.isEmpty()) && can){
            mount.targetRotation = cm.available.get(cm.targetIdx + 1) - unit.rotation;
            mount.aimX = Angles.trnsx(mount.targetRotation + unit.rotation, bullet.range());
            mount.aimY = Angles.trnsy(mount.targetRotation + unit.rotation, bullet.range());
            mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, rotateSpeed * Time.delta);
        }

        if(mount.shoot && can &&
        (!useAmmo || unit.ammo > 0 || !Vars.state.rules.unitAmmo || unit.team.rules().infiniteAmmo) &&
        (!alternate || mount.side == flipSprite) && mount.reload <= 0.0001f &&
        Angles.within(mount.rotation, mount.targetRotation, shootConeAlt)){
            shoot(unit, mount, bulletX, bulletY, mount.aimX, mount.aimY, mountX, mountY, bulletRotation, Mathf.sign(x));

            mount.reload = reload;
            if(useAmmo){
                unit.ammo--;
                if(unit.ammo < 0) unit.ammo = 0;
            }
        }
    }

    void updateScore(MultiTargetPointDefenceMount mount, float rotation, float mX, float mY){
        float x = 0f, y = 0f;
        for(Bullet b : tmp){
            x += (b.x - mX) / tmp.size;
            y += (b.y - mY) / tmp.size;
        }
        float angle = Angles.angle(x, y);
        float score = tmp.sumf(b -> b.damage) * Mathf.clamp(1f - (Utils.angleDist(rotation, angle) / 180f)) * Mathf.clamp(1f - (Mathf.dst(x, y) / bullet.range()));
        tmp.clear();

        mount.available.add(score, angle);
    }

    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float aimX, float aimY, float mountX, float mountY, float rotation, int side){
        MultiTargetPointDefenceMount cm = (MultiTargetPointDefenceMount)mount;
        cm.decideTime = 0f;
        float range = bullet.range();

        Rect r = Tmp.r1.setCentered(mountX, mountY, 0f);
        Utils.shotgunRange(3, shootCone, rotation, a ->
        r.merge(Angles.trnsx(a, range) + mountX, Angles.trnsy(a, range) + mountY));

        tmp.clear();
        Groups.bullet.intersect(r.x, r.y, r.width + r.x, r.height + r.y, b -> {
            if(b.team != unit.team && b.type.hittable && b.within(mountX, mountY, range + (b.hitSize / 2f)) && Angles.within(Angles.angle(mountX, mountY, b.x, b.y), rotation, shootCone)){
                tmp.add(b);
            }
        });
        if(!tmp.isEmpty()){
            tmp.sort(b -> b.dst2(mountX, mountY));
            for(int i = 0; i < Math.min(tmp.size, shots); i++){
                Bullet b = tmp.get(i);
                if(b == null) continue;
                beamEffect.at(shootX, shootY, rotation, color, new Vec2().set(b));
                if(b.damage > bullet.damage){
                    bullet.hitEffect.at(b.x, b.y);
                    b.damage -= bullet.damage;
                }else{
                    if(absorb){
                        b.absorbed = true;
                        absorbEffect.at(b.x, b.y, color);
                    }else{
                        bullet.hitEffect.at(b.x, b.y);
                    }
                    b.remove();
                }
            }

            bullet.shootEffect.at(shootX, shootY, rotation);
            //bullet.hitEffect.at(target.x, target.y, color);
            shootSound.at(shootX, shootY, Mathf.random(0.9f, 1.1f));
        }
    }

    public static class MultiTargetPointDefenceMount extends WeaponMount{
        FloatSeq available = new FloatSeq();
        Seq<Bullet> targets = new Seq<>();
        float decideTime = 0f;
        int targetIdx = 0;

        MultiTargetPointDefenceMount(Weapon weapon){
            super(weapon);
        }
    }
}

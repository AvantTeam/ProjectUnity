package unity.entities;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import unity.type.*;
import unity.util.*;

public class NewTentacle{
    final static Vec2 tv = Tmp.v2, tv2 = Tmp.v3;
    NewTentacleSegment root, end;
    TentacleType type;
    Unit unit;
    Teamc target;
    Bullet bullet;
    boolean attacking = false, stab;
    float targetX, targetY, swayScl,
    attackTime, stabTime, alx, aly, retarget, reload;

    public NewTentacle(TentacleType t, Unit unit){
        type = t;
        this.unit = unit;
        NewTentacleSegment child = null;
        for(int i = 0; i < type.segments; i++){
            NewTentacleSegment seg = new NewTentacleSegment();
            seg.main = this;
            if(child == null){
                root = seg;
            }else{
                child.parent = seg;
                seg.child = child;
            }
            if(i >= type.segments - 1){
                end = seg;
            }
            child = seg;
        }
    }

    float rootRange(){
        return (type.segments * type.segmentLength) + (type.bullet != null ? type.bullet.range() : 0f);
    }

    void updateMovement(){
        end.updateLastPosition();
        if(type.bullet != null){
            tv2.set(end.x, end.y).sub(targetX, targetY).setLength(type.range / 3f).add(targetX, targetY);
            if(!tv2.isNaN()){
                tv.set(tv2).sub(end.x, end.y).scl(1f / 40f).limit(type.speed);
                float ang = Angles.angle(end.x, end.y, targetX, targetY);
                end.rotation = Angles.moveToward(end.rotation, ang, type.rotationSpeed);
                float scl = Mathf.clamp((90f - Angles.angleDist(end.rotation, ang)) / 90f, 0.7f, 1f);

                tv2.trns(end.rotation, type.segmentLength).add(end.prevPos());
                end.x = tv2.x;
                end.y = tv2.y;

                end.vx += tv.x * scl * type.accel;
                end.vy += tv.y * scl * type.accel;
            }
        }else{
            tv.set(targetX, targetY).sub(unitPosition()).scl(2f).add(unitPosition());
            float tx = tv.x,
            ty = tv.y;
            float ang = Angles.angle(end.x, end.y, tx, ty);
            float scl = Mathf.clamp(Math.abs(90f - Angles.angleDist(end.rotation, ang)) / 90f, 0.7f, 1f);

            if(stab){
                tv.set(tx, ty).sub(end.x, end.y).limit(type.speed);

                end.rotation = Angles.moveToward(end.rotation, ang, type.rotationSpeed);

                tv2.trns(end.rotation, type.segmentLength).add(end.prevPos());
                end.x = tv2.x;
                end.y = tv2.y;

                end.vx += tv.x * scl * type.accel;
                end.vy += tv.y * scl * type.accel;

                if((attackTime += Time.delta) >= 80f){
                    attackTime = 0f;
                    stab = false;
                }
            }else{
                alx = end.x;
                aly = end.y;

                tv2.set(targetX, targetY).sub(unitPosition()).setLength(type.range / 5f).add(unitPosition());
                if(!tv2.isNaN()){
                    tv.set(tv2).sub(end.x, end.y).scl(1f / 25f).limit(type.speed);

                    end.rotation = Angles.moveToward(end.rotation, ang, type.rotationSpeed);

                    tv2.trns(end.rotation, type.segmentLength).add(end.prevPos());
                    end.x = tv2.x;
                    end.y = tv2.y;

                    end.vx += tv.x * scl * type.accel;
                    end.vy += tv.y * scl * type.accel;
                }

                attackTime += Time.delta;
                if(attackTime >= 80f){
                    target = null;
                    attackTime = 0f;
                    stab = true;
                }
            }
        }
    }

    void updateWeapon(){
        attacking = false;
        boolean player = unit.isPlayer();
        if(type.automatic || !player){
            if(target == null && (retarget += Time.delta) >= 20f){
                target = Units.closestTarget(unit.team, end.x, end.y, type.range, u -> u.isValid() && unitPosition().within(u, rootRange()), b -> unitPosition().within(b, rootRange()));
                retarget = 0f;
            }
            if(target != null){
                targetX = target.getX();
                targetY = target.getY();
            }
        }else if(unit.isShooting){
            targetX = unit.aimX;
            targetY = unit.aimY;
        }
        Position pos = unitPosition();
        if(Units.invalidateTarget(target, unit.team, pos.getX(), pos.getY(), rootRange()) || (player && !type.automatic)) target = null;

        if(target != null || (player && unit.isShooting)){
            attacking = true;
            if(type.bullet != null && Angles.within(end.rotation, Angles.angle(end.x, end.y, targetX, targetY), type.shootCone)){
                reload += Time.delta * unit.reloadMultiplier;
                if(reload >= type.reload){
                    Bullet b = type.bullet.create(unit, unit.team, end.x, end.y, end.rotation);
                    if(type.continuous) bullet = b;
                    reload = 0f;
                }
            }
        }
        if(type.continuous){
            if(bullet != null && (bullet.type != type.bullet || !bullet.isAdded())) bullet = null;
            if(bullet != null){
                bullet.set(end.x, end.y);
                bullet.rotation(end.rotation);
            }
        }

        if(bullet == null && attacking && end.len() > 0.2f){
            if(stab){
                if((stabTime += Time.delta) >= 5f){
                    Utils.collideLineRawEnemyRatio(unit.team, alx, aly, end.x, end.y, 3f, (building, ratio, direct) -> {
                        if(direct){
                            building.damage(type.tentacleDamage * ratio);
                        }
                        return false;
                    }, (unit, ratio) -> {
                        unit.damage(type.tentacleDamage * ratio);
                        return false;
                    }, Fx.hitBulletSmall::at);

                    alx = end.x;
                    aly = end.y;
                    stabTime = 0f;
                }
            }else{
                alx = end.x;
                aly = end.y;
            }
        }
    }

    public void update(){
        if(!attacking){
            swayScl = Mathf.lerpDelta(swayScl, 1f, 0.04f);
        }else{
            swayScl = Mathf.lerpDelta(swayScl, 0f, 0.04f);
            updateMovement();
        }

        for(int i = 0; i < 2; i++){
            int s = 0;
            NewTentacleSegment cur = i == 0 ? end : root;
            while(cur != null){
                if(i == 0){
                    cur.updateLastPosition();
                    tv.set(cur.vx, cur.vy).limit(type.speed);
                    cur.vx = tv.x;
                    cur.vy = tv.y;
                    cur.x += cur.vx * Time.delta;
                    cur.y += cur.vy * Time.delta;

                    cur.vx *= 1f - (type.drag * Time.delta);
                    cur.vy *= 1f - (type.drag * Time.delta);
                    if(swayScl >= 0.0001f){
                        float sin = swayScl * Mathf.sin(Time.time + type.swayOffset + (s * type.swaySegmentOffset), type.swayScl, type.swayMag) * Mathf.sign(type.flipSprite);
                        cur.rotation += sin;
                    }

                    if(cur.child != null){
                        NewTentacleSegment c = cur.child;
                        float cx = Angles.trnsx(c.rotation + 180f, type.segmentLength) + c.x,
                        cy = Angles.trnsy(c.rotation + 180f, type.segmentLength) + c.y;

                        float sx = Angles.trnsx(cur.rotation + 180f, type.segmentLength) + cur.x,
                        sy = Angles.trnsy(cur.rotation + 180f, type.segmentLength) + cur.y;

                        c.rotation = Angles.angle(cx, cy, sx, sy);
                        float ang = Utils.angleDistSigned(cur.rotation, c.rotation, type.angleLimit);
                        c.rotation += ang;

                        c.x = sx;
                        c.y = sy;
                    }

                    cur = cur.child;
                    s++;
                }else{
                    if(cur.child == null){
                        float parentAng = unit.rotation + type.rotationOffset + 180f,
                        ang = cur.prevPos().angleTo(cur.x, cur.y);
                        cur.rotation = Utils.clampedAngle(ang, parentAng, type.firstSegmentAngleLimit);
                        tv.trns(cur.rotation, type.segmentLength).add(unitPosition());
                    }else{
                        float childAng = cur.child.rotation,
                        ang = cur.prevPos().angleTo(cur.x, cur.y);
                        cur.rotation = Utils.clampedAngle(ang, childAng, type.angleLimit);
                        tv.trns(cur.rotation, type.segmentLength).add(cur.child.x, cur.child.y);
                    }
                    cur.x = tv.x;
                    cur.y = tv.y;

                    cur = cur.parent;
                }
            }
        }

        updateWeapon();
    }

    public void draw(){
        NewTentacleSegment cur = root;
        while(cur != null){
            TextureRegion region = cur.parent == null ? type.tipRegion : type.region;
            Position prev = cur.prevPos();
            tv.set(cur.x, cur.y).sub(prev).setLength(region.width * Draw.scl).add(prev);
            unit.type.applyColor(unit);
            Lines.stroke(region.height * Draw.scl * Mathf.sign(type.flipSprite));
            Lines.line(region, prev.getX(), prev.getY(), tv.x, tv.y, false);

            cur = cur.parent;
        }
        Draw.reset();
    }

    Position unitPosition(){
        return Tmp.v1.trns(unit.rotation - 90f, type.x, type.y).add(unit);
    }

    public static class NewTentacleSegment{
        NewTentacleSegment child, parent;
        NewTentacle main;
        float lx, ly;
        float x, y, rotation;
        float vx, vy;

        void updateLastPosition(){
            lx = x;
            ly = y;
        }

        float len(){
            return Mathf.len(x - lx, y - ly);
        }

        Position prevPos(){
            if(child == null) return main.unitPosition();
            return Tmp.v1.set(child.x, child.y);
        }
    }
}

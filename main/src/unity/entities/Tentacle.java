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

public class Tentacle{
    TentacleSegment[] segments;
    TentacleType type;
    Unit unit;
    Teamc target;
    Interval timer = new Interval(2);
    Vec2 targetPos = new Vec2();
    Vec2 endVelocity = new Vec2();
    float swayScl = 1f;
    float reloadTime = 0f;
    float chargingTime = 0f, chargingTimeB = 0f, chargingTimeC = 0f;
    float lastTipX, lastTipY;
    Bullet bullet;
    boolean attacking = false;

    public void updateMovement(){
        if(!attacking && chargingTime <= 0f){
            swayScl = Mathf.lerpDelta(swayScl, 1f, 0.04f);
        }else{
            swayScl = Mathf.lerpDelta(swayScl, 0f, 0.04f);

            if(type.bullet != null){
                float speed = type.speed * type.accel;
                Tmp.v2.trns(last().angleTo(targetPos) + 180f, type.range / 3f).add(targetPos);
                float dst = last().dst(Tmp.v2) / 200f;
                Tmp.v1.set(last()).approachDelta(Tmp.v2, Math.min(speed, dst)).sub(last());
                endVelocity.add(Tmp.v1).limit(type.speed);
                last().rotation = Angles.moveToward(last().rotation, last().angleTo(targetPos) + 180f, type.rotationSpeed * Time.delta);
                last().rotation = Mathf.slerpDelta(last().rotation, last().angleTo(targetPos) + 180f, (endVelocity.len2() / (type.speed * type.speed)) * 0.25f);
                last().updatePosition();
            }else{
                if(chargingTime < 80f){
                    if(!(last().within(targetPos, 10f) || !Angles.within((last().rotation + 180f) % 360f, last().angleTo(targetPos), 90f)) && last().within(targetPos, type.range() + 10f)){
                        last().rotation = Angles.moveToward(last().rotation, last().angleTo(targetPos) + 180f, type.rotationSpeed * Time.delta);
                        last().rotation = Mathf.slerpDelta(last().rotation, last().angleTo(targetPos) + 180f, (endVelocity.len2() / (type.speed * type.speed)) * 0.25f);
                        last().updatePosition();
                        chargingTimeC += Time.delta;
                    }else{
                        chargingTime += Time.delta;
                    }
                    if(chargingTimeC >= 2f * 60f){
                        chargingTime = 81f;
                    }
                    Tmp.v1.trns(last().rotation + 180f, type.speed * type.accel);
                    endVelocity.add(Tmp.v1).limit(type.speed);
                }else{
                    Position origin = parentPosition(-1);
                    Tmp.v2.trns(last().angleTo(origin) + 180f, type.range / 3f).add(origin);
                    float dst = last().dst(Tmp.v2) / 200f;
                    Tmp.v1.set(last()).approachDelta(Tmp.v2, Math.min(type.speed * type.accel, dst)).sub(last());
                    endVelocity.add(Tmp.v1).limit(type.speed);
                    chargingTimeB += Time.delta;
                    if(chargingTimeB >= 2f * 60f){
                        chargingTimeC = 0f;
                        chargingTimeB = 0f;
                        chargingTime = 0f;
                    }
                }
            }
        }

        if(endVelocity.len2() > 0.0001f){
            last().pos.add(endVelocity, Time.delta);
            for(int i = segments.length - 2; i >= 0; i--){
                TentacleSegment seg = segments[i], segNext = segments[i + 1];

                Position nextPos = Tmp.v1.set(segNext.oppositePosition());
                float newAngle = seg.oppositePosition().angleTo(nextPos) + 180f;
                newAngle = Utils.clampedAngle(newAngle, segNext.rotation, segNext.angleLimit());
                float angVel = Utils.angleDistSigned(newAngle, seg.rotation);
                seg.angularVelocity += angVel;
                seg.rotation = newAngle;
                seg.updatePosBack();
            }
            for(TentacleSegment segment : segments){
                segment.updatePosition();
            }
        }

        endVelocity.scl(1f - Mathf.clamp(type.drag * Time.delta));
    }

    void updateWeapon(){
        if(type.tentacleDamage > 0 && timer.get(1, 5f)){
            if((endVelocity.len() - type.startVelocity) > 0.0001f && type.speed > 0){
                float damage = type.tentacleDamage * Interp.pow2In.apply(Mathf.clamp(((endVelocity.len() - type.startVelocity) * (1f + (type.startVelocity / type.speed))) / type.speed));
                if(damage > 0){
                    Utils.collideLineRawEnemy(unit.team, last().getX(), last().getY(), lastTipX, lastTipY, (building, aBoolean) -> {
                        if(aBoolean){
                            building.damage(damage);
                        }
                        return false;
                    }, unit1 -> unit1.damage(damage), null, Fx.hitBulletSmall::at);
                }
            }
            lastTipX = last().getX();
            lastTipY = last().getY();
        }
        if(type.bullet == null) return;
        if(reloadTime >= type.reload){
            if(Angles.within(last().rotation + 180f, last().angleTo(targetPos), type.shootCone) && last().within(targetPos, type.range) && (target != null || (unit.isPlayer() && unit.isShooting()))){
                Bullet b = type.bullet.create(unit, unit.team, last().getX(), last().getY(), last().rotation + 180f);
                if(type.continuous) bullet = b;
                reloadTime = 0f;
            }
        }else if(bullet == null || !type.continuous){
            reloadTime += Time.delta * unit.reloadMultiplier();
        }
        if(bullet != null){
            bullet.set(last());
            bullet.rotation(last().rotation + 180f);
            if(bullet.time >= bullet.lifetime || !bullet.isAdded() || bullet.type != type.bullet) bullet = null;
        }
    }

    void updateTargeting(){
        Position origin = parentPosition(-1);
        TentacleSegment segment = segments[segments.length - 1];
        if(Units.invalidateTarget(target, unit.team, origin.getX(), origin.getY(), type.range())) target = null;
        if(timer.get(20f) && (!unit.isPlayer() || type.automatic)){
            target = Units.closestTarget(unit.team, segment.getX(), segment.getY(), type.range,
            unit -> origin.within(unit, type.range()) && unit.isValid(),
            building -> origin.within(building, type.range()));
        }
        if(!unit.isPlayer() || type.automatic){
            if(target != null && (type.bullet != null || unit.isShooting())){
                attacking = true;
                targetPos.set(target);
            }else{
                attacking = false;
            }
        }else{
            if(unit.isShooting()){
                attacking = true;
                targetPos.set(unit.aimX, unit.aimY);
            }else{
                attacking = false;
            }
        }
    }

    float indexRotation(int index){
        if(index < 0){
            return unit.rotation + type.rotationOffset;
        }
        return segments[Math.min(index, segments.length - 1)].rotation;
    }

    Position parentPosition(int index){
        if(index < 0){
            return Tmp.v4.trns(unit.rotation - 90f, type.x, type.y).add(unit);
        }
        if(index >= segments.length) return null;
        return segments[index];
    }

    TentacleSegment last(){
        return segments[segments.length - 1];
    }

    public void update(){
        updateMovement();
        for(TentacleSegment segment : segments){
            segment.pos.add(segment.vel.x * Time.delta, segment.vel.y * Time.delta);
            segment.vel.scl(1f - Mathf.clamp(type.drag * Time.delta));

            float offset = swayScl > 0f ? Mathf.sin(Time.time + (segment.index * type.swaySegmentOffset) + type.swayOffset, type.swayScl, type.swayMag * swayScl) * Mathf.sign(type.flipSprite) : 0f;
            segment.angularVelocity += offset;
            segment.angularVelocity = Mathf.clamp(segment.angularVelocity, -type.speed, type.speed);
        }
        for(TentacleSegment segment : segments){
            //float offset = swayScl > 0f ? Mathf.sin(Time.time + (segment.index * type.swaySegmentOffset) + type.swayOffset, type.swayScl, type.swayMag * swayScl) * Mathf.sign(type.flipSprite) : 0f;
            //segment.angularVelocity += offset;
            if(segment.index == 0){
                Tmp.v1.trns(unit.rotation - 90f, type.x, type.y).add(unit);

                float angle = segment.angleTo(Tmp.v1) + (segment.angularVelocity * Time.delta);
                segment.rotation = Utils.clampedAngle(angle, unit.rotation + type.rotationOffset, type.firstSegmentAngleLimit);
                //segment.rotation = segment.angleTo(Tmp.v1) + offset;
                Tmp.v2.trns(segment.rotation, type.segmentLength).add(segment).sub(Tmp.v1);
                segment.pos.sub(Tmp.v2);
                Tmp.v3.trns(segment.rotation, Tmp.v2.len() / Time.delta);
            }else{
                TentacleSegment last = segments[segment.index - 1];

                float angle = segment.angleTo(last) + (segment.angularVelocity * Time.delta);
                segment.rotation = Utils.clampedAngle(angle, last.rotation, type.angleLimit);
                //segment.rotation = segment.angleTo(last) + offset;
                Tmp.v2.trns(segment.rotation, type.segmentLength).add(segment).sub(last);
                segment.pos.sub(Tmp.v2);
                Tmp.v3.trns(segment.rotation, Math.max(last.vel.len(), segment.vel.len()));
            }
            //segment.rotation += segment.angularVelocity * Time.delta;
            segment.vel.add(Tmp.v3);
            segment.vel.limit(type.speed);
            segment.angularVelocity *= 1f - Mathf.clamp(type.drag * Time.delta);
        }
        updateTargeting();
        if(target != null || unit.isPlayer()) updateWeapon();
    }

    public void draw(){
        for(int i = 0; i < segments.length; i++){
            TextureRegion region = i == segments.length - 1 ? type.tipRegion : type.region;
            TentacleSegment a = segments[i];
            Position b = parentPosition(i - 1);
            Tmp.v1.set(a).sub(b).setLength(region.width / 4f).add(b);
            unit.type.applyColor(unit);
            Lines.stroke(region.height * Draw.scl * Mathf.sign(type.flipSprite));
            Lines.line(region, b.getX(), b.getY(), Tmp.v1.x, Tmp.v1.y, false);
        }
        Draw.color();
    }

    public Tentacle add(TentacleType t, Unit unit){
        type = t;
        segments = new TentacleSegment[t.segments];
        this.unit = unit;
        for(int i = 0; i < segments.length; i++){
            TentacleSegment s = new TentacleSegment();
            s.index = i;
            s.main = this;
            s.pos.trns(unit.rotation + type.rotationOffset + 180f, (i + 1) * type.segmentLength).add(Tmp.v1.trns(unit.rotation - 90f, type.x, type.y).add(unit));
            s.rotation = unit.rotation + 180f + type.rotationOffset;
            segments[i] = s;
        }
        lastTipX = last().getX();
        lastTipY = last().getY();
        return this;
    }

    public static class TentacleSegment implements Position{
        Vec2 pos = new Vec2();
        Vec2 vel = new Vec2();
        Tentacle main;
        int index;
        float angularVelocity;
        float rotation;

        float angleLimit(){
            return index == 0 ? main.type.firstSegmentAngleLimit : main.type.angleLimit;
        }

        void updatePosition(){
            float angle = main.indexRotation(index - 1);
            rotation = Utils.clampedAngle(rotation, angle, (index == 0 ? main.type.firstSegmentAngleLimit : main.type.angleLimit));
            Tmp.v2.trns(rotation, main.type.segmentLength).add(this).sub(main.parentPosition(index - 1));
            pos.sub(Tmp.v2);
            vel.limit(main.type.speed);
        }

        void updatePosBack(){
            if(index >= main.segments.length - 1) return;
            TentacleSegment next = main.segments[index + 1];
            Tmp.v2.trns(next.rotation, main.type.segmentLength).add(next).sub(this);
            vel.add(Tmp.v2);
            vel.limit(main.type.speed);
            pos.add(Tmp.v2);
        }

        Position oppositePosition(){
            return Tmp.v4.trns(rotation, main.type.segmentLength).add(this);
        }

        @Override
        public float getX(){
            return pos.x;
        }

        @Override
        public float getY(){
            return pos.y;
        }
    }
}

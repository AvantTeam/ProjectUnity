package unity.entities;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import unity.type.*;
import unity.util.*;

public class Tentacle{
    TentacleSegment[] segments;
    TentacleType type;
    Unit unit;
    Teamc target;
    Interval timer = new Interval();
    Vec2 targetPos = new Vec2();
    Vec2 endVelocity = new Vec2();
    float swayScl = 1f;
    float reloadTime = 0f;
    Bullet bullet;
    boolean attacking = false;

    public void updateMovement(){
        if(!attacking){
            swayScl = Mathf.lerpDelta(swayScl, 1f, 0.04f);
        }else{
            swayScl = Mathf.lerpDelta(swayScl, 0f, 0.04f);

            float speed = type.speed * type.accel;
            Tmp.v1.set(last()).approachDelta(targetPos, speed).sub(last());
            endVelocity.add(Tmp.v1).limit(type.speed);
        }
        //float lastAngle = indexRotation(-1);
        //Tmp.v1.set(last()).approachDelta(targetPos, speed).sub(last());

        //endVelocity.add(Tmp.v1).limit(type.speed);
        if(attacking){
            last().rotation = Angles.moveToward(last().rotation, last().angleTo(targetPos) + 180f, type.rotationSpeed);
            last().updatePosition();

            boolean found = false;
            int k = segments.length + 5;
            int j = 0;

            for(int i = 0; i < segments.length - 1; i++){
                TentacleSegment segment = segments[i];
                if(!Angles.near(segment.rotation, indexRotation(i + 1), segment.angleLimit() - 0.5f) || found || segments.length > k - 5){
                    segment.rotation = Angles.moveToward(segment.rotation, segment.angleTo(targetPos) + 180f, type.rotationSpeed);
                    if(segments.length <= k - 5){
                        if(!found){
                            found = true;
                            j = 0;
                        }else{
                            j++;
                            if(j > 4) found = false;
                        }
                    }
                }
                segment.updatePosition();
                if(Angles.near(segment.rotation, segment.angleTo(targetPos) + 180f, 1f)){
                    k = segment.index;
                }
            }
            last().updatePosition();
        }

        endVelocity.scl(1f - (type.drag * Time.delta));
    }

    void updateWeapon(){
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
            if(bullet.time >= bullet.lifetime || !bullet.isAdded()) bullet = null;
        }
    }

    void updateTargeting(){
        Position origin = parentPosition(-1);
        TentacleSegment segment = segments[segments.length - 1];
        if(Units.invalidateTarget(target, unit.team, origin.getX(), origin.getY(), type.range())) target = null;
        if(timer.get(20f) && !unit.isPlayer()){
            target = Units.closestTarget(unit.team, segment.getX(), segment.getY(), type.range,
            unit -> origin.within(unit, type.range()) && unit.isValid(),
            building -> origin.within(building, type.range()));
        }
        if(!unit.isPlayer()){
            if(target != null){
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
            segment.vel.scl(1f - (type.drag * Time.delta));
        }
        for(TentacleSegment segment : segments){
            float offset = swayScl > 0f ? Mathf.sin(Time.time + (segment.index * type.swaySegmentOffset) + type.swayOffset, type.swayScl, type.swayMag * swayScl) * Mathf.sign(type.flipSprite) : 0f;
            if(segment.index == 0){
                Tmp.v1.trns(unit.rotation - 90f, type.x, type.y).add(unit);
                
                segment.rotation = Utils.clampedAngle(segment.angleTo(Tmp.v1) + offset, unit.rotation + type.rotationOffset, type.firstSegmentAngleLimit);
                //segment.rotation = segment.angleTo(Tmp.v1) + offset;
                Tmp.v2.trns(segment.rotation, type.segmentLength / 2f).add(segment).sub(Tmp.v1);
                segment.pos.sub(Tmp.v2);
                Tmp.v3.trns(segment.rotation, Tmp.v2.len() / Time.delta);
            }else{
                TentacleSegment last = segments[segment.index - 1];
                
                segment.rotation = Utils.clampedAngle(segment.angleTo(last) + offset, last.rotation, type.angleLimit);
                //segment.rotation = segment.angleTo(last) + offset;
                Tmp.v2.trns(segment.rotation, type.segmentLength).add(segment).sub(last);
                segment.pos.sub(Tmp.v2);
                Tmp.v3.trns(segment.rotation, Math.max(last.vel.len(), segment.vel.len()));
            }
            segment.vel.add(Tmp.v3);
            segment.vel.limit(type.speed);
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
        return this;
    }

    public static class TentacleSegment implements Position{
        Vec2 pos = new Vec2();
        Vec2 vel = new Vec2();
        Tentacle main;
        int index;
        float rotation;

        float angleLimit(){
            return index == 0 ? main.type.firstSegmentAngleLimit : main.type.angleLimit;
        }

        public void updatePosition(){
            float angle = main.indexRotation(index - 1);
            rotation = Utils.clampedAngle(rotation, angle, (index == 0 ? main.type.firstSegmentAngleLimit : main.type.angleLimit));
            Tmp.v2.trns(rotation, main.type.segmentLength / (index == 0 ? 2f : 1f)).add(this).sub(main.parentPosition(index - 1));
            pos.sub(Tmp.v2);
            vel.limit(main.type.speed);
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

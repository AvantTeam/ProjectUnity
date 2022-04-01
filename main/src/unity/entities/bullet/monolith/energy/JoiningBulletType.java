package unity.entities.bullet.monolith.energy;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.graphics.*;

import static mindustry.Vars.*;

/**
 * Joins another bullet of the same identifier if within reach. Similar velocity is put to higher priority.
 * @author GlennFolker
 */
public class JoiningBulletType extends BulletType{
    /** Custom trail. */
    public Func<Bullet, Trail> trailType = b -> new Trail(trailLength);
    /** Similar to {@link #homingPower}, but used when attracting to join target. */
    public float sensitivity = 0.2f;
    /** Similar to {@link #homingDelay}, but used when attracting to join target. */
    public float joinDelay = 0f;
    /** The maximum speed of the attraction. */
    public float attractMaxSpeed = -1f;
    /** The join target must be within this angle cone. */
    public float joinCone = 60f;
    /** How far can the bullet sense other joining bullets. */
    public float joinRange = 2f * tilesize;
    /** Minimum dot product of the 2 bullets. */
    public float minDot = 0.2f;

    public final String name;
    public float radius = 6f;
    public TextureRegion region, hair1Region, hair2Region, hair3Region;

    private static int lastID;
    /** Like {@link #id}, but doesn't get modified in {@link #copy()}. */
    private final int identifier;

    private static Bullet lastBullet;
    private static float lastScore;

    public JoiningBulletType(float speed, float damage, String name){
        super(speed, damage);
        identifier = lastID++;
        this.name = name;
    }

    @Override
    public void load(){
        region = Core.atlas.find(name);
        hair1Region = Core.atlas.find(name + "-hair-1");
        hair2Region = Core.atlas.find(name + "-hair-2");
        hair3Region = Core.atlas.find(name + "-hair-3");
    }

    @Override
    public void draw(Bullet b){
        drawTrail(b);

        float
            w = radius * 2f, h = radius * 2f * ((6f + b.vel.len()) / (6f + speed)), rot = b.rotation() - 90f,
            s1 = 0.8f + Mathf.absin(8f, 0.2f), s2 = 0.85f + Mathf.absin(14f, 0.15f), s3 = 0.9f + Mathf.absin(20f, 0.1f);

        Draw.rect(region, b.x, b.y, w, h, rot);
        UnityDrawf.distortRect(hair1Region, b.x, b.y, w * s1, h * s1, rot, (float vx, float vy, float x, float y, float rotation, float pivotX, float pivotY, float sw, float sh, Vec2 out) -> {
            float cx = x + sw / 2f, cy = y + sh / 2f;
            return out
                .trns(Time.time * 12f + b.id * 32f, w / 5f * s3)
                .scl(1f - Mathf.dst((vx - cx) / sw, (vy - cy) / sh));
        });

        UnityDrawf.distortRect(hair2Region, b.x, b.y, w * s2, h * s2, rot, (float vx, float vy, float x, float y, float rotation, float pivotX, float pivotY, float sw, float sh, Vec2 out) -> {
            float cx = x + sw / 2f, cy = y + sh / 2f;
            return out
                .trns(Time.time * 6f + b.id * 32f, w / 5f * s1)
                .scl(1f - Mathf.dst((vx - cx) / sw, (vy - cy) / sh));
        });

        Draw.blend(Blending.additive);
        UnityDrawf.distortRect(hair3Region, b.x, b.y, w * s3, h * s3, rot, (float vx, float vy, float x, float y, float rotation, float pivotX, float pivotY, float sw, float sh, Vec2 out) -> {
            float cx = x + sw / 2f, cy = y + sh / 2f;
            return out
                .trns(Time.time * 2f + b.id * 32f, w / 5f * s2)
                .scl(1f - Mathf.dst((vx - cx) / sw, (vy - cy) / sh));
        });
        Draw.blend();
    }

    @Override
    public void init(){
        super.init();
        if(attractMaxSpeed == -1f) attractMaxSpeed = speed * 2.5f;
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        JoinData data = new JoinData();
        data.bullet = b;
        b.data = data;
    }

    @Override
    public void update(Bullet b){
        lastBullet = null;
        lastScore = 0f;
        Groups.bullet.intersect(b.x - joinRange, b.y - joinRange, 2f * joinRange, 2f * joinRange, e -> {
            float dot = 0f;
            if(e.team == b.team && (lastBullet == null || (
                e.type instanceof JoiningBulletType type && type.identifier == identifier &&
                Angles.within(b.rotation(), e.rotation(), joinCone)
            ) && (dot = b.vel.dot(e.vel)) >= minDot && lastScore < dot)){
                lastBullet = e;
                lastScore = dot;
            }
        });

        JoinData data = (JoinData)b.data;
        if(lastBullet == null){
            data.target = null;
        }else if(data.target == null){
            data.target = lastBullet;
            data.rotation = b.rotation();
        }else{
            data.target = lastBullet;
        }

        Bullet t = data.target;
        if(t != null){
            if(b.isAdded() && b.collides(t)){
                b.remove();
                t.remove();

                JoinData other = t.data instanceof JoinData d ? d : null;
                float bfin = b.fin(), tfin = t.fin();

                create(
                    b.owner == null ? t.owner : b.owner, b.team,
                    (b.x + t.x) / 2f, (b.y + t.y) / 2f,
                    (data.rotation + (other != null ? other.rotation : t.rotation())) / 2f,
                    Math.max(b.damage, t.damage) + Math.min(b.damage, t.damage) / 2f,
                    1f, Math.max(bfin, tfin) + Math.min(bfin, tfin) / 2f, null
                );
            }

            if(b.time >= joinDelay){
                float len = b.vel.len();

                b.vel.add(Tmp.v1.set(t).sub(b).setLength(sensitivity * Time.delta * 0.3f));
                b.vel.limit(Math.max(len, speed * 2.5f));
            }
        }else if(homingPower > 0.0001f && b.time >= homingDelay){
            Teamc target;
            if(healPercent > 0){
                target = Units.closestTarget(null, b.x, b.y, homingRange,
                    e -> e.checkTarget(collidesAir, collidesGround) && e.team != b.team && !b.hasCollided(e.id),
                    e -> collidesGround && (e.team != b.team || e.damaged()) && !b.hasCollided(e.id)
                );
            }else{
                target = Units.closestTarget(b.team, b.x, b.y, homingRange, e -> e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id), e -> collidesGround && !b.hasCollided(e.id));
            }

            if(target != null){
                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50f));
            }
        }

        if(weaveMag > 0){
            b.vel.rotate(Mathf.sin(b.time + Mathf.pi * weaveScale/2f, weaveScale, weaveMag * (Mathf.randomSeed(b.id, 0, 1) == 1 ? -1 : 1)) * Time.delta);
        }

        if(trailChance > 0){
            if(Mathf.chanceDelta(trailChance)){
                trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : trailParam, trailColor);
            }
        }

        if(trailInterval > 0f){
            if(b.timer(0, trailInterval)){
                trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : trailParam, trailColor);
            }
        }
    }

    @Override
    public void updateTrail(Bullet b){
        if(!headless && trailLength > 0){
            if(b.trail == null){
                b.trail = trailType.get(b);
            }

            b.trail.update(b.x, b.y, trailInterp.apply(b.fin()));
        }
    }

    @Override
    public void despawned(Bullet b){
        super.despawned(b);
        b.trail = null;
    }

    protected static class JoinData{
        protected Bullet bullet;
        protected Bullet target;
        protected float rotation;
    }
}

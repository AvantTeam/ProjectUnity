package unity.entities.bullet.monolith.energy;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.gen.*;
import unity.graphics.*;
import unity.util.*;

import static mindustry.Vars.*;

/**
 * Joins another bullet of the same identifier if within reach. Similar velocity is put to higher priority.
 * @author GlennFolker
 */
public class JoiningBulletType extends BulletType{
    /** Similar to {@link #homingPower}, but used when attracting to join target. */
    public float sensitivity = 0.2f;
    /** Similar to {@link #homingDelay}, but used when attracting to join target. */
    public float joinDelay = 0f;
    /** The maximum speed of the attraction. */
    public float attractMaxSpeed = -1f;
    /** The maximum joint damage. */
    public float maxDamage = -1f;
    /** The scale of damage yielded when joining. */
    public float yieldScl = 1.25f;
    /** The join target must be within this angle cone. */
    public float joinCone = 60f;
    /** How far can the bullet sense other joining bullets. */
    public float joinRange = 2f * tilesize;
    /** Minimum dot product of the 2 bullets. */
    public float minDot = 0.2f;

    public float radius = 10f;
    public Color[] colors = {UnityPal.monolithGreenLight, UnityPal.monolithGreen, UnityPal.monolithGreenDark};
    public Color edgeColor = UnityPal.monolithGreenLight.cpy().a(0.8f);

    public Effect joinEffect = Fx.none;

    private static int lastID;
    /** Like {@link #id}, but doesn't get modified in {@link #copy()}. */
    private final int identifier;

    private static Bullet lastBullet;
    private static float lastScore;

    public JoiningBulletType(float speed, float damage){
        super(speed, damage);
        identifier = lastID++;
    }

    public float bulletRadius(Bullet b){
        return 0.84f + 0.16f * (b.damage / damage);
    }

    @Override
    public void init(){
        if(attractMaxSpeed == -1f) attractMaxSpeed = speed * 2.5f;
        if(maxDamage == -1f) maxDamage = damage * 4f;
        super.init();
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        JoinData data = new JoinData();
        data.bullet = b;
        b.data = data;
    }

    @Override
    public void draw(Bullet b){
        drawTrail(b);

        float r = radius * bulletRadius(b), start = radius * 0.8f, stroke = 2f, z = Layer.flyingUnitLow - 0.01f;

        Lines.stroke(stroke);
        TextureRegion reg = Core.atlas.white(), light = Core.atlas.find("unity-line-shade");

        int startAmount = Math.max(Mathf.round((r - start) / stroke), 0),
            amount = Math.max(Mathf.round(r / stroke), 1);

        for(int i = startAmount; i < amount; i++){
            Draw.color(colors[Mathf.randomSeed(b.id - i, 0, colors.length - 1)]);
            float sr = stroke + i * stroke;

            Mathf.rand.setSeed(b.id + i);
            Utils.q1.set(Tmp.v31.set(switch(Mathf.randomSeed(b.id * 2L, 0, 2)){
                case 0 -> Vec3.X;
                case 1 -> Vec3.Y;
                default -> Vec3.Z;
            }).setToRandomDirection(), Time.time * 6f + Mathf.randomSeed((b.id + i) * 4L, 0f, 1000f));

            UnityDrawf.panningCircle(reg,
                b.x, b.y, 1f, 1f,
                sr, 360f, 0f,
                Utils.q1, true, z, z
            );

            Draw.color(Draw.getColor(), Color.black, 0.33f);
            Draw.blend(Blending.additive);
            UnityDrawf.panningCircle(light,
                b.x, b.y, 5f, 5f,
                sr, 360f, 0f,
                Utils.q1, true, z, z
            );

            Draw.blend();
        }

        Fill.light(b.x, b.y, Lines.circleVertices(r), r, Color.clear, edgeColor);
        Draw.reset();
    }

    @Override
    public void hit(Bullet b, float x, float y){
        super.hit(b, x, y);
        hitEffect.at(x, y, b.rotation(), hitColor, Float2.construct(radius, bulletRadius(b)));
    }

    @Override
    public void despawned(Bullet b){
        super.despawned(b);
        despawnEffect.at(b.x, b.y, b.rotation(), hitColor, Float2.construct(radius, bulletRadius(b)));
    }

    @Override
    public void removed(Bullet b){
        super.removed(b);
        b.trail = null;
    }

    @Override
    public void update(Bullet b){
        if(!b.isAdded()) return;

        lastBullet = null;
        lastScore = 0f;
        Groups.bullet.intersect(b.x - joinRange, b.y - joinRange, 2f * joinRange, 2f * joinRange, e -> {
            if(!e.isAdded() || e == b) return;

            float dot = 0f;
            if(e.damage < maxDamage && e.team == b.team && (lastBullet == null || (
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
            b.hitbox(Tmp.r1);
            t.hitbox(Tmp.r2);

            if(Tmp.r1.overlaps(Tmp.r2)){
                Effect bd = despawnEffect, td = t.type.despawnEffect;
                despawnEffect = joinEffect;
                t.type.despawnEffect = joinEffect;

                b.remove();
                t.remove();
                despawnEffect = bd;
                t.type.despawnEffect = td;

                JoinData other = t.data instanceof JoinData d ? d : null;
                float bt = b.fout(), tt = t.fout();

                Bullet n = create(
                    b.owner == null ? t.owner : b.owner, b.team,
                    (b.x + t.x) / 2f, (b.y + t.y) / 2f,
                    Mathf.slerp(data.rotation, other != null ? other.rotation : t.rotation(), Mathf.clamp(t.vel.len() / b.vel.len() / 2f)),
                    Math.max(b.damage, t.damage) + Math.min(b.damage, t.damage) * yieldScl,
                    1f, Math.max(bt, tt) + Math.min(bt, tt) / 2f, null
                );
                n.hitSize *= bulletRadius(n);
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

        updateTrail(b);
        if(weaveMag > 0){
            b.vel.rotate(Mathf.sin(b.time + Mathf.pi * weaveScale / 2f, weaveScale, weaveMag * (Mathf.randomSeed(b.id, 0, 1) == 1 ? -1 : 1)) * Time.delta);
        }

        if(trailChance > 0){
            if(Mathf.chanceDelta(trailChance)){
                trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : (trailParam * bulletRadius(b)), trailColor);
            }
        }

        if(trailInterval > 0f){
            if(b.timer(0, trailInterval)){
                trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : (trailParam * bulletRadius(b)), trailColor);
            }
        }
    }

    protected static class JoinData{
        protected Bullet bullet;
        protected Bullet target;
        protected float rotation;
    }
}

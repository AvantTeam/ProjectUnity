package unity.entities.bullet.anticheat;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import unity.graphics.*;
import unity.util.*;

public class DesolationBulletType extends AntiCheatBulletTypeBase{
    protected float health = 1500000f, maxDamage = 5000f;
    protected float bulletDamage = 43f;
    protected float length = 70f, width = 180f, offset = 1.75f;
    protected float startingScl = 1.4f, scaleReduction = 0.8f;
    protected float fadeOutTime = 20f;
    public Color[] colors = {UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};

    public DesolationBulletType(float speed, float damage){
        super(speed, damage);
        hittable = false;
        absorbable = false;
        collides = false;
        pierce = true;
        pierceShields = true;
        impact = true;
        keepVelocity = false;
        knockback = 5f;
    }

    @Override
    public float continuousDamage(){
        return damage / 5f * 60f;
    }

    @Override
    public float estimateDPS(){
        return damage * 100f / 5f * 3f;
    }

    @Override
    public void init(){
        super.init();
        despawnHit = false;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        DesolationBulletData data = new DesolationBulletData();
        data.health = health;
        b.data = data;
    }

    @Override
    public void update(Bullet b){
        if(b.timer(1, 5f) && b.data instanceof DesolationBulletData){
            DesolationBulletData d = (DesolationBulletData)b.data;
            d.collided.clear();
            float in = d.health / health;
            float out = Mathf.clamp(b.time > b.lifetime - fadeOutTime ? 1f - (b.time - (lifetime - fadeOutTime)) / fadeOutTime : 1f);
            Vec2 v1 = Tmp.v1.trns(b.rotation(), length / 2f);
            for(int s : Mathf.signs){
                Vec2 v2 = Tmp.v2.trns(b.rotation() - 90f, width * in * s * out, -(length / 2f) * offset).add(b);
                float x1 = b.x + v1.x, x2 = b.x - v1.x,
                y1 = b.y + v1.y, y2 = b.y - v1.y;
                Utils.inTriangle(Groups.unit, x1, y1, x2, y2, v2.x, v2.y, u -> u.team != b.team && d.collided.add(u.id), u -> {
                    d.health -= Math.min(u.health, maxDamage);
                    hitUnitAntiCheat(b, u);
                    Vec2 p = Intersector.nearestSegmentPoint(x2, y2, v2.x, v2.y, u.x, u.y, Tmp.v3);
                    p.sub(u).limit(u.hitSize / 2f).add(u);
                    hit(b, p.x, p.y);
                });
                Utils.inTriangle(Groups.bullet, x1, y1, x2, y2, v2.x, v2.y, bb -> bb.team != b.team && bb.type.hittable && d.collided.add(bb.id), bb -> {
                    bb.time = Mathf.clamp(bb.time + (bulletDamage / bb.type.damage), 0f, bb.lifetime);
                    Tmp.v3.trns(Mathf.slerp(b.rotation(), b.angleTo(bb), 0.5f), knockback / (bb.hitSize / 2f)).add(bb.vel).limit(bb.type.speed);
                    bb.vel.set(Tmp.v3);
                });
                Utils.inTriangleBuilding(b.team, true, x1, y1, x2, y2, v2.x, v2.y, build -> d.collided.add(build.id), build -> {
                    d.health -= Math.min(build.health, maxDamage);
                    hitBuildingAntiCheat(b, build);
                    Vec2 p = Intersector.nearestSegmentPoint(x2, y2, v2.x, v2.y, build.x, build.y, Tmp.v3);
                    float sz = build.block.size * Vars.tilesize / 2f;
                    p.x = Mathf.clamp(p.x, build.x - sz, build.x + sz);
                    p.y = Mathf.clamp(p.y, build.y - sz, build.y + sz);
                    hit(b, p.x, p.y);
                });
            }
            if(d.health <= 0f) b.remove();
        }
    }

    @Override
    public void draw(Bullet b){
        if(b.data instanceof DesolationBulletData){
            DesolationBulletData d = (DesolationBulletData)b.data;
            float in = d.health / health;
            Vec2 v1 = Tmp.v1.trns(b.rotation(), length / 2f);
            float scl = startingScl;
            float out = Mathf.clamp(b.time > b.lifetime - fadeOutTime ? 1f - (b.time - (lifetime - fadeOutTime)) / fadeOutTime : 1f);
            for(Color c : colors){
                Draw.color(c);
                float rx = Mathf.range(2f), ry = Mathf.range(2f);
                for(int s : Mathf.signs){
                    Vec2 v2 = Tmp.v2.trns(b.rotation() - 90f, width * in * s * out, -(length / 2f) * offset);
                    float x1 = b.x + (v1.x * scl), x2 = b.x - v1.x, x3 = b.x + (v2.x * scl),
                    y1 = b.y + (v1.y * scl), y2 = b.y - v1.y, y3 = b.y + (v2.y * scl);
                    Fill.tri(x1 + rx, y1 + ry, x2 + rx, y2 + ry, x3 + rx, y3 + ry);
                }
                scl *= scaleReduction;
            }
            Draw.reset();
        }
    }

    @Override
    public void drawLight(Bullet b){

    }

    static class DesolationBulletData{
        IntSet collided = new IntSet(103);
        float health = 0f;
    }
}

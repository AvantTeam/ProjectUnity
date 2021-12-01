package unity.entities.bullet.anticheat;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.Pool.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.effects.*;
import unity.graphics.*;
import unity.util.*;

public class VoidPortalBulletType extends AntiCheatBulletTypeBase{
    public float length = 800f;
    public float width = 95f;
    public float fadeInTime = 180f, fadeOutTime = 20f;
    public float tentacleRange = 500f, tentacleWidth = 7f, tentaclePull = 3f, tentacleOutOfRangeStrength = 1f / 80f, tentacleRangeReduction = 4f;
    public float tentacleDamage = 50f;

    private final static IntSet collided = new IntSet(102);
    private final static BasicPool<VoidTentacle> tentaclePool = new BasicPool<>(8, 250, VoidTentacle::new);

    public VoidPortalBulletType(float damage){
        super(0f, damage);
        lifetime = 4f * 60f;
        collides = hittable = absorbable = keepVelocity = false;
        pierce = pierceShields = true;
        despawnEffect = Fx.none;
    }

    @Override
    public void init(){
        super.init();

        drawSize = length * 2f;
    }

    @Override
    public float range(){
        return length;
    }

    @Override
    public float estimateDPS(){
        return damage * (lifetime / 2f) / 5f * 3f;
    }

    @Override
    public float continuousDamage(){
        return damage / 5f * 60f;
    }

    @Override
    public void update(Bullet b){
        float fout = Mathf.clamp(b.time > b.lifetime - fadeOutTime ? 1f - (b.time - (lifetime - fadeOutTime)) / fadeOutTime : 1f);
        float fin = b.time < fadeInTime ? Mathf.clamp(b.time / fadeInTime) : 1f;
        float fin2 = Mathf.curve(b.fin(), 0f, 15f / lifetime);

        Vec2 end = Tmp.v1.trns(b.rotation(), length * fin2).add(b);
        Vec2 mid = Tmp.v2.set(end).sub(b).scl(1f / 2f).add(b);
        Vec2 s = Tmp.v3.trns(b.rotation() - 90f, width * fin * fout);

        Effect.shake(5f * fin, 5f * fin, mid);

        if(b.timer(0, 5f)){
            float ex = end.x, ey = end.y, mx = mid.x, my = mid.y, sx = s.x, sy = s.y;
            collided.clear();

            Utils.inTriangleBuilding(b.team, true, b.x, b.y, mx + sx, my + sy, mx - sx, my - sy, building -> collided.add(building.id), building -> {
                hit(b, building.x, building.y);
                hitBuildingAntiCheat(b, building);
            });
            Utils.inTriangleBuilding(b.team, true, mx + sx, my + sy, mx - sx, my - sy, ex, ey, building -> collided.add(building.id), building -> {
                hit(b, building.x, building.y);
                hitBuildingAntiCheat(b, building);
            });

            Utils.inTriangle(Groups.unit, b.x, b.y, mx + sx, my + sy, mx - sx, my - sy, u -> u.team != b.team && collided.add(u.id), u -> {
                hit(b, u.x, u.y);
                hitUnitAntiCheat(b, u);
            });
            Utils.inTriangle(Groups.unit, mx + sx, my + sy, mx - sx, my - sy, ex, ey, u -> u.team != b.team && collided.add(u.id), u -> {
                hit(b, u.x, u.y);
                hitUnitAntiCheat(b, u);
            });
        }
        if(b.data instanceof VoidPortalData){
            VoidPortalData data = (VoidPortalData)b.data;
            if(Mathf.chanceDelta(0.2f)){
                Tmp.v1.set(b).sub(mid);
                float l = Mathf.range(1f);
                float o = Mathf.range(1f) * (1f - Math.abs(l));
                float x = (Tmp.v1.x * l) + mid.x + (s.x * o);
                float y = (Tmp.v1.y * l) + mid.y + (s.y * o);
                Unit unit = Units.bestEnemy(b.team, x, y, tentacleRange, Healthc::isValid, (u, sx, sy) ->
                data.map.get(u.id, 0) + (b.dst(u) / (tentacleRange * 2f)));
                if(unit != null){
                    VoidTentacle t = tentaclePool.obtain();
                    t.set(unit, x, y, tentacleRange);
                    data.tentacles.add(t);

                    data.map.put(unit.id, data.map.get(unit.id, 0) + 1);
                }
            }
            data.tentacles.removeAll(t -> t.update(b, this, mid.x, mid.y));
        }
    }

    @Override
    public void draw(Bullet b){
        float fout = Mathf.clamp(b.time > b.lifetime - fadeOutTime ? 1f - (b.time - (b.lifetime - fadeOutTime)) / fadeOutTime : 1f);
        float fin = b.time < fadeInTime ? Mathf.clamp(b.time / fadeInTime) : 1f;
        float fin2 = Mathf.curve(b.fin(), 0f, 15f / lifetime);

        Vec2 end = Tmp.v1.trns(b.rotation(), length * fin2).add(b);
        Vec2 mid = Tmp.v2.set(end).sub(b).scl(1f / 2f).add(b);
        Vec2 s = Tmp.v3.trns(b.rotation() - 90f, width * fin * fout);

        float z = Draw.z();
        Draw.z(Layer.flyingUnitLow - 0.0001f);
        Draw.color(UnityPal.scarColor);
        Draw.blend(UnityBlending.shadowRealm);
        Fill.tri(b.x, b.y, mid.x + s.x, mid.y + s.y, mid.x - s.x, mid.y - s.y);
        Fill.tri(end.x, end.y, mid.x + s.x, mid.y + s.y, mid.x - s.x, mid.y - s.y);
        Draw.blend();
        Draw.z(Layer.flyingUnit + 0.001f);
        if(b.data instanceof VoidPortalData){
            Draw.color(Color.black);
            for(VoidTentacle t : ((VoidPortalData)b.data).tentacles){
                t.draw(tentacleWidth, fout);
            }
        }
        Draw.z(z);
        Draw.color();
    }

    @Override
    public void drawLight(Bullet b){

    }

    @Override
    public void removed(Bullet b){
        super.removed(b);
        if(b.data instanceof VoidPortalData){
            VoidPortalData data = (VoidPortalData)b.data;
            for(VoidTentacle tentacle : data.tentacles){
                tentaclePool.free(tentacle);
            }
        }
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = new VoidPortalData();
    }

    static class VoidPortalData{
        Seq<VoidTentacle> tentacles = new Seq<>();
        IntIntMap map = new IntIntMap(102);
    }

    static class VoidTentacle implements Poolable{
        float x, y, randLen, randAng, length, time, timer;
        Unit unit;
        boolean side = Mathf.randomBoolean();

        void set(Unit unit, float x, float y, float length){
            this.unit = unit;
            this.x = x;
            this.y = y;
            this.length = length;
            randAng = Mathf.range(360f);
            randLen = Mathf.random(unit.hitSize / 3f);
            side = Mathf.randomBoolean();
        }

        @Override
        public void reset(){
            unit = null;
            x = y = randLen = randAng = length = time = timer = 0f;
        }

        boolean update(Bullet b, VoidPortalBulletType type, float cx, float cy){
            if(unit.isValid()){
                time = Math.min(20f, time + Time.delta);
                timer += Time.delta;
                float tx = unit.x + Angles.trnsx(unit.rotation + randAng, randLen);
                float ty = unit.y + Angles.trnsy(unit.rotation + randAng, randLen);

                Tmp.v1.set(x, y).sub(tx, ty).nor();
                float mx = Tmp.v1.x, my = Tmp.v1.y;
                float scl = type.tentaclePull;

                float dst = Mathf.dst(x, y, tx, ty);

                if(dst > length){
                    float s = (dst - length) * type.tentacleOutOfRangeStrength;
                    if(timer >= 5f){
                        boolean wasDead = unit.dead;
                        type.hitUnitAntiCheat(b, unit, -b.damage + (type.tentacleDamage * s));
                        if(unit.dead && !wasDead){
                            if(unit.isAdded()){
                                unit.destroy();
                            }
                            if(Vars.renderer.animateShields){
                                SpecialFx.fragmentationFast.at(unit.x, unit.y, unit.angleTo(cx, cy) + 180f, unit);
                            }
                        }
                        timer = 0f;
                    }
                    scl += s;
                }else{
                    length = Math.max(dst, length - ((length - dst) * type.tentacleOutOfRangeStrength * Time.delta));
                }
                length = Math.max(0f, length - (type.tentacleRangeReduction * Time.delta));
                scl *= 20f;
                unit.impulse(mx * scl * Time.delta, my * scl * Time.delta);
            }else{
                time -= Time.delta;
            }
            return time < 0f && !unit.isValid();
        }

        void draw(float width, float fout){
            float fin = (time / 20f) * fout;

            float tx = unit.x + Angles.trnsx(unit.rotation + randAng, randLen);
            float ty = unit.y + Angles.trnsy(unit.rotation + randAng, randLen);

            int res = 16;
            float dst = (Mathf.dst(x, y, tx, ty) / res) * Mathf.clamp(time / 13f);
            float angle = Angles.angle(x, y, tx, ty);

            Tmp.v1.set(x, y);
            float w = width * fin;
            for(int i = 0; i < res; i++){
                float bend = (1f - fin) * (360f / res) * Mathf.sign(side) * i;
                float lx = Tmp.v1.x;
                float ly = Tmp.v1.y;
                float w2 = w - ((w * (i / (float)res)) / 1.5f);
                Vec2 v = Tmp.v1.add(Tmp.v2.trns(bend + angle, dst));

                if(i == 0) Fill.circle(lx, ly, w2 / 2f);
                Lines.stroke(w2);
                Lines.line(lx, ly, v.x, v.y, false);
                Fill.circle(v.x, v.y, w2 / 2f);
            }
        }
    }
}

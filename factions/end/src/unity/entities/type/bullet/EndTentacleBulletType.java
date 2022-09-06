package unity.entities.type.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import unity.entities.*;
import unity.entities.PUDamage.*;
import unity.graphics.*;
import unity.util.*;

public class EndTentacleBulletType extends EndBulletTypeBase{
    public float estimatedRange = 170f;
    public float swaySpeed = 2.25f;
    public float endTime = 40f;
    public int maxNode = 5, minNodes = 3;
    public float segmentsPerNode = 5, collisionSegments = 2.5f;

    public EndTentacleBulletType(){
        super(0f, 210f);
        lifetime = 50f;
        collides = false;
        keepVelocity = false;
        despawnEffect = Fx.none;
    }

    @Override
    protected float calculateRange(){
        return estimatedRange;
    }

    @Override
    public void init(){
        super.init();
        drawSize = estimatedRange * 3f;
        despawnHit = false;
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        EndTentacleData d = new EndTentacleData();
        int nodes = Mathf.random(minNodes, maxNode) * 2;
        float scl = lifetime / endTime;
        d.pos = new float[nodes];
        d.vel = new float[nodes];
        for(int i = 0; i < nodes; i += 2){
            float speed = (i / (nodes - 2f)) * ((scl * estimatedRange) / lifetime);
            d.pos[i] = b.x;
            d.pos[i + 1] = b.y;
            Tmp.v1.trns(b.rotation() + (i >= (nodes - 2) ? 0f : Mathf.range(15f)), speed * Mathf.random(0.9f, 1.3f), (i > 0 && i < nodes - 2) ? Mathf.range(swaySpeed) : 0f);
            d.vel[i] = Tmp.v1.x;
            d.vel[i + 1] = Tmp.v1.y;
        }
        if(b.owner instanceof Posc p){
            d.x = p.x();
            d.y = p.y();
        }
        if(b.aimX != -1 && b.aimY != -1){
            d.target = Units.closestTarget(b.team, b.aimX, b.aimY, 15f);
        }
        b.data = d;
    }

    @Override
    public void update(Bullet b){
        //super.update(b);
        if(!(b.data instanceof EndTentacleData data)) return;
        if(data.target != null && Units.invalidateTarget(data.target, b.team, b.x, b.y)) data.target = null;
        for(int i = 0; i < data.pos.length; i += 2){
            float[] pos = data.pos;

            if(b.owner instanceof Posc p){
                float fin = 1f - (i / (data.pos.length - 2f));
                float dx = (p.x() - data.x) * fin;
                float dy = (p.y() - data.y) * fin;
                pos[i] += dx;
                pos[i + 1] += dy;
                data.x = p.x();
                data.y = p.y();
            }

            float x = pos[i] += data.vel[i] * Time.delta;
            float y = pos[i + 1] += data.vel[i + 1] * Time.delta;

            if(i >= data.pos.length - 4 && data.target != null){
                float rot = Angles.angle(x, y, data.target.x(), data.target.y());
                Tmp.v1.set(data.vel[i], data.vel[i + 1]);
                float ang = Mathf.clamp(MathUtils.angleDistSigned(Tmp.v1.angle(), rot) / 10f, -7f, 7f) * Time.delta * b.fin();
                Tmp.v1.rotate(-ang);
                data.vel[i] = Tmp.v1.x;
                data.vel[i + 1] = Tmp.v1.y;
            }

            if(i > 0){
                Building bd = Vars.world.buildWorld(pos[i], pos[i + 1]);
                float drg = bd != null ? (bd.block.absorbLasers ? 0.3f : 0.08f) : ((i < data.pos.length - 4) ? 0.02f : 0f);

                data.vel[i] *= 1f - drg * Time.delta;
                data.vel[i + 1] *= 1f - drg * Time.delta;
                if(b.time > lifetime / 2f){
                    float f = (b.time - (lifetime / 2f)) / (lifetime / 2f);
                    float ex = pos[pos.length - 2], ey = pos[pos.length - 1];
                    Vec2 v = Intersector.nearestSegmentPoint(b.x, b.y, ex, ey, x, y, Tmp.v1);
                    v.sub(x, y).scl((1f / 25f) * f);
                    data.vel[i] += v.x;
                    data.vel[i + 1] += v.y;
                }
            }
        }
        b.x = data.pos[0];
        b.y = data.pos[1];
        if(b.time >= endTime && b.fdata != 1){
            int seg = (int)((data.pos.length / 2) * collisionSegments);
            float lx = data.pos[0], ly = data.pos[1];
            CollideLineData ld = PUDamage.lineData.clear();
            ld.hitBuilding = collidesTiles && collidesGround;
            ld.unitFilter = u -> u.checkTarget(collidesAir, collidesGround);
            for(int i = 0; i < seg; i++){
                float f = (i + 1f) / seg;
                Vec2 v = MathUtils.curve(data.pos, data.pos.length, f, Tmp.v1);
                PUDamage.collideLine(b.team, lx, ly, v.x, v.y, ld, (x, y, h) -> {
                    if(data.collided.add(h.id())){
                        if(h instanceof Building bd){
                            hitBuilding(b, bd, -1f, true);
                            //Log.info("hit building");
                        }else if(h instanceof Unit u){
                            hitUnit(b, u, -1f);
                        }
                        hit(b, x, y);
                    }
                    return false;
                });
            }
            b.fdata = 1f;
        }
    }

    @Override
    public void draw(Bullet b){
        if(!(b.data instanceof EndTentacleData data)) return;
        int seg = (int)((data.pos.length / 2) * segmentsPerNode);
        //Draw.color(Color.black);
        float z = Draw.z();
        float fin = b.time < endTime ? 0f : 1f - ((b.time - endTime) / (lifetime - endTime));
        float fin2 = b.time < endTime ? 1f : 1f - ((b.time - endTime) / (lifetime - endTime));
        float w = 7f * fin2;
        Tmp.c1.set(Color.black).lerp(EndPal.endMid, fin);
        Draw.color(Tmp.c1);
        Fill.circle(data.pos[0], data.pos[1], w / 2);
        DrawUtils.beginLine();
        for(int i = 0; i < seg; i++){
            float f = i / (seg - 1f);
            Vec2 v = MathUtils.curve(data.pos, data.pos.length, f, Tmp.v1);
            DrawUtils.linePoint(v.x, v.y, Tmp.c1.toFloatBits(), (1f - f) * w, z);
            //Lines.linePoint(v);
        }
        DrawUtils.endLine(false);
        //Lines.endLine();
    }

    static class EndTentacleData{
        float[] pos, vel;
        float x, y;
        IntSet collided = new IntSet();
        Posc target;
    }
}

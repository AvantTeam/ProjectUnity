package unity.entities.bullet.misc;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.util.*;

public class TentacleBulletType extends BulletType{
    public float length = 100f;
    public float width = 2f;
    public int segments = 8;
    public float angleVelocity = 8f;
    public float angleDrag = 0.1f;
    public float angularVelocityInherit = 0.2f;
    public Color fromColor = Color.white;
    public Color toColor = Color.white;
    protected Cons<Building> hitBuilding;
    protected Cons<Unit> hitUnit;
    private boolean hit;

    public TentacleBulletType(float damage){
        this.damage = damage;
        speed = 0.001f;
        lifetime = 20f;
        pierce = true;
        despawnEffect = Fx.none;
        keepVelocity = false;
        hittable = false;
        absorbable = false;
    }

    @Override
    public float calculateRange(){
        return length / 1.4f;
    }

    @Override
    public float estimateDPS(){
        return damage * 100f / 5f * 3f;
    }

    @Override
    public float continuousDamage(){
        return damage / 5f * 60f;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        int sign = Mathf.signs[Mathf.randomSeed(b.id, 0, 1)];
        float ang = 360f / segments;
        TentacleBulletData data = new TentacleBulletData();
        TentacleNode[] nodes = data.nodes = new TentacleNode[segments];
        if(b.owner instanceof Position){
            data.offsetX = b.x - ((Position)b.owner).getX();
            data.offsetY = b.y - ((Position)b.owner).getY();
        }
        data.length = Damage.findLaserLength(b, length);
        Position last = b;
        for(int i = 0; i < nodes.length; i++){
            float av = (((i + 1f) / nodes.length) + (i == 0 ? 1f - (1f / nodes.length) : 0f)) * (i == 0 ? -1f : 1f);
            TentacleNode node = new TentacleNode();
            node.angle = (ang * i * sign) + b.rotation() + ((sign * 90f) + 180f);
            node.angularVelocity = angleVelocity * av * -sign;
            node.pos.set(last);
            //node.pos.trns(node.angle, (length / segments)).add(last);
            last = node.pos;
            nodes[i] = node;
        }
        b.data = data;
    }

    @Override
    public void draw(Bullet b){
        if(b.data instanceof TentacleBulletData data){
            TentacleNode[] nodes = data.nodes;
            float[] tmp = new float[8];
            float out = 1f - Mathf.clamp(b.fout() * 2f);
            Position last = b;
            float lastRot = nodes[0].angle;
            Draw.color(fromColor, toColor, out);
            int ix = 0;
            for(int sign : Mathf.signs){
                Tmp.v1.trns(lastRot - 90f, (width / 2f) * Mathf.clamp(b.fout() * 2f) * sign).add(last);
                tmp[ix++] = Tmp.v1.x;
                tmp[ix++] = Tmp.v1.y;
            }
            Tmp.v1.trns(lastRot + 180f, width).add(last);
            tmp[ix++] = Tmp.v1.x;
            tmp[ix] = Tmp.v1.y;
            Fill.tri(tmp[0], tmp[1], tmp[2], tmp[3], tmp[4], tmp[5]);
            for(int i = 0; i < nodes.length; i++){
                int idx = 0;
                TentacleNode node = nodes[i];
                float scl = (((float)(nodes.length) - i) / (nodes.length)) * (width / 2f) * Mathf.clamp(b.fout() * 2f);
                float sclB = (((nodes.length) - (i + 1f)) / (nodes.length)) * (width / 2f) * Mathf.clamp(b.fout() * 2f);
                for(int sign : Mathf.signs){
                    Tmp.v1.trns(lastRot - 90f, scl * sign).add(last);
                    tmp[idx++] = Tmp.v1.x;
                    tmp[idx++] = Tmp.v1.y;
                }
                for(int sign : Mathf.signs){
                    Tmp.v1.trns(node.angle - 90f, sclB * -sign).add(node.pos);
                    tmp[idx++] = Tmp.v1.x;
                    tmp[idx++] = Tmp.v1.y;
                }
                last = node.pos;
                lastRot = node.angle;
                Fill.quad(tmp[0], tmp[1], tmp[2], tmp[3], tmp[4], tmp[5], tmp[6], tmp[7]);
            }
            Draw.reset();
        }
    }

    @Override
    public void drawLight(Bullet b){

    }

    @Override
    public void update(Bullet b){
        if(b.data instanceof TentacleBulletData data){
            TentacleNode[] nodes = data.nodes;
            Position last = b;
            int next = 1;
            if(b.owner instanceof Position){
                b.x = ((Position)b.owner).getX() + data.offsetX;
                b.y = ((Position)b.owner).getY() + data.offsetY;
            }
            for(TentacleNode node : nodes){
                node.angle += node.angularVelocity * Time.delta;
                node.pos.trns(node.angle, (length / segments) * b.fin()).add(last);
                if(next < nodes.length){
                    nodes[next].angularVelocity += node.angularVelocity * angularVelocityInherit;
                }
                node.angularVelocity *= 1f - (angleDrag * Time.delta);
                last = node.pos;
                next++;
            }
            if(b.timer(1, 5f)){
                last = b;
                hit = false;
                for(TentacleNode node : nodes){
                    Utils.collideLineRaw(last.getX(), last.getY(), node.pos.x, node.pos.y, 3f, bu -> bu.team != b.team && !hit, un -> un.team != b.team && !hit, (build, direct) -> {
                        if(direct){
                            if(hitBuilding != null) hitBuilding.get(build);
                            build.damage(damage * Mathf.clamp(b.fout() * 2f) * buildingDamageMultiplier);
                        }
                        if(build.block.absorbLasers) hit = true;
                        return build.block.absorbLasers;
                    }, unit -> {
                        if(hitUnit != null) hitUnit.get(unit);
                        unit.damage(damage * Mathf.clamp(b.fout() * 2f));
                        unit.apply(status, statusDuration);
                    }, null, (ex, ey) -> hitEffect.at(ex, ey, node.angle));
                    last = node.pos;
                }
            }
        }
    }

    static class TentacleNode{
        Vec2 pos = new Vec2();
        float angularVelocity = 0f;
        float angle = 0f;
    }

    static class TentacleBulletData{
        TentacleNode[] nodes;
        float offsetX, offsetY, length;
    }
}

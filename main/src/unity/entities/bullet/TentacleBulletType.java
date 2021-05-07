package unity.entities.bullet;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
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
    public float range(){
        return length;
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
        TentacleNode[] nodes = new TentacleNode[segments];
        Position last = b;
        for(int i = 0; i < nodes.length; i++){
            float av = (i + 1f) / nodes.length;
            TentacleNode node = new TentacleNode();
            node.angle = (ang * i * sign) + b.rotation();
            node.angularVelocity = angleVelocity * av * -sign;
            node.pos.set(last);
            //node.pos.trns(node.angle, (length / segments)).add(last);
            last = node.pos;
            nodes[i] = node;
        }
        b.data = nodes;
    }

    @Override
    public void draw(Bullet b){
        if(b.data instanceof TentacleNode[] nodes){
            float[] tmp = new float[8];
            Position last = b;
            float lastRot = b.rotation();
            Draw.color(fromColor, toColor, b.fin());
            int ix = 0;
            for(int sign : Mathf.signs){
                Tmp.v1.trns(b.rotation() - 90f, (width / 2f) * Mathf.clamp(b.fout() * 2f) * sign).add(last);
                tmp[ix++] = Tmp.v1.x;
                tmp[ix++] = Tmp.v1.y;
            }
            Tmp.v1.trns(b.rotation() + 180f, width).add(last);
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
        if(b.data instanceof TentacleNode[] nodes){
            Position last = b;
            int next = 1;
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
                for(TentacleNode node : nodes){
                    Utils.collideLineRawEnemy(b.team, last.getX(), last.getY(), node.pos.x, node.pos.y, build -> {
                        if(hitBuilding != null) hitBuilding.get(build);
                        build.damage(damage * Mathf.clamp(b.fout() * 2f) * buildingDamageMultiplier);
                        return false;
                    }, unit -> {
                        if(hitUnit != null) hitUnit.get(unit);
                        unit.damage(damage * Mathf.clamp(b.fout() * 2f));
                        unit.apply(status, statusDuration);
                    }, hitEffect);
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
}

package unity.entities.comp;

import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;
import unity.entities.effects.*;
import unity.entities.effects.SlowLightningType.*;
import unity.gen.*;
import unity.util.*;

@SuppressWarnings("unused")
@EntityDef(value = {SlowLightningc.class}, serialize = false)
@EntityComponent(base = true)
abstract class SlowLightningComp implements Drawc, Childc{
    static Vec2 tv = new Vec2();
    static boolean collided = false;

    @Import
    float x, y, rotation;
    Team team = Team.derelict;
    Position target;
    Bullet bullet;
    Floatp liveDamage;
    SlowLightningType type;
    Seq<SlowLightningNode> nodes = new Seq<>(SlowLightningNode.class);
    int layer = 0, seed = 1, bulletId = -1;
    float time, distance, timer;
    float lastX, lastY;
    boolean ended = false, passed = false;

    @Override
    public void remove(){
        for(SlowLightningNode n : nodes){
            SlowLightningType.nodes.free(n);
        }
        nodes.clear();
    }

    @Override
    public void add(){
        lastX = x;
        lastY = y;
        if(bullet != null){
            bulletId = bullet.id;
        }

        end(null);
    }

    void end(SlowLightningNode node){
        boolean split = nextBoolean(type.splitChance);
        for(int i = 0; i < (split ? 2 : 1); i++){
            float r = nextRange(split ? type.splitRandSpacing : type.randSpacing);
            float tr = node != null ? node.rotation + node.rotRand : rotation;
            if(target != null){
                float scl = 1f - Mathf.clamp(dst(target) / type.rotationDistance);
                tr = Angles.moveToward(tr, angleTo(target), ((type.maxRotationSpeed - type.minRotationSpeed) * scl) + type.minRotationSpeed);
            }
            float rr = tr + r;

            collided = false;
            float nl = type.nodeLength;
            Vec2 v2 = Tmp.v2.set(node == null ? self() : node);
            Vec2 v = Tmp.v1.trns(rr, Math.min(type.nodeLength, type.range - nl)).add(v2);
            float l = Utils.findLaserLength(v2.x, v2.y, v.x, v.y, tile -> collided |= (tile.team() != team && tile.block() != null && tile.block().absorbLasers));
            if(l < type.nodeTime){
                v.sub(v2).scl(l / type.nodeLength).add(v2);
            }

            SlowLightningNode n = SlowLightningType.nodes.obtain();
            n.main = self();
            n.parent = node;
            n.rotation = rr;
            n.x = v.x;
            n.y = v.y;
            if(node != null){
                n.rotRand = -node.rotRand + (-r + node.rotRand) * nextRand();
                n.layer = node.layer + 1;
                n.dist = node.dist + l;
            }else{
                n.rotRand = -r;
                n.layer = layer + 1;
                n.dist = l;
            }
            n.ended = collided || n.dist >= type.range;
            distance = Math.max(distance, n.dist);
            layer = Math.max(layer, n.layer);
            nodes.add(n);
        }
    }

    @Override
    @MethodPriority(2)
    public void update(){
        if(parent() != null){
            float dx = x - lastX, dy = y - lastY;
            for(SlowLightningNode n : nodes){
                n.move(layer, dx, dy);
            }
        }
        if(bullet != null && bullet.id != bulletId){
            bullet = null;
        }
        if(type.continuous && (timer += Time.delta) >= 5f){
            for(SlowLightningNode n : nodes){
                n.collide();
            }
            timer = 0f;
        }
        for(int i = 0; i < nodes.size; i++){
            nodes.items[i].update();
        }
        if(time >= type.lifetime){
            remove();
        }
        time += Time.delta;
    }

    @Override
    public void draw(){
        float fin = Math.min(type.lifetime - time, type.fadeTime) / type.fadeTime;
        float z = Draw.z();
        Draw.z(Layer.effect);
        Lines.stroke(type.lineWidth * fin);
        for(SlowLightningNode n : nodes){
            n.draw();
        }
        Draw.reset();
        Draw.z(z);
    }

    @Insert(value = "update()", after = false)
    void updateLastPosition(){
        lastX = x;
        lastY = y;
    }

    boolean nextBoolean(float chance){
        boolean b = Mathf.randomSeed(seed, 1f) < chance;
        seed = Mathf.randomSeed(seed, 63, 2147483647);
        return b;
    }

    float nextRange(float range){
        float r = Mathf.randomSeed(seed, -range, range);
        seed = Mathf.randomSeed(seed, 63, 2147483647);
        return r;
    }

    float nextRand(){
        float r = Mathf.randomSeed(seed, 1f);
        seed = Mathf.randomSeed(seed, 63, 2147483647);
        return r;
    }
}

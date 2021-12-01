package unity.entities.comp;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.entities.effects.*;
import unity.gen.*;

@SuppressWarnings("unused")
@EntityDef(value = {SlowLightningc.class}, pooled = true, serialize = false)
@EntityComponent(base = true)
abstract class SlowLightningComp implements Drawc, Childc{
    static Vec2 tv = new Vec2();

    @Import float x, y, rotation;
    Team team = Team.derelict;
    Posc owner;
    SlowLightning root, last;
    SlowLightning[] children;
    Vec2 target;
    Floatp liveDamage;
    SlowLightningType type;
    int layer = 0, seed = 0;
    float rotOffset = 0f, progress, colorProgress, tx, ty, time, distance;
    float lastX, lastY;

    @Override
    public void add(){
        lastX = x;
        lastY = y;
    }

    @Override
    public void remove(){
        if(children != null){
            for(SlowLightning child : children){
                child.remove();
            }
        }
    }

    @Override
    @MethodPriority(1)
    public void update(){
        if(last != null){
            x = last.tx;
            y = last.ty;
        }
        if(isRoot()){
            if(children != null){
                float dx = x - lastX;
                float dy = y - lastY;
                tx += dx;
                ty += dy;

                for(SlowLightning node : children){
                    float scl = 1f - (node.layer / (float)layer);
                    float scl2 = 1f - ((node.layer - 1f) / layer);
                    node.tx += dx * scl;
                    node.ty += dy * scl;
                    node.x += dx * scl2;
                    node.y += dy * scl2;
                }
            }
        }
        if(progress >= 1f) return;
        progress = Math.min(1f, progress + (Time.delta / type.nodeTime));
        if(progress >= 1f) end();
    }

    @Insert(value = "update()", after = false)
    void updateLastPosition(){
        lastX = x;
        lastY = y;
    }

    boolean isRoot(){
        return root == null;
    }

    void end(){
        if(isRoot()){
            layer++;
        }else{
            root.layer++;
        }

        boolean split = nextBoolean();

        children = new SlowLightning[split ? 2 : 1];
        for(int i = 0; i < children.length; i++){
            float offset = nextRange(split ? type.splitRandSpacing : type.randSpacing);
            float ang = (rotation - rotOffset) + offset;
            SlowLightning s = SlowLightning.create();
            s.set(tx, ty);
            s.rotation = ang;
            s.last = self();
            s.root = isRoot() ? self() : root;
            s.target = target;
            s.add();
        }
        target = null;
    }

    float nextRange(float range){
        SlowLightning l = root != null ? root : self();

        float s = Mathf.randomSeedRange(l.seed, range);
        l.seed = Mathf.randomSeed(l.seed, 0, 2147483647);
        return s;
    }

    boolean nextBoolean(){
        SlowLightning l = root != null ? root : self();
        boolean b = Mathf.randomSeed(l.seed, 1f) < type.splitChance;
        l.seed = Mathf.randomSeed(l.seed, 0, 2147483647);
        return b;
    }
}

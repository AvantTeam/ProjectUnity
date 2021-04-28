package unity.entities.comp;

import arc.func.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.gen.LightHoldc.*;

import static mindustry.Vars.*;
import static mindustry.core.World.*;

@EntityDef(value = Lightc.class, serialize = false)
@EntityComponent(base = true)
abstract class LightComp implements Drawc, Rotc{
    transient LightHoldBuildc reflector;
    transient Light parent;
    transient Light child;

    transient float range;
    transient float maxRange;
    transient float strength;

    float endX, endY;

    @Import float x, y, rotation;

    @Override
    public void add(){
        range = maxRange;
        walk();
    }

    @Override
    public void remove(){
        apply(Light::remove);
    }

    protected void computeRange(){
        if(parent != null){
            range = parent.range - parent.dst(this);
        }
        apply(Light::computeRange);
    }

    protected void computeRotation(float before){
        if(reflector == null) return;

        float from = rotation;

        Tmp.v1.trns(before, 1f);
        Tmp.v2.trns(reflector.mirrorAngle(), 1f);
        rotation = Tmp.v1.sub(Tmp.v2.scl(2 * Tmp.v1.dot(Tmp.v2))).angle();

        if(from != rotation){
            apply(Light::remove, false);
            walk();
        }
    }

    protected void apply(Cons<Light> cons){
        apply(cons, true);
    }

    protected void apply(Cons<Light> cons, boolean self){
        if(self) cons.get(self());
        if(child != null) cons.get(child);
    }

    @Override
    public void update() {
        if(parent != null && !parent.isAdded()) parent = null;
        if(child != null && !child.isAdded()) child = null;
        if(reflector != null && !reflector.isAdded()) reflector = null;

        Tmp.v1.trns(rotation, range);
        endX = x + Tmp.v1.x;
        endY = y + Tmp.v1.y;
    }

    @Override
    public void draw(){
        Lines.line(x, y, endX, endY);
    }

    protected void walk(){
        Tmp.v1.trns(rotation, range);

        int[] ref = {-1, -1};
        boolean hit = world.raycast(toTile(x), toTile(y), toTile(x + Tmp.v1.x), toTile(y + Tmp.v1.y), (x, y) -> {
            Building build = world.build(x, y);
            if(build instanceof LightHoldBuildc light){
                boolean accept = light.acceptLight(self());
                if(accept){
                    ref[0] = x;
                    ref[1] = y;
                    return true;
                }
            }

            return build == null ? false : build.tile.solid();
        });

        if(hit){
            Building build = world.build(ref[0], ref[1]);
            if(build instanceof LightHoldBuildc light && light.reflect()){
                reflect(ref[0], ref[1], light);
            }
        }
    }

    public void reflect(float x, float y, LightHoldBuildc reflector){
        Light light = Light.create();
        light.set(x, y);
        light.strength = strength;
        light.reflector = reflector;
        light.range = range - dst(light);
        light.computeRotation(rotation);
        light.add();

        child = light;
    }

    @Override
    @Replace
    public float clipSize(){
        return dst(endX, endY);
    }
}

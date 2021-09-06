package unity.entities.comp;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.QuadTree.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.gen.LightHoldc.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

/** @author GlennFolker */
@SuppressWarnings("unused")
@EntityDef(value = Lightc.class, serialize = false, pooled = true)
@EntityComponent
abstract class LightComp implements Drawc, QuadTreeObject{
    static final float yield = 50f * tilesize;
    static final float width = 1.5f;
    static final float rotationInc = 22.5f;
    static boolean proper = false;

    @Import float x, y;
    @ReadOnly transient volatile float endX, endY;

    @ReadOnly transient volatile float strength = 0f;
    transient volatile float queueStrength = 0f;

    @ReadOnly transient volatile float rotation = 0f;
    transient volatile float queueRotation = 0f;
    transient volatile long queuePosition = 0;

    @ReadOnly transient volatile LightHoldBuildc source;
    transient volatile LightHoldBuildc queueSource;

    transient volatile LightHoldBuildc pointed;
    protected transient final Seq<Light> parents = new Seq<>(2);
    /** Maps child rotation with the actual entity. Value might be null if the child is a merged light */
    protected transient final ObjectMap<Floatf<Light>, Light> children = new ObjectMap<>();

    private static final ObjectMap<Floatf<Light>, Light> tmp = new ObjectMap<>();

    /** Called synchronously before {@link #cast()} is called */
    public void snap(){
        strength = queueStrength + recStrength();
        rotation = queueRotation;
        source = queueSource;

        x = SVec2.x(queuePosition);
        y = SVec2.y(queuePosition);
    }

    /** Called asynchronously */
    public void cast(){
        if((source == null || !source.isValid()) && parentsAny(Seq::isEmpty)){
            lights.queueRemove(self());
        }

        long end = SVec2.add(SVec2.trns(0, rotation, strength * yield), this);
        float
            endXf = SVec2.x(end),
            endYf = SVec2.y(end);

        world.raycastEachWorld(x, y, endXf, endYf, (tx, ty) -> {
            endX = tx * tilesize;
            endY = ty * tilesize;

            var tile = world.tile(tx, ty);
            if(tile == null){
                lights.queuePoint(self(), null);
                return true;
            }

            var build = tile.build;
            if(build instanceof LightHoldBuildc hold){
                if(hold == source || parentsAny(p -> p.contains(l -> hold == l.pointed))) return false;

                if(hold.acceptLight(self())){
                    lights.queuePoint(self(), hold);
                    return true;
                }else if(tile.solid()){
                    lights.queuePoint(self(), null);
                    return true;
                }
            }else if(tile.solid()){
                lights.queuePoint(self(), null);
                return true;
            }

            return false;
        });

        var tile = world.tileWorld(endX, endY);
        if(tile != null){
            children(children -> {
                print(this + "\n" + children);
                synchronized(tmp){
                    tmp.clear();
                }

                for(var e : children.entries()){
                    var key = e.key;
                    float rot = key.get(self());

                    lights.quad(quad -> quad.intersect(tile.worldx() - tilesize / 2f, tile.worldy() - tilesize / 2f, tilesize, tilesize, l -> {
                        print("Intersected light: " + l + ", " + l.rotation());
                        if(e.value != l && Angles.near(rot, l.rotation(), 1f)){
                            if(e.value != null){
                                e.value.detach(self());
                                lights.queueRemove(e.value);
                            }

                            l.parent(self());
                            synchronized(tmp){
                                e.value = l;
                                tmp.put(key, l);
                            }
                        }
                    }));

                    if(e.value == null || !Angles.near(rot, e.value.rotation(), 1f)){
                        if(e.value != null) e.value.detach(self());

                        print("Creating new light", rot, e.value == null ? "null" : e.value.rotation());
                        var l = Light.create();
                        l.set(endX, endY);
                        l.parent(self());

                        synchronized(tmp){
                            tmp.put(key, l);
                        }

                        lights.queueAdd(l);
                    }
                }

                synchronized(tmp){
                    children.putAll(tmp);
                }
            });
        }

        children(children -> {
            for(var e : children.entries()){
                var l = e.value;
                if(l != null && l.isParent(self())){
                    l.queuePosition = SVec2.construct(endX, endY);
                    l.queueRotation = e.key.get(self());
                }
            }
        });
    }

    public float recStrength(){
        return parents.sumf(l -> l.endStrength() / l.children.size);
    }

    @Override
    @Replace
    public float clipSize(){
        return Mathf.dst(x, y, endX, endY) * 2f;
    }

    public float endStrength(){
        return Math.max(strength - Mathf.dst(x, y, endX, endY) / yield, 0f);
    }

    @Override
    public void add(){
        lights.quad.insert(self());
    }

    @Override
    public void remove(){
        clearParents();
        clearChildren();
        lights.quad.remove(self());
    }

    @Override
    public void hitbox(Rect out){
        out.set(x, y, 0f, 0f);
    }

    public void children(Cons<ObjectMap<Floatf<Light>, Light>> cons){
        synchronized(children){
            cons.get(children);
        }
    }

    public void parents(Cons<Seq<Light>> cons){
        synchronized(parents){
            cons.get(parents);
        }
    }

    public boolean parentsAny(Boolf<Seq<Light>> cons){
        synchronized(parents){
            return cons.get(parents);
        }
    }

    public void clearChildren(){
        children(children -> {
            for(var e : children.entries()){
                if(e.value != null){
                    if(e.value.isParent(self())) e.value.remove();
                    e.value.detach(self());
                }
            }

            children.clear();
        });
    }

    public void clearParents(){
        parents(parents -> {
            for(var l : parents){
                l.children(children -> {
                    var key = children.findKey(this, true);
                    if(key != null) children.put(key, null);
                });
            }

            parents.clear();
        });
    }

    public boolean isParent(Light light){
        return parentsAny(parents -> parents.contains(light));
    }

    public void parent(Light light){
        parents(parents -> {
            if(!isParent(light)) parents.add(light);
        });
    }

    public void child(Floatf<Light> child){
        children(children -> children.put(child, null));
    }

    public void detach(Light light){
        parents(parents -> parents.remove(light));
    }

    public float realRotation(){
        return Angles.angle(x, y, endX, endY);
    }

    @Override
    public void draw(){
        float z = Draw.z();
        Draw.z(Layer.bullet);
        Draw.blend(Blending.additive);

        if(proper){
            Lines.stroke(width, Tmp.c1.set(Color.white).a(strength));
            Lines.line(x, y, endX, endY);
        }else{
            float
                stroke = width / 4f,
                rot = realRotation(),
                op = strength - 1f,

                startc = Tmp.c1.set(Color.white).a(strength).toFloatBits(),
                endc = Tmp.c1.set(Color.white).a(endStrength()).toFloatBits();

            if(op > 0f){
                Tmp.v1.trns(rot, op * yield).add(this);
                float
                    x2 = Tmp.v1.x,
                    y2 = Tmp.v1.y;

                float
                    len = Mathf.len(x2 - x, y2 - y),
                    diffx = (x2 - x) / len * stroke,
                    diffy = (y2 - y) / len * stroke * 2f;

                Fill.quad(
                    x - diffx - diffy,
                    y - diffy + diffx,
                    startc,

                    x - diffx + diffy,
                    y - diffy - diffx,
                    startc,

                    x2 + diffx + diffy,
                    y2 + diffy - diffx,
                    startc,

                    x2 + diffx - diffy,
                    y2 + diffy + diffx,
                    startc
                );
            }

            Tmp.v1.trns(rot, Math.max(op, 0f) * yield).add(this);
            Tmp.v2.trns(rot, strength * yield).add(this);

            float
                x = Tmp.v1.x, y = Tmp.v1.y,
                x2 = Tmp.v2.x, y2 = Tmp.v2.y;

            float
                len = Mathf.len(x2 - x, y2 - y),
                diffx = (x2 - x) / len * stroke,
                diffy = (y2 - y) / len * stroke * 2f;

            Fill.quad(
                x - diffx - diffy,
                y - diffy + diffx,
                startc,

                x - diffx + diffy,
                y - diffy - diffx,
                startc,

                x2 + diffx + diffy,
                y2 + diffy - diffx,
                endc,

                x2 + diffx - diffy,
                y2 + diffy + diffx,
                endc
            );
        }

        Draw.blend();
        Draw.z(z);
    }
}

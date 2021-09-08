package unity.entities.comp;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.QuadTree.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.gen.LightHoldc.*;
import unity.util.func.*;

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

    @ReadOnly transient volatile LightHoldBuildc source = null;
    transient volatile LightHoldBuildc queueSource = null;

    private transient volatile boolean valid = false;

    transient volatile LightHoldBuildc pointed;

    /** Maps parent with strength multipliers */
    protected transient final ObjectFloatMap<Light> parents = new ObjectFloatMap<>(2);
    /** Maps child data with the actual entity. Value might be null if the child is a merged light */
    protected transient final ObjectMap<Longf<Light>, Light> children = new ObjectMap<>();

    private static final ObjectMap<Longf<Light>, Light> tmp = new ObjectMap<>();

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
        if((source == null || !source.isValid()) && parentsAny(parents -> parents.size <= 0)){
            lights.queueRemove(self());
        }

        float
            targetX = x + Angles.trnsx(rotation, strength * yield),
            targetY = y + Angles.trnsy(rotation, strength * yield);

        boolean hit = world.raycast(World.toTile(x), World.toTile(y), World.toTile(targetX), World.toTile(targetY), (tx, ty) -> {
            var tile = world.tile(tx, ty);
            if(tile == null){
                lights.queuePoint(self(), null);
                endX = tx * tilesize;
                endY = ty * tilesize;

                return true;
            }

            var build = tile.build;
            if(build instanceof LightHoldBuildc hold){
                if(hold == source || parentsAny(p -> {
                    for(var l : p.keys()){
                        if(hold == l.pointed) return true;
                    }
                    return false;
                })) return false;

                if(hold.acceptLight(self())){
                    lights.queuePoint(self(), hold);
                    endX = tile.worldx();
                    endY = tile.worldy();

                    return true;
                }else if(tile.solid()){
                    lights.queuePoint(self(), null);
                    endX = tile.worldx();
                    endY = tile.worldy();

                    return true;
                }
            }else if(tile.solid()){
                lights.queuePoint(self(), null);
                endX = tile.worldx();
                endY = tile.worldy();

                return true;
            }

            return false;
        });

        if(!hit){
            endX = Mathf.round(targetX / tilesize) * tilesize;
            endY = Mathf.round(targetY / tilesize) * tilesize;
        }

        var tile = world.tileWorld(endX, endY);
        if(tile != null){
            children(children -> {
                synchronized(tmp){
                    tmp.clear();
                }

                for(var e : children.entries()){
                    var key = e.key;
                    long res = key.get(self());

                    float rot = Float2.x(res);
                    float str = Float2.y(res);

                    lights.quad(quad -> quad.intersect(tile.worldx() - tilesize / 2f, tile.worldy() - tilesize / 2f, tilesize, tilesize, l -> {
                        if(e.value != l && Angles.near(rot, l.realRotation(), 1f)){
                            if(e.value != null){
                                e.value.detach(self());
                                lights.queueRemove(e.value);
                            }

                            l.parent(self(), str);
                            synchronized(tmp){
                                e.value = l;
                                tmp.put(key, l);
                            }
                        }
                    }));

                    if(e.value == null || !Angles.near(rot, e.value.rotation(), 1f)){
                        if(e.value != null) e.value.detach(self());

                        var l = Light.create();
                        l.set(endX, endY);
                        l.parent(self(), str);

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

        valid = true;
    }

    public float recStrength(){
        float str = 0f;
        synchronized(parents){
            for(var p : parents.entries()){
                str += p.key.endStrength() * p.value;
            }
        }

        return str;
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
        lights.quad(quad -> quad.insert(self()));
    }

    @Override
    public void remove(){
        clearParents();
        clearChildren();
        lights.quad(quad -> quad.remove(self()));
    }

    @Override
    public void hitbox(Rect out){
        out.set(x, y, 0f, 0f);
    }

    public void children(Cons<ObjectMap<Longf<Light>, Light>> cons){
        synchronized(children){
            cons.get(children);
        }
    }

    public void parents(Cons<ObjectFloatMap<Light>> cons){
        synchronized(parents){
            cons.get(parents);
        }
    }

    public boolean parentsAny(Boolf<ObjectFloatMap<Light>> cons){
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
            for(var l : parents.entries()){
                l.key.children(children -> {
                    var key = children.findKey(this, true);
                    if(key != null) children.put(key, null);
                });
            }

            parents.clear();
        });
    }

    public boolean isParent(Light light){
        return parentsAny(parents -> parents.containsKey(light));
    }

    public void parent(Light light, float mult){
        parents(parents -> {
            if(!isParent(light)) parents.put(light, mult);
        });
    }

    public void child(Longf<Light> child){
        children(children -> children.put(child, null));
    }

    public void detach(Light light){
        parents(parents -> parents.remove(light, 0f));
    }

    public float realRotation(){
        return Angles.angle(x, y, endX, endY);
    }

    @Override
    public void draw(){
        if(!valid) return;

        float z = Draw.z();
        Draw.z(Layer.bullet);
        Draw.blend(Blending.additive);

        float
            stroke = width / 4f,
            rot = realRotation(),
            op = strength - 1f,

            startc = Tmp.c1.set(Color.white).a(Math.max(strength, 0.3f)).toFloatBits(),
            endc = Tmp.c1.set(Color.white).a(Math.max(endStrength(), 0.3f)).toFloatBits();

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

        Draw.blend();
        Draw.z(z);
    }
}

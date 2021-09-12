package unity.entities.comp;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.QuadTree.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.gen.LightHoldc.*;
import unity.util.*;
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

    @Import float x, y;
    @ReadOnly transient volatile float endX, endY;

    @ReadOnly transient volatile float strength = 0f;
    transient volatile float queueStrength = 0f;

    @ReadOnly transient volatile float rotation = 0f;
    transient volatile float queueRotation = 0f;
    transient volatile long queuePosition = 0;

    @ReadOnly transient volatile LightHoldBuildc source = null;
    transient volatile LightHoldBuildc queueSource = null;

    @ReadOnly transient volatile int color = Color.whiteRgba;
    transient volatile int queueColor = SColor.a(Color.whiteRgba, 0f);

    @ReadOnly transient volatile boolean casted = false;
    @ReadOnly transient volatile boolean valid = false;

    transient volatile LightHoldBuildc pointed;

    /** Maps parent with strength multipliers */
    private transient final ObjectFloatMap<Light> parents = new ObjectFloatMap<>(2);
    private transient final ThreadLocal<ObjectFloatMap.Entries<Light>> parentEntries = new ThreadLocal<>(){
        @Override
        protected ObjectFloatMap.Entries<Light> initialValue(){
            return new ObjectFloatMap.Entries<>(parents);
        }
    };

    /** Maps child data with the actual entity. Value might be null if the child is a merged light */
    private transient final ObjectMap<Longf<Light>, AtomicPair<Light, Light>> children = new ObjectMap<>(2);
    private transient final ThreadLocal<Entries<Longf<Light>, AtomicPair<Light, Light>>> childEntries = new ThreadLocal<>(){
        @Override
        protected Entries<Longf<Light>, AtomicPair<Light, Light>> initialValue(){
            return new Entries<>(children);
        }
    };

    private static final Color tmpCol = new Color();

    /** Called synchronously before {@link #cast()} is called */
    public void snap(){
        // Values that are needed to stay as is in async process are snapped here
        strength = queueStrength + recStrength();
        rotation = fixRot(queueRotation);
        source = queueSource;
        color = combinedCol(queueColor);

        x = SVec2.x(queuePosition);
        y = SVec2.y(queuePosition);
    }

    /** Called asynchronously */
    public void cast(){
        clearInvalid();

        // If this doesn't come from a light source and it has no parents, remove
        if((source == null || !source.isValid()) && parentsAny(parents -> parents.size <= 0)){
            queueRemove();
            return;
        }

        float
            targetX = x + Angles.trnsx(rotation, strength * yield),
            targetY = y + Angles.trnsy(rotation, strength * yield);

        boolean hit = world.raycast(World.toTile(x), World.toTile(y), World.toTile(targetX), World.toTile(targetY), (tx, ty) -> {
            var tile = world.tile(tx, ty);
            if(tile == null){ // Out of map bounds, don't waste time
                lights.queuePoint(self(), null);
                endX = tx * tilesize;
                endY = ty * tilesize;

                return true;
            }

            var build = tile.build;
            if(build instanceof LightHoldBuildc hold){
                // Only handle light holders if the holder isn't the source and there are no parents pointing at it
                if(hold == source || parentsAny(p -> {
                    for(var l : p.keys()){
                        if(hold == l.pointed) return true;
                    }
                    return false;
                })) return false;

                // Either stop if the holder accepts this light or the tile is solid
                if(hold.acceptLight(self())){
                    // Insert self to light holder
                    lights.queuePoint(self(), hold);
                    endX = tile.worldx();
                    endY = tile.worldy();

                    return true;
                }else if(tile.solid()){
                    // Stop ray-casting, no light holder is being handled
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

        // Recalculate end position if didn't hit any tile
        if(!hit){
            endX = Mathf.round(targetX / tilesize) * tilesize;
            endY = Mathf.round(targetY / tilesize) * tilesize;
        }

        var tile = world.tileWorld(endX, endY);
        if(tile != null){
            children(children -> {
                // Iterate through planned children:
                // - Check for existing lights in the end position. If there are any light with the preferred amount
                //   of rotation, remove own direct child and set said light's parent as this, but don't refer said
                //   light as this light's children directly
                // - Otherwise pool a new light as child and directly link it
                for(var e : childEntries()){
                    var key = e.key;

                    // The pair's key is direct child, value is indirect child
                    var pair = e.value;
                    long res = key.get(self());

                    // The rotation and strength data of the children are packed in a Float2 struct
                    float rot = Float2.x(res);
                    float str = Float2.y(res);

                    lights.quad(quad -> quad.intersect(tile.worldx() - tilesize / 2f, tile.worldy() - tilesize / 2f, tilesize, tilesize, l -> {
                        // Only accept existing light if:
                        // - It isn't already this child, for obvious reasons
                        // - It isn't this light's parent
                        // - Has the preferred amount of rotation
                        if(l.valid() && pair.key != l && pair.value != l && !isParent(l) && Angles.near(rot, l.rotation(), 1f)){
                            // If already contains a preferred child, move on to the other one
                            if(pair.key != null){
                                pair.key.queueRemove();
                                pair.key = null;
                            }

                            if(pair.value != null) pair.value.detachParent(self());
                            pair.value = l;
                            pair.value.parent(self(), str);
                        }
                    }));

                    // If it was using an indirect child yet it does not meed the criteria anymore, pool a new direct
                    // child light
                    if(pair.key == null && (pair.value == null || !Angles.near(rot, pair.value.rotation(), 1f))){
                        // Dispose indirect child
                        if(pair.value != null){
                            pair.value.detachParent(self());
                            pair.value = null;
                        }

                        Log.info("Creating new light for @: direct: @, indirect: @, indirectRot: @", this, pair.key, pair.value, pair.value == null ? 0f : pair.value.rotation());
                        var l = Light.create();
                        l.set(endX, endY);
                        l.parent(self(), str);
                        l.queueAdd();

                        pair.key = l;
                    }
                }
            });
        }

        children(children -> {
            // Assign position, rotation, and strength values
            for(var e : childEntries()){
                var l = e.value.key;
                if(l != null){
                    l.queuePosition = SVec2.construct(endX, endY);

                    long res = e.key.get(self());
                    float rot = Float2.x(res);
                    float str = Float2.y(res);

                    l.queueRotation = rot;
                    l.parent(self(), str);
                }
            }
        });

        casted = true;
        valid = true;
    }

    public float recStrength(){
        float str = 0f;
        synchronized(parents){
            for(var p : parentEntries()){
                str += p.key.endStrength() * p.value;
            }
        }

        return str;
    }

    public int combinedCol(int baseCol){
        synchronized(tmpCol){
            tmpCol.set(1f, 1f, 1f, 1f);
            parents(parents -> {
                for(var e : parentEntries()){
                    int col = e.key.color();
                    tmpCol.r += SColor.r(col);
                    tmpCol.g += SColor.g(col);
                    tmpCol.b += SColor.b(col);
                }

                int size = parents.size;
                if(size > 0){
                    tmpCol.r /= size;
                    tmpCol.g /= size;
                    tmpCol.b /= size;
                }

                tmpCol.lerp(
                    SColor.r(baseCol), SColor.g(baseCol), SColor.b(baseCol), 1f,
                    SColor.a(baseCol) / Math.min(size + 1f, 2f)
                );
            });

            return tmpCol.rgba();
        }
    }

    @Override
    @Replace
    public float clipSize(){
        return Mathf.dst(x, y, endX, endY) * 3f;
    }

    public float endStrength(){
        return Math.max(strength - Mathf.dst(x, y, endX, endY) / yield, 0f);
    }

    @Override
    public void add(){
        lights.quad(quad -> quad.insert(self()));
    }

    public void queueAdd(){
        lights.queueAdd(self());
    }

    @Override
    public void remove(){
        clearParents();
        clearChildren();
        lights.quad(quad -> quad.remove(self()));
    }

    public void queueRemove(){
        if(!valid) return;

        valid = false;
        lights.queueRemove(self());
    }

    @Override
    public void hitbox(Rect out){
        out.set(x, y, 0f, 0f);
    }

    public void children(Cons<ObjectMap<Longf<Light>, AtomicPair<Light, Light>>> cons){
        synchronized(children){
            cons.get(children);
        }
    }

    public Entries<Longf<Light>, AtomicPair<Light, Light>> childEntries(){
        var e = childEntries.get();
        e.reset();

        return e;
    }

    public void parents(Cons<ObjectFloatMap<Light>> cons){
        synchronized(parents){
            cons.get(parents);
        }
    }

    public ObjectFloatMap.Entries<Light> parentEntries(){
        var e = parentEntries.get();
        e.reset();

        return e;
    }

    public boolean parentsAny(Boolf<ObjectFloatMap<Light>> cons){
        synchronized(parents){
            return cons.get(parents);
        }
    }

    public void clearChildren(){
        children(children -> {
            for(var e : childEntries()){
                var pair = e.value;
                var direct = pair.key;
                var indirect = pair.value;

                if(direct != null){
                    direct.queueRemove();
                    pair.key = null;
                }
                
                if(indirect != null){
                    indirect.detachParent(self());
                    pair.value = null;
                }
            }

            children.clear();
        });
    }

    public void clearParents(){
        parents(parents -> {
            for(var l : parentEntries()){
                l.key.detachChild(self());
            }

            parents.clear();
        });
    }

    public void clearInvalid(){
        parents(parents -> {
            var it = parentEntries();
            while(it.hasNext){
                var l = it.next().key;
                if(l != null && l.casted() && !l.valid()){
                    it.remove();
                }
            }
        });

        children(children -> {
            for(var e : childEntries()){
                var pair = e.value;
                var direct = pair.key;
                var indirect = pair.value;

                if(direct != null && direct.casted() && !direct.valid()) pair.key = null;
                if(indirect != null && indirect.casted() && !indirect.valid()) pair.value = null;
            }
        });
    }

    public boolean isParent(Light light){
        return parentsAny(parents -> parents.containsKey(light));
    }

    public void parent(Light light, float mult){
        parents(parents -> parents.put(light, mult));
    }

    public void child(Longf<Light> child){
        children(children -> children.get(child, AtomicPair::new).reset());
    }

    public void detachChild(Light light){
        children(children -> {
            for(var e : childEntries()){
                var pair = e.value;
                if(pair.key == light) pair.key = null;
                if(pair.value == light) pair.value = null;
            }
        });
    }

    public void detachParent(Light light){
        parents(parents -> parents.remove(light, 0f));
    }

    public float visualRot(){
        return Angles.angle(x, y, endX, endY);
    }

    @Override
    public void draw(){
        if(!valid) return;

        float z = Draw.z();
        Draw.z(Layer.blockOver);
        Draw.blend(Blending.additive);

        // Since vertex color interpolation is always linear, first draw an opaque line assuming the end of it
        // is where strength == 1, then do the gradually fading line later
        float
            stroke = width / 4f,
            rot = visualRot(),
            op = strength - 1f,
            dst2 = dst2(endX, endY),

            startc = Tmp.c1.set(color).a(Mathf.clamp(strength)).toFloatBits(),
            endc = Tmp.c1.set(color).a(Mathf.clamp(endStrength())).toFloatBits();

        if(op > 0f){
            Tmp.v1.trns(rot, op * yield).limit2(dst2).add(this);
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

        Tmp.v1.trns(rot, Math.max(op, 0f) * yield).limit2(dst2).add(this);
        float
            x2 = Tmp.v1.x,
            y2 = Tmp.v1.y,

            len = Mathf.len(endX - x2, endY - y2),
            diffx = (endX - x2) / len * stroke,
            diffy = (endY - y2) / len * stroke * 2f;

        Fill.quad(
            x2 - diffx - diffy,
            y2 - diffy + diffx,
            startc,

            x2 - diffx + diffy,
            y2 - diffy - diffx,
            startc,

            endX + diffx + diffy,
            endY + diffy - diffx,
            endc,

            endX + diffx - diffy,
            endY + diffy + diffx,
            endc
        );

        Draw.blend();
        Draw.z(z);
    }

    public static float fixRot(float rotation){
        return Mathf.mod(Mathf.round(rotation / rotationInc) * rotationInc, 360f);
    }
}

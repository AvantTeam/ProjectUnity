package unity.entities.comp;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.math.geom.QuadTree.*;
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
    static final float width = 2f;
    static final float rotationInc = 22.5f;

    @Import float x, y;
    @ReadOnly transient volatile float endX, endY;

    @ReadOnly transient volatile float strength = 0f;
    transient volatile float queueStrength = 0f;

    @ReadOnly transient volatile float rotation = 0f;
    transient volatile byte queueRotation = 0;
    transient volatile long queuePosition = 0;

    @ReadOnly transient volatile LightHoldBuildc source;
    transient volatile LightHoldBuildc queueSource;

    transient volatile LightHoldBuildc pointed;
    transient final Seq<Light> parents = new Seq<>(2);
    /**
     * Maps child rotation with the actual entity. Key ranges from [-7..7], which is a packed rotation relative to
     * this light's rotation. Value might be null if the child is a merged light
     */
    transient final IntMap<Light> children = new IntMap<>();

    /** Called synchronously before {@link #cast()} is called */
    public void snap(){
        strength = queueStrength + recStrength();
        rotation = unpackRot(queueRotation);
        source = queueSource;

        x = SVec2.x(queuePosition);
        y = SVec2.y(queuePosition);
    }

    /** Called asynchronously */
    public void cast(){
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
                for(var e : children.entries()){
                    float rot = rotation + e.key * rotationInc;

                    lights.quad(quad -> quad.intersect(tile.worldx() - tilesize / 2f, tile.worldy() - tilesize / 2f, tilesize, tilesize, l -> {
                        if(Mathf.equal(rot, l.rotation())){
                            if(e.value != null){
                                e.value.detach(self());
                                lights.queueRemove(e.value);
                            }

                            l.parent(self());
                            e.value = l;
                        }
                    }));

                    if(e.value == null || !Mathf.equal(rot, e.value.rotation())){
                        if(e.value != null) e.value.detach(self());

                        var l = Light.create();
                        l.set(endX, endY);
                        l.parent(self());

                        e.value = l;
                        lights.queueAdd(l);
                    }
                }
            });
        }

        children(children -> {
            for(var e : children.entries()){
                var l = e.value;
                if(l != null && l.isParent(self())){
                    l.queuePosition = SVec2.construct(endX, endY);
                    l.queueRotation = (byte)(queueRotation + e.key);
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
        lights.quad.remove(self());
    }

    @Override
    public void hitbox(Rect out){
        out.set(x, y, 0f, 0f);
    }

    public void clearChildren(){
        children(children -> {
            for(var e : children.entries()){
                if(e.value != null && e.value.isParent(self())){
                    e.value.remove();
                }
            }

            children.clear();
        });
    }

    public void children(Cons<IntMap<Light>> cons){
        synchronized(children){
            cons.get(children);
        }
    }

    public boolean parentsAny(Boolf<Seq<Light>> cons){
        synchronized(parents){
            return cons.get(parents);
        }
    }

    public boolean isParent(Light light){
        synchronized(parents){
            return parents.contains(light);
        }
    }

    public void parent(Light light){
        synchronized(parents){
            if(!isParent(light)) parents.add(light);
        }
    }

    public void detach(Light light){
        synchronized(parents){
            parents.remove(light);
        }
    }

    public float realRotation(){
        return Angles.angle(x, y, endX, endY);
    }

    @Override
    public void draw(){
        float z = Draw.z();
        Draw.z(Layer.bullet);

        Draw.blend(Blending.additive);

        float
            rot = realRotation(),
            endOpaque = Math.max((strength - 1f) * yield, 0f);

        if(endOpaque >= 0f){
            Tmp.v1.trns(rot, endOpaque);
            Tmp.v2.trns(rot - 90f, width);

            float sx = Tmp.v2.x, sy = Tmp.v2.y;
            float color = Color.whiteFloatBits;

            Fill.quad(
                x + sx, y + sy, color,
                x + sx + Tmp.v1.x, y + sy + Tmp.v1.y, color,
                x - sx + Tmp.v1.x, y - sy + Tmp.v1.y, color,
                x - sx, y - sy, color
            );
        }

        Tmp.v1.trns(rot, endOpaque);
        Tmp.v2.trns(rot, endStrength() * yield - endOpaque);
        Tmp.v3.trns(rot - 90f, width);

        float sx = Tmp.v3.x, sy = Tmp.v3.y;
        float color = Color.whiteFloatBits, ecolor = Color.clearFloatBits;

        Fill.quad(
            x + sx + Tmp.v1.x, y + sy + Tmp.v1.y, color,
            x + sx + Tmp.v1.x + Tmp.v2.x, y + sy + Tmp.v1.y + Tmp.v2.y, ecolor,
            x - sx + Tmp.v1.x + Tmp.v2.x, y - sy + Tmp.v1.y + Tmp.v2.y, ecolor,
            x - sx + Tmp.v1.x, y - sy + Tmp.v1.y, color
        );

        Draw.blend();
        Draw.z(z);
    }

    public static byte packRot(float rotation){
        return (byte)Mathf.round(rotation / rotationInc);
    }

    public static float unpackRot(byte rotation){
        return rotation * rotationInc;
    }
}

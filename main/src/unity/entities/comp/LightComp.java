package unity.entities.comp;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.math.geom.QuadTree.*;
import arc.struct.*;
import mindustry.gen.*;
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
    static final float rotationInc = 22.5f;

    @Import float x, y;
    @ReadOnly transient volatile float endX, endY;

    @ReadOnly transient volatile float strength = 0f;
    transient volatile float queueStrength = 0f;

    @ReadOnly transient volatile float rotation = 0f;
    transient volatile byte queueRotation = 0;

    transient final Seq<Light> parents = new Seq<>(2);
    /**
     * Maps child rotation with the actual entity. Key ranges from [-7..7]. Value might be null if the
     * child is a merged light
     */
    transient final IntMap<Light> children = new IntMap<>();

    /** Called synchronously before {@link #cast()} is called */
    public void snap(){
        strength = queueStrength + recStrength();
        rotation = queueRotation * rotationInc;
    }

    /** Called asynchronously */
    public void cast(){
        long end = SVec2.add(SVec2.trns(0, rotation, strength * yield), this);
        float
            endX = SVec2.x(end),
            endY = SVec2.y(end);

        world.raycastEachWorld(x, y, endX, endY, (tx, ty) -> {
            var tile = world.tile(tx, ty);
            if(tile == null){
                this.endX = tx * tilesize;
                this.endY = ty * tilesize;
                lights.queuePoint(self(), null);

                return true;
            }

            var build = tile.build;
            if(build instanceof LightHoldBuildc hold){
                if(hold.acceptLight(self())){
                    this.endX = tx * tilesize;
                    this.endY = ty * tilesize;
                    lights.queuePoint(self(), hold);

                    return true;
                }else if(tile.solid()){
                    this.endX = tx * tilesize;
                    this.endY = ty * tilesize;
                    lights.queuePoint(self(), null);

                    return true;
                }
            }else if(tile.solid()){
                this.endX = tx * tilesize;
                this.endY = ty * tilesize;
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
                if(l.isParent(self())) l.queueRotation = (byte)(queueRotation + e.key);
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
        return Mathf.dst(x, y, endX, endY) / yield;
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

    public void children(Cons<IntMap<Light>> cons){
        synchronized(children){
            cons.get(children);
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
}

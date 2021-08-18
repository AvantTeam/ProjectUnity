package unity.entities.comp;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.gen.LightHoldc.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
@SuppressWarnings("unused")
@EntityDef(value = Lightc.class, serialize = false, pooled = true)
@EntityComponent
abstract class LightComp implements Drawc, Rotc{
    /** A {@link #strength} with value {@code 1f} will make the light be able to travel this far. */
    static final float yield = 50f * tilesize;
    /** A {@link #strength} with value {@code 1f} will make the light be this thick. */
    static final float width = 5f;

    transient int color = Color.whiteRgba;
    /**
     * Never, <b>ever</b>, forget to set this to the building source unless you want this light to
     * endlessly {@link LightHoldBuildc#addSource(Light)} to its own source causing an out of memory
     * error.
     */
    transient volatile LightHoldBuildc source = null;

    private volatile @Nullable Tile target = null;
    private volatile @Nullable LightHoldBuildc before = null;

    /** Decreases by {@code 1f} every {@link #yield}} distance. */
    private transient volatile float strength;
    transient float relX, relY;
    volatile @ReadOnly float endX = 0f, endY = 0f;

    private final Seq<Light> absorbs = new Seq<>();
    volatile @ReadOnly boolean absorbed = false;

    @Import int id;
    @Import float x, y, rotation;

    /** Called asynchronously. */
    public void walk(Seq<Runnable> tasks){
        if(absorbed) return;

        float targetX = x + Angles.trnsx(rotation, strength() * yield);
        float targetY = y + Angles.trnsy(rotation, strength() * yield);

        target = null;
        world.raycastEachWorld(x, y, targetX, targetY, (tx, ty) -> {
            Tile tile = world.tile(tx, ty);
            if(tile == null || tile.build == source) return false;

            if(tile.solid() || (tile.build instanceof LightHoldBuildc hold && hold.acceptLight(self()))){
                target = tile;
                return true;
            }

            return false;
        });

        if(target != null){
            //this light just hit a solid tile
            float dst = dst(target.worldx(), target.worldy());
            float estimateX = x + Angles.trnsx(rotation, dst);
            float estimateY = y + Angles.trnsy(rotation, dst);

            endX = Mathf.round(estimateX / 4f) * 4f;
            endY = Mathf.round(estimateY / 4f) * 4f;

            if(target.build instanceof LightHoldBuildc hold){
                tasks.add(() -> {
                    if(before != hold){
                        if(before != null) before.removeSource(self());
                        if(hold.acceptLight(self())) hold.addSource(self());
                    }

                    before = hold;
                });
            }else{
                tasks.add(() -> {
                    if(before != null) before.removeSource(self());
                    before = null;
                });
            }
        }else{
            //just set the end position to the maximum travel distance
            endX = Mathf.round(targetX / 4f) * 4f;
            endY = Mathf.round(targetY / 4f) * 4f;
        }
    }

    public boolean overlaps(Light other){
        float r1 = realRotation();
        return
            self() != other &&
            (
                Mathf.equal(Angles.angle(x, y, other.x, other.y), r1) || (
                    Mathf.equal(x, other.x) &&
                    Mathf.equal(y, other.y)
                )
            ) &&
            Mathf.equal(r1, other.realRotation());
    }

    /** Merges its strength to the other light. Call synchronously. */
    public void absorb(){
        if(absorbed) return;
        absorbed = true;
    }

    /** Should be called to set {@link #absorbed} to {@code false}. */
    public void release(){
        absorbed = false;
    }

    public void strength(float strength){
        this.strength = strength;
    }

    public float strength(){
        return strength + absorbs.sumf(l -> l == null ? 0f : l.strength());
    }

    @Override
    public void update(){
        Groups.draw.each(
            e ->
                e instanceof Light light &&
                !light.absorbed() &&
                overlaps(light),

            e -> {
                if(
                    isAdded() &&
                    !absorbed &&
                    e instanceof Light light &&
                    light.isAdded() &&
                    !light.absorbed() &&
                    overlaps(light)
                ){
                    absorbs.add(light);
                    light.absorb();
                }
            }
        );

        var it = absorbs.iterator();
        while(it.hasNext()){
            var next = it.next();
            if(overlaps(next)){
                next.release();
                it.remove();
            }
        }
    }

    @Override
    public void remove(){
        absorbs.each(Light::release);
    }

    @Override
    public void draw(){
        if(absorbed) return;

        float rotation = angleTo(endX, endY);
        float s = Mathf.clamp(strength());
        float cs = Mathf.clamp(endStrength());

        float w = s * (width / 2f);
        float w2 = cs * (width / 2f);

        float
            x1 = x + Angles.trnsx(rotation - 90f, w), y1 = y + Angles.trnsy(rotation - 90f, w),
            x2 = x + Angles.trnsx(rotation + 90f, w), y2 = y + Angles.trnsy(rotation + 90f, w),
            x3 = endX + Angles.trnsx(rotation + 90f, w2), y3 = endY + Angles.trnsy(rotation + 90f, w2),
            x4 = endX + Angles.trnsx(rotation - 90f, w2), y4 = endY + Angles.trnsy(rotation - 90f, w2);

        Tmp.c1.set(color);
        float
            c1 = Tmp.c1.a(Math.max(0.5f, s)).toFloatBits(),
            c2 = Tmp.c1.a(Math.max(0.5f, cs)).toFloatBits();

        float z = Draw.z();
        Draw.z(Layer.effect);
        Draw.blend(Blending.additive);

        Fill.quad(x1, y1, c1, x2, y2, c1, x3, y3, c2, x4, y4, c2);

        Draw.z(z);
        Draw.blend();
    }

    @Override
    @Replace
    public float clipSize(){
        return absorbed ? 0f : length() * 2f + 2f * tilesize;
    }

    @Override
    @Replace
    public void set(float x, float y){
        this.x = x + relX;
        this.y = y + relY;
    }

    public float realRotation(){
        return angleTo(endX, endY);
    }

    public float length(){
        return dst(endX, endY);
    }

    public float endStrength(){
        return calcStrength(length());
    }

    public float calcStrength(float endX, float endY){
        return calcStrength(dst(endX, endY));
    }

    public float calcStrength(float dst){
        return strength() - dst / yield;
    }
}

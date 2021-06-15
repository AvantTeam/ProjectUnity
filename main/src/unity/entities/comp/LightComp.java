package unity.entities.comp;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
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

    transient Vec2 vec = new Vec2();
    transient Color color = Color.white;
    /**
     * Never, <b>ever</b>, forget to set this to the building source unless you want this light to
     * endlessly {@link LightHoldBuildc#addSource(Light)} to its own source causing an out of memory
     * error.
     */
    transient volatile LightHoldBuildc source;

    private volatile @Nullable Tile target = null;
    private volatile @Nullable LightHoldBuildc before;

    /** Decreases by {@code 1f} every {@link #yield}} distance. */
    transient float strength;
    transient float relX, relY;
    volatile @ReadOnly float endX, endY;

    @Import float x, y, rotation;

    /** Called asynchronously. */
    public void walk(){
        vec.trns(rotation, strength * yield);

        target = null;
        world.raycastEachWorld(x, y, x + vec.x, y + vec.y, (tx, ty) -> {
            Tile tile = world.tile(tx, ty);
            if(tile == null || tile.build == source) return false;

            if(tile.solid() || tile.build instanceof LightHoldBuildc){
                target = tile;
                return true;
            }

            return false;
        });

        if(target != null){
            //this light just hit a solid tile
            endX = target.worldx();
            endY = target.worldy();

            if(target.build instanceof LightHoldBuildc hold){
                Core.app.post(() -> {
                    if(before != hold){
                        if(before != null) before.removeSource(self());
                        if(hold.acceptLight(self())) hold.addSource(self());
                    }

                    before = hold;
                });
            }else{
                Core.app.post(() -> {
                    if(before != null) before.removeSource(self());
                    before = null;
                });
            }
        }else{
            //just set the end position to the maximum travel distance
            endX = Mathf.round((x + vec.x) / tilesize) * tilesize;
            endY = Mathf.round((y + vec.y) / tilesize) * tilesize;
        }
    }

    @Override
    public void draw(){
        float rotation = angleTo(endX, endY);
        float s = Mathf.clamp(strength);
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
            c1 = Tmp.c1.a(s).toFloatBits(),
            c2 = Tmp.c1.a(cs).toFloatBits();

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
        return dst(endX, endY) * 2f + 2f * tilesize;
    }

    @Override
    @Replace
    public void set(float x, float y){
        this.x = x + relX;
        this.y = y + relY;
    }

    public float endStrength(){
        return calcStrength(dst(endX, endY));
    }

    public float calcStrength(float endX, float endY){
        return calcStrength(dst(endX, endY));
    }

    public float calcStrength(float dst){
        return strength - dst / yield;
    }
}

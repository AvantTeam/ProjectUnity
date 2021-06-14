package unity.entities.comp;

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
    static final float yield = 30f * tilesize;

    transient Vec2 vec = new Vec2();
    transient Color color = Color.white;
    transient LightHoldBuildc source;

    @Nullable private volatile Tile target = null;
    @Nullable @ReadOnly LightHoldBuildc before;
    /** Decreases by {@code 1f} every {@link #yield}}. {@code 1f} will give {@code 3f} in laser width. */
    transient float strength;
    @ReadOnly float endX, endY;

    @Import float x, y, rotation;

    /** Called asynchronously. */
    public synchronized void walk(){
        vec.trns(rotation, strength * yield);

        world.raycastEachWorld(x, y, x + vec.x, y + vec.y, (tx, ty) -> {
            Tile tile = world.tile(tx, ty);
            if(tile == null || tile.build == source) return false;

            if(tile.solid()){
                target = tile;
                return true;
            }

            return false;
        });

        //this light just hit a solid tile
        if(target != null){
            endX = target.getX();
            endY = target.getY();

            if(target.build instanceof LightHoldBuildc hold){
                if(before != hold){
                    if(before != null) before.removeSource(self());
                    if(hold.acceptLight(self())) hold.addSource(self());
                }

                before = hold;
            }else if(before != null){
                before.removeSource(self());
            }
        }else{
            endX = x + vec.x;
            endY = y + vec.y;
        }
    }

    @Override
    public void draw(){
        float dst = dst(endX, endY);
        float w = strength * 3f;
        float cst = calcStrength(dst);
        float dif = strength - cst;
        float w2 = cst * 3f;

        float
            x1 = x + Angles.trnsx(rotation, -w, 0f), y1 = y + Angles.trnsy(rotation, -w, 0f),
            x2 = x + Angles.trnsx(rotation, w, 0f), y2 = y + Angles.trnsy(rotation, w, 0f),
            x3 = x + Angles.trnsx(rotation, w2, dst), y3 = y + Angles.trnsy(rotation, w2, dst),
            x4 = x + Angles.trnsx(rotation, -w2, dst), y4 = y + Angles.trnsy(rotation, -w2, dst);

        Tmp.c1.set(color);
        float
            c1 = Tmp.c1.toFloatBits(),
            c2 = Tmp.c1.mula(strength - dif * 0.25f).toFloatBits(),
            c3 = Tmp.c1.mula(strength - dif * 0.5f).toFloatBits(),
            c4 = Tmp.c1.mula(strength - dif * 0.75f).toFloatBits();

        float z = Draw.z();
        Draw.z(Layer.effect);
        Draw.blend(Blending.additive);

        Fill.quad(x1, y1, c1, x2, y2, c2, x3, y3, c3, x4, y4, c4);
        Draw.z(z);
        Draw.blend();
    }

    @Override
    @Replace
    public float clipSize(){
        return dst(endX, endY) * 2f + 2f * tilesize;
    }

    public float endStrength(){
        return calcStrength(endX, endY);
    }

    public float calcStrength(float endX, float endY){
        return calcStrength(dst(endX, endY));
    }

    public float calcStrength(float dst){
        return strength - dst / yield;
    }
}

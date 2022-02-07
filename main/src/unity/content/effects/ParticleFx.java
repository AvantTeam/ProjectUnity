package unity.content.effects;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import mindustry.entities.*;
import mindustry.graphics.Layer;
import unity.graphics.*;

import static arc.graphics.g2d.Draw.*;

public class ParticleFx{
    public static Effect
    endRegenDisable = new Effect(30f, e -> {
        color(UnityPal.scarColor);
        Fill.square(e.x, e.y, 2.5f * Interp.pow2In.apply(e.fslope()), 45f);
    }),
    dust = new Effect(70, e -> {
            color(e.color);
            Vec2 v = (Vec2)e.data;
            Fill.circle(e.x + e.finpow()*v.x, e.y + e.finpow()*v.y, (7f - e.fin() * 7f)*0.5f);
        }).layer(Layer.debris);
}
package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.graphics.*;

import static arc.graphics.g2d.Draw.*;
import static arc.math.Angles.*;

public class ParticleFx{
    public static Effect

    endRegenDisable = new Effect(30f, e -> {
        color(UnityPal.scarColor);
        Fill.square(e.x, e.y, 2.5f * Interp.pow2In.apply(e.fslope()), 45f);
    }),

    monolithSpark = new Effect(60f, e -> {
        if(!(e.data instanceof Float data)) return;

        randLenVectors(e.id, 2, data, (x, y) -> {
            color(UnityPal.monolith, UnityPal.monolithDark, e.fin());

            float w = 1f + e.fout() * 4f;
            Fill.rect(e.x + x, e.y + y, w, w, 45f);
        });
    }),

    monolithSoul = new Effect(48f, e -> {
        if(!(e.data instanceof Vec2 data)) return;

        blend(Blending.additive);
        color(UnityPal.monolith, UnityPal.monolithDark, Color.black, e.finpow());

        float time = Time.time - e.rotation, vx = data.x * time, vy = data.y * time;
        randLenVectors(e.id, 1, 5f + e.finpowdown() * 8f, (x, y) -> {
            float fin = 1f - e.fin(Interp.pow2In);

            alpha(1f);
            Fill.circle(e.x + x + vx, e.y + y + vy, fin * 2f);

            alpha(0.67f);
            Draw.rect("circle-shadow", e.x + x + vx, e.y + y + vy, fin * 8f, fin * 8f);
        });

        blend();
    }).layer(Layer.flyingUnit - 0.01f);
}

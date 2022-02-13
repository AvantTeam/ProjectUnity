package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.graphics.*;

import static arc.math.Angles.*;
import static arc.graphics.g2d.Lines.*;
import static arc.graphics.g2d.Draw.*;

/** Any effect that use 1 or 2 Position class data. */
public class LineFx{
    public static final Effect

    endPointDefence = new Effect(17f, 300f * 2f, e -> {
        if(!(e.data instanceof Position data)) return;

        for(int i = 0; i < 2; i++){
            float width = (2 - i) * 2.2f * e.fout();
            color(i == 0 ? UnityPal.scarColor : Color.white);
            stroke(width);
            line(e.x, e.y, data.getX(), data.getY(), false);
            Fill.circle(e.x, e.y, width);
            Fill.circle(data.getX(), data.getY(), width);
        }
    }),

    monolithSoulAbsorb = new Effect(32f, e -> {
        if(!(e.data instanceof Position data)) return;

        Tmp.v1
            .trns(Angles.angle(e.x, e.y, data.getX(), data.getY()) - 90f, Mathf.randomSeedRange(e.id, 3f))
            .scl(Interp.pow3Out.apply(e.fslope()));
        Tmp.v2.trns(Mathf.randomSeed(e.id + 1, 360f), e.fin(Interp.pow4Out));
        Tmp.v3.set(data).sub(e.x, e.y).scl(e.fin(Interp.pow4In))
            .add(Tmp.v2).add(Tmp.v1).add(e.x, e.y);

        float fin = 0.3f + e.fin() * 1.4f;

        blend(Blending.additive);
        color(Color.black, UnityPal.monolithDark, e.fin());

        alpha(1f);
        Fill.circle(Tmp.v3.x, Tmp.v3.y, fin);

        alpha(0.67f);
        Draw.rect("circle-shadow", Tmp.v3.x, Tmp.v3.y, fin + 6f, fin + 6f);

        blend();
    }).layer(Layer.flyingUnitLow),

    monolithSoulTransfer = new Effect(32f, e -> {
        if(!(e.data instanceof Position data)) return;

        e.scaled(25f, i -> {
            Tmp.v2.set(data).sub(e.x, e.y).scl(i.fin(Interp.pow2In)).add(e.x, e.y);

            color(UnityPal.monolithDark, UnityPal.monolithLight, UnityPal.monolith, i.fin());
            randLenVectors(e.id, 13, Interp.pow3Out.apply(i.fslope()), 360f, 0f, 6f, (x, y) ->
                Fill.circle(Tmp.v2.x + x, Tmp.v2.y + y, 1.5f + i.fslope() * 2.7f)
            );
        });

        Tmp.v1.set(data).sub(e.x, e.y).scl(e.fin(Interp.pow2In)).add(e.x, e.y);
        float size = e.fin(Interp.pow10Out) * e.foutpowdown();

        color(UnityPal.monolithDark);
        Fill.circle(Tmp.v1.x, Tmp.v1.y, size * 4.8f);

        color(UnityPal.monolithLight);
        for(int i = 0; i < 4; i++){
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, size * 6.4f, size * 27f, e.rotation + 90f * i + e.finpow() * 45f * Mathf.sign(e.id % 2 == 0));
        }
    });
}

package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.graphics.*;

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
        Draw.rect("circle-shadow", Tmp.v3.x, Tmp.v3.y, fin + 7.5f, fin + 7.5f);

        blend();
    }).layer(Layer.flyingUnitLow);
}

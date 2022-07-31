package unity.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.entities.*;
import mindustry.graphics.*;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;

public class YoungchaFx{
    public static final Effect dust = new Effect(70, e -> {
        color(e.color);
        Vec2 v = (Vec2)e.data;
        var radius = Math.min(v.len(), 7f);
        Fill.circle(e.x + e.finpow() * v.x, e.y + e.finpow() * v.y, e.fout() * radius);
    }).layer(Layer.debris),

    smokePoof = new Effect(30, e -> {
        Draw.color(Color.white);
        Angles.randLenVectors(e.id, 6, 4.0F + 30.0F * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 3.0F);
            Fill.circle(e.x + x / 2.0F, e.y + y / 2.0F, e.fout());
        });
    }).layer(Layer.blockOver - 1),

    steamSlow = new Effect(200, e -> {
        Draw.color(Color.white);
        Draw.alpha(Mathf.sqrt(e.fslope()));
        float ef = e.finpow() * 10f;
        Angles.randLenVectors(e.id, 1, 4.0F + 10.0F * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x + ef, e.y + y + ef, e.fout() * 8.0F);
            Fill.circle(e.x + x / 2.0F + ef, e.y + y / 2.0F + ef, e.fout() * 4.0F);
        });
        Draw.alpha(1);
    }).layer(Layer.blockOver - 1),

    weldSpark = new Effect(12, e -> {
        Draw.color(Color.white, Pal.turretHeat, e.fin());
        Lines.stroke(e.fout() * 0.6f + 0.6f);

        Angles.randLenVectors(e.id, 3, 15 * e.finpow(), e.rotation, 3, (x, y) ->
        Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 5 + 0.5f)
        );
    }),

    tonkCannon = new Effect(35f, e -> {
        color(Pal.accent);
        for(int sus : Mathf.signs){
            Drawf.tri(e.x, e.y, 8 * e.fout(Interp.pow3Out), 80, e.rotation + 20 * sus);
            Drawf.tri(e.x, e.y, 4 * e.fout(Interp.pow3Out), 30, e.rotation + 60 * sus);
        }
    }),
    tonkCannonSmoke = new Effect(45f, e -> {
        color(Pal.lighterOrange, Color.lightGray, Color.gray, e.fin());
        randLenVectors(e.id, 14, 0f + 55f * e.finpow(), e.rotation, 25f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout(Interp.pow5Out) * 4.5f);
        });
    });
}

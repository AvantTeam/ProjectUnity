package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.graphics.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.circle;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;

public class HitFx{
    public static Effect electronHit = new Effect(12f, e -> {
        color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());
        stroke(e.fout() * 3f);
        Lines.circle(e.x, e.y, e.fin() * 90f);

        randLenVectors(e.id, 7, e.finpow() * 45f, (x, y) -> {
            float a = Mathf.angle(x, y);
            Fill.poly(e.x + x, e.y + y, 3, e.fout() * 4f, e.fin() * 120f + e.rotation + a);
        });
    }),

    protonHit = new Effect(20f, e -> {
        color(Pal.lancerLaser, Color.valueOf("4787ff00"), e.fin());
        stroke(e.fout() * 4f);
        Lines.circle(e.x, e.y, e.fin() * 150f);

        randLenVectors(e.id, 12, e.finpow() * 64f, (x, y) -> {
            float a = Mathf.angle(x, y);
            Fill.poly(e.x + x, e.y + y, 3, e.fout() * 6f, e.fin() * 135f + e.rotation + a);
        });
    }),

    neutronHit = new Effect(28f, e -> {
        color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());

        randLenVectors(e.id, 7, e.finpow() * 50f, (x, y) -> {
            float a = Mathf.angle(x, y);
            Fill.poly(e.x + x, e.y + y, 3, e.fout() * 5f, e.fin() * 120f + e.rotation + a);
        });
    }),

    wBosonDecayHitEffect = new Effect(13f, e -> {
        color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());
        stroke(0.5f + e.fout());

        randLenVectors(e.id, 17, e.finpow() * 20f, (x, y) -> {
            float a = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, a, e.fout() * 8f);
        });
    }),

    LightHitLarge = new Effect(15f, e -> {
        color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());
        stroke(0.5f + e.fout());

        randLenVectors(e.id, 17, e.finpow() * 50f, (x, y) -> {
            float a = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, a, e.fout() * 8f);
        });

        stroke(0.5f + e.fout() * 1.2f);
        Lines.circle(e.x, e.y, e.finpow() * 30f);
    }),

    orbHit = new Effect(12f, e -> {
        color(Pal.surge);
        stroke(e.fout() * 1.5f);
        randLenVectors(e.id, 8, e.finpow() * 17f, e.rotation, 360f, (x, y) -> {
            float ang = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, ang, e.fout() * 4f + 1f);
        });
    }),

    plasmaTriangleHit = new Effect(30f, e -> {
        color(Pal.surge);

        stroke(e.fout() * 2.8f);
        circle(e.x, e.y, e.fin() * 60);
    }),

    scarHitSmall = new Effect(14f, e -> {
        color(Color.white, UnityPal.scarColor, e.fin());
        e.scaled(7f, s -> {
            stroke(0.5f + s.fout());
            circle(e.x, e.y, s.fin() * 5f);
        });
        stroke(0.5f + e.fout());
        randLenVectors(e.id, 5, e.fin() * 15f, (x, y) -> lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3f + 1f));
    }),

    scarRailHit = new Effect(18f, e -> {
        for(int i = 0; i < 2; i++){
            int sign = Mathf.signs[i];
            color(UnityPal.scarColor);
            Drawf.tri(e.x, e.y, 10f * e.fout(), 60f, e.rotation + 90f + 90f * sign);
            color(Color.white);
            Drawf.tri(e.x, e.y, Math.max(10 * e.fout() - 4f, 0f), 56f, e.rotation + 90f + 90f * sign);
        }
    }),

    coloredHitSmall = new Effect(14f, e -> {
        color(Color.white, e.color, e.fin());
        e.scaled(7f, s -> {
            stroke(0.5f + s.fout());
            circle(e.x, e.y, s.fin() * 5f);
        });
        stroke(0.5f + e.fout());
        randLenVectors(e.id, 5, e.fin() * 15f, (x, y) -> lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3f + 1f));
    }),

    coloredHitLarge = new Effect(21f, e -> {
        color(Color.white, e.color, e.fin());
        e.scaled(8f, s -> {
            stroke(0.5f + s.fout());
            circle(e.x, e.y, s.fin() * 11f);
        });
        stroke(0.5f + e.fout());
        randLenVectors(e.id, 6, e.fin() * 35f, e.rotation + 180f, 45f, (x, y) -> lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 7f + 1f));
    }),

    eclipseHit = new Effect(15f, e -> {
        color(Color.valueOf("c2ebff"), Color.valueOf("68c0ff"), e.fin());

        randLenVectors(e.id, 4, e.finpow() * 28f, (x, y) -> Fill.poly(e.x + x, e.y + y, 4, 3f + e.fout() * 9f, 0f));

        color(Color.white, Pal.lancerLaser, e.fin());

        stroke(1.5f * e.fout());

        randLenVectors(e.id * 2L, 7, e.finpow() * 42f, (x, y) -> {
            float a = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, a, e.fout() * 8f + 1.5f);
        });
    }),

    tenmeikiriTipHit = new Effect(27f, e ->
    Angles.randLenVectors(e.id, 8, 90f * e.fin(), e.rotation, 80f, (x, y) -> {
        float angle = Mathf.angle(x, y);
        color(UnityPal.scarColor, UnityPal.endColor, e.fin());
        Lines.stroke(1.5f);
        Lines.lineAngleCenter(e.x + x, e.y + y, angle, e.fslope() * 13f);
    })),

    hitMonolithLaser = new Effect(8, e -> {
        color(UnityPal.monolithLight, UnityPal.monolithDark, e.finpow());
        stroke(0.2f + e.fout() * 1.3f);
        Lines.circle(e.x, e.y, e.fin() * 5f);
    }),

    hitAdvanceFlame = new Effect(15f, e -> {
        color(UnityPal.advance, UnityPal.advanceDark, e.fin());

        Angles.randLenVectors(e.id, 2, e.finpow() * 17f, e.rotation, 60f, (x, y) -> {
            Fill.poly(e.x + x, e.y + y, 6, 3f + e.fout() * 3f, e.rotation);
        });
    }),

    branchFragHit = new Effect(8f, e -> {
        color(Color.white, Pal.lancerLaser, e.fin());

        stroke(0.5f + e.fout());
        Lines.circle(e.x, e.y, e.fin() * 5f);

        stroke(e.fout());
        Lines.circle(e.x, e.y, e.fin() * 6f);
    });
}

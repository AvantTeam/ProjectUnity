package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.graphics.*;
import unity.util.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.circle;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;

public class HitFx{
    public static Effect

    electronHit = new Effect(12f, e -> {
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

    philinopsisEmpZap = new Effect(50f, 100f, e -> {
        float rad = 68f;

        e.scaled(7f, b -> {
            color(Pal.heal, b.fout());
            UnityDrawf.arcFill(e.x, e.y, rad, 36f, e.rotation);
        });

        color(Pal.heal);
        stroke(e.fout() * 3f);
        UnityDrawf.arcLine(e.x, e.y, rad, 36f, e.rotation);

        Drawf.tri(e.x + Angles.trnsx(e.rotation, rad), e.y + Angles.trnsy(e.rotation, rad), 6f, 50f * e.fout(), e.rotation);
    }),

    philinopsisEmpHit = new Effect(50f, 100f, e -> {
        float rad = 124f;

        e.scaled(7f, b -> {
            color(Pal.heal, b.fout());
            Fill.circle(e.x, e.y, rad);
        });

        color(Pal.heal);
        stroke(e.fout() * 3f);
        Lines.circle(e.x, e.y, rad);

        int points = 10;
        float offset = Mathf.randomSeed(e.id, 360f);
        for(int i = 0; i < points; i++){
            float angle = i* 360f / points + offset;
            //for(int s : Mathf.zeroOne){
            Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 6f, 50f * e.fout(), angle/* + s*180f*/);
            //}
        }

        Fill.circle(e.x, e.y, 12f * e.fout());
        color();
        Fill.circle(e.x, e.y, 6f * e.fout());
        Drawf.light(e.x, e.y, rad * 1.6f, Pal.heal, e.fout());
    }),

    lightHitLarge = new Effect(15f, e -> {
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

    empHit = new Effect(50f, 100f, e -> {
        float rad = 70f;
        e.scaled(7f, b -> {
            color(Pal.heal, b.fout());
            Fill.circle(e.x, e.y, rad);
        });

        color(Pal.heal);
        stroke(e.fout() * 3f);
        Lines.circle(e.x, e.y, rad);

        int points = 10;
        float offset = Mathf.randomSeed(e.id, 360f);
        for(int i = 0; i < points; i++){
            float angle = i* 360f / points + offset;
            Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 6f, 50f * e.fout(), angle);
        }

        Fill.circle(e.x, e.y, 12f * e.fout());
        color();
        Fill.circle(e.x, e.y, 6f * e.fout());
        Drawf.light(e.x, e.y, rad * 1.6f, Pal.heal, e.fout());
    }),

    plagueLargeHit = new Effect(80f, e -> {
        float fOffset = 0.1f;
        float fOffsetA = 0.05f;
        Rand r = Utils.seedr;
        r.setSeed(e.id * 99999L);

        for(int i = 0; i < 9; i++){
            float f = r.nextFloat() * fOffset;
            float fin = Mathf.curve(e.fin(), f, f + 1 - fOffset);
            float ex = Interp.pow3Out.apply(fin) * 35f * r.nextFloat();
            Vec2 v = Tmp.v1.trns(r.random(360f), ex);

            Draw.color(Color.gray, Color.darkGray, fin);
            Fill.circle(e.x + v.x, e.y + v.y, 9f * Mathf.curve(1f - fin, 0f, 0.7f));
            Fill.circle(e.x + v.x * 0.5f, e.y + v.y * 0.5f, 5f * Mathf.curve(1f - fin, 0f, 0.7f));
        }

        e.scaled(40f, s -> {
            Draw.color(UnityPal.plague);
            for(int i = 0; i < 6; i++){
                float f = r.nextFloat() * fOffsetA;
                float fin = Mathf.curve(s.fin(), f, f + 1 - fOffsetA);
                float ifin = Interp.pow3Out.apply(fin);
                float scl = r.nextFloat();
                float ex = ifin * 50f * scl;
                float slope = Interp.pow3Out.apply(Mathf.slope(ifin));
                Vec2 v = Tmp.v1.trns(r.random(360f), ex, Mathf.sin(ifin * Mathf.PI, 1f / r.random(1f, 5f), slope * scl * r.random(6f, 11f))).add(e.x, e.y);
                Fill.circle(v.x, v.y, 6f * s.fout());
            }
        });

        e.scaled(15f, s -> {
            Draw.color(UnityPal.plague);
            Lines.stroke(2f * s.fout());
            Lines.circle(e.x, e.y, 50f * s.finpow());
        });
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

    voidHit = new Effect(20f, e -> {
        color(Color.black);
        randLenVectors(e.id, 7, e.finpow() * 15f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 5f);
            Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout() * 3f);
        });
    }),

    voidHitBig = new Effect(30f, e -> {
        color(Color.black);
        e.scaled(e.lifetime / 2, s -> {
            for(int i = 0; i < 3; i++){
                float f = Mathf.lerp(10, 5, i / 2f);
                Draw.alpha(Mathf.lerp(0.45f, 1, (i / 2f) * (i / 2f)));

                Drawf.tri(e.x, e.y,  f * 1.22f * s.fout(Interp.pow5Out), 1 + 7 * f * s.fin(Interp.pow5Out), e.rotation);
                Drawf.tri(e.x, e.y,  f * 1.22f * s.fout(Interp.pow5Out), 3 * f * s.fout(Interp.pow5Out), e.rotation - 180f);
            }
        });

        if(e.fin() > 0.45f){
            float l2 = Mathf.curve(e.fin(), 0.45f, 1);
            Angles.randLenVectors(e.id, 20, 35 * Interp.pow2Out.apply(l2), e.rotation, 45, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 4 * Interp.pow2Out.apply(1 - l2));
            });
        }
    }),

    endHitRedSmall = new Effect(15f, e -> {
        e.scaled(e.lifetime / 2f, s -> {
            color(UnityPal.scarColor, UnityPal.endColor, s.fin());
            Lines.stroke(2f * s.fout());
            Lines.circle(e.x, e.y, 10f * s.fin());
        });

        color(UnityPal.endColor, UnityPal.scarColor, e.fin());

        Angles.randLenVectors(e.id, 7, e.fin(Interp.pow3Out) * 20f, (x, y) -> {
            float ang = Mathf.angle(x, y);
            Lines.stroke(e.fout());
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout(Interp.pow5In) * 12f);
        });
    }),

    endHitRedSmoke = new Effect(25f, e -> {
        color(UnityPal.scarColor, Color.darkGray, Color.gray, e.fin());
        Angles.randLenVectors(e.id * 451L, 7, e.fin(Interp.pow3Out) * 35f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 4f);
        });

        e.scaled(15f, s -> {
            color(UnityPal.endColor, UnityPal.scarColor, e.fin());
            Angles.randLenVectors(e.id, 7, e.fin(Interp.pow3Out) * 20f, (x, y) -> {
                float ang = Mathf.angle(x, y);
                Lines.stroke(s.fout());
                Lines.lineAngle(e.x + x, e.y + y, ang, s.fout(Interp.pow5In) * 12f);
            });
        });
    }),

    endHitRedBig = new Effect(15f, e -> {
        color(UnityPal.endColor, UnityPal.scarColor, e.fin());
        Angles.randLenVectors(e.id, 7, e.fin(Interp.pow3Out) * 45f, e.rotation, 45, (x, y) -> {
            float ang = Mathf.angle(x, y);
            Lines.stroke(e.fout() * 2);
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout(Interp.pow3In) * 24f);
        });
    }),

    endHitRail = new Effect(25f, e -> {
        e.scaled(15f, s -> {
            color(UnityPal.endColor, UnityPal.scarColor, e.fin());
            Angles.randLenVectors(e.id, 7, s.fin(Interp.pow3Out) * 45f, e.rotation, 47f, (x, y) -> {
                float ang = Mathf.angle(x, y);
                Lines.stroke(s.fout() * 2);
                Lines.lineAngle(e.x + x, e.y + y, ang, s.fout(Interp.pow3In) * 24f);
            });
        });

        float scl = 0.3f;
        int spikes = Mathf.randomSeed(e.id * 13L, 3, 5);
        color(UnityPal.scarColor);
        for(int i = 0; i < spikes; i++){
            float fin = Mathf.curve(e.fin(), (i / (float)spikes) * scl, (((i + 1f) / spikes) * scl) + (1f - scl));
            float fin2 = Mathf.curve(fin, 0f, 0.3f);
            float fout = 1f - fin;
            float angle = Mathf.randomSeed(e.id * 53L + i * 31L, -25f, 25f) + e.rotation;
            Drawf.tri(e.x, e.y, fout * 20f, fin2 * (80f + Mathf.randomSeed((e.id + i) * 73L, 40f)), angle);
            Drawf.tri(e.x, e.y, fout * 20f, fin2 * 20f, angle + 180f);
        }
    }),

    // perhape
    endFlash = new Effect(7f, e -> {
        Tmp.c1.set(UnityPal.scarColor).a(e.fout());
        color(UnityPal.scarColor);

        blend(Blending.additive);
        for(int i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 5 * e.fout(Interp.pow3In), 40f * e.fout(), 90f * i);
        }

        Fill.light(e.x, e.y, 15, 16, Tmp.c1, Color.clear);

        blend();
    }).layer(Layer.effect + 1),

    hitMonolithLaser = new Effect(8, e -> {
        color(UnityPal.monolithLight, UnityPal.monolithDark, e.finpow());
        stroke(0.2f + e.fout() * 1.3f);
        Lines.circle(e.x, e.y, e.fin() * 5f);
    }),

    hitAdvanceFlame = new Effect(15f, e -> {
        color(UnityPal.advance, UnityPal.advanceDark, e.fin());

        Angles.randLenVectors(e.id, 2, e.finpow() * 17f, e.rotation, 60f, (x, y) ->
        Fill.poly(e.x + x, e.y + y, 6, 3f + e.fout() * 3f, e.rotation));
    }),

    branchFragHit = new Effect(8f, e -> {
        color(Color.white, Pal.lancerLaser, e.fin());

        stroke(0.5f + e.fout());
        Lines.circle(e.x, e.y, e.fin() * 5f);

        stroke(e.fout());
        Lines.circle(e.x, e.y, e.fin() * 6f);
    }),

    hitExplosionMassive = new Effect(70f, 370f, e -> {
        e.scaled(17f, s -> {
            Draw.color(Color.white, Color.lightGray, e.fin());
            stroke(s.fout() + 0.5f);
            Lines.circle(e.x, e.y, e.fin() * 185f);
        });

        color(Color.gray);

        randLenVectors(e.id, 12, 5f + 135f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 22f + 0.5f);
            Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout() * 9f);
        });

        color(Pal.lighterOrange, Pal.lightOrange, Color.gray, e.fin());
        stroke(1.5f * e.fout());

        randLenVectors(e.id + 1, 14, 1f + 160f * e.finpow(), (x, y) ->
        lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f));
    }),

    monoHitSmall = new Effect(14, e -> {
        color(UnityPal.monolith);
        e.scaled(7f, s -> {
            Lines.stroke(s.fout());
            Lines.square(s.x, s.y, 10f * s.fin(), 45f);
        });
        color(UnityPal.monolithLight);
        Angles.randLenVectors(e.id, 5, e.fin() * 15f, (x, y) -> {
            Fill.square(e.x + x, e.y + y, 2f * e.fout());
        });
    }),

    monoHitBig = new Effect(13f, e -> {
        color(UnityPal.monolithLight);
        Angles.randLenVectors(e.id, 10, e.fin() * 20f, (x, y) -> {
            Fill.square(e.x + x, e.y + y, 5f * e.fout());
        });

        Tmp.c1.set(UnityPal.monolith).a(e.fout(Interp.pow3In));

        z(Layer.effect+1f);
        blend(Blending.additive);
        Fill.light(e.x, e.y, 4, 25f * e.fin(Interp.pow5Out), Color.clear, Tmp.c1);
        blend();
    });
}
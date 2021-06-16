package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.graphics.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;

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
    });
}

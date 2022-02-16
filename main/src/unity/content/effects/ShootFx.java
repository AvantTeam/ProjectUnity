package unity.content.effects;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.content.UnityFx;
import unity.graphics.*;
import unity.util.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.circle;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;

public class ShootFx{
    public static Effect

    laserChargeShoot = new Effect(21f, e -> {
        color(e.color, Color.white, e.fout());

        for(int i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 4f * e.fout(), 29f, e.rotation + 90f * i + e.finpow() * 112f);
        }
    }),

    laserChargeShootShort = new Effect(15f, e -> {
        color(e.color, Color.white, e.fout());
        stroke(2f * e.fout());
        Lines.square(e.x, e.y, 0.1f + 20f * e.finpow(), 45f);
    }),

    laserFractalShoot = new Effect(40f, e -> {
        color(Tmp.c1.set(e.color).lerp(Color.white, e.fout()));

        for(int i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 4f * e.fout(), 29f, e.rotation + 90f * i + e.finpow() * 112f);
        }

        for (var h = 1; h <= 5; h++) for(var i = 0; i < 2; i++){
            float m = i == 0 ? 1 : 0.5f;
            float w = 8 * e.fout() * m;
            float length = 8 * 3;
            Vec2 fxPos = new Vec2(e.x, e.y);
            float rot = Angles.angle(fxPos.x, fxPos.y, e.x, e.y) + h * 180f + Mathf.randomSeedRange(e.id, 360) + e.finpow() * 262;

            fxPos.trns(rot, length - 4);
            length *= new Interp.PowOut(25).apply(e.fout());

            Drawf.tri(fxPos.x + e.x, fxPos.y + e.y, w, length * m, rot + 180);
            Drawf.tri(fxPos.x + e.x, fxPos.y + e.y, w , length / 3f * m, rot);

            Draw.alpha(0.5f);
            Drawf.tri(e.x, e.y, w, length * m,  rot + 360);
            Drawf.tri(e.x, e.y, w, length/3 * m, rot);
            Fill.square(fxPos.x + e.x, fxPos.y + e.y, 3 * e.fout(), rot + 45);
        }
    }),

    laserBreakthroughShoot = new Effect(40f, e -> {
        color(e.color);

        stroke(e.fout() * 2.5f);
        Lines.circle(e.x, e.y, e.finpow() * 100f);

        stroke(e.fout() * 5f);
        Lines.circle(e.x, e.y, e.fin() * 100f);

        color(e.color, Color.white, e.fout());

        randLenVectors(e.id, 20, 80f * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 5f));


        for(int i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 9f * e.fout(), 170f, e.rotation + Mathf.randomSeed(e.id, 360f) + 90f * i + e.finpow() * (0.5f - Mathf.randomSeed(e.id)) * 150f);
        }
    }),

    shootSmallBlaze = new Effect(22f, e -> {
        color(Pal.lightFlame, Pal.darkFlame, Pal.gray, e.fin());
        randLenVectors(e.id, 16, e.finpow() * 60f, e.rotation, 18f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.85f + e.fout() * 3.5f));
    }),

    shootPyraBlaze = new Effect(32f, e -> {
        color(Pal.lightPyraFlame, Pal.darkPyraFlame, Pal.gray, e.fin());
        randLenVectors(e.id, 16, e.finpow() * 60f, e.rotation, 18f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.85f + e.fout() * 3.5f));
    }),

    orbShoot = new Effect(21f, e -> {
        color(Pal.surge);
        for(int i = 0; i < 2; i++){
            int l = Mathf.signs[i];
            Drawf.tri(e.x, e.y, 4f * e.fout(), 29f, e.rotation + 67 * l);
        }
    }),

    shrapnelShoot = new Effect(13f, e -> {
        color(Color.white, Pal.bulletYellow, Pal.lightOrange, e.fin());
        stroke(e.fout() * 1.2f + 0.5f);

        randLenVectors(e.id, 10, 30f * e.finpow(), e.rotation, 50f, (x, y) -> lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 5f + 2f));
    }),

    plagueShootSmokeLarge = new Effect(35f, e -> {
        Draw.color(UnityPal.plagueDark, Color.gray, Color.darkGray, e.fin());
        for(int i = 0; i < 12; i++){
            float r = (Utils.randomTriangularSeed((e.id * 191L) + i) * 90f) + e.rotation;
            Vec2 v = Tmp.v1.trns(r, e.finpow() * 20f * Mathf.randomSeed(e.id * 81L + i)).add(e.x, e.y);
            Fill.circle(v.x, v.y, 5f * Mathf.curve(e.fout(), 0f, 0.7f) * Mathf.randomSeed(e.id * 9L + i, 0.8f, 1.1f));
        }
        e.scaled(20f, s -> {
            Lines.stroke(1.5f);
            Draw.color(UnityPal.plague, Color.white, s.fin());
            Angles.randLenVectors(e.id, 5, 25f * s.finpow() + 0.1f, e.rotation, 20f, (x, y) -> {
                float r = Mathf.angle(x, y);
                Lines.lineAngle(e.x + x, e.y + y, r, 5f * s.fout());
            });
        });
    }),

    scarRailShoot = new Effect(24f, e -> {
        e.scaled(10f, b -> {
            color(Color.white, Color.lightGray, b.fin());
            stroke(b.fout() * 3f + 0.2f);
            circle(b.x, b.y, b.fin() * 50f);
        });
        for(int i = 0; i < 2; i++){
            int sign = Mathf.signs[i];
            color(UnityPal.scarColor);
            Drawf.tri(e.x, e.y, 13 * e.fout(), 85f, e.rotation + 90f * sign);
            color(Color.white);
            Drawf.tri(e.x, e.y, Math.max(13 * e.fout() - 4f, 0f), 81f, e.rotation + 90f * sign);
        }
    }),

    endGameShoot = new Effect(45f, 820f * 2f, e -> {
        float curve = Mathf.curve(e.fin(), 0f, 0.2f) * 820f;
        float curveB = Mathf.curve(e.fin(), 0f, 0.7f);

        color(Color.red, Color.valueOf("ff000000"), curveB);
        blend(Blending.additive);
        Fill.poly(e.x, e.y, Lines.circleVertices(curve), curve);
        blend();
    }).layer(Layer.effect + 0.99f),

    monumentShoot = new Effect(48f, e -> {
        color(UnityPal.monolithLight);
        Drawf.tri(e.x, e.y, 10f * e.fout(), 175f - (20f * e.fin()), e.rotation);

        for(int i = 0; i < 2; i++){
            Drawf.tri(e.x, e.y, 10f * e.fout(), 50f, e.rotation + (45f + (e.fin(Interp.pow3Out) * 30f)) * Mathf.signs[i]);
        }

        randLenVectors(e.id, 15, e.fin(Interp.pow2Out) * 80f, e.rotation, 20f, (x, y) ->
        Fill.square(e.x + x, e.y + y, 3f * e.fout()));

        Fill.square(e.x, e.y, 5f * e.fout(Interp.pow3Out), e.rotation + 45f);
        color();
        Fill.square(e.x, e.y, 2f * e.fout(Interp.pow3Out), e.rotation + 45f);

        e.scaled(15f, s -> {
            z(Layer.effect + 1f);
            blend(Blending.additive);
            Tmp.c1.set(UnityPal.monolithLight).a(s.fout(Interp.pow5In));

            Fill.light(s.x, s.y, 4, 40f * s.fin(Interp.pow5Out), Color.clear, Tmp.c1);
            blend();
        });
    }),

    tendenceShoot = new Effect(32f, e -> {
        TextureRegion reg = Core.atlas.find("unity-monolith-chain");
        Utils.q1.set(Vec3.Z, e.rotation + 90f).mul(Utils.q2.set(Vec3.X, 75f));
        float t = e.finpow(), w = reg.width * scl * 0.4f * t, h = reg.height * scl * 0.4f * t, rad = 9f + t * 8f;

        alpha(e.foutpowdown());
        UnityDrawf.panningCircle(reg,
            e.x, e.y, w, h,
            rad, 360f, e.fin(Interp.pow2Out) * 90f * Mathf.sign(e.id % 2 == 0) + e.id * 30f,
            Utils.q1, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
        );

        color(Color.black, UnityPal.monolithDark, 0.5f);
        alpha(e.foutpowdown());

        blend(Blending.additive);
        UnityDrawf.panningCircle(Core.atlas.find("circle-mid"),
            e.x, e.y, w + 6f, h + 6f,
            rad, 360f, 0f,
            Utils.q1, true, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
        );

        blend();
    }).layer(Layer.flyingUnit),

    coloredPlasmaShoot = new Effect(25f, e -> {
        color(Color.white, e.color, e.fin());
        randLenVectors(e.id, 13, e.finpow() * 20f, e.rotation, 23f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 5f);
            Fill.circle(e.x + x / 1.2f, e.y + y / 1.2f, e.fout() * 3f);
        });
    }),

    sapPlasmaShoot = new Effect(25f, e -> {
        color(Color.white, Pal.sapBullet, e.fin());
        randLenVectors(e.id, 13, e.finpow() * 20f, e.rotation, 23f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 5f);
            Fill.circle(e.x + x / 1.2f, e.y + y / 1.2f, e.fout() * 3f);
        });
    }),

    blueTriangleShoot = new Effect(23f, e -> {
        color(Pal.lancerLaser);

        Fill.poly(e.x, e.y, 3, e.fout() * 24f, e.rotation);
        Fill.circle(e.x, e.y, e.fout() * 11f);

        color(Color.white);
        Fill.circle(e.x, e.y, e.fout() * 9f);
    }),

    voidShoot = new Effect(20f, e -> {
        color(Color.black);
        randLenVectors(e.id, 14, e.finpow() * 20f, e.rotation, 20f * Mathf.curve(e.fin(), 0f, 0.2f), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 5f);
            Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout() * 3f);
        });
    });
}

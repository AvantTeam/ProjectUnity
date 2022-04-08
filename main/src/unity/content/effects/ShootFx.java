package unity.content.effects;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.entities.effects.*;
import unity.graphics.*;
import unity.graphics.MultiTrail.*;
import unity.util.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.circle;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static mindustry.Vars.*;

public class ShootFx{
    private static final Color tmpCol = new Color();

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

        for(int h = 1; h <= 5; h++){
            //float rot = h * 180f + Mathf.randomSeedRange(e.id, 360) + e.finpow() * 262;
            float mul = h % 2;
            float rm = 1 + mul * 0.5f;
            float rot = 90 + (1 - e.finpow()) * Mathf.randomSeed(e.id + (long)(mul * 2f), 210 * rm, 360 * rm);
            for(int i = 0; i < 2; i++){
                float m = i == 0 ? 1 : 0.5f;
                float w = 8 * e.fout() * m;
                float length = 8 * 3 / (2 - mul);
                Vec2 fxPos = Tmp.v1.trns(rot, length - 4);
                length *= Utils.pow25Out.apply(e.fout());

                Drawf.tri(fxPos.x + e.x, fxPos.y + e.y, w, length * m, rot + 180);
                Drawf.tri(fxPos.x + e.x, fxPos.y + e.y, w , length / 3f * m, rot);

                Draw.alpha(0.5f);
                Drawf.tri(e.x, e.y, w, length * m,  rot + 360);
                Drawf.tri(e.x, e.y, w, length/3 * m, rot);
                Fill.square(fxPos.x + e.x, fxPos.y + e.y, 3 * e.fout(), rot + 45);
            }
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

    soulConcentrateShoot = new Effect(60f, e -> {
        int id = e.id;
        for(int sign : Mathf.signs){
            float r = e.foutpow() * 2f;

            color(UnityPal.monolithGreen, UnityPal.monolithGreenDark, e.finpowdown());
            for(int rsign : Mathf.signs){
                randLenVectors(id++, 2, e.finpow() * 20f, e.rotation + sign * 90f, 30f, (x, y) ->
                    Fill.rect(e.x + x, e.y + y, r, r, e.foutpow() * 135f * rsign)
                );
            }
        }

        float r = e.fout(Interp.pow5Out) * 2.4f;

        color(UnityPal.monolithGreenLight, UnityPal.monolithGreen, e.fin(Interp.pow5In));
        for(int rsign : Mathf.signs){
            randLenVectors(id++, 3, e.fin(Interp.pow5Out) * 32f, e.rotation, 45f, (x, y) ->
                Fill.rect(e.x + x, e.y + y, r, r, e.foutpow() * 180f * rsign)
            );
        }
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

        color(Color.black, UnityPal.monolithDark, 0.67f);
        alpha(e.foutpowdown());

        blend(Blending.additive);
        UnityDrawf.panningCircle(Core.atlas.find("unity-line-shade"),
            e.x, e.y, w + 6f, h + 6f,
            rad, 360f, 0f,
            Utils.q1, true, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
        );

        blend();
    }).layer(Layer.flyingUnit),

    pedestalShootAdd = new CustomStateEffect(() -> {
        class State extends EffectState{
            @Override
            public void remove(){
                if(data instanceof TrailHold[] data) for(TrailHold trail : data) Fx.trailFade.at(x, y, trail.width, UnityPal.monolithLight, trail.trail.copy());
                super.remove();
            }
        } return Pools.obtain(State.class, State::new);
    }, 24f, e -> {
        if(!(e.data instanceof TexturedTrail[] data)) return;

        float initAngle = Mathf.randomSeed(e.id, 360f);
        for(int i = 0; i < data.length; i++){
            TexturedTrail trail = data[i];
            if(!state.isPaused()){
                Tmp.v1
                    .trns(initAngle + 360f / data.length * i + Time.time * 6f, 4f + e.foutpowdown() * 12f)
                    .add(e.x, e.y);

                trail.update(Tmp.v1.x, Tmp.v1.y, e.fin());
            }

            tmpCol.set(UnityPal.monolith).lerp(UnityPal.monolithLight, e.finpowdown());
            trail.drawCap(tmpCol, 1.4f);
            trail.draw(tmpCol, 1.4f);
        }

        color(UnityPal.monolithDark, UnityPal.monolith, e.fin());
        randLenVectors(e.id + 1, 3, e.foutpow() * 8f, 360f, 0f, 4f, (x, y) ->
            Fill.circle(e.x + x, e.y + y, 0.4f + e.fin() * 1.6f)
        );
    }){
        @Override
        protected EffectState inst(float x, float y, float rotation, Color color, Object data){
            TexturedTrail[] trails = new TexturedTrail[5];
            for(int i = 0; i < trails.length; i++){
                trails[i] = new TexturedTrail(Core.atlas.find("unity-phantasmal-trail"), 16){{
                    shrink = 0f;
                    fadeAlpha = 1f;
                    blend = Blending.additive;
                }};
            }

            EffectState state = super.inst(x, y, rotation, color, data);
            state.data = trails;
            return state;
        }
    }.followParent(true).rotWithParent(true),

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

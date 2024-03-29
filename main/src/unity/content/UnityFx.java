package unity.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import unity.entities.*;
import unity.entities.abilities.BaseAbility.*;
import unity.entities.bullet.energy.*;
import unity.entities.bullet.energy.EphemeronBulletType.*;
import unity.entities.bullet.energy.SingularityBulletType.*;
import unity.entities.bullet.laser.*;
import unity.gen.*;
import unity.graphics.*;
import unity.type.*;
import unity.util.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static mindustry.Vars.tilesize;
import static unity.content.UnityBullets.*;
import static unity.graphics.UnityDrawf.spark;

//deprecate the deprecation ^-^
//** @deprecated These fields will eventually all be interpreted in the classes in {@link unity.content.effects} package. */
public class UnityFx{
    private static int integer;
    private static final Rand rand = new Rand();

    public static final Effect
        //@formatter:off
    fixedTrailFade = new Effect(400f, e -> {
        if(!(e.data instanceof FixedTrail trail)) return;
        //lifetime is how many frames it takes to fade out the trail
        e.lifetime = trail.length * 1.4f;

        trail.shorten();
        trail.drawCap(e.color, e.rotation);
        trail.draw(e.color, e.rotation);
    }),

    //shamelessly stolen from BetaMindy
    sparkle = new Effect(55f, e -> {
        color(e.color);
        integer = 0;
        Angles.randLenVectors(e.id, e.id % 3 + 1, 8f, (x, y) -> {
            integer++;
            spark(e.x+x, e.y+y, e.fout()*2.5f, 0.5f+e.fout(), e.id * integer);
        });
    }),

    expGain = new Effect(75f, 400f, e -> {
        if(!(e.data instanceof Position pos)) return;

        float fin = Mathf.curve(e.fin(), 0, Mathf.randomSeed(e.id, 0.25f, 1f));
        if(fin >= 1) return;

        float a = angle(e.x, e.y, pos.getX(), pos.getY()) - 90;
        float d = Mathf.dst(e.x, e.y, pos.getX(), pos.getY());
        float fslope = fin * (1f - fin) * 4f;
        float sfin = Interp.pow2In.apply(fin);
        float spread = d / 4f;
        Tmp.v1.trns(a, Mathf.randomSeed(e.id * 2L, -spread, spread) * fslope, d * sfin);
        Tmp.v1.add(e.x, e.y);

        color(UnityPal.exp, Color.white, 0.1f + 0.1f * Mathf.sin(Time.time * 0.03f + e.id * 3f));
        Fill.circle(Tmp.v1.x, Tmp.v1.y, 1.5f);
        stroke(0.5f);
        for(int i = 0; i < 4; i++) Drawf.tri(Tmp.v1.x, Tmp.v1.y, 4f, 4 + 1.5f * Mathf.sin(Time.time * 0.12f + e.id * 4f), i * 90f + Mathf.sin(Time.time * 0.04f + e.id * 5f) * 28f);
    }),

    expDump = new Effect(75f, 400f, e -> {
        if(!(e.data instanceof Position pos)) return;

        float fin = Mathf.curve(e.fin(), 0, Mathf.randomSeed(e.id, 0.25f, 1f));
        if(fin >= 1) return;

        float a = angle(e.x, e.y, pos.getX(), pos.getY()) - 90;
        float d = Mathf.dst(e.x, e.y, pos.getX(), pos.getY());
        float fslope = fin * (1f - fin) * 4f;
        float sfin = Interp.pow2In.apply(fin);
        float spread = d / 4f;
        Tmp.v1.trns(a, Mathf.randomSeed(e.id * 2L, -spread, spread) * fslope, d * sfin);
        Tmp.v1.add(e.x, e.y);

        color(UnityPal.exp, Color.white, 0.1f + 0.1f * Mathf.sin(Time.time * 0.03f + e.id * 3f));
        Fill.circle(Tmp.v1.x, Tmp.v1.y, 1.5f);
        stroke(0.5f);
        for(int i = 0; i < 4; i++) Drawf.tri(Tmp.v1.x, Tmp.v1.y, 4f, 4 + 1.5f * Mathf.cos(Time.time * 0.12f + e.id * 4f), i * 90f + Mathf.sin(Time.time * 0.04f + e.id * 5f) * 28f);
    }),

    expPoof = new Effect(60f, e -> {
        color(Pal.accent, UnityPal.exp, e.fin());
        integer = 0;
        randLenVectors(e.id, 9, 1f + 30f * e.finpow(), (x, y) -> {
            integer++;
            Fill.circle(e.x + x, e.y + y, 1.7f * e.fout());
            spark(e.x + x, e.y + y, 5f, (5 + 1.5f * Mathf.sin(Time.time * 0.12f + integer * 4f)) * e.fout(), e.finpow() * 90f + integer * 69f);
        });
    }),

    expShineRegion = new Effect(25f, e -> {
        color();
        Tmp.c1.set(Pal.accent).lerp(UnityPal.exp, e.fin());
        mixcol(Tmp.c1, 1f);
        alpha(1f - e.fin() * e.fin());

        if(e.data instanceof TextureRegion region){
            Draw.rect(region, e.x, e.y, e.rotation);
        }
    }),

    orbDespawn = new Effect(15f, e -> {
        color(UnityPal.exp);
        stroke(e.fout() * 1.2f + 0.01f);
        Lines.circle(e.x, e.y, 4f * e.finpow());
    }),

    expLaser = new Effect(15f, e -> {
        if(e.data instanceof Building b && !b.dead){
            Tmp.v2.set(b);
            Tmp.v1.set(Tmp.v2).sub(e.x, e.y).nor().scl(tilesize / 2f);
            Tmp.v2.sub(Tmp.v1);
            Tmp.v1.add(e.x, e.y);
            Drawf.laser(null, Core.atlas.find("unity-exp-laser"), Core.atlas.find("unity-exp-laser-end"), Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, 0.4f * e.fout());
        }
    }),

    placeShine = new Effect(30f, e -> {
        color(e.color);
        stroke(e.fout());
        square(e.x, e.y, e.rotation / 2f + e.fin() * 3f);
        spark(e.x, e.y, 25f, 15f * e.fout(), e.finpow() * 90f);
    }),

    laserCharge = new Effect(38f, e -> {
        color(e.color);
        randLenVectors(e.id, e.id % 3 + 1, 1f + 20f * e.fout(), e.rotation, 120f, (x, y) ->
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3f + 1f)
        );
    }),

    laserChargeShort = new Effect(18f, e -> {
        color(e.color);
        randLenVectors(e.id, 1, 1f + 20f * e.fout(), e.rotation, 120f, (x, y) ->
            Fill.square(e.x + x, e.y + y, e.fslope() * 1.5f + 0.1f, 45f)
        );
    }),

    laserFractalCharge = new Effect(120f, e -> {
        float radius = 10 * 8;
        float[] p = {0, 0};

        Angles.randLenVectors(e.id, 3, radius/2 + Interp.pow3Out.apply(1 - e.fout(0.5f)) * radius * 1.25f, (x, y) -> {
            e.scaled(60, ee -> {
                ee.scaled(30, e1 ->{
                    p[0] = Mathf.lerp(x, 0, e1.fin(Interp.pow2));
                    p[1] = Mathf.lerp(y, 0, e1.fin(Interp.pow2));
                });

                Lines.stroke(ee.fout(0.5f), Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f).a(ee.fout(0.5f)));
                Lines.line(e.x+x, e.y+y, e.x+p[0], e.y+p[1]);
            });
        });
    }),

    laserFractalChargeBegin = new Effect(90f, e -> {
        int[] r = {9, 10, 11, 12};

        e.scaled(60, ee -> r[0] *= ee.fin());
        e.scaled(40, ee -> r[1] *= ee.fin());
        e.scaled(40, ee -> r[2] *= ee.fin());
        e.scaled(60, ee -> r[3] *= ee.fin());

        Draw.color(UnityPal.lancerSap3.cpy().a(0.1f+0.55f * e.fslope()));
        Lines.arc(e.x, e.y, r[0], 0.6f, Time.time*8-60);
        Lines.arc(e.x, e.y, r[1], 0.6f, Time.time*5);
        Draw.color(Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f+0.5f*Mathf.sin(16*e.fin())).a(0.25f+0.8f * e.fslope()));
        Lines.arc(e.x, e.y, r[2], 0.4f, Time.time*-6+121);
        Lines.arc(e.x, e.y, r[3], 0.4f, Time.time*-4+91);
    }),

    laserChargeBegin = new Effect(60f, e -> {
        color(e.color);
        Fill.square(e.x, e.y, e.fin() * 3f, 45f);

        color();
        Fill.square(e.x, e.y, e.fin() * 2f, 45f);
    }),

    freezeEffect = new Effect(30f, e -> {
        color(Color.white, e.color, e.fin());
        stroke(e.fout() * 2);
        Lines.poly(e.x, e.y, 6, 4f + e.rotation * 1.5f * e.finpow(), Mathf.randomSeed(e.id) * 360f);
        color();
        integer = 0;
        randLenVectors(e.id, 5, e.rotation * 1.6f * e.fin() + 16f, e.fin() * 33f, 360f, (x, y) -> {
            UnityDrawf.snowFlake(e.x + x, e.y + y, e.finpow() * 60f, Mathf.randomSeed(e.id + (long)integer) * 2 + 2);
            integer++;
        });
        randLenVectors(e.id + 1, 3, e.rotation * 2.1f * e.fin() + 7f, e.fin() * -19f, 360f, (x, y) -> {
            UnityDrawf.snowFlake(e.x + x, e.y + y, e.finpow() * 60f, Mathf.randomSeed(e.id + (long)integer) * 2 + 2);
            integer++;
        });
    }),

    giantSplash = new Effect(30f, e -> {
        color(Color.white, e.color, e.fin());
        stroke(2f * e.fout());
        circle(e.x, e.y, e.finpow() * 20f);
        integer = 0;
        randLenVectors(e.id, 11, 4f + 40f * e.finpow(), (x, y) -> {
            integer++;
            Fill.circle(e.x + x, e.y + y, e.fslope() * Mathf.randomSeed(e.id + integer, 5f, 9f) + 0.1f);
        });
    }),

    hotSteam = new Effect(150f, e -> {
        color(e.color, e.fout() * 0.9f);
        integer = 0;
        randLenVectors(e.id, 2, 10f + 20f * e.fin(), (x, y) -> {
            integer++;
            Fill.circle(e.x + x, e.y + y, e.fin() * Mathf.randomSeed(e.id + integer, 15f, 19f) + 0.1f);
        });
    }).layer(Layer.flyingUnit + 1f),

    iceSheet = new Effect(540f, e -> {
        color(Color.white, e.color, 0.3f);
        integer = 0;
        float fin2 = Mathf.clamp(e.fin() * 5f);
        randLenVectors(e.id, 1, 16f + 2f * fin2, (x, y) -> {
            integer++;
            Fill.poly(e.x + x, e.y + y, 6, fin2 * Mathf.randomSeed(e.id + integer, 6f, 13f) * Mathf.clamp(9f * e.fout()) + 0.1f);
        });
    }).layer(Layer.debris - 1.1f),

    shootFlake = new Effect(21f, e -> {
        color(e.color, Color.white, e.fout());

        for(int i = 0; i < 6; i++){
            Drawf.tri(e.x, e.y, 3f * e.fout(), 12f, e.rotation + Mathf.randomSeed(e.id, 360f) + 60f * i);
        }
    }),

    plasmaedEffect = new Effect(50f, e -> {
        color(Liquids.cryofluid.color, Color.white.cpy().mul(0.25f, 0.25f, 1f, e.fout()), e.fout() / 6f + Mathf.randomSeedRange(e.id, 0.1f));

        Fill.square(e.x, e.y, e.fslope() * 2f, 45f);
    }),

    laserBreakthroughChargeBegin = new Effect(100f, 100f, e -> {
        color(Pal.lancerLaser);
        stroke(e.fin() * 3f);

        Lines.circle(e.x, e.y, 4f + e.fout() * 120f);
        Fill.circle(e.x, e.y, e.fin() * 23.5f);

        randLenVectors(e.id, 20, 50f * e.fout(), (x, y) ->
            Fill.circle(e.x + x, e.y + y, e.fin() * 6f)
        );

        color();
        Fill.circle(e.x, e.y, e.fin() * 13);
    }),

    laserBreakthroughChargeBegin2 = new Effect(100f, 100f, e -> {
        color(UnityPal.exp);
        stroke(e.fin() * 3f);

        Lines.circle(e.x, e.y, 4f + e.fout() * 120f);
        Fill.circle(e.x, e.y, e.fin() * 23.5f);

        randLenVectors(e.id, 20, 50f * e.fout(), (x, y) ->
                Fill.circle(e.x + x, e.y + y, e.fin() * 6f)
        );

        color();
        Fill.circle(e.x, e.y, e.fin() * 13);
    }),

    craft = new Effect(10, e -> {
        color(Pal.accent, Color.gray, e.fin());
        stroke(1);
        spikes(e.x, e.y, e.fin() * 4, 1.5f, 6);
    }),

    denseCraft = new Effect(10, e -> {
        color(UnityPal.dense, Color.gray, e.fin());
        stroke(1);
        spikes(e.x, e.y, e.finpow() * 4.5f, 1f, 6);
    }),

    diriumCraft = new Effect(10, e -> {
        color(Color.white, UnityPal.dirium, e.fin());
        stroke(1);
        spikes(e.x, e.y, e.fin() * 4, 1.5f, 6);
    }),

    longSmoke = new Effect(80f, e -> {
        color(Color.gray, Color.clear, e.fin());
        randLenVectors(e.id, 2, 4 + e.fin() * 4, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.2f + e.fin() * 4);
        });
    }).layer(Layer.flyingUnit - 1f),

    blockMelt = new Effect(400f, e -> {
        color(Color.coral, Color.orange, Mathf.absin(9f, 1f));
        integer = 0;
        float f = Mathf.clamp(e.finpow() * 5f);
        randLenVectors(e.id, 15, 2 + f * f * 16f, (x, y) -> {
            integer++;
            Fill.circle(e.x + x, e.y + y, 0.01f + e.fout() * Mathf.randomSeed(e.id + integer, 2f, 6f));
        });
    }),

    absorb = new Effect(12, e -> {
        color(e.color);
        stroke(2f * e.fout());
        Lines.circle(e.x, e.y, 5f * e.fout());
    }),

    deflect = new Effect(12, e -> {
        color(Color.white, e.color, e.fin());
        stroke(2f * e.fout());
        randLenVectors(e.id, 4, 0.1f + 8f * e.fout(), e.rotation, 60f, (x, y) ->
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3f + 1f)
        );
    }),

    forceShrink = new Effect(20, e -> {
        color(e.color, e.fout());
        if(Vars.renderer.animateShields){
            Fill.poly(e.x, e.y, circleVertices(e.rotation * e.fout()), e.rotation * e.fout());
        }else{
            stroke(1.5f);
            alpha(0.09f);
            Fill.circle(e.x, e.y, e.rotation * e.fout());
            alpha(1f);
            Lines.circle(e.x, e.y,e.rotation * e.fout());
        }
    }).layer(Layer.shields),

    shieldBreak = new Effect(40, e -> {
        color(e.color);
        stroke(3f * e.fout());
        Lines.circle(e.x, e.y, e.rotation + e.fin());
    }).followParent(true),

    craftingEffect = new Effect(67f, 35f, e -> {
        float value = Mathf.randomSeed(e.id);

        Tmp.v1.trns(value * 360f + ((value + 4f) * e.fin() * 80f), (Mathf.randomSeed(e.id * 126L) + 1f) * 34f * (1f - e.finpow()));

        color(UnityPal.laserOrange);
        Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fslope() * 3f, 45f);
        color();
    }),

    whirl = new Effect(65f, e -> {
        for(int i = 0; i < 2; i++){
            int h = i * 2;
            float r1 = Interp.exp5In.apply((Mathf.randomSeedRange(e.id + h, 1f) + 1f) / 2f);
            float r2 = (Mathf.randomSeedRange(e.id * 2L + h, 360) + 360f) / 2f;
            float r3 = (Mathf.randomSeedRange(e.id * 4L + h, 5) + 5f) / 2f;
            float a = r2 + ((180f + r3) * e.fin());

            Tmp.v1.trns(a, r1 * 70f * e.fout());

            color(Pal.lancerLaser);
            stroke(e.fout() + 0.25f);
            lineAngle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, a + 270f + 15f, e.fout() * 8f);
        }
    }),

    lightHexagonTrail = new Effect(55f, e -> {
        color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());

        Fill.poly(e.x, e.y, 6, e.rotation * e.fout(), e.rotation);

        color();
    }),

    wBosonEffect = new Effect(24f, e -> {
        color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());
        stroke(1.25f);
        lineAngle(e.x, e.y, e.rotation, e.fout() * 4f);
    }),

    wBosonEffectLong = new Effect(47f, e -> {
        color(Pal.lancerLaser, UnityPal.lightEffect, e.fin());
        stroke(1.25f);
        lineAngle(e.x, e.y, e.rotation, e.fout() * 7f);
    }),

    ephemeronLaser = new Effect(19f, 100f, e -> {
        if(e.data instanceof EphemeronEffectData d && d.b != null && d.b.type instanceof EphemeronPairBulletType && d.b.isAdded()){
            stroke(3.6f * e.fout(), e.color);
            line(d.b.x, d.b.y, d.x, d.y, false);
        }
    }),

    singularityDespawn = new Effect(12f, e -> {
        float[] scales = {8.6f, 7f, 5.5f, 4.3f, 4.1f, 3.9f};
        Color[] colors = new Color[]{Color.valueOf("4787ff80"), Pal.lancerLaser, Color.white, Pal.lancerLaser, UnityPal.lightEffect, Color.black    };

        for(int i = 0; i < colors.length; i++){
            color(colors[i]);
            Fill.circle(e.x + Mathf.range(1), e.y + Mathf.range(1), e.fout() * 5f * scales[i]);
        }
    }),

    singularityAttraction = new Effect(23f, 180f, e -> {
        if(e.data instanceof SingularityAbsorbEffectData d){
            float interp = e.fin(Interp.pow3In);
            float size = 1f - e.fin(Interp.pow5In);
            float rot = e.rotation * 90f;
            float lerpx = Mathf.lerp(d.x, e.x, interp);
            float lerpy = Mathf.lerp(d.y, e.y, interp);

            rect(d.region, lerpx, lerpy, d.region.width * scl * size, d.region.height * scl * size, (e.fin() * Mathf.randomSeedRange(e.id, 32f)) + rot);
        }
    }).layer(Layer.effect + 0.02f),

    orbTrail = new Effect(43f, e -> {
        Tmp.v1.trns(Mathf.randomSeed(e.id) * 360f, Mathf.randomSeed(e.id * 341L) * 12f * e.fin());

        Drawf.light(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 4.7f * e.fout() + 3f, Pal.surge, 0.6f);

        color(Pal.surge);
        Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout() * 2.7f);
    }).layer(Layer.bullet - 0.01f),

    orbCharge = new Effect(38f, e -> {
        color(Pal.surge);
        randLenVectors(e.id, 2, 1f + 20f * e.fout(), e.rotation, 120f, (x, y) -> lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3f + 1f));
    }),

    orbChargeBegin = new Effect(71f, e -> {
        color(Pal.surge);
        Fill.circle(e.x, e.y, e.fin() * 3f);

        color();
        Fill.circle(e.x, e.y, e.fin() * 2f);
    }),

    currentCharge = new Effect(32f, e -> {
        color(Pal.surge, Color.white, e.fin());
        randLenVectors(e.id, 8, 420f + Mathf.random(24f, 28f) * e.fout(), e.rotation, 4f, (x, y) -> {
            stroke(0.3f + e.fout() * 2f);
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 14f + 0.5f);
        });

        stroke(e.fin() * 1.5f);
        circle(e.x, e.y, e.fout() * 60f);
    }),

    currentChargeBegin = new Effect(260f, e -> {
        color(Pal.surge);
        Fill.circle(e.x, e.y, e.fin() * 7f);

        color();
        Fill.circle(e.x, e.y, e.fin() * 3f);
    }),

    plasmaFragAppear = new Effect(12f, e -> {
        color(Color.white);
        Drawf.tri(e.x, e.y, e.fin() * 12f, e.fin() * 13f, e.rotation);
    }).layer(Layer.bullet - 0.01f),

    plasmaFragDisappear = new Effect(12f, e -> {
        color(Pal.surge, Color.white, e.fin());
        Drawf.tri(e.x, e.y, e.fout() * 10f, e.fout() * 11f, e.rotation);
    }).layer(Layer.bullet - 0.01f),

    surgeSplash = new Effect(40f, 100f, e -> {
        color(Pal.surge);
        stroke(e.fout() * 2);
        circle(e.x, e.y, 4 + e.finpow() * 65);

        color(Pal.surge);

        for(int i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 6, 100 * e.fout(), i*90);
        }

        color();

        for(int i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 3, 35 * e.fout(), i*90);
        }
    }),

    oracleCharge = new Effect(30f, e -> {
        color(Pal.lancerLaser);
        Tmp.v1.trns(Mathf.randomSeed(e.id, 360f) + Time.time, (1 - e.finpow()) * 20f);
        Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fin() * 4.5f, 45f);
    }),

    oracleChargeBegin = new Effect(40, e -> {
        color(Pal.lancerLaser);
        Fill.circle(e.x, e.y, e.fin() * 6f);
    }),

    monolithRingEffect = new Effect(60f, e -> {
        if(e.data instanceof Float data){
            color(Pal.lancerLaser);

            stroke(e.fout() * 3f * data);
            circle(e.x, e.y, e.finpow() * 24f * data);
        }
    }),

    scarRailTrail = new Effect(16f, e -> {
        for(int i = 0; i < 2; i++){
            int sign = Mathf.signs[i];
            color(UnityPal.scarColor);
            Drawf.tri(e.x, e.y, 10f * e.fout(), 24f, e.rotation + 90f + 90f * sign);
            color(Color.white);
            Drawf.tri(e.x, e.y, Math.max(10f * e.fout() - 4f, 0f), 20f, e.rotation + 90f + 90f * sign);
        }
    }),

    falseLightning = new Effect(10f, 500f, e -> {
        if(!(e.data instanceof Float length)) return;
        int lenInt = Mathf.round(length / 8f);
        stroke(3f * e.fout());
        color(e.color, Color.white, e.fin());
        //unity.Unity.print(lenInt,"  ",length);
        for(int i = 0; i < lenInt; i++){
            float offsetXA = i == 0 ? 0 : Mathf.randomSeed(e.id + i * 6413L, -4.5f, 4.5f);
            float offsetYA = length / lenInt * i;
            int j = i + 1;
            float offsetXB = j == lenInt ? 0 : Mathf.randomSeed(e.id + j * 6413L, -4.5f, 4.5f);
            float offsetYB = length / lenInt * j;
            Tmp.v1.trns(e.rotation, offsetYA, offsetXA);
            Tmp.v1.add(e.x, e.y);
            Tmp.v2.trns(e.rotation, offsetYB, offsetXB);
            Tmp.v2.add(e.x, e.y);
            line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, false);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, getStroke() / 2f);
        }
    }),

    forgeAbsorbPulseEffect = new Effect(124f, e -> {
        float rad = 110f * e.fout(Interp.pow5In);
        int sides = Lines.circleVertices(rad);

        z(Layer.effect + 1f);
        blend(Blending.additive);
        Tmp.c1.set(UnityPal.endColor);
        Tmp.c1.a = e.fin(Interp.pow5Out);
        Fill.light(e.x, e.y, sides, rad, Color.clear, Tmp.c1);

        Tmp.c1.a = e.fin(Interp.pow10Out) * e.fout(Interp.pow10Out);
        Fill.light(e.x, e.y, 27, 40f, Tmp.c1, Color.clear);
        blend();
    }),

    forgeAbsorbEffect = new Effect(124f, e -> {
        float angle = e.rotation;
        float slope = (0.5f - Math.abs(e.finpow() - 0.5f)) * 2f;
        Tmp.v1.trns(angle, (1 - e.finpow()) * 110f);
        stroke(1.5f, UnityPal.endColor);
        lineAngleCenter(e.x + Tmp.v1.x, e.y + Tmp.v1.y, angle, slope * 8f);
    }),

    forgeFlameEffect = new Effect(84f, e -> { //Someone optimize this mess for me k thx
        float fin = e.fin(Interp.pow5Out);
        float alpha = 1f - Mathf.curve(fin, 0.5f, 1f);
        for(int i = 0; i < 4; i++){
            float a = 90f * i;
            for(int j = 0; j < 2; j++){
                float side = Mathf.signs[j];
                float fa = a - 45f * side;

                Tmp.v1.trns(a, (31f / 4f) * side, 76f / 4f);

                float s = (float)Math.sqrt(72f) / 2f;
                color(UnityPal.endColor);
                alpha(alpha);
                Tmp.v2.trns(fa, s, 0f);
                Tmp.v3.trns(fa, -s, 0f);
                Tmp.v4.trns(fa, 0f, fin * 20f);
                Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, s);
                Fill.tri(e.x + Tmp.v1.x + Tmp.v2.x, e.y + Tmp.v1.y + Tmp.v2.y,
                    e.x + Tmp.v1.x + Tmp.v3.x, e.y + Tmp.v1.y + Tmp.v3.y,
                    e.x + Tmp.v1.x + Tmp.v4.x, e.y + Tmp.v1.y + Tmp.v4.y
                );

                s = (float)Math.sqrt(18f) / 2f;
                color(Color.white);
                alpha(alpha);
                Tmp.v2.trns(fa, s, 0f);
                Tmp.v3.trns(fa, -s, 0f);
                Tmp.v4.trns(fa, 0f, fin * 16f);
                Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, s);
                Fill.tri(e.x + Tmp.v1.x + Tmp.v2.x, e.y + Tmp.v1.y + Tmp.v2.y,
                    e.x + Tmp.v1.x + Tmp.v3.x, e.y + Tmp.v1.y + Tmp.v3.y,
                    e.x + Tmp.v1.x + Tmp.v4.x, e.y + Tmp.v1.y + Tmp.v4.y
                );
            }
        }
    }).layer(Layer.blockOver),

    imberSparkCraftingEffect = new Effect(70f, e -> {
        color(UnityPal.imberColor, Color.valueOf("ffc266"), e.finpow());
        alpha(e.finpow());
        randLenVectors(e.id, 3, (1f - e.finpow()) * 24f, e.rotation, 360f, (x, y) -> {
            Drawf.tri(e.x + x, e.y + y, e.fout() * 8f, e.fout() * 10f, e.rotation);
            Drawf.tri(e.x + x, e.y + y, e.fout() * 4f, e.fout() * 6f, e.rotation);
        });
        color();
    }),

    healLaser = new Effect(60f, e -> {
        if(!(e.data instanceof Position[] temp)) return;
        float[] reduction = new float[]{0f, 1.5f};
        Position a = temp[0], b = temp[1];
        for(int i = 0; i < 2; i++){
            color(i == 0 ? Pal.heal : Color.white);
            stroke((3f - reduction[i]) * e.fout());
            line(a.getX(), a.getY(), b.getX(), b.getY());
            Fill.circle(a.getX(), a.getY(), (2.5f - reduction[i]) * e.fout());
            Fill.circle(b.getX(), b.getY(), (2.5f - reduction[i]) * e.fout());
        }
    }),

    pylonLaserCharge = new Effect(200f, 180f, e -> {
        e.scaled(100f, c -> {
            float slope = Interp.pow3Out.apply(Mathf.mod(c.fout() * 3f, 1f));
            float rot = Mathf.round(c.fout() * 4f);

            color(UnityPal.monolithLight);
            Fill.circle(c.x, c.y, 15f * c.fin());

            z(Layer.effect+1f);
            blend(Blending.additive);

            Tmp.c1.set(UnityPal.monolithLight).a(c.fin(Interp.pow3Out));
            Fill.light(c.x, c.y, 27, 40f * c.fout(Interp.pow10Out), Tmp.c1, Color.clear);

            Tmp.c1.a((1f - slope) * 0.5f);
            Fill.light(c.x, c.y, 4, 80f * slope, Color.clear, Tmp.c1);

            blend();
        });

        shoot: {
            if(e.fin() < 0.5f) break shoot;

            float fin = Mathf.curve(e.fin(), 0.5f, 1f);
            float finscaled = Mathf.curve(fin, 0f, 0.8f);
            float fin5 = Interp.pow5Out.apply(fin);
            float fin3 = Interp.pow3Out.apply(fin);
            float fin2 = Interp.pow2Out.apply(fin);
            float fout = 1f - fin;

            float rot = 370f * fin5;
            float rad = 160f * Interp.pow5Out.apply(finscaled);

            Lines.stroke(3 * fout);
            for(int i = 0; i < 2; i++){
                color(UnityPal.monolithLight, UnityPal.monolith, fin);
                Lines.square(e.x, e.y, 200f * fin3, rot * Mathf.signs[i]);

                Draw.color(UnityPal.monolith);
                Lines.square(e.x, e.y, 100f * fin5, rot * Mathf.signs[i] + 45f);
            };

            color(UnityPal.monolithLight, UnityPal.monolithDark, fin);
            randLenVectors(e.id, 48, fin3 * 180f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 5f * fout);
            });

            z(Layer.effect + 1f);
            blend(Blending.additive);

            Tmp.c1.set(UnityPal.monolithLight).a(1f - fin3);
            Fill.light(e.x, e.y, 27, 40f, Tmp.c1, Color.clear);

            Tmp.c1.set(UnityPal.monolithDark).a((1f - fin2) * 0.8f);
            Fill.light(e.x, e.y, 4, rad, Color.clear, Tmp.c1);
            blend();
        }
    }),

    evaporateDeath = new Effect(64f, 800f, e -> {
        if(!(e.data instanceof UnitVecData temp)) return;
        Unit unit = temp.unit;
        float curve = Interp.exp5In.apply(e.fin());
        Tmp.c1.set(Color.black);
        Tmp.c1.a = e.fout();
        color(Tmp.c1);
        rect(unit.type.region, unit.x + temp.vec.x * curve, unit.y + temp.vec.y * curve, unit.rotation - 90f);
    }),

    vaporation = new Effect(23f, e -> {
        if(!(e.data instanceof Position[] temp)) return;
        Tmp.v1.set(temp[0]);
        Tmp.v1.lerp(temp[1], e.fin());
        color(Pal.darkFlame, Pal.darkerGray, e.fin());
        Fill.circle(Tmp.v1.x + temp[2].getX(), Tmp.v1.y + temp[2].getY(), e.fout() * 5f);
    }).layer(Layer.flyingUnit + 0.012f),

    sparkleFx = new Effect(15f, e -> {
        color(Color.white, e.color, e.fin());
        integer = 1;
        randLenVectors(e.id, e.id % 3 + 1, e.rotation * 4f + 4f, (x, y) -> {
            spark(e.x + x, e.y + y, e.fout() * 4f, 0.5f + e.fout() * 2.2f, e.id * integer);
            integer++;
        });
    }),

    upgradeBlockFx = new Effect(90f, e -> {
        color(Color.white, Color.green, e.fin());
        stroke(e.fout() * 6f * e.rotation);
        square(e.x, e.y, (e.fin() * 4f + 2f) * e.rotation, 0f);
        integer = 1;
        randLenVectors(e.id, e.id % 3 + 7, e.rotation * 4f + 4f + 8f * e.finpow(), (x, y) -> {
            spark(e.x + x, e.y + y, e.fout() * 5f, e.fout() * 3.5f, e.id * integer);
            integer++;
        });
    }),

    imberCircleSparkCraftingEffect = new Effect(30f, e -> {
        color(Pal.surge);
        stroke(e.fslope());
        circle(e.x, e.y, e.fin() * 20f);
    }),

    waitFx = new Effect(30f, e -> {
        // if(!isArray(e.data)) return; (I don't know how to translate this)
        Object[] data = (Object[])e.data;
        float whenReady = (float)data[0];
        Unit u = (Unit)data[1];
        if(u == null || !u.isValid() || u.dead) return;
        color(e.color);
        stroke(e.fout() * 1.5f);
        polySeg(60, 0, (int)(60 * (1 - (e.rotation - Time.time) / whenReady)), u.x, u.y, 8f, 0f);
    }).layer(Layer.effect - 0.00001f),

    //^ this but better
    waitEffect = new Effect(30f, e -> {
        if(e.data instanceof WaitEffectData data){
            if(data.unit() == null || !data.unit().isValid() || data.unit().dead) return;

            color(e.color);
            stroke(e.fout() * 1.5f);
            polySeg(60, 0, (int)(60f * data.progress()), data.unit().x, data.unit().y, 8f, 0f);
        }
    }).layer(Layer.effect - 0.00001f),

    waitEffect2 = new Effect(30f, e -> {
        if(e.data instanceof WaitEffectData data){
            if(data.unit() == null || !data.unit().isValid() || data.unit().dead) return;

            color(e.color);
            stroke(e.fout() * 1.5f);
            polySeg(90, 0, (int)(90f * data.progress()), data.unit().x, data.unit().y, 12f, 0f);
        }
    }).layer(Layer.effect - 0.00001f),

    ringFx = new Effect(25f, e -> {
        if(!(e.data instanceof Unit u)) return;
        if(!u.isValid() || u.dead) return;
        color(Color.white, e.color, e.fin());
        stroke(e.fout() * 1.5f);
        circle(u.x, u.y, 8f);
    }),

    ringEffect2 = new Effect(25f, e -> {
        if(e.data instanceof Unit unit){
            if(!unit.isValid() || unit.dead) return;

            color(Color.white, e.color, e.fin());
            stroke(e.fout() * 1.5f);
            circle(unit.x, unit.y, 12f);
        }
    }),

    smallRingFx = new Effect(20f, e -> {
        if(!(e.data instanceof Unit u)) return;
        if(!u.isValid() || u.dead) return;
        color(Color.white, e.color, e.fin());
        stroke(e.fin());
        circle(u.x, u.y, e.fin() * 5f);
    }),

    smallRingEffect2 = new Effect(20f, e -> {
        if(e.data instanceof Unit unit){
            if(!unit.isValid() || unit.dead) return;

            color(Color.white, e.color, e.fin());
            stroke(e.fin());
            circle(unit.x, unit.y, e.fin() * 7.5f);
        }
    }),

    squareFx = new Effect(25f, e -> {
        if(!(e.data instanceof Unit u)) return;
        if(!u.isValid() || u.dead) return;
        color(Color.white, e.color, e.fin());
        stroke(e.fout() * 2.5f);
        square(u.x, u.y, e.fin() * 18f, 45f);
    }),

    expAbsorb = new Effect(15f, e -> {
        stroke(e.fout() * 1.5f);
        color(UnityPal.exp);
        circle(e.x, e.y, e.fin() * 2.5f + 1f);
    }),

    expDespawn = new Effect(15f, e -> {
        color(UnityPal.exp);
        randLenVectors(e.id, 7, 2f + 5 * e.fin(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout()));
    }),

    maxDamageFx = new Effect(16f, e -> {
        color(Color.orange);
        stroke(2.5f * e.fin());
        square(e.x, e.y, e.rotation * 4f);
    }),

    withstandFx = new Effect(16f, e -> {
        color(Color.orange);
        stroke(1.2f * e.rotation * e.fout());
        square(e.x, e.y, e.rotation * 4f);
    }),

    ahhimaLiquidNow = new Effect(45f, e -> {
        color(Color.gray, Color.clear, e.fin());
        randLenVectors(e.id, 3, 2.5f + e.fin() * 6f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.2f + e.fin() * 3f));
        color(UnityPal.lava, UnityPal.lava2, e.fout());
        randLenVectors(e.id + 1, 4, 1 + e.fin() * 4f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.2f + e.fout() * 1.3f));
    }),

    blinkFx = new Effect(30f, e -> {
        color(Color.white, UnityPal.dirium, e.fin());
        stroke(3f * e.rotation * e.fout());
        square(e.x, e.y, e.rotation * 4f * e.finpow());
    }),

    tpOut = new Effect(30f, e -> {
        color(UnityPal.dirium);
        stroke(3f * e.fout());
        square(e.x, e.y, e.finpow() * e.rotation, 45f);
        stroke(5f * e.fout());
        square(e.x, e.y, e.fin() * e.rotation, 45f);
        randLenVectors(e.id, 10, e.fin() * (e.rotation + 10f), (x, y) -> Fill.square(e.x + x, e.y + y, e.fout() * 4f, 100f * Mathf.randomSeed(e.id + 1) * e.fin()));
    }),

    tpIn = new Effect(50f, e -> {
        if(!(e.data instanceof UnitType type)) return;
        TextureRegion region = type.fullIcon;
        color();
        mixcol(UnityPal.dirium, 1f);
        rect(region, e.x, e.y, region.width * scl * e.fout(), region.height * scl * e.fout(), e.rotation);
        mixcol();
    }),

    tpFlash = new Effect(30f, e -> {
        if(!(e.data instanceof Unit unit) || !unit.isValid()) return;
        TextureRegion region = unit.type.fullIcon;
        mixcol(UnityPal.diriumLight, 1f);
        alpha(e.fout());
        rect(region, unit.x, unit.y, unit.rotation - 90f);
        mixcol();
        color();
    }).layer(Layer.flyingUnit + 1f),

    empShockwave = new Effect(30f, 800f, e -> {
        color(Pal.lancerLaser);
        Lines.stroke(e.fout() + 0.5f);
        Lines.circle(e.x, e.y, e.rotation * Mathf.curve(e.fin(), 0f, 0.23f));
    }),

    empCharge = new Effect(70f, e -> {
        color(Pal.lancerLaser);
        UnityDrawf.shiningCircle(e.id * 63, Time.time, e.x, e.y, 4f * e.fin(), 7, 15f, 24f * e.fin(), 2f * e.fin());
        color(Color.white);
        UnityDrawf.shiningCircle(e.id * 63, Time.time, e.x, e.y, 2f * e.fin(), 7, 15f, 38f * e.fin(), e.fin());
        color();
    }),

    blueTriangleTrail = new Effect(50f, e -> {
        color(Color.white, Pal.lancerLaser, e.fin());
        Fill.poly(e.x, e.y, 3, 4f * e.fout(), e.rotation + 180f);
    }),

    advanceFlameTrail = new Effect(27f, e -> {
        color(UnityPal.advance, UnityPal.advanceDark, e.fin());
        float rot = Mathf.randomSeed(e.id, -1, 1) * 270f;

        Fill.poly(e.x, e.y, 6, e.fout() * 4.1f, e.rotation + e.fin() * rot);
    }),

    advanceFlameSmoke = new Effect(13f, e -> {
        color(Color.valueOf("4d668f77"), Color.valueOf("35455f00"), e.fin());
        float rot = Mathf.randomSeed(e.id, -1, 1) * 270f;

        Angles.randLenVectors(e.id, 2, e.finpow() * 13f, e.rotation, 60f, (x, y) -> Fill.poly(e.x + x, e.y + y, 6, e.fout() * 4.1f, e.rotation + e.fin() * rot));
    }),

    arcCharge = new Effect(27f, e -> {
        color(Color.valueOf("606571"), Color.valueOf("6c8fc7"), e.fin());

        Angles.randLenVectors(e.id, 2, e.fout() * 40f, e.rotation, 135f, (x, y) -> {
            Fill.poly(e.x + x, e.y + y, 6, 1f + Mathf.sin(e.fin() * 3f, 1f, 2f) * 5f, e.rotation);
        });
    }),

    arcSmoke = new Effect(27f, e -> {
        color(Color.valueOf("6c8fc7"), Color.valueOf("606571"), e.fin());

        Angles.randLenVectors(e.id, 3, e.finpow() * 20f, e.rotation, 180f, (x, y) -> {
            Fill.poly(e.x + x, e.y + y, 6, e.fout() * 9f, e.rotation);
        });
    }),

    arcSmoke2 = new Effect(27f, e -> {
        color(Color.valueOf("6c8fc7"), Color.valueOf("606571"), e.fin());

        Tmp.v1.trns(e.rotation, e.fin() * 4.6f * 15f);
        Fill.poly(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 6, e.fout() * 16f, e.rotation);
    }),

    teamConvertedEffect = new Effect(18, e -> {
        color(UnityPal.advance, Color.white, e.fin());
        Fill.square(e.x, e.y, 0.1f + e.fout() * 2.8f, 45f);
    }),

    blueBurnEffect = new Effect(35f, e -> {
        color(UnityPal.advance, UnityPal.advanceDark, e.fin());

        Angles.randLenVectors(e.id, 3, 2 + e.fin() * 7, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.1f + e.fout() * 1.4f);
        });
    }),

    tenmeikiriCut = new Effect(20f, 150f, e -> {
        if(!(e.data instanceof Float)) return;
        color(UnityPal.scarColor, UnityPal.endColor, e.fin());
        Drawf.tri(e.x, e.y, 12f * e.fout(), (float)e.data, e.rotation);
        Drawf.tri(e.x, e.y, 12f * e.fout(), (float)e.data, e.rotation + 180f);
    }),

    vapourizeTile = new Effect(126f, (float)(Vars.tilesize * 16), e -> {
        color(Color.red);
        blend(Blending.additive);

        Fill.square(e.x, e.y, e.fout() * e.rotation * (Vars.tilesize / 2f));

        if(e.data instanceof TurretBuild turret){
            mixcol(Color.red, 1f);
            alpha(e.fout());
            Draw.rect(turret.block.region, e.x, e.y, turret.rotation - 90f);
        }

        blend();
        mixcol();
        color();
    }).layer(Layer.effect + 1f),

    vapourizeUnit = new Effect(126f, 512f, e -> {
        mixcol(Color.red, 1f);
        color(1f, 1f, 1f, e.fout());
        blend(Blending.additive);

        GraphicUtils.simpleUnitDrawer((Unit)e.data, false);

        blend();
        color();
        mixcol();
    }).layer(Layer.effect + 1f),

    endgameLaser = new Effect(76f, 820f * 2f, e -> {
        if(e.data == null) return;
        Color[] colors = {Color.valueOf("f53036"), Color.valueOf("ff786e"), Color.white};
        float[] strokes = {2f, 1.3f, 0.6f};
        float oz = z();
        Object[] data = (Object[])e.data;
        Position a = (Position)data[0];
        Position b = (Position)data[1];
        float width = (float)data[2];
        Tmp.v1.set(a).lerp(b, Mathf.curve(e.fin(), 0f, 0.09f));
        for(int i = 0; i < 3; i++){
            z(oz + (i / 1000f));
            if(i >= 2){
                color(Color.white);
            }else{
                color(Tmp.c1.set(colors[i]).mul(1f, 1f + Utils.offsetSinB(0f, 5f), 1f + Utils.offsetSinB(90f, 5f), 1f));
            }

            Fill.circle(a.getX(), a.getY(), strokes[i] * 4f * width * e.fout());
            Fill.circle(Tmp.v1.x, Tmp.v1.y, strokes[i] * 4f * width * e.fout());

            Lines.stroke(strokes[i] * 4f * width * e.fout());
            Lines.line(a.getX(), a.getY(), Tmp.v1.x, Tmp.v1.y);
        }
        z(oz);
    }),

    rainbowTextureTrail = new Effect(80f, e -> {
        if(!(e.data instanceof RainbowUnitType t)) return;
        blend(Blending.additive);
        color(Tmp.c1.set(Color.red).shiftHue(e.time * 4f).a(Mathf.clamp(e.fout() * 1.5f)));
        Draw.rect(t.trailRegion, e.x, e.y, e.rotation - 90f);
        blend();
    }),

    kamiBulletDespawn = new Effect(60f, e -> {
        float size = Mathf.clamp(e.rotation, 0f, 15f);

        blend(Blending.additive);
        color(Tmp.c1.set(Color.red).shiftHue(((e.time + Time.time) / 2f) * 3f));
        Lines.stroke(2f * e.fout());
        Lines.circle(e.x, e.y, (e.finpow() * size) + (size / 2f));
        Lines.stroke(e.fout());
        Lines.circle(e.x, e.y, (e.finpow() * (size / 2f)) + size);
        blend();
    }),

    kamiEoLCharge = new Effect(60f, e -> {
        if(!(e.data instanceof Unit u)) return;
        blend(Blending.additive);
        for(int i = 0; i < 2; i++){
            float angle = i * 360f / 2f;
            color(Tmp.c1.set(Color.red).shiftHue((e.time * 5f) + angle).a(Mathf.clamp(e.fout() * 1.5f)));
            Tmp.v1.trns(angle + (e.fin() * 180f), 150f * e.fslope()).add(u);
            Draw.rect(u.type.region, Tmp.v1.x, Tmp.v1.y, u.rotation - 90f);
        }
        for(int i = 0; i < 4; i++){
            float angle = i * 360f / 4f;
            color(Tmp.c1.set(Color.red).shiftHue((e.time * 5f) + angle).a(Mathf.clamp(e.fout() * 1.5f)));
            Tmp.v1.trns(angle + (e.fin() * -270f), 100f * e.fslope()).add(u);
            Draw.rect(u.type.region, Tmp.v1.x, Tmp.v1.y, u.rotation - 90f);
        }
        blend();
    }),

    kamiCharge = new Effect(60f, e -> {
        blend(Blending.additive);
        color(Tmp.c1.set(Color.red).shiftHue(e.time * 3f));
        e.scaled(20f, s -> {
            stroke(3f * Mathf.clamp(s.fin() * 2f));
            circle(e.x, e.y, s.fout() * 300f);
        });
        for(int i = 0; i < 15; i++){
            float fout = 1f - Mathf.clamp(Mathf.randomSeed(e.id + (i * 121), 1f, 2f) * e.fin());
            float angle = Mathf.randomSeed(e.id + (i * 3542), 360f);
            float rad = Mathf.randomSeed(e.id + (i * 2451), 150f, 300f);

            if(fout > 0.0001f){
                Tmp.v1.trns(angle, rad * fout).add(e.x, e.y);
                float slope = (0.5f - Math.abs(fout - 0.5f)) * 2f;
                color(Tmp.c1.set(Color.red).shiftHue((e.time + Mathf.randomSeed(e.id + (i * 231), 360f)) * 3f));
                Fill.square(Tmp.v1.x, Tmp.v1.y, 13f * slope, 45f);
            }
        }
        blend();
    }),

    kamiWarningLine = new Effect(120f, 670f * 2f, e -> {
        if(e.data == null) return;
        Position[] data = (Position[])e.data;
        Position a = data[0];
        Position b = data[1];

        color(Tmp.c1.set(Color.red).shiftHue(e.time * 3f));
        Lines.stroke(Mathf.clamp(e.fslope() * 2f) * 1.2f);
        Lines.line(a.getX(), a.getY(), b.getX(), b.getY());
    }),

    pointBlastLaserEffect = new Effect(23f, 600f, e -> {
        if(!(e.data instanceof PointBlastLaserBulletType btype)) return;

        for(int i = 0; i < btype.laserColors.length; i++){
            color(btype.laserColors[i]);
            Fill.circle(e.x, e.y, (e.rotation - (btype.auraWidthReduction * i)) * e.fout());
        }
        Drawf.light(e.x, e.y, e.rotation * e.fout() * 3f, btype.laserColors[0], 0.66f);
    }),

    rockFx = new Effect(10f, e -> {
        color(Color.orange, Color.gray, e.fin());
        stroke(1f);
        spikes(e.x, e.y, e.fin() * 4f, 1.5f, 6);
    }),

    craftFx = new Effect(10f, e -> {
        color(Pal.accent, Color.gray, e.fin());
        stroke(1f);
        spikes(e.x, e.y, e.fin() * 4f, 1.5f, 6);
    }),

    monumentDespawn = new Effect(32f, e -> {
        e.scaled(15f, i -> {
            color(Pal.lancerLaser);
            stroke(i.fout() * 5f);
            circle(e.x, e.y, 4f + i.finpow() * 26f);
        });
        randLenVectors(e.id, 25, 5f + e.fin() * 80f, e.rotation, 60f, (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 3f));
    }),

    monumentTrail = new Effect(32f, e -> {
        float len = ((PointBulletType)monumentRailBullet).trailSpacing - 12f;
        float rot = e.rotation;
        Tmp.v1.trns(rot, len);
        for(int i = 0; i < 2; i++){
            color(i < 1 ? Color.white : Pal.lancerLaser);
            float scl = i < 1 ? 1f : 0.5f;
            stroke(e.fout() * 10f * scl);
            lineAngle(e.x, e.y, rot, len, false);
            Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, getStroke() * 1.22f, 12f * scl, rot);
            Drawf.tri(e.x, e.y, getStroke() * 1.22f, 12 * scl, rot + 180f);
        }
    }),

    supernovaChargeBegin = new Effect(27f, e -> {
        if(e.data instanceof Float data){
            float r = data;
            randLenVectors(e.id, (int)(2f * r), 1f + 27f * e.fout(), (x, y) -> {
                color(Pal.lancerLaser);
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), (1f + e.fslope() * 6f) * r);
            });
        }
    }),

    supernovaStarHeatwave = new Effect(40f, e -> {
        color(Pal.lancerLaser);
        stroke(e.fout());
        circle(e.x, e.y, 110f * e.fin());
        circle(e.x, e.y, 120f * e.finpow() * 0.6f);
    }),

    supernovaChargeStar = new Effect(30f, e -> {
        if(e.data instanceof Float data){
            float r = data;

            color(Pal.lancerLaser);
            alpha(e.fin() * 2f * r);
            circle(e.x, e.y, 150f * Interp.pow2Out.apply(e.fout()) * Mathf.lerp(0.1f, 1f, r));
        }
    }),

    supernovaStarDecay = new Effect(56f, e -> randLenVectors(e.id, 1, 36f * e.finpow(), (x, y) -> {
        color(Pal.lancerLaser);
        Fill.rect(e.x + x, e.y + y, 2.2f * e.fout(), 2.2f * e.fout(), 45f);
    })),

    supernovaChargeStar2 = new Effect(27f, e -> {
        if(e.data instanceof Float data){
            float r = data;
            randLenVectors(e.id, (int)(3f * r), e.fout() * ((90f + r * 150f) * (0.3f + Mathf.randomSeed(e.id, 0.7f))), (x, y) -> {
                color(Pal.lancerLaser);
                Fill.circle(e.x + x, e.y + y, 2f * e.fin());
            });
        }
    }),

    supernovaPullEffect = new Effect(30f, 500f, e -> {
        if(e.data instanceof Long data){
            float size = e.rotation;

            float x = e.x + Mathf.randomSeedRange(e.id, 4f);
            float y = e.y + Mathf.randomSeedRange(e.id + 1, 4f);
            long pos = SVec2.scl(SVec2.sub(data, x, y), e.fin());

            color(Pal.lancerLaser);
            Fill.circle(x + SVec2.x(pos), y + SVec2.y(pos), size * (0.5f + e.fslope() * 0.5f));
        }
    }),

    reflectResumeDynamic = new Effect(22f, e -> {
        color(Color.valueOf("FFF3D6"));
        stroke(e.fout() * 2f);
        Lines.circle(e.x, e.y, Interp.pow3In.apply(e.fout()) * e.rotation);
    }),

    reflectPulseDynamic = new Effect(22f, e -> {
        color(Color.valueOf("FFF3D6"));
        stroke(e.fout() * 2f);
        Lines.circle(e.x, e.y, e.finpow() * e.rotation);
    }),

    slashEffect = new Effect(90f, e -> {
        color(Pal.lancerLaser);
        Drawf.tri(e.x, e.y, 4f * e.fout(), 45f, (e.id * 57f + 90f) % 360f);
        Drawf.tri(e.x, e.y, 4f * e.fout(), 45f, (e.id * 57f - 90f) % 360f);
    }),

    teleportPos = new Effect(60f, e -> {
        if(e.data instanceof UnitType unit){
            blend(Blending.additive);
            alpha(e.fout());
            TextureRegion region = unit.fullIcon;
            float w = region.width * scl * e.fout();
            float h = region.height * scl * e.fout();

            rect(region, e.x, e.y, w, h, e.rotation - 90);
            blend();
        }
    }),

    distortFx = new Effect(18, e -> {
        if(!(e.data instanceof Float)) return;
        color(Pal.lancerLaser, Pal.place, e.fin());
        Fill.square(e.x, e.y, 0.1f + e.fout() * 2.5f, (float)e.data);
    }),

    distSplashFx = new Effect(80, e -> {
        if(!(e.data instanceof Float[])) return;
        color(Pal.lancerLaser, Pal.place, e.fin());
        Lines.stroke(2 * e.fout());
        Lines.circle(e.x, e.y, ((Float[])e.data)[0] * e.fin());
    }){
        @Override
        public void at(float x, float y, float rotation, Object data){
            Effect effect = this;
            if((data instanceof Float[])) effect.lifetime = ((Float[])data)[1];

            create(effect, x, y, rotation, Color.white, data);
        }
    },

    distStart = new Effect(45, e -> {
        if(!(e.data instanceof Float)) return;

        float centerf = Color.clear.toFloatBits();
        float edgef = Pal.lancerLaser.cpy().a(e.fout()).toFloatBits();
        float sides = Mathf.ceil(Lines.circleVertices((float)e.data) / 2f) * 2;
        float space = 360f / sides;

        for(int i = 0; i < sides; i += 2){
            float px = Angles.trnsx(space * i, (float)e.data);
            float py = Angles.trnsy(space * i, (float)e.data);
            float px2 = Angles.trnsx(space * (i + 1), (float)e.data);
            float py2 = Angles.trnsy(space * (i + 1), (float)e.data);
            float px3 = Angles.trnsx(space * (i + 2), (float)e.data);
            float py3 = Angles.trnsy(space * (i + 2), (float)e.data);
            Fill.quad(e.x, e.y, centerf, e.x + px, e.y + py, edgef, e.x + px2, e.y + py2, edgef, e.x + px3, e.y + py3, edgef);
        }
    }),

    smallChainLightning = new Effect(40f, 300f, e -> {
        if(!(e.data instanceof Position p)) return;

        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
        Tmp.v1.set(p).sub(e.x, e.y).nor();

        float normx = Tmp.v1.x, normy = Tmp.v1.y;
        float range = 6f;
        int links = Mathf.ceil(dst / range);
        float spacing = dst / links;

        Lines.stroke(2.5f * e.fout());
        Draw.color(Color.white, e.color, e.fin());

        Lines.beginLine();

        Lines.linePoint(e.x, e.y);

        rand.setSeed(e.id);

        for(int i = 0; i < links; i++){
            float nx, ny;
            if(i == links - 1){
                nx = tx;
                ny = ty;
            }else{
                float len = (i + 1) * spacing;
                Tmp.v1.setToRandomDirection(rand).scl(range/2f);
                nx = e.x + normx * len + Tmp.v1.x;
                ny = e.y + normy * len + Tmp.v1.y;
            }

            Lines.linePoint(nx, ny);
        }

        Lines.endLine();
    }),

    chainLightning = new Effect(30f, 300f, e -> {
        if(!(e.data instanceof Position p)) return;

        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
        Tmp.v1.set(p).sub(e.x, e.y).nor();

        float normx = Tmp.v1.x, normy = Tmp.v1.y;
        float range = 6f;
        int links = Mathf.ceil(dst / range);
        float spacing = dst / links;

        Lines.stroke(4f * e.fout());
        Draw.color(Color.white, e.color, e.fin());

        Lines.beginLine();

        Lines.linePoint(e.x, e.y);

        rand.setSeed(e.id);

        for(int i = 0; i < links; i++){
            float nx, ny;
            if(i == links - 1){
                nx = tx;
                ny = ty;
            }else{
                float len = (i + 1) * spacing;
                Tmp.v1.setToRandomDirection(rand).scl(range/2f);
                nx = e.x + normx * len + Tmp.v1.x;
                ny = e.y + normy * len + Tmp.v1.y;
            }

            Lines.linePoint(nx, ny);
        }

        Lines.endLine();
    }),

    ricochetTrailSmall = new Effect(12f, e -> randLenVectors(e.id, 4, e.fout() * 3.5f, (x, y) -> {
        float w = 0.3f + e.fout();

        color(UnityPal.monolith, UnityPal.monolithDark, e.fin());
        Fill.rect(e.x + x, e.y + y, w, w, 45f);
    })),

    ricochetTrailMedium = new Effect(16f, e -> randLenVectors(e.id, 5, e.fout() * 5f, (x, y) -> {
        float w = 0.3f + e.fout() * 1.3f;

        color(UnityPal.monolith, UnityPal.monolithDark, e.fin());
        Fill.rect(e.x + x, e.y + y, w, w, 45f);
    })),

    ricochetTrailBig = new Effect(20f, e -> randLenVectors(e.id, 6, e.fout() * 6.5f, (x, y) -> {
        float w = 0.3f + e.fout() * 1.7f;

        color(UnityPal.monolith, UnityPal.monolithDark, e.fin());
        Fill.rect(e.x + x, e.y + y, w, w, 45f);
    })),

    plated = new Effect(30f, e -> {
        color(e.color);
        Fill.circle(e.x, e.y, e.fout() * (float)e.data);
    }),

    sparkBoi = new Effect(15f, e -> {
        Draw.color(e.color);
        for (int j = 0; j < 4; j++) {
            Drawf.tri(e.x, e.y, (float) e.data - e.fin(), (float) e.data + 1 - e.fin() * ((float) e.data + 1), 90 * j + e.rotation);
        }
        Draw.color();
    }),

    orbShot = new Effect(20f, e -> {
        Draw.color(e.color);
        Draw.alpha(e.fout());
        Lines.circle(e.x, e.y, 4+e.fin()*2);
    });
}

package unity.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import unity.entities.UnitVecData;
import unity.graphics.*;
import unity.util.*;

//fixing rect as Draw.rect not Lines.rect. currently no use
import static arc.graphics.g2d.Draw.rect;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static unity.content.UnityBullets.*;

public class UnityFx{
    private static int integer;
    public static final Effect
        //@formatter:off
    shootSmallBlaze = new Effect(22f, e -> {    //@formatter:on
        color(Pal.lightFlame, Pal.darkFlame, Pal.gray, e.fin());
        randLenVectors(e.id, 16, e.finpow() * 60f, e.rotation, 18f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.85f + e.fout() * 3.5f));
    }),

    shootPyraBlaze = new Effect(32f, e -> {
        color(Pal.lightPyraFlame, Pal.darkPyraFlame, Pal.gray, e.fin());
        randLenVectors(e.id, 16, e.finpow() * 60f, e.rotation, 18f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.85f + e.fout() * 3.5f));
    }),

    craftingEffect = new Effect(67f, 35f, e -> {
        float value = Mathf.randomSeed(e.id);

        Tmp.v1.trns(value * 360f + ((value + 4f) * e.fin() * 80f), (Mathf.randomSeed(e.id * 126) + 1f) * 34f * (1f - e.finpow()));

        color(UnityPal.laserOrange);
        Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fslope() * 3f, 45f);
        color();
    }),

    orbHit = new Effect(12f, e -> {
        color(Pal.surge);
        stroke(e.fout() * 1.5f);
        randLenVectors(e.id, 8, e.finpow() * 17f, e.rotation, 360f, (x, y) -> {
            float ang = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, ang, e.fout() * 4f + 1f);
        });
    }),

    orbShoot = new Effect(21f, e -> {
        color(Pal.surge);
        for(int i = 0; i < 2; i++){
            int l = Mathf.signs[i];
            Drawf.tri(e.x, e.y, 4f * e.fout(), 29f, e.rotation + 67 * l);
        }
    }),

    orbTrail = new Effect(43f, e -> {
        float originalZ = z();

        Tmp.v1.trns(Mathf.randomSeed(e.id) * 360f, Mathf.randomSeed(e.id * 341) * 12f * e.fin());

        z(Layer.bullet - 0.01f);
        Drawf.light(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 4.7f * e.fout() + 3f, Pal.surge, 0.6f);

        color(Pal.surge);
        Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout() * 2.7f);

        z(originalZ);
    }),

    orbShootSmoke = new Effect(26f, e -> {
        color(Pal.surge);
        randLenVectors(e.id, 7, 80f, e.rotation, 0f, (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 4f));
    }),

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

    plasmaCharge = new Effect(96f, e -> {
        color(Pal.surge);
        randLenVectors(e.id, 5, (1f - e.finpow()) * 24f, e.rotation, 360f, (x, y) -> {
            Drawf.tri(e.x + x, e.y + y, e.fout() * 10f, e.fout() * 11f, e.rotation);
            Drawf.tri(e.x + x, e.y + y, e.fout() * 8f, e.fout() * 9f, e.rotation);
        });
    }),

    plasmaChargeBegin = new Effect(250f, e -> {
        color(Pal.surge);
        Drawf.tri(e.x, e.y, e.fin() * 16f, e.fout() * 20f, e.rotation);
    }),

    plasmaShoot = new Effect(36f, e -> {
        color(Pal.surge, Color.white, e.fin());

        randLenVectors(e.id, 8, e.fin() * 20f + 1f, e.rotation, 40f, (x, y) -> {
            Drawf.tri(e.x + x, e.y + y, e.fout() * 14f, e.fout() * 15f, e.rotation);
            Drawf.tri(e.x + x, e.y + y, e.fout() * 8f, e.fout() * 9f, e.rotation);
        });

        randLenVectors(e.id, 4, e.fin() * 20f + 1f, e.rotation, 40f, (x, y) -> lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 18f + 3f));
    }),

    plasmaTriangleHit = new Effect(30f, e -> {
        color(Pal.surge);

        stroke(e.fout() * 2.8f);
        circle(e.x, e.y, e.fin() * 60);
    }),

    plasmaFragAppear = new Effect(12f, e -> {
        z(Layer.bullet - 0.01f);

        color(Color.white);
        Drawf.tri(e.x, e.y, e.fin() * 12f, e.fin() * 13f, e.rotation);

        z();
    }),

    plasmaFragDisappear = new Effect(12f, e -> {
        z(Layer.bullet - 0.01f);

        color(Pal.surge, Color.white, e.fin());
        Drawf.tri(e.x, e.y, e.fout() * 10f, e.fout() * 11f, e.rotation);

        z();
    }),

    oracleChage = new Effect(30f, e -> {
        color(Pal.lancerLaser);
        Tmp.v1.trns(Mathf.randomSeed(e.id, 360f) + Time.time, (1 - e.finpow()) * 20f);
        Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fin() * 4.5f, 45f);
    }),

    oracleChargeBegin = new Effect(40, e -> {
        color(Pal.lancerLaser);
        Fill.circle(e.x, e.y, e.fin() * 6f);
    }),

    effect = new Effect(60f, e -> {
        color(Pal.lancerLaser);
        float temp = (float)e.data;
        stroke(e.fout() * 3f * temp);
        circle(e.x, e.y, e.finpow() * 24f * temp);
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

    scarRailTrail = new Effect(16f, e -> {
        for(int i = 0; i < 2; i++){
            int sign = Mathf.signs[i];
            color(UnityPal.scarColor);
            Drawf.tri(e.x, e.y, 10f * e.fout(), 24f, e.rotation + 90f + 90f * sign);
            color(Color.white);
            Drawf.tri(e.x, e.y, Math.max(10f * e.fout() - 4f, 0f), 20f, e.rotation + 90f + 90f * sign);
        }
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

    falseLightning = new Effect(10f, 500f, e -> {
        if(!(e.data instanceof Float length)) return;
        int lenInt = Mathf.round(length / 8f);
        stroke(3f * e.fout());
        color(e.color, Color.white, e.fin());
        //unity.Unity.print(lenInt,"  ",length);
        for(int i = 0; i < lenInt; i++){
            float offsetXA = i == 0 ? 0 : Mathf.randomSeed(e.id + i * 6413, -4.5f, 4.5f);
            float offsetYA = length / lenInt * i;
            int j = i + 1;
            float offsetXB = j == lenInt ? 0 : Mathf.randomSeed(e.id + j * 6413, -4.5f, 4.5f);
            float offsetYB = length / lenInt * j;
            Tmp.v1.trns(e.rotation, offsetYA, offsetXA);
            Tmp.v1.add(e.x, e.y);
            Tmp.v2.trns(e.rotation, offsetYB, offsetXB);
            Tmp.v2.add(e.x, e.y);
            line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, false);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, getStroke() / 2f);
        }
    }),

    forgeAbsorbEffect = new Effect(124f, e -> {
        float angle = e.rotation;
        float slope = (0.5f - Math.abs(e.finpow() - 0.5f)) * 2f;
        Tmp.v1.trns(angle, (1 - e.finpow()) * 110f);
        color(UnityPal.endColor);
        stroke(1.5f);
        lineAngleCenter(e.x + Tmp.v1.x, e.y + Tmp.v1.y, angle, slope * 8f);
    }),

    imberSparkCraftingEffect = new Effect(70f, e -> {
        color(UnityPal.imberColor, Color.valueOf("ffc266"), e.finpow());
        alpha(e.finpow());
        randLenVectors(e.id, 3, (1f - e.finpow()) * 24f, e.rotation, 360f, (x, y) -> {
            Drawf.tri(e.x + x, e.y + y, e.fout() * 8f, e.fout() * 10f, e.rotation);
            Drawf.tri(e.x + x, e.y + y, e.fout() * 4f, e.fout() * 6f, e.rotation);
        });
        color();
    }),

    imberCircleSparkCraftingEffect = new Effect(30f, e -> {
        color(Pal.surge);
        stroke(e.fslope());
        circle(e.x, e.y, e.fin() * 20f);
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

    coloredHitSmall = new Effect(14f, e -> {
        color(Color.white, e.color, e.fin());
        e.scaled(7f, s -> {
            stroke(0.5f + s.fout());
            circle(e.x, e.y, s.fin() * 5f);
        });
        stroke(0.5f + e.fout());
        randLenVectors(e.id, 5, e.fin() * 15f, (x, y) -> lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3f + 1f));
    }),

    pylonLaserCharge = new Effect(200f, 180f, e -> {
        LaserBulletType laser = (LaserBulletType)pylonLaser;
        e.scaled(100f, c -> {
            float cwidth = laser.width;

            for(int i = 0; i < laser.colors.length; i++){
                cwidth *= laser.lengthFalloff;

                color(laser.colors[i]);
                Fill.circle(e.x, e.y, cwidth * c.fin());

                for(int j = 0; j < 2; j++){
                    stroke(c.fin() * 1.5f * i);
                    square(e.x, e.y, c.fout() * laser.width * i, Time.time * 4f * Mathf.signs[j]);
                }
            }
        });

        shoot:
        {
            if(e.fin() < 0.5f) break shoot;

            float fin = Mathf.curve(e.fin(), 0.5f, 1f);
            float finpow = Interp.pow3Out.apply(fin);
            float fout = 1 - fin;

            for(int i = 0; i < laser.colors.length; i++){
                color(laser.colors[i]);

                for(int j = 0; j < 2; j++){
                    stroke(fout * 1.5f * i);

                    float rot = Mathf.signs[j] * (Time.time + (fin * 720f));
                    square(e.x, e.y, finpow * laser.width * 2f * i, rot);
                }
            }

            randLenVectors(e.id, 48, finpow * 180f, (x, y) -> {
                color(Color.white, Pal.lancerLaser, Color.cyan, fin);

                Fill.circle(e.x + x, e.y + y, fout * 5f);
            });

            color(Pal.lancerLaser, fout * 0.4f);
            Fill.circle(e.x, e.y, finpow * 180f);
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
            UnityDrawf.spark(e.x + x, e.y + y, e.fout() * 4f, 0.5f + e.fout() * 2.2f, e.id * integer);
            integer++;
        });
    }),

    upgradeBlockFx = new Effect(90f, e -> {
        color(Color.white, Color.green, e.fin());
        stroke(e.fout() * 6f * e.rotation);
        square(e.x, e.y, (e.fin() * 4f + 2f) * e.rotation, 0f);
        integer = 1;
        randLenVectors(e.id, e.id % 3 + 7, e.rotation * 4f + 4f + 8f * e.finpow(), (x, y) -> {
            UnityDrawf.spark(e.x + x, e.y + y, e.fout() * 5f, e.fout() * 3.5f, e.id * integer);
            integer++;
        });
    }),

    expAbsorb = new Effect(15f, e -> {
        stroke(e.fout() * 1.5f);
        color(UnityPal.expColor);
        circle(e.x, e.y, e.fin() * 2.5f + 1f);
    }),

    expDespawn = new Effect(15f, e -> {
        color(UnityPal.expColor);
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
        color(UnityPal.lavaColor, UnityPal.lavaColor2, e.fout());
        randLenVectors(e.id + 1, 4, 1 + e.fin() * 4f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.2f + e.fout() * 1.3f));
    }),

    blinkFx = new Effect(30f, e -> {
        color(Color.white, UnityPal.diriumColor, e.fin());
        stroke(3f * e.rotation * e.fout());
        square(e.x, e.y, e.rotation * 4f * e.finpow());
    }),

    tpOut = new Effect(30f, e -> {
        color(UnityPal.diriumColor);
        stroke(3f * e.fout());
        square(e.x, e.y, e.finpow() * e.rotation, 45f);
        stroke(5f * e.fout());
        square(e.x, e.y, e.fin() * e.rotation, 45f);
        randLenVectors(e.id, 10, e.fin() * (e.rotation + 10f), (x, y) -> Fill.square(e.x + x, e.y + y, e.fout() * 4f, 100f * Mathf.randomSeed(e.id + 1) * e.fin()));
    }),

    tpIn = new Effect(50f, e -> {
        if(!(e.data instanceof UnitType type)) return;
        TextureRegion region = type.icon(Cicon.full);
        color();
        mixcol(UnityPal.diriumColor, 1f);
        rect(region, e.x, e.y, region.width * scl * e.fout(), region.height * scl * e.fout(), e.rotation);
        mixcol();
    }),

    tpFlash = new Effect(30f, e -> {
        if(!(e.data instanceof Unit unit) || !unit.isValid()) return;
        TextureRegion region = unit.type.icon(Cicon.full);
        mixcol(UnityPal.diriumColor2, 1f);
        alpha(e.fout());
        rect(region, unit.x, unit.y, unit.rotation - 90f);
        mixcol();
        color();
    }).layer(Layer.flyingUnit + 1f),

    endGameShoot = new Effect(45f, 820f * 2f, e -> {
        float curve = Mathf.curve(e.fin(), 0f, 0.2f) * 820f;
        float curveB = Mathf.curve(e.fin(), 0f, 0.7f);

        Draw.color(Color.red, Color.valueOf("ff000000"), curveB);
        Draw.blend(Blending.additive);
        Fill.poly(e.x, e.y, Lines.circleVertices(curve), curve);
        Draw.blend();
    }).layer(110.99f),

    vapourizeTile = new Effect(126f, (float)(Vars.tilesize * 16), e -> {
        Draw.color(Color.red);
        Draw.blend(Blending.additive);

        Fill.circle(e.x, e.y, e.fout() * e.rotation * (Vars.tilesize / 2f));

        if(e.data instanceof TurretBuild turret){
            Draw.mixcol(Color.red, 1f);
            Draw.alpha(e.fout());
            Draw.rect(turret.block.region, e.x, e.y, turret.rotation - 90f);
        }

        Draw.blend();
        Draw.mixcol();
        Draw.color();
    }).layer(111f),

    vapourizeUnit = new Effect(126f, 512f, e -> {
        Draw.mixcol(Color.red, 1f);
        Draw.color(1f, 1f, 1f, e.fout());
        Draw.blend(Blending.additive);

        Funcs.simpleUnitDrawer((Unit)e.data, false);

        Draw.blend();
        Draw.color();
        Draw.mixcol();
    }).layer(111f),

    endgameLaser = new Effect(76f, 820f * 2f, e -> {
        if(e.data == null) return;
        Color[] colors = {Color.valueOf("f53036"), Color.valueOf("ff786e"), Color.white};
        float[] strokes = {2f, 1.3f, 0.6f};
        float oz = Draw.z();
        Object[] data = (Object[])e.data;
        Position a = (Position)data[0];
        Position b = (Position)data[1];
        float width = (float)data[2];
        Tmp.v1.set(a).lerp(b, Mathf.curve(e.fin(), 0f, 0.09f));
        for(int i = 0; i < 3; i++){
            Draw.z(oz + (i / 1000f));
            if(i >= 2){
                Draw.color(Color.white);
            }else{
                Draw.color(Tmp.c1.set(colors[i]).mul(1f, 1f + Funcs.offsetSinB(0f, 5f), 1f + Funcs.offsetSinB(90f, 5f), 1f));
            }

            Fill.circle(a.getX(), a.getY(), strokes[i] * 4f * width * e.fout());
            Fill.circle(Tmp.v1.x, Tmp.v1.y, strokes[i] * 4f * width * e.fout());

            Lines.stroke(strokes[i] * 4f * width * e.fout());
            Lines.line(a.getX(), a.getY(), Tmp.v1.x, Tmp.v1.y);
        }
        Draw.z(oz);
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
    //I have no idea with naming
    monumentShoot = new Effect(48f, e -> {
        color(Color.white, Pal.lancerLaser, Color.cyan, e.fin());
        randLenVectors(e.id, 12, e.finpow() * 64f, e.rotation, 16f, (x, y) -> Fill.circle(e.x + x, e.y + y, 1f + e.fout() * 5f));
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

    lightningSpawnShoot = new Effect(18f, e -> {
        e.scaled(12f, i -> {
            randLenVectors(e.id, 8, 4f + i.fin() * 18f, (x, y) -> {
                color(Color.white, Pal.lancerLaser, e.fin());
                Fill.square(e.x + x, e.y + y, 1f + i.fout() * 3f, 45f);
            });
            color(Color.white, Pal.lancerLaser, e.fin());
            alpha(e.fout());
            Fill.circle(e.x, e.y, e.finpow() * 8f);
        });
    });
}

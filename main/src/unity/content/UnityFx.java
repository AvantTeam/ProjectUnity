package unity.content;

import arc.Core;
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
import mindustry.ui.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import unity.entities.UnitVecData;
import unity.entities.abilities.BaseAbility.*;
import unity.entities.bullet.*;
import unity.entities.effects.*;
import unity.graphics.*;
import unity.type.*;
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
    expGain = new Effect(75f, 400f, e -> {
        if(!(e.data instanceof Position pos)) return;

        float fin = Mathf.curve(e.fin(), 0, Mathf.randomSeed(e.id, 0.25f, 1f));
        if(fin >= 1) return;

        float a = angle(e.x, e.y, pos.getX(), pos.getY()) - 90;
        float d = Mathf.dst(e.x, e.y, pos.getX(), pos.getY());
        float fslope = fin * (1f - fin) * 4f;
        float sfin = Interp.pow2In.apply(fin);
        float spread = d / 4f;
        Tmp.v1.trns(a, Mathf.randomSeed(e.id * 2l, -spread, spread) * fslope, d * sfin);
        Tmp.v1.add(e.x, e.y);

        color(UnityPal.expColor, Color.white, 0.1f + 0.1f * Mathf.sin(Time.time * 0.03f + e.id * 3f));
        Fill.circle(Tmp.v1.x, Tmp.v1.y, 1.5f);
        stroke(0.5f);
        for(int i = 0; i < 4; i++) Drawf.tri(Tmp.v1.x, Tmp.v1.y, 4f, 4 + 1.5f * Mathf.sin(Time.time * 0.12f + e.id * 4f), i * 90f + Mathf.sin(Time.time * 0.04f + e.id * 5f) * 28f);
    }),

    laserCharge = new Effect(38f, e -> {
        color(e.color);
        randLenVectors(e.id, e.id % 3 + 1, 1f + 20f * e.fout(), e.rotation, 120f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3f + 1f);
        });
    }),

    laserChargeBegin = new Effect(60f, e -> {
        color(e.color);
        Fill.square(e.x, e.y, e.fin() * 3f, 45f);
    
        color();
        Fill.square(e.x, e.y, e.fin() * 2f, 45f);
    }),

    laserChargeShoot = new Effect(21f, e -> {
        color(e.color, Color.white, e.fout());

        for(var i=0; i<4; i++){
            Drawf.tri(e.x, e.y, 4f * e.fout(), 29f, e.rotation + 90f * i + e.finpow() * 112f);
        }
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

    branchFragHit = new Effect(8f, e -> {
        color(Color.white, Pal.lancerLaser, e.fin());
    
        stroke(0.5f + e.fout());
        Lines.circle(e.x, e.y, e.fin() * 5f);
    
        stroke(e.fout());
        Lines.circle(e.x, e.y, e.fin() * 6f);
    }),

    laserBreakthroughChargeBegin = new Effect(100f, 100f, e -> {
        color(e.color);
        stroke(e.fin() * 3f);
        Lines.circle(e.x, e.y, 4f + e.fout() * 120f);
    
        Fill.circle(e.x, e.y, e.fin() * 23.5f);
    
        randLenVectors(e.id, 20, 50f * e.fout(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fin() * 6f);
        });
    
        color();
    
        Fill.circle(e.x, e.y, e.fin() * 13);
    }),
    
    laserBreakthroughShoot = new Effect(40f, e -> {
        color(e.color);
    
        stroke(e.fout() * 2.5f);
        Lines.circle(e.x, e.y, e.finpow() * 100f);
    
        stroke(e.fout() * 5f);
        Lines.circle(e.x, e.y, e.fin() * 100f);
    
        color(e.color, Color.white, e.fout());
    
        randLenVectors(e.id, 20, 80f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 5f);
        });
    
    
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

    craftingEffect = new Effect(67f, 35f, e -> {
        float value = Mathf.randomSeed(e.id);

        Tmp.v1.trns(value * 360f + ((value + 4f) * e.fin() * 80f), (Mathf.randomSeed(e.id * 126) + 1f) * 34f * (1f - e.finpow()));

        color(UnityPal.laserOrange);
        Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fslope() * 3f, 45f);
        color();
    }),
    
    catastropheCharge = new Effect(70f, e -> {
        float slope = Interp.pow2Out.apply(Mathf.curve(e.fin(), 0f, 0.70f)) - Interp.pow5In.apply(Mathf.curve(e.fin(), 0.85f, 1f));
        float shrink = 1 - Mathf.curve(e.fin(), 0.85f, 1f);
        float foutpow = Interp.pow3Out.apply(e.fout());
        float rotpow = Interp.pow3Out.apply(e.fin());
        int spikes = 7;
        float spread = 50;
        
        color(Color.valueOf("ff9c5a"));
        stroke(e.fin() * 6f);
        Lines.circle(e.x, e.y, foutpow * 28f);

        Fill.circle(e.x, e.y, slope * 6f * (1f + 3f * rotpow) / 2f);
        for(int i = 0; i < spikes; i++){
            float rot = Mathf.lerp((i - (int)(spikes / 2f)) * spread, 0, rotpow);
            Drawf.tri(e.x, e.y, shrink * 6f * (1f + 3f * rotpow), 80f * slope, e.rotation + rot);
        }

        color();
        Fill.circle(e.x, e.y, slope * 3f * (1f + 3f * rotpow) / 2f);
        for(int i = 0; i < spikes; i++){
            float rot = Mathf.lerp((i - (int)(spikes / 2f)) * spread, 0, rotpow);
            Drawf.tri(e.x, e.y, shrink * 3f * (1f + 3f * rotpow), 30f * slope, e.rotation + rot);
        }
    }), 
    
    calamityCharge = new Effect(140f, e -> {
        float slope = Interp.pow2Out.apply(Mathf.curve(e.fin(), 0f, 0.70f)) - Interp.pow5In.apply(Mathf.curve(e.fin(), 0.85f, 1f));
        float shrink = 1 - Mathf.curve(e.fin(), 0.85f, 1f);
        float foutpow = Interp.pow3Out.apply(e.fout());
        float rotpow = Interp.pow3Out.apply(e.fin());
        int spikes = 9;
        float spread = 39f;
        
        color(Color.valueOf("ff9c5a"));
        stroke(e.fin() * 8f);
        Lines.circle(e.x, e.y, foutpow * 45f);

        Fill.circle(e.x, e.y, slope * 8f * (1f + 3f * rotpow) / 2f);
        for(int i = 0; i < spikes; i++){
            float rot = Mathf.lerp((i - (int)(spikes / 2f)) * spread, 0, rotpow);
            Drawf.tri(e.x, e.y, shrink * 8f * (1f + 3f * rotpow), 140f * slope, e.rotation + rot);
        }

        color();
        Fill.circle(e.x, e.y, slope * 4f * (1f + 3f * rotpow) / 2f);
        for(int i = 0; i < spikes; i++){
            float rot = Mathf.lerp((i - (int)(spikes / 2f)) * spread, 0, rotpow);
            Drawf.tri(e.x, e.y, shrink * 4f * (1f + 3f * rotpow), 50f * slope, e.rotation + rot);
        }
    }), 
    
    extinctionCharge = new Effect(210f, e -> {
        float slope = Interp.pow2Out.apply(Mathf.curve(e.fin(), 0f, 0.70f)) - Interp.pow5In.apply(Mathf.curve(e.fin(), 0.85f, 1f));
        float shrink = 1 - Mathf.curve(e.fin(), 0.85f, 1f);
        float foutpow = Interp.pow3Out.apply(e.fout());
        float rotpow = Interp.pow3Out.apply(e.fin());
        int spikes = 13;
        float spread = 27f;
        
        color(Color.valueOf("ff9c5a"));
        stroke(e.fin() * 10f);
        Lines.circle(e.x, e.y, foutpow * 70f);

        Fill.circle(e.x, e.y, slope * 10f * (1f + 3f * rotpow) / 2f);
        for(int i = 0; i < spikes; i++){
            float rot = Mathf.lerp((i - (int)(spikes / 2f)) * spread, 0, rotpow);
            Drawf.tri(e.x, e.y, shrink * 10f * (1f + 3f * rotpow), 200f * slope, e.rotation + rot);
        }

        color();
        Fill.circle(e.x, e.y, slope * 5f * (1f + 3f * rotpow) / 2f);
        for(int i = 0; i < spikes; i++){
            float rot = Mathf.lerp((i - (int)(spikes / 2f)) * spread, 0, rotpow);
            Drawf.tri(e.x, e.y, shrink * 5f * (1f + 3f * rotpow), 80f * slope, e.rotation + rot);
        }
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
    
    surgeSplash = new Effect(40f, 100f, e -> {
        color(Pal.surge);
        stroke(e.fout() * 2);
        circle(e.x, e.y, 4 + e.finpow() * 65);

        color(Pal.surge);

        for(var i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 6, 100 * e.fout(), i*90);
        }

        color();

        for(var i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 3, 35 * e.fout(), i*90);
        }
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

    scarHitSmall = new Effect(14f, e -> {
        color(Color.white, UnityPal.scarColor, e.fin());
        e.scaled(7f, s -> {
            stroke(0.5f + s.fout());
            circle(e.x, e.y, s.fin() * 5f);
        });
        stroke(0.5f + e.fout());
        randLenVectors(e.id, 5, e.fin() * 15f, (x, y) -> lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3f + 1f));
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
        z(Layer.effect - 0.00001f);
        color(e.color);
        stroke(e.fout() * 1.5f);
        polySeg(60, 0, (int)(60 * (1 - (e.rotation - Time.time) / whenReady)), u.x, u.y, 8f, 0f);
    }),

    //^ this but better
    waitEffect = new Effect(30f, e -> {
        if(e.data instanceof WaitEffectData data){
            if(data.unit() == null || !data.unit().isValid() || data.unit().dead) return;

            z(Layer.effect - 0.00001f);
            color(e.color);
            stroke(e.fout() * 1.5f);
            polySeg(60, 0, (int)(60f * data.progress()), data.unit().x, data.unit().y, 8f, 0f);
        }
    }),

    waitEffect2 = new Effect(30f, e -> {
        if(e.data instanceof WaitEffectData data){
            if(data.unit() == null || !data.unit().isValid() || data.unit().dead) return;

            z(Layer.effect - 0.00001f);
            color(e.color);
            stroke(e.fout() * 1.5f);
            polySeg(90, 0, (int)(90f * data.progress()), data.unit().x, data.unit().y, 12f, 0f);
        }
    }),

    ringFx = new Effect(25f, e -> {
        if(!(e.data instanceof Unit)) return;
        Unit u = (Unit)e.data;
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
        if(!(e.data instanceof Unit)) return;
        Unit u = (Unit)e.data;
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
        if(!(e.data instanceof Unit)) return;
        Unit u = (Unit)e.data;
        if(!u.isValid() || u.dead) return;
        color(Color.white, e.color, e.fin());
        stroke(e.fout() * 2.5f);
        square(u.x, u.y, e.fin() * 18f, 45f);
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

    blueTriangleTrail = new Effect(50f, e -> {
        Draw.color(Color.white, Pal.lancerLaser, e.fin());
        Fill.poly(e.x, e.y, 3, 4f * e.fout(), -90f);
    }),

    hitAdvanceFlame = new Effect(15f, e -> {
        Draw.color(UnityPal.advance, UnityPal.advanceDark, e.fin());

        Angles.randLenVectors(e.id, 2, e.finpow() * 17f, e.rotation, 60f, (x, y) -> {
            Fill.poly(e.x + x, e.y + y, 6, 3f + e.fout() * 3f, e.rotation);
        });
    }),

    advanceFlameTrail = new Effect(27f, e -> {
        Draw.color(UnityPal.advance, UnityPal.advanceDark, e.fin());
        float rot = Mathf.randomSeed(e.id, -1, 1) * 270f;

        Fill.poly(e.x, e.y, 6, e.fout() * 4.1f, e.rotation + e.fin() * rot);
    }),

    advanceFlameSmoke = new Effect(13f, e -> {
        Draw.color(Color.valueOf("4d668f77"), Color.valueOf("35455f00"), e.fin());
        float rot = Mathf.randomSeed(e.id, -1, 1) * 270f;

        Angles.randLenVectors(e.id, 2, e.finpow() * 13f, e.rotation, 60f, (x, y) -> Fill.poly(e.x + x, e.y + y, 6, e.fout() * 4.1f, e.rotation + e.fin() * rot));
    }),

    teamConvertedEffect = new Effect(18, e -> {
        Draw.color(UnityPal.advance, Color.white, e.fin());
        Fill.square(e.x, e.y, 0.1f + e.fout() * 2.8f, 45f);
    }),

    blueBurnEffect = new Effect(35f, e -> {
        Draw.color(UnityPal.advance, UnityPal.advanceDark, e.fin());

        Angles.randLenVectors(e.id, 3, 2 + e.fin() * 7, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 0.1f + e.fout() * 1.4f);
        });
    }),

    tenmeikiriCut = new Effect(20f, 150f, e -> {
        if(!(e.data instanceof Float)) return;
        Draw.color(UnityPal.scarColor, UnityPal.endColor, e.fin());
        Drawf.tri(e.x, e.y, 12f * e.fout(), (float)e.data, e.rotation);
        Drawf.tri(e.x, e.y, 12f * e.fout(), (float)e.data, e.rotation + 180f);
    }),

    tenmeikiriChargeEffect = new ParentEffect(40f, e -> {
        Angles.randLenVectors(e.id, 2, 10f, 90f, (x, y) -> {
            float angle = Mathf.angle(x, y);
            Draw.color(UnityPal.scarColor, UnityPal.endColor, e.fin());
            Lines.stroke(1.5f);
            Lines.lineAngleCenter(e.x + (x * e.fout()), e.y + (y * e.fout()), angle, e.fslope() * 13f);
        });
    }),

    tenmeikiriTipHit = new Effect(27f, e -> {
        Angles.randLenVectors(e.id, 8, 90f * e.fin(), e.rotation, 80f, (x, y) -> {
            float angle = Mathf.angle(x, y);
            Draw.color(UnityPal.scarColor, UnityPal.endColor, e.fin());
            Lines.stroke(1.5f);
            Lines.lineAngleCenter(e.x + x, e.y + y, angle, e.fslope() * 13f);
        });
    }),

    tenmeikiriChargeBegin = new ParentEffect(158f, e -> {
        Color[] colors = {UnityPal.scarColor, UnityPal.endColor, Color.white};
        for(int ii = 0; ii < 3; ii++){
            float s = (3 - ii) / 3f;
            float width = Mathf.clamp(e.time / 80f) * (20f + Mathf.absin(Time.time + (ii * 1.4f), 1.1f, 7f)) * s;
            float length = e.fin() * (100f + Mathf.absin(Time.time + (ii * 1.4f), 1.1f, 11f)) * s;
            Draw.color(colors[ii]);
            for(int i : Mathf.signs){
                float rotation = e.rotation + (i * 90f);
                Drawf.tri(e.x, e.y, width, length * 0.5f, rotation);
            }
            Drawf.tri(e.x, e.y, width, length * 1.25f, e.rotation);
        }
    }),

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

        Fill.square(e.x, e.y, e.fout() * e.rotation * (Vars.tilesize / 2f));

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

        Utils.simpleUnitDrawer((Unit)e.data, false);

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
                Draw.color(Tmp.c1.set(colors[i]).mul(1f, 1f + Utils.offsetSinB(0f, 5f), 1f + Utils.offsetSinB(90f, 5f), 1f));
            }

            Fill.circle(a.getX(), a.getY(), strokes[i] * 4f * width * e.fout());
            Fill.circle(Tmp.v1.x, Tmp.v1.y, strokes[i] * 4f * width * e.fout());

            Lines.stroke(strokes[i] * 4f * width * e.fout());
            Lines.line(a.getX(), a.getY(), Tmp.v1.x, Tmp.v1.y);
        }
        Draw.z(oz);
    }),

    devourerShootEffect = new Effect(41f, e -> {
        Color[] colors = {UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};

        for(int i = 0; i < colors.length; i++){
            Draw.color(colors[i]);
            float size = Math.max(0f, (e.fslope() * 35f) - (i * ((7f + (1f - e.fslope())) * 2f)));
            Fill.circle(e.x, e.y, size);

            int finalI = i;
            Angles.randLenVectors(e.id, 13, 140f, (x, y) -> {
                float s = 3.4f + (colors.length - finalI);
                Fill.circle(e.x + (x * (1f - e.finpow())), e.y + (y * (1f - e.finpow())), e.fin() * 2f * s);
            });
        }
    }),

    rainbowTextureTrail = new Effect(80f, e -> {
        if(!(e.data instanceof RainbowUnitType t)) return;
        Draw.blend(Blending.additive);
        Draw.color(Tmp.c1.set(Color.red).shiftHue(e.time * 4f).a(Mathf.clamp(e.fout() * 1.5f)));
        Draw.rect(t.trailRegion, e.x, e.y, e.rotation - 90f);
        Draw.blend();
    }),

    kamiBulletDespawn = new Effect(60f, e -> {
        float size = Mathf.clamp(e.rotation, 0f, 15f);

        Draw.blend(Blending.additive);
        Draw.color(Tmp.c1.set(Color.red).shiftHue(((e.time + Time.time) / 2f) * 3f));
        Lines.stroke(2f * e.fout());
        Lines.circle(e.x, e.y, (e.finpow() * size) + (size / 2f));
        Lines.stroke(e.fout());
        Lines.circle(e.x, e.y, (e.finpow() * (size / 2f)) + size);
        Draw.blend();
    }),

    kamiEoLCharge = new Effect(60f, e -> {
        if(!(e.data instanceof Unit u)) return;
        Draw.blend(Blending.additive);
        for(int i = 0; i < 2; i++){
            float angle = i * 360f / 2f;
            Draw.color(Tmp.c1.set(Color.red).shiftHue((e.time * 5f) + angle).a(Mathf.clamp(e.fout() * 1.5f)));
            Tmp.v1.trns(angle + (e.fin() * 180f), 150f * e.fslope()).add(u);
            Draw.rect(u.type.region, Tmp.v1.x, Tmp.v1.y, u.rotation - 90f);
        }
        for(int i = 0; i < 4; i++){
            float angle = i * 360f / 4f;
            Draw.color(Tmp.c1.set(Color.red).shiftHue((e.time * 5f) + angle).a(Mathf.clamp(e.fout() * 1.5f)));
            Tmp.v1.trns(angle + (e.fin() * -270f), 100f * e.fslope()).add(u);
            Draw.rect(u.type.region, Tmp.v1.x, Tmp.v1.y, u.rotation - 90f);
        }
        Draw.blend();
    }),

    kamiWarningLine = new Effect(120f, 670f * 2f, e -> {
        if(e.data == null) return;
        Position[] data = (Position[])e.data;
        Position a = data[0];
        Position b = data[1];

        Draw.color(Tmp.c1.set(Color.red).shiftHue(e.time * 3f));
        Lines.stroke(Mathf.clamp(e.fslope() * 2f) * 1.2f);
        Lines.line(a.getX(), a.getY(), b.getX(), b.getY());
    }),

    pointBlastLaserEffect = new Effect(23f, 600f, e -> {
        if(!(e.data instanceof PointBlastLaserBulletType btype)) return;

        for(int i = 0; i < btype.laserColors.length; i++){
            Draw.color(btype.laserColors[i]);
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
    }),

    flareEffect = new Effect(18f, e -> {
        color(Pal.lightFlame, Pal.darkFlame, e.fin());
        Lines.stroke(e.fout());

        randLenVectors(e.id, 6, 8f * e.finpow(), e.rotation, 18f, (x, y) -> {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 12f * e.fin());
        });
    }),

    supernovaStar = new Effect(45f, e -> {
        if(e.data instanceof Float data){
            float a = Mathf.randomSeed(e.id, 360f);
            float d = 0.5f + e.fout() * 0.5f;

            float r = data.floatValue();

            color(Pal.lancerLaser);
            alpha(0.6f * e.fout());
            Fill.circle(e.x + trnsx(a, d), e.y + trnsy(a, d), r);
        }
    }),

    supernovaCharge = new Effect(20f, e -> {
        if(e.data instanceof Float data){
            float r = data.floatValue();

            color(Pal.lancerLaser);
            alpha(0.6f * e.fout());
            Fill.circle(e.x, e.y, Mathf.lerp(0.2f, 1f, e.fout()) * r);
        }
    }),

    supernovaChargeBegin = new Effect(27f, e -> {
        if(e.data instanceof Float data){
            float r = data.floatValue();
            randLenVectors(e.id, (int)(2f * r), 1f + 27f * e.fout(), (x, y) -> {
                color(Pal.lancerLaser);
                lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), (1f + e.fslope() * 6f) * r);
            });
        }
    }),

    supernovaStarHeatwave = new Effect(40f, e -> {
        color(Pal.lancerLaser);
        stroke(e.fout());
        circle(e.x, e.y, 120f * e.fin());
        circle(e.x, e.y, 120f * e.finpow() * 0.6f);
    }),

    supernovaChargeStar = new Effect(30f, e -> {
        if(e.data instanceof Float data){
            float r = data.floatValue();

            color(Pal.lancerLaser);
            alpha(e.fin() * 2f * r);
            circle(e.x, e.y, 150f * Interp.pow2Out.apply(e.fout()) * Mathf.lerp(0.1f, 1f, r));
        }
    }),

    supernovaStarDecay = new Effect(56f, e -> {
        randLenVectors(e.id, 2, 36f * e.finpow(), (x, y) -> {
            color(Pal.lancerLaser);
            Fill.circle(e.x + x, e.y + y, 2.2f * e.fout());
        });
    }),

    supernovaChargeStar2 = new Effect(27f, e -> {
        if(e.data instanceof Float data){
            float r = data.floatValue();
            randLenVectors(e.id, (int)(3f * r), e.fout() * ((90f + r * 150f) * (0.3f + Mathf.randomSeed(e.id, 0.7f))), (x, y) -> {
                color(Pal.lancerLaser);
                Fill.circle(e.x + x, e.y + y, 2f * e.fin());
            });
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
            TextureRegion region = unit.icon(Cicon.full);
            float w = region.width * scl * e.fout();
            float h = region.height * scl * e.fout();

            rect(region, e.x, e.y, w, h, e.rotation - 90);
            blend();
        }
    }),

    distortFx = new Effect(18, e -> {
        if(!(e.data instanceof Float)) return;
        Draw.color(Pal.lancerLaser, Pal.place, e.fin());
        Fill.square(e.x, e.y, 0.1f + e.fout() * 2.5f, (float)e.data);
    }),

    distSplashFx = new Effect(80, e -> {
        if(!(e.data instanceof Float[])) return;
        Draw.color(Pal.lancerLaser, Pal.place, e.fin());
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

    ricochetTrailSmall = new Effect(12f, e -> {
        randLenVectors(e.id, 4, e.fout() * 3.5f, (x, y) -> {
            float w = 0.3f + e.fout();

            Draw.color(e.color);
            Fill.rect(e.x + x, e.y + y, w, w, 45f);
        });
    }),

    ricochetTrailMedium = new Effect(16f, e -> {
        randLenVectors(e.id, 5, e.fout() * 5f, (x, y) -> {
            float w = 0.3f + e.fout() * 1.3f;

            Draw.color(e.color);
            Fill.rect(e.x + x, e.y + y, w, w, 45f);
        });
    }),

    ricochetTrailBig = new Effect(20f, e -> {
        randLenVectors(e.id, 6, e.fout() * 6.5f, (x, y) -> {
            float w = 0.3f + e.fout() * 1.7f;

            Draw.color(e.color);
            Fill.rect(e.x + x, e.y + y, w, w, 45f);
        });
    }),

    plated = new Effect(30f, e -> {
        Draw.color(e.color);
        Draw.z(Layer.effect);
        Lines.circle(e.x, e.y, e.fout() * (float) e.data);
        Draw.reset();
    });
}

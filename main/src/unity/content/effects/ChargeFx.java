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
import unity.content.*;
import unity.entities.effects.*;
import unity.graphics.*;
import unity.graphics.MultiTrail.*;
import unity.util.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static mindustry.Vars.*;

public class ChargeFx{
    private static final Color tmpCol = new Color();

    public static Effect

    greenLaserChargeSmallParent = new ParentEffect(40f, 100f, e -> {
        color(Pal.heal);
        stroke(e.fin() * 2f);
        Lines.circle(e.x, e.y, e.fout() * 50f);
    }),

    greenLaserChargeParent = new ParentEffect(80f, 100f, e -> {
        color(Pal.heal);
        stroke(e.fin() * 2f);
        Lines.circle(e.x, e.y, 4f + e.fout() * 100f);

        Fill.circle(e.x, e.y, e.fin() * 20);

        randLenVectors(e.id, 20, 40f * e.fout(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fin() * 5f);
            Drawf.light(e.x + x, e.y + y, e.fin() * 15f, Pal.heal, 0.7f);
        });

        color();

        Fill.circle(e.x, e.y, e.fin() * 10);
        Drawf.light(e.x, e.y, e.fin() * 20f, Pal.heal, 0.7f);
    }),

    sagittariusCharge = new Effect(2f * 60f, e -> {
        float size = e.fin() * 15f;
        color(Pal.heal);
        Fill.circle(e.x, e.y, size);
        MathU.randLenVectors(e.id * 9999L, 15, e.fout(), 0.5f, 0.6f, 0.2f,
        f -> f * f * f * 90f, (ex, ey, fin) -> {
            float fout = 1f - fin;
            if(fin < 0.9999) Fill.circle(ex + e.x, ey + e.y, fout * 11f);
        });
        float f = Mathf.curve(e.fin(), 0.4f);

        if(f > 0.0001f){
            for(int s : Mathf.signs){
                Drawf.tri(e.x, e.y, Interp.pow2Out.apply(f) * 15f * 1.22f, f * f * 80f, e.rotation + 90f * s);
            }
        }

        color(Color.white);
        Fill.circle(e.x, e.y, size * 0.5f);
    }).followParent(true).rotWithParent(true),

    tenmeikiriChargeEffect = new ParentEffect(40f, e -> {
        Angles.randLenVectors(e.id, 2, 10f, 90f, (x, y) -> {
            float angle = Mathf.angle(x, y);
            color(UnityPal.scarColor, UnityPal.endColor, e.fin());
            Lines.stroke(1.5f);
            Lines.lineAngleCenter(e.x + (x * e.fout()), e.y + (y * e.fout()), angle, e.fslope() * 13f);
        });
    }),

    tenmeikiriChargeBegin = new ParentEffect(158f, e -> {
        Color[] colors = {UnityPal.scarColor, UnityPal.endColor, Color.white};
        for(int ii = 0; ii < 3; ii++){
            float s = (3 - ii) / 3f;
            float width = Mathf.clamp(e.time / 80f) * (20f + Mathf.absin(Time.time + (ii * 1.4f), 1.1f, 7f)) * s;
            float length = e.fin() * (100f + Mathf.absin(Time.time + (ii * 1.4f), 1.1f, 11f)) * s;
            color(colors[ii]);
            for(int i : Mathf.signs){
                float rotation = e.rotation + (i * 90f);
                Drawf.tri(e.x, e.y, width, length * 0.5f, rotation);
            }
            Drawf.tri(e.x, e.y, width, length * 1.25f, e.rotation);
        }
    }),

    devourerChargeEffect = new ParentEffect(41f, e -> {
        Color[] colors = {UnityPal.scarColor, UnityPal.endColor, Color.white};

        for(int i = 0; i < colors.length; i++){
            color(colors[i]);
            float scl = (colors.length - (i / 1.25f)) * (17f / colors.length);
            float width = (35f / (1f + (i / Mathf.pi))) * e.fin();
            float spikeIn = e.fslope() * scl * 1.5f;

            UnityDrawf.shiningCircle(e.id * 241, Time.time + (i * 3f), e.x, e.y, scl * e.fin(), 9, 12f, width, spikeIn);
        }
    }),

    oppressionCharge = new Effect(5f * 60f, 2530f * 2f, e -> {
        Rand r = Utils.seedr, r2 = Utils.seedr2, r3 = Utils.seedr3;
        r.setSeed(e.id * 9999L);

        float off = 140f / e.lifetime;
        float off2 = 70f / e.lifetime;

        float fin1 = e.time >= 150f ? 1f : e.time / 150f;
        float fin2 = e.time >= 60f ? 1f : e.time / 60f;

        float time = Time.time;
        color(UnityPal.scarColor);
        for(int i = 0; i < 11; i++){
            float f = (i / 10f) * off2;
            float cf = Mathf.curve(e.fin(), f, (1f - off2) + f);
            float cfo = 1f - cf;
            float rot = e.rotation + (r.nextFloat() - r.nextFloat()) * 6f;
            float len = r.random(75f, 210f) * Interp.pow2Out.apply(MathU.slope(cf, 0.75f));
            float wid = (len / 15f) * cf * 2f * r.random(0.8f, 1.2f);
            float trns = r.random(2530f - len * 2f) + len;
            if(cf <= 0f || cf >= 1f) continue;
            Vec2 v = Tmp.v1.trns(rot, trns * Interp.pow3In.apply(cfo)).add(e.x, e.y);
            UnityDrawf.diamond(v.x + Mathf.range(4f) * cf, v.y + Mathf.range(4f) * cf, wid, len, rot);
        }
        if(e.time > 145f){
            float fin3 = e.time - 145f >= 140f ? 1f : (e.time - 145f) / 140f;
            r3.setSeed(e.id * 9999L + 781);
            float spikef = Mathf.clamp((e.time - 145f) / 20f, 0f, 13f);
            int spikei = Mathf.ceil(spikef);
            for(int i = 0; i < spikei; i++){
                float spikem = spikef >= 13f || i < spikei - 1 ? 1f : (spikef % 1f);
                float d = r3.random(25f, 45f);
                float timeOffset = r3.random(d);
                float f = ((time + timeOffset) % d) / d;
                float fo = 1f - f;
                int timeSeed = Mathf.floor((time + timeOffset) / d) + r3.nextInt();
                float offs = 0.33f;
                float lt = f < offs ? Interp.pow2In.apply(f / offs) : 1f - (f - offs) / (1f - offs);

                r2.setSeed(timeSeed);
                float rot = r2.random(360f) + r2.range(5f) * f;
                float trns = (r2.random(8f, 13f) + r2.random(5f, 10f) * e.fin());
                float w = r2.random(17f, 30f) + r2.random(8f) * fin3 * Mathf.curve(fo, 0f, 0.5f);
                float l = r2.random(75f, 180f) * lt * spikem;
                Tmp.v1.trns(rot, trns).add(e.x, e.y);
                UnityDrawf.diamond(Tmp.v1.x, Tmp.v1.y, w, l, 0.4f, rot);
            }
            float fin4 = (e.time - 145f) / (e.lifetime - 145f);
            UnityDrawf.diamond(e.x, e.y, 17f * Interp.pow2Out.apply(Mathf.curve(fin4, 0f, 0.2f)), (160f + Mathf.absin(8f, 6f)) * Interp.pow2.apply(fin4), e.rotation + 90f);
        }
        for(int i = 0; i < 35; i++){
            float d = r.random(10f, 30f);
            float timeOffset = r.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + r.nextInt();
            float f = ((time + timeOffset) % d) / d;
            float fo = 1f - f;
            float trv = 1f - (f < 0.75f ? Interp.pow3Out.apply(f / 0.75f) * 0.75f : Interp.pow2In.apply((f - 0.75f) / 0.25f) * 0.25f + 0.75f);

            r2.setSeed(timeSeed);
            float rot = r2.random(360f);
            float trns = (r2.random(15f, 65f) + r2.random(15f, 75f) * e.fin()) * trv;
            float trns2 = r2.random(200f, 900f) * fo * (1f - fin1);
            float rad = (r2.random(10f, 22f) + 11f * e.fin()) * fin2 * Interp.pow2Out.apply(MathU.slope(f, 0.75f));
            if(trns2 > 0){
                Tmp.v1.trns(e.rotation + r2.range(4f), trns2).add(e.x, e.y);
            }else{
                Tmp.v1.set(e.x, e.y);
            }
            color(UnityPal.scarColor, Color.black, Mathf.curve(f, 0.35f, 0.75f));
            Vec2 v = Tmp.v2.trns(rot, trns).add(Tmp.v1);
            Fill.square(v.x, v.y, rad, 45f);
        }

        color(UnityPal.scarColor);
        for(int i = 0; i < 22; i++){
            float f = (i / 21f) * off;
            float cf = Mathf.curve(e.fin(), f, (1f - off) + f);
            float cfo = 1f - cf;
            float rot = e.rotation + (r.nextFloat() - r.nextFloat()) * 20f;
            float len = r.random(300f, 800f);
            float trns = r.random(2530f - len) * cfo * cfo;
            if(cf <= 0f || cf >= 1f) continue;
            Vec2 v = Tmp.v1.trns(rot, trns).add(e.x, e.y);
            stroke(3f);
            lineAngle(v.x, v.y, rot, len * Mathf.slope(cfo * cfo), false);
        }
        float t = e.time < 3.75f * 60f ? 0f : Mathf.clamp((e.time - 3.75f * 60f) / 30f);
        float length = Interp.pow3.apply(Mathf.clamp(e.time / 20f)) * 2530f;
        color(UnityPal.scarColor, Color.black, t);
        stroke(5f);
        lineAngle(e.x, e.y, e.rotation, length);
        if(t > 0f){
            r3.setSeed(e.id * 9999L + 613);
            float dr = 3.75f * 60f;
            float partf = Mathf.clamp((e.time - dr) / (e.lifetime - dr)) * 9f;
            int parti = Mathf.ceil(partf);

            for(int j = 0; j < parti; j++){
                float partm = partf >= 9f || j < parti - 1 ? 1f : (partf % 1f);

                for(int i = 0; i < 9; i++){
                    float d = r3.random(7f, 11f);
                    float timeOffset = r3.random(d);
                    int timeSeed = Mathf.floor((time + timeOffset) / d) + r3.nextInt();
                    float f = ((time + timeOffset) % d) / d;

                    r2.setSeed(timeSeed);
                    float l = r2.random(100f, 200f) * Interp.pow2Out.apply(Mathf.curve(f, 0f, 0.5f)) * partm;
                    float w = r2.random(9f, 19f) * MathU.slope(f, 0.8f) * partm * t;

                    float trns = r2.random(2530f - l * 2f) + l + r2.range(3f) * f;
                    float of = (r2.nextFloat() - r2.nextFloat()) * 35f * Interp.pow3Out.apply(1f - f) * (0.5f + t * 0.5f);
                    //float scl = r2.random(5f, 10f) * t * partm * MathU.slope(f, 0.8f);
                    Tmp.v1.trns(e.rotation, trns, of).add(e.x, e.y);
                    color(UnityPal.scarColor, Color.black, Mathf.curve(f, 0.2f, 0.75f));
                    UnityDrawf.diamond(Tmp.v1.x, Tmp.v1.y, w, l, e.rotation);
                    //Fill.square(Tmp.v1.x, Tmp.v1.y, scl, 45f);
                }
            }
        }
        if(e.time < 3.75f * 60f){
            float t2 = Mathf.clamp((3.75f * 60f - e.time) / 30f);

            r3.setSeed(e.id * 9999L + 613);
            color(UnityPal.scarColor);
            for(int i = 0; i < 30; i++){
                float d = r3.random(18f, 24f);
                float timeOffset = r3.random(d);
                int timeSeed = Mathf.floor((time + timeOffset) / d) + r3.nextInt();
                float f = ((time + timeOffset) % d) / d;

                r2.setSeed(timeSeed);
                float trns = r2.random(length) + r2.range(2f) * f;
                float of = (r2.nextFloat() - r2.nextFloat()) * 65f * Interp.pow3In.apply(f) * (0.5f + t2 * 0.5f);
                float scl = r2.random(3f, 8f) * t2 * MathU.slope(f, 0.25f);
                Tmp.v1.trns(e.rotation, trns, of).add(e.x, e.y);
                Fill.square(Tmp.v1.x, Tmp.v1.y, scl, 45f);
            }
        }
    }).followParent(true).rotWithParent(true),

    wBosonChargeBeginEffect = new Effect(38f, e -> {
        color(UnityPal.lightEffect, Pal.lancerLaser, e.fin());
        Fill.circle(e.x, e.y, 3f + e.fin() * 6f);
        color(Color.white);
        Fill.circle(e.x, e.y, 1.75f + e.fin() * 5.75f);
    }),

    wBosonChargeEffect = new Effect(24f, e -> {
        color(UnityPal.lightEffect, Pal.lancerLaser, e.fin());
        stroke(1.5f);

        randLenVectors(e.id, 2, (1f - e.finpow()) * 50f, (x, y) -> {
            float a = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, a, Mathf.sin(e.finpow() * 3f, 1f, 8f) + 1.5f);
            Fill.circle(e.x + x, e.y + y, 2f + e.fin() * 1.75f);
        });
    }),

    ephemeronCharge = new Effect(80f, e -> {
        color(Pal.lancerLaser);
        UnityDrawf.shiningCircle(e.id, Time.time, e.x, e.y, e.fin() * 9.5f, 6, 25f, 20f, 3f * e.fin());
        color(Color.white);
        UnityDrawf.shiningCircle(e.id, Time.time, e.x, e.y, e.fin() * 7.5f, 6, 25f, 20f, 2.5f * e.fin());
    }),

    tendenceCharge = new CustomStateEffect(() -> {
        class State extends EffectState{
            @Override
            public void remove(){
                if(data instanceof TrailHold[] data) for(TrailHold trail : data) Fx.trailFade.at(x, y, trail.width, UnityPal.monolithLight, trail.trail.copy());
                super.remove();
            }
        } return Pools.obtain(State.class, State::new);
    }, 40f, e -> {
        if(!(e.data instanceof TrailHold[] data)) return;

        color(UnityPal.monolith, UnityPal.monolithLight, e.fin());
        randLenVectors(e.id, 8, 8f + e.foutpow() * 32f, (x, y) ->
            Fill.circle(e.x + x, e.y + y, 0.5f + e.fin() * 2.5f)
        );

        color();
        for(TrailHold hold : data){
            Tmp.v1.set(hold.x, hold.y);
            Tmp.v2.trns(Tmp.v1.angle() - 90f, Mathf.sin(hold.width * 2.6f, hold.width * 8f * Interp.pow2Out.apply(e.fslope())));
            Tmp.v1.scl(e.foutpowdown()).add(Tmp.v2).add(e.x, e.y);

            float w = hold.width * e.fin();
            if(!state.isPaused()) hold.trail.update(Tmp.v1.x, Tmp.v1.y, w);

            tmpCol.set(UnityPal.monolith).lerp(UnityPal.monolithLight, e.finpowdown());
            hold.trail.drawCap(tmpCol, w);
            hold.trail.draw(tmpCol, w);
        }

        stroke(Mathf.curve(e.fin(), 0.5f) * 1.4f, UnityPal.monolithLight);
        Lines.circle(e.x, e.y, e.fout() * 64f);
    }){
        @Override
        protected EffectState inst(float x, float y, float rotation, Color color, Object data){
            TrailHold[] trails = new TrailHold[12];
            for(int i = 0; i < trails.length; i++){
                Tmp.v1.trns(Mathf.random(360f), Mathf.random(24f, 64f));
                trails[i] = new TrailHold(Utils.with(Trails.soul(26), t -> {
                    if(t.trails[t.trails.length - 1].trail instanceof TexturedTrail tr){
                        tr.trailChance = 0.1f;
                    }
                }), Tmp.v1.x, Tmp.v1.y, Mathf.random(1f, 2f));
            }

            EffectState state = super.inst(x, y, rotation, color, data);
            state.data = trails;
            return state;
        }
    }.followParent(true);
}

package unity.content.effects;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
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
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static mindustry.Vars.*;

public class ChargeFx{
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

    ephmeronCharge = new Effect(80f, e -> {
        color(Pal.lancerLaser);
        UnityDrawf.shiningCircle(e.id, Time.time, e.x, e.y, e.fin() * 9.5f, 6, 25f, 20f, 3f * e.fin());
        color(Color.white);
        UnityDrawf.shiningCircle(e.id, Time.time, e.x, e.y, e.fin() * 7.5f, 6, 25f, 20f, 2.5f * e.fin());
    }),

    tendenceCharge = new Effect(32f, e -> {
        if(!(e.data instanceof TrailHold[] data)) return;

        color(UnityPal.monolithDark, UnityPal.monolith, e.fin());
        alpha(e.fin());
        randLenVectors(e.id, 8, e.foutpow() * 20f, (x, y) ->
            Fill.circle(e.x + x, e.y + y, 1.5f + e.fin() * 4.5f)
        );

        color();
        for(TrailHold hold : data){
            Tmp.v1.set(hold.x, hold.y).sub(e.x, e.y);
            Tmp.v2.trns(Tmp.v1.angle(), Mathf.sin(hold.width * 0.3f, hold.width * 4f * e.fin()));

            Tmp.v1.setLength(1f - e.finpowdown()).add(Tmp.v2);
            float x = e.x + Tmp.v1.x, y = e.y + Tmp.v1.y;

            if(!state.isPaused()) hold.trail.update(x, y, hold.width);
            hold.trail.draw(UnityPal.monolithLight, hold.width);
        }

        stroke(e.fin(), UnityPal.monolithLight);
        Lines.circle(e.x, e.y, e.fout() * 32f);
    }){
        boolean initialized;
        final int trailAmount = 12;
        final int trailLength = 12;

        @Override
        public void at(float x, float y, float rotation, Color color){
            create(x, y, rotation, color, null);
        }

        @Override
        public void at(float x, float y, float rotation, Color color, Object data){
            create(x, y, rotation, color, data);
        }

        void create(float x, float y, float rotation, Color color, Object data){
            if(headless || !Core.settings.getBool("effects")) return;

            if(Core.camera.bounds(Tmp.r1).overlaps(Tmp.r2.setCentered(x, y, clip))){
                if(!initialized){
                    initialized = true;
                    init();
                }

                if(startDelay <= 0f){
                    inst(x, y, rotation, color, data);
                }else{
                    Time.runTask(startDelay, () -> inst(x, y, rotation, color, data));
                }
            }
        }

        void inst(float x, float y, float rotation, Color color, Object data){
            CustomEffectState entity = Pools.obtain(CustomEffectState.class, CustomEffectState::new);
            entity.effect = this;
            entity.rotation = baseRotation + rotation;
            entity.data = createTrails();
            entity.lifetime = lifetime;
            entity.set(x, y);
            entity.color.set(color);
            if(followParent && data instanceof Posc p){
                entity.parent = p;
                entity.rotWithParent = rotWithParent;
            }

            entity.add();
        }

        TrailHold[] createTrails(){
            TrailHold[] trails = new TrailHold[trailAmount];
            for(int i = 0; i < trails.length; i++){
                Tmp.v1.setLength(Mathf.random(16f, 32f)).setToRandomDirection();
                trails[i] = new TrailHold(new TexturedTrail(Core.atlas.find("unity-phantasmal-trail"), trailLength){{
                    fadeAlpha = 0.5f;
                    blend = Blending.additive;
                }}, Tmp.v1.x, Tmp.v1.y, Mathf.random(1f, 2f));
            }
            return trails;
        }

        static class CustomEffectState extends EffectState{
            @Override
            public void remove(){
                if(data instanceof TrailHold[] data) for(TrailHold trail : data) Fx.trailFade.at(x, y, trail.width, trail.trail);
                super.remove();
            }
        }
    }.followParent(true);
}

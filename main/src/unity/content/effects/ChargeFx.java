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
                trails[i] = new TrailHold(new TexturedTrail(Core.atlas.find("unity-phantasmal-trail"), 8){{
                    shrink = 1f;
                    fadeAlpha = 0.5f;
                    blend = Blending.additive;
                }}, Tmp.v1.x, Tmp.v1.y, Mathf.random(1f, 2f));
            }

            EffectState state = super.inst(x, y, rotation, color, data);
            state.data = trails;
            return state;
        }
    }.followParent(true);
}

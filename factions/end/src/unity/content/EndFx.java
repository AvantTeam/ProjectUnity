package unity.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.graphics.*;
import unity.util.*;

public class EndFx{
    private final static FloatSeq floatSeq = new FloatSeq();
    private final static Rand r = new Rand();
    public static Effect endgameEyeLaser = new Effect(40f, 750f * 2f, e -> {
        if(!(e.data instanceof LaserEffectData data)) return;
        Position a = data.a, b = data.b;
        float scl = e.rotation;
        //int l = Math.max((int)(a.dst(b) / 60), 3);
        r.setSeed(e.id * 9999L);

        e.lifetime = 40f * scl;

        if(scl >= 0.9f){
            floatSeq.clear();
            int l = Math.max((int)(a.dst(b) / 60), 3);
            float offset = 0f;

            for(int i = 0; i < l; i++){
                float ra = r.range(3f);
                floatSeq.add(offset);
                offset += ra;
            }
            Lines.beginLine();
            for(int i = 0; i < l; i++){
                float val = floatSeq.get(i) - (offset / l) * i;
                Tmp.v2.trns(a.angleTo(b) + 90f, val * 4f * e.fout());
                Vec2 v = Tmp.v1.set(a).lerp(b, (i / (l - 1f))).add(Tmp.v2);
                Lines.linePoint(v);
            }
            Draw.color(EndPal.endMid);
            Lines.stroke(2.5f * e.fout());
            Lines.endLine();

            r.setSeed(e.id * 9999L);
        }

        float rad = (1.75f + Mathf.absin(6f, 1f)) * scl * e.fout();
        for(int i = 0; i < 3; i++){
            Color c = i == 0 ? EndPal.endMid : (i == 1 ? EndPal.endLight : Color.black);
            Draw.color(c);

            Lines.stroke(3f * rad);
            Lines.line(a.getX(), a.getY(), b.getX(), b.getY(), false);
            Fill.circle(a.getX(), a.getY(), 2f * rad);
            Fill.circle(b.getX(), b.getY(), 2f * rad);

            rad *= 0.65f;
        }

        float offset = r.random(0.4f, 0.7f);
        for(int i = 0; i < 15; i++){
            float of = r.nextFloat();
            float f = Mathf.curve(e.fin(), offset * of, (1f - offset) + offset * of);
            float f2 = Interp.pow3Out.apply(Mathf.curve(e.fin(), 0f, 0.5f));

            Vec2 v = Tmp.v1.trns(r.random(360f), r.nextFloat() * 10f * f2).add(a.getX(), a.getY());
            v.lerp(b, f * f);
            Draw.color(Color.black, EndPal.endMid, f);
            Fill.square(v.x, v.y, 2.5f * Interp.pow3Out.apply(1f - f) * f2 * scl * r.random(0.3f, 1f), 45f);
        }
    }).layer(Layer.flyingUnit),

    endRingHit = new Effect(30f, 300f, e -> {
        float finpow = e.finpow();
        r.setSeed(e.id * 9999L);
        Draw.color(EndPal.endMid);
        Drawf.tri(e.x, e.y, 15f * e.fout(), 75f * finpow, e.rotation);
        Drawf.tri(e.x, e.y, 15f * e.fout(), 75f * finpow, e.rotation + 180f);

        Draw.blend(Blending.additive);
        Lines.stroke(2.5f);
        for(int i = 0; i < 20; i++){
            float off = r.random(Mathf.PI),
            c1 = (Mathf.sin(Time.time * 0.75f + off) + 1f) / 4f + 0.5f,
            c2 = (Mathf.cos(Time.time * 0.75f + off) + 1f) / 4f + 0.5f;
            Draw.color(Tmp.c1.set(EndPal.endMid).mul(1f, c1, c2, 1f));
            Vec2 v = Tmp.v1.trns(r.random(360f), r.random(12f, 80f) * finpow + 0.1f);
            Lines.lineAngle(e.x + v.x, e.y + v.y, v.angle(), 10f * e.fout());
        }

        Draw.blend();
        Draw.color(EndPal.endLight);
        Lines.stroke(4f * e.fout());
        Lines.circle(e.x, e.y, 120f * finpow);
    }),

    endgameShoot = new Effect(60f, 900f * 2f, e -> {
        float rad = (e.rotation - 130f) * Interp.pow3Out.apply(Mathf.curve(e.fin(), 0f, 0.5f)) + 130f;
        float rad2 = e.rotation * Mathf.curve(e.fin(), 0.25f, 1f);
        r.setSeed(e.id * 9999L);

        Draw.blend(PUBlending.shadowRealm);
        //Fill.poly(e.x, e.y, 6, rad);
        Draw.color(EndPal.endMid);
        DrawUtils.hollowPoly(e.x, e.y, Lines.circleVertices(rad) / 2, rad2, rad);
        Draw.blend();

        Draw.color(Color.black);
        for(int i = 0; i < 60; i++){
            Vec2 v = Tmp.v1.trns(r.random(360f), rad);
            float ang = v.angle();
            DrawPU.diamond(e.x + v.x, e.y + v.y, r.random(25f, 40f) * e.fslope(), r.random(80f, 110f) * Mathf.curve(e.fout(), 0f, 0.5f), ang);
        }
    }).layer(Layer.flyingUnit);

    public static class LaserEffectData{
        public Position a, b;
    }
}

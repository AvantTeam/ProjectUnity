package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.entities.effects.*;
import unity.graphics.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;

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
    });
}
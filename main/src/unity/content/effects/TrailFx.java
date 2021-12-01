package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.graphics.*;
import unity.type.decal.CapeDecorationType.*;

import static arc.graphics.g2d.Draw.*;

public class TrailFx{
    public static Effect coloredRailgunTrail = new Effect(30f, e -> {
        for(int i = 0; i < 2; i++){
            int sign = Mathf.signs[i];
            color(e.color);
            Drawf.tri(e.x, e.y, 10f * e.fout(), 24f, e.rotation + 90f + 90f * sign);
        }
    }),

    coloredRailgunSmallTrail = new Effect(30f, e -> {
        for(int i = 0; i < 2; i++){
            int sign = Mathf.signs[i];
            color(e.color);
            Drawf.tri(e.x, e.y, 5f * e.fout(), 12f, e.rotation + 90f + 90f * sign);
        }
    }),

    coloredArrowTrail = new Effect(40f, 80f, e -> {
        Tmp.v1.trns(e.rotation, 5f * e.fout());
        Draw.color(e.color);
        for(int s : Mathf.signs){
            Tmp.v2.trns(e.rotation - 90f, 9f * s * ((e.fout() + 2f) / 3f), -20f);
            Fill.tri(Tmp.v1.x + e.x, Tmp.v1.y + e.y, -Tmp.v1.x + e.x, -Tmp.v1.y + e.y, Tmp.v2.x + e.x, Tmp.v2.y + e.y);
        }
    }),

    endRailTrail = new Effect(50f, e -> {
        Draw.color(UnityPal.scarColor, UnityPal.endColor, e.fin());
        Drawf.tri(e.x, e.y, 13f * e.fout(), 29f, e.rotation);
        Drawf.tri(e.x, e.y, 13f * e.fout(), 29f, e.rotation + 180f);
    }),

    endTrail = new Effect(50f, e -> {
        Draw.color(Color.black, UnityPal.scarColor, Mathf.curve(e.fin(), 0f, 0.3f));
        Angles.randLenVectors(e.id, 2, e.finpow() * 7f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 3f * e.fout());
        });
    }),

    capeTrail = new Effect(30f, e -> {
        CapeEffectData data = e.data();
        TextureRegion reg = data.type.region;

        Draw.alpha(data.alpha * e.fout());
        Draw.blend(Blending.additive);
        for(int sign : Mathf.signs){
            Tmp.v1.trns(e.rotation - 90f, data.type.x * sign, data.type.y);
            Draw.rect(
                reg,
                e.x + Tmp.v1.x, e.y + Tmp.v1.y,
                reg.width * scl * sign,
                reg.height * scl,
                e.rotation + data.sway * sign - 90f
            );
        }

        Draw.blend(Blending.normal);
    });
}

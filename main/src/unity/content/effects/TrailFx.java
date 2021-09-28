package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.graphics.*;

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

    endTrail = new Effect(50f, e -> {
        Draw.color(Color.black, UnityPal.scarColor, Mathf.curve(e.fin(), 0f, 0.3f));
        Angles.randLenVectors(e.id, 2, e.finpow() * 7f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, 3f * e.fout());
        });
    });
}

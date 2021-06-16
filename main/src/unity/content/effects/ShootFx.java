package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.graphics.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;

public class ShootFx{
    public static Effect laserChargeShoot = new Effect(21f, e -> {
        color(e.color, Color.white, e.fout());

        for(int i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 4f * e.fout(), 29f, e.rotation + 90f * i + e.finpow() * 112f);
        }
    }),

    laserBreakthroughShoot = new Effect(40f, e -> {
        color(e.color);

        stroke(e.fout() * 2.5f);
        Lines.circle(e.x, e.y, e.finpow() * 100f);

        stroke(e.fout() * 5f);
        Lines.circle(e.x, e.y, e.fin() * 100f);

        color(e.color, Color.white, e.fout());

        randLenVectors(e.id, 20, 80f * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 5f));


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

    orbShoot = new Effect(21f, e -> {
        color(Pal.surge);
        for(int i = 0; i < 2; i++){
            int l = Mathf.signs[i];
            Drawf.tri(e.x, e.y, 4f * e.fout(), 29f, e.rotation + 67 * l);
        }
    });
}

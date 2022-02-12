package unity.content.effects;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import unity.graphics.*;

import static arc.math.Angles.*;
import static arc.math.Interp.*;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;

public final class DeathFx{
    public static final Effect

    monolithSoulDeath = new Effect(64f, e -> {
        color(UnityPal.monolith, UnityPal.monolithDark, e.fin());
        randLenVectors(e.id, 27, e.finpow() * 56f, (x, y) ->
            Fill.circle(e.x + x, e.y + y, 1f + e.fout() * 2f)
        );

        e.scaled(48f, i -> {
            stroke(i.fout() * 2.5f, UnityPal.monolithLight);
            Lines.circle(e.x, e.y, i.fin(pow10Out) * 32f);

            float thick = i.foutpowdown() * 4f;

            Fill.circle(e.x, e.y, thick / 2f);
            for(int t = 0; t < 4; t++){
                Drawf.tri(e.x, e.y, thick, thick * 14f,
                    Mathf.randomSeed(e.id + 1, 360f) + 90f * t + i.finpow() * 60f * Mathf.sign(e.id % 2 == 0)
                );
            }
        });
    });

    private DeathFx(){
        throw new AssertionError();
    }
}

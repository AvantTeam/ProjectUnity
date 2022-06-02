package unity.content.effects;

import arc.math.*;
import mindustry.entities.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static unity.graphics.Palettes.*;

public final class MonolithShootFx{
    public static final Effect
    strayShoot = new Effect(12f, e -> {
        color(monolithLight, monolith, monolithDark, e.finpowdown());
        stroke(e.fout() * 1.2f + 0.5f);

        randLenVectors(e.id, 2, 22f * e.finpow(), e.rotation, 50f, (x, y) ->
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 5f + 2f)
        );
    });

    private MonolithShootFx(){
        throw new AssertionError();
    }
}

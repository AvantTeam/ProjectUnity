package unity.content.effects;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.*;
import unity.graphics.*;

import static arc.graphics.g2d.Draw.*;

public class ParticleFx{
    public static Effect

    endRegenDisable = new Effect(30f, e -> {
        color(UnityPal.scarColor);
        Fill.square(e.x, e.y, 2.5f * Interp.pow2In.apply(e.fslope()), 45f);
    });
}

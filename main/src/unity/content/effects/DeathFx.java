package unity.content.effects;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.content.units.*;
import unity.gen.*;
import unity.graphics.*;
import unity.util.*;

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
    }),

    monolithSoulCrack = new Effect(20f, e -> {
        UnitType type = MonolithUnitTypes.monolithSoul;
        for(int i = 0; i < type.wreckRegions.length; i++){
            float off = (360f / type.wreckRegions.length) * i;

            Tmp.v1.trns(e.rotation + off, e.finpow() * 24f).add(e.x, e.y);

            alpha(e.fout());
            Draw.rect(type.wreckRegions[i], Tmp.v1.x, Tmp.v1.y, e.rotation - 90f);
        }
    }).layer(Layer.flyingUnit),

    monolithSoulJoin = new Effect(72f, e -> {
        if(!(e.data instanceof MonolithSoul soul)) return;

        stroke(1.5f, UnityPal.monolith);

        TextureRegion reg = Core.atlas.find("unity-monolith-chain");
        Quat rot = Utils.q1.set(Vec3.Z, e.rotation + 90f).mul(Utils.q2.set(Vec3.X, 75f));
        float t = e.foutpowdown(), w = reg.width * scl * 0.5f * t, h = reg.height * scl * 0.5f * t,
            rad = t * 25f, a = Mathf.curve(t, 0.25f);

        alpha(a);
        UnityDrawf.panningCircle(reg,
            e.x, e.y, w, h,
            rad, 360f, Time.time * 6f * Mathf.sign(soul.id % 2 == 0) + soul.id * 30f,
            rot, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
        );

        color(Color.black, UnityPal.monolithDark, 0.5f);
        alpha(a);

        blend(Blending.additive);
        UnityDrawf.panningCircle(Core.atlas.find("circle-mid"),
            e.x, e.y, w + 6f, h + 6f,
            rad, 360f, 0f,
            rot, true, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
        );

        blend();
    }).layer(Layer.flyingUnit);

    private DeathFx(){
        throw new AssertionError();
    }
}

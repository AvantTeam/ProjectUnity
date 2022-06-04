package unity.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.entities.type.*;
import unity.graphics.*;
import unity.graphics.MultiTrail.*;
import unity.mod.*;
import unity.util.*;

import static arc.Core.*;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static mindustry.Vars.*;
import static unity.graphics.Palettes.*;

/**
 * Defines all {@linkplain Faction#monolith monolith} effect types.
 * @author GlennFolker
 */
public final class MonolithFx{
    private static final Color col = new Color();

    public static final Effect
    spark = new Effect(60f, e -> randLenVectors(e.id, 2, e.rotation, (x, y) -> {
        color(monolith, monolithDark, e.fin());

        float w = 1f + e.fout() * 4f;
        Fill.rect(e.x + x, e.y + y, w, w, 45f);
    })),

    strayShoot = new Effect(12f, e -> {
        color(monolithLight, monolith, monolithDark, e.finpowdown());
        stroke(e.fout() * 1.2f + 0.5f);

        randLenVectors(e.id, 2, 22f * e.finpow(), e.rotation, 50f, (x, y) ->
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 5f + 2f)
        );
    }).followParent(false),

    tendenceShoot = new Effect(32f, e -> {
        TextureRegion reg = atlas.find("unity-monolith-chain");
        MathUtils.q1.set(Vec3.Z, e.rotation + 90f).mul(MathUtils.q2.set(Vec3.X, 75f));
        float t = e.finpow(), w = reg.width * scl * 0.4f * t, h = reg.height * scl * 0.4f * t, rad = 9f + t * 8f;

        color(monolithLight);
        alpha(e.foutpowdown());

        DrawUtils.panningCircle(reg,
            e.x, e.y, w, h,
            rad, 360f, e.fin(Interp.pow2Out) * 90f * Mathf.sign(e.id % 2 == 0) + e.id * 30f,
            MathUtils.q1, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
        );

        color(Color.black, monolithDark, 0.67f);
        alpha(e.foutpowdown());

        blend(Blending.additive);
        DrawUtils.panningCircle(atlas.find("unity-line-shade"),
            e.x, e.y, w + 6f, h + 6f,
            rad, 360f, 0f,
            MathUtils.q1, true, Layer.flyingUnitLow - 0.01f, Layer.flyingUnit
        );

        blend();
    }).layer(Layer.flyingUnit),

    tendenceCharge = new PUEffect(() -> {
        class State extends EffectState{
            @Override
            public void remove(){
                if(data instanceof TrailHold[] data) for(TrailHold trail : data) Fx.trailFade.at(x, y, trail.width, monolithLight, trail.trail.copy());
                super.remove();
            }
        } return Pools.obtain(State.class, State::new);
    }, () -> {
        TrailHold[] trails = new TrailHold[12];
        for(int i = 0; i < trails.length; i++){
            Tmp.v1.trns(Mathf.random(360f), Mathf.random(24f, 64f));
            MultiTrail trail = MonolithTrails.soul(26);
            if(trail.trails[trail.trails.length - 1].trail instanceof TexturedTrail tr) tr.trailChance = 0.1f;

            trails[i] = new TrailHold(trail, Tmp.v1.x, Tmp.v1.y, Mathf.random(1f, 2f));
        }

        return trails;
    }, 40f, e -> {
        if(!(e.data instanceof TrailHold[] data)) return;

        color(monolith, monolithLight, e.fin());
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

            col.set(monolith).lerp(monolithLight, e.finpowdown());
            hold.trail.drawCap(col, w);
            hold.trail.draw(col, w);
        }

        stroke(Mathf.curve(e.fin(), 0.5f) * 1.4f, monolithLight);
        Lines.circle(e.x, e.y, e.fout() * 64f);
    }),

    tendenceHit = new Effect(52f, e -> {
        color(monolithLight, monolith, monolithDark, e.fin());
        for(int sign : Mathf.signs){
            randLenVectors(e.id + sign, 3, e.fin(Interp.pow5Out) * 32f, e.rotation, 30f, 16f, (x, y) ->
                Fill.square(e.x + x, e.y + y, e.foutpowdown() * 2.5f, e.id * 30f + e.finpow() * 90f * sign)
            );
        }
    });

    private MonolithFx(){
        throw new AssertionError();
    }
}

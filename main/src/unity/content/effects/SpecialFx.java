package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.Effect.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.entities.bullet.anticheat.*;
import unity.entities.bullet.kami.*;
import unity.entities.effects.*;
import unity.gen.*;
import unity.graphics.*;
import unity.mod.*;

import static arc.graphics.g2d.Draw.color;

public class SpecialFx{
    private static final Rand rand = new Rand();

    public static Effect

    kamiBulletSpawn = new Effect(30f, 300f, e -> {
        if(!(e.data instanceof KamiBullet kb)) return;
        KamiBulletType type = (KamiBulletType)kb.type;
        TextureRegion r = KamiBulletType.region;
        e.lifetime = type.delay;
        float time = (Time.time / 2f) + (e.time - type.delay) * 2f;
        float scl = 1f + (e.fout() * 5f);
        float st = Mathf.clamp(Math.max(kb.width, kb.length) / 10f + 1.2f, 1.5f, 4f) * (1f + Mathf.absin(time, 10f, 0.33f));
        Tmp.c1.set(Color.red).shiftHue(time).a(e.fin());
        Draw.color(Tmp.c1);
        Draw.rect(r, kb.x, kb.y, ((kb.width * 2f) + st) * scl, ((kb.length * 2f) + st) * scl, kb.rotation());
        Draw.color(Color.white);
        Draw.rect(r, kb.x, kb.y, kb.width * 2f * e.fin(), kb.length * 2f * e.fin(), kb.rotation());
    }),

    endDeny = new Effect(80f, 1200f, e -> {
        if(!(e.data instanceof Unit u)) return;
        Draw.blend(Blending.additive);
        float a = (e.color.a / 2f) + 0.5f;
        e.scaled(40f, s -> {
            Draw.color(UnityPal.scarColor);
            Interp in = Interp.pow3Out;
            float f1 = in.apply(Mathf.curve(s.fin(), 0f, 0.8f)),
            f2 = in.apply(Mathf.curve(s.fin(), 0.2f, 1f)),
            hs = u.hitSize / 2f;
            rand.setSeed(e.id * 99999L);
            for(int i = 0; i < (int)(7f * a); i++){
                float len = (hs * rand.random(0.75f, 1f));
                float r = rand.range(360f), scl = rand.random(0.75f, 1.5f);
                Vec2 v = Tmp.v1.trns(r, len + hs * f1 * scl).add(e.x, e.y),
                v2 = Tmp.v2.trns(r, len + hs * f2 * scl).add(e.x, e.y);
                Lines.stroke(1.5f);
                Lines.line(v.x, v.y, v2.x, v2.y);
            }
        });
        Draw.alpha(e.fout());
        Draw.mixcol(UnityPal.scarColor, a);

        Draw.rect(u.icon(), u.x + Mathf.range(e.fin() * 2f), u.y + Mathf.range(e.fin() * 2f), u.rotation - 90f);

        Draw.blend();
        Draw.reset();
    }),

    fragmentation = new FragmentationShaderEffect(3.5f * 60f),

    fragmentationFast = new FragmentationShaderEffect(1.5f * 60f){{
        fragOffset = 0f;
        heatOffset = 0f;
    }},

    endgameVapourize = new VapourizeShaderEffect(3f * 60f, 900f).updateVel(false),

    /** {@link Fx#chainLightning} but uses {@link EffectContainer#rotation} as blinking scale */
    chainLightningActive = new Effect(20f, 300f, e -> {
        if(!(e.data instanceof Position p)) return;

        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
        Tmp.v1.set(p).sub(e.x, e.y).nor();

        float normx = Tmp.v1.x, normy = Tmp.v1.y;
        float range = 6f;
        int links = Mathf.ceil(dst / range);
        float spacing = dst / links;

        Lines.stroke(2.5f * e.fout());
        Draw.color(Color.white, e.color, e.fin());

        Lines.beginLine();
        Lines.linePoint(e.x, e.y);

        rand.setSeed(e.id + (long)(Time.time / e.rotation));

        for(int i = 0; i < links; i++){
            float nx, ny;
            if(i == links - 1){
                nx = tx;
                ny = ty;
            }else{
                float len = (i + 1) * spacing;
                Tmp.v1.setToRandomDirection(rand).scl(range/2f);
                nx = e.x + normx * len + Tmp.v1.x;
                ny = e.y + normy * len + Tmp.v1.y;
            }

            Lines.linePoint(nx, ny);
        }

        Lines.endLine();
    }).followParent(false),

    chargeTransfer = new Effect(20f, e -> {
        if(!(e.data instanceof Position)) return;
        Position to = e.data();
        Tmp.v1.set(e.x, e.y).interpolate(Tmp.v2.set(to), e.fin(), Interp.pow3)
        .add(Tmp.v2.sub(e.x, e.y).nor().rotate90(1).scl(Mathf.randomSeedRange(e.id, 1f) * e.fslope() * 10f));
        float x = Tmp.v1.x, y = Tmp.v1.y, s = e.fslope() * 4f;
        Draw.color(e.color);
        Fill.square(x, y, s, 45f);
    }),

    timeStop = new CustomStateEffect(() -> {
        EffectState s = EffectState.create();
        if(TimeStop.inTimeStop()) TimeStop.addEntity(s, (3.5f * 60) + 60f);
        return s;
    }, 3.5f * 60f, 2f * 500, e -> {
        float s = Interp.pow2.apply(e.fslope()) * 500f;
        Draw.blend(UnityBlending.invert);
        Fill.poly(e.x, e.y, (int)(s / 5) + 24, s);
        Draw.blend(UnityBlending.multiply);
        Draw.color(Color.red);
        Fill.poly(e.x, e.y, (int)(s / 5) + 24, s);
        Draw.blend();
    }),

    voidFractureEffect = new Effect(30f, 700f, e -> {
        if(!(e.data instanceof VoidFractureData)) return;
        VoidFractureData data = (VoidFractureData)e.data;
        float rot = Angles.angle(data.x, data.y, data.x2, data.y2);

        Draw.color(Color.black);
        for(int i = 0; i < 3; i++){
            float f = Mathf.lerp(data.b.width, data.b.widthTo, i / 2f);
            float a = Mathf.lerp(0.25f, 1f, (i / 2f) * (i / 2f));

            Draw.alpha(a);
            Lines.stroke(f * e.fout());
            Lines.line(data.x, data.y, data.x2, data.y2, false);
            Drawf.tri(data.x2, data.y2, f * 1.22f * e.fout(), f * 2f, rot);
            Drawf.tri(data.x, data.y, f * 1.22f * e.fout(), f * 2f, rot + 180f);
        }

        FloatSeq s = data.spikes;
        if(!s.isEmpty()){
            for(int i = 0; i < data.spikes.size; i += 4){
                float x1 = s.get(i), y1 = s.get(i + 1), x2 = s.get(i + 2), y2 = s.get(i + 3);
                Drawf.tri(x1, y1, (data.b.widthTo + 1f) * e.fout(), Mathf.dst(x1, y1, x2, y2) * 2f * Mathf.curve(e.fin(), 0f, 0.2f), Angles.angle(x1, y1, x2, y2));
                Fill.circle(x1, y1, ((data.b.widthTo + 1f) / 1.22f) * e.fout());
            }
        }
    }).layer(Layer.effect + 0.03f),

    pointBlastLaserEffect = new Effect(23f, 600f, e -> {
        if(!(e.data instanceof PointBlastInterface data)) return;

        for(int i = 0; i < data.colors().length; i++){
            color(data.colors()[i]);
            Fill.circle(e.x, e.y, (e.rotation - (data.widthReduction() * i)) * e.fout());
        }
        Drawf.light(e.x, e.y, e.rotation * e.fout() * 3f, data.colors()[0], 0.66f);
    });

    public interface PointBlastInterface{
        Color[] colors();
        float widthReduction();
    }

    public static class VoidFractureData{
        public float x, y, x2, y2;
        public VoidFractureBulletType b;
        public FloatSeq spikes = new FloatSeq();
    }
}

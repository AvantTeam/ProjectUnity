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
import mindustry.graphics.*;
import unity.entities.bullet.anticheat.*;
import unity.entities.bullet.laser.*;
import unity.entities.effects.*;

import static arc.graphics.g2d.Draw.color;

public class SpecialFx{
    private static final Rand rand = new Rand();

    public static Effect

    fragmentation = new FragmentationShaderEffect(3.5f * 60f),

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

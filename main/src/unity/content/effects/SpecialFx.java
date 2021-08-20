package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.Effect.*;
import unity.entities.effects.*;

public class SpecialFx{
    private static final Rand rand = new Rand();

    public static Effect

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
    }).followParent(false);
}

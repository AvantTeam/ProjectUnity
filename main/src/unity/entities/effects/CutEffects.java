package unity.entities.effects;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.assets.list.*;
import unity.gen.*;

import static arc.Core.*;

public class CutEffects{
    private static final Vec2 tmp = new Vec2(), tmp2 = new Vec2();
    private static TextureRegion white;
    private static final FrameBuffer buffer = new FrameBuffer();
    private static final float minCut = 10f, minFragments = 5f;
    public static EntityGroup<CutEffect> group = new EntityGroup<>(CutEffect.class, true, false);
    public float[] stencil, drawStencil;

    static{
        Events.on(EventType.ResetEvent.class, e -> group.clear());
        Events.run(Trigger.update, () -> {
            if(Vars.state.isPlaying()) Vars.collisions.updatePhysics(group);
        });
    }

    public static void cutUnit(Unit unit, float x, float y, float x2, float y2){
        unit.remove();
    }

    public static void draw(CutEffect effect){
        buffer.resize(graphics.getWidth(), graphics.getHeight());
        Draw.draw(effect.z(), () -> {
            Drawc subject = effect.other;
            boolean rotate = subject instanceof Rotc;
            Rotc rot = rotate ? (Rotc)subject : null;
            float ox = effect.x - effect.originX, oy = effect.y - effect.originY,
            lx = subject.x(), ly = subject.y(), lr = rotate ? rot.rotation() : 0f;

            UnityShaders.stencilShader.stencilColor.set(Color.green);
            UnityShaders.stencilShader.heatColor.set(Pal.lightFlame).lerp(Pal.darkFlame, effect.fin());
            buffer.begin(UnityShaders.stencilShader.stencilColor);

            subject.trns(ox, oy);
            if(rotate){
                tmp2.set(subject).sub(effect).rotate(effect.rotation).add(effect).sub(subject);
                subject.trns(tmp2);
                rot.rotation(rot.rotation() + effect.rotation);
            }

            subject.draw();
            Draw.color(UnityShaders.stencilShader.stencilColor);
            for(CutEffects stencil : effect.stencils){
                stencil.draw(effect.x, effect.y, effect.rotation);
            }

            subject.set(lx, ly);
            if(rotate){
                rot.rotation(lr);
            }

            buffer.end();
            Draw.blit(buffer, UnityShaders.stencilShader);
            Draw.reset();
        });
    }

    public void draw(float x, float y, float rotation){
        for(int i = 0; i < stencil.length; i += 2){
            tmp.set(stencil[i], stencil[i + 1]).rotate(rotation).add(x, y);
            drawStencil[i] = tmp.x;
            drawStencil[i + 1] = tmp.y;
        }
        if(white == null) white = Core.atlas.white();
        Draw.vert(white.texture, drawStencil, 0, drawStencil.length);
    }
}

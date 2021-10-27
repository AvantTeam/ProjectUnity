package unity.entities.effects;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.util.pooling.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.assets.list.*;
import unity.assets.list.UnityShaders.*;

public class FragmentationShaderEffect extends Effect{
    public FragmentationShaderEffect(float lifetime){
        this.lifetime = lifetime;
    }

    @Override
    public void at(float x, float y, float rotation, Object data){
        if(!Vars.headless){
            FragEffectState e = Pools.obtain(FragEffectState.class, FragEffectState::new);
            e.x = x;
            e.y = y;
            e.rotation = rotation;
            e.lifetime = lifetime;
            e.data = data;
            if(data instanceof Drawc){
                e.clipSize = ((Drawc)data).clipSize();
            }else{
                e.clipSize = clip;
            }
            e.add();
        }
    }

    static class FragEffectState extends EffectState{
        float clipSize;

        @Override
        public void draw(){
            if(data instanceof Drawc){
                Drawc draw = (Drawc)data;

                Draw.draw(Layer.flyingUnitLow, () -> {
                    FragmentationShader s = UnityShaders.fragmentShader;
                    if(draw instanceof Unit){
                        Unit u = (Unit)data;
                        u.hitTime = 0f;
                        s.direction.trns(rotation, u.hitSize / 14f);
                        s.source.set(u);
                        s.size = u.hitSize / 4f;
                    }else{
                        s.source.set(x, y);
                        s.direction.trns(rotation, clipSize / 14f);
                        s.size = 0f;
                    }
                    s.heatColor.set(Pal.lightFlame).lerp(Pal.darkFlame, Mathf.curve(fin(), 0f, 0.3f));
                    s.fragProgress = Mathf.curve(fin(), 0.2f, 1f);
                    s.heatProgress = Mathf.curve(fin(), 0f, 0.3f);
                    FrameBuffer buffer = UnityShaders.bufferAlt;
                    buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
                    buffer.begin(Color.clear);
                    draw.draw();
                    buffer.end();
                    Draw.blit(buffer, s);
                });
            }
        }

        @Override
        public float clipSize(){
            return clipSize * 2f;
        }
    }
}

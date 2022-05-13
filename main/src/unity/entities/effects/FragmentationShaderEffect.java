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
import mindustry.type.*;
import unity.assets.list.*;
import unity.assets.list.UnityShaders.*;

public class FragmentationShaderEffect extends Effect{
    public float fragOffset = 0.2f;
    public float heatOffset = 0.3f;
    public float windPower = -1f;

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
            e.type = this;
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
        FragmentationShaderEffect type;
        float clipSize;

        @Override
        public void draw(){
            if(data instanceof Drawc draw){
                Unit unit = draw instanceof Unit ? (Unit)draw : null;

                float z = Layer.flyingUnitLow;
                if(unit != null){
                    UnitType t = unit.type;
                    z = unit.elevation > 0.5f ? (t.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : t.groundLayer + Mathf.clamp(t.hitSize / 4000f, 0, 0.01f);
                }

                Draw.draw(z, () -> {
                    FragmentationShader s = UnityShaders.fragmentShader;
                    if(unit != null){
                        unit.hitTime = 0f;
                        s.direction.trns(rotation, type.windPower >= 0f ? type.windPower : unit.hitSize / 14f);
                        s.source.set(unit);
                        s.size = unit.hitSize / 4f;
                    }else{
                        s.source.set(x, y);
                        s.direction.trns(rotation, type.windPower >= 0f ? type.windPower : clipSize / 14f);
                        s.size = 0f;
                    }
                    float heat = type.heatOffset > 0f ? Mathf.curve(fin(), 0f, type.heatOffset) : 1f;
                    s.heatColor.set(Pal.lightFlame).lerp(Pal.darkFlame, heat);
                    s.fragProgress = Mathf.curve(fin(), type.fragOffset, 1f);
                    s.heatProgress = heat;
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

package unity.entities.effects;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.assets.list.*;
import unity.assets.list.UnityShaders.*;

import static arc.graphics.g2d.Draw.*;

public class VapourizeShaderEffect extends Effect{
    boolean updateVel = true;

    public VapourizeShaderEffect(float lifetime, float clipsize){
        super(lifetime, clipsize, e -> {});
    }

    @Override
    public void at(Position pos){
        at(pos.getX(), pos.getY());
    }

    @Override
    public void at(float x, float y){
        at(x, y, 0f);
    }

    @Override
    public void at(Position pos, float rotation){
        at(pos.getX(), pos.getY(), rotation);
    }

    @Override
    public void at(float x, float y, Color color){
        at(x, y);
    }

    @Override
    public void at(float x, float y, float rotation){
        at(x, y, rotation, null, null);
    }

    @Override
    public void at(float x, float y, float rotation, Color color){
        at(x, y, rotation, null, null);
    }

    @Override
    public void at(float x, float y, float rotation, Color color, Object data){
        at(x, y, rotation, data);
    }

    @Override
    public void at(float x, float y, float rotation, Object data){
        if(Vars.headless || !Core.settings.getBool("effects")) return;
        VapourizeShaderEffectState s = Pools.obtain(VapourizeShaderEffectState.class, VapourizeShaderEffectState::new);
        s.x = x;
        s.y = y;
        s.rotation = rotation;
        float l = lifetime;
        if(data instanceof Object[] d){
            s.datab = d[0];
            if(d.length >= 3){
                s.clipSize = rotation * 2f;
                s.windScl = (float)d[2];
                l /= 2f;
            }
            data = d[1];
        }
        s.data = data;
        s.lifetime = l;
        s.add();
    }

    public VapourizeShaderEffect updateVel(boolean v){
        updateVel = v;
        return this;
    }

    public class VapourizeShaderEffectState extends EffectState{
        float windScl = -1f, clipSize;
        Object datab;

        @Override
        public void reset(){
            super.reset();
            datab = null;
            windScl = -1f;
            clipSize = 0f;
        }

        @Override
        public void update(){
            super.update();

            if(updateVel && data instanceof Velc v){
                v.move(v.vel());
                v.vel().scl(1f - (v.drag() * Time.delta));
            }
        }

        @Override
        public void draw(){
            if(data instanceof Drawc draw){
                float c = windScl > 0 ? windScl : draw.clipSize() / 8f;
                z(Layer.flyingUnitLow);
                blend(Blending.additive);
                mixcol(Color.red, 1f);
                alpha(fout());

                if(data instanceof Unit u){
                    u.hitTime = 0f;
                    rect(u.type.fullIcon, u, u.rotation - 90f);
                }else if(data instanceof Building b){
                    rect(b.block.region, b.x, b.y);
                }

                blend();

                if(Vars.renderer.animateShields){
                    Draw.draw(z() + 0.001f, () -> {
                        float in = Mathf.clamp(fin() * 2f);

                        VapourizeShader s = UnityShaders.vapourizeShader;
                        FrameBuffer buffer = UnityShaders.bufferAlt;
                        buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
                        s.toColor.set(Pal.rubble);
                        s.colorProgress = Interp.pow2In.apply(Mathf.clamp(in * 1.25f));
                        s.progress = Interp.pow2In.apply(in);
                        s.windSource.set(datab instanceof Position p ? p : draw);
                        s.fragProgress = Interp.pow3In.apply(in) * c;
                        s.size = c;
                        buffer.begin(Color.clear);
                        draw.draw();
                        buffer.end();
                        buffer.blit(UnityShaders.vapourizeShader);
                    });
                }
            }else if(Vars.renderer.animateShields && data instanceof Building[] drwA && datab != null){
                Draw.draw(Layer.block + 0.001f, () -> {
                    float in = fin();

                    VapourizeShader s = UnityShaders.vapourizeShader;
                    FrameBuffer buffer = UnityShaders.bufferAlt;
                    s.toColor.set(Pal.rubble);
                    s.colorProgress = Interp.pow2In.apply(Mathf.clamp(in * 1.25f));
                    s.progress = Interp.pow2In.apply(in);
                    s.windSource.set((Position)datab);
                    s.fragProgress = Interp.pow3In.apply(in) * windScl;
                    s.size = 0f;
                    buffer.begin(Color.clear);

                    for(Building d : drwA){
                        if(Core.camera.bounds(Tmp.r1).overlaps(Tmp.r2.setCentered(d.x(), d.y(), d.block.clipSize + s.fragProgress * 2f))){
                            d.draw();
                        }
                    }

                    buffer.end();
                    buffer.blit(UnityShaders.vapourizeShader);
                });
            }
            Draw.reset();
        }

        @Override
        public float clipSize(){
            return Math.max(clip, clipSize);
        }
    }
}

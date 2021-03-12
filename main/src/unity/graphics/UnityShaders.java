package unity.graphics;

import arc.*;
import arc.assets.*;
import arc.files.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;

import static mindustry.Vars.*;

public class UnityShaders implements Loadable, Disposable{
    public static HolographicShieldShader holoShield;

    protected static UnityShader[] all;

    public static void load(){
        Core.assets.load(new UnityShaders());
    }

    @Override
    public void loadSync(){
        all = new UnityShader[]{
            holoShield = new HolographicShieldShader()
        };

        for(int i = 0; i < all.length; i++){
            UnityShader shader = all[i];
            if(shader != null){
                shader.layer = Layer.shields + 2f + i * ((1f / all.length) - 0.01f);
            }
        }

        Events.run(Trigger.draw, () -> {
            float range = (1f / all.length) - 0.01f;
            for(int i = 0; i < all.length; i++){
                UnityShader shader = all[i];

                if(shader != null && shader.apply.get()){
                    Draw.drawRange(shader.layer, range, () -> renderer.effectBuffer.begin(Color.clear), () -> {
                        renderer.effectBuffer.end();
                        renderer.effectBuffer.blit(shader);
                    });
                }
            }
        });
    }

    @Override
    public void dispose(){
        if(!headless){
            holoShield.dispose();
        }
    }

    public static class UnityShader extends Shader{
        public final Boolp apply;
        protected float layer;

        public UnityShader(Fi vert, Fi frag){
            this(vert, frag, () -> true);
        }

        public UnityShader(Fi vert, Fi frag, Boolp apply){
            super(vert, frag);
            this.apply = apply;
        }

        public float getLayer(){
            return layer;
        }
    }

    public static class HolographicShieldShader extends UnityShader{
        public HolographicShieldShader(){
            super(
                Core.files.internal("shaders/screenspace.vert"),
                tree.get("shaders/holographicshield.frag"),
                () -> renderer.animateShields
            );
        }

        @Override
        public void apply(){
            setUniformf("u_time", Time.globalTime);
            setUniformf("u_dp", Scl.scl(1f));
        }
    }
}

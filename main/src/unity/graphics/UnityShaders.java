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

public class UnityShaders implements Loadable{
    public static HolographicShieldShader holoShield;
    public static StencilShader stencilShader;

    protected static FrameBuffer buffer;
    protected static UnityShader[] all;

    public static void load(){
        Core.assets.load(new UnityShaders());
    }

    @Override
    public void loadSync(){
        if(headless) return;

        buffer = new FrameBuffer();
        all = new UnityShader[]{
            holoShield = new HolographicShieldShader(),
            stencilShader = new StencilShader()
        };

        for(int i = 0; i < all.length; i++){
            UnityShader shader = all[i];
            if(shader != null){
                shader.layer = Layer.shields + 2f + i * ((1f / all.length) - 0.01f);
            }
        }

        Events.run(Trigger.draw, () -> {
            buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());

            float range = (1f / all.length) / 2f;
            for(UnityShader shader : all){
                if(shader != null && shader.apply.get()){
                    Draw.drawRange(shader.getLayer(), range, () -> buffer.begin(Color.clear), () -> {
                        buffer.end();
                        Draw.blit(buffer, shader);
                    });
                }
            }
        });
    }

    public static void dispose(){
        if(!headless){
            buffer.dispose();
            for(UnityShader shader : all){
                shader.dispose();
            }
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

    public static class StencilShader extends UnityShader{
        public Color stencilColor = new Color();
        public Color heatColor = new Color();

        public StencilShader(){
            super(Core.files.internal("shaders/screenspace.vert"),
            tree.get("shaders/unitystencil.frag"),
            () -> false);
        }

        @Override
        public void apply(){
            setUniformf("stencilcolor", stencilColor);
            setUniformf("heatcolor", heatColor);
            setUniformf("u_invsize", 1f / Core.camera.width, 1f / Core.camera.height);
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
            setUniformf("u_time", Time.time / Scl.scl(1f));
            setUniformf("u_scl", 1f / Core.camera.height);
            setUniformf("u_offset",
                Core.camera.position.x - Core.camera.width / 2f,
                Core.camera.position.y - Core.camera.height / 2f
            );
        }
    }
}

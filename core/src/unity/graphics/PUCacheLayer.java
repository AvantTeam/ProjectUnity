package unity.graphics;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.util.*;
import mindustry.graphics.*;
import unity.assets.list.*;

import static mindustry.Vars.*;
import static mindustry.graphics.CacheLayer.*;

/** Defines all {@link CacheLayer}s this mod has to load. */
public final class PUCacheLayer{
    public static @Nullable ModSurfaceShader pit, waterpit;

    public static CacheLayer
    eneraphyte,

    //youngcha
    pitLayer, waterpitLayer;

    private PUCacheLayer(){
        throw new AssertionError();
    }

    public static void load(){
        if(!headless){
            try{
                pit = new PitShader("pit", "unity-concrete-blank1", "unity-stone-sheet", "unity-truss");
                waterpit = new PitShader("waterpit", "unity-concrete-blank1", "unity-stone-sheet", "unity-truss");
            }catch(Exception e){
                Log.err("There was an exception loading the shaders: @", e);
            }
        }

        add(eneraphyte = new ApplicableShaderLayer(PUShaders.eneraphyte));
        add(pitLayer = new CacheLayer.ShaderLayer(pit));
        add(waterpitLayer = new CacheLayer.ShaderLayer(waterpit));
    }

    /**
     * A forcible {@link ShaderLayer} that {@linkplain Shader#apply() applies} additional shader uniforms.
     * @author GlennFolker
     */
    public static class ApplicableShaderLayer extends CacheLayer{
        public @Nullable Shader shader;
        public @Nullable Cons<Shader> apply;
        public boolean force;

        public ApplicableShaderLayer(Shader shader){
            this(shader, false);
        }

        public ApplicableShaderLayer(Shader shader, boolean force){
            this(shader, force, null);
        }

        public ApplicableShaderLayer(Shader shader, Cons<Shader> apply){
            this(shader, false, apply);
        }

        public ApplicableShaderLayer(Shader shader, boolean force, Cons<Shader> apply){
            this.shader = shader;
            this.force = force;
            this.apply = apply;
        }

        @Override
        public void begin(){
            if(!force && !Core.settings.getBool("animatedwater")) return;

            renderer.blocks.floor.endc();
            renderer.effectBuffer.begin();

            Core.graphics.clear(Color.clear);
            renderer.blocks.floor.beginc();
        }

        @Override
        public void end(){
            if(!force && !Core.settings.getBool("animatedwater")) return;

            renderer.blocks.floor.endc();
            renderer.effectBuffer.end();

            if(apply != null) apply.get(shader);
            renderer.effectBuffer.blit(shader);
            renderer.blocks.floor.beginc();
        }
    }

    /**
     * SurfaceShader but uses a mod fragment asset.
     * @author xelo, younggam
     */
    public static class ModSurfaceShader extends Shader{
        Texture noiseTex;
        String noiseTexName = "noise";

        public ModSurfaceShader(String frag){
            super(
            Core.files.internal("shaders/screenspace.vert"),
            tree.get("shaders/" + frag + ".frag")
            );
            loadNoise();
        }

        public ModSurfaceShader(String vertRaw, String fragRaw){
            super(vertRaw, fragRaw);
            loadNoise();
        }

        public String textureName(){
            return noiseTexName;
        }

        public void loadNoise(){
            Core.assets.load("sprites/" + textureName() + ".png", Texture.class).loaded = t -> {
                t.setFilter(Texture.TextureFilter.linear);
                t.setWrap(Texture.TextureWrap.repeat);
            };
        }

        @Override
        public void apply(){
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time);

            if(hasUniform("u_noise")){
                if(noiseTex == null){
                    noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
                }

                noiseTex.bind(1);
                renderer.effectBuffer.getTexture().bind(0);

                setUniformi("u_noise", 1);
            }
        }
    }


    /**
     * SurfaceShader but uses a mod fragment asset.
     * @author xelo, younggam
     */
    public static class PitShader extends ModSurfaceShader{
        TextureRegion toplayer, bottomlayer, truss;
        String toplayerName, bottomlayerName, trussName;

        public PitShader(String name, String toplayer, String bottomlayer, String truss){
            super(name);
            toplayerName = toplayer;
            bottomlayerName = bottomlayer;
            trussName = truss;
        }

        @Override
        public void apply(){
            var texture = Core.atlas.find("grass1").texture;
            if(toplayer == null){
                toplayer = Core.atlas.find(toplayerName);
            }
            if(bottomlayer == null){
                bottomlayer = Core.atlas.find(bottomlayerName);
            }
            if(truss == null){
                truss = Core.atlas.find(trussName);
            }
            if(noiseTex == null){
                noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
            }
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time);
//tvariants
            setUniformf("u_toplayer", toplayer.u, toplayer.v, toplayer.u2, toplayer.v2);
            setUniformf("u_bottomlayer", bottomlayer.u, bottomlayer.v, bottomlayer.u2, bottomlayer.v2);
            setUniformf("bvariants", bottomlayer.width / 32f);
            setUniformf("u_truss", truss.u, truss.v, truss.u2, truss.v2);

            texture.bind(2);
            noiseTex.bind(1);
            renderer.effectBuffer.getTexture().bind(0);
            setUniformi("u_noise", 1);
            setUniformi("u_texture2", 2);
        }
    }
}

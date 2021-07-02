package unity.graphics;

import arc.*;
import arc.assets.*;
import arc.files.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g3d.*;
import arc.graphics.gl.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;
import unity.assets.type.g3d.*;
import unity.assets.type.g3d.attribute.*;
import unity.assets.type.g3d.attribute.type.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

public class UnityShaders implements Loadable{
    public static HolographicShieldShader holoShield;
    public static StencilShader stencilShader;

    public static Graphics3DShaderProvider graphics3DProvider;

    protected static FrameBuffer buffer;
    protected static UnityShader[] all;

    protected static boolean loaded;

    public static void load(){
        if(headless) return;
        Core.assets.load(new UnityShaders());
    }

    @Override
    public void loadSync(){
        buffer = new FrameBuffer();
        all = new UnityShader[]{
            holoShield = new HolographicShieldShader(),
            stencilShader = new StencilShader()
        };
        graphics3DProvider = new Graphics3DShaderProvider();

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
                    Draw.drawRange(shader.getLayer() + range / 2f, range, () -> buffer.begin(Color.clear), () -> {
                        buffer.end();
                        Draw.blit(buffer, shader);
                    });
                }
            }
        });

        loaded = true;
    }

    public static void dispose(){
        if(!headless || !loaded){
            buffer.dispose();
            for(UnityShader shader : all){
                shader.dispose();
            }
            graphics3DProvider.dispose();
        }
    }

    public static class UnityShader extends Shader{
        public final Boolp apply;
        protected float layer;

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

    public static class Graphics3DShaderProvider implements Disposable{
        protected final String vertSource;
        protected final String fragSource;
        protected LongMap<Graphics3DShader> shaders = new LongMap<>();

        public Graphics3DShaderProvider(){
            vertSource = tree.get("shaders/g3d.vert").readString();
            fragSource = tree.get("shaders/g3d.frag").readString();
        }

        public Graphics3DShader get(Renderable render){
            return get(render.material.mask());
        }

        public Graphics3DShader get(long mask){
            if(!shaders.containsKey(mask)){
                String prefix = "\n";

                if((mask & TextureAttribute.diffuse) != 0) prefix += "#define diffuseTextureFlag\n";
                if((mask & ColorAttribute.diffuse) != 0) prefix += "#define diffuseColorFlag\n";
                if((mask & TextureAttribute.specular) != 0) prefix += "#define specularTextureFlag\n";
                if((mask & ColorAttribute.specular) != 0) prefix += "#define specularColorFlag\n";
                if((mask & TextureAttribute.emissive) != 0) prefix += "#define emissiveTextureFlag\n";
                if((mask & ColorAttribute.emissive) != 0) prefix += "#define emissiveColorFlag\n";

                shaders.put(mask, new Graphics3DShader(prefix + vertSource, prefix + fragSource));
            }

            return shaders.get(mask);
        }

        @Override
        public void dispose(){
            for(var it = shaders.entries(); it.hasNext;){
                it.next().value.dispose();
                it.remove();
            }
        }
    }

    public static class Graphics3DShader extends Shader{
        protected Graphics3DShader(String vertexShader, String fragmentShader){
            super(vertexShader, fragmentShader);
        }

        public void apply(Renderable render){
            Camera3D camera = model.camera;
            Material material = render.material;

            setUniformMatrix4("u_projViewTrans", camera.combined.val);

            setUniformf("u_cameraPosition", camera.position.x, camera.position.y, camera.position.z, 1.1881f / (camera.far * camera.far));
            setUniformf("u_cameraDirection", camera.direction);
            setUniformf("u_cameraUp", camera.up);
            setUniformf("u_cameraNearFar", camera.near, camera.far);

            setUniformMatrix4("u_worldTrans", render.worldTransform.val);
            setUniformMatrix("u_normalMatrix", Tmp.m1.set(render.worldTransform.toNormalMatrix().val));

            FloatAttribute shine = material.get(FloatAttribute.shininess);
            if(shine != null) setUniformf("u_shininess", shine.value);

            TextureAttribute diff = material.get(TextureAttribute.diffuse);
            ColorAttribute diffCol = material.get(ColorAttribute.diffuse);
            if(diffCol != null) setUniformf("u_diffuseColor", diffCol.color);
            if(diff != null){
                setUniformi("u_diffuseTexture", model.bind(diff, 5));
                setUniformf("u_diffuseUVTransform", diff.offsetU, diff.offsetV, diff.scaleU, diff.scaleV);
            }

            TextureAttribute spec = material.get(TextureAttribute.specular);
            ColorAttribute specCol = material.get(ColorAttribute.specular);
            if(specCol != null) setUniformf("u_specularColor", specCol.color);
            if(spec != null){
                setUniformi("u_specularTexture", model.bind(spec, 4));
                setUniformf("u_specularUVTransform", spec.offsetU, spec.offsetV, spec.scaleU, spec.scaleV);
            }

            TextureAttribute em = material.get(TextureAttribute.emissive);
            ColorAttribute emCol = material.get(ColorAttribute.emissive);
            if(emCol != null) setUniformf("u_emissiveColor", emCol.color);
            if(em != null){
                setUniformi("u_emissiveTexture", model.bind(em, 3));
                setUniformf("u_emissiveUVTransform", em.offsetU, em.offsetV, em.scaleU, em.scaleV);
            }

            TextureAttribute ref = material.get(TextureAttribute.reflection);
            ColorAttribute refCol = material.get(ColorAttribute.reflection);
            if(refCol != null) setUniformf("u_reflectionColor", refCol.color);
            if(ref != null){
                setUniformi("u_reflectionTexture", model.bind(ref, 2));
                setUniformf("u_reflectionUVTransform", ref.offsetU, ref.offsetV, ref.scaleU, ref.scaleV);
            }

            TextureAttribute am = material.get(TextureAttribute.ambient);
            ColorAttribute amCol = material.get(ColorAttribute.ambient);
            if(amCol != null) setUniformf("u_ambientColor", amCol.color);
            if(am != null){
                setUniformi("u_ambientTexture", model.bind(am, 1));
                setUniformf("u_ambientUVTransform", am.offsetU, am.offsetV, am.scaleU, am.scaleV);
            }

            TextureAttribute nor = material.get(TextureAttribute.normal);
            if(nor != null){
                setUniformi("u_normalTexture", model.bind(nor, 0));
                setUniformf("u_normalUVTransform", nor.offsetU, nor.offsetV, nor.scaleU, nor.scaleV);
            }
        }
    }
}

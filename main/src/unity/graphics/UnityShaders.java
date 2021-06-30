package unity.graphics;

import arc.*;
import arc.assets.*;
import arc.files.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g3d.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
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
    public static Graphics3DShader graphics3D;

    protected static FrameBuffer buffer;
    protected static UnityShader[] all;

    public static void load(){
        if(headless) return;
        Core.assets.load(new UnityShaders());
    }

    @Override
    public void loadSync(){
        buffer = new FrameBuffer();
        all = new UnityShader[]{
            holoShield = new HolographicShieldShader(),
            stencilShader = new StencilShader(),
            graphics3D = new Graphics3DShader()
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
            if(buffer != null) buffer.dispose();
            for(UnityShader shader : all){
                shader.dispose();
            }
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

    public static class Graphics3DShader extends UnityShader{
        private final Mat3D temp = new Mat3D();
        private final static Mat3D idtMatrix = new Mat3D();
        public final float[] bones = new float[12 * 16];

        public Graphics3DShader(){
            super(tree.get("shaders/g3d.vert"), tree.get("shaders/g3d.frag"), () -> false);
        }

        public void apply(Renderable render){
            Camera3D camera = model.camera;
            Material material = render.material;

            setUniformMatrix4("u_projTrans", camera.projection.val);
            setUniformMatrix4("u_viewTrans", camera.view.val);
            setUniformMatrix4("u_projViewTrans", camera.combined.val);

            setUniformf("u_cameraPosition", camera.position.x, camera.position.y, camera.position.z, 1.1881f / (camera.far * camera.far));
            setUniformf("u_cameraDirection", camera.direction);
            setUniformf("u_cameraUp", camera.up);
            setUniformf("u_cameraNearFar", camera.near, camera.far);

            setUniformf("u_time", Time.time);

            setUniformMatrix4("u_worldTrans", render.worldTransform.val);
            setUniformMatrix4("u_viewWorldTrans", temp.set(camera.view).mul(render.worldTransform).val);
            setUniformMatrix4("u_projViewWorldTrans", temp.set(camera.combined).mul(render.worldTransform).val);
            setUniformMatrix("u_normalMatrix", Tmp.m1.set(render.worldTransform.toNormalMatrix().val));

            for(int i = 0; i < bones.length; i += 16){
                int idx = i / 16;
                if(render.bones == null || idx >= render.bones.length || render.bones[idx] == null){
                    System.arraycopy(idtMatrix.val, 0, bones, i, 16);
                }else{
                    System.arraycopy(render.bones[idx].val, 0, bones, i, 16);
                }
            }

            setUniformMatrix4fv("u_bones", bones, 0, bones.length);

            FloatAttribute shine = material.get(FloatAttribute.shininess);
            if(shine != null) setUniformf("u_shininess", shine.value);

            TextureAttribute diff = material.get(TextureAttribute.diffuse);
            if(diff != null){
                setUniformf("u_diffuseColor", material.<ColorAttribute>get(ColorAttribute.diffuse).color);
                setUniformi("u_diffuseTexture", model.bind(diff));
                setUniformf("u_diffuseUVTransform", diff.offsetU, diff.offsetV, diff.scaleU, diff.scaleV);
            }

            TextureAttribute spec = material.get(TextureAttribute.specular);
            if(spec != null){
                setUniformf("u_specularColor", material.<ColorAttribute>get(ColorAttribute.specular).color);
                setUniformi("u_specularTexture", model.bind(spec));
                setUniformf("u_specularUVTransform", spec.offsetU, spec.offsetV, spec.scaleU, spec.scaleV);
            }

            TextureAttribute em = material.get(TextureAttribute.emissive);
            if(em != null){
                setUniformf("u_emissiveColor", material.<ColorAttribute>get(ColorAttribute.emissive).color);
                setUniformi("u_emissiveTexture", model.bind(em));
                setUniformf("u_emissiveUVTransform", em.offsetU, em.offsetV, em.scaleU, em.scaleV);
            }

            TextureAttribute ref = material.get(TextureAttribute.reflection);
            if(ref != null){
                setUniformf("u_reflectionColor", material.<ColorAttribute>get(ColorAttribute.reflection).color);
                setUniformi("u_reflectionTexture", model.bind(ref));
                setUniformf("u_reflectionUVTransform", ref.offsetU, ref.offsetV, ref.scaleU, ref.scaleV);
            }

            TextureAttribute nor = material.get(TextureAttribute.normal);
            if(nor != null){
                setUniformi("u_normalTexture", model.bind(nor));
                setUniformf("u_normalUVTransform", nor.offsetU, nor.offsetV, nor.scaleU, nor.scaleV);
            }

            TextureAttribute am = material.get(TextureAttribute.ambient);
            if(am != null){
                setUniformi("u_ambientTexture", model.bind(am));
                setUniformf("u_ambientUVTransform", am.offsetU, am.offsetV, am.scaleU, am.scaleV);
            }
        }
    }
}

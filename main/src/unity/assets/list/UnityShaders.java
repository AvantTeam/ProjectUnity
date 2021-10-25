package unity.assets.list;

import arc.*;
import arc.files.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.g2d.*;
import arc.graphics.g3d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;
import mindustry.graphics.Shaders.*;
import mindustry.type.*;
import unity.assets.type.g3d.*;
import unity.assets.type.g3d.attribute.*;
import unity.assets.type.g3d.attribute.light.*;
import unity.assets.type.g3d.attribute.type.*;
import unity.assets.type.g3d.attribute.type.light.*;

import static mindustry.Vars.*;

public class UnityShaders{
    public static StencilShader stencilShader;
    public static MegalithRingShader megalithRingShader;
    public static Graphics3DShaderProvider graphics3DProvider;
    public static VapourizeShader vapourizeShader;
    public static VerticalBuildShader vBuild;

    protected static FrameBuffer buffer;

    public static void load(){
        if(headless) return;

        buffer = new FrameBuffer();
        CondShader[] conds = new CondShader[]{};

        vapourizeShader = new VapourizeShader();
        stencilShader = new StencilShader();
        vBuild = new VerticalBuildShader();
        megalithRingShader = new MegalithRingShader();
        graphics3DProvider = new Graphics3DShaderProvider();

        if(conds.length == 0) return;
        for(int i = 0; i < conds.length; i++){
            CondShader shader = conds[i];
            shader.layer = Layer.shields + 2f + i * ((1f / conds.length) - 0.01f);
        }

        float range = (1f / conds.length) / 2f;
        Events.run(Trigger.drawOver, () -> {
            buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());

            for(var shader : conds){
                if(shader.apply.get()){
                    Draw.drawRange(shader.getLayer() + range / 2f, range, () -> buffer.begin(Color.clear), () -> {
                        buffer.end();
                        Draw.blit(buffer.getTexture(), shader);
                    });
                }
            }
        });
    }

    public static class CondShader extends Shader{
        public final Boolp apply;
        protected float layer;

        public CondShader(Fi vert, Fi frag, Boolp apply){
            super(vert, frag);
            this.apply = apply;
        }

        public float getLayer(){
            return layer;
        }
    }

    public static class VapourizeShader extends Shader{
        public Texture noise;
        public Vec2 windSource = new Vec2();
        public FrameBuffer buffer = new FrameBuffer();
        public Color toColor = new Color();
        public float progress, colorProgress, fragProgress, size;

        public VapourizeShader(){
            super(
            Core.files.internal("shaders/screenspace.vert"),
            tree.get("shaders/vapourize.frag")
            );
            if(noise == null){
                Fi path = tree.get("shaders/vapourizenoise.png");
                noise = new Texture(path);
                noise.setWrap(TextureWrap.mirroredRepeat);
            }
        }

        @Override
        public void apply(){
            noise.bind(1);
            buffer.getTexture().bind(0);

            setUniformi("u_noise", 1);

            setUniformf("position", windSource);

            setUniformf("progress", progress);
            setUniformf("fragprogress", fragProgress);

            setUniformf("tocolor", toColor);
            setUniformf("colorprog", colorProgress);
            setUniformf("size", size);

            setUniformf("u_texsize", Core.camera.width, Core.camera.height);
            setUniformf("u_invsize", 1f / Core.camera.width, 1f / Core.camera.height);
            setUniformf("u_offset",
            Core.camera.position.x - Core.camera.width / 2,
            Core.camera.position.y - Core.camera.height / 2);
        }
    }

    public static class StencilShader extends Shader{
        public Color stencilColor = new Color();
        public Color heatColor = new Color();

        public StencilShader(){
            super(
                Core.files.internal("shaders/screenspace.vert"),
                tree.get("shaders/unitystencil.frag")
            );
        }

        @Override
        public void apply(){
            setUniformf("stencilcolor", stencilColor);
            setUniformf("heatcolor", heatColor);
            setUniformf("u_invsize", 1f / Core.camera.width, 1f / Core.camera.height);
        }
    }

    public static class PlanetObjectShader extends Shader{
        public Vec3 lightDir = new Vec3(1, 1, 1).nor();
        public Color ambientColor = Color.white.cpy();
        public Vec3 camDir = new Vec3();

        public PlanetObjectShader(Fi vert, Fi frag){
            super(vert, frag);
        }

        @Override
        public void apply(){
            //TODO hacky; should use 'public Planet planet;' and assign the planet before rendering.
            //I would fix this myself but I don't want to mess with the ShaderRef stuff - anuke
            camDir.set(renderer.planets.cam.direction).rotate(Vec3.Y, ui.planet.state.planet.getRotation());

            setUniformf("u_lightdir", lightDir);
            setUniformf("u_ambientColor", ambientColor.r, ambientColor.g, ambientColor.b);
            setUniformf("u_camdir", camDir);
        }

        public <T extends PlanetObjectShader> Cons<T> cons(Planet planet){
            return s -> {
                s.lightDir.set(planet.solarSystem.position).sub(planet.position).rotate(Vec3.Y, planet.getRotation()).nor();
                s.ambientColor.set(planet.solarSystem.lightColor);
            };
        }
    }

    public static class VerticalBuildShader extends Shader{
        public float progress, time;
        public Color color = new Color();
        public TextureRegion region;

        public VerticalBuildShader(){
            super(
                Core.files.internal("shaders/default.vert"),
                tree.get("shaders/vertbuild.frag")
            );
        }

        @Override
        public void apply(){
            setUniformf("u_time", time);
            setUniformf("u_color", color);
            setUniformf("u_progress", progress);
            setUniformf("u_uv", region.u, region.v);
            setUniformf("u_uv2", region.u2, region.v2);
            setUniformf("u_texsize", region.texture.width, region.texture.height);
        }
    }

    public static class MegalithRingShader extends PlanetObjectShader{
        protected final Texture texture;

        public MegalithRingShader(){
            super(
                tree.get("shaders/megalithring.vert"),
                tree.get("shaders/megalithring.frag")
            );

            texture = new Texture(tree.get("models/megalithring.png"));
            texture.setFilter(TextureFilter.linear);
            texture.setWrap(TextureWrap.repeat);
        }

        @Override
        public void apply(){
            super.apply();

            texture.bind(1);
            renderer.effectBuffer.getTexture().bind(0);

            setUniformi("u_ringTexture", 1);
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
            return get(render.material.mask() | Models.environment.mask(), render.meshPart.mesh.attributes);
        }

        public Graphics3DShader get(long mask, VertexAttribute[] attributes){
            if(!shaders.containsKey(mask)){
                String prefix = "\n";

                if(Structs.indexOf(attributes, VertexAttribute.color) != -1) prefix += define("color");

                if(Models.environment.mask() != 0) prefix += define("lighting");
                if((mask & ColorAttribute.ambientLight) != 0) prefix += define("ambientLight");
                if((mask & DirectionalLightsAttribute.light) != 0){
                    prefix += defineRaw("numDirectionalLights " + Models.environment.<DirectionalLightsAttribute>get(DirectionalLightsAttribute.light).lights.size);
                }else{
                    prefix += defineRaw("numDirectionalLights 0");
                }

                if((mask & PointLightsAttribute.light) != 0){
                    prefix += defineRaw("numPointLights " + Models.environment.<PointLightsAttribute>get(PointLightsAttribute.light).lights.size);
                }else{
                    prefix += defineRaw("numPointLights 0");
                }

                if((mask & TextureAttribute.diffuse) != 0) prefix += define(TextureAttribute.diffuseAlias);
                if((mask & ColorAttribute.diffuse) != 0) prefix += define(ColorAttribute.diffuseAlias);
                if((mask & TextureAttribute.specular) != 0) prefix += define(TextureAttribute.specularAlias);
                if((mask & ColorAttribute.specular) != 0) prefix += define(ColorAttribute.specularAlias);
                if((mask & TextureAttribute.emissive) != 0) prefix += define(TextureAttribute.emissiveAlias);
                if((mask & ColorAttribute.emissive) != 0) prefix += define(ColorAttribute.emissiveAlias);
                if((mask & FloatAttribute.shininess) != 0) prefix += define(FloatAttribute.shininessAlias);
                if((mask & FloatAttribute.alphaTest) != 0) prefix += define(FloatAttribute.alphaTestAlias);

                shaders.put(mask, new Graphics3DShader(prefix + vertSource, prefix + fragSource));
            }

            return shaders.get(mask);
        }

        public String define(String alias){
            return "#define " + alias + "Flag" + "\n";
        }

        public String defineRaw(String alias){
            return "#define " + alias + "\n";
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
            Camera3D camera = Models.camera;
            Material material = render.material;
            Environment env = Models.environment;

            setUniformMatrix4("u_proj", camera.combined.val);
            setUniformMatrix4("u_trans", render.worldTransform.val);

            setUniformf("u_camPos", camera.position.x, camera.position.y, camera.position.z, 1.1881f / (camera.far * camera.far));
            setUniformf("u_res", camera.width, camera.height);
            setUniformf("u_scl", Core.graphics.getWidth() / camera.width);

            // I don't know where this value came from. I literally just did trial-and-error so many times that I came
            // up with this value because some positions misbehaved, or maybe I just suck at math, or both. I don't
            // really care, if it works it works.
            setUniformf("u_zscl", 2.0415f);

            float[] mval = Tmp.m1.val, wval = render.worldTransform.val;
            mval[Mat.M00] = wval[Mat3D.M00];
            mval[Mat.M10] = wval[Mat3D.M10];
            mval[Mat.M20] = wval[Mat3D.M20];
            mval[Mat.M01] = wval[Mat3D.M01];
            mval[Mat.M11] = wval[Mat3D.M11];
            mval[Mat.M21] = wval[Mat3D.M21];
            mval[Mat.M02] = wval[Mat3D.M02];
            mval[Mat.M12] = wval[Mat3D.M12];
            mval[Mat.M22] = wval[Mat3D.M22];
            setUniformMatrix("u_normalMatrix", Tmp.m1.inv().transpose());

            BlendingAttribute blend = material.get(BlendingAttribute.blend);
            setUniformf("u_opacity", blend != null ? blend.opacity : 1f);

            FloatAttribute shine = material.get(FloatAttribute.shininess);
            if(shine != null) setUniformf("u_shininess", shine.value);

            TextureAttribute diff = material.get(TextureAttribute.diffuse);
            ColorAttribute diffCol = material.get(ColorAttribute.diffuse);
            if(diffCol != null) setUniformf("u_diffuseColor", diffCol.color);
            if(diff != null){
                setUniformi("u_diffuseTexture", Models.bind(diff, 6));
                setUniformf("u_diffuseUVTransform", diff.offsetU, diff.offsetV, diff.scaleU, diff.scaleV);
            }

            TextureAttribute spec = material.get(TextureAttribute.specular);
            ColorAttribute specCol = material.get(ColorAttribute.specular);
            if(specCol != null) setUniformf("u_specularColor", specCol.color);
            if(spec != null){
                setUniformi("u_specularTexture", Models.bind(spec, 5));
                setUniformf("u_specularUVTransform", spec.offsetU, spec.offsetV, spec.scaleU, spec.scaleV);
            }

            TextureAttribute em = material.get(TextureAttribute.emissive);
            ColorAttribute emCol = material.get(ColorAttribute.emissive);
            if(emCol != null) setUniformf("u_emissiveColor", emCol.color);
            if(em != null){
                setUniformi("u_emissiveTexture", Models.bind(em, 4));
                setUniformf("u_emissiveUVTransform", em.offsetU, em.offsetV, em.scaleU, em.scaleV);
            }

            TextureAttribute ref = material.get(TextureAttribute.reflection);
            ColorAttribute refCol = material.get(ColorAttribute.reflection);
            if(refCol != null) setUniformf("u_reflectionColor", refCol.color);
            if(ref != null){
                setUniformi("u_reflectionTexture", Models.bind(ref, 3));
                setUniformf("u_reflectionUVTransform", ref.offsetU, ref.offsetV, ref.scaleU, ref.scaleV);
            }

            TextureAttribute am = material.get(TextureAttribute.ambient);
            ColorAttribute amCol = material.get(ColorAttribute.ambient);
            if(amCol != null) setUniformf("u_ambientColor", amCol.color);
            if(am != null){
                setUniformi("u_ambientTexture", Models.bind(am, 2));
                setUniformf("u_ambientUVTransform", am.offsetU, am.offsetV, am.scaleU, am.scaleV);
            }

            TextureAttribute nor = material.get(TextureAttribute.normal);
            if(nor != null){
                setUniformi("u_normalTexture", Models.bind(nor, 1));
                setUniformf("u_normalUVTransform", nor.offsetU, nor.offsetV, nor.scaleU, nor.scaleV);
            }

            renderer.effectBuffer.getTexture().bind(0);

            ColorAttribute aml = env.get(ColorAttribute.ambientLight);
            if(aml != null) setUniformf("u_ambientLight", aml.color);

            DirectionalLightsAttribute dirl = env.get(DirectionalLightsAttribute.light);
            if(dirl != null){
                for(int i = 0; i < dirl.lights.size; i++){
                    DirectionalLight light = dirl.lights.get(i);
                    setUniformf("u_dirLights[" + i + "].color", light.color);
                    setUniformf("u_dirLights[" + i + "].direction", light.direction);
                }
            }
        }
    }
}
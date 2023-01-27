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
import arc.util.*;
import unity.graphics.*;
import unity.util.*;

import java.lang.reflect.*;

import static arc.graphics.gl.Shader.*;
import static mindustry.Vars.*;
import static mindustry.graphics.Shaders.*;

/** Lists all {@link Shader}s the mod has to load. */
public final class PUShaders{
    public static PlanetObjectShader planet;
    public static PUSurfaceShader eneraphyte, pit, waterpit;
    public static InvisibilityShader dimensionshift;
    public static EndAreaShader endAreaShader;
    public static EndAirShader endAirShader;

    private PUShaders(){
        throw new AssertionError();
    }

    public static void load(){
        if(headless) return;

        planet = new PlanetObjectShader();
        eneraphyte = new PUSurfaceShader("eneraphyte", "noise");
        pit = new PitShader("pit", 9, "noise", "concrete-blank1", "stone-sheet", "truss");
        waterpit = new PitShader("waterpit", 9, "noise", "concrete-blank1", "stone-sheet");
        dimensionshift = new InvisibilityShader("dimensionshift");
        endAreaShader = new EndAreaShader();
        endAirShader = new EndAirShader();

        RegionBatch.init();
    }

    public static <T extends Shader> T preprocess(String vertPreprocess, String fragPreprocess, Prov<T> create){
        prependVertexCode = vertPreprocess;
        prependFragmentCode = fragPreprocess;

        T shader = create.get();

        prependVertexCode = "";
        prependFragmentCode = "";
        return shader;
    }

    /**
     * <p>A surface shader that loads modded textures in {@code shaders/textures/}. Texture uniforms that correspond with the
     * texture list should be named as {@code u_texture[n + 1]} (e.g. {@code u_texture1}, {@code u_texture2}, ...), whereas the
     * frame buffer texture uniform can be of any name (e.g. {@code u_noise}, {@code u_texture}, ...).</p>
     *
     * <p>There are also 4 preset uniforms: {@code u_campos}, {@code u_resolution}, {@code u_viewport} and {@code u_time};
     * corresponds to the camera position, the camera dimension, the screen dimension, and {@link Time#time time}.</p>
     *
     * <p><b>The shader only supports up to 8 textures</b>: 1 frame buffer texture and 7 additional textures.</p>
     * @author GlennFolker
     */
    public static class PUSurfaceShader extends Shader{
        public Texture[] textures;

        public PUSurfaceShader(String frag, String... textures){
            this(getShaderFi("screenspace.vert"), file(frag + ".frag"), textures);
        }

        public PUSurfaceShader(Fi vert, Fi frag, String... textures){
            super(vert.readString(), frag.readString());
            loadTextures(textures);
        }

        protected void loadTextures(String... textureNames){
            if(textureNames.length > 7) throw new IllegalArgumentException("Max custom texture amount is 7.");
            if(textures != null) for(Texture texture : textures) texture.dispose();

            textures = new Texture[textureNames.length];
            for(int i = 0; i < textures.length; i++){
                Texture texture = new Texture(tree.get(tex(textureNames[i])));
                texture.setFilter(TextureFilter.linear);
                texture.setWrap(TextureWrap.repeat);
                textures[i] = texture;
            }
        }

        @Override
        public void apply(){
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2f, Core.camera.position.y - Core.camera.height / 2f);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_viewport", Core.graphics.getWidth(), Core.graphics.getHeight());
            setUniformf("u_time", Time.time);

            for(int i = textures.length - 1; i >= 0; i--){
                int unit = i + 1;
                String name = "u_texture" + unit;

                if(hasUniform(name)){
                    textures[i].bind(unit);
                    setUniformi(name, unit);
                }
            }

            renderer.effectBuffer.getTexture().bind(0);
        }

        @Override
        public void dispose(){
            super.dispose();
            if(textures != null){
                for(Texture texture : textures) texture.dispose();
                textures = null;
            }
        }
    }

    /**
     * {@link PlanetShader} but with correct normal transformations and an additional emission color.
     * @author GlennFolker
     */
    public static class PlanetObjectShader extends Shader{
        public Vec3 lightDir = new Vec3(1f, 1f, 1f).nor();
        public Color ambientColor = Color.white.cpy();
        public Color emissionColor = Color.clear.cpy();

        public PlanetObjectShader(){
            super(file("planet.vert"), getShaderFi("planet.frag"));
        }

        @Override
        public void apply(){
            Camera3D cam = renderer.planets.cam;

            setUniformf("u_lightdir", lightDir);
            setUniformf("u_ambientColor", ambientColor);
            setUniformf("u_emissionColor", emissionColor);
            setUniformf("u_camdir", cam.direction);
            setUniformf("u_campos", cam.position);
        }
    }

    public static Fi file(String path){
        return tree.get("shaders/" + path);
    }

    public static String tex(String name){
        return "shaders/textures/" + name + ".png";
    }

    /**
     * Not much differs from {@link PUSurfaceShader}.
     * As number of textures are limited, uses variants in texture sheet. Similar as atlas.
     * @author younggam, xelo
     */
    public static class PitShader extends PUSurfaceShader{
        public int variants;

        public PitShader(String frag, int variants, String... textures){
            super(frag, textures);
            this.variants = variants;
        }

        public PitShader(Fi vert, Fi frag, int variants, String... textures){
            super(vert, frag, textures);
            this.variants = variants;
        }

        @Override
        public void apply(){
            setUniformf("variants", (float)variants);
            super.apply();
        }
    }

    public static class InvisibilityShader extends Shader{
        public float progress;
        public Color overrideColor = Color.white.cpy();
        private Batch lastBatch;

        private static final Field csField = ReflectUtils.findf(Batch.class, "customShader"),
        shaderField = ReflectUtils.findf(Batch.class, "shader");
        private static Shader baseShader;

        public InvisibilityShader(String fragmentShader){
            super(file("defaultwmix.vert"), file(fragmentShader + ".frag"));
        }

        @Override
        public void apply(){
            RegionBatch b = (RegionBatch)Core.batch;

            setUniformf("u_progress", progress);
            setUniformf("u_override_color", overrideColor);

            setUniformf("u_uv", b.u, b.v);
            setUniformf("u_uv2", b.u2, b.v2);
            //setUniformf("u_texsize", region.texture.width, region.texture.height);
            setUniformf("u_texsize", b.getTexture().width, b.getTexture().height);
        }

        public void updateBaseShader(){
            if(baseShader == null){
                try{
                    baseShader = (Shader)shaderField.get(Core.batch);
                }catch(Exception e){
                    baseShader = SpriteBatch.createShader();
                    Log.err(e);
                }
            }
        }

        public void begin(){
            updateBaseShader();
            lastBatch = Core.batch;
            Mat proj = Draw.proj(), trans = Draw.trans();
            Core.batch = RegionBatch.batch;
            Draw.proj(proj);
            Draw.trans(trans);
            Draw.shader(this);
        }

        public void end(){
            Draw.flush();
            Core.batch = lastBatch;
            baseShader.bind();
        }
    }

    public static class EndAreaShader extends Shader{
        public float width = 2f;
        public Color color = new Color();

        public EndAreaShader(){
            super(getShaderFi("screenspace.vert"), file("endareashader.frag"));
        }

        @Override
        public void apply(){
            super.apply();
            setUniformf("u_color", color);
            setUniformf("u_offset_x", Mathf.sin(Time.time / 60f) * width);
            setUniformf("u_offset_y", Mathf.cos(Time.time / 60f) * width);

            //setUniformf("u_texsize", Core.camera.width, Core.camera.height);
            setUniformf("u_invsize", 1f/Core.camera.width, 1f/Core.camera.height);
        }
    }

    public static class EndAirShader extends Shader{
        public float offsetX, offsetY, div;
        public Texture tex;

        public EndAirShader(){
            super(getShaderFi("screenspace.vert"), file("endair.frag"));
        }

        @Override
        public void apply(){
            super.apply();
            setUniformf("u_offset", offsetX, offsetY);
            setUniformf("u_div", div);

            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2f, Core.camera.position.y - Core.camera.height / 2f);
            setUniformf("u_invsize", 1f/Core.camera.width, 1f/Core.camera.height);
            setUniformf("u_camsize", Core.camera.width, Core.camera.height);
            
            setUniformf("u_texsize", tex.width, tex.height);
        }
    }
}

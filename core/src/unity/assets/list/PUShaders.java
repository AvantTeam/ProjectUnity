package unity.assets.list;

import arc.files.*;
import arc.graphics.*;
import arc.graphics.g3d.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import mindustry.graphics.*;
import mindustry.graphics.Shaders.*;

import static mindustry.Vars.*;

/** Lists all {@link Shader}s the mod has to load. */
public final class PUShaders{
    public static PlanetObjectShader planet;

    public static void load(){
        if(headless) return;

        planet = new PlanetObjectShader();
    }

    /**
     * {@link PlanetShader} but with correct normal transformations.
     * @author GlennFolker
     */
    public static class PlanetObjectShader extends Shader{
        public Vec3 lightDir = new Vec3(1f, 1f, 1f).nor();
        public Color ambientColor = Color.white.cpy();
        public Color emissionColor = Color.clear.cpy();

        public PlanetObjectShader(){
            super(file("planet.vert"), Shaders.getShaderFi("planet.frag"));
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
}

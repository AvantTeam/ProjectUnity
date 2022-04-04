package unity.graphics;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.graphics.*;
import mindustry.graphics.Shaders.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;

/**
 * @author GlennFolker
 */
public class CompositeMesh extends PlanetMesh{
    public Seq<MeshComp> comps = new Seq<>();

    /**
     * Note that the superclass' {@link #mesh} won't be actually used.
     * @param objects Triple of Mesh, ShaderRef, and Blending. Note that all the meshes will be disposed after usage no matter what.
     *                See {@link unity.util.GraphicUtils#copy(Mesh)}.
     */
    public CompositeMesh(Planet planet, Object... objects){
        super(planet, null, null);
        for(int i = 0; i < objects.length - 2; i += 3){
            comps.add(new MeshComp((Mesh)objects[i], (ShaderRef<?>)objects[i + 1], (Blending)objects[i + 2]));
        }
    }

    public static Mesh defMesh(Planet planet, int divisions){
        return MeshBuilder.buildHex(planet.generator, divisions, false, planet.radius, 0.2f);
    }

    public static ShaderRef<PlanetShader> defShader(Planet planet){
        return new ShaderRef<>(Shaders.planet, s -> {
            s.lightDir.set(planet.solarSystem.position).sub(planet.position).rotate(Vec3.Y, planet.getRotation()).nor();
            s.ambientColor.set(planet.solarSystem.lightColor);
        });
    }

    @Override
    public void render(PlanetParams params, Mat3D projection, Mat3D transform){
        for(MeshComp e : comps) e.preRender();
        for(MeshComp e : comps) e.render(projection, transform);
        Blending.normal.apply();
    }

    public static class MeshComp{
        public final Mesh mesh;
        public final ShaderRef<?> shader;
        public final Blending blend;

        MeshComp(Mesh mesh, ShaderRef<?> shader, Blending blend){
            this.mesh = mesh;
            this.shader = shader;
            this.blend = blend;
        }

        public void preRender(){
            shader.apply();
        }

        public void render(Mat3D projection, Mat3D transform){
            Shader s = shader.shader;
            s.bind();
            s.setUniformMatrix4("u_proj", projection.val);
            s.setUniformMatrix4("u_trans", transform.val);
            s.apply();

            blend.apply();
            mesh.render(s, Gl.triangles);
        }
    }

    public static class ShaderRef<T extends Shader>{
        public final T shader;
        public final Cons<T> apply;

        public ShaderRef(T shader, Cons<T> apply){
            this.shader = shader;
            this.apply = apply;
        }

        public void apply(){
            apply.get(shader);
        }
    }
}

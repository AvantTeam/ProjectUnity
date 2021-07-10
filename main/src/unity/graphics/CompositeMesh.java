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

public class CompositeMesh extends PlanetMesh{
    public Seq<MeshComp> comps = new Seq<>();

    /**
     * Note that the {@link #mesh} won't be actually used.
     * @param objects Pair of Mesh and ShaderRef. Note that all the meshes will be disposed after usage no matter what.
     *                See {@link unity.util.GraphicUtils#copy(Mesh)}.
     */
    public CompositeMesh(Planet planet, Object... objects){
        super(planet, null, null);

        ObjectMap<Mesh, ShaderRef<?>> pair = ObjectMap.of(objects);
        for(var e : pair.entries()){
            comps.add(new MeshComp(e.key, e.value));
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
    public void preRender(){
        for(var e : comps){
            e.shader.apply();
        }
    }

    @Override
    public void render(Mat3D projection, Mat3D transform){
        preRender();
        for(var e : comps){
            var s = e.shader.shader;
            s.bind();
            s.setUniformMatrix4("u_proj", projection.val);
            s.setUniformMatrix4("u_trans", transform.val);
            s.apply();

            e.mesh.render(s, Gl.triangles);
        }
    }

    @Override
    public void dispose(){
        for(var e : comps){
            e.mesh.dispose();
        }
    }

    public static class MeshComp{
        public final Mesh mesh;
        public final ShaderRef<?> shader;

        MeshComp(Mesh mesh, ShaderRef<?> shader){
            this.mesh = mesh;
            this.shader = shader;
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

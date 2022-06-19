package unity.content;

import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import unity.graphics.g3d.*;
import unity.mod.*;
import unity.util.*;
import unity.world.planets.*;

import static unity.graphics.MonolithPalettes.*;

/**
 * Defines all {@linkplain Faction#monolith monolith} planets.
 * @author GlennFolker
 */
public final class MonolithPlanets{
    public static Planet megalith;

    private MonolithPlanets(){
        throw new AssertionError();
    }

    public static void load(){
        megalith = new Planet("megalith", Planets.sun, 1f, 3){{
            Color[] colors = {monolithLight, monolithLighter};
            Func<Integer, PlanetMesh> ring = id -> {
                int i = id * 4;
                return new PlanetMesh(this, PUMeshBuilder.createToroid(1.2f, 0.1f, 0.01f, 80, 6, prog -> {
                    Tmp.v1.trns(prog * 360f, 1f);
                    return Tmp.c1.set(monolithMid).lerp(colors, Simplex.noise2d(i, 4d, 0.67d, 2.1d, Tmp.v1.x, Tmp.v1.y)).toFloatBits();
                }), Shaders.planet){
                    final float
                        init1 = Mathf.randomSeed(i + 1, 360f),
                        init2 = Mathf.randomSeed(i + 2, 360f),
                        init3 = Mathf.randomSeed(i + 3, 360f);

                    @Override
                    public void preRender(PlanetParams params){
                        Shaders.planet.planet = planet;
                        Shaders.planet.lightDir.set(planet.solarSystem.position).sub(planet.position).rotate(Vec3.Y, planet.getRotation()).nor();
                        Shaders.planet.ambientColor.set(planet.solarSystem.lightColor);
                    }

                    @Override
                    public void render(PlanetParams params, Mat3D projection, Mat3D transform){
                        preRender(params);
                        shader.bind();
                        shader.setUniformMatrix4("u_proj", projection.val);
                        shader.setUniformMatrix4("u_trans", MathUtils.m31.set(transform).rotate(MathUtils.q1
                            .set(Vec3.Y, Time.globalTime + init1)
                            .mul(MathUtils.q2.set(Vec3.X, Time.globalTime + init2))
                            .add(MathUtils.q2.set(Vec3.Z, Time.globalTime + init3))
                            .nor()
                        ).val);

                        shader.apply();
                        mesh.render(shader, Gl.triangles);
                    }
                };
            };

            generator = new MegalithPlanetGenerator();
            meshLoader = () -> new MultiMesh(
                new HexMesh(this, 6),
                ring.get((int)id),
                ring.get(id + 1),
                ring.get(id + 2)
            );
        }};
    }
}

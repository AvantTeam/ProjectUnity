package unity.content;

import arc.graphics.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.assets.list.*;
import unity.gen.*;
import unity.graphics.*;
import unity.graphics.CompositeMesh.*;
import unity.map.planets.*;
import unity.util.*;

public class UnityPlanets{
    public static @FactionDef("imber") Planet electrode, inert;

    public static @FactionDef("monolith") Planet megalith;

    public static void load(){
        megalith = new Planet("megalith", Planets.sun, 1f, 3){{
            generator = new MegalithPlanetGenerator();
            meshLoader = () -> new CompositeMesh(this,
                // Planet.
                CompositeMesh.defMesh(this, 6),
                CompositeMesh.defShader(this),
                Blending.normal,

                // Ring.
                GraphicUtils.copy(UnityModels.megalithring.meshes.first()),
                new ShaderRef<>(UnityShaders.megalithRingShader, UnityShaders.megalithRingShader.cons(this)),
                Blending.additive
            );

            accessible = true;
            atmosphereColor = UnityPal.monolithAtmosphere;
            startSector = 200;
            atmosphereRadIn = 0.04f;
            atmosphereRadOut = 0.35f;
        }};

        electrode = new Planet("electrode", Planets.sun, 1f, 3){{
            generator = new ElectrodePlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            accessible = true;
            atmosphereColor = Pal.surge;
            startSector = 30;
        }};

        inert = new Planet("inert", electrode, 0.5f){{
            atmosphereColor = Color.white.cpy();
            accessible = false;
            meshLoader = () -> new ColorMesh(
                this, 3, 4, 0.3, 1.7, 1.2, 1, 0.9f,
                Color.valueOf("121211"),
                Color.valueOf("141414"),
                Color.valueOf("131313"),
                Color.valueOf("181617"),
                Color.valueOf("191415"),
                Color.valueOf("101111")
            );
        }};
    }
}

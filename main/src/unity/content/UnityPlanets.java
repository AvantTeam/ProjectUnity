package unity.content;

import arc.graphics.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.graphics.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import unity.graphics.*;
import unity.planets.*;

public class UnityPlanets implements ContentList{
    public static Planet

    megalith, electrode, inert;

    @Override
    public void load(){
        megalith = new Planet("megalith", Planets.sun, 3, 1){
            {
                generator = new MegalithPlanetGenerator();
                meshLoader = () -> new HexMesh(megalith, 6);
                atmosphereColor = Color.valueOf("0f3ad2");
                startSector = 30;
            }
        };

        electrode = new Planet("electrode", Planets.sun, 3, 1){
            {
                generator = new ElectrodePlanetGenerator();
                meshLoader = () -> new HexMesh(electrode, 6);
                atmosphereColor = Pal.surge;
                startSector = 30;
            }
        };

        inert = new Planet("inert", electrode, 0, 0.5f){
            {
                atmosphereColor = Color.white.cpy();
                accessible = false;
                meshLoader = () -> new ColorMesh(
                this, 3, 4, 0.3, 1.7, 1.2, 1, 0.9f,
                Color.valueOf("121211"),
                Color.valueOf("141414"),
                Color.valueOf("131313"),
                Color.valueOf("181617"),
                Color.valueOf("191415"),
                Color.valueOf("101111"));
            }
        };
    }
}
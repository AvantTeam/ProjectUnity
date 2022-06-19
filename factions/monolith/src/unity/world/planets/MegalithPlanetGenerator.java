package unity.world.planets;

import arc.graphics.*;
import arc.math.geom.*;
import arc.util.noise.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;

/**
 * Planet mesh and sector generator for the  planet.
 * @author GlennFolker
 */
public class MegalithPlanetGenerator extends PlanetGenerator{
    private static final Color color = new Color();

    @Override
    public float getHeight(Vec3 position){
        return 1f;
    }

    @Override
    public Color getColor(Vec3 position){
        return Color.black;
    }

    protected float rawHeight(Vec3 position){
        return Simplex.noise3d(seed, 4, 0.6f, 1.1f, position.x, position.y, position.z);
    }

    protected Block getBlock(Vec3 position){
        return Blocks.stone;
    }

    @Override
    public boolean allowLanding(Sector sector){
        //TODO avoid landing on crater sectors until the "requirements" have been fulfilled.
        return super.allowLanding(sector);
    }

    @Override
    public void generateSector(Sector sector){
        //TODO i don't know how the bases should be...
    }

    @Override
    public void addWeather(Sector sector, Rules rules){
        //TODO i don't know how any of this works (yet), when i do i'll make the sectors have a "very cool" megalith climate
        super.addWeather(sector, rules);
    }

    @Override
    protected void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 20f) > 0.67f){
            tile.block = Blocks.air;
        }
    }
}

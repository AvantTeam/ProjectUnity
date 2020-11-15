package unity.world.blocks.defense;

import mindustry.world.blocks.defense.Wall;
import unity.world.blocks.LightRepeaterBuildBase;

public class LightWall extends Wall{
    public LightWall(String name){
        super(name);
        update = true;
        absorbLasers = true;
    }

    public class LightWallBuild extends WallBuild implements LightRepeaterBuildBase{

    }
}

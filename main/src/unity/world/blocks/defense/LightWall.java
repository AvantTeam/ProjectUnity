package unity.world.blocks.defense;

import mindustry.world.blocks.defense.*;
import unity.world.blocks.*;

/** @deprecated Just don't use it. */
public class LightWall extends Wall{
    public LightWall(String name){
        super(name);
        update = true;
        absorbLasers = true;
    }

    public class LightWallBuild extends WallBuild implements LightRepeaterBuildBase{

    }
}

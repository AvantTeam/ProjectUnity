package unity.world.blocks;

import unity.world.meta.*;

public interface LightRepeaterBuildBase{
    default LightData calcLight(LightData ld, int i){
        return new LightData(ld).length(ld.length - i + 1);
    }
}

package unity.world.blocks.effect;

import unity.annotations.Annotations.*;
import unity.gen.*;

@Merge(value = SoulHoldc.class)
public class SoulContainer extends SoulHoldBlock{
    public SoulContainer(String name){
        super(name);
    }

    //TODO also strip the "Building" suffix in the annotation processor to result "SoulBuild" instead
    public class SoulContainerBuild extends SoulBuildingBuild{

    }
}

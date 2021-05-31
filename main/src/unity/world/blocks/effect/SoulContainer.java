package unity.world.blocks.effect;

import unity.annotations.Annotations.*;
import unity.gen.*;

@Merge(value = SoulHoldc.class)
public class SoulContainer extends SoulHoldBlock{
    public SoulContainer(String name){
        super(name);
    }

    public class SoulContainerBuild extends SoulBuild{

    }
}

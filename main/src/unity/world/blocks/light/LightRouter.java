package unity.world.blocks.light;

import unity.annotations.Annotations.*;
import unity.gen.*;

@Merge(LightHoldc.class)
public class LightRouter extends LightHoldBlock{
    public LightRouter(String name){
        super(name);
        requiresLight = false;
    }

    public class LightRouterBuild extends LightHoldBuild{

    }
}

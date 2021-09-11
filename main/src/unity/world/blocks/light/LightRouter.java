package unity.world.blocks.light;

import unity.annotations.Annotations.*;
import unity.gen.*;

@Merge(LightHoldc.class)
public class LightRouter extends LightHoldBlock{
    public LightRouter(String name){
        super(name);
        rotate = true;
    }

    public class LightRouterBuild extends LightHoldBuild{
        /** Format: x -> left, y -> right, z -> front */
        public byte config = Bool3.construct(true, true, true);

        @Override
        public Byte config(){
            return config;
        }
    }
}

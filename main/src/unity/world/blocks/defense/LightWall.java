package unity.world.blocks.defense;

import mindustry.world.blocks.defense.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.world.*;

@Merge(base = Wall.class, value = LightHoldc.class)
public class LightWall extends LightHoldWall{
    public float suppression = 0.8f;

    public LightWall(String name){
        super(name);
    }

    @Override
    public void init(){
        super.init();
        acceptors.add(new LightAcceptorType(){{
            x = 0;
            y = 0;
            width = size;
            height = size;
            required = -1f;
        }});
    }

    public class LightWallBuild extends LightHoldWallBuild{
        @Override
        public void interact(Light light){
            light.child(l -> Float2.construct(l.rotation(), suppression));
        }
    }
}

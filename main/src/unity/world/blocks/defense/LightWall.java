package unity.world.blocks.defense;

import mindustry.world.blocks.defense.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

@Merge(base = Wall.class, value = LightHoldc.class)
public class LightWall extends LightHoldWall{
    public float suppression = 0.8f;

    public LightWall(String name){
        super(name);
        acceptsLight = true;
    }

    public class LightWallBuild extends LightHoldWallBuild{
        @Override
        public void interact(Light light){
            light.child(l -> Float2.construct(l.rotation(), suppression));
        }
    }
}

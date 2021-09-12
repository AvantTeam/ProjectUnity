package unity.world.blocks.light;

import unity.annotations.Annotations.*;
import unity.gen.*;

/** @author GlennFolker */
@Merge(LightHoldc.class)
public class LightDiffractor extends LightHoldBlock{
    public int diffractionCount = 3;
    public float minAngle = 22.5f;
    public float maxAngle = 90f;

    public LightDiffractor(String name){
        super(name);
        solid = true;
        configurable = true;
    }

    public class LightDiffractorBuild extends LightHoldBuild{

    }
}

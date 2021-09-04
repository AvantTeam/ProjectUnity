package unity.world.blocks.light;

import mindustry.world.blocks.production.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

/** @author GlennFolker */
@Merge(base = GenericCrafter.class, value = LightHoldc.class)
public class LightSource extends LightHoldGenericCrafter{
    public float lightProduction = 1f;

    public float angleRange = 22.5f;
    public float rotateSpeed = 5f;

    public LightSource(String name){
        super(name);
        solid = true;
        requiresLight = false;
        acceptsLight = false;
        configurable = true;
        outlineIcon = true;
    }

    public class LightSourceBuild extends LightHoldGenericCrafterBuild{

    }
}

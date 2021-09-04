package unity.world.blocks.light;

import arc.*;
import arc.graphics.g2d.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

/** @author GlennFolker */
@Merge(LightHoldc.class)
public class LightReflector extends LightHoldBlock{
    public float angleRange = 22.5f;
    public float rotateSpeed = 5f;

    public TextureRegion baseRegion;

    public LightReflector(String name){
        super(name);
        solid = true;
        requiresLight = false;
        acceptsLight = true;
        configurable = true;
        outlineIcon = true;
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    public class LightReflectorBuild extends LightHoldBuild{

    }
}

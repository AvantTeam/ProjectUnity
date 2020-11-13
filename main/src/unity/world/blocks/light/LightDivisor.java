package unity.world.blocks.light;

import static arc.Core.atlas;

public class LightDivisor extends LightReflector{
    /** change this */
    private static final String spriteName = "unity-light-divisor";

    public LightDivisor(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        angleRegions[0] = atlas.find(spriteName + (diagonal ? "" : "-1"));
        angleRegions[1] = atlas.find(spriteName + (diagonal ? "-2" : "-3"));
    }
}

package unity.world.blocks.exp;

import arc.graphics.g2d.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;

//a secondary base used for @Dupe's building. Every public field and method will have @Override added, and super added in front. Use @Blocc to add fields to the block instead of the building.
public class LevelDrawBase {
    @Blocc public TextureRegion[] levelRegions; //level top regions
    @Blocc public TextureRegion edgeRegion, shieldRegion;
    @Blocc public float shieldZ = Layer.buildBeam;

    @Ignore TextureRegion region;

    public void draw(){

    }

    @Ignore float levelf(){
        return 0;
    }

    protected TextureRegion levelRegion(){
        if(levelRegions == null) return region;
        return levelRegions[Math.min((int)(levelf() * levelRegions.length), levelRegions.length - 1)];
    }
}

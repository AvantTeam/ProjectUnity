package unity.graphics.g2d;

import arc.graphics.g2d.*;

public class UnityTextureAtlas extends TextureAtlas{
    public void merge(TextureAtlas atlas){
        for(AtlasRegion region : getRegions()){
            atlas.addRegion(region.name, region);
        }
    }
}

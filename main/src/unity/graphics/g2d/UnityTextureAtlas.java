package unity.graphics.g2d;

import arc.graphics.g2d.*;

public class UnityTextureAtlas extends TextureAtlas{
    public UnityTextureAtlas(TextureAtlasData data){
        super(data);
    }

    public void merge(TextureAtlas atlas){
        for(AtlasRegion region : getRegions()){
            region.name = parseName(region.name);
            atlas.addRegion(region.name, region);
        }
    }

    protected String parseName(String name){
        String parsed = name;

        int i;
        if((i = parsed.lastIndexOf("/")) != -1){
            parsed = parsed.substring(i + 1, parsed.length() - 1);
        }

        if(!parsed.startsWith("unity")){
            parsed = "unity-" + parsed;
        }

        return parsed;
    }
}

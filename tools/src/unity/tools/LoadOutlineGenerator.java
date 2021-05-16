package unity.tools;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.util.*;
import unity.gen.*;

import java.lang.reflect.*;

public class LoadOutlineGenerator implements Generator{
    @Override
    public void generate(){
        try{
            Regions.load();

            Func<TextureRegion, String> parseName = reg -> ((AtlasRegion)reg).name.replaceFirst("unity-", "");
            for(Field field : Regions.class.getDeclaredFields()){
                if(!TextureRegion.class.isAssignableFrom(field.getType()) || !field.getName().endsWith("OutlineRegion")) continue;

                Field raw = Regions.class.getDeclaredField(field.getName().replaceFirst("Outline", ""));
                TextureRegion region = (TextureRegion)raw.get(null);
                Color outlineColor = Color.valueOf("404049");

                String fname = parseName.get(region);
                if(!SpriteProcessor.has(fname)) continue;

                Sprite sprite = SpriteProcessor.get(fname);
                sprite.draw(sprite.outline(4, outlineColor));
                sprite.antialias();

                sprite.save(fname + "-outline");
            }

            Regions.load();
        }catch(Exception e){
            Log.err("Failed to generate outlines for Regions: " + e.getMessage());
        }
    }
}

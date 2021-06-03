package unity.tools;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import unity.gen.*;

import java.lang.reflect.*;

import static unity.tools.SpriteProcessor.*;

public class LoadOutlineGenerator implements Generator{
    @Override
    public void generate(){
        try{
            Regions.load();

            int outlineColor = Color.valueOf("404049").rgba();
            for(Field field : Regions.class.getDeclaredFields()){
                if(!TextureRegion.class.isAssignableFrom(field.getType()) || !field.getName().endsWith("OutlineRegion")) continue;

                Field raw = Regions.class.getDeclaredField(field.getName().replaceFirst("Outline", ""));
                TextureRegion region = (TextureRegion)raw.get(null);

                String fname = fixName(region);
                if(!has(fname)) continue;

                Pixmap sprite = get(fname);
                sprite.draw(sprite.outline(outlineColor, 4));

                save(sprite, fname, fname + "-outline");
            }

            Regions.load();
        }catch(Exception e){
            Log.err("Failed to generate outlines for Regions: " + e.getMessage());
        }
    }
}

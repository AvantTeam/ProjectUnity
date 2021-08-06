package unity.tools;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;

public class GenAtlas extends TextureAtlas{
    private final ObjectMap<String, GenRegion> regions = new ObjectMap<>();

    @Override
    public GenRegion addRegion(String name, TextureRegion textureRegion){
        var reg = (GenRegion)textureRegion;

        regions.put(name, reg);
        return reg;
    }

    @Override
    public GenRegion addRegion(String name, Texture texture, int x, int y, int width, int height){
        var pixmap = texture.getTextureData().getPixmap();
        var reg = new GenRegion(name, Pixmaps.crop(pixmap, x, y, width, height));

        regions.put(name, reg);
        return reg;
    }

    @Override
    public GenRegion find(String name){
        if(!regions.containsKey(name)){
            var reg = new GenRegion(name, null);
            reg.invalid = true;
            return reg;
        }

        return regions.get(name);
    }

    @Override
    public GenRegion find(String name, String def){
        return regions.get(name, regions.get(def));
    }

    @Override
    public GenRegion find(String name, TextureRegion def){
        return regions.get(name, (GenRegion)def);
    }

    @Override
    public void dispose(){
        for(var pix : regions.values()){
            if(pix.pixmap != null) pix.pixmap.dispose();
        }
        regions.clear();
    }

    public static class GenRegion extends AtlasRegion{
        protected boolean invalid;
        protected Pixmap pixmap;

        public GenRegion(String name, Pixmap pixmap){
            this.name = name;
            this.pixmap = pixmap;
        }

        @Override
        public boolean found(){
            return !invalid;
        }

        /**
         * @return The pixmap associated with this region. Must not be directly modified
         * @see Pixmap#copy()
         * @see Pixmap#dispose()
         */
        public Pixmap pixmap(){
            if(!found()) throw new IllegalArgumentException("Region does not exist: " + name);
            return pixmap;
        }
    }
}

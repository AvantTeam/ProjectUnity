package unity.tools;

import arc.files.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;

public class GenAtlas extends TextureAtlas{
    private final ObjectMap<String, GenRegion> regions = new ObjectMap<>();

    /** The name should be prefixed with {@code unity-} */
    public GenRegion addRegion(Fi file){
        var name = "unity-" + file.nameWithoutExtension();
        var reg = new GenRegion(name, new Pixmap(file));

        var path = file.path();
        path = path.substring(path.indexOf("sprites/") + "sprites/".length(), path.lastIndexOf("/"));
        reg.relativePath = path;

        synchronized(regions){
            regions.put(name, reg);
            return reg;
        }
    }

    /** @inheritDocs. The name should be prefixed with {@code unity-} */
    @Override
    public GenRegion addRegion(String name, TextureRegion textureRegion){
        synchronized(regions){
            var reg = (GenRegion)textureRegion;

            regions.put(name, reg);
            return reg;
        }
    }

    /** @inheritDocs. The name should be prefixed with {@code unity-} */
    @Override
    public GenRegion addRegion(String name, Texture texture, int x, int y, int width, int height){
        var pixmap = texture.getTextureData().getPixmap();
        var reg = new GenRegion(name, Pixmaps.crop(pixmap, x, y, width, height));

        synchronized(regions){
            regions.put(name, reg);
            return reg;
        }
    }

    @Override
    public GenRegion find(String name){
        synchronized(regions){
            if(!regions.containsKey(name)){
                var reg = new GenRegion(name, null);
                reg.invalid = true;
                return reg;
            }

            return regions.get(name);
        }
    }

    @Override
    public GenRegion find(String name, String def){
        synchronized(regions){
            return regions.get(name, regions.get(def));
        }
    }

    @Override
    public GenRegion find(String name, TextureRegion def){
        synchronized(regions){
            return regions.get(name, (GenRegion) def);
        }
    }

    @Override
    public void dispose(){
        synchronized(regions){
            for(var pix : regions.values()){
                if(pix.pixmap != null) pix.pixmap.dispose();
            }

            regions.clear();
        }
    }

    public static class GenRegion extends AtlasRegion{
        public boolean invalid;
        public String relativePath = "";

        private final Pixmap pixmap;

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

package unity.tools;

import arc.*;
import arc.files.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.struct.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.mod.Mods.*;
import unity.*;

import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

import static mindustry.Vars.*;

public class SpriteProcessor{
    private static ObjectMap<String, TextureRegion> regionCache = new ObjectMap<>();
    private static ObjectMap<String, BufferedImage> spriteCache = new ObjectMap<>();

    public static Unity mod = new Unity();

    public static void main(String[] args) throws Exception{
        headless = true;

        content = new ContentLoader();
        content.createBaseContent();

        //setup dummy loaded mod to load unity contents properly
        content.setCurrentMod(new LoadedMod(null, null, mod, new ModMeta(){{
            name = "unity";
        }}));

        try{
            mod.loadContent();
        }catch(StackOverflowError e){}

        content.setCurrentMod(null);

        Fi.get("./sprites").walk(path -> {
            if(!path.extEquals("png")) return;

            path.copyTo(Fi.get("./sprites-gen"));
        });

        Fi.get("./sprites-gen").walk(path -> {
            String fname = path.nameWithoutExtension();

            try{
                BufferedImage sprite = ImageIO.read(path.file());
                if(sprite == null) throw new IOException("sprite " + path.absolutePath() + " is corrupted or invalid!");

                GenRegion region = new GenRegion(fname, path){{
                    width = sprite.getWidth();
                    height = sprite.getHeight();
                    u2 = v2 = 1f;
                    u = v = 0f;
                }};

                regionCache.put(fname, region);
                spriteCache.put(fname, sprite);
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        });

        Core.atlas = new TextureAtlas(){
            @Override
            public AtlasRegion find(String name){
                if(!regionCache.containsKey(name)){
                    GenRegion region = new GenRegion(name, null);
                    region.invalid = true;

                    return region;
                }

                return (AtlasRegion)regionCache.get(name);
            }

            @Override
            public AtlasRegion find(String name, TextureRegion def){
                if(!regionCache.containsKey(name)){
                    return (AtlasRegion)def;
                }

                return (AtlasRegion)regionCache.get(name);
            }

            @Override
            public AtlasRegion find(String name, String def){
                if(!regionCache.containsKey(name)){
                    return (AtlasRegion)regionCache.get(def);
                }

                return (AtlasRegion)regionCache.get(name);
            }

            @Override
            public boolean has(String name){
                return regionCache.containsKey(name);
            }
        };

        Generators.generate();

        Fi.get("./sprites-gen").walk(path -> {
            try{
                BufferedImage image = ImageIO.read(path.file());

                Sprite sprite = new Sprite(image);
                sprite.floorAlpha().antialias();

                sprite.save(path.nameWithoutExtension());
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        });

        Sprite.dispose();
    }

    static BufferedImage buffer(TextureRegion reg){
        return spriteCache.get(((AtlasRegion)reg).name.replaceFirst("mechanical-warfare-", ""));
    }

    static boolean has(String name){
        return Core.atlas.has(name);
    }

    static boolean has(TextureRegion region){
        return has(((AtlasRegion)region).name.replaceFirst("mechanical-warfare-", ""));
    }

    static Sprite get(String name){
        return get(Core.atlas.find(name));
    }

    static Sprite get(TextureRegion region){
        GenRegion.validate(region);

        return new Sprite(spriteCache.get(((AtlasRegion)region).name.replaceFirst("mechanical-warfare-", "")));
    }

    static void err(String message, Object... args){
        throw new IllegalArgumentException(Strings.format(message, args));
    }

    static class GenRegion extends AtlasRegion{
        boolean invalid;
        Fi path;

        GenRegion(String name, Fi path){
            if(name == null) throw new IllegalArgumentException("name is null");
            this.name = name;
            this.path = path;
        }

        @Override
        public boolean found(){
            return !invalid;
        }

        static void validate(TextureRegion region){
            if(((GenRegion)region).invalid){
                err("Region does not exist: @", ((GenRegion)region).name);
            }
        }
    }
}

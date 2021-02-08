package unity.tools;

import arc.*;
import arc.files.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.packer.*;
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
    static ObjectMap<String, TextureRegion> regionCache = new ObjectMap<>();
    static ObjectMap<String, BufferedImage> spriteCache = new ObjectMap<>();
    static ColorBleedEffect bleeder = new ColorBleedEffect();

    public static Unity mod;

    public static void main(String[] args) throws Exception{
        headless = true;
        mod = new Unity();

        content = new ContentLoader();
        content.createBaseContent();

        //setup dummy loaded mod to load unity contents properly
        content.setCurrentMod(new LoadedMod(null, null, mod, new ModMeta(){{
            name = "unity";
        }}));

        try{
            mod.loadContent();
        }catch(StackOverflowError ignored){}

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

                GenRegion region = new GenRegion(fname, path){
                    {
                        width = sprite.getWidth();
                        height = sprite.getHeight();
                        u2 = v2 = 1f;
                        u = v = 0f;
                    }
                };

                regionCache.put(fname, region);
                spriteCache.put(fname, sprite);
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        });

        Core.atlas = new TextureAtlas(){
            @Override
            public AtlasRegion find(String name){
                String fname = name.replaceFirst("unity-", "");

                if(!regionCache.containsKey(fname)){
                    GenRegion region = new GenRegion(fname, null);
                    region.invalid = true;

                    return region;
                }

                return (AtlasRegion)regionCache.get(fname);
            }

            @Override
            public AtlasRegion find(String name, TextureRegion def){
                String fname = name.replaceFirst("unity-", "");

                if(!regionCache.containsKey(fname)){
                    return (AtlasRegion)def;
                }

                return (AtlasRegion)regionCache.get(fname);
            }

            @Override
            public AtlasRegion find(String name, String def){
                String fname = name.replaceFirst("unity-", "");

                if(!regionCache.containsKey(fname)){
                    return (AtlasRegion)regionCache.get(def.replaceFirst("unity-", ""));
                }

                return (AtlasRegion)regionCache.get(fname);
            }

            @Override
            public boolean has(String name){
                return regionCache.containsKey(name.replaceFirst("unity-", ""));
            }
        };

        Generators.generate();

        Fi.get("./sprites-gen").walk(path -> {
            if(path.absolutePath().contains("ui/")) return;

            try{
                BufferedImage image = ImageIO.read(path.file());

                Sprite sprite = new Sprite(image);
                sprite.alphaBleed(2).antialias();

                sprite.save(path.nameWithoutExtension());
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        });

        Sprite.dispose();
    }

    static BufferedImage buffer(TextureRegion reg){
        return spriteCache.get(((AtlasRegion)reg).name.replaceFirst("unity-", ""));
    }

    static boolean has(String name){
        return Core.atlas.has(name.replaceFirst("unity-", ""));
    }

    static boolean has(TextureRegion region){
        return has(((AtlasRegion)region).name.replaceFirst("unity-", ""));
    }

    static Sprite get(String name){
        return get(Core.atlas.find(name));
    }

    static Sprite get(TextureRegion region){
        GenRegion.validate(region);

        return new Sprite(spriteCache.get(((AtlasRegion)region).name.replaceFirst("unity-", "")));
    }

    static TextureRegion getRegion(String name){
        return regionCache.get(name.replaceFirst("unity-", ""));
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

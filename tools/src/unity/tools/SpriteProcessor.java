package unity.tools;

import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.struct.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.mod.Mods.*;
import unity.*;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.*;

import static mindustry.Vars.*;

public class SpriteProcessor{
    static ObjectMap<String, SpriteIndex> cache = new ObjectMap<>();
    static Color color = new Color();

    static Unity mod;

    static final Fi spritesGen = Fi.get("../assets-raw/sprites-gen/");

    public static void main(String[] args){
        headless = true;
        loadLogger();

        Fi handle = Fi.get("bundles/bundle");
        Core.bundle = I18NBundle.createBundle(handle, Locale.ROOT);

        mod = new Unity();

        content = new ContentLoader();
        content.createBaseContent();

        //setup dummy loaded mod to load unity contents properly
        content.setCurrentMod(new LoadedMod(null, null, mod, Unity.class.getClassLoader(), new ModMeta(){{
            name = "unity";
        }}));

        try{
            mod.loadContent();
        }catch(Throwable t){
            Log.err(t);
        }

        content.setCurrentMod(null);

        Fi.get("sprites/").walk(path -> {
            if(!path.extEquals("png")) return;

            cache.put(path.nameWithoutExtension(), new SpriteIndex(path));
        });

        Core.atlas = new TextureAtlas(){
            @Override
            public AtlasRegion find(String name){
                name = fixName(name);

                if(!cache.containsKey(name)){
                    GenRegion region = new GenRegion(name, null);
                    region.invalid = true;
                    return region;
                }

                SpriteIndex index = cache.get(name);
                if(index.pixmap == null){
                    index.pixmap = new Pixmap(index.file);
                    index.region = new GenRegion(name, index.file){{
                        width = index.pixmap.width;
                        height = index.pixmap.height;
                        u2 = v2 = 1f;
                        u = v = 0f;
                    }};
                }
                return index.region;
            }

            @Override
            public AtlasRegion find(String name, TextureRegion def){
                name = fixName(name);

                if(!cache.containsKey(name)){
                    return (AtlasRegion)def;
                }
                return find(name);
            }

            @Override
            public AtlasRegion find(String name, String def){
                name = fixName(name);

                if(!cache.containsKey(name)){
                    return find(def);
                }
                return find(name);
            }

            @Override
            public boolean has(String name){
                name = fixName(name);
                return cache.containsKey(name);
            }
        };

        Generators.generate();
        Sprite.dispose();
    }

    static void drawCenter(Pixmap pix, Pixmap other){
        pix.draw(other, pix.width / 2 - other.width / 2, pix.height / 2 - other.height / 2, true);
    }

    /** Almost Bilinear Interpolation except the underlying color interpolator uses SpriteProcessor#pythagoreanLerp */
    static Color getColor(Pixmap pix, float x, float y){
        // Cast floats into ints twice instead of casting 20 times
        int xInt = (int) x;
        int yInt = (int) y;

        if(!Structs.inBounds(xInt, yInt, pix.width, pix.height)) return color.set(0, 0, 0, 0);

        // A lot of these booleans are commonly checked, so let's run each check just once
        boolean isXInt = x == xInt;
        boolean isYInt = y == yInt;
        boolean xOverflow = x + 1 > pix.width;
        boolean yOverflow = y + 1 > pix.height;

        // Remember: x & y values themselves are already checked if in-bounds
        if((isXInt && isYInt) || (xOverflow && yOverflow)) return color.set(pix.get(xInt, yInt));

        if(isXInt || xOverflow){
            return color.set(MathUtil.colorLerp(Tmp.c1.set(getAlphaMedianColor(pix, xInt, yInt)), getAlphaMedianColor(pix, xInt, yInt + 1), y % 1));
        }else if(isYInt || yOverflow){
            return color.set(MathUtil.colorLerp(Tmp.c1.set(getAlphaMedianColor(pix, xInt, yInt)), getAlphaMedianColor(pix, xInt + 1, yInt), x % 1));
        }

        // Because Color is mutable, strictly 3 Color objects are effectively pooled; this sprite's color ("c0") and Temp's c1 & c2.
        // The first row sets color to c0, which is then set to c1. New color is set to c0, and SpriteProcessor#colorLerp puts result of c1 and c0 onto c1.
        // The second row does the same thing, but with c2 in the place of c1. Finally on return line, the result between c1 and c2 is put onto c1, which is then set to c0
        MathUtil.colorLerp(Tmp.c1.set(getAlphaMedianColor(pix, xInt, yInt)), getAlphaMedianColor(pix, xInt + 1, yInt), x % 1);
        MathUtil.colorLerp(Tmp.c2.set(getAlphaMedianColor(pix, xInt, yInt + 1)), getAlphaMedianColor(pix, xInt + 1, yInt + 1), x % 1);

        return color.set(MathUtil.colorLerp(Tmp.c1, Tmp.c2, y % 1));
    }

    static Color getAlphaMedianColor(Pixmap pix, int x, int y){
        float alpha = color.set(pix.get(x, y)).a;
        if(alpha >= 0.1f) return color;

        return alphaMedian(
            color.cpy(),
            new Color(pix.get(x + 1, y)),
            new Color(pix.get(x, y + 1)),
            new Color(pix.get(x - 1, y)),
            new Color(pix.get(x, y - 1))
        ).a(alpha);
    }

    static Color alphaMedian(Color main, Color... colors){
        ObjectIntMap<Color> matches = new ObjectIntMap<>();
        int count, primaryCount = -1, secondaryCount = -1;

        Tmp.c3.set(main);
        Tmp.c4.set(main);

        for(Color color : colors){
            if(color.a < 0.1f) continue;

            count = matches.increment(color) + 1;

            if(count > primaryCount){
                secondaryCount = primaryCount;
                Tmp.c4.set(Tmp.c3);

                primaryCount = count;
                Tmp.c3.set(color);
            }else if(count > secondaryCount){
                secondaryCount = count;
                Tmp.c4.set(color);
            }
        }

        if(primaryCount > secondaryCount){
            return color.set(Tmp.c3);
        }else if(primaryCount == -1){
            return color.set(main);
        }else{
            return color.set(MathUtil.averageColor(Tmp.c3, Tmp.c4));
        }
    }

    static void save(Pixmap pix, String sibling, String path){
        cache.get(sibling).file.sibling(path + ".png").writePng(pix);
    }

    static String fixName(String name){
        if(name.startsWith("unity-")){
            name = name.substring("unity-".length());
        }
        return name;
    }

    static String fixName(TextureRegion t){
        return fixName(((AtlasRegion)t).name);
    }

    static void replace(String name, Pixmap image){
        name = name.replaceFirst("unity-", "");

        Fi.get(name + ".png").writePng(image);
        ((GenRegion)Core.atlas.find(name)).path.delete();
    }

    static void replace(TextureRegion region, Pixmap image){
        replace(fixName(region), image);
    }

    static Pixmap get(String name){
        return get(Core.atlas.find(name));
    }

    static boolean has(String name){
        return Core.atlas.has(name);
    }

    static Pixmap get(TextureRegion region){
        validate(region);

        return cache.get(((AtlasRegion)region).name).pixmap.copy();
    }

    static void validate(TextureRegion region){
        if(((GenRegion)region).invalid){
            err("Region does not exist: @", ((GenRegion)region).name);
        }
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
    }

    static class SpriteIndex{
        @Nullable AtlasRegion region;
        @Nullable Pixmap pixmap;
        Fi file;

        public SpriteIndex(Fi file){
            this.file = file;
        }
    }
}

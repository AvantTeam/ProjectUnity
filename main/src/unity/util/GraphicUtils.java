package unity.util;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.MultiPacker.*;
import mindustry.type.*;

import java.nio.*;

import static arc.Core.*;

public final class GraphicUtils{
    public static TextureRegion getRegionRect(TextureRegion region, float x, float y, int rw, int rh, int w, int h){
        TextureRegion reg = new TextureRegion(region);
        float tileW = (reg.u2 - reg.u) / w;
        float tileH = (region.v2 - region.v) / h;
        float tileX = x / w;
        float tileY = y / h;

        reg.u = Mathf.map(tileX, 0f, 1f, reg.u, reg.u2) + tileW * 0.02f;
        reg.v = Mathf.map(tileY, 0f, 1f, reg.v, reg.v2) + tileH * 0.02f;
        reg.u2 = reg.u + tileW * (rw - 0.02f);
        reg.v2 = reg.v + tileH * (rh - 0.02f);
        reg.width = 32 * rw;
        reg.height = 32 * rh;

        return reg;
    }

    /**
     * Gets multiple regions inside a {@link TextureRegion}. The size for each region has to be 32.
     * @param w The amount of regions horizontally.
     * @param h The amount of regions vertically.
     */
    public static TextureRegion[] getRegions(TextureRegion region, int w, int h){
        int size = w * h;
        TextureRegion[] regions = new TextureRegion[size];

        float tileW = (region.u2 - region.u) / w;
        float tileH = (region.v2 - region.v) / h;

        for(int i = 0; i < size; i++){
            float tileX = ((float)(i % w)) / w;
            float tileY = ((float)(i / w)) / h;
            TextureRegion reg = new TextureRegion(region);

            //start coordinate
            reg.u = Mathf.map(tileX, 0f, 1f, reg.u, reg.u2) + tileW * 0.02f;
            reg.v = Mathf.map(tileY, 0f, 1f, reg.v, reg.v2) + tileH * 0.02f;
            //end coordinate
            reg.u2 = reg.u + tileW * 0.96f;
            reg.v2 = reg.v + tileH * 0.96f;

            reg.width = reg.height = 32;

            regions[i] = reg;
        }
        return regions;
    }

    /** Same thing like the drawer from {@link UnitType} without applyColor and outlines. */
    public static void simpleUnitDrawer(Unit unit, boolean drawLegs){
        UnitType type = unit.type;

        if(drawLegs){
            if(unit instanceof Mechc){
                //TODO draw the legs
            }
        }

        Draw.rect(type.region, unit.x, unit.y, unit.rotation - 90f);
        float rotation = unit.rotation - 90f;
        for(WeaponMount mount : unit.mounts){
            Weapon weapon = mount.weapon;

            float weaponRotation = rotation + (weapon.rotate ? mount.rotation : 0f);
            float recoil = -(mount.reload / weapon.reload * weapon.recoil);

            float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0f, recoil);
            float wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0f, recoil);

            Draw.rect(weapon.region, wx, wy, weapon.region.width * Draw.scl * -Mathf.sign(weapon.flipSprite), weapon.region.height * Draw.scl, weaponRotation);
        }
    }

    /**
     * Interpolates 2 {@link TextureRegion}s.
     * @author sk7725
     */
    public static TextureRegion blendSprites(TextureRegion a, TextureRegion b, float f, String name){
        PixmapRegion r1 = atlas.getPixmap(a);
        PixmapRegion r2 = atlas.getPixmap(b);

        Pixmap out = new Pixmap(r1.width, r1.height);
        Color color1 = new Color();
        Color color2 = new Color();

        for(int x = 0; x < r1.width; x++){
            for(int y = 0; y < r1.height; y++){
                r1.get(x, y, color1);
                r2.get(x, y, color2);
                out.setRaw(x, y, color1.lerp(color2, f).rgba());
            }
        }

        Texture tex  = new Texture(out);
        return atlas.addRegion(name + "-blended-" + (int)(f * 100), tex, 0, 0, tex.width, tex.height);
    }

    public static Pixmap outline(TextureRegion region, Color color, int width){
        var out = Pixmaps.outline(atlas.getPixmap(region), color, width);
        if(Core.settings.getBool("linear")){
            Pixmaps.bleed(out);
        }

        return out;
    }

    public static Pixmap outline(Pixmap pixmap, Color color, int width){
        var out = Pixmaps.outline(new PixmapRegion(pixmap), color, width);
        if(Core.settings.getBool("linear")){
            Pixmaps.bleed(out);
        }

        return out;
    }

    public static void outline(MultiPacker packer, TextureRegion region, Color color, int width){
        if(region instanceof AtlasRegion at && at.found()){
            outline(packer, region, color, width, at.name + "-outline", false);
        }
    }

    public static void outline(MultiPacker packer, TextureRegion region, Color color, int width, String name, boolean override){
        if(region instanceof AtlasRegion at && at.found() && (override || !packer.has(name))){
            packer.add(PageType.main, name, outline(region, color, width));
        }
    }

    public static void drawCenter(Pixmap pix, Pixmap other){
        pix.draw(other, pix.width / 2 - other.width / 2, pix.height / 2 - other.height / 2, true);
    }

    public static void drawCenter(Pixmap pix, PixmapRegion other){
        var copy = other.crop();
        drawCenter(pix, copy);
        copy.dispose();
    }

    public static PixmapRegion get(MultiPacker packer, TextureRegion region){
        if(region instanceof AtlasRegion at){
            var reg = packer.get(at.name);
            if(reg != null) return reg;

            return atlas.getPixmap(at.name);
        }else{
            return null;
        }
    }

    public static PixmapRegion get(MultiPacker packer, String name){
        var reg = packer.get(name);
        if(reg != null) return reg;

        return atlas.getPixmap(name);
    }

    /** @author Drullkus */
    public static Color colorLerp(Color a, Color b, float frac){
        return a.set(
            pythagoreanLerp(a.r, b.r, frac),
            pythagoreanLerp(a.g, b.g, frac),
            pythagoreanLerp(a.b, b.b, frac),
            pythagoreanLerp(a.a, b.a, frac)
        );
    }

    /** @author Drullkus */
    public static Color averageColor(Color a, Color b){
        return a.set(
            pythagoreanAverage(a.r, b.r),
            pythagoreanAverage(a.g, b.g),
            pythagoreanAverage(a.b, b.b),
            pythagoreanAverage(a.a, b.a)
        );
    }

    /**
     * Pythagorean-style interpolation will result in color transitions that appear more natural than linear interpolation
     * @author Drullkus
     */
    public static float pythagoreanLerp(float a, float b, float frac){
        if(a == b || frac <= 0) return a;
        if(frac >= 1) return b;

        a *= a * (1 - frac);
        b *= b * frac;

        return Mathf.sqrt(a + b);
    }

    /** @author Drullkus */
    public static float pythagoreanAverage(float a, float b){
        return Mathf.sqrt(a * a + b * b) * Utils.sqrtHalf;
    }

    /**
     * Almost Bilinear Interpolation except the underlying color interpolator uses {@link #pythagoreanLerp(float, float, float)}.
     * @author Drullkus
     */
    public static Color getColor(PixmapRegion pix, Color ref, float x, float y){
        // Cast floats into ints twice instead of casting 20 times
        int xInt = (int)x;
        int yInt = (int)y;

        if(!Structs.inBounds(xInt, yInt, pix.width, pix.height)) return ref.set(0, 0, 0, 0);

        // A lot of these booleans are commonly checked, so let's run each check just once
        boolean isXInt = x == xInt;
        boolean isYInt = y == yInt;
        boolean xOverflow = x + 1 > pix.width;
        boolean yOverflow = y + 1 > pix.height;

        // Remember: x & y values themselves are already checked if in-bounds
        if((isXInt && isYInt) || (xOverflow && yOverflow)) return ref.set(pix.get(xInt, yInt));

        if(isXInt || xOverflow){
            return ref.set(colorLerp(Tmp.c1.set(getAlphaMedianColor(pix, ref, xInt, yInt)), getAlphaMedianColor(pix, ref, xInt, yInt + 1), y % 1));
        }else if(isYInt || yOverflow){
            return ref.set(colorLerp(Tmp.c1.set(getAlphaMedianColor(pix, ref, xInt, yInt)), getAlphaMedianColor(pix, ref, xInt + 1, yInt), x % 1));
        }

        // Because Color is mutable, strictly 3 Color objects are effectively pooled; this sprite's color ("c0") and Temp's c1 & c2.
        // The first row sets color to c0, which is then set to c1. New color is set to c0, and #colorLerp() puts result of c1 and c0 onto c1.
        // The second row does the same thing, but with c2 in the place of c1. Finally on return line, the result between c1 and c2 is put onto c1, which is then set to c0
        colorLerp(Tmp.c1.set(getAlphaMedianColor(pix, ref, xInt, yInt)), getAlphaMedianColor(pix, ref, xInt + 1, yInt), x % 1);
        colorLerp(Tmp.c2.set(getAlphaMedianColor(pix, ref, xInt, yInt + 1)), getAlphaMedianColor(pix, ref, xInt + 1, yInt + 1), x % 1);

        return ref.set(colorLerp(Tmp.c1, Tmp.c2, y % 1));
    }

    /** @author Drullkus */
    public static Color getAlphaMedianColor(PixmapRegion pix, Color ref, int x, int y){
        float alpha = ref.set(pix.get(x, y)).a;
        if(alpha >= 0.1f) return ref;

        Color
            c1 = Pools.obtain(Color.class, Color::new).set(0f, 0f, 0f, 0f),
            c2 = Pools.obtain(Color.class, Color::new).set(0f, 0f, 0f, 0f),
            c3 = Pools.obtain(Color.class, Color::new).set(0f, 0f, 0f, 0f),
            c4 = Pools.obtain(Color.class, Color::new).set(0f, 0f, 0f, 0f);

        Color out = alphaMedian(
            ref.cpy(),
            c1.set(pix.get(x + 1, y)),
            c2.set(pix.get(x, y + 1)),
            c3.set(pix.get(x - 1, y)),
            c4.set(pix.get(x, y - 1))
        ).a(alpha);

        Pools.free(c1);
        Pools.free(c2);
        Pools.free(c3);
        Pools.free(c4);

        return out;
    }

    /** @author Drullkus */
    public static Color alphaMedian(Color main, Color ref, Color... colors){
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
            return ref.set(Tmp.c3);
        }else if(primaryCount == -1){
            return ref.set(main);
        }else{
            return ref.set(averageColor(Tmp.c3, Tmp.c4));
        }
    }

    public static Mesh copy(Mesh mesh){
        var originf = mesh.getVerticesBuffer();
        originf.clear();

        var origini = mesh.getIndicesBuffer();
        origini.clear();

        Mesh out = new Mesh(true, mesh.getNumVertices(), mesh.getNumIndices(), mesh.attributes);

        var dstf = out.getVerticesBuffer();
        dstf.clear();
        dstf.put(originf);
        originf.clear();
        dstf.clear();

        var dsti = out.getIndicesBuffer();
        dsti.clear();
        dsti.put(origini);
        origini.clear();
        dsti.clear();

        return out;
    }
}

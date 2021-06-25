package unity.util;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.math.*;
import arc.util.pooling.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;

import java.util.*;

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

    public static TextureRegion outline(TextureRegion region, Color color, int width){
        if(!(region instanceof AtlasRegion at)) throw new IllegalArgumentException("region must be AtlasRegion");

        var pix = Pixmaps.outline(atlas.getPixmap(region), color, width);
        var tex = new Texture(pix);
        return atlas.addRegion(at.name + "-outline", tex, 0, 0, tex.width, tex.height);
    }

    public static void antialias(TextureRegion region){
        antialias(atlas.getPixmap(region).pixmap);
    }

    /**
     * Anti-aliases a {@link Pixmap}, based on {@code Mindustry/tools/build.gradle}. Note that
     * this <em>overwrites</em> the given pixmap.
     * @param image The texture region that is going to be anti-aliased.
     */
    public static void antialias(Pixmap image){
        var out = image.copy();

        var color = Pools.obtain(Color.class, Color::new).set(0);
        var sum = Pools.obtain(Color.class, Color::new).set(0);
        var suma = Pools.obtain(Color.class, Color::new).set(0);
        var p = new int[9];

        for(int x = 0; x < image.width; x++){
            for(int y = 0; y < image.height; y++){
                int A = getColorClamped(out, x - 1, y + 1),
                    B = getColorClamped(out, x, y + 1),
                    C = getColorClamped(out, x + 1, y + 1),
                    D = getColorClamped(out, x - 1, y),
                    E = getColorClamped(out, x, y),
                    F = getColorClamped(out, x + 1, y),
                    G = getColorClamped(out, x - 1, y - 1),
                    H = getColorClamped(out, x, y - 1),
                    I = getColorClamped(out, x + 1, y - 1);

                Arrays.fill(p, E);

                if(D == B && D != H && B != F) p[0] = D;
                if((D == B && D != H && B != F && E != C) || (B == F && B != D && F != H && E != A)) p[1] = B;
                if(B == F && B != D && F != H) p[2] = F;
                if((H == D && H != F && D != B && E != A) || (D == B && D != H && B != F && E != G)) p[3] = D;
                if((B == F && B != D && F != H && E != I) || (F == H && F != B && H != D && E != C)) p[5] = F;
                if(H == D && H != F && D != B) p[6] = D;
                if((F == H && F != B && H != D && E != G) || (H == D && H != F && D != B && E != I)) p[7] = H;
                if(F == H && F != B && H != D) p[8] = F;

                suma.set(0);

                for(int val : p){
                    color.rgba8888(val);
                    suma.r += color.r * color.a;
                    suma.g += color.g * color.a;
                    suma.b += color.b * color.a;
                    suma.a += color.a;
                }

                float fm = suma.a <= 0.001f ? 0f : (1f / suma.a);
                suma.mul(fm, fm, fm, fm);

                float total = 0;
                sum.set(0);

                for(int val : p){
                    color.rgba8888(val);
                    float a = color.a;
                    color.lerp(suma, 1f - a);
                    sum.r += color.r;
                    sum.g += color.g;
                    sum.b += color.b;
                    sum.a += a;
                    total += 1f;
                }

                fm = 1f / total;
                sum.mul(fm, fm, fm, fm);
                out.setRaw(x, y, sum.rgba8888());
                sum.set(0);
            }
        }

        Pools.free(color);
        Pools.free(sum);
        Pools.free(suma);

        var pixels = image.getPixels();
        var outp = out.getPixels();

        outp.position(0);
        pixels.position(0);
        pixels.put(outp);
        pixels.position(0);

        out.dispose();
    }

    public static int getColorClamped(Pixmap image, int x, int y){
        return image.getRaw(Math.max(Math.min(x, image.width - 1), 0), Math.max(Math.min(y, image.height - 1), 0));
    }
}

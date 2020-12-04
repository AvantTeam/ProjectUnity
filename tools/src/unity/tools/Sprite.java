package unity.tools;

import arc.files.*;
import arc.func.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.*;

public class Sprite{
    private static Seq<Sprite> toDispose = new Seq<>();

    private BufferedImage sprite;
    private Graphics2D graphics;
    private Color color = new Color();

    final int width, height;

    Sprite(TextureRegion reg){
        this(SpriteProcessor.buffer(reg));
    }

    Sprite(BufferedImage buf){
        sprite = new BufferedImage(buf.getWidth(), buf.getHeight(), BufferedImage.TYPE_INT_ARGB);
        graphics = sprite.createGraphics();
        graphics.drawImage(buf, 0, 0, null);
        width = sprite.getWidth();
        height = sprite.getHeight();

        toDispose.add(this);
    }

    Sprite(int width, int height){
        this(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
    }

    static Sprite createEmpty(int width, int height){
        Sprite out = new Sprite(width, height);

        return out;
    }

    Sprite copy(){
        Sprite out = new Sprite(width, height);
        out.draw(this);

        return out;
    }

    int getRGB(int x, int y){
        return sprite.getRGB(Math.max(Math.min(x, sprite.getWidth() - 1), 0), Math.max(Math.min(y, sprite.getHeight() - 1), 0));
    }

    Color getColor(int x, int y){
        if(!Structs.inBounds(x, y, width, height)) return color.set(0, 0, 0, 0);

        int i = getRGB(x, y);
        color.argb8888(i);

        return color;
    }

    void each(Intc2 cons){
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                cons.get(x, y);
            }
        }
    }

    void draw(int x, int y, Color color){
        graphics.setColor(new java.awt.Color(color.r, color.g, color.b, color.a));
        graphics.fillRect(x, y, 1, 1);
    }

    void draw(Sprite sprite){
        draw(sprite, 0, 0);
    }

    void draw(Sprite sprite, int x, int y){
        draw(sprite, x, y, false, false);
    }

    void draw(Sprite sprite, int x, int y, boolean flipx, boolean flipy){
        int ofx = 0, ofy = 0;

        graphics.drawImage(sprite.sprite,
        x, y,
        x + sprite.width,
        y + sprite.height,
        (flipx ? sprite.width : 0) + ofx,
        (flipy ? sprite.height : 0) + ofy,
        (flipx ? 0 : sprite.width) + ofx,
        (flipy ? 0 : sprite.height) + ofy,
        null);
    }

    void drawCenter(Sprite sprite){
        drawCenter(sprite, false, false);
    }

    void drawCenter(Sprite sprite, boolean flipx, boolean flipy){
        draw(sprite, (width - sprite.width) / 2, (height - sprite.height) / 2, flipx, flipy);
    }

    void drawScaled(Sprite sprite){
        graphics.drawImage(sprite.sprite.getScaledInstance(width, height, java.awt.Image.SCALE_AREA_AVERAGING), 0, 0, width, height, null);
    }

    Sprite antialias(){
        Color sum = new Color();
        Color suma = new Color();
        int[] p = new int[9];

        for(int x = 0; x < sprite.getWidth(); x++){
            for(int y = 0; y < sprite.getHeight(); y++){
                int A = getRGB(x - 1, y + 1),
                B = getRGB(x, y + 1),
                C = getRGB(x + 1, y + 1),
                D = getRGB(x - 1, y),
                E = getRGB(x, y),
                F = getRGB(x + 1, y),
                G = getRGB(x - 1, y - 1),
                H = getRGB(x, y - 1),
                I = getRGB(x + 1, y - 1);

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
                    color.argb8888(val);
                    suma.r += color.r * color.a;
                    suma.g += color.g * color.a;
                    suma.b += color.b * color.a;
                    suma.a += color.a;
                }

                float fm = suma.a <= 0.001f ? 0f : 1f / suma.a;
                suma.mul(fm, fm, fm, fm);

                float total = 0f;
                sum.set(0);

                for(int val : p){
                    color.argb8888(val);
                    float a = color.a;
                    color.lerp(suma, 1f - a);
                    sum.r += color.r;
                    sum.g += color.g;
                    sum.b += color.b;
                    sum.a += a;
                    total += 1f;
                }

                fm = (float)(1f / total);
                sum.mul(fm, fm, fm, fm);
                sprite.setRGB(x, y, sum.argb8888());
                sum.set(0);
            }
        }

        return this;
    }

    Sprite outline(int radius, Color outlineColor){
        Sprite out = createEmpty(width, height);

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if(getColor(x, y).a < 1f){
                    boolean found = false;

                    outer:
                    for(int rx = -radius; rx <= radius; rx++){
                        for(int ry = -radius; ry <= radius; ry++){
                            if(Mathf.dst(rx, ry) <= radius && getColor(rx + x, ry + y).a > 0.01f){
                                found = true;

                                break outer;
                            }
                        }
                    }

                    if(found){
                        out.draw(x, y, outlineColor);
                    }
                }
            }
        }

        return out;
    }

    Sprite alphaBleed(int maxIterations){
        sprite = SpriteProcessor.bleeder.processImage(sprite, maxIterations);

        return this;
    }

    void save(String name){
        try{
            ImageIO.write(sprite, "png", Fi.get("./sprites-gen").child(name + ".png").file());
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    static void dispose(){
        for(Sprite sprite : toDispose){
            sprite.graphics.dispose();
        }

        toDispose.clear();
    }
}

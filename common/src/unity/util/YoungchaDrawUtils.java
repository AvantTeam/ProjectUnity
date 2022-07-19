package unity.util;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;

public class YoungchaDrawUtils{
    private final static TextureRegion tr1 = new TextureRegion(), tr2 = new TextureRegion();

    public static void drawRotRect(TextureRegion region, float x, float y, float w, float h, float th, float rot, float ang1, float ang2){
        if(region == null || !Core.settings.getBool("effects")) return;
        float amod1 = Mathf.mod(ang1, 360f);
        float amod2 = Mathf.mod(ang2, 360f);
        if(amod1 >= 180f && amod2 >= 180f) return;

        tr1.set(region);
        float uy1 = tr1.v;
        float uy2 = tr1.v2;
        float uCenter = (uy1 + uy2) / 2f;
        float uSize = (uy2 - uy1) * h / th * 0.5f;
        uy1 = uCenter - uSize;
        uy2 = uCenter + uSize;
        tr1.v = uy1;
        tr1.v2 = uy2;

        float s1 = -Mathf.cos(ang1 * Mathf.degreesToRadians);
        float s2 = -Mathf.cos(ang2 * Mathf.degreesToRadians);
        if(amod1 > 180f){
            tr1.v2 = Mathf.map(0f, amod1 - 360f, amod2, uy2, uy1);
            s1 = -1f;
        }else if(amod2 > 180f){
            tr1.v = Mathf.map(180f, amod1, amod2, uy2, uy1);
            s2 = 1f;
        }
        s1 = Mathf.map(s1, -1f, 1f, y - h / 2f, y + h / 2f);
        s2 = Mathf.map(s2, -1f, 1f, y - h / 2f, y + h / 2f);
        Draw.rect(tr1, x, (s1 + s2) * 0.5f, w, s2 - s1, w * 0.5f, y - s1, rot);
    }

    static float getYPos(float d, float r, float h){
        float c1 = Mathf.pi * r;
        if(d < c1){
            return r * (1f - Mathf.sinDeg(180 * d / c1));
        }else if(d > c1 + h - r){
            return (h - r) + r * (Mathf.sinDeg(180 * (d - (c1 + h - r)) / c1));
        }else{
            return d - c1 + r;
        }
    }

    public static void drawTread(TextureRegion region, float x, float y, float w, float h, float r, float rot, float d1, float d2){
        float c1 = Mathf.pi * r;
        float cut1 = c1 * 0.5f;
        float cut2 = c1 * 1.5f + h - r * 2;
        if(d1 < cut1 && d2 < cut1){return;}//cant be seen
        if(d1 > cut2 && d2 > cut2){return;}//cant be seen

        float y1 = getYPos(d1, r, h) - h * 0.5f;
        float y2 = getYPos(d2, r, h) - h * 0.5f;
        TextureRegion reg = region;
        if(d1 < cut1){
            y1 = -h * 0.5f;
            tr1.set(region);
            tr1.v = Mathf.map(cut1, d1, d2, tr1.v, tr1.v2);
            reg = tr1;
        }

        if(d2 > cut2){
            y2 = h * 0.5f;
            tr1.set(region);
            tr1.v2 = Mathf.map(cut2, d1, d2, tr1.v, tr1.v2);
            reg = tr1;
        }

        Draw.rect(reg, x, y + (y1 + y2) * 0.5f, w, y2 - y1, w * 0.5f, -y1, rot);

    }
}

package unity.graphics;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.graphics.*;

public class UnityDrawf{
    public static void spark(float x, float y, float w, float h, float r){
        Drawf.tri(x, y, w, h, r);
        //is this order imporant?
        Drawf.tri(x, y, w, h, r + 180f);
        Drawf.tri(x, y, w, h, r + 90f);
        Drawf.tri(x, y, w, h, r + 270f);
    }

    public static void drawHeat(TextureRegion reg, float x, float y, float rot, float temp){
        float a;
        if(temp > 273.15f){
            a = Math.max(0f, (temp - 498f) * 0.001f);
            if(a < 0.01f) return;
            if(a > 1f){
                Color fCol = Pal.turretHeat.cpy().add(0, 0, 0.01f * a);
                fCol.mul(a);
                Draw.color(fCol, a);
            }else Draw.color(Pal.turretHeat, a);
        }else{
            a = 1f - Mathf.clamp(temp / 273.15f);
            if(a < 0.01f) return;
            Draw.color(UnityPal.coldColor, a);
        }
        Draw.blend(Blending.additive);
        Draw.rect(reg, x, y, rot);
        Draw.blend();
        Draw.color();
    }
}

package unity.ai.kami;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import unity.ai.*;

public class KamiDrawers{
    private final static Color tCol = new Color();
    private final static Vec2 tVec = new Vec2();
    public static Cons<NewKamiAI> utsuhoDrawer, marisaDrawer;

    public static void load(){
        utsuhoDrawer = ai -> {
            for(int i = 0; i < 3; i++){
                Draw.color(tCol.set(Color.red).shiftHue((i * (360f / 3f)) + (Time.time * 3f)).a(Mathf.clamp(ai.drawIn * 1.5f)));
                tVec.trns((i * (360f / 3f)) + 90f, ai.drawIn * (70f + Mathf.sin(60f, 15f))).add(ai.getX(), ai.getY());
                Draw.rect(KamiRegions.okuu[i], tVec.x, tVec.y, (i * (360f / 3f)) + 90f);
            }
        };

        marisaDrawer = ai -> {
            TextureRegion refRegion = KamiRegions.marisaBroom[0];
            for(int i = 0; i < KamiRegions.marisaBroom.length; i++){
                Draw.color(tCol.set(Color.red).shiftHue((i * (360f / KamiRegions.marisaBroom.length)) + (Time.time * 3f)).a(Mathf.clamp(ai.drawIn * 1.5f)));
                Draw.rect(KamiRegions.marisaBroom[i], ai.getX(), ai.getY(), refRegion.width * Draw.scl * 1.5f, refRegion.height * Draw.scl * 1.5f);
                if(i <= 4){
                    float angle = (Mathf.clamp(ai.drawIn * 1.5f) * i * (360f / 5f)) + Time.time;
                    tVec.trns(angle, ai.drawIn * 60f).add(ai);
                    Draw.color(tCol.set(Color.red).shiftHue((i * (360f / 5f)) + (Time.time * 2f)).a(Mathf.clamp(ai.drawIn * 1.2f)));
                    Fill.circle(tVec.x, tVec.y, 12f);
                }
            }
        };
    }
}

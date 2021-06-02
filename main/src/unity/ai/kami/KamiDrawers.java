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
    private final static Vec2 tVec = new Vec2(), tVec2 = new Vec2();
    public static Cons<KamiAI> utsuhoDrawer, marisaDrawer, byakurenScrollDrawer, keikiDrawer;

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

        byakurenScrollDrawer = ai -> {
            int progress = Mathf.floor(Time.time / 7f);
            int trueLength = 20;
            for(int i = 0; i < trueLength; i++){
                TextureRegion region = KamiRegions.byakurenScroll[Mathf.mod(i + progress, KamiRegions.byakurenScroll.length)];
                float spacing = 1.7f;
                float mod = (Time.time / 7f) % 1f;
                float offset = mod * spacing;
                float angle = ((i * spacing - (trueLength - 1) * spacing / 2f) - (offset - (spacing / 2f))) * ai.drawIn;
                float width = i == 0 || i >= trueLength - 1 ? (i != 0 ? mod : 1 - mod) : 1f;

                Draw.color(tCol.set(Color.red).shiftHue(((Time.time * 2f) + (i * 5f))));
                tVec.trns(angle + ai.relativeRotation, 300f).sub(tVec2.trns(ai.relativeRotation, 240f)).add(ai);
                Draw.rect(region, tVec.x, tVec.y, region.width * Draw.scl * width * ai.drawIn, region.height * Draw.scl, (angle + ai.relativeRotation) - 90f);
            }
        };

        keikiDrawer = ai -> {
            int len = KamiRegions.keikiSpirit.length;
            for(int i = 0; i < len; i++){
                Draw.color(tCol.set(Color.red).a(ai.drawIn).shiftHue(((Time.time * 2f) + (i * 8f))));
                Draw.rect(KamiRegions.keikiSpirit[i], ai, (Time.time / 2f) % 360f);
                if(i < 8){
                    float ang = -(i * 360f / 8f) - 22.5f;
                    tVec.trns(ang, 70f + Mathf.sinDeg((Time.time + (i * 360f / 8f)) * 2f)).add(ai);
                    Draw.color(tCol.set(Color.red).a(ai.drawIn).shiftHue(((Time.time * 1.5f) + (i * 360f / 8f))));
                    Draw.rect(KamiRegions.keikiTools[i], tVec.x, tVec.y, Mathf.mod(ang - 90f, 360f));
                }
            }
        };
    }
}

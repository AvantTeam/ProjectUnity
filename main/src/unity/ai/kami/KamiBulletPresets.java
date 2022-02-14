package unity.ai.kami;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;

public class KamiBulletPresets{
    private static final Vec2 vec = new Vec2();

    public static void shootLine(float from, float to, int density, Floatc c){
        for(int i = 0; i < density; i++){
            float fin = Mathf.lerp(from, to, i / ((float)density - 1f));
            if(Float.isNaN(fin)) fin = to;
            c.get(fin);
        }
    }

    public static void shootLine(float from, float to, int density, Floatc2 c){
        for(int i = 0; i < density; i++){
            float fin = Mathf.lerp(from, to, i / ((float)density - 1f));
            if(Float.isNaN(fin)) fin = to;
            c.get(fin, i);
        }
    }

    public static void square(BulletType type, Entityc owner, Team team, float x, float y, float rotation, float speed, int density, Cons<Bullet> cons){
        for(int i = 0; i < 4; i++){
            for(int s = 0; s < density; s++){
                float fin = ((s / (float)density) - 0.5f) * 2f;
                vec.trns(i * 90f + rotation, speed, speed * fin);
                Bullet b = type.create(owner, team, x, y, rotation);
                b.vel.set(vec);
                if(cons != null) cons.get(b);
            }
        }
    }
}

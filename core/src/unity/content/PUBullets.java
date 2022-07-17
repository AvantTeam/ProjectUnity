package unity.content;

import mindustry.entities.bullet.*;

public class PUBullets{
    public static BulletType smallBullet;

    private PUBullets(){
        throw new AssertionError();
    }

    public static void load(){
        //standardCopper
        smallBullet = new BasicBulletType(3f, 10){{
            width = 7f;
            height = 9f;
            lifetime = 50f;
        }};
    }
}

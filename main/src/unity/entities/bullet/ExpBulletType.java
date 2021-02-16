package unity.entities.bullet;

import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.entities.comp.*;

public class ExpBulletType extends BulletType{
    /** Exp gained on hit, if owner is an exp block */
    public float expGain;

    public ExpBulletType(float speed, float damage){
        super(speed, damage);
    }

    @Override
    public void hit(Bullet b, float x, float y) {
        if(b.owner instanceof ExpBuildc exp) exp.incExp(expGain);
        super.hit(b, x, y);
    }
}

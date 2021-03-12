package unity.entities.bullet.exp;

import arc.*;
import arc.math.geom.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.content.*;
import unity.gen.Expc.*;

public class ExpBulletType extends BulletType{
    /** Exp gained on hit, if owner is an exp block */
    public float expGain;

    public ExpBulletType(float speed, float damage){
        super(speed, damage);
    }

    @Override
    public void hit(Bullet b, float x, float y) {
        if(b.owner instanceof ExpBuildc exp){
            if(exp.levelf() < 1 && Core.settings.getBool("hitexpeffect")){
                for(int i = 0; i < Math.ceil(expGain); i++){
                    UnityFx.expGain.at(x, y, 0f, (Position)b.owner);
                }
            }
            exp.incExp(expGain);
        }
        super.hit(b, x, y);
    }
}

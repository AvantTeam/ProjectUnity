package unity.entities.bullet;

import arc.math.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.entities.units.*;
import mindustry.gen.*;

public class GuidedMissileBulletType extends MissileBulletType{
    public float threshold = 1f;

    public GuidedMissileBulletType(float speed, float damage){
        super(speed, damage);
    }

    @Override
    public void update(Bullet b){
        if(b.data instanceof WeaponMount mount && homingPower > 0){
            float ang = b.angleTo(mount.aimX, mount.aimY);
            b.rotation(Angles.moveToward(b.rotation(), ang, homingPower * Time.delta * 50f));
            if(Angles.within(b.rotation(), ang, threshold)){
                b.data = null;
            }
        }
        super.update(b);
    }
}

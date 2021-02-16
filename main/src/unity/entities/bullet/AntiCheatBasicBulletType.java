package unity.entities.bullet;

import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.entities.units.*;

public class AntiCheatBasicBulletType extends BasicBulletType{
    public float tolerance = 1200f;
    public float fade = 20f;
    public float scl = 5f;
    public float otherAntiCheatScl = 1f;

    public AntiCheatBasicBulletType(float speed, float damage, String sprite){
        super(speed, damage, sprite);
    }

    public AntiCheatBasicBulletType(float speed, float damage){
        super(speed, damage, "bullet");
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc other, float initialHealth){
        if(!(other instanceof Healthc h)) return;
        if(other instanceof AntiCheatBase) ((AntiCheatBase)other).overrideAntiCheatDamage(b.damage * otherAntiCheatScl);
        if(h.maxHealth() > tolerance){
            float damage = (float)Math.pow((h.maxHealth() - tolerance) / fade, 2f) * scl;
            h.damage(damage);
        }
    }

    @Override
    public void hitTile(Bullet b, Building build, float initialHealth, boolean direct){
        super.hitTile(b, build, initialHealth, direct);
        if(build.maxHealth() > tolerance){
            float damage = (float)Math.pow((build.maxHealth() - tolerance) / fade, 2f) * scl;
            build.damage(damage);
        }
    }
}

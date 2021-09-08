package unity.entities.bullet.end;

import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import unity.*;
import unity.entities.units.*;
import unity.mod.*;

public class AntiCheatBasicBulletType extends BasicBulletType{
    private final static float toleranceScl = 4f;
    private final static float fadeScl = 8f;

    public float tolerance = 1200f;
    public float fade = 20f;
    public float scl = 5f;
    public float otherAntiCheatScl = 1f;
    public int priority = 0;

    public AntiCheatBasicBulletType(float speed, float damage, String sprite){
        super(speed, damage, sprite);
    }

    public AntiCheatBasicBulletType(float speed, float damage){
        super(speed, damage, "bullet");
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc other, float initialHealth){
        if(!(other instanceof Unit h)) return;
        if(other instanceof AntiCheatBase) ((AntiCheatBase)other).overrideAntiCheatDamage(b.damage * otherAntiCheatScl, priority);
        float score = h.maxHealth + h.type.dpsEstimate;
        if(score > tolerance * toleranceScl){
            float damage = (float)Math.pow((score - (tolerance * toleranceScl)) / (fade * fadeScl), 2f) * scl;
            h.damage(damage);
        }
        if(score >= Float.MAX_VALUE - 1000f){
            AntiCheat.annihilateEntity(other, false);
        }

        if(h.health >= initialHealth){
            Unity.antiCheat.samplerAdd(h);
        }else{
            Unity.antiCheat.samplerAdd(h, true);
        }
    }

    @Override
    public void hitTile(Bullet b, Building build, float initialHealth, boolean direct){
        super.hitTile(b, build, initialHealth, direct);
        float score = build.maxHealth;

        if(build instanceof TurretBuild turret && !turret.ammo.isEmpty()){
            score += turret.peekAmmo().estimateDPS();
        }
        if(score > tolerance * toleranceScl){
            float damage = (float)Math.pow((score - (tolerance * toleranceScl)) / (fade * fadeScl), 2f) * scl;
            build.damage(damage);
        }
        if(score >= Float.MAX_VALUE - 1000f){
            AntiCheat.annihilateEntity(build, false);
        }
        if(build.health >= initialHealth){
            Unity.antiCheat.samplerAdd(build);
        }else{
            Unity.antiCheat.samplerAdd(build, true);
        }
    }
}

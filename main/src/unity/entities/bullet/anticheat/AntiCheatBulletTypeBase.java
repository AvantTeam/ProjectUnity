package unity.entities.bullet.anticheat;

import arc.math.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.*;
import unity.entities.bullet.anticheat.modules.*;
import unity.mod.*;

abstract class AntiCheatBulletTypeBase extends BulletType{
    /** 0-1 */
    protected float ratioDamage = 0f;
    /** Ratio Damage starts if health is higher than this value */
    protected float ratioStart = 25000f;
    /** Prevents regeneration */
    protected float bleedDuration = -1f;
    /** If the targets health exceeds this value, damage starts increasing */
    protected float overDamage = 1000000f;
    protected float overDamageScl = 350f;
    protected float overDamagePower = 2f;

    protected boolean pierceShields = false;

    protected AntiCheatBulletModule[] modules;

    public AntiCheatBulletTypeBase(float speed, float damage){
        super(speed, damage);
    }

    public AntiCheatBulletTypeBase(){

    }

    public void hitUnitAntiCheat(Bullet b, Unit unit){
        hitUnitAntiCheat(b, unit, 0f);
    }

    public void hitUnitAntiCheat(Bullet b, Unit unit, float extraDamage){
        if(unit.health >= Float.MAX_VALUE || Float.isNaN(unit.health) || unit.health >= Float.POSITIVE_INFINITY){
            AntiCheat.annihilateEntity(unit, true);
            return;
        }
        float lh = unit.health, ls = unit.shield;
        float score = unit.health + unit.type.dpsEstimate;
        float pow = score > overDamage ? Mathf.pow((score - overDamage) / overDamageScl, overDamagePower) : 0f;
        float ratio = unit.health > ratioStart ? ratioDamage * Math.max(unit.maxHealth, unit.health) : 0f;
        float damage = Math.max(ratio, (b.damage * b.damageMultiplier()) + extraDamage + pow);
        if(bleedDuration > 0){
            Unity.antiCheat.applyStatus(unit, bleedDuration);
        }
        if(modules != null){
            for(AntiCheatBulletModule mod : modules){
                mod.hitUnit(unit);
            }
        }

        if(pierceShields){
            unit.damagePierce(damage);
        }else{
            unit.damage(damage);
        }
        float hd = unit.health - lh, sd = unit.shield - ls;
        Unity.antiCheat.notifyDamage(unit.id, hd);
        Unity.antiCheat.samplerAdd(unit, (hd + sd) < 0.00001f);

        Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f);
        if(impact) Tmp.v3.setAngle(b.rotation() + (knockback < 0 ? 180f : 0f));
        unit.impulse(Tmp.v3);
        unit.apply(status, statusDuration);
    }

    public void hitBuildingAntiCheat(Bullet b, Building building){
        hitBuildingAntiCheat(b, building, 0f);
    }

    public void hitBuildingAntiCheat(Bullet b, Building building, float extraDamage){
        if(building.health >= Float.MAX_VALUE || Float.isNaN(building.health) || building.health >= Float.POSITIVE_INFINITY){
            AntiCheat.annihilateEntity(building, true);
            return;
        }
        boolean col = !(collidesTiles && collides);
        float pow = building.health > overDamage ? Mathf.pow((building.health - overDamage) / overDamageScl, overDamagePower) : 0f;
        if(col || pow > 0f || ratioDamage > 0f){
            float ratio = building.health > ratioStart ? ratioDamage * Math.max(building.maxHealth, building.health) : 0f;
            float damage = Math.max(ratio, (col ? ((b.damage * b.damageMultiplier()) + extraDamage) * buildingDamageMultiplier : 0f) + pow);
            building.damage(damage);
        }
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health){
        if(entity instanceof Unit){
            hitUnitAntiCheat(b, (Unit)entity);
        }else{
            super.hitEntity(b, entity, health);
        }
    }

    @Override
    public void hitTile(Bullet b, Building build, float initialHealth, boolean direct){
        hitBuildingAntiCheat(b, build);
        super.hitTile(b, build, initialHealth, direct);
    }
}

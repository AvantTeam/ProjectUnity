package unity.entities.bullet.anticheat.modules;

import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;

public class ForceFieldDamageModule implements AntiCheatBulletModule{
    private final float maxRadius, maxShield, maxRegen, ratioDamage, damage, cooldown;

    public ForceFieldDamageModule(float damage, float maxRadius, float maxShield, float maxRegen, float ratioDamage, float cooldown){
        this.maxRadius = maxRadius;
        this.maxShield = maxShield;
        this.maxRegen = maxRegen;
        this.ratioDamage = ratioDamage;
        this.damage = damage;
        this.cooldown = cooldown;
    }

    public ForceFieldDamageModule(float damage, float maxRadius, float maxShield, float maxRegen, float ratioDamage){
        this(damage, maxRadius, maxShield, maxRegen, ratioDamage, 0f);
    }

    @Override
    public float getUnitData(Unit unit){
        return unit.shield;
    }

    @Override
    public void handleAbility(Ability ability, Unit unit, Bullet bullet){
        if(ability instanceof ForceFieldAbility f){
            if(f.regen > maxRegen){
                f.regen = Math.max(maxRegen, f.regen - Math.max(damage / 5f, f.regen * ratioDamage));
            }
            if(f.max > maxShield){
                f.max = Math.max(maxShield, f.max - Math.max(damage, f.max * ratioDamage));
            }
            if(f.radius > maxRadius + (unit.hitSize / 2f)){
                f.radius = Math.max(maxRadius + (unit.hitSize / 2f), f.radius - Math.max(damage, f.radius * ratioDamage));
            }
        }
    }

    @Override
    public void handleUnitPost(Unit unit, Bullet bullet, float data){
        if(data > 0f && unit.shield <= 0f){
            for(Ability a : unit.abilities){
                if(a instanceof ForceFieldAbility f){
                    unit.shield -= Math.max(f.cooldown * f.regen, cooldown * f.regen);
                    Fx.shieldBreak.at(unit.x, unit.y, f.radius, unit.team.color);
                    break;
                }
            }
        }
    }
}

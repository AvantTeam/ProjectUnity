package unity.entities.bullet.anticheat.modules;

import mindustry.entities.abilities.*;
import mindustry.gen.*;

public class ForceFieldDamageModule implements AntiCheatBulletModule{
    private final float maxRadius, maxShield, maxRegen, ratioDamage, damage;

    public ForceFieldDamageModule(float damage, float maxRadius, float maxShield, float maxRegen, float ratioDamage){
        this.maxRadius = maxRadius;
        this.maxShield = maxShield;
        this.maxRegen = maxRegen;
        this.ratioDamage = ratioDamage;
        this.damage = damage;
    }

    @Override
    public void handleAbility(Ability ability, Unit unit){
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
}

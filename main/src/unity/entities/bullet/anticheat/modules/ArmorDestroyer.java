package unity.entities.bullet.anticheat.modules;

import mindustry.entities.abilities.*;
import mindustry.gen.*;

public class ArmorDestroyer implements AntiCheatBulletModule{
    private final float armorDamage, shieldDamage, efficiencyDamage, ratioDamage;
    private float minimumArmorShield = 20f, minimumEfficiency = 2f;

    public ArmorDestroyer(float armorDamage, float shieldDamage, float efficiencyDamage){
        this.armorDamage = armorDamage;
        this.shieldDamage = shieldDamage;
        this.efficiencyDamage = efficiencyDamage;
        ratioDamage = 0f;
    }

    public ArmorDestroyer(float ratioDamage, float armorDamage, float shieldDamage, float efficiencyDamage){
        this.armorDamage = armorDamage;
        this.shieldDamage = shieldDamage;
        this.ratioDamage = ratioDamage;
        this.efficiencyDamage = efficiencyDamage;
    }

    public ArmorDestroyer set(float minAS, float minE){
        minimumArmorShield = minAS;
        minimumEfficiency = minE;
        return this;
    }

    @Override
    public void hitUnit(Unit unit){
        if(unit.armor > minimumArmorShield){
            unit.armor = Math.max(unit.armor - Math.max(armorDamage, unit.armor * ratioDamage), 0f);
            if(unit.armor < minimumArmorShield) unit.armor = minimumArmorShield;
        }
        if(unit.shield > minimumArmorShield){
            unit.shield = Math.max(unit.shield - Math.max(shieldDamage, unit.shield * ratioDamage), 0f);
            if(unit.shield < minimumArmorShield) unit.shield = minimumArmorShield;
        }
        for(Ability ability : unit.abilities){
            if(ability instanceof ShieldRegenFieldAbility s){
                if(s.max > minimumEfficiency){
                    s.max = Math.max(s.max - Math.max(efficiencyDamage, s.max * ratioDamage), 0f);
                    if(s.max < minimumEfficiency) s.max = minimumEfficiency;
                }
            }
        }
    }
}

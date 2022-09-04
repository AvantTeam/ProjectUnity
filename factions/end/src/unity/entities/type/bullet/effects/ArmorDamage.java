package unity.entities.type.bullet.effects;

import mindustry.entities.abilities.*;
import mindustry.gen.*;

public class ArmorDamage implements EndBulletEffects{
    protected float armorDamage = 5f, shieldDamage = 5f, efficiencyDamage = 5f,
            percentileDamage = 0.2f, percentileLimit = 1500f, percentileAbilityLimit = 200f;
    protected float minArmor = 60f, minShield = 250f, minEfficiency = 2f;

    @Override
    public void hitUnit(Unit unit, Bullet b){
        if(unit.armor > minArmor){
            if(unit.armor > percentileLimit) unit.armor -= (unit.armor - percentileLimit) * percentileDamage;
            unit.armor -= armorDamage;
            if(unit.armor < minArmor) unit.armor = minArmor;
        }
        if(unit.shield > minShield){
            if(unit.shield > percentileLimit) unit.shield -= (unit.shield - percentileLimit) * percentileDamage;
            unit.shield -= shieldDamage;
            if(unit.shield < minArmor) unit.shield = minArmor;
        }
    }

    @Override
    public void handleAbility(Ability ability, Unit unit, Bullet b){
        if(ability instanceof ShieldRegenFieldAbility s){
            if(s.max > minEfficiency){
                //s.max = Math.max(s.max - Math.max(efficiencyDamage, s.max * percentileDamage), 0f);
                if(s.max > percentileAbilityLimit) s.max -= (s.max - percentileAbilityLimit) * percentileDamage;
                s.max -= efficiencyDamage;
                if(s.max < minEfficiency) s.max = minEfficiency;
            }
        }
    }
}

package unity.entities.bullet.anticheat.modules;

import mindustry.entities.abilities.*;
import mindustry.gen.*;

public class AbilityDamageModule implements AntiCheatBulletModule{
    private final float minimumEfficiency, maximumReload;
    private final float efficiencyDamage, ratioDamage, reloadDamage;

    public AbilityDamageModule(float mE, float mR, float eD, float rd, float reD){
        minimumEfficiency = mE;
        maximumReload = mR;
        efficiencyDamage = eD;
        ratioDamage = rd;
        reloadDamage = reD;
    }
    @Override
    public void handleAbility(Ability ability, Unit unit){
        if(ability instanceof StatusFieldAbility s){
            if(s.duration > minimumEfficiency){
                s.duration = Math.max(minimumEfficiency, s.duration - Math.max(efficiencyDamage, s.duration * ratioDamage));
            }
            if(s.reload < maximumReload){
                s.reload = Math.min(s.reload + Math.max(reloadDamage, ratioDamage * s.reload), maximumReload);
            }
        }else if(ability instanceof RepairFieldAbility r){
            if(r.amount > minimumEfficiency){
                r.amount = Math.max(minimumEfficiency, r.amount - Math.max(efficiencyDamage, r.amount * ratioDamage));
            }
            if(r.reload < maximumReload){
                r.reload = Math.min(r.reload + Math.max(reloadDamage, ratioDamage * r.reload), maximumReload);
            }
        }
    }
}

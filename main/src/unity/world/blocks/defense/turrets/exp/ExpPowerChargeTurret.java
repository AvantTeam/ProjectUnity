package unity.world.blocks.defense.turrets.exp;

import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;

public class ExpPowerChargeTurret extends ExpPowerTurret{
    public ExpPowerChargeTurret(String name){
        super(name);
    }

    public class ExpPowerChargeTurretBuild extends ExpPowerTurretBuild{
        @Override
        public void shoot(BulletType ammo){
            useAmmo();
            float lvl = levelf();
    
            tr.trns(rotation, shootLength);
            chargeBeginEffect.at(x + tr.x, y + tr.y, rotation, getShootColor(lvl));
            chargeSound.at(x + tr.x, y + tr.y, 1f);
    
            for(int i = 0; i < chargeEffects; i++){
                Time.run(Mathf.random(chargeMaxDelay), () -> {
                    if(!isValid()) return;
                    tr.trns(rotation, shootLength);
                    chargeEffect.at(x + tr.x, y + tr.y, rotation, getShootColor(lvl));
                });
            }
    
            charging = true;
    
            for(var i = 0; i < shots; i++){
                Time.run(burstSpacing * i, () -> {
                    Time.run(chargeTime, () -> {
                        if(!isValid()) return;
                        tr.trns(rotation, shootLength, Mathf.range(xRand));
                        recoil = recoilAmount;
                        heat = 1f;
                        bullet(ammo, rotation + Mathf.range(inaccuracy));
                        effects();
                        charging = false;
                    });
                });
            }
        }

        @Override
        public void effects(){
            Effect fshootEffect = shootEffect == Fx.none ? peekAmmo().shootEffect : shootEffect;
            Effect fsmokeEffect = smokeEffect == Fx.none ? peekAmmo().smokeEffect : smokeEffect;
            fshootEffect.at(x + tr.x, y + tr.y, rotation, getShootColor(levelf()));
            fsmokeEffect.at(x + tr.x, y + tr.y, rotation);
            shootSound.at(x + tr.x, y + tr.y, Mathf.random(0.9f, 1.1f));
    
            if(shootShake > 0){
                Effect.shake(shootShake, shootShake, this);
            }
    
            recoil = recoilAmount;
        }
    }
}

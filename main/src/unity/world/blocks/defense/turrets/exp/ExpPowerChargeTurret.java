package unity.world.blocks.defense.turrets.exp;

import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;

public class ExpPowerChargeTurret extends ExpPowerTurret{
    /** Charge color lerp based on level */
    public Color fromColor = Pal.lancerLaser, toColor = Pal.sapBullet;
    
    public ExpPowerChargeTurret(String name){
        super(name);
    }

    public class ExpPowerChargeTurretBuild extends ExpPowerTurretBuild{
        public Color getShootColor(float lvl){
            return Tmp.c1.set(fromColor).lerp(toColor, lvl);
        }

        @Override
        public void shoot(BulletType ammo){
            useAmmo();
            float lvl = levelf();
    
            tr.trns(rotation, size * Vars.tilesize / 2);
            chargeBeginEffect.at(x + tr.x, y + tr.y, rotation, getShootColor(lvl));
            chargeSound.at(x + tr.x, y + tr.y, 1);
    
            for(int i = 0; i < chargeEffects; i++){
                Time.run(Mathf.random(chargeMaxDelay), () -> {
                    if(!isValid()) return;
                    tr.trns(rotation, size * Vars.tilesize / 2f);
                    chargeEffect.at(x + tr.x, y + tr.y, rotation, getShootColor(lvl));
                });
            }
    
            charging = true;
    
            Time.run(chargeTime, () -> {
                if(!isValid()) return;
                tr.trns(rotation, size * Vars.tilesize / 2f);
                recoil = recoilAmount;
                heat = 1f;
                bullet(ammo, rotation + Mathf.range(inaccuracy));
                effects();
                charging = false;
            });
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

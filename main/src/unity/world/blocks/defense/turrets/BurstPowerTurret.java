package unity.world.blocks.defense.turrets;

import arc.audio.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.*;

import static mindustry.Vars.*;

public class BurstPowerTurret extends PowerTurret{
    protected BulletType subShootType;
    protected int subShots = 1;
    protected float subBurstSpacing;
    protected Effect subShootEffect = Fx.none;
    protected Sound subShootSound = Sounds.none;

    public BurstPowerTurret(String name){
        super(name);
    }

    public class BurstPowerTurretBuild extends PowerTurretBuild{
        @Override
        public void shoot(BulletType type){
            if(chargeTime > 0f){
                useAmmo();
                tr.trns(rotation, size * tilesize / 2f);
                
                chargeBeginEffect.at(x + tr.x, y + tr.y, rotation);
                chargeSound.at(x + tr.x, y + tr.y, 1f);
                
                for(int i = 0; i < chargeEffects; i++){
                    Time.run(Mathf.random(chargeMaxDelay), () -> {
                        if(!isValid()) return;
                        tr.trns(rotation, size * tilesize / 2f);
                        chargeEffect.at(x + tr.x, y + tr.y, rotation);
                    });
                }
                charging = true;
                
                Time.run(chargeTime, () -> {
                    if(!isValid()) return;
                    tr.trns(rotation, size * tilesize / 2f);
                    recoil = recoilAmount;
                    heat = 1f;
                    
                    for(int i = 0; i < shots; i++){
                        Time.run(burstSpacing * 2f, () -> {
                            bullet(type, rotation + Mathf.range(inaccuracy));
                        });
                    }
                    for(int i = 0; i < subShots; i++){
                        Time.run(subBurstSpacing * i, () -> {
                            bullet(subShootType, rotation + Mathf.range(subShootType.inaccuracy));
                            subEffects();
                        });
                    }
                    effects();
                    charging = false;
                });
            }else{
                super.shoot(type);
            }
        }

        protected void subEffects(){
            subShootEffect.at(x + tr.x, y + tr.y, rotation);
            subShootSound.at(x + tr.x, y + tr.y, 1f);
        }
    }
}

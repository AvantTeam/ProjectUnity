package unity.world.blocks.defense.turrets;

import arc.audio.Sound;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Sounds;
import mindustry.world.blocks.defense.turrets.ChargeTurret;

import static mindustry.Vars.*;

public class BurstChargeTurret extends ChargeTurret{
    protected boolean alwaysTurn;
    protected BulletType subShootType;
    protected int subShots = 1;
    protected float subBurstSpacing;
    protected Effect subShootEffect = Fx.none;
    protected Sound subShootSound = Sounds.none;

    public BurstChargeTurret(String name){
        super(name);
    }

    public class BurstChargeTurretBuile extends ChargeTurretBuild{
        @Override
        public void shoot(BulletType type){
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
            shooting = true;
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
                shooting = false;
            });
        }

        @Override
        public boolean shouldTurn(){
            return alwaysTurn || super.shouldTurn();
        }

        protected void subEffects(){
            subShootEffect.at(x + tr.x, y + tr.y, rotation);
            subShootSound.at(x + tr.x, y + tr.y, 1f);
        }
    }
}

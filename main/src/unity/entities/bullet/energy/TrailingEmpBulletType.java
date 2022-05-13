package unity.entities.bullet.energy;

import arc.struct.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

//What do I call this help
public class TrailingEmpBulletType extends EmpBulletType{
    public float empRadius = 50f;
    public float empTimer = 15f;
    public Effect zapEffect = Fx.none;

    @Override
    public void init(Bullet b){
        super.init(b);

        b.data = new IntSeq();
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        IntSeq zapped = (IntSeq)b.data;

        if(b.timer(1, empTimer)){
            Vars.indexer.allBuildings(b.x, b.y, empRadius, other -> {
                if(!zapped.contains(other.id)){
                    if(other.team == b.team){
                        if(other.block.hasPower && other.block.canOverdrive && other.timeScale() < timeIncrease){
                            //other.timeScale = Math.max(other.timeScale, timeIncrease);
                            //other.timeScaleDuration = Math.max(other.timeScaleDuration, timeDuration);
                            chainEffect.at(b.x, b.y, 0, hitColor, other);
                            applyEffect.at(other, other.block.size * 7f);
                            zapEffect.at(b.x, b.y, b.angleTo(other));
                            zapped.add(other.id);
                        }

                        if(other.block.hasPower && other.damaged()){
                            other.heal(healPercent / 100f * other.maxHealth());
                            Fx.healBlockFull.at(other.x, other.y, other.block.size, hitColor);
                            applyEffect.at(other, other.block.size * 7f);
                        }
                    }else if(other.power != null){
                        var absorber = Damage.findAbsorber(b.team, b.x, b.y, other.x, other.y);
                        if(absorber != null){
                            other = absorber;
                        }

                        if(other.power != null && other.power.graph.getLastPowerProduced() > 0f){
                            //other.timeScale = Math.min(other.timeScale, powerSclDecrease);
                            //other.timeScaleDuration = timeDuration;
                            other.damage(damage * powerDamageScl);
                            hitPowerEffect.at(other.x, other.y, b.angleTo(other), hitColor);
                            chainEffect.at(b.x, b.y, 0, hitColor, other);
                            zapEffect.at(b.x, b.y, b.angleTo(other));
                            zapped.add(other.id);
                        }
                    }
                }
            });

            if(hitUnits){
                Units.nearbyEnemies(b.team, b.x, b.y, empRadius, other -> {
                    if(other.team != b.team && !zapped.contains(other.id)){
                        var absorber = Damage.findAbsorber(b.team, b.x, b.y, other.x, other.y);
                        if(absorber != null){
                            return;
                        }

                        hitPowerEffect.at(other.x, other.y, b.angleTo(other), hitColor);
                        chainEffect.at(b.x, b.y, 0, hitColor, other);
                        other.damage(damage * unitDamageScl);
                        other.apply(status, statusDuration);
                        zapEffect.at(b.x, b.y, b.angleTo(other));
                        zapped.add(other.id);
                    }
                });
            }
        }
    }
}

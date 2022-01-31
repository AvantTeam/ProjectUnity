package unity.ai;

import arc.math.geom.*;
import mindustry.ai.types.*;
import mindustry.gen.*;
import unity.content.*;

import static mindustry.Vars.*;

@Deprecated
public class HealerAI extends FlyingAI{
    protected float score;

    @Override
    public boolean invalid(Teamc target){
        boolean damaged = !(target instanceof Healthc t) || !t.damaged() && !t.isValid();
        return target == null || target.team() != unit.team || damaged;
    }

    @Override
    public void updateMovement(){
        if(target instanceof Unit temp){
            vec.trns(unit.angleTo(temp) + 180f, unit.type.range + temp.hitSize);
            vec.add(target).sub(unit).scl(0.01f).limit(1f).scl(unit.speed());
            unit.moveAt(vec);
            unit.lookAt(target);
        }
    }

    @Override
    public void updateWeapons(){
        if(target != null && (unit.ammo > 0.0001f || !state.rules.unitAmmo) && target instanceof Unit temp){
            if(timer.get(3, 5f) && unit.within(target, unit.type.range + temp.hitSize)){
                if(state.rules.unitAmmo) unit.ammo--;
                UnityFx.healLaser.at(unit.x, unit.y, 0f, new Position[]{unit, temp});

                temp.heal(unit.type.buildSpeed);
            }
        }
    }

    @Override
    public void updateTargeting(){
        if(retarget()){
            score = 0f;
            target = null;

            Groups.unit.each(x -> x.team == unit.team, e -> {
                float scoreB = (1 - e.healthf()) * 200f + (1000000f - unit.dst(e)) / 500f;
                if(scoreB > score && e.damaged() && e != unit && e.isValid()){
                    score = scoreB;
                    target = e;
                }
            });
        }

        updateWeapons();
    }
}

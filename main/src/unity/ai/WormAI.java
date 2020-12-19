package unity.ai;

import arc.math.*;
import arc.math.geom.*;
import mindustry.ai.types.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.meta.*;
import unity.entities.comp.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class WormAI extends FlyingAI{
    @Override
    public void updateMovement(){
        if(worm().isHead()){
            if(target != null && (unit.hasWeapons() || worm().headDamage() > 0f) && command() == UnitCommand.attack){
                attack(120f);
            }

            if(target == null && command() == UnitCommand.attack && state.rules.waves && unit.team == state.rules.defaultTeam){
                moveTo(getClosestSpawner(), state.rules.dropZoneRadius + 120f);
            }

            if(command() == UnitCommand.rally){
                moveTo(targetFlag(unit.x, unit.y, BlockFlag.rally, false), 60f);
            }
        }else{
            worm().updateMovement();
        }
    }

    @Override
    protected void attack(float circleLength){
        if(worm().isHead()){
            super.attack(circleLength);
            updateRotation();
        }
    }

    @Override
    protected void moveTo(Position target, float circleLength){
        if(worm().isHead()){
            super.moveTo(target, circleLength);
            updateRotation();
        }
    }

    @Override
    protected void moveTo(Position target, float circleLength, float smooth){
        if(worm().isHead()){
            super.moveTo(target, circleLength, smooth);
            updateRotation();
        }
    }

    protected void updateRotation(){
        if(unit.vel.isZero(0.001f)){
            unit.rotation(Mathf.slerpDelta(unit.rotation(), unit.vel.angle(), unit.type.rotateSpeed / 60f));
        }
    }

    protected <T extends Unit & Wormc> T worm(){
        return (T)unit;
    }
}

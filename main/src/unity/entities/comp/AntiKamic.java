package unity.entities.comp;

import arc.math.*;
import mindustry.gen.*;
import unity.content.*;
import unity.annotations.Annotations.*;

public interface AntiKamic extends Unitc{
    @Override
    @Replace
    default float prefRotation(){
        if(Groups.unit.contains(u -> u.type() == UnityUnitTypes.kami)){
            return 90f;
        }else if(activelyBuilding()){
            return angleTo(buildPlan());
        }else if(mineTile() != null){
            return angleTo(mineTile());
        }else if(moving()){
            return vel().angle();
        }
        return rotation();
    }

    @Override
    @Replace
    default float speed(){
        float pentaly = Groups.unit.contains(u -> u.type() == UnityUnitTypes.kami) ? 1 : type().strafePenalty;
        float strafePenalty = isGrounded() || !isPlayer() ? 1f : Mathf.lerp(1f, pentaly, Angles.angleDist(vel().angle(), rotation()) / 180f);
        //limit speed to minimum formation speed to preserve formation
        return (isCommanding() ? minFormationSpeed() * 0.98f : type().speed) * strafePenalty;
    }
}

package unity.entities.comp;

import arc.math.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;

@EntityComponent
abstract class BoostComp implements Unitc{
    @Import UnitType type;
    @Import float rotation, minFormationSpeed;
    @Import UnitController controller;

    @Replace
    @Override
    public float speed(){
        float strafePenalty = isGrounded() || !isPlayer() ? 1f : Mathf.lerp(1f, type.strafePenalty, Angles.angleDist(vel().angle(), rotation) / 180f);
        float boost = Mathf.lerp(1f, type.boostMultiplier, ((isPlayer() && ((Player)controller).boosting) ? 1f : 0f));
        return (isCommanding() ? minFormationSpeed * 0.98f : type.speed) * strafePenalty * boost * floorSpeedMultiplier();
    }
}

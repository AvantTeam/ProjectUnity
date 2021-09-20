package unity.entities.comp;

import mindustry.entities.EntityCollisions.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;

@SuppressWarnings({"unused"})
@EntityComponent
abstract class TallComp implements Unitc, Flyingc{
    @Import UnitType type;

    @Override
    @Replace
    public boolean checkTarget(boolean targetAir, boolean targetGround){
        return targetAir || targetGround;
    }

    @Override
    @Replace(100)
    public SolidPred solidity(){
        return (x, y) -> false;
    }

    @Override
    @Replace
    public boolean isFlying(){
        return true;
    }
}

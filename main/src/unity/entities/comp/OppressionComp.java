package unity.entities.comp;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

@SuppressWarnings("unused")
@EntityDef({Unitc.class, Endc.class, Wormc.class, Oppressionc.class})
@EntityComponent
abstract class OppressionComp implements Unitc, Wormc{
    @Import float speedMultiplier, rotation;
    @Import WeaponMount[] mounts;
    @Import UnitType type;

    @Insert(value = "update()", block = Statusc.class)
    void updateLaserSpeed(){
        if(isHead() && mounts != null && mounts[0].bullet != null){
            speedMultiplier *= 0.075f;
        }
    }

    @Override
    @Replace(2)
    public void rotateMove(Vec2 vec){
        moveAt(Tmp.v2.trns(rotation, vec.len()));

        if(!vec.isZero()){
            rotation = Angles.moveToward(rotation, vec.angle(), type.rotateSpeed * speedMultiplier * Math.max(Time.delta, 1));
        }
    }
}

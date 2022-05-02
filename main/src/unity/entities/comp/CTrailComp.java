package unity.entities.comp;

import arc.math.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.type.*;

@EntityComponent
abstract class CTrailComp implements Unitc{
    @Import UnitType type;
    @Import float x, y, rotation, elevation;

    transient Trail trail;

    @Override
    public void setType(UnitType type){
        if(type instanceof UnityUnitType t){
            trail = t.trailType.get(self());
        }
    }

    @Override
    @MethodPriority(1)
    public void update(){
        if(trail == null) return;

        float scale = elevation;
        float offset = type.engineOffset / 2f + type.engineOffset / 2f * scale;
        trail.update(x + Angles.trnsx(rotation + 180f, offset), y + Angles.trnsy(rotation + 180f, offset));
    }
}

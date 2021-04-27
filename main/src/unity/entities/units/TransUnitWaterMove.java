package unity.entities.units;

import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

@EntityPoint
public class TransUnitWaterMove extends UnitWaterMove implements TransformerBase{
    protected float timeTrans;

    @Override
    public void update(){
        super.update();
        transUpdate();
    }

    @Override
    public void setTimeTrans(float time){
        timeTrans = time;
    }

    @Override
    public float getTimeTrans(){
        return timeTrans;
    }

    @Override
    public int classId(){
        return UnityEntityMapping.classId(TransUnitWaterMove.class);
    }
}

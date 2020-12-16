package unity.entities.units;

import arc.util.Time;
import mindustry.gen.*;
import mindustry.world.blocks.environment.ShallowLiquid;
import unity.type.*;

public interface TransformerBase extends Unitc{
    void setTimeTrans(float time);

    float getTimeTrans();

    default void transUpdate(){
        UnityUnitType temp = (UnityUnitType)type();
        float current = getTimeTrans();
        if(floorOn().isLiquid && !(floorOn() instanceof ShallowLiquid) ^ self() instanceof WaterMovec){
            if(current < 0f || current > temp.transformTime){
                Unit groundUnit = temp.toTrans.get().spawn(team(), x(), y());
                groundUnit.rotation = rotation();
                groundUnit.add();
                groundUnit.vel.set(vel());
                if(isPlayer()){
                    groundUnit.controller(controller());
                }
                remove();
            }else setTimeTrans(current - Time.delta);
        }
    }
}

package unity.ai;

import mindustry.ai.types.MinerAI;
import mindustry.entities.units.*;
import mindustry.gen.*;

public class EmptyAI implements UnitController{
    protected Unit unit;

    @Override
    public Unit unit(){
        MinerAI a;
        return unit;
    }

    @Override
    public void unit(Unit unit){
        this.unit = unit;
    }
}

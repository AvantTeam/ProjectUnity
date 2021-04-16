package unity.ai;

import mindustry.entities.units.*;
import mindustry.gen.*;

public class EmptyAI implements UnitController{
    protected Unit unit;

    @Override
    public Unit unit(){
        return unit;
    }

    @Override
    public void unit(Unit unit){
        this.unit = unit;
    }
}

package unity.entities;

import arc.math.geom.*;
import mindustry.gen.*;

public class UnitVecData{
    public final Unit unit;
    public final Vec2 vec;

    public UnitVecData(Unit unit, Vec2 vec){
        this.unit = unit;
        this.vec = vec;
    }
}

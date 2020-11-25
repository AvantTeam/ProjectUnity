package unity.entities;

import arc.math.geom.Vec2;
import mindustry.gen.Unit;

public class UnitVecData{
    public final Unit unit;
    public final Vec2 vec;

    public UnitVecData(Unit unit, Vec2 vec){
        this.unit = unit;
        this.vec = vec;
    }
}

package unity.ai;

import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;

public class MonolithSoulAI implements UnitController{
    protected static final Vec2 vec = new Vec2();
    protected Unit unit;

    protected Unit target;
    protected Interval timer = new Interval(1);

    @Override
    public void unit(Unit unit){
        this.unit = unit;
    }

    @Override
    public Unit unit(){
        return unit;
    }

    @Override
    public void updateUnit(){
        if(timer.get(5f)){
            target = Units.closest(unit.team, unit.x, unit.y, u -> u.health < u.maxHealth);
        }

        if(target != null){
            vec.set(target)
                .sub(unit)
                .setLength(unit.realSpeed());

            unit.moveAt(vec);
            unit.lookAt(unit.prefRotation());
        }
    }
}

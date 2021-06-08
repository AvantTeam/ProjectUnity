package unity.ai;

import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import unity.entities.*;
import unity.gen.*;

public class MonolithSoulAI implements UnitController{
    protected static final Vec2 vec = new Vec2();
    protected MonolithSoul unit;

    protected Teamc target;
    protected Interval timer = new Interval(1);

    public boolean empty = true;

    @Override
    public void unit(Unit unit){
        this.unit = (MonolithSoul)unit;
    }

    @Override
    public Unit unit(){
        return unit;
    }

    @Override
    public void updateUnit(){
        if(empty) unit.kill();

        if(timer.get(5f)){
            target = Units.closest(unit.team, unit.x, unit.y, u -> {
                Soul soul = Soul.toSoul(u);
                return soul != null && soul.acceptSoul(unit) > 0;
            });
        }

        if(target != null && !unit.dead){
            vec.set(target)
                .sub(unit)
                .setLength(unit.realSpeed());

            unit.moveAt(vec);
            unit.lookAt(unit.prefRotation());
        }
    }
}

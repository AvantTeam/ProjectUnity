package unity.ai;

import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import unity.gen.*;
import unity.mod.*;

public class MonolithSoulAI implements UnitController{
    protected static final Vec2 vec = new Vec2();
    protected static final Rect rec1 = new Rect();
    protected static final Rect rec2 = new Rect();
    protected MonolithSoul unit;

    protected Unit target;
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
        if(empty){
            unit.health = 0f;
        }

        if(timer.get(5f)){
            target = Units.closest(unit.team, unit.x, unit.y, u -> FactionMeta.map(u) == Faction.monolith);
        }

        if(target != null && !unit.dead){
            vec.set(target)
                .sub(unit)
                .setLength(unit.realSpeed());

            unit.moveAt(vec);
            unit.lookAt(unit.prefRotation());

            unit.hitbox(rec1);
            target.hitbox(rec2);
            if(rec1.overlaps(rec2)){
                unit.invoke(target);
            }
        }
    }
}

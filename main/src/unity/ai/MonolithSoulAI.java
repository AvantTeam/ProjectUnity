package unity.ai;

import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import unity.gen.*;
import unity.gen.SoulHoldc.*;

import static mindustry.Vars.*;

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
        if(empty){
            unit.kill();
        }

        if(timer.get(5f)){
            Unit targetUnit = Units.closest(unit.team, unit.x, unit.y, u -> u instanceof Monolithc m && m.canJoin());
            Building targetBuilding = indexer.findTile(unit.team, unit.x, unit.y, Float.MAX_VALUE, b -> b instanceof SoulBuildc soul && soul.canJoin());

            if(targetUnit != null && targetBuilding != null){
                if(unit.dst2(targetUnit) > unit.dst2(targetBuilding)){
                    target = targetBuilding;
                }else{
                    target = targetUnit;
                }
            }else{
                if(targetUnit != null){
                    target = targetUnit;
                }else if(targetBuilding != null){
                    target = targetBuilding;
                }
            }
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

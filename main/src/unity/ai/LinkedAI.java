package unity.ai;

import arc.util.Time;
import mindustry.ai.types.*;
import mindustry.gen.*;
import unity.type.UnityUnitType;

public class LinkedAI extends FlyingAI{
    public Unit spawner;
    public float angle = 0f;

    @Override
    public void updateUnit(){
        super.updateUnit();

        if(spawner != null && spawner.dead)
        {
            unit.kill();
        }

        if(spawner != null && unit.dead){
            spawner.kill();
        }
    }

    @Override
    public void updateMovement(){
        super.updateMovement();

        unit.rotation = angle;

        angle += (((UnityUnitType)unit.type).rotationSpeed / 60f) * Time.delta;
    }
}

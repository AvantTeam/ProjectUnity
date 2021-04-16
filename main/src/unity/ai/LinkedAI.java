package unity.ai;

import mindustry.ai.types.*;
import mindustry.gen.*;

public class LinkedAI extends FlyingAI{
    public Unit spawner;

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
}

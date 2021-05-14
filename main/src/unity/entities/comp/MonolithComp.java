package unity.entities.comp;

import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class MonolithComp implements Unitc{
    @Import float x, y, hitSize;

    @Override
    public void killed(){
        if(net.server() || !net.active()){
            int amount = (int)(MonolithSoul.maxSize % hitSize);
            boolean transferred = false;

            for(int i = 0; i < amount; i++){
                MonolithSoul soul = MonolithSoul.create();
                soul.set(x, y);

                Tmp.v1.trns(360f / amount * i, Mathf.random(3f, 5f));
                soul.rotation = Tmp.v1.angle();
                soul.vel.set(Tmp.v1.x, Tmp.v1.y);

                soul.setType(MonolithSoul.defaultType.get());

                if(isPlayer() && !transferred && (Mathf.chance(1f / amount) || i == amount - 1)){
                    soul.controller(getPlayer());
                    transferred = true;
                }

                soul.add();
            }
        }
    }
}

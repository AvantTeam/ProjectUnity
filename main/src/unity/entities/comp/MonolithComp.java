package unity.entities.comp;

import arc.math.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.ai.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class MonolithComp implements Unitc{
    @Import Team team;
    @Import float x, y, hitSize, maxHealth;

    @Override
    public void killed(){
        if(net.server() || !net.active()){
            int amount = (int)(Math.max(hitSize, MonolithSoul.maxSize + 1) % MonolithSoul.maxSize);
            boolean transferred = false;

            float start = Mathf.random(360f);
            for(int i = 0; i < amount; i++){
                MonolithSoul soul = MonolithSoul.defaultType.get().create(team).as();
                soul.set(x + Mathf.range(hitSize), y + Mathf.range(hitSize));

                Tmp.v1.trns(start + 360f / amount * i, Mathf.random(6f, 12f));
                soul.rotation = Tmp.v1.angle();
                soul.vel.set(Tmp.v1.x, Tmp.v1.y);
                soul.healAmount = maxHealth / 10f / amount;

                if(isPlayer() && !transferred && (Mathf.chance(1f / amount) || i == amount - 1)){
                    soul.controller(getPlayer());
                    transferred = true;
                }

                if(soul.controller() instanceof MonolithSoulAI ai){
                    ai.empty = false;
                }
                soul.add();
            }
        }
    }
}

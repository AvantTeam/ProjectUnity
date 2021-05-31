package unity.entities.comp;

import arc.math.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.ai.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.entities.*;
import unity.gen.*;
import unity.mod.*;
import unity.type.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class MonolithComp implements Unitc, Factionc, Soul{
    @Import UnitController controller;
    @Import Team team;
    @Import float x, y, hitSize, maxHealth;
    @Import UnitType type;

    private int souls;
    private transient int maxSouls;

    @Override
    public Faction faction(){
        return FactionMeta.map(type);
    }

    @Override
    public void setType(UnitType type){
        if(type instanceof UnityUnitType def){
            maxSouls = def.maxSouls;
        }
    }

    @Override
    @MethodPriority(-5)
    public void update(){
        if(disabled()){
            if(!hasEffect(UnityStatusEffects.disabled)){
                apply(UnityStatusEffects.disabled, Float.MAX_VALUE);
            }
        }else{
            unapply(UnityStatusEffects.disabled);
        }
    }

    @Override
    public void killed(){
        if(net.server() || !net.active()){
            boolean transferred = false;

            float start = Mathf.random(360f);
            for(int i = 0; i < souls; i++){
                MonolithSoul soul = MonolithSoul.defaultType.get().create(team).as();

                Tmp.v1.trns(Mathf.random(360f), Mathf.random(hitSize));
                soul.set(x + Tmp.v1.x, y + Tmp.v1.y);

                Tmp.v1.trns(start + 360f / souls * i, Mathf.random(6f, 12f));
                soul.rotation = Tmp.v1.angle();
                soul.vel.set(Tmp.v1.x, Tmp.v1.y);
                soul.healAmount = maxHealth / 10f / souls;

                if(isPlayer() && !transferred && (Mathf.chance(1f / souls) || i == souls - 1)){
                    soul.controller(getPlayer());
                    transferred = true;
                }

                if(controller instanceof MonolithSoulAI ai){
                    ai.empty = false;
                }
                soul.add();
            }
        }
    }

    public boolean disabled(){
        return souls <= 0;
    }

    @Override
    public int souls(){
        return souls;
    }

    @Override
    public int maxSouls(){
        return maxSouls;
    }

    @Override
    public void join(){
        if(canJoin()) souls++;
    }

    @Override
    public void unjoin(){
        if(souls > 0) souls--;
    }

    public boolean canControl(){
        return canJoin() && (headless || acceptSoul(player.unit()) > 0);
    }

    @Override
    @Replace
    public boolean isAI(){
        return controller instanceof AIController && canControl();
    }
}

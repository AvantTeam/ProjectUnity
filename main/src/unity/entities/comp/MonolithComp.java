package unity.entities.comp;

import arc.math.*;
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
            spreadSouls();
        }
    }

    @Override
    public boolean apply(MonolithSoul soul, int index, boolean transferred){
        if(isPlayer() && !transferred && (Mathf.chance(1f / souls) || index == souls - 1)){
            soul.controller(getPlayer());
            transferred = true;
        }

        if(soul.controller() instanceof MonolithSoulAI ai){
            ai.empty = false;
        }

        return transferred;
    }

    public boolean disabled(){
        return !hasSouls();
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

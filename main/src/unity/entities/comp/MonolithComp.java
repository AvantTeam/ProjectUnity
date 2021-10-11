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
    @Import boolean spawnedByCore;

    private int souls;
    private transient int maxSouls;

    @Override
    public Faction faction(){
        return FactionMeta.map(type);
    }

    @Override
    public void setType(UnitType type){
        // Spawned-by-core units can't have souls
        if(!spawnedByCore && type instanceof UnityUnitType def){
            maxSouls = def.maxSouls;
        }else{
            maxSouls = 0;
            souls = 0;
        }
    }

    @Override
    public void add(){
        if(spawnedByCore){
            maxSouls = 0;
            souls = 0;
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

    boolean disabled(){
        return !spawnedByCore && !hasSouls();
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
        souls++;
    }

    @Override
    public void unjoin(){
        souls--;
    } // Could've named it "join't"
}

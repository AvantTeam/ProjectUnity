package unity.world.blocks.units;

import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public class ConversionPad extends MechPad{
    public Seq<UnitType[]> upgrades = new Seq<>();
    
    public ConversionPad(String name){
        super(name);
    }

    public class ConversionPadBuild extends MechPadBuild{
        UnitType resultUnit;
        boolean coreSpawn;

        @Override
        public boolean inRange(Player player){
            boolean isValid = false;
            for(UnitType[] unitTypes : upgrades){
                if(player.unit().type == unitTypes[0]) isValid = true;
            }
            return super.inRange(player) && isValid;
        }

        @Override
        public void configured(@Nullable Unit unit, @Nullable Object value){
            if(unit != null && unit.isPlayer() && !(unit instanceof BlockUnitc)){
                time = 0;
                for(UnitType[] unitTypes : upgrades){
                    if(unit.type == unitTypes[0]) resultUnit = unitTypes[1];
                }
                coreSpawn = unit.spawnedByCore;
                unit.spawnedByCore = true;
                if(!net.client()){
                    unit.getPlayer().unit(unit());
                }
            }
        }

        @Override
        public UnitType getResultUnit(){
            return resultUnit;
        }

        @Override
        public void finishUnit(){
            Player thisP = thisU.getPlayer();
            if(thisP == null) return;
            Fx.spawn.at(self());
            
            if(!net.client()){
                Unit unit = getResultUnit().create(team);
                unit.set(self());
                unit.rotation = spawnRot;
                unit.impulse(0, spawnForce);
                unit.set(getResultUnit(), thisP);
                unit.spawnedByCore = coreSpawn;
                unit.add();
            }
            
            if(state.isCampaign() && thisP == player) getResultUnit().unlock();
            
            consume();
            time = 0;
        }
    }
}

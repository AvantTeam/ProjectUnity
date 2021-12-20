package unity.entities.comp;

import mindustry.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.EntityCollisions.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.environment.*;
import unity.annotations.Annotations.*;
import unity.entities.legs.*;
import unity.type.*;

@SuppressWarnings({"unused"})
@EntityComponent
abstract class CLegComp implements Unitc{
    transient CLegGroup[] legGroups;
    transient Floor floor;
    @Import UnitType type;

    @Override
    @Replace
    public SolidPred solidity(){
        return type.allowLegStep ? EntityCollisions::legsSolid : EntityCollisions::solid;
    }

    @Override
    @Replace
    public int pathType(){
        return type.allowLegStep ? Pathfinder.costLegs : Pathfinder.costGround;
    }

    @Override
    @Replace
    public Floor drownFloor(){
        return canDrown() ? floor : null;
    }

    @Override
    public void add(){
        UnityUnitType uType = (UnityUnitType)type();
        legGroups = new CLegGroup[uType.legGroup.size];
        for(int i = 0; i < legGroups.length; i++){
            legGroups[i] = uType.legGroup.get(i).create();
            legGroups[i].reset(self());
        }
    }

    @Override
    public void update(){
        floor = Vars.world.floorWorld(x(), y());
        boolean allNull = floor != null && floor != Blocks.air;
        for(CLegGroup group : legGroups){
            group.update(self());
            floor = group.lastFloor;
            if(!allNull) allNull = group.lastFloor == null;
        }
        if(allNull) floor = null;
    }
}

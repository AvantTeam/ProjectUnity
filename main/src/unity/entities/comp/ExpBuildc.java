package unity.entities.comp;

import arc.util.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.gen.*;
import unity.type.*;
import unity.type.exp.*;

public interface ExpBuildc extends ExpEntityc<Block, ExpBlock>, Buildingc{
    @Override
    default ExpBlock expType(){
        ExpType<?> type = ExpMeta.map(block());
        if(!(type instanceof ExpBlock)){
            throw new IllegalStateException("No 'ExpBlock' type found for '" + block().localizedName + "'");
        }

        return (ExpBlock)type;
    }

    @Override
    default void updateTile(){
        Log.info("Appended method");
    }
}

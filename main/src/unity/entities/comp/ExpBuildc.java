package unity.entities.comp;

import mindustry.gen.*;
import mindustry.world.*;
import unity.entities.*;
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
    default void incExp(float exp){
        
    }

    default boolean consumesOrb(){
        return expType().hasExp;
    }

    @Override
    default void onDestroyed(){
        ExpOrbs.spreadExp(x(), y(), exp() * expType().orbRefund, 3f * block().size);
    }
}

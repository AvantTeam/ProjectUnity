package unity.entities.comp;

import mindustry.gen.*;
import unity.gen.*;
import unity.type.*;
import unity.type.exp.*;

public interface ExpBuildc extends ExpEntityc, Buildingc{
    @Override
    default float maxExp(){
        return exp().maxExp;
    }

    default ExpBlock exp(){
        ExpType<?> exp = ExpMeta.map(block());
        if(!(exp instanceof ExpBlock block)){
            throw new IllegalStateException("No ExpBlock found for type: '" + block().localizedName + "'");
        }

        return block;
    }
}

package unity.entities.comp;

import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.type.*;
import unity.type.decal.UnitDecorationType.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class DecorationComp implements Unitc{
    transient UnitDecoration[] decors = {};

    @Override
    @MethodPriority(1)
    public void setType(UnitType type){
        UnityUnitType uType = (UnityUnitType)type;
        decors = new UnitDecoration[uType.decorations.size];
        for(int i = 0; i < uType.decorations.size; i++){
            decors[i] = uType.decorations.get(i).decalType.get(uType.decorations.get(i));
        }
    }

    @Override
    public void update(){
        for(UnitDecoration decor : decors){
            decor.update(self());
        }
    }

    @Override
    public void add(){
        for(UnitDecoration decor : decors){
            decor.added(self());
        }
    }
}

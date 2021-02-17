package unity.entities.comp;

import mindustry.content.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.net.*;

public interface Bossc extends Unitc{
    @Override
    @MethodPriority(-1)
    default void add(){
        if(!isAdded()){
            apply(StatusEffects.boss);
            UnityCall.bossMusic(type().name, true);
        }
    }

    @Override
    @MethodPriority(-1)
    default void remove(){
        if(isAdded()){
            UnityCall.bossMusic(type().name, false);
        }
    }
}

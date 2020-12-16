package unity.entities.comp;

import mindustry.gen.*;
import arc.math.*;
import unity.type.*;

public interface Copterc extends Unitc{
    @Override
    default void update(){
        if(dead()){
            rotation(rotation() + ((UnityUnitType)type()).fallRotateSpeed * Mathf.signs[id() % 2]);
        }
    }
}

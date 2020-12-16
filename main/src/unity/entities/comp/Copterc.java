package unity.entities.comp;

import mindustry.gen.*;
import arc.math.*;
import unity.entities.units.*;

public interface Copterc extends Unitc{
    @Override
    default void update(){
        if(dead()){
            rotation(rotation() + ((CopterUnitType)type()).fallRotateSpeed * Mathf.signs[id() % 2]);
        }
    }
}

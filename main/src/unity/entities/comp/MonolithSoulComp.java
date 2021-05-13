package unity.entities.comp;

import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

@SuppressWarnings("unused")
@EntityDef(value = {Unitc.class, MonolithSoulc.class})
@EntityComponent
@ExcludeGroups(Unitc.class)
abstract class MonolithSoulComp implements Unitc{
    @Import UnitController controller;
    @Import float health, maxHealth;

    @Override
    public void update(){
        if(controller == null){
            kill();
        }else{
            health -= maxHealth / 6f * Time.delta;
        }
    }
}

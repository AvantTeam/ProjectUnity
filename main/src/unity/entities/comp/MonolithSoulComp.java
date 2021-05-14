package unity.entities.comp;

import arc.func.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.gen.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
@EntityDef({Unitc.class, MonolithSoulc.class})
@EntityComponent
@ExcludeGroups(Unitc.class)
abstract class MonolithSoulComp implements Unitc{
    static final float maxSize = 2f * tilesize;
    static final Prov<UnitType> defaultType = () -> UnityUnitTypes.monolithSoul;

    float size;
    float healAmount;

    @Import UnitController controller;
    @Import Team team;
    @Import float x, y, health, maxHealth, hitSize;
    @Import boolean dead;

    @Override
    public void update(){
        if(controller == null){
            kill();
        }else{
            health -= maxHealth / 6f * Time.delta;

            if(!dead && isPlayer()){
                Units.nearby(team, x, y, size, unit -> {
                    if(!unit.dead && unit.within(this, size)){
                        invoke(unit);

                        if(unit.getPlayer() == null){
                            unit.controller(getPlayer());
                        }
                    }
                });
            }
        }
    }

    public void invoke(Unit unit){
        unit.heal((size / maxSize) * healAmount);
        kill();
    }
}

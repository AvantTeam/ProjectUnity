package unity.parts.types;

import mindustry.entities.abilities.*;
import unity.parts.*;
import unity.parts.stats.*;
import unity.parts.stats.AdditiveStat.*;

public class ModularUnitAbilityType extends ModularUnitPartType{
    public ModularUnitAbilityType(String name){
        super(name);
    }

    public void ability(int abilitySlots, Ability ability){
        stats.add(new AbilitySlotUseStat(abilitySlots));
        stats.add(new AbilityStat(ability));
    }
}

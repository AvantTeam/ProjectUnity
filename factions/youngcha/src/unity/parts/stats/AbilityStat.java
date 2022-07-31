package unity.parts.stats;

import mindustry.entities.abilities.*;
import unity.parts.*;
import unity.parts.PartType.*;
import unity.util.*;

public class AbilityStat extends PartStat{
    public Ability ability;

    public AbilityStat(Ability ability){
        super("abilities");
        this.ability = ability;
    }

    @Override
    public void merge(PartStatMap id, Part part){
        if(id.has("abilities")){
            var weaponsarr = id.stats.getList("abilities");
            ValueMap abilityMap = new ValueMap();
            abilityMap.put("part", part);
            Ability copy = ability.copy();
            abilityMap.put("ability", copy);
            weaponsarr.add(abilityMap);
        }
    }

    @Override
    public void mergePost(PartStatMap id, Part part){

    }
}

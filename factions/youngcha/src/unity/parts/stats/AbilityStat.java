package unity.parts.stats;

import arc.struct.*;
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
    public void merge(ValueMap id, Part part){
        var abilitySeq = id.<Seq<ValueMap>>getObject("abilities", Seq::new);
        ValueMap abilityMap = new ValueMap();
        Ability copy = ability.copy();
        abilityMap.put("ability", copy);
        abilitySeq.add(abilityMap);
    }

    @Override
    public void mergePost(ValueMap id, Part part){

    }
}

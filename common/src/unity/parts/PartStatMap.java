package unity.parts;


import arc.struct.*;
import unity.parts.PartType.*;
import unity.util.*;

public class PartStatMap{
    public ValueMap stats = new ValueMap();

    public ValueMap getOrCreate(String name){
        if(!stats.has(name)){
            stats.put(name, new ValueMap());
        }
        return stats.getValueMap(name);
    }

    public boolean has(String name){
        return stats.has(name);
    }

    public float getValue(String name){
        return stats.getValueMap(name).getFloat("value");
    }

    public float getValue(String name, String subfield){
        return stats.getValueMap(name).getFloat(subfield, 0);
    }

    public void getStats(Seq<? extends Part> parts){
        for(int i = 0; i < parts.size; i++){
            parts.get(i).type.appendStats(this, parts.get(i));
        }
        for(int i = 0; i < parts.size; i++){
            parts.get(i).type.appendStatsPost(this, parts.get(i));
        }
    }
}

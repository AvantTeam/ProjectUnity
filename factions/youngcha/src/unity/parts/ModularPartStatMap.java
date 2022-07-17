package unity.parts;


import unity.util.*;

public class ModularPartStatMap{
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

}

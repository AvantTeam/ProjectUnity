package unity.parts;

import unity.util.Utils;
import unity.util.ValueMap;

public class ModularPartStatMap{
    public ValueMap stats = new ValueMap();

    public ValueMap getOrCreate(String name){
        if(stats.get(name)==null){
            stats.put(name,new ValueMap());
        }
        return stats.getValueMap(name);
    }
    public boolean has(String name){
        return stats.has(name);
    }

    public float getValue(String name){
        return stats.getValueMap(name).getFloat("value");
    }
    public float getValue(String name,String subfield){
        return Utils.getFloat(stats.getValueMap(name),subfield,0);
    }

}

package unity.parts;

import unity.util.ValueList;
import unity.util.ValueMap;

public class ModularUnitStatMap extends ModularPartStatMap{
    public ModularUnitStatMap(){
        stats.put("health",(new ValueMap()).put("value",0f));
        stats.put("mass",(new ValueMap()).put("value",0f));
        stats.put("power",(new ValueMap()).put("value",0f));
        stats.put("powerusage",(new ValueMap()).put("value",0f));
        stats.put("speed",(new ValueMap()).put("value",0f));
        stats.put("armour",(new ValueMap()).put("value",0f));
        stats.put("weapons",new ValueList());
        stats.put("wheel",(new ValueMap()));
    }
}

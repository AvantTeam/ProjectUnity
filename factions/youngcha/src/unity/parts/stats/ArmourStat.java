package unity.parts.stats;

import arc.math.*;
import unity.parts.PartType.*;
import unity.util.*;

public class ArmourStat extends AdditiveStat{
    public ArmourStat(float value){
        super("armor", value);
    }

    @Override
    public void mergePost(ValueMap id, Part part){
        var armor = id.getFloat("armor");
        if(id.has("realArmor")) return;

        armor = Mathf.log(2, armor);
        id.put("realArmor", armor);
    }
}

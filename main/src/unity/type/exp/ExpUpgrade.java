package unity.type.exp;

import mindustry.world.*;
import unity.gen.*;

public class ExpUpgrade<T extends Block & Expc>{
    public final T type;
    /** Do NOT modify directly */
    public int index;

    public int min = 1;
    public int max;

    public boolean hide = false;

    public ExpUpgrade(T type){
        this.type = type;
        max = type.maxLevel();
    }
}

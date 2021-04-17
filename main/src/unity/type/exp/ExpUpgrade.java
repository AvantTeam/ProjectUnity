package unity.type.exp;

import mindustry.world.*;

/** @author GlennFolker */
public class ExpUpgrade{
    public final Block type;
    /** Do NOT modify directly */
    public int index;

    public int min = 1;
    public int max;

    public boolean hide = false;

    public ExpUpgrade(Block type){
        this.type = type;
    }
}

package unity.world.blocks;

import java.lang.reflect.*;;

public class ExpField{
    public final String name;
    public final Field field;
    public final int start;
    public final int[] intensity;

    public ExpField(String name, Field field, int start, int... intensity){
        this.name = name;
        this.field = field;
        this.start = start;
        this.intensity = intensity;
    }
}

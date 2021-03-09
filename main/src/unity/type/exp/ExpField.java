package unity.type.exp;

import java.lang.reflect.*;

@SuppressWarnings("rawtypes")
public class ExpField{
    public final ExpFieldType type;
    /** No generics because I need the EXACT type from the class, for casting and reflection */
    public final Class classType;
    public final Field field;

    public float startFloat = 0f;
    public boolean startBool = false;
    public float intensity = 1f;

    public final Class intensityType;
    public Object[] intensityList;

    public ExpField(ExpFieldType type, Class classType, Class intensityType, Field field){
        this.type = type;
        this.classType = classType;
        this.intensityType = intensityType;

        this.field = field;
        this.field.setAccessible(true);
    }
}

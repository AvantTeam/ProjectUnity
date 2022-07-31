package unity.util;

public class StatValue{
    float floatVal;
    int intVal;
    Object objectVal;

    public StatValue(){
    }

    public StatValue(float floatVal){
        this.floatVal = floatVal;
    }

    public StatValue(int intVal){
        this.intVal = intVal;
    }

    public StatValue(Object objectVal){
        this.objectVal = objectVal;
    }

    public interface ValueStruct{
        StatValue add(String key, float val);

        StatValue add(String key, int val);

        StatValue add(String key, Object val);

        StatValue add(String key, StatValue val);

        default float getFloat(String key){
            return getFloat(key, 0);
        }

        float getFloat(String key, float defaultVal);

        default int getInt(String key){
            return getInt(key, 0);
        }

        int getInt(String key, int defaultVal);

        default <T> T getObject(String key){
            return getObject(key, null);
        }

        <T> T getObject(String key, T defaultVal);
    }
}

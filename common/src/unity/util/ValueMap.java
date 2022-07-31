package unity.util;

import arc.func.*;
import arc.struct.*;
import unity.parts.PartType.*;

public class ValueMap{
    ObjectMap<String, Value> map = new ObjectMap<>();

    public void add(String key, float f){
        var value = map.get(key);
        put(key, value != null ? value.floatVal + f : f);
    }

    public void add(String key, int i){
        var value = map.get(key);
        put(key, value != null ? value.intVal + i : i);
    }

    public void mul(String key, float f){
        var value = map.get(key);
        put(key, value != null ? value.floatVal * f : 0);
    }

    public void mul(String key, int i){
        var value = map.get(key);
        put(key, value != null ? value.intVal * i : 0);
    }

    public ValueMap put(String key, float f){
        map.put(key, new Value(f));
        return this;
    }

    public ValueMap put(String key, int i){
        map.put(key, new Value(i));
        return this;
    }

    public ValueMap put(String key, Object o){
        map.put(key, new Value(o));
        return this;
    }

    public boolean has(String name){
        return map.containsKey(name);
    }

    public float getFloat(String key){
        return getFloat(key, 0);
    }

    public float getFloat(String key, float defaultVal){
        var value = map.get(key);
        if(value == null){
            value = new Value(defaultVal);
            map.put(key, value);
        }
        return value.floatVal;
    }

    public float getFloat(String name, String subfield){
        return getValueMap(name).getFloat(subfield);
    }

    public int getInt(String key){
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultVal){
        var value = map.get(key);
        if(value == null){
            value = new Value(defaultVal);
            map.put(key, value);
        }
        return value.intVal;
    }

    public <T> T getObject(String key){
        return getObject(key, () -> null);
    }

    public <T> T getObject(String key, Prov<T> defaultProv){
        var value = map.get(key);
        if(value == null){
            var defaultVal = defaultProv.get();
            value = new Value(defaultVal);
            map.put(key, value);
            return defaultVal;
        }
        return (T)value.objectVal;
    }

    public ValueMap getValueMap(String key){
        return getObject(key, ValueMap::new);
    }

    public void getStats(Seq<? extends Part> parts){
        for(int i = 0; i < parts.size; i++){
            parts.get(i).type.appendStats(this, parts.get(i));
        }
        for(int i = 0; i < parts.size; i++){
            parts.get(i).type.appendStatsPost(this, parts.get(i));
        }
    }

    public static class Value{
        float floatVal;
        int intVal;
        Object objectVal;

        public Value(){}

        public Value(float floatVal){
            this.floatVal = floatVal;
        }

        public Value(int intVal){
            this.intVal = intVal;
        }

        public Value(Object objectVal){
            this.objectVal = objectVal;
        }
    }
}

package younggamExperimental;

import arc.func.*;

public class PartStat{
    public final PartStatType category;
    final Object value;
    final Cons mod;

    public PartStat(PartStatType category, Object value, Cons mod){
        this.category = category;
        this.value = value;
        this.mod = mod;
    }

    public PartStat(PartStatType category, Object value){
        this(category, value, null);
    }

    //I really don't like this. THEN DONT
    public String asString(){
        return (String)value;
    }

    public float asFloat(){
        return (float)value;
    }

    public int asInt(){
        return (int)value;
    }

    public boolean asBool(){
        return (boolean)value;
    }
}

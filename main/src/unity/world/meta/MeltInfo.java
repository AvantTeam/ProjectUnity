package unity.world.meta;

import arc.struct.*;
import mindustry.type.*;

public class MeltInfo{
    public static final MeltInfo[] all = new MeltInfo[14];
    public static final ObjectMap<Item, MeltInfo> map = new ObjectMap<>(14);
    
    public final Item item;
    public final MeltInfo additiveID;
    public final String name;
    
    public final float meltPoint, meltSpeed, evaporation, evaporationTemp, additiveWeight;
    public final int priority;
    public final byte id;
    public final boolean additive;

    private static byte total;

    public MeltInfo(Item item, MeltInfo additiveID, String name, float meltPoint, float meltSpeed, float evaporation, float evaporationTemp, float additiveWeight, int priority, boolean additive){
        this.item = item;
        this.additiveID = additiveID;
        this.name = name;
        
        this.meltPoint = meltPoint;
        this.meltSpeed = meltSpeed;
        this.evaporation = evaporation;
        this.evaporationTemp = evaporationTemp;
        this.additiveWeight = additiveWeight;
        this.priority = priority;
        this.additive = additive;
        
        all[total] = this;
        
        if(item != null) map.put(item, this);
        id = total++;
    }

    public MeltInfo(Item item, float meltPoint, float meltSpeed, float evaporation, float evaporationTemp, int priority){
        this(item, null, item.name, meltPoint, meltSpeed, evaporation, evaporationTemp, -1f, priority, false);
    }

    public MeltInfo(String name, float meltPoint, float meltSpeed, float evaporation, float evaporationTemp, int priority){
        this(null, null, name, meltPoint, meltSpeed, evaporation, evaporationTemp, -1f, priority, false);
    }

    public MeltInfo(Item item, MeltInfo additiveID, float additiveWeight, int priority, boolean additive){
        this(item, additiveID, item.name, -1f, -1f, -1f, -1f, additiveWeight, priority, additive);
    }

    public MeltInfo(Item item, float meltPoint, float meltSpeed, int priority){
        this(item, meltPoint, meltSpeed, -1f, -1f, priority);
    }
}

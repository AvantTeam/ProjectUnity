package unity.mod;

import mindustry.world.meta.*;

public class UnityStats{
    public final static StatCat essence = new StatCat("unity-essence");

    public final static Stat
    essenceCapacity = new Stat("unity-essenceCapacity", essence),
    essenceRange = new Stat("unity-essenceRange", essence),
    essenceConnection = new Stat("unity-essenceConnection", essence),
    essenceFlow = new Stat("unity-essenceFlow", essence),
    essenceFlowFract = new Stat("unity-essenceFlowFrac", essence),
    essenceAbsorb = new Stat("unity-essenceAbsorb", essence);
}

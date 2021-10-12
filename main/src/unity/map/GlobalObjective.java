package unity.map;

import arc.*;

/**
 * Maximum amount is 64.
 * @author GlennFolker
 */
public enum GlobalObjective{
    sectorAccretionComplete;

    private static long currentStatus = 0;

    static{
        update();
    }

    public static boolean reached(GlobalObjective objective){
        update();
        return (currentStatus & objective.value()) == objective.value();
    }

    public static boolean reached(GlobalObjective... objectives){
        for(var o : objectives){
            if(!reached(o)) return false;
        }
        return true;
    }

    public static void fire(GlobalObjective objective){
        if(reached(objective)) return;

        currentStatus |= objective.value();
        Core.settings.put("unity.global-objective.status", currentStatus);

        Events.fire(objective);
    }

    private static void update(){
        currentStatus = Core.settings.getLong("unity.global-objective.status", 0);
    }

    public long value(){
        return 1L << ordinal();
    }
}

package unity.type;

import arc.*;

/** Maximum amount is 64 */
public enum GlobalObjective{
    sectorAccretionComplete;

    private static long currentStatus = 0;

    static{
        update();
    }

    public static boolean reached(GlobalObjective... objectives){
        if(objectives == null || objectives.length <= 0) return true;
        update();

        long value = 0;
        for(GlobalObjective objective : objectives){
            value |= objective.value();
        }

        return value == currentStatus;
    }

    public static void fire(GlobalObjective objective){
        update();
        if(((currentStatus >>> objective.value()) & 1) == 1) return;

        Events.fire(objective);

        currentStatus |= objective.value();
        Core.settings.put("unity.global-objective.status", currentStatus);
    }

    private static void update(){
        currentStatus = Core.settings.getLong("unity.global-objective.status", 0);
    }

    public long value(){
        return 1 << ordinal();
    }
}

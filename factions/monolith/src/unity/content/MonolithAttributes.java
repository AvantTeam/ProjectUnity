package unity.content;

import mindustry.world.meta.*;
import unity.mod.*;

import static mindustry.world.meta.Attribute.*;

/**
 * Defines all {@linkplain Faction#monolith monolith} environmental block attributes.
 * @author GlennFolker
 */
public final class MonolithAttributes{
    /** Intermediate infusion in floors, usually marked in dark blue strands or shards. */
    public static Attribute
    intermediateInfusion,
    /** Intermediate emission in vent-like cavities. */
    intermediateEmission;

    private MonolithAttributes(){
        throw new AssertionError();
    }

    public static void load(){
        intermediateInfusion = add("unity-monolith-intermediate-infusion");
        intermediateEmission = add("unity-monolith-intermediate-emission");
    }
}

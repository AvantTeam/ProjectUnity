package unity.content;

import mindustry.world.meta.*;
import unity.mod.*;

import static mindustry.world.meta.Attribute.*;

/**
 * Defines all {@linkplain Faction#monolith monolith} environmental block attributes.
 * @author GlennFolker
 */
public final class MonolithAttributes{
    /** Eneraphyte infusion in floors, usually marked in dark blue strands or shards. */
    public static Attribute
    eneraphyteInfusion,
    /** Eneraphyte emission in vent-like cavities. */
    eneraphyteEmission;

    private MonolithAttributes(){
        throw new AssertionError();
    }

    public static void load(){
        eneraphyteInfusion = add("unity-eneraphyte-infusion");
        eneraphyteEmission = add("unity-eneraphyte-emission");
    }
}

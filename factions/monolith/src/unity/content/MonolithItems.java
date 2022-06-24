package unity.content;

import mindustry.type.*;
import unity.content.type.*;
import unity.mod.*;

import static unity.graphics.MonolithPal.*;

/**
 * Defines all {@linkplain Faction#monolith monolith} liquids and gasses.
 * @author GlennFolker
 */
public final class MonolithItems{
    public static Item
    proximite;

    private MonolithItems(){
        throw new AssertionError();
    }

    public static void load(){
        proximite = new PUItem("proximite", monolithLight);
    }
}

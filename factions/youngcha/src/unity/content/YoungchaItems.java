package unity.content;

import arc.graphics.*;
import mindustry.type.*;
import unity.mod.*;
import unity.type.*;

import static unity.mod.FactionRegistry.register;

/**
 * Defines all {@linkplain Faction#youngcha youngcha} item types.
 * @author younggam, xelo
 */
public class YoungchaItems{
    public static Item nickel, cupronickel, superAlloy;

    private YoungchaItems(){
        throw new AssertionError();
    }

    public static void load(){
        nickel = register(Faction.youngcha, new PUItem("nickel", Color.valueOf("6e9675")){{
            hardness = 3;
            cost = 1.5f;
        }});

        cupronickel = register(Faction.youngcha, new Item("cupronickel", Color.valueOf("a19975")){{
            cost = 2.5f;
        }});

        superAlloy = register(Faction.youngcha, new Item("super-alloy", Color.valueOf("67a8a0")){{
            cost = 30f; // ridiculously high cost to be justified later
        }});
    }
}

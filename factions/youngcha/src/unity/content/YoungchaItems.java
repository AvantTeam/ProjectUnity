package unity.content;

import arc.graphics.*;
import mindustry.type.*;
import unity.content.type.*;
import unity.mod.*;

/**
 * Defines all {@linkplain Faction#youngcha youngcha} items.
 * @author younggam, xelo
 */
public final class YoungchaItems{
    public static Item
    nickel, cupronickel, superAlloy;

    private YoungchaItems(){
        throw new AssertionError();
    }

    public static void load(){
        nickel = new PUItem("nickel", Color.valueOf("6e9675")){{
            hardness = 3;
            cost = 1.5f;
        }};

        cupronickel = new PUItem("cupronickel", Color.valueOf("a19975")){{
            cost = 2.5f;
        }};

        superAlloy = new PUItem("super-alloy", Color.valueOf("67a8a0")){{
            cost = 30f; // ridiculously high cost to be justified later
        }};
    }
}

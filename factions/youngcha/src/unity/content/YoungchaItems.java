package unity.content;

import arc.graphics.*;
import mindustry.type.*;
import unity.content.type.*;
import unity.mod.*;

import static unity.mod.FactionRegistry.*;

/**
 * Defines all {@linkplain Faction#youngcha youngcha} item types.
 * @author younggam, xelo
 */
public class YoungchaItems{
    public static Item nickel;

    private YoungchaItems(){
        throw new AssertionError();
    }

    public static void load(){
        nickel = register(Faction.youngcha, new PUItem("nickel", Color.valueOf("6e9675")){{
            hardness = 3;
            cost = 1.5f;
        }});
    }
}

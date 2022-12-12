package unity.content;

import arc.graphics.*;
import mindustry.type.*;
import unity.content.type.*;
import unity.graphics.*;

public class EndItems{
    public static Item orsusite;

    public static void load(){
        orsusite = new PUItem("orsusite", new Color(0x5a545cff)){{
            hardness = 3;
            healthScaling = 0.125f;
        }};
    }
}

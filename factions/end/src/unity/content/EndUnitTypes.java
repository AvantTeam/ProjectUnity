package unity.content;

import unity.entities.prop.*;
import unity.entities.type.*;
import unity.gen.entities.*;
import unity.graphics.*;
import unity.mod.*;

import static unity.gen.entities.EntityRegistry.content;
import static unity.mod.FactionRegistry.register;

public final class EndUnitTypes{
    public static PUUnitType eversion;

    public static void load(){
        EndProps.add = EndCurse::addUnit;
        EndProps.remove = EndCurse::removeUnit;

        eversion = register(Faction.end, content("eversion", EndMechUnit.class, n -> new PUUnitType(n){{
            health = 800f;
            speed = 0.6f;
            hitSize = 13f;
            mechFrontSway = 0.4f;
            armor = 30f;
            outlineColor = EndPal.endOutline;
            mechLegColor = EndPal.endSolidDark;

            prop(new EndProps(){{
                maxDamage = 700f;
                maxDamageCurve = 90f;
                invincibilityFrames = 40f;
                invincibilityTrigger = 150f;
            }});
        }}));
    }
}

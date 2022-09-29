package unity.content;

import arc.graphics.*;
import mindustry.entities.pattern.*;
import mindustry.type.*;
import unity.assets.list.*;
import unity.entities.prop.*;
import unity.entities.type.*;
import unity.entities.type.bullet.*;
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

        eversion = register(Faction.end, content("eversion", EndInvisibleMechUnit.class, n -> new InvisibleUnitType(n){{
            health = 800f;
            speed = 0.6f;
            hitSize = 13f;
            mechFrontSway = 0.4f;
            armor = 30f;
            outlineColor = EndPal.endOutline;
            mechLegColor = EndPal.endSolidDark;

            invisibilityShader = PUShaders.dimensionshift;
            fadeColor = Color.red;

            prop(new EndProps(){{
                maxDamage = 700f;
                maxDamageCurve = 90f;
                invincibilityFrames = 40f;
                invincibilityTrigger = 150f;
            }});

            weapons.add(new Weapon(""){{
                x = 0f;
                y = 0f;
                reload = 120f;
                mirror = false;
                bullet = new EndTentacleBulletType();
                inaccuracy = 35f;
                shoot = new ShootPattern(){{
                    shotDelay = 4f;
                    shots = 6;
                }};
            }});
        }}));
    }
}

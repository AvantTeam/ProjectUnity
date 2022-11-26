package unity.content;

import arc.graphics.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.weapons.*;
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
    public static PUUnitType eversion,
            constrict;

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

        constrict = register(Faction.end, content("constrict", EndRestrictedUnit.class, n -> new PUUnitType(n){{
            health = 600f;
            flying = true;
            drag = 0.1f;
            speed = 8.5f;
            accel = 0.1f;
            hitSize = 13f;
            buildSpeed = 0.001f;
            armor = 20f;

            mineTier = 3;
            mineSpeed = 9f;
            payloadCapacity = 0f;
            itemCapacity = 100;
            hitSize = 12f;
            rotateSpeed = 12f;

            isEnemy = false;
            targetPriority = -2;
            lowAltitude = false;
            mineWalls = true;
            mineFloor = true;
            mineHardnessScaling = false;
            outlineColor = EndPal.endOutline;

            prop(new EndProps(){{
                maxDamage = 500f;
                maxDamageCurve = 90f;
                invincibilityFrames = 30f;
                invincibilityTrigger = 150f;
            }});
            prop(new RestrictedProps());

            weapons.add(new RepairBeamWeapon(""){{
                widthSinMag = 0.11f;
                reload = 20f;
                x = 0f;
                y = 8f;
                rotate = false;
                shootY = 0f;
                beamWidth = 0.7f;
                aimDst = 0f;
                shootCone = 40f;
                mirror = false;

                repairSpeed = 0.7f;
                fractionRepairSpeed = 0.02f;

                targetUnits = false;
                targetBuildings = true;
                autoTarget = false;
                controllable = true;
                laserColor = Pal.accent;
                healColor = Pal.accent;

                bullet = new BulletType(){{
                    maxRange = 40f;
                }};
            }});
        }}));
    }
}

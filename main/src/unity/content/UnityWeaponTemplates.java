package unity.content;

import arc.graphics.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.graphics.*;
import unity.entities.bullet.*;
import unity.gen.*;
import unity.graphics.*;
import unity.type.*;

public class UnityWeaponTemplates implements ContentList{
    public static CloneableSetWeapon apocalypseSmall, apocalypseLauncher, apocalypseLaser, waveformSmallMount;

    @Override
    public void load(){
        apocalypseSmall = new CloneableSetWeapon("unity-ravager-small-turret"){{
            reload = 2f * 60f;
            shootY = 6.5f;
            shots = 3;
            spacing = 15f;
            shootCone = 10f;
            shadow = 15f;
            mirror = true;
            alternate = true;
            rotate = true;

            bullet = new AntiCheatBasicBulletType(6f, 140f){{
                lifetime = 70f;
                width = 15f;
                height = 19f;
                shrinkY = 0f;
                backColor = hitColor = lightColor = UnityPal.scarColor;
                frontColor = UnityPal.endColor;
                tolerance = 21000f;
                fade = 400f;
            }};
        }};

        apocalypseLauncher = new CloneableSetWeapon("unity-doeg-launcher"){{
            reload = 3.5f * 60f;
            rotateSpeed = 6f;
            shootY = 6.5f;
            shots = 12;
            shotDelay = 6f;
            inaccuracy = 20f;
            shootCone = 10f;
            shadow = 24f;
            mirror = true;
            alternate = true;
            rotate = true;

            bullet = new AntiCheatBasicBulletType(6f, 220f, "missile"){{
                lifetime = 80f;
                width = 15f;
                height = 17f;
                shrinkY = 0f;
                drag = -0.013f;
                splashDamageRadius = 40f;
                splashDamage = 210f;
                backColor = trailColor = hitColor = lightColor = UnityPal.scarColor;
                frontColor = UnityPal.endColor;
                trailChance = 0.2f;
                homingPower = 0.08f;
                weaveScale = 6f;
                weaveMag = 1.2f;
                priority = 2;
                hitEffect = Fx.blastExplosion;
                despawnEffect = Fx.blastExplosion;
                tolerance = 19000f;
                fade = 200f;
            }};
        }};

        apocalypseLaser = new CloneableSetWeapon("unity-ravager-artillery"){{
            reload = 5f * 60f;
            rotateSpeed = 2f;
            shootY = 6.5f;
            shootCone = 10f;
            shadow = 24f;
            shootSound = UnitySounds.continuousLaserB;
            continuous = true;
            rotate = true;
            mirror = true;
            alternate = false;

            bullet = UnityBullets.endLaserSmall;
        }};

        waveformSmallMount = new CloneableSetWeapon("unity-emp-small-mount"){{
            reload = 6f;
            mirror = false;
            alternate = true;
            rotate = true;
            shootSound = UnitySounds.zbosonShoot;

            bullet = new EmpBasicBulletType(5.5f, 9f){{
                lifetime = 38f;
                splashDamageRadius = 15f;
                splashDamage = 1.5f;
                shrinkY = 0f;
                height = 13f;
                width = 10f;

                powerGridIteration = 1;
                empDuration = 0f;
                empBatteryDamage = 4000f;
                empRange = 90f;

                hitEffect = Fx.hitLancer;
                backColor = lightColor = hitColor = Pal.lancerLaser;
                frontColor = Color.white;
            }};
        }};
    }
}

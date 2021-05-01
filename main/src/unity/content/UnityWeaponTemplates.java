package unity.content;

import arc.graphics.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import unity.entities.bullet.*;
import unity.gen.*;
import unity.graphics.*;
import unity.type.*;

public class UnityWeaponTemplates implements ContentList{
    public static CloneableSetWeapon apocalypseSmall, apocalypseLauncher, apocalypseLaser, waveformSmallMount, ultravioletMount, plagueSmallMount;

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
                height = 12f;
                width = 9f;

                powerGridIteration = 1;
                empDuration = 0f;
                empBatteryDamage = 4000f;
                empRange = 90f;

                hitEffect = Fx.hitLancer;
                backColor = lightColor = hitColor = Pal.lancerLaser;
                frontColor = Color.white;
            }};
        }};

        ultravioletMount = new CloneableSetWeapon("unity-emp-small-launcher"){{
            shootY = 6.75f;
            reload = 20f;
            mirror = false;
            alternate = true;
            rotate = true;
            shootSound = UnitySounds.zbosonShoot;

            bullet = new EmpBasicBulletType(5.7f, 25f){{
                lifetime = 40f;
                splashDamageRadius = 15f;
                splashDamage = 5f;
                shrinkY = 0f;
                height = 14f;
                width = 10f;

                powerGridIteration = 7;
                empDuration = 20f;
                empBatteryDamage = 8000f;
                empRange = 120f;

                hitEffect = Fx.hitLancer;
                backColor = lightColor = hitColor = Pal.lancerLaser;
                frontColor = Color.white;
            }};
        }};

        plagueSmallMount = new CloneableSetWeapon("unity-small-plague-launcher"){{
            shootY = 4.75f;
            reload = 1.5f * 60f;
            shots = 4;
            inaccuracy = 15f;
            mirror = false;
            alternate = true;
            rotate = true;

            bullet = new MissileBulletType(3.8f, 9f){{
                width = height = 8f;
                lifetime = 45f;
                backColor = hitColor = lightColor = trailColor = UnityPal.plagueDark;
                frontColor = UnityPal.plague;
                shrinkY = 0f;
                drag = -0.01f;
                splashDamage = 17f;
                splashDamageRadius = 30f;
                weaveScale = 8f;
                weaveMag = 2f;
                hitEffect = Fx.blastExplosion;
                despawnEffect = Fx.blastExplosion;
            }};
        }};
    }
}

package unity.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.world.meta.*;
import unity.ai.*;
import unity.ai.AssistantAI.*;
import unity.annotations.Annotations.*;
import unity.content.effects.*;
import unity.entities.*;
import unity.entities.abilities.*;
import unity.entities.bullet.anticheat.*;
import unity.entities.bullet.anticheat.modules.*;
import unity.entities.bullet.energy.*;
import unity.entities.bullet.laser.*;
import unity.entities.bullet.misc.*;
import unity.entities.bullet.physical.*;
import unity.entities.units.*;
import unity.gen.*;
import unity.graphics.*;
import unity.type.*;
import unity.type.decal.*;
import unity.type.weapons.*;

import static mindustry.Vars.*;
import static unity.content.UnityWeaponTemplates.*;

@SuppressWarnings("unchecked")
public class UnityUnitTypes{
    // global unit + copter
    public static @EntityDef({Unitc.class, Copterc.class})
    UnitType caelifera, schistocerca, anthophila, vespula, lepidoptera, mantodea;

    // global flying
    public static @EntityPoint(UnitEntity.class)
    UnitType angel, malakhim,
        discharge, pulse, emission, waveform, ultraviolet;

    // global T6/7 units

    // reign
    public static @EntityPoint(MechUnit.class)
    UnitType citadel, empire;
    // corvus + toxopid
    public static @EntityPoint(LegsUnit.class)
    UnitType cygnus, sagittarius, araneidae, theraphosidae;
    // eclipse
    public static @EntityPoint(UnitEntity.class)
    UnitType mantle, aphelion;
    // oct
    public static @EntityPoint(PayloadUnit.class)
    UnitType sedec, trigintaduo;
    // omura
    public static @EntityPoint(UnitWaterMove.class)
    UnitType fin, blue;

    // global unit + watermove + transform
    public static @EntityDef({Unitc.class, WaterMovec.class, Transc.class})
    UnitType amphibiNaval, amphibi;

    // global unit + legs + transform
    public static @EntityDef({Unitc.class, Legsc.class, Transc.class})
    UnitType craberNaval, craber;

    // scar legs
    public static @FactionDef("scar") @EntityPoint(LegsUnit.class)
    UnitType hovos, ryzer, zena, sundown, rex, excelsus;

    // scar flying
    public static @FactionDef("scar") @EntityPoint(UnitEntity.class)
    UnitType whirlwind, jetstream, vortex;

    // imber worm
    public static @FactionDef("imber") @EntityPoint(WormDefaultUnit.class)
    UnitType arcnelidia;

    // imber
    public static @FactionDef("imber") @EntityDef({Unitc.class, Imberc.class})
    UnityUnitType rayTest;

    // imber... random stuff
    public static @FactionDef("imber") @EntityPoint(UnitEntity.class)
    UnityUnitType testLink, test;

    // plague unit + trijointleg
    public static @FactionDef("plague") @EntityDef({Unitc.class, TriJointLegsc.class})
    UnitType exowalker;

    // plague unit + worm
    public static @FactionDef("plague") @EntityDef({Unitc.class, Wormc.class})
    UnitType toxobyte, catenapede;

    // koruh mech
    public static @FactionDef("koruh") @EntityPoint(MechUnit.class)
    UnitType buffer, omega;

    // koruh flying
    public static @FactionDef("koruh") @EntityPoint(UnitEntity.class)
    UnitType cache, dijkstra, phantasm;

    public static @FactionDef("monolith") @EntityPoint(MonolithSoul.class)
    UnitType monolithSoul;

    // monolith unit + mech
    public static @FactionDef("monolith") @EntityDef({Unitc.class, Mechc.class, Monolithc.class})
    UnitType stele, pedestal, pilaster;

    // monolith unit + legs
    public static @FactionDef("monolith") @EntityDef({Unitc.class, Legsc.class, Monolithc.class})
    UnitType pylon, monument, colossus, bastion;

    // monolith unit + assistant
    public static @FactionDef("monolith") @EntityDef({Unitc.class, Assistantc.class, Monolithc.class})
    UnitType adsect, comitate/*, praesid*/;

    // koruh kami
    public static @FactionDef("koruh") @EntityPoint(Kami.class)
    UnitType kami;

    public static @FactionDef("advance") @EntityDef({Unitc.class, Decorationc.class}) UnitType deviation, anomaly;

    // end
    public static @FactionDef("end") @EntityDef({Unitc.class, Endc.class}) UnitType voidVessel;

    public static @FactionDef("end") @EntityDef({Unitc.class, Endc.class, TimeStopVelc.class}) UnitType chronos;

    // end invisible
    public static @FactionDef("end") @EntityDef({Unitc.class, Endc.class, Invisiblec.class}) UnitType opticaecus;

    // end worm
    public static @FactionDef("end") @EntityDef({Unitc.class, Endc.class, Wormc.class}) UnitType devourer, oppression;

    // end apocalypse
    public static @FactionDef("end") @EntityDef({Unitc.class, Endc.class, Invisiblec.class, Tentaclec.class}) UnitType apocalypse;

    // end legs
    public static @FactionDef("end") @EntityDef({Unitc.class, Endc.class, Legsc.class}) UnitType ravager;
    public static @FactionDef("end") @EntityDef({Unitc.class, Endc.class, Legsc.class, Tentaclec.class, Tallc.class}) UnitType desolation;

    // end naval
    public static @FactionDef("end") @EntityDef({Unitc.class, Endc.class, WaterMovec.class, Decorationc.class}) UnitType thalassophobia;

    // monolith characters
    public static @FactionDef("monolith") @EntityDef({Unitc.class, Mechc.class, Decorationc.class, Monolithc.class}) UnitType charShadowcape;

    public static void load(){
        testLink = new UnityUnitType("test-link"){{
            defaultController = LinkedAI::new;
            rotationSpeed = 65f;
            speed = 1f;
            drag = 0.08f;
            accel = 0.04f;
            fallSpeed = 0.005f;
            health = 75;
            engineSize = 0f;
            hovering = true;
            hitSize = 12f;
            range = 140f;
        }};

        test = new UnityUnitType("test"){{
            defaultController = LinkerAI::new;
            linkType = testLink;
            linkCount = 5;
            rotationSpeed = 65f;
            speed = 1f;
            drag = 0.08f;
            accel = 0.04f;
            fallSpeed = 0.005f;
            health = 75;
            engineSize = 0f;
            hovering = true;
            hitSize = 12f;
            range = 140f;
        }};

        //region flying-units

        caelifera = new UnityUnitType("caelifera"){{
            defaultController = CopterAI::new;
            speed = 5f;
            drag = 0.08f;
            accel = 0.04f;
            fallSpeed = 0.005f;
            health = 75;
            engineSize = 0f;
            flying = true;
            hitSize = 12f;
            range = 140f;

            weapons.add(new Weapon(name + "-gun"){{
                bottomWeapons.add(this);

                reload = 6f;
                x = 5.25f;
                y = 6.5f;
                shootY = 1.5f;
                shootSound = Sounds.pew;
                ejectEffect = Fx.casing1;
                bullet = new BasicBulletType(5f, 7f){{
                    lifetime = 30f;
                    shrinkY = 0.2f;
                }};
            }},
            new Weapon(name + "-launcher"){{
                reload = 30f;
                x = 4.5f;
                y = 0.5f;
                shootY = 2.25f;
                shootSound = Sounds.shootSnap;
                ejectEffect = Fx.casing2;
                bullet = new MissileBulletType(3f, 1f){{
                    speed = 3f;
                    lifetime = 45f;
                    splashDamage = 40f;
                    splashDamageRadius = 8f;
                    drag = -0.01f;
                }};
            }});

            rotors.add(new Rotor(name + "-rotor"){{
                x = 0f;
                y = 6f;
            }});
        }};

        schistocerca = new UnityUnitType("schistocerca"){{
            defaultController = CopterAI::new;
            speed = 4.5f;
            drag = 0.07f;
            accel = 0.03f;
            fallSpeed = 0.005f;
            health = 150;
            engineSize = 0f;
            flying = true;
            hitSize = 13f;
            range = 165f;
            rotateSpeed = 4.6f;

            weapons.add(new Weapon(name + "-gun"){{
                bottomWeapons.add(this);

                top = false;
                x = 1.5f;
                y = 11f;
                shootX = -0.75f;
                shootY = 3f;
                shootSound = Sounds.pew;
                ejectEffect = Fx.casing1;
                reload = 8f;
                bullet = new BasicBulletType(4f, 5f){{
                    lifetime = 36;
                    shrinkY = 0.2f;
                }};
            }},
            new Weapon(name + "-gun"){{
                top = false;
                x = 4f;
                y = 8.75f;
                shootX = -0.75f;
                shootY = 3f;
                shootSound = Sounds.shootSnap;
                ejectEffect = Fx.casing1;
                reload = 12f;
                bullet = new BasicBulletType(4f, 8f){{
                    width = 7f;
                    height = 9f;
                    lifetime = 36f;
                    shrinkY = 0.2f;
                }};
            }},
            new Weapon(name + "-gun-big"){{
                x = 6.75f;
                y = 5.75f;
                shootX = -0.5f;
                shootY = 2f;
                shootSound = Sounds.shootSnap;
                ejectEffect = Fx.casing1;
                reload = 30f;
                bullet = Bullets.standardIncendiary;
            }});

            for(int i : Mathf.signs){
                rotors.add(new Rotor(name + "-rotor"){{
                    x = 0f;
                    y = 6.5f;
                    bladeCount = 3;
                    ghostAlpha = 0.4f;
                    shadowAlpha = 0.2f;
                    shadeSpeed = 3f * i;
                    speed = 29f * i;
                }});
            }
        }};

        anthophila = new UnityUnitType("anthophila"){{
            defaultController = CopterAI::new;
            speed = 4f;
            drag = 0.07f;
            accel = 0.03f;
            fallSpeed = 0.005f;
            health = 450;
            engineSize = 0f;
            flying = true;
            hitSize = 15f;
            range = 165f;
            fallRotateSpeed = 2f;
            rotateSpeed = 3.8f;

            weapons.add(new Weapon(name + "-gun"){{
                bottomWeapons.add(this);

                x = 4.25f;
                y = 14f;
                shootX = -1f;
                shootY = 2.75f;
                reload = 15;
                shootSound = Sounds.shootBig;
                bullet = new BasicBulletType(6f, 60f){{
                    lifetime = 30f;
                    width = 16f;
                    height = 20f;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootBigSmoke;
                }};
            }});

            weapons.add(new Weapon(name + "-tesla"){{
                x = 7.75f;
                y = 8.25f;
                shootY = 5.25f;
                reload = 30f;
                shots = 3;
                shootSound = Sounds.spark;
                bullet = new LightningBulletType(){{
                    damage = 15f;
                    lightningLength = 12;
                    lightningColor = Pal.surge;
                }};
            }});

            for(int i : Mathf.signs){
                rotors.add(new Rotor(name + "-rotor2"){{
                    x = 0f;
                    y = -13f;
                    bladeCount = 2;
                    ghostAlpha = 0.4f;
                    shadowAlpha = 0.2f;
                    shadeSpeed = 3f * i;
                    speed = 29f * i;
                }});
            }

            rotors.add(new Rotor(name + "-rotor1"){{
                mirror = true;
                x = 13f;
                y = 3f;
                bladeCount = 3;
            }});
        }};

        vespula = new UnityUnitType("vespula"){{
            defaultController = CopterAI::new;
            speed = 3.5f;
            drag = 0.07f;
            accel = 0.03f;
            fallSpeed = 0.003f;
            health = 4000;
            engineSize = 0f;
            flying = true;
            hitSize = 30f;
            range = 165f;
            lowAltitude = true;
            rotateSpeed = 3.5f;

            weapons.add(new Weapon(name + "-gun-big"){{
                x = 8.25f;
                y = 9.5f;
                shootX = -1f;
                shootY = 7.25f;
                reload = 12f;
                shootSound = Sounds.shootBig;
                bullet = new BasicBulletType(6f, 60f){{
                    lifetime = 30f;
                    width = 16f;
                    height = 20f;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootBigSmoke;
                }};
            }},
            new Weapon(name + "-gun"){{
                bottomWeapons.add(this);

                x = 6.5f;
                y = 21.5f;
                shootX = -0.25f;
                shootY = 5.75f;
                reload = 20f;
                shots = 4;
                shotDelay = 2f;
                shootSound = Sounds.shootSnap;
                bullet = Bullets.standardThorium;
            }},
            new Weapon(name + "-laser-gun"){{
                x = 13.5f;
                y = 15.5f;
                shootY = 4.5f;
                reload = 60f;
                shootSound = Sounds.laser;
                bullet = new LaserBulletType(240f){{
                    sideAngle = 45f;
                    length = 200f;
                }};
            }});

            for(int i : Mathf.signs){
                rotors.add(new Rotor(name + "-rotor"){{
                    mirror = true;
                    x = 15f;
                    y = 6.75f;
                    speed = 29f * i;
                    ghostAlpha = 0.4f;
                    shadowAlpha = 0.2f;
                    shadeSpeed = 3f * i;
                }});
            }
        }};

        lepidoptera = new UnityUnitType("lepidoptera"){{
            defaultController = CopterAI::new;
            speed = 3f;
            drag = 0.07f;
            accel = 0.03f;
            fallSpeed = 0.003f;
            health = 9500;
            engineSize = 0f;
            flying = true;
            hitSize = 45f;
            range = 300f;
            lowAltitude = true;
            fallRotateSpeed = 0.8f;
            rotateSpeed = 2.7f;

            weapons.add(new Weapon(name + "-gun"){{
                bottomWeapons.add(this);

                x = 14f;
                y = 27f;
                shootY = 5.5f;
                shootSound = Sounds.shootBig;
                ejectEffect = Fx.casing3Double;
                reload = 10f;
                bullet = new BasicBulletType(7f, 80f){{
                    lifetime = 30f;
                    width = 18f;
                    height = 22f;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootBigSmoke;
                }};
            }},
            new Weapon(name + "-launcher"){{
                x = 17f;
                y = 14f;
                shootY = 5.75f;
                shootSound = Sounds.shootSnap;
                ejectEffect = Fx.casing2;
                shots = 2;
                spacing = 2f;
                reload = 20f;
                bullet = new MissileBulletType(6f, 1f){{
                    width = 8f;
                    height = 14f;
                    trailColor = Pal.missileYellowBack;
                    weaveScale = 2f;
                    weaveMag = 2f;
                    lifetime = 35f;
                    drag = -0.01f;
                    splashDamage = 48f;
                    splashDamageRadius = 12f;
                    frontColor = Pal.missileYellow;
                    backColor = Pal.missileYellowBack;
                }};
            }},
            new Weapon(name + "-gun-big"){{
                rotate = true;
                rotateSpeed = 3f;
                x = 8f;
                y = 3f;
                shootY = 6.75f;
                shootSound = Sounds.shotgun;
                ejectEffect = Fx.none;
                shots = 3;
                spacing = 15f;
                shotDelay = 0f;
                reload = 45f;
                bullet = new ShrapnelBulletType(){{
                    toColor = Pal.accent;
                    damage = 150f;
                    keepVelocity = false;
                    length = 150f;
                }};
            }});

            for(int i : Mathf.signs){
                rotors.add(new Rotor(name + "-rotor1"){{
                    mirror = true;
                    x = 22.5f;
                    y = 21.25f;
                    bladeCount = 3;
                    speed = 19f * i;
                    ghostAlpha = 0.4f;
                    shadowAlpha = 0.2f;
                    shadeSpeed = 3f * i;
                }}, new Rotor(name + "-rotor2"){{
                    mirror = true;
                    x = 17.25f;
                    y = 1f;
                    bladeCount = 2;
                    speed = 23f * i;
                    ghostAlpha = 0.4f;
                    shadowAlpha = 0.2f;
                    shadeSpeed = 4f * i;
                }});
            }
        }};

        mantodea = new UnityUnitType("mantodea"){{
            defaultController = CopterAI::new;
            speed = 5f;
            drag = 0.1f;
            accel = 0.03f;
            fallSpeed = 0.0025f;
            health = 25000;
            engineSize = 0f;
            flying = true;
            hitSize = 45f;
            lowAltitude = true;
            fallRotateSpeed = 0.8f;
            rotateSpeed = 2.2f;

            BulletType flak = new FlakBulletType(8f, 20f){{
                lifetime = 10f;
                collidesGround = true;

                lightning = 3;
                lightningLength = 4;
                lightningLengthRand = 2;
                lightningDamage = 15f;
                lightningColor = Pal.surge;
            }};

            weapons.add(new Weapon(name + "-gun"){{
                bottomWeapons.add(this);

                mirror = true;
                rotate = false;
                x = 14.25f;
                y = 26.5f;
                recoil = 2.5f;
                shootY = 10f;

                shootSound = Sounds.shoot;
                shots = 3;
                spacing = 0f;
                shotDelay = 3f;
                reload = 25f;

                bullet = flak;
            }}, new Weapon(name + "-gun"){{
                bottomWeapons.add(this);

                mirror = true;
                rotate = false;
                x = 26.25f;
                y = 19.5f;
                recoil = 2.5f;
                shootY = 10f;

                shootSound = Sounds.shoot;
                shots = 2;
                spacing = 0f;
                shotDelay = 3f;
                reload = 15f;

                bullet = flak;
            }});

            for(int i : Mathf.signs){
                rotors.add(new Rotor(name + "-rotor2"){{
                    y = -31.25f;
                    bladeCount = 4;
                    speed = 19f * i;
                    ghostAlpha = 0.4f;
                    shadowAlpha = 0.2f;
                    shadeSpeed = 4f * i;
                }}, new Rotor(name + "-rotor3"){{
                    mirror = true;
                    x = 28.5f;
                    y = -11.75f;
                    bladeCount = 3;
                    speed = 23f * i;
                    ghostAlpha = 0.4f;
                    shadowAlpha = 0.2f;
                    shadeSpeed = 3f * i;
                }});
            }

            rotors.add(new Rotor(name + "-rotor1"){{
                y = 9.25f;
                bladeCount = 3;
                speed = 29f;
                shadeSpeed = 5f;
                bladeFade = 0.8f;
            }});
        }};

        angel = new UnityUnitType("angel"){{
            defaultController = HealerAI::new;
            buildSpeed = 10f;
            health = 90;
            engineOffset = 5.7f;
            flying = true;
            speed = 4.3f;
            accel = 0.08f;
            drag = 0.01f;
            range = 40f;
            commandLimit = 0;
            ammoType = new PowerAmmoType(1000);
            hitSize = 9f;
        }};

        malakhim = new UnityUnitType("malakhim"){{
            defaultController = HealerAI::new;
            buildSpeed = 15f;
            health = 170;
            engineOffset = 11.7f;
            flying = true;
            speed = 3.9f;
            accel = 0.08f;
            drag = 0.01f;
            range = 50f;
            commandLimit = 2;
            ammoType = new PowerAmmoType(1000);
            hitSize = 10.5f * 1.7f;

            weapons.add(new Weapon("heal-weapon-mount"){{
                rotate = true;
                x = 11f;
                y = -7f;
                reload = 10f;
                bullet = new LaserBoltBulletType(5.2f, 10f){{
                    lifetime = 35f;
                    healPercent = 5.5f;
                    collidesTeam = true;
                    backColor = Pal.heal;
                    frontColor = Color.white;
                }};
            }});
        }};

        discharge = new UnityUnitType("discharge"){{
            flying = true;
            lowAltitude = true;
            health = 60f;
            speed = 2f;
            accel = 0.09f;
            drag = 0.02f;
            hitSize = 11.5f;
            engineOffset = 7.25f;
            ammoType = new PowerAmmoType(1000);

            weapons.add(new Weapon(){{
                rotate = false;
                mirror = false;
                x = 0f;
                y = 0f;
                shootY = 4f;
                reload = 4f * 60f;
                shootSound = UnitySounds.zbosonShoot;

                bullet = new EmpBasicBulletType(6f, 3f){{
                    lifetime = 35f;
                    splashDamageRadius = 20f;
                    splashDamage = 3f;
                    shrinkY = 0f;
                    height = 14f;
                    width = 11f;
                    trailWidth = (width / 2f) / 2f;

                    hitEffect = Fx.hitLancer;
                    trailColor = backColor = lightColor = hitColor = Pal.lancerLaser;
                    frontColor = Color.white;
                }};
            }});
        }};

        pulse = new UnityUnitType("pulse"){{
            flying = true;
            lowAltitude = true;
            health = 210f;
            speed = 1.8f;
            accel = 0.1f;
            drag = 0.06f;
            hitSize = 16.5f;
            engineOffset = 8.25f;
            ammoType = new PowerAmmoType(1000);

            weapons.add(new Weapon(){{
                rotate = false;
                mirror = false;
                x = 0f;
                y = 0f;
                shootY = 7f;
                reload = 3f * 60f;
                firstShotDelay = 70f;
                shootStatus = StatusEffects.unmoving;
                shootStatusDuration = 70f;
                shootSound = UnitySounds.zbosonShoot;

                bullet = new EmpBasicBulletType(6.25f, 4f){{
                    splashDamageRadius = 25f;
                    splashDamage = 9f;
                    shrinkY = 0f;
                    height = 16f;
                    width = 12f;
                    trailWidth = (width / 2f) / 2f;

                    empRange = 120f;
                    empDisconnectRange = 40f;
                    empBatteryDamage = 11000f;
                    empLogicDamage = 5f;

                    hitEffect = Fx.hitLancer;
                    trailColor = backColor = lightColor = hitColor = Pal.lancerLaser;
                    frontColor = Color.white;
                    shootEffect = UnityFx.empCharge;
                }};
            }});
        }};

        emission = new UnityUnitType("emission"){{
            flying = true;
            lowAltitude = true;
            health = 550f;
            speed = 1.2f;
            accel = 0.1f;
            drag = 0.07f;
            hitSize = 24.5f;
            engineOffset = 3.75f;
            ammoType = new PowerAmmoType(1000);

            weapons.add(new Weapon("unity-emp-launcher"){{
                rotate = true;
                mirror = true;
                x = 11.75f;
                y = -7.25f;
                shootY = 5f;
                shootSound = UnitySounds.zbosonShoot;
                reload = 1.7f * 60f;

                bullet = new EmpBasicBulletType(6f, 2f){{
                    lifetime = 35f;
                    splashDamageRadius = 17f;
                    splashDamage = 2f;
                    shrinkY = 0f;
                    height = 13f;
                    width = 10f;
                    trailWidth = (width / 2f) / 2f;

                    powerGridIteration = 5;
                    empDuration = 15f;
                    empBatteryDamage = 4300f;
                    empRange = 90f;

                    hitEffect = Fx.hitLancer;
                    trailColor = backColor = lightColor = hitColor = Pal.lancerLaser;
                    frontColor = Color.white;
                }};
            }}, new Weapon(){{
                mirror = false;
                x = 0f;
                y = -11.5f;
                shootY = 0f;
                shootSound = UnitySounds.zbosonShoot;
                shootStatus = StatusEffects.unmoving;
                shootStatusDuration = 70f;
                reload = 5f * 60f;
                firstShotDelay = 70f;

                bullet = new EmpBasicBulletType(6.7f, 8f){{
                    splashDamageRadius = 30f;
                    splashDamage = 12f;
                    shrinkY = 0f;
                    height = 17f;
                    width = 13f;

                    empRange = 150f;
                    empDuration = 60f * 2f;
                    empMaxRange = 800f;
                    empDisconnectRange = 95f;
                    empBatteryDamage = 26000f;
                    empLogicDamage = 12f;
                    powerGridIteration = 15;
                    trailLength = 13;
                    trailWidth = (width / 2f) / 2f;

                    hitEffect = Fx.hitLancer;
                    trailColor = backColor = lightColor = hitColor = Pal.lancerLaser;
                    frontColor = Color.white;
                    shootEffect = UnityFx.empCharge;
                }};
            }});
        }};

        waveform = new UnityUnitType("waveform"){{
            flying = true;
            lowAltitude = true;
            health = 4500f;
            speed = 0.9f;
            accel = 0.09f;
            drag = 0.07f;
            hitSize = 41.5f;
            engineOffset = 24.25f;
            ammoType = new PowerAmmoType(2000);

            CloneableSetWeapon t = UnityWeaponTemplates.waveformSmallMount;

            weapons.addAll(t.set(w -> {
                w.x = 15.75f;
                w.y = 4f;
                w.reload *= 4f;
                w.otherSide = 1;
            }), t.set(w -> {
                w.x = -15.75f;
                w.y = 4f;
                w.reload *= 4f;
                w.flipSprite = true;
                w.otherSide = 2;
            }), t.set(w -> {
                w.x = -19.25f;
                w.y = -15.25f;
                w.reload *= 4f;
                w.flipSprite = true;
                w.otherSide = 3;
            }), t.set(w -> {
                w.x = 19.25f;
                w.y = -15.25f;
                w.reload *= 4f;
                w.flipSprite = true;
                w.otherSide = 0;
                w.name = "unity-emp-small-mount-flipped";
            }), new Weapon("unity-emp-launcher"){{
                x = 10f;
                y = -8.5f;
                reload = 4f * 60f;
                mirror = true;
                rotate = true;
                rotateSpeed = 3f;
                shootY = 5f;
                shootSound = UnitySounds.zbosonShoot;

                bullet = new EmpBasicBulletType(6.8f, 8f){{
                    hitSize = 6f;
                    splashDamageRadius = 30f;
                    splashDamage = 14f;
                    shrinkY = 0f;
                    height = 18f;
                    width = 14f;

                    empRange = 160f;
                    empDuration = 60f * 2f;
                    empMaxRange = 800f;
                    empDisconnectRange = 100f;
                    empBatteryDamage = 30000f;
                    empLogicDamage = 12f;
                    powerGridIteration = 15;
                    trailLength = 15;
                    trailWidth = (width / 2f) / 2f;

                    hitEffect = Fx.hitLancer;
                    trailColor = backColor = lightColor = hitColor = Pal.lancerLaser;
                    frontColor = Color.white;
                }};
            }});
        }};

        ultraviolet = new UnityUnitType("ultraviolet"){{
            flying = true;
            lowAltitude = true;
            health = 12000f;
            speed = 0.53f;
            accel = 0.06f;
            drag = 0.07f;
            hitSize = 57.5f;
            engineOffset = 33.75f;
            engineSize = 3.5f;
            ammoType = new PowerAmmoType(2000);

            CloneableSetWeapon t = UnityWeaponTemplates.ultravioletMount;

            weapons.addAll(t.set(w -> {
                w.x = 13.25f;
                w.y = 20.25f;
                w.otherSide = 2;
            }), t.set(w -> {
                w.x = -13.25f;
                w.y = 20.25f;
                w.flipSprite = true;
                w.otherSide = 0;
            }), t.flp(w -> {
                w.x = 19.75f;
                w.y = 12f;
                w.otherSide = 4;
            }), t.set(w -> {
                w.x = -19.75f;
                w.y = 12f;
                w.flipSprite = true;
                w.otherSide = 1;
            }), t.flp(w -> {
                w.x = 25.25f;
                w.y = 0f;
                w.otherSide = 6;
            }), t.set(w -> {
                w.x = -25.25f;
                w.y = 0f;
                w.flipSprite = true;
                w.otherSide = 3;
            }), t.flp(w -> {
                w.x = 22.75f;
                w.y = -12f;
                w.otherSide = 8;
            }), t.set(w -> {
                w.x = -22.75f;
                w.y = -12f;
                w.flipSprite = true;
                w.otherSide = 5;
            }), t.flp(w -> {
                w.x = 16f;
                w.y = -19.5f;
                w.otherSide = 9;
            }), t.set(w -> {
                w.x = -16f;
                w.y = -19.5f;
                w.flipSprite = true;
                w.otherSide = 7;
            }), new Weapon("unity-emp-large-launcher"){{
                x = 0f;
                y = -20.25f;
                shootY = 11f;
                mirror = false;
                rotate = true;
                rotateSpeed = 2f;
                reload = 4f * 60f;
                shootSound = UnitySounds.zbosonShoot;

                bullet = new EmpBasicBulletType(6.8f, 9f){{
                    lifetime = 42f;
                    hitSize = 6f;
                    splashDamageRadius = 45f;
                    splashDamage = 23f;
                    shrinkY = 0f;
                    height = 19f;
                    width = 14.5f;

                    empRange = 175f;
                    empDuration = 60f * 2f;
                    empMaxRange = 800f;
                    empDisconnectRange = 125f;
                    empBatteryDamage = 40000f;
                    empLogicDamage = 26f;
                    powerGridIteration = 15;
                    trailLength = 15;
                    trailWidth = (width / 2f) / 2f;

                    hitEffect = Fx.hitLancer;
                    trailColor = backColor = lightColor = hitColor = Pal.lancerLaser;
                    frontColor = Color.white;
                }};
            }});
        }};

        //endregion
        //region T6/7

        citadel = new UnityUnitType("citadel"){{
            speed = 0.3f;
            hitSize = 49f;
            rotateSpeed = 1.5f;
            health = 48750f;
            armor = 15f;
            mechStepParticles = true;
            mechStepShake = 0.8f;
            canDrown = false;
            mechFrontSway = 2f;
            mechSideSway = 0.7f;
            mechStride = (4f + (hitSize - 8f) / 2.1f) / 1.25f;
            immunities.add(StatusEffects.burning);

            weapons.add(new Weapon(name + "-weapon"){{
                top = false;
                x = 31.5f;
                y = -6.25f;
                shootY = 30.25f;
                reload = 90f;
                recoil = 7f;
                shake = 3f;
                ejectEffect = Fx.casing4;
                shootSound = Sounds.railgun;

                bullet = new SlowRailBulletType(25f, 250f){{
                    lifetime = 13f;
                    trailSpacing = 25f;
                    splashDamage = 95f;
                    splashDamageRadius = 50f;
                    hitEffect = Fx.hitBulletBig;
                    shootEffect = Fx.instShoot;
                    trailEffect = TrailFx.coloredRailgunSmallTrail;
                    width = 9f;
                    height = 17f;
                    shrinkY = 0f;
                    shrinkX = 0f;
                    pierceCap = 7;
                    backColor = hitColor = trailColor = Pal.bulletYellowBack;
                    frontColor = Color.white;
                }};
            }}, new LimitedAngleWeapon(name + "-flamethrower"){{
                x = 17.75f;
                y = 11.25f;
                shootY = 5.5f;
                reload = 5f;
                recoil = 0.5f;
                shootSound = Sounds.flame;
                angleCone = 80f;
                rotate = true;

                bullet = UnityBullets.citadelFlame;
            }}, new LimitedAngleWeapon(name + "-flamethrower"){{
                x = 14f;
                y = -9f;
                shootY = 5.5f;
                reload = 4f;
                recoil = 0.5f;
                shootSound = Sounds.flame;
                angleCone = 80f;
                rotate = true;

                bullet = UnityBullets.citadelFlame;
            }});
        }};

        empire = new UnityUnitType("empire"){{
            speed = 0.25f;
            hitSize = 49f;
            rotateSpeed = 1.25f;
            health = 65000f;
            armor = 16f;
            mechStepParticles = true;
            mechStepShake = 0.83f;
            canDrown = false;
            mechFrontSway = 4f;
            mechSideSway = 0.7f;
            mechStride = (4f + (hitSize - 8f) / 2.1f) / 1.3f;
            immunities.addAll(StatusEffects.burning, StatusEffects.melting);

            weapons.add(new LimitedAngleWeapon(name + "-weapon"){{
                bottomWeapons.add(this);

                x = 36.5f;
                y = 2.75f;
                shootY = 19.25f;
                xRand = 4.5f;
                alternate = false;
                rotate = true;
                rotateSpeed = 1.2f;
                inaccuracy = 4f;
                reload = 3f;
                shots = 2;
                angleCone = 20f;
                angleOffset = -15f;
                shootCone = 20f;
                shootSound = Sounds.flame;
                cooldownTime = 180f;

                bullet = new FlameBulletType(6.6f, 63f){{
                    lifetime = 33f;
                    pierceCap = 6;
                    pierceBuilding = true;
                    collidesAir = true;
                    incendChance = 0.2f;
                    incendAmount = 1;
                    particleAmount = 23;
                    particleSizeScl = 8f;
                    particleSpread = 11f;
                    hitSize = 9f;
                    status = StatusEffects.melting;
                    smokeColors = new Color[]{Pal.darkFlame, Color.darkGray, Color.gray};
                    colors = new Color[]{Color.white, Color.valueOf("fff4ac"), Pal.lightFlame, Pal.darkFlame, Color.gray};
                }};
            }}, new Weapon(name + "-cannon"){{
                x = 20.75f;
                y = -4f;
                shootY = 9.75f;
                rotate = true;
                rotateSpeed = 4f;
                inaccuracy = 10f;
                shots = 8;
                velocityRnd = 0.2f;
                shootSound = Sounds.artillery;
                reload = 40f;

                bullet = new ArtilleryBulletType(3f, 15, "shell"){{
                    hitEffect = Fx.blastExplosion;
                    knockback = 0.8f;
                    lifetime = 110f;
                    width = height = 14f;
                    collides = true;
                    collidesTiles = true;
                    splashDamageRadius = 45f;
                    splashDamage = 95f;
                    backColor = Pal.bulletYellowBack;
                    frontColor = Pal.bulletYellow;
                }};
            }});
        }};

        cygnus = new UnityUnitType("cygnus"){{
            speed = 0.26f;
            health = 26250;
            hitSize = 37f;
            armor = 10f;
            landShake = 1.5f;
            commandLimit = 8;
            rotateSpeed = 1.3f;

            legCount = 6;
            legLength = 29f;
            legBaseOffset = 8f;
            legMoveSpace = 0.7f;
            legTrns = 0.6f;
            hovering = true;
            visualElevation = 0.23f;
            allowLegStep = true;
            ammoType = new PowerAmmoType(2000);
            groundLayer = Layer.legUnit;

            weapons.add(new Weapon(){{
                x = 0f;
                y = 8.25f;
                mirror = false;
                reload = 4f * 60f;
                recoil = 0f;
                shootStatus = StatusEffects.slow;
                shootStatusDuration = 80f;

                BulletType t = new CygnusBulletType(){{
                    float rad = 170f;

                    damage = 45f;
                    speed = 4.3f;
                    lifetime = 75f;
                    shootEffect = Fx.hitEmpSpark;
                    smokeEffect = Fx.shootBigSmoke2;
                    healPercent = 12f;
                    trailLength = 35;
                    trailWidth = 9f;
                    clipSize = (size + trailLength * speed) * 2.5f;
                    scaleVelocity = true;
                    unitDamageScl = 0.7f;
                    hitShake = 7f;
                    splashDamage = 95f;
                    splashDamageRadius = rad;
                    radius = rad;
                    timeIncrease = 1.75f;
                    timeDuration = 60f * 20f;
                    powerDamageScl = 3f;
                    allyStatusDuration = 60f * 15f;
                    status = StatusEffects.electrified;
                    lightning = 6;
                    lightningLength = 10;
                    lightningLengthRand = 4;
                    lightningDamage = 35f;
                    backColor = trailColor = lightningColor = Pal.heal;
                    frontColor = Color.white;

                    hitEffect = new Effect(50f, 100f, e -> {
                        e.scaled(7f, b -> {
                            Draw.color(Pal.heal, b.fout());
                            Fill.circle(e.x, e.y, rad);
                        });

                        Draw.color(Pal.heal);
                        Lines.stroke(e.fout() * 3f);
                        Lines.circle(e.x, e.y, rad);

                        int points = 10;
                        float offset = Mathf.randomSeed(e.id, 360f);
                        for(int i = 0; i < points; i++){
                            float angle = i* 360f / points + offset;
                            Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 6f, 50f * e.fout(), angle);
                        }

                        Fill.circle(e.x, e.y, 12f * e.fout());
                        Draw.color();
                        Fill.circle(e.x, e.y, 6f * e.fout());
                        Drawf.light(e.x, e.y, rad * 1.6f, Pal.heal, e.fout());
                    });
                }};

                bullet = new MultiBulletType(){{
                    lifetime = ChargeFx.greenLaserChargeParent.lifetime;
                    shootEffect = ChargeFx.greenLaserChargeParent;
                    mirror = false;
                    bullets = new MultiBulletData[]{
                        new MultiBulletData(t, 0f, 0f, 0f)
                    };
                }};
            }}, new Weapon(name + "-mount"){{
                x = 22.5f;
                y = -3f;
                shootY = 8.75f;
                rotate = true;
                alternate = false;
                rotateSpeed = 2f;
                reload = 140f;
                continuous = true;
                chargeSound = Sounds.lasercharge2;
                shootSound = Sounds.beam;
                firstShotDelay = ChargeFx.greenLaserChargeSmallParent.lifetime - 1f;
                cooldownTime = 130f;

                bullet = new ContinuousLaserBulletType(){{
                    damage = 45f;
                    length = 150f;
                    width = 5f;
                    hitEffect = Fx.hitMeltHeal;
                    drawSize = 420f;
                    lifetime = 140f;
                    shake = 1f;
                    despawnEffect = Fx.smokeCloud;
                    smokeEffect = Fx.none;

                    shootEffect = ChargeFx.greenLaserChargeSmallParent;

                    incendChance = 0.1f;
                    incendSpread = 5f;
                    incendAmount = 1;

                    healPercent = 1.2f;
                    collidesTeam = true;
                    largeHit = false;

                    colors = new Color[]{Pal.heal.cpy().a(0.2f), Pal.heal.cpy().a(0.5f), Pal.heal.cpy().mul(1.2f), Color.white};
                }};
            }});
        }};

        sagittarius = new UnityUnitType("sagittarius"){{
            speed = 0.2f;
            health = 43750;
            hitSize = 55f;
            armor = 12f;
            landShake = 2f;
            commandLimit = 8;
            rotateSpeed = 1.2f;

            legCount = 4;
            legLength = 34.36f;
            legBaseOffset = 11f;
            legMoveSpace = 0.7f;
            legTrns = 0.6f;
            hovering = true;
            visualElevation = 0.23f;
            allowLegStep = true;
            ammoType = new PowerAmmoType(2000);
            groundLayer = Layer.legUnit;
            drawShields = false;

            abilities.add(new ForceFieldAbility(130f, 3f, 3500f, 60f * 7));

            weapons.add(new AcceleratingWeapon(name + "-mount"){{
                x = 28.25f;
                y = -9.25f;
                shootY = 17f;
                reload = 30f;
                accelCooldownWaitTime = 31f;
                minReload = 5f;
                accelPerShot = 0.5f;
                rotateSpeed = 5f;
                inaccuracy = 5f;
                rotate = true;
                alternate = false;
                shots = 2;
                bullet = new ArrowBulletType(7f, 25f){{
                    lifetime = 60f;
                    pierce = true;
                    pierceBuilding = true;
                    pierceCap = 4;
                    backColor = trailColor = hitColor = lightColor = lightningColor = Pal.heal;
                    frontColor = Color.white;
                    trailWidth = 4f;
                    width = 9f;
                    height = 15f;
                    splashDamage = 15f;
                    splashDamageRadius = 25f;
                    healPercent = 3f;
                    homingRange = 70f;
                    homingPower = 0.05f;
                }};
            }});
        }};

        araneidae = new UnityUnitType("araneidae"){{
            groundLayer = Layer.legUnit + 0.01f;
            drag = 0.1f;
            speed = 0.42f;
            hitSize = 35.5f;
            health = 30000;
            rotateSpeed = 1.3f;

            legCount = 8;
            legMoveSpace = 0.76f;
            legPairOffset = 0.7f;
            legGroupSize = 2;
            legLength = 112f;
            legExtension = -8.25f;
            legBaseOffset = 8f;
            landShake = 2.4f;
            legLengthScl = 1f;
            rippleScale = 2f;
            legSpeed = 0.2f;

            legSplashDamage = 80f;
            legSplashRange = 40f;
            hovering = true;

            armor = 13f;
            allowLegStep = true;
            visualElevation = 0.95f;

            weapons.add(new Weapon("unity-araneidae-mount"){{
                x = 15f;
                y = -1.75f;
                shootY = 7.5f;
                reload = 30f;
                shake = 4f;
                rotateSpeed = 2f;
                rotate = true;
                shadow = 15f;
                shots = 3;
                spacing = 15f;
                shootSound = Sounds.laser;

                bullet = UnityBullets.sapLaser;
            }}, new MultiBarrelWeapon("unity-araneidae-cannon"){{
                mirror = false;
                x = 0f;
                y = -12.25f;
                shootY = 22f;
                reload = 120f;
                shake = 10f;
                recoil = 3f;
                rotateSpeed = 1f;
                ejectEffect = Fx.none;
                shootSound = Sounds.railgun;
                rotate = true;
                shadow = 40f;
                barrelSpacing = 11.25f;
                barrelOffset = 8.5f;
                barrelRecoil = 5f;
                barrels = 2;

                bullet = new SlowRailBulletType(15f, 95f){{
                    lifetime = 20f;
                    splashDamageRadius = 90f;
                    splashDamage = 90f;
                    hitEffect = Fx.sapExplosion;
                    ammoMultiplier = 4f;
                    trailEffect = TrailFx.coloredRailgunSmallTrail;
                    trailSpacing = 15f;
                    backColor = trailColor = Pal.sapBulletBack;
                    frontColor = lightningColor = Pal.sapBullet;
                    lightning = 3;
                    lightningLength = 20;
                    smokeEffect = Fx.shootBigSmoke2;
                    hitShake = 10f;
                    lightRadius = 40f;
                    lightColor = Pal.sap;
                    lightOpacity = 0.6f;
                    width = 12f;
                    height = 23f;
                    shrinkY = 0f;
                    collidesAir = false;
                    scaleVelocity = true;
                    pierceCap = 2;

                    status = StatusEffects.sapped;
                    statusDuration = 60f * 10;

                    fragLifeMin = 0.3f;
                    fragBullets = 4;

                    fragBullet = UnityBullets.sapArtilleryFrag;
                }};
            }});
        }};

        theraphosidae = new UnityUnitType("theraphosidae"){{
            speed = 0.4f;
            drag = 0.12f;
            hitSize = 49f;
            hovering = true;
            allowLegStep = true;
            health = 38750;
            armor = 16f;
            rotateSpeed = 1.3f;
            legCount = 8;
            legGroupSize = 2;
            legMoveSpace = 0.7f;
            legPairOffset = 0.2f;
            legLength = 176f;
            legExtension = -24f;
            legBaseOffset = 9f;
            visualElevation = 1f;
            groundLayer = Layer.legUnit + 0.02f;
            rippleScale = 3.4f;
            legSplashDamage = 130f;
            legSplashRange = 60f;
            targetAir = false;
            commandLimit = 5;

            weapons.add(new LimitedAngleWeapon(name + "-launcher"){{
                bottomWeapons.add(this);

                x = 33f;
                y = 8.5f;
                shootY = 6.25f - 1f;
                reload = 7f;
                recoil = 1f;
                rotate = true;
                shootCone = 20f;
                angleCone = 60f;
                angleOffset = 45f;
                inaccuracy = 25f;
                xRand = 2.25f;
                shots = 2;
                shootSound = Sounds.missile;

                bullet = new MissileBulletType(3.7f, 15f){{
                    width = 10f;
                    height = 12f;
                    shrinkY = 0f;
                    drag = -0.01f;
                    splashDamageRadius = 30f;
                    splashDamage = 55f;
                    ammoMultiplier = 5f;
                    hitEffect = Fx.blastExplosion;
                    despawnEffect = Fx.blastExplosion;
                    backColor = trailColor = Pal.sapBulletBack;
                    frontColor = lightningColor = lightColor = Pal.sapBullet;
                    trailLength = 13;
                    homingRange = 80f;
                    weaveScale = 8f;
                    weaveMag = 2f;
                    lightning = 2;
                    lightningLength = 2;
                    lightningLengthRand = 1;
                    lightningCone = 15f;

                    status = StatusEffects.blasted;
                    statusDuration = 60f;
                }};
            }}, new LimitedAngleWeapon(name + "-mount"){{
                x = 26.75f;
                y = 7.5f;
                shootY = 10.25f - 5f;
                reload = 120f;
                angleCone = 60f;
                rotate = true;
                continuous = true;
                alternate = false;
                rotateSpeed = 1.5f;
                recoil = 5f;
                shootSound = UnitySounds.continuousLaserA;

                bullet = UnityBullets.continuousSapLaser;
            }}, new Weapon(name + "-railgun"){{
                x = 20.5f;
                y = -10f;
                shootY = 20.5f - 4f;
                shootSound = Sounds.railgun;
                rotate = true;
                alternate = true;
                rotateSpeed = 0.9f;
                cooldownTime = 90f;
                reload = 90f;
                shake = 6f;
                recoil = 8f;

                bullet = new SlowRailBulletType(15f, 95f){{
                    lifetime = 23f;
                    splashDamageRadius = 110f;
                    splashDamage = 90f;
                    hitEffect = Fx.sapExplosion;
                    ammoMultiplier = 4f;
                    trailEffect = TrailFx.coloredRailgunSmallTrail;
                    trailSpacing = 15f;
                    backColor = trailColor = Pal.sapBulletBack;
                    frontColor = lightningColor = Pal.sapBullet;
                    lightning = 3;
                    lightningLength = 20;
                    smokeEffect = Fx.shootBigSmoke2;
                    hitShake = 10f;
                    lightRadius = 40f;
                    lightColor = Pal.sap;
                    lightOpacity = 0.6f;
                    width = 13f;
                    height = 27f;
                    shrinkY = 0f;
                    collidesAir = false;
                    scaleVelocity = true;
                    pierceCap = 3;

                    status = StatusEffects.sapped;
                    statusDuration = 60f * 10;

                    fragLifeMin = 0.3f;
                    fragBullets = 4;

                    fragBullet = UnityBullets.sapArtilleryFrag;
                }};
            }});
        }};

        mantle = new UnityUnitType("mantle"){{
            health = 41250f;
            armor = 17f;
            speed = 0.45f;
            accel = 0.04f;
            drag = 0.04f;
            rotateSpeed = 0.9f;
            flying = true;
            lowAltitude = true;
            destructibleWreck = false;
            targetFlags = new BlockFlag[]{BlockFlag.reactor, null};
            hitSize = 80f;
            engineOffset = 42.75f;
            engineSize = 5.75f;

            BulletType b = UnitTypes.scepter.weapons.get(0).bullet.copy();
            b.speed = 6.5f;
            b.damage = 60f;
            b.lifetime = 47f;

            weapons.add(new Weapon(){{
                x = 0f;
                y = 0f;
                shootY = 4f;
                mirror = false;
                reload = 4f * 60f;
                continuous = true;
                recoil = 0f;
                shootStatus = StatusEffects.slow;
                shootStatusDuration = 180f;

                bullet = new AcceleratingLaserBulletType(230f){{
                    lifetime = 180f;
                    maxLength = 380f;
                    maxRange = 330f;
                    oscOffset = 0.1f;
                    incendChance = 0.2f;
                    incendAmount = 2;
                    width = 27f;
                    collisionWidth = 10f;
                    pierceCap = 2;
                    hitEffect = HitFx.coloredHitLarge;
                    hitColor = Pal.meltdownHit;
                }};
            }}, new Weapon(name + "-mount"){{
                x = 30.75f;
                y = -6.25f;
                shootY = 10.5f;
                alternate = true;
                rotate = true;
                recoil = 5f;
                reload = 55f;
                shots = 4;
                shotDelay = 4f;
                rotateSpeed = 3f;
                shadow = 22f;

                bullet = b;
            }}, new Weapon(name + "-mount"){{
                x = 19f;
                y = -18f;
                shootY = 10.5f;
                alternate = true;
                rotate = true;
                recoil = 5f;
                reload = 60f;
                shots = 4;
                shotDelay = 4f;
                rotateSpeed = 3f;
                shadow = 22f;

                bullet = b;
            }});
        }};

        aphelion = new UnityUnitType("aphelion"){{
            health = 50000f;
            armor = 16f;
            speed = 0.44f;
            accel = 0.04f;
            drag = 0.03f;
            rotateSpeed = 0.7f;
            flying = true;
            lowAltitude = true;
            destructibleWreck = false;
            targetFlags = new BlockFlag[]{BlockFlag.reactor, null};
            hitSize = 96f;
            engineOffset = 46.5f;
            engineSize = 6.75f;

            BulletType b = UnitTypes.scepter.weapons.get(0).bullet.copy();
            b.speed = 6.5f;
            b.damage = 40f;
            b.lightning = 3;
            b.lightningDamage = 27f;
            b.lightningCone = 360f;
            b.lifetime = 50f;
            b.lightningLength = 14;
            b.lightningType = new BulletType(0f, 10f){
                {
                    lifetime = Fx.lightning.lifetime;
                    hitEffect = Fx.hitLancer;
                    despawnEffect = Fx.none;
                    status = StatusEffects.shocked;
                    statusDuration = 60f;
                    hittable = false;
                    lightningColor = b.lightningColor;
                    lightning = 1;
                    lightningCone = 65f;
                    lightningLength = 6;
                    lightningLengthRand = 3;
                }

                @Override
                public void init(Bullet b){
                    if(Mathf.chance(0.3f)) Lightning.create(b.team, lightningColor, damage, b.x, b.y, b.rotation() + Mathf.range(lightningCone), lightningLength + Mathf.random(lightningLengthRand));
                }

                @Override
                public void hit(Bullet b, float x, float y){

                }
            };

            weapons.add(new Weapon(name + "-laser"){{
                x = 0f;
                y = 0f;
                shootY = 34.25f;
                shootCone = 2f;
                mirror = false;
                reload = 7f * 60f;
                continuous = true;
                recoil = 0f;
                cooldownTime = 6f * 60f;

                bullet = new AcceleratingLaserBulletType(320f){{
                    lifetime = 4f * 60f;
                    maxLength = 430f;
                    maxRange = 400f;
                    oscOffset = 0.2f;
                    incendChance = 0.3f;
                    incendAmount = 2;
                    width = 37f;
                    collisionWidth = 16f;
                    accel = 60f;
                    laserSpeed = 20f;
                    splashDamage = 40f;
                    splashDamageRadius = 50f;
                    pierceCap = 5;
                    hitEffect = HitFx.coloredHitLarge;
                    hitColor = Pal.meltdownHit;
                }};

                shootStatus = StatusEffects.slow;
                shootStatusDuration = bullet.lifetime;
            }}, new Weapon(name + "-mount"){{
                x = 30f;
                y = -9.5f;
                shootY = 14.25f;
                shadow = 32f;
                rotate = true;
                rotateSpeed = 2f;
                reload = 2f;
                xRand = 3f;
                inaccuracy = 4f;

                bullet = b;
            }});
        }};

        sedec = new UnityUnitType("sedec"){{
            defaultController = HealingDefenderAI::new;
            health = 45000f;
            armor = 20f;
            speed = 0.7f;
            rotateSpeed = 1f;
            accel = 0.04f;
            drag = 0.018f;
            flying = true;
            engineOffset = 48f;
            engineSize = 7.8f;
            rotateShooting = false;
            hitSize = 85f;
            payloadCapacity = (6.2f * 6.2f) * tilePayload;
            buildSpeed = 5f;
            drawShields = false;
            commandLimit = 8;
            buildBeamOffset = 29.5f;

            //ammo resupply mechanics removed in 129 until further notice; TODO remove or rework
            //ammoCapacity = 1700;
            //ammoResupplyAmount = 30;

            abilities.add(new ForceFieldAbility(190f, 6f, 8000f, 60f * 12), new RepairFieldAbility(180f, 60f * 2, 160f));

            weapons.add(new Weapon(name + "-laser"){{
                bottomWeapons.add(this);

                x = 0f;
                y = 0f;
                shootY = 42f - 3f;
                reload = 260f;
                recoil = 3f;

                continuous = rotate = true;

                mirror = false;
                rotateSpeed = 1.5f;

                bullet = new HealingConeBulletType(3f){{
                    healPercent = 6f;
                    allyStatus = StatusEffects.overclock;
                    allyStatusDuration = 9f * 60f;
                    status = UnityStatusEffects.weaken;
                    statusDuration = 40f;
                    lifetime = 6f * 60f;
                }};
            }});
        }};

        trigintaduo = new UnityUnitType("trigintaduo"){{
            defaultController = HealingDefenderAI::new;
            health = 52500f;
            armor = 22f;
            speed = 0.6f;
            rotateSpeed = 1f;
            accel = 0.04f;
            drag = 0.018f;
            flying = true;
            engineOffset = 41.25f;
            engineSize = 6.5f;
            rotateShooting = false;
            hitSize = 105.5f;
            payloadCapacity = (8.1f * 8.1f) * tilePayload;
            buildSpeed = 6f;
            drawShields = false;
            commandLimit = 12;
            buildBeamOffset = 47.75f;

            //ammo resupply mechanics removed in 129 until further notice; TODO remove or rework
            //ammoCapacity = 2000;
            //ammoResupplyAmount = 35;

            weapons.add(new Weapon(name + "-heal-mount"){{
                x = 33.5f;
                y = -7.75f;
                shootY = 10.25f;
                reload = 220f;
                recoil = 3f;
                shadow = 22f;

                continuous = rotate = true;
                alternate = false;
                rotateSpeed = 3.5f;

                bullet = new HealingConeBulletType(3f){{
                    healPercent = 3f;
                    cone = 15f;
                    scanAccuracy = 25;
                    allyStatus = StatusEffects.overclock;
                    allyStatusDuration = 9f * 60f;
                    status = UnityStatusEffects.weaken;
                    statusDuration = 40f;
                    lifetime = 6f * 60f;
                }};
            }}, new EnergyChargeWeapon(""){{
                mirror = false;
                x = 0f;
                y = 10.75f;
                shootY = 0f;

                reload = 30f * 60f;
                shootCone = 360f;
                ignoreRotation = true;

                drawCharge = (unit, mount, charge) -> {
                    float rotation = unit.rotation - 90f,
                        wx = unit.x + Angles.trnsx(rotation, x, y),
                        wy = unit.y + Angles.trnsy(rotation, x, y);

                    Draw.color(Pal.heal);
                    UnityDrawf.shiningCircle(unit.id, Time.time, wx, wy, 13f * charge, 5, 70f, 15f, 6f * charge, 360f);
                    Draw.color(Color.white);
                    UnityDrawf.shiningCircle(unit.id, Time.time, wx, wy, 6.5f * charge, 5, 70f, 15f, 4f * charge, 360f);
                };

                bullet = new HealingNukeBulletType(){{
                    allyStatus = StatusEffects.overclock;
                    allyStatusDuration = 15f * 60f;
                    status = UnityStatusEffects.disabled;
                    statusDuration = 120f;
                    healPercent = 20f;
                }};
            }});
        }};

        //endregion
        //region naval-units

        fin = new UnityUnitType("fin"){{
            health = 36250f;
            speed = 0.5f;
            drag = 0.18f;
            hitSize = 77.5f;
            armor = 17f;
            accel = 0.19f;
            rotateSpeed = 0.86f;
            rotateShooting = false;

            trailLength = 70;
            trailX = 18f;
            trailY = -32f;
            trailScl = 3.5f;

            weapons.add(new Weapon(name + "-launcher"){{
                x = 19f;
                y = 14f;
                shootY = 8f;
                rotate = true;
                inaccuracy = 15f;
                reload = 7f;
                xRand = 2.25f;
                shootSound = Sounds.missile;

                bullet = UnityBullets.basicMissile;
            }}, new Weapon(name + "-launcher"){{
                x = 24.5f;
                y = -39.25f;
                shootY = 8f;
                rotate = true;
                inaccuracy = 15f;
                reload = 7f;
                xRand = 2.25f;
                shootSound = Sounds.missile;

                bullet = UnityBullets.basicMissile;
            }}, new MortarWeapon(name + "-mortar"){{
                x = 0f;
                y = -13.75f;
                shootY = 39.5f;
                mirror = false;
                rotate = true;
                rotateSpeed = 1f;
                shots = 3;
                inaccuracy = 3f;
                velocityRnd = 0.1f;
                reload = 60f * 2f;
                recoil = 2f;

                bullet = new MortarBulletType(7f, 4f){{
                    width = height = 22f;
                    splashDamageRadius = 160f;
                    splashDamage = 160f;
                    trailWidth = 7f;
                    trailColor = Pal.bulletYellowBack;
                    hitEffect = HitFx.hitExplosionMassive;
                    lifetime = 65f;
                    fragBullet = Bullets.artilleryDense;
                    fragBullets = 7;
                    fragLifeMax = 0.15f;
                    fragLifeMin = 0.15f;
                    despawnHit = true;
                    collidesAir = false;
                }};
            }});
        }};

        blue = new UnityUnitType("blue"){{
            health = 42500f;
            speed = 0.4f;
            drag = 0.18f;
            hitSize = 80f;
            armor = 18f;
            accel = 0.19f;
            rotateSpeed = 0.78f;
            rotateShooting = false;

            trailLength = 70;
            trailX = 26f;
            trailY = -42f;
            trailScl = 4f;

            float spawnTime = 15f * 60f;

            abilities.add(new UnitSpawnAbility(schistocerca, spawnTime, 24.75f, -29.5f), new UnitSpawnAbility(schistocerca, spawnTime, -24.75f, -29.5f));

            weapons.addAll(new LimitedAngleWeapon(name + "-front-cannon"){{
                bottomWeapons.add(this);

                x = 22.25f;
                y = 30.25f;
                shootY = 9.5f;
                recoil = 5f;
                shots = 5;
                shotDelay = 3f;
                inaccuracy = 5f;
                shootCone = 15f;
                rotate = true;
                angleLimit = 3f;
                shootSound = Sounds.artillery;
                reload = 25f;

                bullet = Bullets.standardThoriumBig;
            }}, new LimitedAngleWeapon(name + "-side-silo"){
                {
                    bottomWeapons.add(this);

                    x = 29.75f;
                    y = -13f;
                    shootY = 7f;
                    xRand = 9f;
                    defaultAngle = angleOffset = 90f;
                    angleCone = 0f;
                    shootCone = 125f;
                    alternate = false;
                    rotate = true;
                    reload = 50f;
                    shots = 12;
                    shotDelay = 3f;
                    inaccuracy = 5f;
                    shootSound = Sounds.missile;

                    bullet = new GuidedMissileBulletType(3f, 20f){{
                        homingPower = 0.09f;
                        width = 8f;
                        height = 8f;
                        shrinkX = shrinkY = 0f;
                        drag = -0.003f;
                        keepVelocity = false;
                        splashDamageRadius = 40f;
                        splashDamage = 45f;
                        lifetime = 65f;
                        trailColor = Pal.missileYellowBack;
                        hitEffect = Fx.blastExplosion;
                        despawnEffect = Fx.blastExplosion;
                    }};
                }

                @Override
                protected Bullet bullet(Unit unit, float shootX, float shootY, float angle, float lifescl){
                    Bullet b = super.bullet(unit, shootX, shootY, angle, lifescl);
                    if(b.type instanceof GuidedMissileBulletType){
                        WeaponMount m = null;
                        for(WeaponMount mount : unit.mounts){
                            if(mount.weapon == this){
                                m = mount;
                                break;
                            }
                        }
                        if(m != null){
                            b.data = m;
                        }
                    }
                    return b;
                }
            }, new LimitedAngleWeapon(fin.name + "-launcher"){{
                x = 0f;
                y = 21f;
                shootY = 8f;
                rotate = true;
                mirror = false;
                inaccuracy = 15f;
                reload = 7f;
                xRand = 2.25f;
                shootSound = Sounds.missile;
                angleCone = 135f;

                bullet = UnityBullets.basicMissile;
            }}, new PointDefenceMultiBarrelWeapon(name + "-flak-turret"){{
                x = 26.5f;
                y = 15f;
                shootY = 15.75f;
                barrels = 2;
                barrelOffset = 5.25f;
                barrelSpacing = 6.5f;
                barrelRecoil = 4f;
                rotate = true;
                mirrorBarrels = true;
                alternate = false;
                reload = 6f;
                recoil = 0.5f;
                shootCone = 7f;
                shadow = 30f;
                targetInterval = 20f;
                autoTarget = true;
                controllable = false;
                bullet = new AntiBulletFlakBulletType(8f, 6f){{
                    lifetime = 45f;
                    splashDamage = 12f;
                    splashDamageRadius = 60f;
                    bulletRadius = 60f;
                    explodeRange = 45f;
                    bulletDamage = 18f;
                    width = 8f;
                    height = 12f;
                    scaleVelocity = true;
                    collidesGround = false;
                    status = StatusEffects.blasted;
                    statusDuration = 60f;
                }};
            }}, new Weapon(name + "-railgun"){{
                x = 0f;
                y = 0f;
                shootY = 38.5f;
                mirror = false;
                rotate = true;
                rotateSpeed = 0.7f;
                shadow = 46f;
                reload = 60f * 2.5f;
                shootSound = Sounds.railgun;

                bullet = new SlowRailBulletType(70f, 2100f){{
                    lifetime = 10f;
                    width = 20f;
                    height = 38f;
                    splashDamage = 50f;
                    splashDamageRadius = 30f;
                    pierceDamageFactor = 0.15f;
                    pierceCap = -1;
                    fragBullet = Bullets.standardDense;
                    fragBullets = 2;
                    fragCone = 20f;
                    fragLifeMin = 0.4f;
                    fragLifeMax = 0.7f;
                    trailSpacing = 40f;
                    trailEffect = TrailFx.coloredArrowTrail;
                    backColor = trailColor = Pal.bulletYellowBack;
                    frontColor = Pal.bulletYellow;
                    collisionWidth = 12f;
                }};
            }});
        }};

        amphibiNaval = new UnityUnitType("amphibi-naval"){
            {
                toTrans = unit -> amphibi;
                speed = 1.3f;
                health = 365;
                engineSize = 5f;
                engineOffset = 12f;
                accel = 0.3f;
                baseRotateSpeed = 0.2f;
                rotateSpeed = 1.6f;
                hitSize = 12f;
                armor = 2f;
                immunities.add(StatusEffects.wet);
                trailX = 3f;
                trailY = -5f;
                trailLength = 13;
                trailScl = 1.75f;
                rotateShooting = true;
                transformTime = 10f;

                weapons.add(new Weapon("artillery"){{
                    reload = 35f;
                    x = 5.5f;
                    y = -4f;
                    shots = 2;
                    shotDelay = 2f;
                    inaccuracy = 5f;
                    rotate = true;
                    shake = 3f;
                    rotateSpeed = 4f;
                    bullet = new ArtilleryBulletType(2.1f, 1f){{
                        collidesTiles = true;
                        hitEffect = Fx.blastExplosion;
                        knockback = 0.8f;
                        speed = 2.1f;
                        lifetime = 80f;
                        width = height = 11f;
                        ammoMultiplier = 4f;
                        splashDamageRadius = 35f;
                        splashDamage = 25f;
                        backColor = UnityPal.navalReddish;
                        frontColor = lightningColor = UnityPal.navalYellowish;
                        smokeEffect = Fx.shootBigSmoke2;
                        shake = 4.5f;
                        statusDuration = 60 * 10f;
                    }};
                }});
            }

            @Override
            public boolean isHidden(){
                return true;
            }
        };

        amphibi = new UnityUnitType("amphibi"){{
            toTrans = unit -> amphibiNaval;
            speed = 0.3f;
            health = 365;
            armor = 1f;
            hitSize = 12f;
            hovering = true;
            allowLegStep = true;
            visualElevation = 0.5f;
            legCount = 6;
            legLength = 16f;
            legMoveSpace = 0.7f;
            legSpeed = 0.06f;
            legPairOffset = 0.9f;
            legGroupSize = 4;
            legBaseOffset = 0f;
            legExtension = -3f;
            kinematicScl = 0.6f;
            groundLayer = 65f;
            rippleScale = 1f;
            transformTime = 10f;

            weapons.add(amphibiNaval.weapons.get(0));
        }};

        craberNaval = new UnityUnitType("craber-naval"){
            {
                toTrans = unit -> craber;
                speed = 1.2f;
                health = 730;
                engineSize = 5f;
                engineOffset = 12f;
                accel = 0.26f;
                baseRotateSpeed = 1.6f;
                hitSize = 16f;
                armor = 2f;
                immunities.add(StatusEffects.wet);
                trailX = 3f;
                trailY = -9f;
                trailLength = 16;
                trailScl = 1.85f;
                rotateShooting = true;
                transformTime = 30f;

                weapons.add(new Weapon("unity-laser-weapon"){{
                    reload = 5f;
                    x = 6f;
                    y = -3f;
                    rotate = true;
                    shake = 1f;
                    rotateSpeed = 6f;
                    bullet = new SapBulletType(){{
                        sapStrength = 0f;
                        color = Color.white.cpy().lerp(Pal.lancerLaser, 0.5f);
                        damage = 35f;
                        lifetime = 22f;
                        status = StatusEffects.shocked;
                        statusDuration = 60f * 5f;
                        width = 0.7f;
                        length = 170f;
                    }};
                }});
            }

            @Override
            public boolean isHidden(){
                return true;
            }
        };

        craber = new UnityUnitType("craber"){{
            toTrans = unit -> craberNaval;
            speed = 0.3f;
            health = 730;
            armor = 10f;
            hitSize = 16f;
            hovering = true;
            allowLegStep = true;
            visualElevation = 0.5f;
            legCount = 6;
            legLength = 18f;
            legMoveSpace = 0.7f;
            legSpeed = 0.06f;
            legPairOffset = 0.9f;
            legGroupSize = 4;
            legBaseOffset = 0f;
            legExtension = -3f;
            kinematicScl = 0.7f;
            groundLayer = 65f;
            rippleScale = 1f;
            transformTime = 30f;

            weapons.add(craberNaval.weapons.get(0));
        }};

        //endregion
        //region scar

        hovos = new UnityUnitType("hovos"){{
            defaultController = DistanceGroundAI::new;
            speed = 0.8f;
            health = 340;
            hitSize = 7.75f * 1.7f;
            range = 350f;
            allowLegStep = true;
            legMoveSpace = 0.7f;
            legTrns = 0.4f;
            legLength = 30f;
            legExtension = -4.3f;

            weapons.add(new Weapon("unity-small-scar-railgun"){{
                reload = 60f * 2;
                x = 0f;
                y = -2f;
                shootY = 9f;
                mirror = false;
                rotate = true;
                shake = 2.3f;
                rotateSpeed = 2f;

                bullet = new RailBulletType(){{
                    damage = 500f;
                    //speed = 59f;
                    //lifetime = 8f;
                    length = 59f * 6f;
                    updateEffectSeg = 59f;
                    shootEffect = ShootFx.scarRailShoot;
                    pierceEffect = HitFx.scarRailHit;
                    updateEffect = UnityFx.scarRailTrail;
                    hitEffect = Fx.massiveExplosion;
                    pierceDamageFactor = 0.3f;
                }};
            }});
        }};

        ryzer = new UnityUnitType("ryzer"){{
            defaultController = DistanceGroundAI::new;
            speed = 0.7f;
            health = 640;
            hitSize = 9.5f * 1.7f;
            range = 350f;
            allowLegStep = true;
            legMoveSpace = 0.73f;
            legCount = 6;
            legTrns = 0.4f;
            legLength = 32f;
            legExtension = -4.3f;

            weapons.add(new Weapon(){{
                reload = 2.5f * 60f;
                x = 0f;
                y = 7.5f;
                shootY = 2f;
                mirror = false;
                shake = 2.3f;

                bullet = new RailBulletType(){{
                    damage = 700f;
                    //speed = 59f;
                    //lifetime = 8f;
                    length = 59f * 7f;
                    updateEffectSeg = 59f;
                    shootEffect = ShootFx.scarRailShoot;
                    pierceEffect = HitFx.scarRailHit;
                    updateEffect = UnityFx.scarRailTrail;
                    hitEffect = Fx.massiveExplosion;
                    pierceDamageFactor = 0.3f;
                }};
            }}, new Weapon("unity-scar-missile-launcher"){{
                reload = 50f;
                x = 6.25f;
                shots = 5;
                shotDelay = 3f;
                inaccuracy = 4f;
                rotate = true;
                bullet = new MissileBulletType(5f, 1f){{
                    speed = 5f;
                    width = 7f;
                    height = 12f;
                    shrinkY = 0f;
                    backColor = trailColor = UnityPal.scarColor;
                    frontColor = UnityPal.endColor;
                    splashDamage = 25f;
                    splashDamageRadius = 20f;
                    weaveMag = 3f;
                    weaveScale = 4f;
                }};
            }});
        }};

        zena = new UnityUnitType("zena"){{
            defaultController = DistanceGroundAI::new;
            speed = 0.7f;
            health = 1220;
            hitSize = 17.85f;
            range = 350f;
            allowLegStep = true;
            legMoveSpace = 0.73f;
            legCount = 6;
            legTrns = 0.4f;
            legLength = 40f;
            legExtension = -9.3f;

            weapons.add(
                new Weapon(){{
                    x = 0f;
                    y = 12f;
                    shootY = 0f;
                    mirror = false;
                    rotate = false;
                    shake = 2.3f;
                    reload = 2.75f * 60f;

                    bullet = new RailBulletType(){{
                        damage = 780f;
                        length = 60f * 7f;
                        updateEffectSeg = 60f;
                        shootEffect = ShootFx.scarRailShoot;
                        pierceEffect = HitFx.scarRailHit;
                        updateEffect = UnityFx.scarRailTrail;
                        hitEffect = Fx.massiveExplosion;
                        pierceDamageFactor = 0.2f;
                    }};
                }}, new Weapon(){{
                    x = 10.25f;
                    y = 2f;
                    rotate = false;
                    shake = 1.1f;
                    reload = 2.25f * 70f;

                    bullet = new RailBulletType(){{
                        damage = 230f;
                        length = 40f * 7f;
                        updateEffectSeg = 40f;
                        shootEffect = ShootFx.scarRailShoot;
                        pierceEffect = HitFx.scarRailHit;
                        updateEffect = UnityFx.scarRailTrail;
                        hitEffect = Fx.massiveExplosion;
                        pierceDamageFactor = 0.5f;
                    }};
                }}, new Weapon("unity-scar-missile-launcher"){{
                    x = 12.25f;
                    y = -5f;
                    rotate = true;
                    shots = 5;
                    shotDelay = 3f;
                    inaccuracy = 4f;
                    reload = 50f;
                    bullet = new MissileBulletType(5f, 0f){{
                        width = 7f;
                        height = 12f;
                        shrinkY = 0f;
                        backColor = trailColor = UnityPal.scarColor;
                        frontColor = UnityPal.endColor;
                        splashDamage = 30f;
                        splashDamageRadius = 20f;
                        weaveMag = 3f;
                        weaveScale = 4f;
                    }};
                }}
            );
        }};

        sundown = new UnityUnitType("sundown"){{
            defaultController = DistanceGroundAI::new;
            speed = 0.6f;
            health = 9400;
            hitSize = 36f;
            range = 360f;
            allowLegStep = true;
            legMoveSpace = 0.53f;
            rotateSpeed = 2.5f;
            armor = 4f;
            legCount = 4;
            legTrns = 0.4f;
            legLength = 44f;
            legExtension = -9.3f;
            legSplashDamage = 20f;
            legSplashRange = 30f;

            groundLayer = Layer.legUnit;
            visualElevation = 0.65f;

            weapons.add(new Weapon("unity-scar-large-launcher"){{
                x = 13.5f;
                y = -6.5f;
                shootY = 5f;
                shadow = 8f;
                rotateSpeed = 5f;
                rotate = true;
                reload = 80f;
                shake = 1f;
                shots = 12;
                inaccuracy = 19f;
                velocityRnd = 0.2f;
                xRand = 1.2f;
                shootSound = Sounds.missile;

                bullet = UnityBullets.scarMissile;
            }}, new Weapon("unity-scar-railgun"){{
                x = 7f;
                y = -9.25f;
                shootY = 10.75f;
                rotateSpeed = 2f;
                rotate = true;
                shadow = 12f;
                reload = 60f * 2.7f;
                shootSound = Sounds.artillery;

                bullet = new RailBulletType(){{
                    damage = 880f;
                    length = 61f * 7f;
                    updateEffectSeg = 61f;
                    shootEffect = ShootFx.scarRailShoot;
                    pierceEffect = HitFx.scarRailHit;
                    updateEffect = UnityFx.scarRailTrail;
                    hitEffect = Fx.massiveExplosion;
                    pierceDamageFactor = 0.2f;
                }};
            }});

            DirectionShieldAbility shield = new DirectionShieldAbility(4, 0.1f, 20f, 1600f, 2.3f, 1.3f, 32.2f);
            shield.healthBarColor = UnityPal.endColor;

            abilities.add(shield);
        }};

        rex = new UnityUnitType("rex"){{
            defaultController = DistanceGroundAI::new;
            speed = 0.55f;
            health = 23000;
            hitSize = 47.5f;
            range = 390f;
            allowLegStep = true;
            rotateSpeed = 2f;
            armor = 12f;

            hovering = true;
            groundLayer = Layer.legUnit + 0.01f;
            visualElevation = 0.95f;

            legCount = 4;
            legTrns = 1f;
            legLength = 56f;
            legExtension = -9.5f;
            legSplashDamage = 90f;
            legSplashRange = 65f;
            legSpeed = 0.08f;
            legMoveSpace = 0.57f;
            legPairOffset = 0.8f;

            weapons.add(new Weapon(name + "-railgun"){{
                x = 31.25f;
                y = -12.25f;
                shootY = 23.25f;
                rotate = false;
                top = false;
                reload = 60f * 4.5f;
                recoil = 4f;
                shootSound = Sounds.artillery;

                bullet = new RailBulletType(){
                    {
                        damage = 3300f;
                        buildingDamageMultiplier = 0.5f;
                        length = 61f * 8f;
                        updateEffectSeg = 61f;
                        shootEffect = ShootFx.scarRailShoot;
                        pierceEffect = HitFx.scarRailHit;
                        updateEffect = UnityFx.scarRailTrail;
                        hitEffect = Fx.massiveExplosion;
                        pierceDamageFactor = 0.35f;
                    }
                    @Override
                    public void init(Bullet b){
                        //super.init(b);
                        b.fdata = length;
                        Damage.collideLine(b, b.team, b.type.hitEffect, b.x, b.y, b.rotation(), length, true);
                        float resultLen = b.fdata;

                        Vec2 nor = Tmp.v1.set(b.vel).nor();
                        for(float i = 0; i <= resultLen; i += updateEffectSeg){
                            updateEffect.at(b.x + nor.x * i, b.y + nor.y * i, b.rotation());
                        }
                    }
                };
            }}, new Weapon("unity-scar-large-launcher"){{
                x = 12.25f;
                y = 13f;
                shootY = 5f;
                xRand = 2.2f;
                shadow = 8f;
                rotateSpeed = 5f;
                rotate = true;
                reload = 4f;
                inaccuracy = 5f;

                bullet = new BasicBulletType(6f, 12f){{
                    lifetime = 35f;
                    width = 7f;
                    height = 12f;
                    pierce = true;
                    pierceBuilding = true;
                    pierceCap = 2;
                }};
            }}, new Weapon("unity-scar-large-launcher"){{
                x = 15.75f;
                y = -17.5f;
                shootY = 5f;
                shadow = 8f;
                rotateSpeed = 5f;
                rotate = true;
                reload = 85f;
                shake = 1f;
                shots = 9;
                inaccuracy = 19f;
                velocityRnd = 0.2f;
                xRand = 1.2f;
                shootSound = Sounds.missile;

                bullet = UnityBullets.scarMissile;
            }}, new Weapon("unity-scar-large-launcher"){{
                x = 9.25f;
                y = -13.75f;
                shootY = 5f;
                shadow = 8f;
                rotateSpeed = 5f;
                rotate = true;
                reload = 90f;
                shake = 1f;
                shots = 9;
                inaccuracy = 19f;
                velocityRnd = 0.2f;
                xRand = 1.2f;
                shootSound = Sounds.missile;

                bullet = UnityBullets.scarMissile;
            }});

            DirectionShieldAbility shield = new DirectionShieldAbility(3, 0.06f, 45f, 3100f, 3.3f, 0.9f, 49f);
            shield.healthBarColor = UnityPal.endColor;

            abilities.add(shield);
        }};

        excelsus = new UnityUnitType("excelsus"){{
            defaultController = DistanceGroundAI::new;
            speed = 0.6f;
            health = 38000;
            hitSize = 66.5f;
            range = 370f;
            allowLegStep = true;
            rotateSpeed = 1.4f;
            armor = 18f;
            customBackLegs = true;

            hovering = true;
            groundLayer = Layer.legUnit + 0.03f;
            visualElevation = 1.1f;

            legCount = 6;
            legTrns = 1f;
            legLength = 62f;
            legExtension = -9.5f;
            legSplashDamage = 120f;
            legSplashRange = 85f;
            legSpeed = 0.06f;
            legMoveSpace = 0.57f;
            legPairOffset = 0.8f;
            kinematicScl = 0.7f;

            immunities = ObjectSet.with(StatusEffects.burning);

            weapons.add(new Weapon("unity-scar-large-launcher"){{
                x = 8.25f;
                y = -18.5f;
                shootY = 5f;
                shadow = 8f;
                rotateSpeed = 5f;
                rotate = true;
                reload = 80f;
                shake = 1f;
                shots = 12;
                inaccuracy = 19f;
                velocityRnd = 0.2f;
                xRand = 1.2f;
                shootSound = Sounds.missile;

                bullet = UnityBullets.scarMissile;
            }}, new Weapon("unity-scar-large-launcher"){{
                x = 13.75f;
                y = -24.5f;
                shootY = 5f;
                shadow = 8f;
                rotateSpeed = 5f;
                rotate = true;
                reload = 75f;
                shake = 1f;
                shots = 12;
                inaccuracy = 19f;
                velocityRnd = 0.2f;
                xRand = 1.2f;
                shootSound = Sounds.missile;

                bullet = UnityBullets.scarMissile;
            }}, new Weapon("unity-scar-small-laser-weapon"){{
                x = 18.25f;
                y = 11.75f;
                shootY = 4f;
                rotateSpeed = 5f;
                rotate = true;
                reload = 3f * 60f;
                shake = 1.2f;
                continuous = true;
                alternate = false;
                shootSound = Sounds.none;

                bullet = new ContinuousLaserBulletType(40f){{
                    length = 180f;
                    lifetime = 10f * 60f;
                    shake = 1.2f;
                    incendChance = 0f;
                    largeHit = false;
                    colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.endColor, Color.white};
                    width = 4f;
                    hitColor = UnityPal.scarColor;
                    lightColor = UnityPal.scarColorAlpha;
                    hitEffect = HitFx.scarHitSmall;
                }};
            }}, new Weapon(name + "-laser-weapon"){{
                x = 29.75f;
                y = -20.5f;
                shootY = 7f;
                shadow = 19f;
                rotateSpeed = 1.5f;
                rotate = true;
                reload = 7f * 60f;
                shake = 2f;
                continuous = true;
                alternate = false;
                shootSound = Sounds.none;

                bullet = new ContinuousLaserBulletType(210f){{
                    length = 360f;
                    lifetime = 3f * 60f;
                    shake = 3f;
                    colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.endColor, Color.white};
                    width = 8f;
                    hitColor = UnityPal.scarColor;
                    lightColor = UnityPal.scarColorAlpha;
                    hitEffect = HitFx.scarHitSmall;
                }};
            }});

            DirectionShieldAbility shield = new DirectionShieldAbility(6, 0.04f, 29f, 3400f, 4.2f, 0.9f, 54f);
            shield.healthBarColor = UnityPal.endColor;

            abilities.add(shield);
        }};

        whirlwind = new UnityUnitType("whirlwind"){{
            health = 280;
            rotateSpeed = 4.5f;
            faceTarget = false;
            flying = true;
            speed = 8f;
            drag = 0.019f;
            accel = 0.028f;
            hitSize = 8f;
            engineOffset = 8f;

            weapons.add(new Weapon(){{
                mirror = false;
                x = 0f;
                y = 4f;
                minShootVelocity = 5f;
                continuous = true;
                shootStatus = UnityStatusEffects.reloadFatigue;
                shootCone = 20f;

                bullet = new SaberContinuousLaserBulletType(21f){{
                    lightStroke = 40f;
                    largeHit = false;
                    lifetime = 10 * 60f;
                    length = 160f;
                    width = 5f;
                    incendChance = 0f;
                    hitEffect = HitFx.coloredHitSmall;
                    lightColor = hitColor = UnityPal.scarColorAlpha;
                    colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.endColor, Color.white};
                    strokes = new float[]{1.5f, 1f, 0.3f};
                }};

                shootStatusDuration = bullet.lifetime;
                reload = 2 * 60f;
            }}, new Weapon(){{
                rotate = true;
                x = 4.2f;
                reload = 50f;
                inaccuracy = 1.1f;
                shots = 5;
                shotDelay = 3f;

                bullet = new MissileBulletType(5f, 1f){{
                    height = 10f;
                    shrinkY = 0f;
                    backColor = trailColor = UnityPal.scarColor;
                    frontColor = UnityPal.endColor;
                    splashDamage = 25f;
                    splashDamageRadius = 20f;
                    weaveMag = 3f;
                    weaveScale = 4f;
                }};
            }});
        }};

        jetstream = new UnityUnitType("jetstream"){{
            //description = "There will be Bloodshed"; use bundle, eye
            health = 670;
            rotateSpeed = 12.5f;
            flying = true;
            speed = 9.2f;
            drag = 0.019f;
            accel = 0.028f;
            hitSize = 11f;
            engineOffset = 11f;

            weapons.add(new Weapon(){{
                mirror = false;
                x = 0f;
                y = 7f;
                continuous = true;
                shootStatus = UnityStatusEffects.reloadFatigue;
                shootCone = 15f;

                bullet = new SaberContinuousLaserBulletType(35f){{
                    swipe = true;
                    lightStroke = 40f;
                    largeHit = false;
                    lifetime = 15f * 60f;
                    length = 150f;
                    width = 5f;
                    incendChance = 0f;
                    hitEffect = HitFx.coloredHitSmall;
                    lightColor = hitColor = UnityPal.scarColorAlpha;
                    colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.endColor, Color.white};
                    strokes = new float[]{1.5f, 1f, 0.3f};
                    lenscales = new float[]{0.85f, 0.97f, 1f, 1.02f};
                }};

                reload = 60f * 3.2f;
                shootStatusDuration = bullet.lifetime;
            }}, new Weapon("unity-small-scar-weapon"){{
                rotate = true;
                x = 7.25f;
                y = -3.5f;
                reload = 50f;
                inaccuracy = 1.1f;
                shots = 6;
                shotDelay = 4f;

                bullet = new MissileBulletType(5f, 1f){{
                    width = 7f;
                    height = 12f;
                    shrinkY = 0f;
                    backColor = trailColor = UnityPal.scarColor;
                    frontColor = UnityPal.endColor;
                    splashDamage = 40f;
                    splashDamageRadius = 20f;
                    weaveMag = 3f;
                    weaveScale = 4f;
                }};
            }});
        }};

        vortex = new UnityUnitType("vortex"){{
            health = 1200;
            rotateSpeed = 12.5f;
            flying = true;
            speed = 9.1f;
            drag = 0.019f;
            accel = 0.028f;
            hitSize = 11f;
            engineOffset = 14f;

            weapons.add(new Weapon(){{
                mirror = false;
                x = 0f;
                continuous = true;

                bullet = new SaberContinuousLaserBulletType(60f){{
                    swipe = true;
                    swipeDamageMultiplier = 1.2f;
                    largeHit = false;
                    lifetime = 5f * 60f;
                    length = 190f;
                    width = 5f;
                    incendChance = 0f;
                    hitEffect = HitFx.coloredHitSmall;
                    lightColor = hitColor = UnityPal.scarColorAlpha;
                    colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.endColor, Color.white};
                    strokes = new float[]{1.5f, 1f, 0.3f};
                }};

                reload = 2f * 60f;
            }});
        }};

        //endregion
        //region imber

        arcnelidia = new UnityUnitType("arcnelidia"){{
            segmentOffset = 23f;
            hitSize = 17f;
            health = 800;
            speed = 4f;
            accel = 0.035f;
            drag = 0.007f;
            rotateSpeed = 3.2f;
            engineSize = -1f;
            faceTarget = false;
            armor = 5f;
            flying = true;
            visualElevation = 0.8f;
            range = 210f;
            outlineColor = UnityPal.darkerOutline;

            weapons.add(new Weapon(){{
                x = 0f;
                reload = 10f;
                rotateSpeed = 50f;
                shootSound = Sounds.laser;
                mirror = rotate = true;
                minShootVelocity = 2.1f;
                bullet = new LaserBulletType(200f){{
                    colors = new Color[]{Pal.surge.cpy().mul(1f, 1f, 1f, 0.4f), Pal.surge, Color.white};
                    drawSize = 400f;
                    collidesAir = false;
                    length = 190f;
                }};
            }});

            segWeapSeq.add(new Weapon(){{
                x = 0f;
                reload = 60f;
                rotateSpeed = 50f;
                minShootVelocity = 0.01f;
                bullet = UnitTypes.horizon.weapons.first().bullet;
            }});
        }};

        rayTest = new UnityUnitType("ray-test"){{
            defaultController = GroundAI::new;
            flying = false;
            health = 500;
            speed = 1f;
            accel = 0.035f;
            drag = 0.007f;
            rotateSpeed = 3.2f;
            range = 210f;
            laserRange = 30f;
            maxConnections = 2;
        }};

        //endregion
        //region plague

        exowalker = new UnityUnitType("exowalker"){{
            health = 6000f;
            speed = 0.7f;
            drag = 0.1f;
            hitSize = 33f;

            rotateSpeed = 2f;

            legCount = 8;
            legGroupSize = 4;
            legLength = 120f;
            legBaseOffset = 9f;
            legMoveSpace = 0.9f;
            legPairOffset = 1.5f;
            legTrns = 0.5f;

            hovering = true;
            allowLegStep = true;
            visualElevation = 0.7f;
            groundLayer = Layer.legUnit + 0.01f;
            outlineColor = UnityPal.darkerOutline;

            CloneableSetWeapon t = UnityWeaponTemplates.plagueSmallMount;

            weapons.addAll(t.set(w -> {
                w.x = 9.5f;
                w.y = 8f;
                w.otherSide = 2;
            }), t.set(w -> {
                w.x = -9.5f;
                w.y = 8f;
                w.flipSprite = true;
                w.otherSide = 0;
            }), t.set(w -> {
                w.x = 12.25f;
                w.y = -12.25f;
                w.name += "-flipped";
                w.flipSprite = true;
                w.otherSide = 3;
            }), t.set(w -> {
                w.x = -12.25f;
                w.y = -12.25f;
                w.flipSprite = true;
                w.otherSide = 1;
            }), new Weapon("unity-drain-laser"){{
                x = 16f;
                y = -2.25f;
                shootY = 6.25f;
                mirror = true;
                rotate = true;
                shots = 3;
                spacing = 17.5f;
                reload = 1.5f * 60f;

                bullet = new ShrapnelBulletType(){{
                    damage = 43f;
                    length = 80f;
                    toColor = UnityPal.plague;
                }};
            }});
        }};

        toxobyte = new UnityUnitType("toxobyte"){{
            defaultController = WormAI::new;
            flying = true;
            health = 200f;
            speed = 3f;
            accel = 0.035f;
            drag = 0.012f;
            hitSize = 15.75f;
            segmentOffset = 16.25f;
            regenTime = 15f * 60f;
            splittable = true;
            circleTarget = true;
            omniMovement = false;
            angleLimit = 25f;
            segmentLength = 25;
            segmentDamageScl = 8f;
            engineSize = -1f;
            outlineColor = UnityPal.darkerOutline;

            weapons.add(new Weapon(){{
                x = 0f;
                rotate = false;
                mirror = false;
                reload = 70f;
                shots = 12;
                shootCone = 90f;
                inaccuracy = 35f;
                xRand = 2f;
                shotDelay = 0.5f;
                bullet = new SapBulletType(){{
                    color = UnityPal.plague;
                    damage = 20f;
                    length = 130f;
                    width = 1f;
                    status = StatusEffects.none;
                }};
            }});

            segWeapSeq.add(new Weapon(){{
                rotate = true;
                mirror = false;
                reload = 60f;
                bullet = new ArtilleryBulletType(5f, 7){{
                    collidesTiles = collidesAir = collidesGround = true;
                    width = height = 11f;
                    splashDamage = 25f;
                    splashDamageRadius = 25f;
                    trailColor = hitColor = lightColor = backColor = UnityPal.plagueDark;
                    frontColor = UnityPal.plague;
                }};
            }});
        }};

        catenapede = new UnityUnitType("catenapede"){{
            defaultController = WormAI::new;
            flying = true;
            health = 750f;
            speed = 2.4f;
            accel = 0.06f;
            drag = 0.03f;
            hitSize = 30f;
            segmentOffset = 31f;
            regenTime = 30f * 60f;
            splittable = true;
            chainable = true;
            circleTarget = true;
            lowAltitude = true;
            omniMovement = false;
            rotateSpeed = 2.7f;
            angleLimit = 25f;
            segmentLength = 2;
            maxSegments = 15;
            segmentDamageScl = 12f;
            healthDistribution = 0.15f;
            engineSize = -1f;
            outlineColor = UnityPal.darkerOutline;
            range = 160f;

            weapons.add(new Weapon("unity-drain-laser"){{
                y = -9f;
                x = 14f;
                shootY = 6.75f;
                rotateSpeed = 5f;
                reload = 5f * 60f;
                shootCone = 45f;
                rotate = true;
                continuous = true;
                alternate = false;
                shootSound = Sounds.respawning;

                bullet = new PointDrainLaserBulletType(45f){{
                    healPercent = 0.5f;
                    maxLength = 160f;
                    knockback = -34f;
                    lifetime = 10f * 60f;
                }};
            }});

            segWeapSeq.add(new Weapon("unity-small-plague-launcher"){{
                y = -8f;
                x = 14.75f;
                rotate = true;
                reload = 25f;
                shootSound = Sounds.missile;

                bullet = UnityBullets.plagueMissile;
            }}, new Weapon("unity-small-plague-launcher"){{
                y = -12.5f;
                x = 7.25f;
                rotate = true;
                reload = 15f;
                shootSound = Sounds.missile;

                bullet = UnityBullets.plagueMissile;
            }});
        }};

        //endregion
        //region koruh

        buffer = new UnityUnitType("buffer"){{
            mineTier = 1;
            speed = 0.75f;
            boostMultiplier = 1.26f;
            itemCapacity = 15;
            health = 150;
            buildSpeed = 0.9f;
            engineColor = Color.valueOf("d3ddff");
            canBoost = true;
            boostMultiplier = 1.5f;
            landShake = 1f;

            weapons.add(new Weapon(name + "-shotgun"){{
                top = false;
                shake = 2f;
                x = 3f;
                y = 0.5f;
                shootX = 0f;
                shootY = 3.5f;
                reload = 55f;
                shotDelay = 3f;
                alternate = true;
                shots = 2;
                inaccuracy = 0f;
                ejectEffect = Fx.none;
                shootSound = Sounds.spark;
                bullet = new LightningBulletType(){{
                    damage = 12;
                    shootEffect = Fx.hitLancer;
                    smokeEffect = Fx.none;
                    despawnEffect = Fx.none;
                    hitEffect = Fx.hitLancer;
                    keepVelocity = false;
                }};
            }});

            abilities.add(new LightningBurstAbility(120f, 8, 8, 17f, 14, Pal.lancerLaser));
        }};

        omega = new  UnityUnitType("omega"){{
            mineTier = 2;
            mineSpeed = 1.5f;
            itemCapacity = 80;
            speed = 0.4f;
            accel = 0.36f;
            canBoost = true;
            boostMultiplier = 0.6f;
            engineColor = Color.valueOf("feb380");
            health = 350f;
            buildSpeed = 1.5f;
            landShake = 4f;
            rotateSpeed = 3f;

            weapons.add(new Weapon(name + "-cannon"){{
                top = false;
                x = 4f;
                y = 0f;
                shootX = 1f;
                shootY = 3f;
                recoil = 4f;
                reload = 38f;
                shots = 4;
                spacing = 8f;
                inaccuracy = 8f;
                alternate = true;
                ejectEffect = Fx.none;
                shake = 3f;
                shootSound = Sounds.shootBig;
                bullet = new MissileBulletType(2.7f, 12f){{
                    width = height = 8f;
                    shrinkX = shrinkY = 0f;
                    drag = -0.003f;
                    homingRange = 60f;
                    keepVelocity = false;
                    splashDamageRadius = 25f;
                    splashDamage = 10f;
                    lifetime = 120f;
                    trailColor = Color.gray;
                    backColor = Pal.bulletYellowBack;
                    frontColor = Pal.bulletYellow;
                    hitEffect = Fx.blastExplosion;
                    despawnEffect = Fx.blastExplosion;
                    weaveScale = 8f;
                    weaveMag = 2f;

                    status = StatusEffects.blasted;
                    statusDuration = 60f;
                }};
            }});

            String armorRegion = name + "-armor";
            abilities.add(new ShootArmorAbility(50f, 0.06f, 2f, 0.5f, armorRegion));
        }};

        cache = new UnityUnitType("cache"){{
            mineTier = -1;
            speed = 7f;
            drag = 0.001f;
            health = 560;
            engineColor = Color.valueOf("d3ddff");
            flying = true;
            armor = 6f;
            accel = 0.02f;

            weapons.add(new Weapon(){{
                top = false;
                shootY = 1.5f;
                reload = 70f;
                shots = 4;
                inaccuracy = 2f;
                alternate = true;
                ejectEffect = Fx.none;
                velocityRnd = 0.2f;
                spacing = 1f;
                shootSound = Sounds.missile;
                bullet = new MissileBulletType(5f, 21f){{
                    width = 8f;
                    height = 8f;
                    shrinkY = 0f;
                    drag = -0.003f;
                    keepVelocity = false;
                    splashDamageRadius = 20f;
                    splashDamage = 1f;
                    lifetime = 60;
                    trailColor = Color.valueOf("b6c6fd");
                    hitEffect = Fx.blastExplosion;
                    despawnEffect = Fx.blastExplosion;
                    backColor = Pal.bulletYellowBack;
                    frontColor = Pal.bulletYellow;
                    weaveScale = 8f;
                    weaveMag = 2f;
                }};
            }});

            String shieldSprite = name + "-shield";
            abilities.add(new MoveLightningAbility(10f, 14, 0.15f, 4f, 3.6f, 6f, Pal.lancerLaser, shieldSprite));
        }};

        dijkstra = new UnityUnitType("dijkstra"){{
            mineTier = -1;
            speed = 7.5f;
            drag = 0.01f;
            health = 640f;
            flying = true;
            armor = 8;
            accel = 0.01f;
            lowAltitude = true;
            range = 220f;

            abilities.add(new SlashAbility(unit ->
                Units.closestEnemy(unit.team, unit.x, unit.y, 20f * tilesize, u ->
                    u.within(unit, 15f * tilesize) &&
                        Angles.angleDist(unit.rotation, unit.angleTo(u)) < 5f
                ) != null
            ));

            weapons.add(new Weapon(){{
                rotate = true;
                rotateSpeed = 8f;
                shadow = 20f;
                x = 0f;
                y = 0f;
                reload = 150f;
                shots = 1;
                alternate = false;
                ejectEffect = Fx.none;
                bullet = UnityBullets.laserZap;
                shootSound = Sounds.laser;
                mirror = false;
            }}, new Weapon(){{
                x = 0f;
                y = 0f;
                reload = 7f;
                shots = 1;
                alternate = true;
                ejectEffect = Fx.none;
                velocityRnd = 1.5f;
                spacing = 15f;
                inaccuracy = 20f;

                bullet = UnityBullets.plasmaBullet;
                shootSound = Sounds.spark;
            }});
        }};

        phantasm = new UnityUnitType("phantasm"){{
            mineTier = -1;
            speed = 5.6f;
            drag = 0.08f;
            accel = 0.08f;
            range = 240f;

            health = 720f;
            flying = true;
            hitSize = 15f;
            rotateSpeed = 12f;

            engineOffset = 4.6f;
            engineSize = 2.5f;

            abilities.add(new TeleportAbility<>(unit -> {
                Bullet[] should = {null};

                float rad = 3f * tilesize + unit.hitSize() / 2f;
                Groups.bullet.intersect(unit.x - rad, unit.y - rad, rad * 2f, rad * 2f, b -> {
                    if(unit.team.isEnemy(b.team) && b.within(unit, rad) && b.collides(unit) && should[0] == null){
                        should[0] = b;
                    }
                });

                return should[0];
            }, 15f * tilesize){{
                slots = 5;

                rechargeTime = 180f;
                delayTime = 60f;

                waitEffect = UnityFx.waitEffect2;
                rechargeEffect = UnityFx.ringEffect2;
                delayEffect = UnityFx.smallRingEffect2;
            }});

            weapons.add(new Weapon("unity-phantasmal-gun"){{
                top = false;
                x = 1.25f;
                y = 3.25f;
                reload = 9f;
                inaccuracy = 2f;

                ejectEffect = Fx.casing2;
                shootSound = Sounds.shootBig;
                bullet = UnityBullets.phantasmalBullet;
            }});
        }};

        //endregion
        //region monolith

        monolithSoul = new UnityUnitType("monolith-soul"){
            {
                defaultController = MonolithSoulAI::new;

                health = 100f;
                speed = 1.4f;
                rotationSpeed = 15f;
                accel = 0.2f;
                drag = 0.08f;
                flying = true;
                lowAltitude = true;
                fallSpeed = 1f;
                omniMovement = false;
            }

            @Override
            public boolean isHidden(){
                return true;
            }

            @Override
            public void applyColor(Unit unit){
                Draw.mixcol(UnityPal.monolithDark, 0.8f);
                Draw.alpha(0.8f);
            }

            @Override
            public void draw(Unit unit){
                Draw.blend(Blending.additive);
                super.draw(unit);
                Draw.blend();

                float z = Draw.z();
                Draw.z(Layer.effect);

                Draw.color(UnityPal.monolithLight);

                float rad = unit.hitSize + 8f;
                Lines.polySeg(Lines.circleVertices(rad), 0, (int)(48f * unit.healthf()), unit.x, unit.y, rad, 0f);
                Draw.color();

                Draw.z(z);
            }
        };

        stele = new UnityUnitType("stele"){{
            health = 300f;
            speed = 0.6f;
            hitSize = 8f;
            armor = 5f;

            canBoost = true;
            boostMultiplier = 2.5f;
            outlineColor = UnityPal.darkOutline;

            weapons.add(new Weapon(name + "-shotgun"){{
                top = false;
                x = 5.25f;
                y = -0.25f;

                reload = 60f;
                recoil = 2.5f;
                shots = 12;
                shotDelay = 0f;
                spacing = 0.3f;
                inaccuracy = 0.5f;
                velocityRnd = 0.2f;
                shootSound = Sounds.shootBig;

                bullet = new BasicBulletType(3.5f, 6f){
                    {
                        lifetime = 60f;
                        trailWidth = width = height = 2f;
                        weaveScale = 3f;
                        weaveMag = 5f;
                        homingPower = 0.7f;

                        shootEffect = Fx.hitLancer;
                        hitEffect = despawnEffect = HitFx.monoHitSmall;
                        trailColor = frontColor = Pal.lancerLaser;
                        backColor = Pal.lancerLaser.cpy().mul(0.7f);

                        trailLength = 6;
                    }

                    @Override
                    public void draw(Bullet b){
                        drawTrail(b);

                        Draw.color(frontColor);
                        Fill.circle(b.x, b.y, width);
                        Draw.color();
                    }
                };
            }});
        }};

        pedestal = new UnityUnitType("pedestal"){{
            health = 1200f;
            speed = 0.5f;
            rotateSpeed = 2.6f;
            hitSize = 11f;
            armor = 10f;
            singleTarget = true;
            maxSouls = 4;

            canBoost = true;
            boostMultiplier = 2.5f;
            engineSize = 3.5f;
            engineOffset = 6f;
            outlineColor = UnityPal.darkOutline;

            weapons.add(new Weapon(name + "-gun"){{
                top = false;
                x = 10.75f;
                y = 2.25f;

                reload = 40f;
                recoil = 3.2f;
                shootSound = UnitySounds.energyBolt;

                BulletType subBullet = new LightningBulletType();
                subBullet.damage = 24f;

                bullet = new RicochetBulletType(3f, 72f, "shell"){
                    {
                        width = 20f;
                        height = 20f;
                        lifetime = 60f;
                        frontColor = UnityPal.monolithLight;
                        backColor = UnityPal.monolith.cpy().mul(0.75f);
                        trailColor = UnityPal.monolithDark.cpy().mul(0.5f);

                        trailEffect = UnityFx.ricochetTrailSmall;
                        shootEffect = Fx.lightningShoot;
                        hitEffect = despawnEffect = HitFx.monoHitBig;
                    }

                    @Override
                    public void init(Bullet b){
                        super.init(b);
                        for(int i = 0; i < 3; i++){
                            subBullet.create(b, b.x, b.y, b.vel.angle());
                            Sounds.spark.at(b.x, b.y, Mathf.random(0.6f, 0.8f));
                        }
                    }
                };
            }});
        }};

        pilaster = new UnityUnitType("pilaster"){{
            health = 2000f;
            speed = 0.4f;
            rotateSpeed = 2.2f;
            hitSize = 26.5f;
            armor = 15f;
            mechFrontSway = 0.55f;
            maxSouls = 5;

            canBoost = true;
            boostMultiplier = 2.5f;
            engineSize = 5f;
            engineOffset = 10f;

            ammoType = new PowerAmmoType(1000);
            outlineColor = UnityPal.darkOutline;

            weapons.add(new Weapon("unity-monolith-medium-weapon-mount"){{
                top = false;
                x = 4f;
                y = 7.5f;
                shootY = 6f;

                rotate = true;
                recoil = 2.5f;
                reload = 25f;
                shots = 3;
                spacing = 3f;
                shootSound = Sounds.spark;

                bullet = new LightningBulletType(){{
                    damage = 30f;
                    lightningLength = 18;
                }};
            }}, new Weapon("unity-monolith-large-weapon-mount"){{
                top = false;
                x = 13f;
                y = 2f;
                shootY = 10.5f;

                rotate = true;
                rotateSpeed = 10f;
                recoil = 3f;
                reload = 40f;
                shootSound = Sounds.laser;

                bullet = new LaserBulletType(160f);
            }});
        }};

        pylon = new UnityUnitType("pylon"){{
            health = 14400f;
            speed = 0.43f;
            rotateSpeed = 1.48f;
            hitSize = 36f;
            armor = 23f;
            commandLimit = 8;
            maxSouls = 7;

            allowLegStep = hovering = true;
            visualElevation = 0.2f;
            legCount = 4;
            legExtension = 8f;
            legSpeed = 0.08f;
            legLength = 16f;
            legMoveSpace = 1.2f;
            legTrns = 0.5f;
            legBaseOffset = 11f;

            ammoType = new PowerAmmoType(2000);
            groundLayer = Layer.legUnit;
            outlineColor = UnityPal.darkOutline;

            weapons.add(new Weapon(name + "-laser"){{
                soundPitchMin = 1f;
                top = false;
                mirror = false;
                shake = 15f;
                shootY = 11f;
                x = y = 0f;
                reload = 280f;
                recoil = 0f;
                cooldownTime = 280f;

                shootStatusDuration = 60f * 1.8f;
                shootStatus = StatusEffects.unmoving;
                shootSound = Sounds.laserblast;
                chargeSound = Sounds.lasercharge;
                firstShotDelay = UnityFx.pylonLaserCharge.lifetime / 2f;

                bullet = UnityBullets.pylonLaser;
            }}, new Weapon("unity-monolith-large2-weapon-mount"){{
                x = 14f;
                y = 5f;
                shootY = 14f;

                rotate = true;
                rotateSpeed = 3.5f;
                shootSound = Sounds.laser;
                shake = 5f;
                reload = 20f;
                recoil = 4f;

                bullet = UnityBullets.pylonLaserSmall;
            }});
        }};

        monument = new UnityUnitType("monument"){{
            health = 32000f;
            speed = 0.42f;
            rotateSpeed = 1.4f;
            hitSize = 48f;
            armor = 32f;
            commandLimit = 8;
            maxSouls = 9;

            visualElevation = 0.3f;
            allowLegStep = hovering = true;
            legCount = 6;
            legLength = 30f;
            legExtension = 8f;
            legSpeed = 0.1f;
            legTrns = 0.5f;
            legBaseOffset = 15f;
            legMoveSpace = 1.2f;
            legPairOffset = 3f;
            legSplashDamage = 64f;
            legSplashRange = 48f;

            ammoType = new PowerAmmoType(2000);
            groundLayer = Layer.legUnit;
            outlineColor = UnityPal.darkOutline;

            BulletType laser = new LaserBulletType(640f);
            weapons.add(new Weapon("unity-monolith-large2-weapon-mount"){{
                top = false;
                x = 14f;
                y = 12f;
                shootY = 14f;

                rotate = true;
                rotateSpeed = 3.5f;
                reload = 36f;
                recoil = shake = 5f;
                shootSound = Sounds.laser;

                bullet = laser;
            }}, new Weapon("unity-monolith-large2-weapon-mount"){{
                top = false;
                x = 20f;
                y = 3f;
                shootY = 14f;

                rotate = true;
                rotateSpeed = 3.5f;
                reload = 48f;
                recoil = shake = 5f;
                shootSound = Sounds.laser;

                bullet = laser;
            }}, new Weapon("unity-monolith-railgun-big"){{
                mirror = false;
                x = 0f;
                y = -12f;
                shootY = 35f;
                shadow = 30f;

                reload = 200f;
                recoil = shake = 8f;
                shootCone = 2f;
                cooldownTime = 210f;
                shootSound = Sounds.railgun;

                bullet = UnityBullets.monumentRailBullet;
            }});
        }};

        colossus = new UnityUnitType("colossus"){{
            health = 60000f;
            speed = 0.4f;
            rotateSpeed = 1.2f;
            hitSize = 64f;
            armor = 45f;
            commandLimit = 8;
            maxSouls = 12;

            visualElevation = 0.5f;
            allowLegStep = hovering = true;
            legCount = 6;
            legLength = 48f;
            legExtension = 12f;
            legSpeed = 0.1f;
            legTrns = 0.5f;
            legBaseOffset = 15f;
            legMoveSpace = 0.82f;
            legPairOffset = 3f;
            legSplashDamage = 84f;
            legSplashRange = 48f;

            ammoType = new PowerAmmoType(2000);
            groundLayer = Layer.legUnit;
            outlineColor = UnityPal.darkOutline;

            abilities.add(new LightningSpawnAbility(8, 32f, 2f, 0.05f, 180f, 56f, 200f));

            weapons.add(new Weapon(name + "-weapon"){{
                top = false;
                x = 30f;
                y = 7.75f;
                shootY = 20f;

                reload = 144f;
                recoil = 8f;
                spacing = 1f;
                inaccuracy = 6f;
                shots = 5;
                shotDelay = 3f;
                shootSound = Sounds.laserblast;

                bullet = new LaserBulletType(1920f){{
                    width = 45f;
                    length = 400f;
                    lifetime = 32f;

                    lightningSpacing = 35f;
                    lightningLength = 4;
                    lightningDelay = 1.5f;
                    lightningLengthRand = 6;
                    lightningDamage = 48f;
                    lightningAngleRand = 30f;
                    lightningColor = Pal.lancerLaser;
                }};
            }});
        }};

        bastion = new UnityUnitType("bastion"){{
            health = 120000f;
            speed = 0.4f;
            rotateSpeed = 1.2f;
            hitSize = 67f;
            armor = 100f;
            commandLimit = 8;
            maxSouls = 15;

            visualElevation = 0.7f;
            allowLegStep = hovering = true;
            legCount = 6;
            legLength = 72f;
            legExtension = 16f;
            legSpeed = 0.12f;
            legTrns = 0.6f;
            legBaseOffset = 18f;
            legMoveSpace = 0.6f;
            legPairOffset = 3f;
            legSplashDamage = 140f;
            legSplashRange = 56f;

            ammoType = new PowerAmmoType(2000);
            groundLayer = Layer.legUnit;
            outlineColor = UnityPal.darkOutline;

            abilities.add(new LightningSpawnAbility(12, 16f, 3f, 0.05f, 300f, 96f, 640f));

            BulletType energy = new RicochetBulletType(6f, 50f, "shell"){{
                width = 9f;
                height = 11f;
                shrinkY = 0.3f;
                lifetime = 45f;
                weaveScale = weaveMag = 3f;
                trailChance = 0.3f;

                frontColor = UnityPal.monolithLight;
                backColor = UnityPal.monolith;
                trailColor = UnityPal.monolithDark;
                shootEffect = Fx.lancerLaserShoot;
                smokeEffect = Fx.hitLancer;
                hitEffect = despawnEffect = HitFx.monoHitSmall;

                splashDamage = 60f;
                splashDamageRadius = 10f;

                lightning = 3;
                lightningDamage = 12f;
                lightningColor = Pal.lancerLaser;
                lightningLength = 6;
            }};

            weapons.add(new Weapon(name + "-mount"){{
                x = 9f;
                y = -11.5f;
                shootY = 10f;

                rotate = true;
                rotateSpeed = 8f;

                reload = 24f;
                recoil = 6f;
                shots = 8;
                velocityRnd = 0.3f;
                spacing = 5f;
                shootSound = UnitySounds.energyBolt;

                bullet = energy;
            }}, new Weapon(name + "-mount"){{
                x = 23.5f;
                y = 5.5f;
                shootY = 10f;

                rotate = true;
                rotateSpeed = 8f;

                reload = 15f;
                recoil = 6f;
                shots = 5;
                velocityRnd = 0.3f;
                spacing = 6f;
                shootSound = UnitySounds.energyBolt;

                bullet = energy;
            }}, new Weapon(name + "-gun"){{
                x = 12.5f;
                y = 12f;
                shootY = 13.5f;

                rotate = true;
                rotateSpeed = 6f;
                shots = 8;
                shotDelay = 3f;

                reload = 30f;
                recoil = 8f;
                shootSound = Sounds.shootBig;

                bullet = new RicochetBulletType(12.5f, 640f, "shell"){
                    {
                        width = 20f;
                        height = 25f;
                        shrinkY = 0.2f;
                        lifetime = 30f;
                        trailLength = 3;
                        pierceCap = 6;

                        frontColor = Color.white;
                        backColor = UnityPal.monolithLight;
                        trailColor = UnityPal.monolith;
                        shootEffect = Fx.lancerLaserShoot;
                        smokeEffect = Fx.hitLancer;
                        hitEffect = despawnEffect = HitFx.monoHitBig;

                        lightning = 3;
                        lightningDamage = 12f;
                        lightningColor = Pal.lancerLaser;
                        lightningLength = 15;
                    }

                    @Override
                    public void update(Bullet b){
                        super.update(b);
                        if(Mathf.chanceDelta(0.3f)){
                            Lightning.create(b, lightningColor, lightningDamage, b.x, b.y, b.rotation(), lightningLength / 2);
                        }
                    }
                };
            }});
        }};

        adsect = new UnityUnitType("adsect"){{
            defaultController = AssistantAI.create(Assistance.mendCore, Assistance.mine, Assistance.build);
            health = 180f;
            speed = 4f;
            accel = 0.4f;
            drag = 0.2f;
            rotateSpeed = 15f;
            flying = true;
            mineTier = 2;
            mineSpeed = 3f;
            buildSpeed = 0.8f;
            circleTarget = false;

            ammoType = new PowerAmmoType(500);
            engineColor = UnityPal.monolith;
            outlineColor = UnityPal.darkOutline;

            weapons.add(new Weapon(){{
                mirror = false;
                rotate = false;
                x = 0f;
                y = 4f;
                reload = 6f;
                shootCone = 40f;

                shootSound = Sounds.lasershoot;
                bullet = new LaserBoltBulletType(4f, 23f){{
                    healPercent = 1.5f;
                    lifetime = 40f;
                    collidesTeam = true;
                    frontColor = UnityPal.monolithLight;
                    backColor = UnityPal.monolith;
                    smokeEffect = hitEffect = despawnEffect = HitFx.hitMonolithLaser;
                }};
            }});
        }};

        comitate = new UnityUnitType("comitate"){{
            defaultController = AssistantAI.create(Assistance.mendCore, Assistance.mine, Assistance.build, Assistance.heal);
            health = 420f;
            speed = 4.5f;
            accel = 0.5f;
            drag = 0.15f;
            rotateSpeed = 15f;
            flying = true;
            mineTier = 3;
            mineSpeed = 5f;
            buildSpeed = 1.3f;
            circleTarget = false;

            ammoType = new PowerAmmoType(500);
            engineColor = UnityPal.monolith;
            outlineColor = UnityPal.darkOutline;

            weapons.add(new Weapon(){{
                mirror = false;
                rotate = false;
                x = 0f;
                y = 6f;
                reload = 12f;
                shootCone = 40f;

                shootSound = UnitySounds.energyBolt;
                bullet = new LaserBoltBulletType(6.5f, 60f){{
                    width = 4f;
                    height = 12f;
                    keepVelocity = false;
                    healPercent = 3.5f;
                    lifetime = 35f;
                    collidesTeam = true;
                    frontColor = UnityPal.monolithLight;
                    backColor = UnityPal.monolith;
                    smokeEffect = hitEffect = despawnEffect = HitFx.hitMonolithLaser;
                }};
            }}, new Weapon("unity-monolith-small-weapon-mount"){{
                top = false;
                mirror = alternate = true;
                x = 3f;
                y = 3f;
                reload = 40f;
                shots = 2;
                shotDelay = 5f;
                shootCone = 20f;

                shootSound = Sounds.lasershoot;
                bullet = new LaserBoltBulletType(4f, 30f){{
                    healPercent = 1.5f;
                    lifetime = 40f;
                    collidesTeam = true;
                    frontColor = UnityPal.monolithLight;
                    backColor = UnityPal.monolith;
                    smokeEffect = hitEffect = despawnEffect = HitFx.hitMonolithLaser;
                }};
            }});
        }};

        kami = new RainbowUnitType("kami-mkii"){{
            defaultController = EmptyAI::new;
            health = 120000f;
            speed = 15f;
            hitSize = 36f;
            flying = true;
            drawCell = false;
            outlineColor = Color.valueOf("464a61");
            clipSize = 1200f;

            antiCheatType = new AntiCheatVariables(health / 15f, health / 1.5f, 10f, health / 20f, 0.2f, 6f * 60f, 3f * 60f, 5f, 1);
        }};

        //endregion
        //region advance

        deviation = new UnityUnitType("deviation"){{
            health = 8000f;
            speed = 2.7f;
            accel = 0.07f;
            drag = 0.04f;
            hitSize = 96f;
            engineOffset = 38f;
            engineSize = 4.75f;
            flying = true;
            lowAltitude = true;
            outlineColor = Color.valueOf("464a61");

            weapons.add(new Weapon(name + "-mount"){{
                x = 28f;
                y = -17.5f;
                shootY = 10.25f;
                rotate = true;
                rotateSpeed = 5f;
                reload = 80f;
                inaccuracy = 1f;

                bullet = new LightningTurretBulletType(6f, 30f){{
                    range = 120f;
                    trailLength = 12;
                    trailColor = color = lightningColor = Pal.lancerLaser;
                    lightningDamage = 20f;
                    lightning = 5;
                    splashDamage = 20f;
                    splashDamageRadius = 35f;
                    status = StatusEffects.shocked;
                    reload = 30f;
                    duration = 5f * 60f;
                }};
            }});

            decorations.add(new WingDecorationType(name + "-wing", 4){{
                flapScl = 120f;
                flapAnimation = new Interp.ExpOut(2, 2.5f);
                wings.add(new Wing(0, 19f, -35.25f, 0.75f, 19f),
                    new Wing(1, 24.75f, -28.75f, 0.5f, 18f),
                    new Wing(2, 24.25f, -8f, 0.25f, 17f),
                    new Wing(3, 18f, 0.25f, 0f, 16f));
            }});
        }};

        anomaly = new UnityUnitType("anomaly"){{
            health = 17000f;
            speed = 2.1f;
            rotateSpeed = 1f;
            accel = 0.08f;
            drag = 0.07f;
            hitSize = 137.5f;
            engineSize = -1f;
            flying = true;
            lowAltitude = true;
            outlineColor = Color.valueOf("464a61");

            decorations.add(new WingDecorationType(name + "-wing", 2){{
                flapScl = 90f;
                flapAnimation = new Interp.ExpOut(2, 2.5f);
                wings.addAll(new Wing(0, 7.5f, -61f, 0f, 20f),
                new Wing(0, 10.5f, -48.25f, 0.1666f, 20f),
                new Wing(0, 13.5f, -35.5f, 0.3333f, 20f),
                new Wing(1, 13.5f, -22.75f, 0.5f, 20f),
                new Wing(1, 17.5f, -10f, 0.6666f, 20f),
                new Wing(1, 21.25f, 2.75f, 0.8333f, 20f));
            }});

            BulletType t = new BulletType(0f, 5f){{
                status = StatusEffects.slow;
                statusDuration = 20f;
                maxRange = 290f;
            }};

            weapons.add(new TractorBeamWeapon(name + "-mount"){{
                x = 18.75f;
                y = 22.5f;
                shootY = 8.75f + 2f;
                pullStrength = 40f;
                scaledForce = 50f;
                includeDead = true;
                bullet = t;
            }}, new TractorBeamWeapon(name + "-mount"){{
                x = 19.75f;
                y = 63f;
                shootY = 8.75f + 2f;
                pullStrength = 40f;
                scaledForce = 50f;
                includeDead = true;
                bullet = t;
            }}, new EnergyChargeWeapon(""){{
                x = 0f;
                y = 39.75f;
                shootY = 0f;
                mirror = false;
                reload = 2f * 60f;

                bullet = new AnomalyLaserBulletType(400f){{
                    lightningColor = Pal.lancerLaser;
                }};

                float rad = 70f;
                drawCharge = (unit, mount, charge) -> {
                    float rotation = unit.rotation - 90f,
                    wx = unit.x + Angles.trnsx(rotation, x, y),
                    wy = unit.y + Angles.trnsy(rotation, x, y),
                    scl = Math.max(1f - (mount.reload / reload), 0f) / 2f;

                    Draw.color(Pal.lancerLaser);
                    UnityDrawf.shiningCircle(unit.id, Time.time, wx, wy, 10f * scl, 5, 70f, 15f, 4f * scl, 90f);
                    Draw.color(Color.white);
                    UnityDrawf.shiningCircle(unit.id, Time.time, wx, wy, 5f * scl, 5, 70f, 15f, 3f * scl, 90f);

                    Lines.stroke(2f);
                    Draw.color(Pal.lancerLaser);
                    UnityDrawf.dashCircleAngle(wx, wy, rad, Mathf.sin(Time.time + Mathf.randomSeed(unit.id, 0f, 6f), 90f, 30f));
                    Draw.reset();
                };
                chargeCondition = (unit, mount) -> {
                    ChargeMount m = (ChargeMount)mount;
                    if(mount.reload > 0f){
                        mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0);
                    }
                    if((m.timer += Time.delta) >= 5f){
                        float rotation = unit.rotation - 90f,
                        wx = unit.x + Angles.trnsx(rotation, x, y),
                        wy = unit.y + Angles.trnsy(rotation, x, y);

                        Units.nearbyEnemies(unit.team, wx, wy, rad, u -> {
                            u.damage(90f);
                            if(u.dead){
                                m.charge += Mathf.sqrt(u.maxHealth) * (u.isFlying() ? Mathf.clamp(u.type.fallSpeed * 5f) : 1f);
                                for(int i = 0; i < 4; i++){
                                    Time.run(i * 5f, () ->
                                    SpecialFx.chargeTransfer.at(u.x, u.y, 0f, Pal.lancerLaser, unit));
                                }
                            }
                        });

                        m.timer = 0f;
                    }
                    if(m.charge > 0f){
                        float v = Math.min(m.charge, Time.delta * 2f);
                        m.charge -= v;
                        mount.reload -= v;
                    }
                    if(mount.reload < -250f){
                        mount.reload = -250f;
                        m.charge = 0f;
                    }
                };
            }});
        }};

        //endregion
        //region end

        voidVessel = new UnityUnitType("void-vessel"){{
            health = 10000f;
            speed = 3f;
            accel = 0.1f;
            drag = 0.03f;
            hitSize = 16f;
            engineOffset = 12.5f;
            engineSize = 1.5f;
            flying = true;
            lowAltitude = true;
            outlineColor = UnityPal.darkerOutline;

            antiCheatType = new AntiCheatVariables(health / 20f, health / 1.25f, health / 15f, health / 25f, 0.2f, 6f * 60f, 3f * 60f, 15f, 4);

            weapons.add(new Weapon("unity-end-small-mount"){{
                x = 8.5f;
                y = -4.5f;
                mirror = true;
                rotate = true;
                reload = 30f;
                inaccuracy = 15f;
                shootSound = UnitySounds.spaceFracture;

                bullet = new VoidFractureBulletType(32f, 600f){{
                    ratioDamage = 1f / 50f;
                    ratioStart = damage * 20f;
                    shootEffect = ShootFx.voidShoot;

                    modules = new AntiCheatBulletModule[]{
                        new ArmorDamageModule(1f, 2f, 0f)
                    };
                }};
            }});
        }};

        chronos = new UnityUnitType("chronos"){{
            health = 17000f;
            speed = 2f;
            accel = 0.1f;
            drag = 0.08f;
            hitSize = 36f;
            engineOffset = 19f;
            engineSize = 4f;
            flying = true;
            lowAltitude = true;
            outlineColor = UnityPal.darkerOutline;

            antiCheatType = new AntiCheatVariables(health / 20f, health / 1.25f, health / 15f, health / 25f, 0.2f, 6f * 60f, 3f * 60f, 15f, 4);

            abilities.add(new TimeStopAbility(unit -> false, 15f * 60, 10f * 60));

            weapons.add(new Weapon("unity-end-point-defence"){{
                x = 12f;
                y = -7.5f;
                reload = 12f;
                rotate = true;
                rotateSpeed = 5f;

                bullet = new TimeStopBulletType(6f, 510f);
            }});
        }};

        opticaecus = new InvisibleUnitType("opticaecus"){{
            health = 60000f;
            speed = 1.8f;
            drag = 0.02f;
            hitSize = 60.5f;
            flying = true;
            lowAltitude = true;
            circleTarget = false;
            engineOffset = 38f;
            engineSize = 6f;
            outlineColor = UnityPal.darkerOutline;

            antiCheatType = new AntiCheatVariables(health / 15f, health / 1.5f, health / 12.5f, health / 20f, 0.2f, 6f * 60f, 3f * 60f, 30f, 4);
            weapons.add(new Weapon(){{
                mirror = false;
                rotate = false;
                x = 0f;
                y = 11.25f;
                shootY = 0f;
                reload = 4f * 60f;

                bullet = new LaserBulletType(1400f){{
                    colors = new Color[]{UnityPal.scarColor, UnityPal.endColor, Color.white};
                    hitColor = UnityPal.endColor;
                    width = 30f;
                    length = 390f;
                    largeHit = true;

                    hitEffect = HitFx.endHitRedBig;
                }};
            }}, new Weapon("unity-doeg-launcher"){{
                x = 24.75f;
                mirror = true;
                rotate = true;
                reload = 1.2f * 60f;
                inaccuracy = 20f;
                shotDelay = 2f;
                shots = 10;

                bullet = new MissileBulletType(6f, 170f){{
                    lifetime = 55f;
                    frontColor = UnityPal.endColor;
                    backColor = trailColor = lightColor = UnityPal.scarColor;
                    shrinkY = 0.1f;
                    splashDamage = 320f;
                    splashDamageRadius = 45f;
                    weaveScale = 15f;
                    weaveMag = 2f;
                    width *= 1.6f;
                    height *= 2.1f;

                    hitEffect = HitFx.endHitRedSmall;
                }};
            }});
        }};

        devourer = new UnityUnitType("devourer-of-eldrich-gods"){{
            health = 1250000f;
            flying = true;
            speed = 5f;
            accel = 0.12f;
            drag = 0.1f;
            defaultController = WormAI::new;
            circleTarget = counterDrag = true;
            rotateShooting = false;
            splittable = chainable = false;
            hitSize = 39f * 1.55f;
            segmentOffset = (41f * 1.55f) + 7f;
            headOffset = 27.75f;
            segmentLength = 60;
            segmentCast = 7;
            barrageRange = 240f;
            lowAltitude = true;
            visualElevation = 2f;
            rotateSpeed = 2.2f;
            engineSize = -1f;
            range = 480f;
            armor = 16f;
            anglePhysicsSmooth = 0.5f;
            jointStrength = 1f;
            omniMovement = false;
            preventDrifting = true;
            outlineColor = UnityPal.darkerOutline;
            envEnabled = Env.terrestrial | Env.space;

            immuneAll = true;

            antiCheatType = new AntiCheatVariables(health / 600f, health / 190f, health / 610f, health / 100f, 0.6f, 7f * 60f, 8f * 60f, 35f, 4);

            BulletType t = new EndBasicBulletType(9.2f, 325f){{
                hitSize = 8f;
                shrinkY = 0f;
                width = 19f;
                height = 25f;

                overDamage = 800000f;
                ratioDamage = 1f / 300f;
                ratioStart = 15000;
                bleedDuration = 20f;

                backColor = hitColor = lightColor = UnityPal.scarColor;
                frontColor = UnityPal.endColor;

                hitEffect = HitFx.endHitRedSmall;
            }};

            weapons.add(new Weapon(){{
                mirror = false;
                ignoreRotation = true;
                x = 0f;
                y = 23f;
                reload = 15f * 60f;
                continuous = true;
                shake = 4f;
                firstShotDelay = 41f;
                chargeSound = UnitySounds.devourerMainLaser;
                shootSound = UnitySounds.continuousLaserB;
                bullet = UnityBullets.endLaser;
            }}, new Weapon("unity-doeg-destroyer"){{
                mirror = true;
                ignoreRotation = true;
                rotate = true;
                x = 19.25f;
                y = -22.75f;
                shootY = 12f;
                shadow = 16f;
                reload = 1.5f * 60;
                inaccuracy = 1.4f;
                shots = 6;
                shotDelay = 4f;
                shootSound = Sounds.shootBig;

                bullet = t;
            }});

            segWeapSeq.add(new Weapon("unity-doeg-launcher"){{
                mirror = true;
                rotate = true;
                x = 19f;
                y = 0f;
                shootY = 8f;
                shadow = 16f;
                reload = 1.2f * 60;
                inaccuracy = 1.4f;
                shots = 8;
                shotDelay = 3f;
                xRand = 12f;
                shootSound = Sounds.missile;

                bullet = new EndBasicBulletType(6f, 100f, "missile"){{
                    width = 9f;
                    height = 11f;
                    shrinkY = 0f;
                    hitSound = Sounds.explosion;
                    trailChance = 0.2f;
                    lifetime = 52f;
                    homingPower = 0.08f;
                    splashDamage = 90f;
                    splashDamageRadius = 45f;
                    weaveMag = 18f;
                    weaveScale = 1.6f;

                    overDamage = 900000f;
                    ratioDamage = 1f / 200f;
                    ratioStart = 17000f;

                    backColor = trailColor = hitColor = lightColor = UnityPal.scarColor;
                    frontColor = UnityPal.endColor;

                    hitEffect = HitFx.endHitRedSmall;
                }};
            }}, new Weapon("unity-doeg-destroyer"){{
                mirror = true;
                ignoreRotation = true;
                rotate = true;
                x = 22f;
                y = -15.75f;
                shootY = 12f;
                shadow = 16f;
                reload = 1.5f * 60;
                inaccuracy = 1.4f;
                shots = 6;
                shotDelay = 4f;
                shootSound = Sounds.shootBig;

                bullet = t;
            }}, new Weapon("unity-doeg-small-laser"){{
                mirror = true;
                alternate = false;
                rotate = true;
                x = 17.5f;
                y = 16.5f;
                reload = 2f * 60;
                shadow = 14f;
                shootSound = UnitySounds.continuousLaserA;
                continuous = true;

                bullet = UnityBullets.endLaserSmall;
            }});
        }};

        oppression = new UnityUnitType("oppression"){{
            health = 2500000f;
            flying = true;
            speed = 4.5f;
            accel = 0.13f;
            drag = 0.12f;
            defaultController = WormAI::new;
            circleTarget = counterDrag = true;
            rotateShooting = false;
            wormDecal = new WormDecal(name + "-hydraulics"){{
                lineWidth = 11.5f;
                lineColor = UnityPal.scarColor;
                baseX = 41.25f;
                baseY = 40.25f;
                endX = 81.75f;
                endY = -71.75f;
                baseOffset = 19.5f;
                segments = 2;
            }};
            splittable = chainable = false;
            hitSize = (114f * 2f) - 10f;
            angleLimit = 35f;
            segmentOffset = 114f * 2f;
            segmentLength = 60;
            segmentCast = 11;
            barrageRange = 490f;
            lowAltitude = true;
            visualElevation = 3f;
            rotateSpeed = 2.2f;
            engineSize = -1f;
            armor = 30f;
            anglePhysicsSmooth = 0.5f;
            jointStrength = 1f;
            omniMovement = false;
            preventDrifting = true;
            outlineColor = UnityPal.darkerOutline;
            envEnabled = Env.terrestrial | Env.space;

            immuneAll = true;

            antiCheatType = new AntiCheatVariables(8000, health / 190f, 10000f, health / 100f, 0.6f, 7f * 60f, 8f * 60f, 35f, 3);

            weapons.add(new Weapon(name + "-destroyer-1"){{
                x = 81.75f;
                y = -71.5f;
                shootY = 9.75f;
                
                rotate = true;
                rotateSpeed = 1.75f;

                reload = 2.3f * 60f;
                shots = 5;
                shotDelay = 6f;
                inaccuracy = 2f;

                bullet = UnityBullets.oppressionShell;
            }});

            segmentWeapons = new Seq[]{
                new Seq<Weapon>().addAll(
                    new Weapon(name + "-soul-destroyer"){{
                        mirror = false;

                        x = 0f;
                        y = 72f;
                        shootY = 0f;
                        layerOffset = 0.0001f;

                        rotate = true;
                        rotateSpeed = 1.5f;

                        reload = 4.75f * 60f;

                        bullet = new EndRailBulletType(){{
                            damage = 15000f;
                            length = 850f;
                            updateEffectSeg = 50f;
                            pierceDamageFactor = 0.001f;

                            overDamage = 640000f;
                            overDamagePower = 2.7f;
                            overDamageScl = 4000f;

                            ratioStart = 14000f;
                            ratioDamage = 1 / 10f;

                            hitEffect = HitFx.endHitRail;

                            modules = new AntiCheatBulletModule[]{
                            new ForceFieldDamageModule(150f, 10f, 500f, 0.5f, 1f / 20f, 10f * 60f),
                            new ArmorDamageModule(1f / 20f, 20f, 25f, 40f).set(10f, 2f)
                            };
                        }};
                    }},
                    new Weapon(name + "-destroyer-2"){{
                        x = 98f;
                        y = -26.25f;
                        shootY = 22f;
                        shootCone = 0.5f;
                        alternate = false;

                        rotate = true;
                        rotateSpeed = 1.75f;
                        continuous = true;

                        reload = 3.5f * 60f;

                        bullet = new EndContinuousLaserBulletType(550f){{
                            lifetime = 1.5f * 60;
                            length = 370f;
                            for(int i = 0; i < strokes.length; i++){
                                strokes[i] *= 0.7f;
                            }
                            overDamage = 800000f;
                            ratioDamage = 1f / 45f;
                            ratioStart = 800000f;
                            colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};
                            modules = new AntiCheatBulletModule[]{
                            new ForceFieldDamageModule(40f, 20f, 2000f, 1f, 1f / 40f, 3f * 60f),
                            new ArmorDamageModule(0.1f, 30f, 30f, 0.4f)
                            };
                        }};
                    }}
                ),
                new Seq<Weapon>().addAll(
                    new SweepWeapon(name + "-oppressor"){{
                        mirror = false;

                        x = 0f;
                        y = 72f;
                        shootY = 21f;
                        layerOffset = 0.0001f;

                        rotateSpeed = 2f;

                        reload = 4f * 60f;

                        bullet = new EndSweepLaser(7000f){{
                            lifetime = 130f;
                            length = 850f;
                            overDamage = 640000f;
                            overDamagePower = 2.7f;
                            overDamageScl = 4000f;
                            width = 25f;
                            collisionWidth = (width / 2f) * widthLoss;
                            distance = 220f;

                            ratioStart = 14000f;
                            ratioDamage = 1 / 10f;

                            hitEffect = HitFx.endHitRedBig;
                            hitBullet = UnityBullets.oppressionArea;
                            pierce = true;
                            pierceCap = 3;
                        }};
                    }},
                    new Weapon(name + "-destroyer-3"){{
                        x = 98f;
                        y = -26.25f;
                        shootY = 6f;

                        shootSound = Sounds.missile;

                        rotate = true;
                        rotateSpeed = 4f;
                        inaccuracy = 3f;
                        xRand = 10.25f;

                        shots = 13;
                        shotDelay = 5f;

                        reload = 3f * 60f;

                        bullet = UnityBullets.missileAntiCheat.copy();
                        bullet.drag = -0.01f;
                        bullet.lifetime = 90f;
                        bullet.homingRange = 90f;
                    }}
                ),
                new Seq<Weapon>().addAll(
                    new Weapon(name + "-void"){{
                        mirror = false;

                        x = 0f;
                        y = 72f;
                        shootY = 21f;
                        layerOffset = 0.0001f;

                        rotate = true;
                        rotateSpeed = 1.3f;

                        reload = 6f * 60f;

                        bullet = new VoidPortalBulletType(1300f){{
                            hitEffect = HitFx.voidHit;
                            shootEffect = ShootFx.voidShoot;
                            bleedDuration = 180f;
                            modules = new AntiCheatBulletModule[]{
                            new ForceFieldDamageModule(200f, 40f, 1500f, 1f, 1f / 50f, 4f * 60f),
                            new ArmorDamageModule(0.1f, 70f, 30f, 0.9f)
                            };
                        }};
                    }},
                new Weapon(name + "-destroyer-4"){{
                    x = 98f;
                    y = -26.25f;
                    shootY = 17.5f;

                    shootSound = UnitySounds.oppressionLightning;

                    rotate = true;
                    rotateSpeed = 3f;
                    inaccuracy = 15f;
                    xRand = 6f;

                    shots = 5;

                    reload = 4f * 60f;

                    bullet = new SlowLightningBulletType(120f);
                }}
                ),
                new Seq<Weapon>()
            };
        }};

        apocalypse = new InvisibleUnitType("apocalypse"){{
            health = 1725000f;
            speed = 0.75f;
            accel = 0.06f;
            drag = 0.06f;
            armor = 17f;
            hitSize = 205f;
            rotateSpeed = 0.3f;
            visualElevation = 3f;
            engineOffset = 116.5f;
            engineSize = 14f;
            rotateShooting = false;
            flying = true;
            lowAltitude = true;
            outlineColor = UnityPal.darkerOutline;

            immuneAll = true;

            antiCheatType = new AntiCheatVariables(health / 600f, health / 200f, health / 600f, health / 100f, 0.6f, 7f * 60f, 8f * 60f, 35f, 4);

            CloneableSetWeapon a = UnityWeaponTemplates.apocalypseSmall,
                b = UnityWeaponTemplates.apocalypseLaser, c = UnityWeaponTemplates.apocalypseLauncher;

            weapons.addAll(a.set(w -> {
                w.y = 47.25f;
                w.x = 74.75f;
            }), a.set(w -> {
                w.y = 24.75f;
                w.x = 80f;
                w.reload += 2;
            }), a.set(w -> {
                w.y = -76.5f;
                w.x = 108.5f;
                w.reload += 4;
            }), a.set(w -> {
                w.y = -31.25f;
                w.x = 70.25f;
                w.reload += 6;
            }), a.set(w -> {
                w.y = -74.5f;
                w.x = 56f;
                w.reload += 8;
            }), a.set(w -> {
                w.y = 0f;
                w.x = 51f;
            }), a.set(w -> {
                w.y = -80.25f;
                w.x = 65.5f;
                w.reload += 10;
            }), a.set(w -> {
                w.y = -63.5f;
                w.x = 36.25f;
                w.reload += 12;
            }), b.set(w -> {
                w.y = 37.25f;
                w.x = 44.25f;
            }), b.set(w -> {
                w.y = -22.75f;
                w.x = 51f;
                w.reload += 2;
            }), b.set(w -> {
                w.y = -51.75f;
                w.x = 62.25f;
                w.reload += 4;
            }), c.set(w -> {
                w.x = 87f;
                w.y = 0f;
            }), c.set(w -> {
                w.y = -23.25f;
                w.x = 97f;
                w.reload += 2;
            }), c.set(w -> {
                w.y = -50.5f;
                w.x = 97.5f;
                w.reload += 4;
            }), c.set(w -> {
                w.y = -74.75f;
                w.x = 87f;
                w.reload += 6;
            }), new Weapon("unity-quetzalcoatl"){{
                x = y = 0f;
                shootY = -8.25f;
                mirror = false;
                rotate = true;
                continuous = true;
                rotateSpeed = 0.2f;
                shadow = 63f;
                shootCone = 1f;
                reload = 6f * 60f;
                shootSound = UnitySounds.continuousLaserB;
                bullet = new EndCutterLaserBulletType(3100f){{
                    maxLength = 1200f;
                    lifetime = 3f * 60f;
                    width = 17f;
                    antiCheatScl = 5f;
                    laserSpeed = 70f;
                    buildingDamageMultiplier = 0.4f;
                    lightningColor = UnityPal.scarColor;
                    lightningDamage = 55f;
                    lightningLength = 13;

                    bleedDuration = 5f * 60f;
                    overDamage = 500000f;
                    ratioDamage = 1f / 100f;
                    ratioStart = 7000f;

                    hitEffect = HitFx.endHitRedBig;
                }};
            }});

            tentacles.add(new TentacleType(name + "-tentacle"){{
                x = 101.75f;
                y = -72.5f;
                rotationOffset = 30f;

                rotationSpeed = 3f;
                accel = 0.2f;
                speed = 4f * 2f;

                segments = 15;
                segmentLength = 37.25f;
                //angleLimit = firstSegmentAngleLimit = 361f;

                bullet = UnityBullets.endLaserSmall;
                automatic = false;
                continuous = true;
                reload = 4f * 60f;
            }}, new TentacleType(name + "-tentacle"){{
                x = 56.5f;
                y = -71.75f;
                rotationOffset = 10f;

                rotationSpeed = 3f;
                accel = 0.2f;
                speed = 4f * 2f;

                segments = 10;
                segmentLength = 37.25f;
                swayOffset = 90f;
                //angleLimit = firstSegmentAngleLimit = 361f;

                bullet = UnityBullets.endLaserSmall;
                automatic = false;
                continuous = true;
                reload = 4f * 60f;
            }}, new TentacleType(name + "-small-tentacle"){{
                x = 104.25f;
                y = -49f;
                rotationOffset = 35f;

                rotationSpeed = 3f;
                accel = 0.15f;
                speed = 5f * 2f;

                segments = 20;
                segmentLength = 28f;
                swayOffset = 120f;
                swayMag = 0.2f;
                swayScl = 120f;

                bullet = null;
                automatic = true;
                tentacleDamage = 430f;
            }}, new TentacleType(name + "-small-tentacle"){{
                x = 69.75f;
                y = -74.25f;
                rotationOffset = 20f;

                rotationSpeed = 3f;
                accel = 0.15f;
                speed = 5f * 2f;

                segments = 23;
                segmentLength = 28f;
                swayOffset = 70f;
                swayMag = 0.2f;
                swayScl = 120f;

                bullet = null;
                automatic = true;
                tentacleDamage = 430f;
            }});
        }

            @Override
            public void drawEngine(Unit unit){
                if(!unit.isFlying()) return;

                super.drawEngine(unit);

                float scale = unit.elevation;
                //float offset = (engineOffset / 2f) + ((engineOffset / 2f) * scale);

                for(int i : Mathf.signs){
                    float offset = 0.5f + (0.5f * scale);
                    float engineSizeB = 3.25f;
                    Tmp.v1.trns(unit.rotation, -105f * offset, 73.5f * i).add(unit);
                    Draw.color(unit.team.color);
                    if(unit instanceof Invisiblec e) Draw.alpha(fade(e));
                    Fill.circle(Tmp.v1.x, Tmp.v1.y, (engineSizeB + Mathf.absin(Time.time + 90f, 2f, engineSizeB / 2f)) * scale);
                    Tmp.v1.trns(unit.rotation, (-105f * offset) + 1f, 74f * i).add(unit);
                    Draw.color(Color.white);
                    if(unit instanceof Invisiblec e) Draw.alpha(fade(e));
                    Fill.circle(Tmp.v1.x, Tmp.v1.y, (engineSizeB + Mathf.absin(Time.time + 90f, 2f, engineSizeB / 2f)) / 2f * scale);
                    Draw.color();
                }
            }
        };

        ravager = new UnityUnitType("ravager"){{
            health = 1650000f;
            speed = 0.65f;
            drag = 0.16f;
            armor = 15f;
            hitSize = 138f;
            rotateSpeed = 1.1f;

            immuneAll = true;

            allowLegStep = true;
            hovering = true;
            groundLayer = Layer.legUnit + 6f;
            visualElevation = 3f;
            legCount = 8;
            legGroupSize = 4;
            legPairOffset = 2f;
            legMoveSpace = 0.5f;
            legLength = 140f;
            legExtension = -15f;
            legBaseOffset = 50f;
            legSpeed = 0.15f;
            legTrns = 0.2f;
            rippleScale = 7f;

            legSplashRange = 90f;
            legSplashDamage = 1400f;
            outlineColor = UnityPal.darkerOutline;

            antiCheatType = new AntiCheatVariables(health / 610f, health / 190f, health / 560f, health / 120f, 0.6f, 7f * 60f, 8f * 60f, 35f, 4);

            weapons.addAll(new Weapon(name + "-nightmare"){{
                bottomWeapons.add(this);

                x = 80.25f;
                y = -7.75f;
                shootY = 75f;
                reload = 6f * 60f;
                recoil = 8f;
                alternate = true;
                rotate = false;
                shootSound = UnitySounds.ravagerNightmareShoot;
                bullet = UnityBullets.ravagerLaser;
            }}, new Weapon(name + "-artillery"){{
                shootY = 11f;
                shots = 5;
                inaccuracy = 10f;
                shadow = 13.25f * 2f;
                y = -31.75f;
                x = 44.25f;
                rotate = true;
                rotateSpeed = 2f;
                velocityRnd = 0.2f;
                reload = 2f * 50f;
                shootSound = Sounds.artillery;
                bullet = UnityBullets.ravagerArtillery;
            }}, new Weapon(name + "-artillery"){{
                shootY = 11f;
                shots = 5;
                inaccuracy = 10f;
                shadow = 13.25f * 2f;
                y = -4.25f;
                x = 51.25f;
                rotate = true;
                rotateSpeed = 2f;
                velocityRnd = 0.2f;
                reload = 2.25f * 50f;
                shootSound = Sounds.artillery;
                bullet = UnityBullets.ravagerArtillery;
            }}, new Weapon(name + "-small-turret"){{
                shootY = 7f;
                inaccuracy = 2f;
                shadow = 9.25f * 2f;
                y = 53.75f;
                x = 34.5f;
                rotate = true;
                xRand = 2f;
                reload = 7f;
                shootSound = Sounds.missile;

                bullet = UnityBullets.missileAntiCheat;
            }}, new Weapon(name + "-small-turret"){{
                shootY = 7f;
                inaccuracy = 2f;
                shadow = 9.25f * 2f;
                y = 24.25f;
                x = 50.75f;
                rotate = true;
                xRand = 2f;
                reload = 7f;
                shootSound = Sounds.missile;

                bullet = UnityBullets.missileAntiCheat;
            }});
        }};

        desolation = new UnityUnitType("desolation"){{
            defaultController = SmartGroundAI::new;
            health = 307300f;
            speed = 0.7f;
            drag = 0.16f;
            armor = 35f;
            hitSize = 257f;
            rotateSpeed = 0.9f;

            visualElevation = 8f;
            groundLayer = Layer.flyingUnitLow + 1f;

            allowLegStep = legShadows = hovering = true;
            immuneAll = true;

            legTrns = 0.3f;
            legLength = 672f * (1f - (legTrns * 0.85f * 0.5f));
            legExtension = -48f;
            legCount = 8;
            legGroupSize = 2;
            legPairOffset = 1f;
            legMoveSpace = 0.2f;
            legBaseOffset = 61.25f;
            rippleScale = 12f;

            legSplashRange = 120f;
            legSplashDamage = 1700f;

            aimDst = hitSize / 2f;
            bulletWidth = 190f;

            outlineColor = UnityPal.darkerOutline;
            antiCheatType = new AntiCheatVariables(6000f, 12000f, health / 560f, health / 120f, 0.6f, 7f * 60f, 8f * 60f, 35f, 4);

            BulletType bRange = new BulletType(0f, 220f){{
                maxRange = 280f;
            }};

            Weapon w = new Weapon("unity-end-mount"){{
                reload = 35f;
                shootY = 9f;
                inaccuracy = 5f;
                shots = 3;
                shotDelay = 5f;
                rotate = true;
                rotateSpeed = 15f;
                mirror = false;
                alternate = true;
                bullet = new EndBasicBulletType(5f, 260f){{
                    lifetime = 70f;
                    width = 19f;
                    height = 27f;
                    backColor = lightColor = UnityPal.scarColor;
                    frontColor = Color.black;
                    trailEffect = TrailFx.endTrail;
                    trailChance = 0.4f;
                    hitSound = UnitySounds.spaceFracture;

                    overDamage = 2200000f;
                    ratioDamage = 1f / 170f;
                    ratioStart = 4000f;

                    fragBullets = 3;
                    fragVelocityMax = 1.2f;
                    fragVelocityMin = 0.5f;
                    fragCone = 120f;

                    hitEffect = HitFx.endHitRedSmall;

                    fragBullet = new VoidFractureBulletType(15f, 100f){{
                        width = 9.5f;
                        widthTo = 2f;
                        maxTargets = 5;
                        spikesRange = 90f;
                        spikesDamage = 50f;
                        overDamage = 1800000f;
                        ratioDamage = 1f / 50f;
                        ratioStart = 50000f;
                        shootEffect = ShootFx.voidShoot;

                        modules = new AntiCheatBulletModule[]{
                        new ArmorDamageModule(1f, 20f, 2f)
                        };
                    }};
                }};
            }}, w2 = new Weapon("unity-end-mount-2"){{
                shootY = 12f;
                reload = 100f;
                rotate = true;
                rotateSpeed = 5f;
                alternate = true;
                mirror = false;
                shots = 2;
                shotDelay = 5;

                bullet = new EndBasicBulletType(7f, 380f, "shell"){{
                    lifetime = 95f;
                    pierceShields = pierce = pierceBuilding = true;
                    pierceCap = 3;
                    hitShake = 1f;
                    shootEffect = Fx.shootBig;

                    shrinkY = 0f;

                    backColor = lightningColor = lightColor = UnityPal.scarColor;
                    frontColor = UnityPal.endColor;

                    lightning = 3;
                    lightningLength = 8;
                    lightningLengthRand = 4;
                    lightningDamage = 80f;
                    lightningType = UnityBullets.endLightning;

                    splashDamage = 220f;
                    splashDamageRadius = 80f;
                    width = 15f;
                    height = 21f;

                    hitEffect = HitFx.endHitRedSmall;
                }};
            }};

            weapons.addAll(new EnergyChargeWeapon(name + "-main"){{//0
                drawRegion = false;
                mirror = false;
                x = 0f;
                y = 80f;
                shootY = 36.5f;
                reload = 15f * 60f;

                bullet = new DesolationBulletType(1.75f, 2500f){{
                    lifetime = 8f * 60f;
                    overDamage = 900000f;
                    overDamagePower = 3f;
                    overDamageScl = 3500f;
                    ratioDamage = 1f / 50f;
                    ratioStart = 200000f;
                    bleedDuration = 10f * 60f;

                    modules = new AntiCheatBulletModule[]{
                    new ArmorDamageModule(1f / 30f, 15f, 30f, 5f),
                    new AbilityDamageModule(50f, 10f * 60f, 10f, 1f / 60f, 15f),
                    new ForceFieldDamageModule(5f, 15f, 200f, 7f, 1f / 40f, 5f * 60f)
                    };
                }};

                drawCharge = (unit, mount, charge) -> {
                    float r = unit.rotation - 90f,
                    wx = Angles.trnsx(r, x, y) + unit.x,
                    wy = Angles.trnsy(r, x, y) + unit.y;

                    Draw.color(UnityPal.scarColor);
                    Draw.blend(Blending.additive);
                    for(int i = 0; i < 4; i++){
                        float in = Mathf.curve(charge, i / 4f, (i + 1f) / 4f);
                        if(in > 0.0001f){
                            Draw.alpha(in);

                            Draw.rect(heatRegion, wx + Mathf.range(12f - (in * 11.3f)), wy + Mathf.range(12f - (in * 11.3f)), r);
                        }
                    }
                    Draw.blend();
                    Draw.color();
                };
            }}, new MultiTargetPointDefenceWeapon("unity-end-point-defence"){{//1 2
                x = 96.75f;
                y = 9f;
                alternate = false;
                reload = 15f;
                shots = 7;
                shootCone = 20f;
                rotationSpeed = 15f;
                beamEffect = LineFx.endPointDefence;
                color = UnityPal.scarColor;
                bullet = bRange;
            }}, new MultiTargetPointDefenceWeapon("unity-end-point-defence"){{//3 4
                x = 82f;
                y = 20.5f;
                alternate = false;
                reload = 10f;
                shots = 5;
                shootCone = 20f;
                rotationSpeed = 15f;
                beamEffect = LineFx.endPointDefence;
                color = UnityPal.scarColor;
                bullet = bRange;
            }},

            clnW(w, c -> {//5
                c.x = 62.25f;
                c.y = 6.75f;
                c.otherSide = 6;
            }), clnW(w, c -> {//6
                c.x = 57f;
                c.y = -16.25f;
                c.otherSide = 7;
                c.flipSprite = true;
            }), clnW(w, c -> {//7
                c.x = 52f;
                c.y = -39f;
                c.otherSide = 8;
                c.flipSprite = true;
            }), clnW(w, c -> {//8
                c.x = 46.75f;
                c.y = -61.75f;
                c.otherSide = 5;
                c.flipSprite = true;
            }),

            clnW(w, c -> {//9
                c.x = -62.25f;
                c.y = 6.75f;
                c.otherSide = 10;
            }), clnW(w, c -> {//10
                c.x = -57f;
                c.y = -16.25f;
                c.otherSide = 11;
                c.flipSprite = true;
            }), clnW(w, c -> {//11
                c.x = -52f;
                c.y = -39f;
                c.otherSide = 12;
                c.flipSprite = true;
            }), clnW(w, c -> {//12
                c.x = -46.75f;
                c.y = -61.75f;
                c.otherSide = 9;
                c.flipSprite = true;
            }),

            clnW(w2, c -> {//13
                c.x = 100.75f;
                c.y = -13f;
                c.otherSide = 14;
            }), clnW(w2, c -> {//14
                c.x = 79f;
                c.y = -23.5f;
                c.otherSide = 13;
                c.flipSprite = true;
            }),

            clnW(w2, c -> {//15
                c.x = -100.75f;
                c.y = -13f;
                c.otherSide = 16;
            }), clnW(w2, c -> {//16
                c.x = -79f;
                c.y = -23.5f;
                c.otherSide = 15;
                c.flipSprite = true;
            }),

            clnW(w2, c -> {//17
                c.x = 85f;
                c.y = -48.25f;
                c.otherSide = 18;
            }), clnW(w2, c -> {//18
                c.x = 68.75f;
                c.y = -65.75f;
                c.otherSide = 17;
                c.flipSprite = true;
            }),

            clnW(w2, c -> {//19
                c.x = -85f;
                c.y = -48.25f;
                c.otherSide = 20;
            }), clnW(w2, c -> {//20
                c.x = -68.75f;
                c.y = -65.75f;
                c.otherSide = 19;
                c.flipSprite = true;
            }));

            tentacles.addAll(new TentacleType(name + "-tentacle"){{
                x = 139f;
                y = -13.5f;
                rotationSpeed = 2.5f;
                accel = 0.2f;
                speed = 6f;
                top = true;

                firstSegmentAngleLimit = 17f;
                angleLimit = 30f;
                automatic = false;
                segmentLength = 44.5f;
                segments = 15;
                swayMag = 0.2f;
                swayScl = 120f;

                rotationOffset = 40f;

                reload = 3f * 60f;
                shootCone = 4f;
                shootSound = UnitySounds.ravagerNightmareShoot;
                bullet = new EndPointBlastLaserBulletType(250f){{
                    length = 320f;
                    width = 17f;
                    lifetime = 20f;
                    widthReduction = 3f;
                    auraWidthReduction = 4f;
                    damageRadius = 60f;
                    auraDamage = 1000f;

                    overDamage = 900000f;
                    ratioDamage = 1f / 200f;
                    ratioStart = 11000f;
                    bleedDuration = 10f * 60f;

                    hitEffect = HitFx.endHitRedSmall;

                    laserColors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.black};
                }};
                range = bullet.range();
            }},
            new TentacleType("unity-apocalypse-tentacle"){{
                x = 122.75f;
                y = -41f;
                rotationOffset = 35f;
                firstSegmentAngleLimit = 20f;
                top = true;

                rotationSpeed = 3f;
                accel = 0.2f;
                speed = 4f * 2f;

                segments = 17;
                segmentLength = 37.25f;

                bullet = UnityBullets.endLaserSmall;
                bulletDuration = 1.5f * 60f;
                automatic = false;
                continuous = true;
                reload = 4f * 60f;
            }},
            new TentacleType("unity-apocalypse-tentacle"){{
                x = 111.5f;
                y = -57.5f;
                rotationOffset = 30f;
                firstSegmentAngleLimit = 18f;
                top = true;

                rotationSpeed = 3f;
                accel = 0.2f;
                speed = 4f * 2f;

                segments = 14;
                segmentLength = 37.25f;
                swayOffset = 45f;

                bullet = UnityBullets.endLaserSmall;
                bulletDuration = 1.5f * 60f;
                automatic = false;
                continuous = true;
                reload = 4f * 60f;
            }},
            new TentacleType("unity-apocalypse-tentacle"){{
                x = 95.25f;
                y = -63f;
                rotationOffset = 25f;
                firstSegmentAngleLimit = 16f;
                top = true;

                rotationSpeed = 3f;
                accel = 0.2f;
                speed = 4f * 2f;

                segments = 9;
                segmentLength = 37.25f;
                swayOffset = 90f;

                bullet = UnityBullets.endLaserSmall;
                bulletDuration = 1.5f * 60f;
                automatic = false;
                continuous = true;
                reload = 4f * 60f;
            }});
        }};

        thalassophobia = new UnityUnitType("thalassophobia"){{
            health = 2750000;
            hitSize = 242.5f;
            speed = 1.9f;
            accel = 0.2f;
            drag = 0.16f;
            rotateSpeed = 0.3f;
            outlineColor = UnityPal.darkerOutline;

            immuneAll = true;

            antiCheatType = new AntiCheatVariables(8000f, 16000f, health / 520f, health / 120f, 0.6f, 7f * 60f, 8f * 60f, 35f, 4);

            decorations.add(new FlagellaDecorationType(name + "-tail", 4, 15, 45.75f){{
                x = 0f;
                y = -172f;
                swayScl = hitSize / speed;
                swayOffset = 67f;
            }});

            Weapon w = new EnergyChargeWeapon("unity-void-fracture-turret"){{
                mirror = false;
                alternate = true;
                shadow = 47f;
                shots = 3;
                shotDelay = 6f;
                reload = 120f;
                inaccuracy = 20f;
                shootCone = 7f;
                shootY = 0f;
                rotate = true;
                rotateSpeed = 2f;
                velocityRnd = 0.1f;
                shootSound = UnitySounds.spaceFracture;

                drawCharge = (unit, mount, charge) -> {
                    Weapon w = mount.weapon;
                    float rotation = unit.rotation - 90f,
                    wx = unit.x + Angles.trnsx(rotation, w.x, w.y),
                    wy = unit.y + Angles.trnsy(rotation, w.x, w.y);

                    Draw.color(Color.black);
                    UnityDrawf.shiningCircle(unit.id * 321 + Math.max(0, w.otherSide * 41), Time.time, wx, wy, 3.5f * charge, 6, 60f, 17f, 3f * charge, 70f);
                    Draw.color();
                };

                bullet = new VoidFractureBulletType(40f, 800f){{
                    speed = 5f;
                    delay = 50f;
                    lifetime = 60f;
                    drag = 0.09f;
                    nextLifetime = 13f;
                    ratioDamage = 1f / 170f;
                    ratioStart = 30000f;
                    bleedDuration = 40f;
                    length = 52f;
                    width = 20f;
                    widthTo = 8f;
                    spikesRand = 16f;
                    spikesDamage = 310f;
                    targetingRange = 400f;
                    maxTargets = 20;
                    shootEffect = ShootFx.voidShoot;

                    hitEffect = HitFx.voidHitBig;
                    smokeEffect = HitFx.voidHit;

                    modules = new AntiCheatBulletModule[]{
                        new ArmorDamageModule(50f, 50f, 2f),
                        new ForceFieldDamageModule(2f, 20f, 220f, 7f, 1f / 50f, 2f * 60f)
                    };
                }};
            }},
            m = new Weapon("unity-end-missile-launcher"){{
                shootY = 7.25f;
                reload = 50f;
                alternate = true;
                mirror = false;
                shotDelay = 3f;
                shots = 5;
                xRand = 5.75f;
                rotate = true;
                rotateSpeed = 4f;
                inaccuracy = 7f;
                shootSound = Sounds.missile;

                bullet = new EndBasicBulletType(4f, 210f, "missile"){{
                    lifetime = 75f;
                    width = height = 12f;
                    shrinkY = 0f;
                    drag = -0.01f;
                    splashDamageRadius = 45f;
                    splashDamage = 220f;
                    homingPower = 0.08f;
                    homingRange = 100f;
                    trailChance = 0.3f;
                    weaveScale = 6f;
                    weaveMag = 1f;

                    overDamage = 950000f;
                    ratioDamage = 1f / 400f;
                    ratioStart = 2000f;

                    hitEffect = HitFx.endHitRedSmall;
                    despawnEffect = HitFx.endHitRedSmall;

                    backColor = lightColor = trailColor = UnityPal.scarColor;
                    frontColor = UnityPal.endColor;
                }};
            }};

            weapons.addAll(
            clnW(w, y -> {//0
                y.x = 79.5f;
                y.y = -34f;
                y.otherSide = 1;
            }),
            clnW(w, y -> {//1
                y.x = 90.5f;
                y.y = -71.5f;
                y.otherSide = 2;
                y.flipSprite = true;
            }),
            clnW(w, y -> {//2
                y.x = 91.25f;
                y.y = -104f;
                y.otherSide = 0;
                y.flipSprite = true;
            }),

            clnW(w, y -> {//3
                y.x = -79.5f;
                y.y = -34f;
                y.otherSide = 4;
            }),
            clnW(w, y -> {//4
                y.x = -90.5f;
                y.y = -71.5f;
                y.otherSide = 5;
                y.flipSprite = true;
            }),
            clnW(w, y -> {//5
                y.x = -91.25f;
                y.y = -104f;
                y.otherSide = 3;
                y.flipSprite = true;
            }),

            clnW(m, y -> {//6
                y.x = 73.5f;
                y.y = 69.5f;
                y.otherSide = 7;
            }),
            clnW(m, y -> {//7
                y.x = 84f;
                y.y = 40f;
                y.otherSide = 8;
                y.flipSprite = true;
            }),
            clnW(m, y -> {//8
                y.x = 72.5f;
                y.y = 6.25f;
                y.otherSide = 6;
                y.flipSprite = true;
            }),

            clnW(m, y -> {//9
                y.x = -73.5f;
                y.y = 69.5f;
                y.otherSide = 10;
            }),
            clnW(m, y -> {//10
                y.x = -84f;
                y.y = 40f;
                y.otherSide = 11;
                y.flipSprite = true;
            }),
            clnW(m, y -> {//11
                y.x = -72.5f;
                y.y = 6.25f;
                y.otherSide = 9;
                y.flipSprite = true;
            }),
            new EnergyChargeWeapon(""){
                TextureRegion r;
                TextureRegion[] rs;
                final Color[] colors = {UnityPal.scarColor, UnityPal.endColor, Color.white, Color.black};

                {
                    mirror = false;
                    x = 0f;
                    y = 41.25f;
                    reload = 15f * 60f;
                    continuous = true;
                    shootSound = UnitySounds.thalassophobiaLaser;

                    bullet = new ContinuousSingularityLaserBulletType(3500f){{
                        lifetime = 5f * 60f;
                        width = 40f;
                        widthReduction = 6f;
                        collisionWidth = 17f;
                        accel = 15f;
                        laserSpeed = 45f;
                        pierceAmount = 20;
                        gravityStrength = 20f * 80f;
                        baseTriangleSize = 210f;
                        oscScl = 5f;
                        buildingDamageMultiplier = 0.7f;

                        overDamage = 600000f;
                        overDamagePower = 3f;
                        overDamageScl = 7000f;
                        ratioDamage = 1f / 200f;
                        ratioStart = 20000f;
                        bleedDuration = 600f;

                        hitEffect = HitFx.endHitRedBig;

                        modules = new AntiCheatBulletModule[]{
                        new ArmorDamageModule(0.01f, 3f, 40f, 5f),
                        new AbilityDamageModule(50f, 300f, 20f, 0.002f, 3f)
                        };
                    }};

                    drawCharge = (unit, mount, charge) -> {
                        float len = 32.25f, rr = unit.rotation;
                        float wx = Angles.trnsx(rr, y) + unit.x,
                        wy = Angles.trnsy(rr, y) + unit.y;

                        float tx = Angles.trnsx(rr, len),
                        ty = Angles.trnsy(rr, len);

                        for(int i = 0; i < colors.length; i++){
                            float w = 1f - ((6f * i) / 50f),
                            rx = Mathf.range(charge),
                            ry = Mathf.range(charge),
                            s = Mathf.absin(40f, 5f);

                            Draw.color(colors[i]);
                            Drawf.tri(wx + rx, wy + ry, (35f + s) * w * charge * 2f * 1.22f, charge * charge * w * (160f + Mathf.absin(Time.time + i * 8f, 15f, 12f)), unit.rotation);
                            UnityDrawf.shiningCircle(unit.id, Time.time, wx + rx, wy + ry, (35f + s) * w * charge, 5, 90f, 0.5f, 25f, 15f * charge, 45f);
                        }

                        Draw.color(UnityPal.scarColor);
                        Draw.blend(Blending.additive);

                        Draw.alpha(charge);
                        Draw.rect(r, wx + tx + Mathf.range(charge * 3f), wy + ty + Mathf.range(charge * 3f), rr - 90f);

                        for(int i = 0; i < rs.length; i++){
                            float f = i / (float)rs.length,
                            t = (i + 1f) / rs.length;
                            float a = Mathf.curve(charge, f, t), inv = Interp.pow2In.apply(1f - a) * 3f;
                            Draw.alpha(Mathf.clamp(a * 2f));
                            Draw.rect(rs[i], wx + (tx * (1f + inv)) + Mathf.range(a * 2f),
                            wy + (ty * (1f + inv)) + Mathf.range(a * 2f),
                            rr - 90f);
                        }

                        Draw.blend();
                        Draw.reset();
                    };
                }

                @Override
                public void load(){
                    super.load();
                    r = Core.atlas.find("unity-thalassophobia-cannon");
                    rs = new TextureRegion[4];
                    for(int i = 0; i < 4; i++){
                        rs[i] = Core.atlas.find("unity-thalassophobia-cannon-" + i);
                    }
                }
            }
            );
        }

            @Override
            public void load(){
                super.load();
                softShadowRegion = Core.atlas.find(name + "-soft-shadow");
            }

            @Override
            public void drawSoftShadow(Unit unit){
                Draw.color(0, 0, 0, 1f);
                float rad = 1.6f;
                float size = Math.max(region.width, region.height) * Draw.scl;
                Draw.rect(softShadowRegion, unit, size * rad * Draw.xscl, size * rad * Draw.yscl, unit.rotation - 90f);
                Draw.color();
            }
        };

        //endregion

        charShadowcape = new UnityUnitType("shadowcape"){{
            canBoost = true;

            decorations.add(
            new CapeDecorationType(name + "-cape-1"){{
                x = 8.25f;
                y = 0.5f;
                swayAmount = -30f;
                swaySpeed = 0.5f;
                alphaTo = 0.6f;
                alphaScl = 3f;
                shakeAmount = 1.5f;
                shakeScl = 2f;
            }},

            new CapeDecorationType(name + "-cape-2"){{
                x = 3f;
                y = -7f;
                swayAmount = -15f;
                shakeScl = 3f;
            }}
            );
        }};
    }
}

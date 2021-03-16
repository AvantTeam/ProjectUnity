package unity.content;

import arc.*;
import arc.func.*;
import arc.math.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.gen.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.content.*;
import unity.*;
import unity.ai.*;
import unity.annotations.Annotations.*;
import unity.entities.abilities.*;
import unity.entities.bullet.*;
import unity.entities.comp.*;
import unity.entities.units.*;
import unity.entities.units.EndWormUnit.*;
import unity.gen.*;
import unity.graphics.*;
import unity.type.*;
import unity.util.*;

import static mindustry.Vars.*;

public class UnityUnitTypes implements ContentList{
    private static final Prov<?>[] constructors = new Prov[]{
        WormSegmentUnit::new,
        WormDefaultUnit::new,
        TransUnitWaterMove::new,
        TransLegsUnit::new,
        EndInvisibleUnit::new,
        EndWormUnit::new,
        EndWormSegmentUnit::new,
        EndLegsUnit::new
    };

    private static final int[] classIDs = new int[constructors.length];

    /** Global {@linkplain Copterc copter} units */
    public static @EntityDef(base = UnitEntity.class, def = Copterc.class)
    UnitType caelifera, schistocerca, anthophila, vespula, lepidoptera;

    /** Global {@linkplain UnitEntity flying} units */
    public static @EntityPoint(UnitEntity.class)
    UnitType angel, malakhim;
    
    /** Global {@linkplain LegsUnit legs} units */
    public static @EntityPoint(LegsUnit.class)
    UnitType orion, araneidae, theraphosidae;

    /** Global naval units */
    public static UnitType//@formatter:off
    amphibiNaval, amphibi, craberNaval, craber;
    //formatter:on
    /** Scar {@linkplain LegsUnit legs} units */
    public static @FactionDef("scar") @EntityPoint(LegsUnit.class)
    UnitType hovos, ryzer, zena, sundown, rex, excelsus;

    /** Scar {@linkplain UnitEntity flying} units */
    public static @FactionDef("scar") @EntityPoint(UnitEntity.class)
    UnitType whirlwind, jetstream, vortex;

    /** Imber wyrm units */
    public static @FactionDef("imber")
    UnitType arcnelidia;

    public static @FactionDef("plague")
    UnitType toxobyte, catenapede;
    
    /** Koruh {@linkplain MechUnit mech} units */
    public static @FactionDef("koruh") @EntityPoint(MechUnit.class)
    UnitType buffer, omega;
    
    /** Koruh {@linkplain UnitEntity flying} units */
    public static @FactionDef("koruh") @EntityPoint(UnitEntity.class)
    UnitType cache, dijkstra, phantasm;

    /** Monolith {@linkplain MechUnit mech} units */
    public static @FactionDef("monolith") @EntityPoint(MechUnit.class)
    UnitType stele, pedestal, pilaster;

    /** Monolith {@linkplain LegsUnit legs} units */
    public static @FactionDef("monolith") @EntityPoint(LegsUnit.class)
    UnitType pylon, monument, colossus, bastion;

    public static @FactionDef("koruh") @EntityDef(base = KamiUnit.class, def = Bossc.class)
    UnitType kami;

    public static @FactionDef("end") UnitType opticaecus, devourer, ravager;

    public static @EntityDef(base = UnitEntity.class, def = Extensionc.class)
    UnitType extension;

    public static int getClassId(int index){
        return classIDs[index];
    }

    private static void setEntity(String name, Prov<?> c){
        EntityMapping.nameMap.put(name, c);
    }

    @Override
    public void load(){
        for(int i = 0, j = 0, len = EntityMapping.idMap.length; i < len; i++){
            if(EntityMapping.idMap[i] == null){
                classIDs[j] = i;
                EntityMapping.idMap[i] = constructors[j++];

                if(j >= constructors.length) break;
            }
        }
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
            }}, new Weapon(name + "-launcher"){{
                reload = 30f;
                x = 4.5f;
                y = 0.5f;
                shootY = 2.25f;
                shootSound = Sounds.shootSnap;
                ejectEffect = Fx.casing2;
                bullet = new MissileBulletType(3f, 1f){{
                    speed = 3f;
                    lifetime = 50f;
                    splashDamage = 40f;
                    splashDamageRadius = 6f;
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

            weapons.add(new Weapon(name + "-gun"){{
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
            }}, new Weapon(name + "-gun"){{
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
            }}, new Weapon(name + "-gun-big"){{
                x = 6.75f;
                y = 5.75f;
                shootX = -0.5f;
                shootY = 2f;
                shootSound = Sounds.shootBig;
                ejectEffect = Fx.casing2;
                reload = 30f;
                bullet = Bullets.standardIncendiaryBig;
            }});

            for(int i : Mathf.signs){
                rotors.add(new Rotor(name + "-rotor"){{
                    x = 0f;
                    y = 6.5f;
                    bladeCount = 3;
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

            weapons.add(new Weapon(name + "-gun"){{
                x = 4.25f;
                y = 14f;
                shootX = -1f;
                shootY = 2.75f;
                reload = 15;
                shootSound = Sounds.shootBig;
                bullet = Bullets.standardThoriumBig;
            }});

            weapons.add(new Weapon(name + "-tesla"){{
                x = 7.75f;
                y = 8.25f;
                shootY = 5.25f;
                reload = 30f;
                shots = 5;
                shootSound = Sounds.spark;
                bullet = new LightningBulletType(){{
                    damage = 15f;
                }};
            }});

            rotors.add(new Rotor(name + "-rotor"){{
                x = 0f;
                y = -13f;
                scale = 0.6f;
            }}, new Rotor(name + "-rotor"){{
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

            weapons.add(new Weapon(name + "-gun-big"){{
                x = 8.25f;
                y = 9.5f;
                shootX = -1f;
                shootY = 7.25f;
                reload = 12f;
                shootSound = Sounds.shootBig;
                bullet = Bullets.standardDenseBig;
            }}, new Weapon(name + "-gun"){{
                x = 6.5f;
                y = 21.5f;
                shootX = -0.25f;
                shootY = 5.75f;
                reload = 20f;
                shots = 4;
                shotDelay = 2f;
                shootSound = Sounds.shootSnap;
                bullet = Bullets.standardThorium;
            }}, new Weapon(name + "-laser-gun"){{
                x = 13.5f;
                y = 15.5f;
                shootY = 4.5f;
                reload = 60f;
                shootSound = Sounds.laser;
                bullet = new LaserBulletType(240f){
                    {
                        sideAngle = 45f;
                        length = 200f;
                    }
                };
            }});

            for(int i : Mathf.signs){
                rotors.add(new Rotor(name + "-rotor"){{
                    mirror = true;
                    x = 15f;
                    y = 6.75f;
                    speed = 29f * i;
                    rotOffset = 180f * i;
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

            weapons.add(new Weapon(name + "-gun"){{
                x = 14f;
                y = 27f;
                shootY = 5.5f;
                shootSound = Sounds.shootBig;
                ejectEffect = Fx.casing3Double;
                reload = 10f;
                bullet = Bullets.standardThoriumBig;
            }}, new Weapon(name + "-launcher"){{
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
                    trailColor = UnityPal.monolithDarker;
                    weaveScale = 2f;
                    weaveMag = -2f;
                    lifetime = 50f;
                    drag = -0.01f;
                    splashDamage = 48f;
                    splashDamageRadius = 12f;
                    frontColor = UnityPal.monolithLighter;
                    backColor = UnityPal.monolithDarker;
                }};
            }}, new Weapon(name + "-gun-big"){{
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
                    toColor = UnityPal.monolithLighter;
                    damage = 150f;
                    keepVelocity = false;
                    length = 150f;
                }};
            }});

            for(int i : Mathf.signs){
                rotors.add(new Rotor(name + "-rotor"){{
                    mirror = true;
                    x = 22.5f;
                    y = 21.25f;
                    bladeCount = 3;
                    speed = 19f * i;
                }}, new Rotor(name + "-rotor"){{
                    mirror = true;
                    x = 17.25f;
                    y = 1f;
                    scale = 0.8f;
                    bladeCount = 2;
                    speed = 23f * i;
                }});
            }
        }};

        angel = new UnityUnitType("angel"){{
            defaultController = UnitHealerAI::new;
            buildSpeed = 10f;
            health = 90;
            engineOffset = 5.7f;
            flying = true;
            speed = 4.3f;
            accel = 0.08f;
            drag = 0.01f;
            range = 40f;
            commandLimit = 0;
            ammoType = AmmoTypes.power;
            hitSize = 9f;
        }};

        malakhim = new UnityUnitType("malakhim"){{
            defaultController = UnitHealerAI::new;
            buildSpeed = 15f;
            health = 170;
            engineOffset = 11.7f;
            flying = true;
            speed = 3.9f;
            accel = 0.08f;
            drag = 0.01f;
            range = 50f;
            commandLimit = 2;
            ammoType = AmmoTypes.power;
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

        //endregion
        //region ground-units
        
        orion = new UnityUnitType("orion"){{
            speed = 0.3f;
            health = 20000;
            mechFrontSway = 1.9f;
            mechSideSway = 0.6f;
            hitSize = 31f;
            weapons.add(new Weapon(){{
                reload = 300f;
                ejectEffect = Fx.none;
                shootCone = 10f;
                shootSound = Sounds.laserblast;
                chargeSound = Sounds.lasercharge;
                firstShotDelay = Fx.greenLaserCharge.lifetime;
                soundPitchMin = 1f;
                mirror = false;
                top = false;
                x = 0f;
                shake = 14f;
                recoil = 5f;
                shots = 1;
                allowLegStep = true;
                shootStatus = StatusEffects.unmoving;
                shootStatusDuration = 140f;
                bullet = new BallLightningBulletType(0.5f, 20f, "unity-lightning-ball"){{
                    frontColor = Color.white;
                    backColor = ballLightningColor = Pal.heal;
                    lifetime = 300f;

                    width = height = 24f;
                    damageRadius = 12f;

                    shootEffect = Fx.greenLaserCharge;
                    hitEffect = Fx.none;
                    despawnEffect = Fx.none;
                    
                    ballLightnings = 5;
                    ballLightningInaccuracy = 30f;
                    ballLightningRange = 80f;
                    ballLightningDamage = 25f;
                    ballLightningLength = 12;
                    ballLightningLengthRand = 4;

                    fragBullets = 3;
                    fragCone = 45f;
                    fragVelocityMin = 1f;
                    fragBullet  = new BallLightningBulletType(0.5f, 5f, "unity-lightning-ball"){{
                        frontColor = Color.white;
                        backColor = ballLightningColor = Pal.heal;
                        lifetime = 300f;

                        width = height = 12f;
                        damageRadius = 6f;
                        
                        hitEffect = Fx.none;
                        despawnEffect = Fx.none;
                        
                        ballLightningRange = 50f;
                        ballLightningDamage = 20f;
                        ballLightningLength = 8;
                        ballLightningLengthRand = 2;
                    }};
                }};
            }});
        }};

        araneidae = new UnityUnitType("araneidae"){{
            groundLayer = Layer.legUnit + 3f;
            drag = 0.1f;
            speed = 0.4f;
            hitSize = 34f;
            health = 20000;
            
            legCount = 12;
            legMoveSpace = 1f;
            legPairOffset = 0.7f;
            legGroupSize = 3;
            legLength = 77;
            legExtension = -12f;
            legBaseOffset = 22f;
            landShake = 2.4f;
            legLengthScl = 1f;
            kinematicScl = 0.7f;
            rippleScale = 2f;
            legSpeed = 0.2f;

            legSplashDamage = 80f;
            legSplashRange = 40f;
            hovering = true;

            armor = 13f;
            allowLegStep = true;
            visualElevation = 0.7f;
            weapons.add(new Weapon(name + "-sapper"){{
                reload = 50f;
                x = 13f;
                y = -17f;
                shootY = 5f;
                rotate = true;
                shake = 1f;
                rotateSpeed = 1f;
                shots = 5;
                shotDelay = 6f;
                shootSound = Sounds.artillery;
                bullet = new ArtilleryBulletType(2.5f, 1f){{
                    hitEffect = Fx.sapExplosion;
                    knockback = 0.8f;
                    speed = 2.5f;
                    lifetime = 70f;
                    width = height = 19f;
                    ammoMultiplier = 4f;
                    splashDamageRadius = 95f;
                    splashDamage = 55f;
                    backColor = Pal.sapBulletBack;
                    frontColor = lightningColor = Pal.sapBullet;
                    lightning = 3;
                    lightningLength = 10;
                    smokeEffect = Fx.shootBigSmoke2;
                    shake = 5f;
                    status = StatusEffects.sapped;
                    statusDuration = 60f * 10f;
                }};
            }}, new Weapon("mount-purple-weapon"){{
                reload = 20f;
                x = 25f;
                y = 10f;
                rotate = true;
                shake = 1f;
                rotateSpeed = 5f;
                shootSound = Sounds.flame;
                alternate = false;
                bullet = new SapBulletType(){{
                    sapStrength = 0.8f;
                    length = 90f;
                    damage = 25f;
                    shootEffect = Fx.shootSmall;
                    hitColor = color = UnityPal.purpleLightning;
                    width = 0.7f;
                    lifetime = 35f;
                    knockback = -1.5f;
                }};
            }});
            Weapon weap3 = weapons.get(1).copy();
            weap3.x = 20f;
            weap3.y = 13f;
            weapons.insert(2, weap3);
            Weapon weap4 = weapons.get(2).copy();
            weap4.reload = 23f;
            weap4.x = 15f;
            weap4.y = 18f;
            weap4.rotateSpeed = 3f;
            weap4.bullet = new SapBulletType(){{
                sapStrength = 0.9f;
                length = 90f;
                damage = 30f;
                shootEffect = Fx.shootSmall;
                hitColor = color = UnityPal.purpleLightning;
                width = 0.75f;
                lifetime = 35f;
                knockback = -1.5f;
            }};
            weapons.insert(3, weap4);
            Weapon weap5 = weap4.copy();
            weap5.x = 25f;
            weap5.y = 5f;
            weapons.insert(4, weap5);
        }};

        theraphosidae = new UnityUnitType("theraphosidae"){{
            speed = 0.4f;
            drag = 0.12f;
            hitSize = 29f;
            hovering = true;
            allowLegStep = true;
            health = 31000;
            armor = 16f;
            rotateSpeed = 1.3f;
            legCount = 16;
            legGroupSize = 4;
            legMoveSpace = 0.4f;
            legPairOffset = 0.4f;
            legLength = 121f;
            legExtension = -13.5f;
            kinematicScl = 0.8f;
            legBaseOffset = 9f;
            legSpeed = 0.092f;
            visualElevation = 1f;
            groundLayer = 79f;
            rippleScale = 3.4f;
            legSplashDamage = 130f;
            legSplashRange = 60f;
            targetAir = false;
            commandLimit = 5;
            weapons.add(new Weapon(name + "-cannon"){{
                x = 32.5f;
                y = -1.75f;
                shootX = -7.5f;
                shootY = 30.25f;
                inaccuracy = 7.3f;
                velocityRnd = 0.1f;
                shots = 4;
                shotDelay = 7f;
                shootSound = Sounds.artillery;
                rotate = false;
                reload = 130f;
                shake = 6f;
                recoil = 5f;
                bullet = new ArtilleryBulletType(3.5f, 45f){
                    @Override
                    public void update(Bullet b){
                        super.update(b);
                        if(Mathf.chanceDelta(0.3f)) Lightning.create(b, UnityPal.purpleLightning, 43f, b.x, b.y, Mathf.range(56f) + b.rotation(), 8);
                    }

                    {
                        lifetime = 85f;
                        collides = true;
                        collidesTiles = true;
                        splashDamageRadius = 90f;
                        splashDamage = 50f;
                        width = height = 27f;
                        ammoMultiplier = 3f;
                        knockback = 0.9f;
                        hitShake = 7f;
                        status = StatusEffects.sapped;
                        statusDuration = 60f * 10f;
                        smokeEffect = Fx.shootBigSmoke2;
                        backColor = Pal.sapBulletBack;
                        frontColor = lightningColor = Pal.sapBullet;
                        lightning = 6;
                        lightningLength = 23;
                        fragLifeMin = 0.3f;
                        fragBullets = 13;
                        fragBullet = new ArtilleryBulletType(2.5f, 23f){{
                            lifetime = 82f;
                            splashDamageRadius = 40f;
                            splashDamage = 20f;
                            width = height = 20f;
                            hitShake = 4f;
                            status = StatusEffects.sapped;
                            statusDuration = 60f * 10f;
                            smokeEffect = Fx.shootBigSmoke2;
                            backColor = Pal.sapBulletBack;
                            frontColor = lightningColor = Pal.sapBullet;
                            lightning = 3;
                            lightningLength = 6;
                        }};
                    }
                };
            }}, new Weapon(name + "-main-laser"){{
                x = 18.75f;
                y = -11f;
                shootY = 19f;
                rotateSpeed = 1f;
                shootSound = Sounds.laser;
                reload = 60f;
                recoil = 4f;
                rotate = true;
                shadow = 15f;
                shake = 4f;
                bullet = new LaserBulletType(325f){
                    @Override
                    public void hit(Bullet b, float x, float y){
                        super.hit(b, x, y);
                        if(Mathf.chance(0.4f)) Lightning.create(b, UnityPal.purpleLightning, 34f, x, y, Mathf.range(30f) + b.rotation(), 12);
                    }

                    {
                        colors = new Color[]{Color.valueOf("a96bfa80"), UnityPal.purpleLightning, Color.white};
                        length = 290f;
                        ammoMultiplier = 4f;
                        width = 43f;
                        sideLength = 45f;
                        drawSize = length * 2f + 20f;
                    }
                };
            }}, new Weapon(name + "-main-sapper"){
                {
                    x = -17f;
                    y = -18.5f;
                    shootY = 8f;
                    shootSound = Sounds.laser;
                    reload = 30f;
                    rotate = true;
                    flipSprite = true;
                    bullet = new LaserBulletType(98f){
                        @Override
                        public void hit(Bullet b, float x, float y){
                            super.hit(b, x, y);
                            if(Mathf.chance(0.3f)) Lightning.create(b, UnityPal.purpleLightning, 12f, x, y, Mathf.range(30f) + b.rotation(), 7);
                        }

                        {
                            colors = new Color[]{Color.valueOf("a96bfa80"), UnityPal.purpleLightning, Color.white};
                            length = 195f;
                            ammoMultiplier = 6f;
                            width = 19f;
                            drawSize = length * 2f + 20f;
                        }
                    };
                }
            });
            weapons.add(weapons.get(2).copy());
            Weapon temp = weapons.get(3);
            temp.x = 10.25f;
            temp.y = -23.25f;
            temp.flipSprite = false;
        }};

        //endregion
        //region naval-units

        setEntity("amphibi-naval", TransUnitWaterMove::new);
        amphibiNaval = new UnityUnitType("amphibi-naval"){
            {
                toTrans = () -> amphibi;
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

        setEntity("amphibi", TransLegsUnit::new);
        amphibi = new UnityUnitType("amphibi"){{
            toTrans = () -> amphibiNaval;
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

        setEntity("craber-naval", TransUnitWaterMove::new);
        craberNaval = new UnityUnitType("craber-naval"){
            {
                toTrans = () -> craber;
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

        setEntity("craber", TransLegsUnit::new);
        craber = new UnityUnitType("craber"){{
            toTrans = () -> craberNaval;
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
                    shootEffect = UnityFx.scarRailShoot;
                    pierceEffect = UnityFx.scarRailHit;
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
                    shootEffect = UnityFx.scarRailShoot;
                    pierceEffect = UnityFx.scarRailHit;
                    updateEffect = UnityFx.scarRailTrail;
                    hitEffect = Fx.massiveExplosion;
                    pierceDamageFactor = 0.3f;
                }};
            }}, new Weapon("unity-scar-missile-launcher"){
                {
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
                }
            });
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
                        shootEffect = UnityFx.scarRailShoot;
                        pierceEffect = UnityFx.scarRailHit;
                        updateEffect = UnityFx.scarRailTrail;
                        hitEffect = Fx.massiveExplosion;
                        pierceDamageFactor = 0.2f;
                    }};
                }},
                new Weapon(){{
                    x = 10.25f;
                    y = 2f;
                    rotate = false;
                    shake = 1.1f;
                    reload = 2.25f * 70f;

                    bullet = new RailBulletType(){{
                        damage = 230f;
                        length = 40f * 7f;
                        updateEffectSeg = 40f;
                        shootEffect = UnityFx.scarRailShoot;
                        pierceEffect = UnityFx.scarRailHit;
                        updateEffect = UnityFx.scarRailTrail;
                        hitEffect = Fx.massiveExplosion;
                        pierceDamageFactor = 0.5f;
                    }};
                }},
                new Weapon("unity-scar-missile-launcher"){{
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

            weapons.add(
                new Weapon("unity-scar-large-launcher"){{
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
                }},
                new Weapon("unity-scar-railgun"){{
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
                        shootEffect = UnityFx.scarRailShoot;
                        pierceEffect = UnityFx.scarRailHit;
                        updateEffect = UnityFx.scarRailTrail;
                        hitEffect = Fx.massiveExplosion;
                        pierceDamageFactor = 0.2f;
                    }};
                }}
            );

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

            weapons.add(new Weapon("unity-rex-railgun"){{
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
                        shootEffect = UnityFx.scarRailShoot;
                        pierceEffect = UnityFx.scarRailHit;
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
            }},
            new Weapon("unity-scar-large-launcher"){{
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
            }},
            new Weapon("unity-scar-large-launcher"){{
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
            }},
            new Weapon("unity-scar-large-launcher"){{
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
            }},
            new Weapon("unity-scar-large-launcher"){{
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
            }},
            new Weapon("unity-scar-small-laser-weapon"){{
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
                    hitEffect = UnityFx.scarHitSmall;
                }};
            }},
            new Weapon("unity-excelsus-laser-weapon"){{
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
                    hitEffect = UnityFx.scarHitSmall;
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
                    hitEffect = UnityFx.coloredHitSmall;
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
                    hitEffect = UnityFx.coloredHitSmall;
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
                    hitEffect = UnityFx.coloredHitSmall;
                    lightColor = hitColor = UnityPal.scarColorAlpha;
                    colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.endColor, Color.white};
                    strokes = new float[]{1.5f, 1f, 0.3f};
                }};

                reload = 2f * 60f;
            }});
        }};

        //endregion
        //region imber

        setEntity("arcnelidia", WormDefaultUnit::new);
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

        //endregion
        //region plague

        setEntity("toxobyte", WormDefaultUnit::new);
        toxobyte = new UnityUnitType("toxobyte"){{
            defaultController = WormAI::new;
            flying = true;
            health = 2000f;
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

        setEntity("catenapede", WormDefaultUnit::new);
        catenapede = new UnityUnitType("catenapede"){{
            defaultController = WormAI::new;
            flying = true;
            health = 7500f;
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

            weapons.add(new Weapon("unity-dijkstra-laser"){{
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
            }}, new Weapon("unity-dijkstra-plasmagun"){{
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

            abilities.add(new TeleportAbility<Bullet>(unit -> {
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

        stele = new UnityUnitType("stele"){{
            health = 150f;
            speed = 0.6f;
            hitSize = 8f;

            canBoost = true;
            boostMultiplier = 2.5f;
            outlineColor = UnityPal.darkOutline;

            weapons.add(
            new Weapon(name + "-shotgun"){{
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

                bullet = new BasicBulletType(3.5f, 3f){
                    {
                        lifetime = 60f;
                        width = height = 2f;
                        weaveScale = 3f;
                        weaveMag = 5f;
                        homingPower = 0.7f;

                        shootEffect = Fx.hitLancer;
                        frontColor = Pal.lancerLaser;
                        backColor = Pal.lancerLaser.cpy().mul(0.7f);
                    }

                    @Override
                    public void init(Bullet b){
                        b.data = new Trail(6);
                    }

                    @Override
                    public void draw(Bullet b){
                        if(b.data instanceof Trail t){
                            t.draw(frontColor, width);

                            Draw.color(frontColor);
                            Fill.circle(b.x, b.y, width);
                            Draw.color();
                        }
                    }

                    @Override
                    public void update(Bullet b){
                        super.update(b);
                        if(b.data instanceof Trail t){
                            t.update(b.x, b.y);
                        }
                    }
                };
            }}
            );
        }};

        pedestal = new UnityUnitType("pedestal"){{
            health = 600;
            speed = 0.5f;
            rotateSpeed = 2.6f;
            hitSize = 11f;
            armor = 3.5f;
            singleTarget = true;

            canBoost = true;
            boostMultiplier = 2.5f;
            engineSize = 3.5f;
            engineOffset = 6f;
            outlineColor = UnityPal.darkOutline;

            weapons.add(
            new Weapon(name + "-gun"){{
                top = false;
                x = 10.75f;
                y = 2.25f;

                reload = 40f;
                recoil = 3.2f;
                shootSound = UnitySounds.energyBolt;

                BulletType subBullet = new LightningBulletType();
                subBullet.damage = 12f;

                bullet = new BasicBulletType(3f, 36f, "shell"){
                    {
                        width = 20f;
                        height = 20f;
                        lifetime = 60f;
                        frontColor = Pal.lancerLaser;
                        backColor = Pal.lancerLaser.cpy().mul(0.6f);
                        shootEffect = Fx.lightningShoot;
                    }

                    @Override
                    public void init(Bullet b){
                        for(int i = 0; i < 3; i++){
                            subBullet.create(b, b.x, b.y, b.vel.angle());
                            Sounds.spark.at(b.x, b.y, Mathf.random(0.6f, 0.8f));
                        }
                    }
                };
            }}
            );
        }};

        pilaster = new UnityUnitType("pilaster"){{
            health = 1000f;
            speed = 0.4f;
            rotateSpeed = 2.2f;
            hitSize = 26.5f;
            armor = 4f;
            mechFrontSway = 0.55f;

            canBoost = true;
            boostMultiplier = 2.5f;
            engineSize = 5f;
            engineOffset = 10f;

            ammoType = AmmoTypes.power;
            outlineColor = UnityPal.darkOutline;

            weapons.add(
            new Weapon("unity-monolith-medium-weapon-mount"){{
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
                    damage = 15f;
                    lightningLength = 15;
                }};
            }},
            new Weapon("unity-monolith-large-weapon-mount"){{
                top = false;
                x = 13f;
                y = 2f;
                shootY = 10.5f;

                rotate = true;
                rotateSpeed = 10f;
                recoil = 3f;
                reload = 40f;
                shootSound = Sounds.laser;

                bullet = new LaserBulletType(80f);
            }}
            );
        }};

        pylon = new UnityUnitType("pylon"){{
            health = 7200f;
            speed = 0.43f;
            rotateSpeed = 1.48f;
            hitSize = 36f;
            armor = 7f;
            commandLimit = 8;

            allowLegStep = hovering = true;
            visualElevation = 0.2f;
            legCount = 4;
            legExtension = 8f;
            legSpeed = 0.08f;
            legLength = 16f;
            legMoveSpace = 1.2f;
            legTrns = 0.5f;
            legBaseOffset = 11f;

            ammoType = AmmoTypes.powerHigh;
            groundLayer = Layer.legUnit;
            outlineColor = UnityPal.darkOutline;

            weapons.add(
            new Weapon("unity-pylon-laser"){{
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
            }},
            new Weapon("unity-monolith-large2-weapon-mount"){{
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
            }}
            );
        }};

        monument = new UnityUnitType("monument"){{
            health = 16000f;
            speed = 0.42f;
            rotateSpeed = 1.4f;
            hitSize = 48f;
            armor = 9f;
            commandLimit = 8;

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

            ammoType = AmmoTypes.powerHigh;
            groundLayer = Layer.legUnit;
            outlineColor = UnityPal.darkOutline;

            BulletType laser = new LaserBulletType(320f);
            weapons.add(
            new Weapon("unity-monolith-large2-weapon-mount"){{
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
            }},
            new Weapon("unity-monolith-large2-weapon-mount"){{
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
            }},
            new Weapon("unity-monolith-railgun-big"){{
                mirror = false;
                x = 0f;
                y = 12f;
                shootY = 35f;
                shadow = 30f;

                rotate = true;
                rotateSpeed = 1.2f;
                reload = 240f;
                recoil = shake = 8f;
                shootCone = 2f;
                cooldownTime = 210f;
                shootSound = Sounds.railgun;

                bullet = UnityBullets.monumentRailBullet;
            }}
            );
        }};

        colossus = new UnityUnitType("colossus"){{
            health = 30000f;
            speed = 0.4f;
            rotateSpeed = 1.2f;
            hitSize = 64f;
            armor = 13f;
            commandLimit = 8;

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

            ammoType = AmmoTypes.powerHigh;
            groundLayer = Layer.legUnit;
            outlineColor = UnityPal.darkOutline;

            abilities.add(new LightningSpawnAbility(8, 48f, 2f, 0.05f, 120f, 56f, 100f));

            weapons.add(
            new Weapon(name + "-weapon"){{
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

                bullet = new LaserBulletType(960f){{
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
            }}
            );
        }};

        bastion = new UnityUnitType("bastion"){{
            health = 45000f;
            speed = 0.4f;
            rotateSpeed = 1.2f;
            hitSize = 67f;
            armor = 17f;
            commandLimit = 8;

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

            ammoType = AmmoTypes.powerHigh;
            groundLayer = Layer.legUnit;
            outlineColor = UnityPal.darkOutline;

            abilities.add(new LightningSpawnAbility(12, 24f, 3f, 0.05f, 160f, 96f, 200f));

            BulletType energy = new BasicBulletType(5f, 20f, "shell"){
                {
                    width = 9f;
                    height = 11f;
                    shrinkY = 0.3f;
                    lifetime = 30f;
                    weaveScale = weaveMag = 3f;

                    frontColor = Color.white;
                    backColor = Pal.lancerLaser;
                    shootEffect = Fx.lancerLaserShoot;
                    smokeEffect = Fx.hitLancer;
                    hitEffect = Fx.flakExplosion;

                    splashDamage = 60f;
                    splashDamageRadius = 10f;

                    lightning = 3;
                    lightningDamage = 12f;
                    lightningColor = Pal.lancerLaser;
                    lightningLength = 6;
                }

                @Override
                public void init(Bullet b){
                    b.data = new Trail(6);
                }

                @Override
                public void update(Bullet b){
                    super.update(b);
                    if(b.data instanceof Trail t){
                        t.update(b.x, b.y);
                    }
                }

                @Override
                public void draw(Bullet b){
                    if(b.data instanceof Trail t){
                        t.draw(backColor, width * 0.4f);
                    }

                    super.draw(b);
                }
            };

            weapons.add(
            new Weapon(name + "-mount"){{
                x = 9f;
                y = -11.5f;
                shootY = 10f;

                rotate = true;
                rotateSpeed = 8f;

                reload = 24f;
                recoil = 6f;
                shootSound = UnitySounds.energyBolt;

                bullet = energy;
            }},
            new Weapon(name + "-mount"){{
                x = 23.5f;
                y = 5.5f;
                shootY = 10f;

                rotate = true;
                rotateSpeed = 8f;

                reload = 15f;
                recoil = 6f;
                shootSound = UnitySounds.energyBolt;

                bullet = energy;
            }},
            new Weapon(name + "-gun"){{
                x = 12.5f;
                y = 12f;
                shootY = 13.5f;

                rotate = true;
                rotateSpeed = 6f;
                shots = 5;
                shotDelay = 3f;

                reload = 30f;
                recoil = 8f;
                shootSound = Sounds.shootBig;

                bullet = new BasicBulletType(12.5f, 320f, "shell"){
                    {
                        width = 14f;
                        height = 25f;
                        shrinkY = 0.2f;
                        lifetime = 24f;

                        frontColor = Color.white;
                        backColor = Pal.lancerLaser;
                        shootEffect = Fx.lancerLaserShoot;
                        smokeEffect = hitEffect = Fx.hitLancer;

                        lightning = 3;
                        lightningDamage = 12f;
                        lightningColor = Pal.lancerLaser;
                        lightningLength = 6;
                    }

                    @Override
                    public void update(Bullet b){
                        super.update(b);
                        if(Mathf.chanceDelta(0.5f)){
                            Lightning.create(b, backColor, lightningDamage, x, y, b.rotation(), lightningLength);
                        }
                    }
                };
            }}
            );
        }};

        kami = new RainbowUnitType("kami-mkii"){{
            defaultController = EmptyAI::new;
            health = 120000f;
            speed = 15f;
            hitSize = 36f;
            flying = true;
            drawCell = false;

            Unity.musicHandler.registerLoop(name, UnityMusics.radiantDischargeIntro, UnityMusics.radiantDischargeLoop);
        }};

        //endregion
        //region dark

        setEntity("opticaecus", EndInvisibleUnit::new);
        opticaecus = new InvisibleUnitType("opticaecus"){{
            health = 60000f;
            speed = 1.8f;
            drag = 0.02f;
            hitSize = 60.5f;
            flying = true;
            lowAltitude = true;
            circleTarget = false;
            engineOffset = 40f;
            engineSize = 6f;
            outlineColor = UnityPal.darkerOutline;

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
                }};
            }}, new Weapon("unity-doeg-launcher"){{
                x = 24.75f;
                mirror = true;
                rotate = true;
                reload = 1.2f * 60f;
                inaccuracy = 20f;
                shotDelay = 2f;
                shots = 10;

                bullet = new MissileBulletType(6f, 220f){{
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
                }};
            }});
        }};

        setEntity("devourer-of-eldrich-gods", EndWormUnit::new);
        devourer = new UnityUnitType("devourer-of-eldrich-gods"){{
            health = 1250000f;
            flying = true;
            speed = 4f;
            accel = 0.053f;
            drag = 0.012f;
            defaultController = WormAI::new;
            circleTarget = counterDrag = true;
            rotateShooting = faceTarget = false;
            splittable = chainable = false;
            hitSize = 41f * 1.55f;
            segmentOffset = (41f * 1.55f) + 1f;
            segmentLength = 45;
            lowAltitude = true;
            visualElevation = 2f;
            rotateSpeed = 3.2f;
            engineSize = -1f;
            range = 480f;
            armor = 16f;
            omniMovement = false;
            outlineColor = UnityPal.darkerOutline;

            BulletType t = new AntiCheatBasicBulletType(9.2f, 430f){{
                hitSize = 8f;
                shrinkY = 0f;
                width = 19f;
                height = 25f;
                otherAntiCheatScl = 4.75f;
                priority = 1;
                backColor = hitColor = lightColor = UnityPal.scarColor;
                frontColor = UnityPal.endColor;
            }};

            weapons.add(new Weapon(){{
                mirror = false;
                ignoreRotation = true;
                x = 0f;
                y = 18f;
                reload = 15f * 60f;
                continuous = true;
                shake = 4f;
                firstShotDelay = 41f;
                chargeSound = UnitySounds.devourerMainLaser;
                shootSound = UnitySounds.continuousLaserA;
                bullet = new SparkingContinuousLaserBulletType(2400f){{
                    length = 340f;
                    lifetime = 5f * 60f;
                    incendChance = -1f;
                    fromBlockAmount = 1;
                    fromBlockChance = 0.4f;
                    fromBlockDamage = 80f;
                    shootEffect = UnityFx.devourerShootEffect;
                    keepVelocity = true;
                    lightColor = lightningColor = hitColor = UnityPal.scarColor;
                    colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};
                }

                    @Override
                    public void hitEntity(Bullet b, Hitboxc other, float initialHealth){
                        super.hitEntity(b, other, initialHealth);
                        if(other instanceof Unit unit){
                            float threat = unit.maxHealth + unit.type.dpsEstimate;
                            float extraDamage = (float)Math.pow(Mathf.clamp((unit.maxHealth + unit.type.dpsEstimate - 43000f) / 14000f, 0f, 8f), 2f);
                            float trueDamage = damage + Mathf.clamp((unit.maxHealth + unit.type.dpsEstimate - 32000f) / 2f, 0f, 90000000f);
                            trueDamage += extraDamage * (damage / 3f);

                            if(!(other instanceof AntiCheatBase)) unit.damage(trueDamage);
                            if((Float.isInfinite(threat) || Float.isNaN(threat) || threat >= Float.MAX_VALUE) && !(other instanceof AntiCheatBase)) UnityAntiCheat.annihilateEntity(other, false);
                        }
                        if(other instanceof AntiCheatBase) ((AntiCheatBase)other).overrideAntiCheatDamage(damage * 4f, 2);
                    }
                };
            }}, new Weapon("unity-doeg-destroyer"){{
                mirror = true;
                ignoreRotation = true;
                rotate = true;
                x = 22f;
                y = -17.75f;
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

                bullet = new AntiCheatBasicBulletType(6f, 120f, "missile"){{
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
                    backColor = trailColor = hitColor = lightColor = UnityPal.scarColor;
                    frontColor = UnityPal.endColor;
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
                shootSound = Sounds.beam;
                continuous = true;

                bullet = new ContinuousLaserBulletType(85f){{
                    lifetime = 2f * 60;
                    length = 230f;
                    for(int i = 0; i < strokes.length; i++){
                        strokes[i] *= 0.4f;
                    }
                    colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};
                }

                    @Override
                    public void hitEntity(Bullet b, Hitboxc other, float initialHealth){
                        super.hitEntity(b, other, initialHealth);
                        if(other instanceof Unit unit){
                            for(Ability ability : unit.abilities){
                                if(ability instanceof ForceFieldAbility force){
                                    if(force.max >= 10000){
                                        force.max -= force.max / 35f;
                                        unit.shield = Math.min(force.max, unit.shield);
                                    }
                                    if(force.radius > unit.hitSize * 4f){
                                        force.radius -= force.radius / 20f;
                                    }
                                    if(force.regen > 2700f / 5f) force.regen /= 1.2f;
                                    continue;
                                }
                                if(ability instanceof RepairFieldAbility repair){
                                    if(repair.amount > unit.maxHealth / 7f) repair.amount *= 0.9f;
                                    continue;
                                }
                                if(ability instanceof StatusFieldAbility status){
                                    if((status.effect.damage < -unit.maxHealth / 20f || status.effect.reloadMultiplier > 8f) && status.duration > 20f){
                                        statusDuration -= statusDuration / 15f;
                                    }
                                }
                            }
                            unit.shield -= damage * 0.4f;
                            if(unit.armor > unit.hitSize) unit.armor -= Math.max(damage, unit.armor / 20f);
                        }
                        if(other instanceof AntiCheatBase) ((AntiCheatBase)other).overrideAntiCheatDamage(damage * 6f, 3);
                    }
                };
            }});
        }
            @Override
            public void init(){
                super.init();
                immunities.addAll(content.getBy(ContentType.status));
            }
        };

        setEntity("ravager", EndLegsUnit::new);
        ravager = new UnityUnitType("ravager"){{
            health = 1650000f;
            speed = 0.65f;
            drag = 0.16f;
            armor = 15f;
            hitSize = 138f;
            rotateSpeed = 1.1f;

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

            legSplashRange = 120f;
            legSplashDamage = 1600f;
            outlineColor = UnityPal.darkerOutline;

            bottomWeapons.add("unity-ravager-nightmare");
            weapons.addAll(new Weapon("unity-ravager-nightmare"){{
                x = 80.25f;
                y = -7.75f;
                shootY = 75f;
                reload = 6f * 60f;
                recoil = 8f;
                alternate = true;
                rotate = false;
                shootSound = UnitySounds.ravagerNightmareShoot;
                bullet = UnityBullets.ravagerLaser;
            }}, new Weapon("unity-ravager-artillery"){{
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
            }}, new Weapon("unity-ravager-artillery"){{
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
            }}, new Weapon("unity-ravager-small-turret"){{
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
            }}, new Weapon("unity-ravager-small-turret"){{
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
        }
            @Override
            public void init(){
                super.init();
                immunities.addAll(content.getBy(ContentType.status));
            }
        };

        extension = new UnityUnitType("extension"){
            {
                speed = 0f;
                hitSize = 0f;
                health = 1;
                rotateSpeed = 360f;
                itemCapacity = 0;
                commandLimit = 0;
            }

            @Override
            public boolean isHidden(){
                return true;
            }
        };

        //endregion
    }
}

package unity.content;

import arc.func.*;
import arc.math.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.gen.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.content.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitFactory.*;
import unity.ai.*;
import unity.annotations.Annotations.*;
import unity.entities.abilities.*;
import unity.entities.bullet.*;
import unity.entities.comp.Copterc;
import unity.entities.units.*;
import unity.graphics.*;
import unity.type.*;

import static mindustry.type.ItemStack.*;

public class UnityUnitTypes implements ContentList{
    private static final Prov<?>[] constructors = new Prov[]{
        WormSegmentUnit::new,
        WormDefaultUnit::new,
        TransUnitWaterMove::new,
        TransLegsUnit::new
    };

    private static final int[] classIDs = new int[constructors.length];
    //moved to here to not confuse load order. if you want to move somewhere, change load order too.
    public static @EntityDef(base = UnitEntity.class, def = {Copterc.class})
    UnitType caelifera, schistocerca, anthophila, vespula, lepidoptera;

    public static UnitType//@formatter:off
    //flying-units
    angel, malakhim,
    //ground-units  @formatter:on
    arcaetana, projectSpiboss,
    //naval-units
    rexed, storm, amphibiNaval, amphibi, craberNaval, craber;

    public static @FactionDef(type = "scar")
    UnitType hovos, ryzer, sundown, whirlwind, jetstream, vortex;

    public static @FactionDef(type = "imber")
    UnitType arcnelidia;

    public static @FactionDef(type = "monolith")
    UnitType stele, pedestal, pilaster, pylon, monument, colossus;

    //public static @FactionDef(type = "end") UnitType devourer;

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
                reload = 15;
                shootSound = Sounds.shootBig;
                bullet = Bullets.standardThoriumBig;
            }});

            weapons.add(new Weapon(name + "-tesla"){{
                x = 7.75f;
                y = 8.25f;
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
                reload = 12f;
                shootSound = Sounds.shootBig;
                bullet = Bullets.standardDenseBig;
            }}, new Weapon(name + "-gun"){{
                x = 6.5f;
                y = 21.5f;
                reload = 20f;
                shots = 4;
                shotDelay = 2f;
                shootSound = Sounds.shootSnap;
                bullet = Bullets.standardThorium;
            }}, new Weapon(name + "-laser-gun"){{
                x = 13.5f;
                y = 15.5f;
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
                shootSound = Sounds.shootBig;
                ejectEffect = Fx.casing3Double;
                reload = 10f;
                bullet = Bullets.standardThoriumBig;
            }}, new Weapon(name + "-launcher"){{
                x = 17f;
                y = 14f;
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

        ((UnitFactory)Blocks.airFactory).plans.add(new UnitPlan(caelifera, 60f * 25, with(Items.silicon, 15, Items.titanium, 25)));

        setEntity("angel", UnitEntity::create);
        angel = new UnitType("angel"){{
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

        setEntity("malakhim", UnitEntity::create);
        malakhim = new UnitType("malakhim"){{
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

        setEntity("project-spiboss", LegsUnit::create);
        projectSpiboss = new UnitType("project-spiboss"){{
            groundLayer = Layer.legUnit + 3f;
            drag = 0.1f;
            speed = 0.4f;
            hitSize = 34f;
            health = 20000;
            legCount = 10;
            legMoveSpace = 0.7f;
            legPairOffset = 0.7f;
            legGroupSize = 3;
            legLength = 55f;
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
            weapons.add(new Weapon("large-purple-mount"){{
                reload = 50f;
                x = 13f;
                y = -17f;
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
            weap4.name = "spiroct-weapon";
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
            Weapon weap5 = weap4.copy();
            weap5.x = 25f;
            weap5.y = 5f;
            weapons.add(weap4, weap5);
        }};

        setEntity("arcaetana", LegsUnit::create);
        arcaetana = new UnitType("arcaetana"){{
            speed = 0.4f;
            drag = 0.12f;
            hitSize = 29f;
            hovering = true;
            allowLegStep = true;
            health = 31000;
            armor = 16f;
            rotateSpeed = 1.3f;
            legCount = 12;
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
            }}, new Weapon("unity-gummy-main-sapper"){
                {
                    x = 10.25f;
                    y = -23.25f;
                    shootY = 8f;
                    shootSound = Sounds.laser;
                    reload = 30f;
                    rotate = true;
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
            weapons.add(weapons.get(1).copy(), new Weapon(name + "-main-laser"){{
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
            }});
            Weapon temp = weapons.get(2);
            temp.x = -17f;
            temp.y = -18.5f;
            temp.flipSprite = true;
        }};

        //endregion
        //region naval-units

        setEntity("rexed", UnitWaterMove::create);
        rexed = new UnitType("rexed"){{
            speed = 0.6f;
            health = 1370;
            engineSize = 5f;
            engineOffset = 12f;
            accel = 0.2f;
            baseRotateSpeed = 0.1f;
            rotateSpeed = 1.6f;
            hitSize = 29f;
            armor = 3f;
            immunities.add(StatusEffects.wet);
            trailX = 9.75f;
            trailY = -15f;
            trailLength = 37;
            trailScl = 1.75f;
            rotateShooting = true;
            abilities.add(new ForceFieldAbility(65f, 0.1f, 300f, 450f));
            weapons.add(new Weapon(name + "-main"){{
                reload = 120f;
                shootY = 7f;
                x = 0f;
                y = -6f;
                rotate = true;
                shake = 3f;
                rotateSpeed = 1.6f;
                mirror = false;
                inaccuracy = 3f;
                bullet = new ArtilleryBulletType(2.9f, 1f){{
                    collidesTiles = true;
                    hitEffect = Fx.blastExplosion;
                    knockback = 1.2f;
                    lifetime = 128;
                    width = height = 19f;
                    ammoMultiplier = 3f;
                    splashDamageRadius = 75f;
                    splashDamage = 95f;
                    backColor = UnityPal.navalReddish;
                    frontColor = lightningColor = UnityPal.navalYellowish;
                    smokeEffect = Fx.shootBigSmoke2;
                    shake = 4.5f;
                    statusDuration = 60f * 10f;
                }};
            }}, new Weapon("missiles-mount"){{
                reload = 35f;
                x = 3.5f;
                y = 5f;
                shots = 3;
                shotDelay = 3f;
                inaccuracy = 5f;
                rotate = true;
                shake = 3f;
                rotateSpeed = 4f;
                bullet = new MissileBulletType(3f, 3f){{
                    lifetime = 49f;
                    splashDamageRadius = 35f;
                    splashDamage = 20f;
                    weaveScale = 8f;
                    weaveMag = 1f;
                    despawnEffect = Fx.blastExplosion;
                    width = height = 9f;
                    backColor = Pal.bulletYellowBack;
                    frontColor = Pal.bulletYellow;
                    trailColor = Color.gray;
                }};
            }});
        }};

        setEntity("storm", UnitWaterMove::create);
        storm = new UnitType("storm"){{
            speed = 0.5f;
            health = 3450;
            engineSize = 5f;
            engineOffset = 12f;
            accel = 0.2f;
            baseRotateSpeed = 0.05f;
            rotateSpeed = 0.7f;
            hitSize = 41f;
            armor = 7f;
            immunities.add(StatusEffects.wet);
            trailX = 15f;
            trailY = -26f;
            trailLength = 45;
            trailScl = 2f;
            rotateShooting = true;
            weapons.add(new Weapon(name + "-igniter"){{
                shootSound = Sounds.laser;
                shadow = 20f;
                shootY = 10f;
                reload = 170f;
                x = 0f;
                y = -2f;
                rotate = true;
                shake = 5f;
                rotateSpeed = 1f;
                mirror = false;
                bullet = new LaserBulletType(155f){{
                    sideAngle = 25f;
                    sideWidth = 2f;
                    sideLength = 25f;
                    width = 25f;
                    length = 220f;
                    shootEffect = Fx.shockwave;
                    colors = new Color[]{UnityPal.navalReddish, UnityPal.navalYellowish, Color.white};
                }};
            }}, new Weapon(name + "-main"){{
                reload = 120f;
                shootY = 7f;
                x = 17f;
                y = -5f;
                rotate = true;
                shake = 3f;
                rotateSpeed = 1f;
                bullet = new ArtilleryBulletType(2.9f, 1f){{
                    hitEffect = Fx.blastExplosion;
                    knockback = 1.5f;
                    lifetime = 129f;
                    width = height = 23f;
                    collidesTiles = true;
                    ammoMultiplier = 3;
                    splashDamageRadius = 135f;
                    splashDamage = 75f;
                    backColor = UnityPal.navalReddish;
                    frontColor = lightningColor = UnityPal.navalYellowish;
                    smokeEffect = Fx.shootBigSmoke2;
                    shake = 4.5f;
                    statusDuration = 60f * 10f;
                }};
            }}, new Weapon("missiles-mount"){{
                reload = 45f;
                x = 15f;
                y = 12f;
                rotate = true;
                shake = 2f;
                rotateSpeed = 4f;
                shots = 4;
                shotDelay = 3f;
                inaccuracy = 5f;
                bullet = new MissileBulletType(3.5f, 5f){{
                    lifetime = 49f;
                    splashDamageRadius = 45f;
                    splashDamage = 30f;
                    weaveScale = 8f;
                    weaveMag = 1f;
                    despawnEffect = Fx.blastExplosion;
                    width = height = 10f;
                    backColor = Pal.bulletYellowBack;
                    frontColor = Pal.bulletYellow;
                    trailColor = Color.gray;
                }};
            }});
            Weapon temp = weapons.get(2).copy();
            temp.reload = 35f;
            temp.x = 12f;
            temp.y = -8;
            weapons.add(temp);
        }};

        setEntity("amphibi-naval", TransUnitWaterMove::new);
        amphibiNaval = new UnityUnitType("amphibi-naval"){{
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
        }};

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
        craberNaval = new UnityUnitType("craber-naval"){{
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
        }};

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

        ((UnitFactory)Blocks.navalFactory).plans.add(new UnitPlan(amphibiNaval, 60f * 25f, with(Items.silicon, 15, Items.titanium, 25)));

        //endregion
        //region scar

        setEntity("hovos", LegsUnit::create);
        hovos = new UnitType("hovos"){{
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

        setEntity("ryzer", LegsUnit::create);
        ryzer = new UnitType("ryzer"){{
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

        setEntity("sundown", LegsUnit::create);
        sundown = new UnityUnitType("sundown"){{
            defaultController = DistanceGroundAI::new;
            speed = 0.5f;
            health = 8700f;
            hitSize = 36f;
            range = 260f;
            allowLegStep = true;
            legMoveSpace = 0.53f;
            legCount = 4;
            legTrns = 0.4f;
            legLength = 44f;
            legExtension = -9.3f;

            weapons.add(new Weapon("unity-scar-large-launcher"){{
                x = 13.5f;
                y = -6.5f;
                shootY = 5f;
                shadow = 6f;
                rotateSpeed = 5f;
                rotate = true;
                reload = 90f;
                shake = 1f;
                shots = 12;
                inaccuracy = 26f;
                velocityRnd = 0.2f;
                xRand = 1.2f;
                shootSound = Sounds.bigshot;

                bullet = new MissileBulletType(6f, 7f){{
                    lifetime = 70f;
                    speed = 5f;
                    width = 7f;
                    height = 12f;
                    shrinkY = 0f;
                    backColor = trailColor = UnityPal.scarColor;
                    frontColor = UnityPal.endColor;
                    splashDamage = 46f;
                    splashDamageRadius = 20f;
                    weaveMag = 3f;
                    weaveScale = 6f;
                    pierceBuilding = true;
                    pierceCap = 3;
                }};
            }});

            abilities.add(new DirectionShieldAbility(4, 0.2f, 20f, 600f, 1.3f, 0.4f, hitSize / 1.42f));
        }};

        setEntity("whirlwind", UnitEntity::create);
        whirlwind = new UnitType("whirlwind"){{
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

        setEntity("jetstream", UnitEntity::create);
        jetstream = new UnitType("jetstream"){{
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

        setEntity("vortex", UnitEntity::create);
        vortex = new UnitType("vortex"){{
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
        //region monolith

        setEntity("stele", MechUnit::create);
        stele = new UnitType("stele"){{
            speed = 0.5f;
            hitSize = 8f;
            health = 200;
            canBoost = true;
            boostMultiplier = 2.5f;
            weapons.add(new Weapon(name + "-shotgun"){{
                top = false;
                reload = 60f;
                recoil = 2.5f;
                x = 5.25f;
                y = -0.25f;
                shots = 12;
                spacing = 0.3f;
                inaccuracy = 0.5f;
                velocityRnd = 0.2f;
                shotDelay = 0f;
                shootSound = Sounds.shootBig;
                bullet = new BasicBulletType(3.5f, 3f){
                    {
                        frontColor = Pal.lancerLaser;
                        backColor = Pal.lancerLaser.cpy().mul(0.7f);
                        width = height = 2f;
                        weaveScale = 3f;
                        weaveMag = 5f;
                        homingPower = 1f;
                        lifetime = 60f;
                        shootEffect = Fx.hitLancer;
                    }

                    @Override
                    public void init(Bullet b){
                        b.data = new Trail(6);
                    }

                    @Override
                    public void draw(Bullet b){
                        ((Trail)b.data).draw(frontColor, width);
                        Draw.color(frontColor);
                        Fill.circle(b.x, b.y, width);
                        Draw.color();
                    }

                    @Override
                    public void update(Bullet b){
                        super.update(b);
                        ((Trail)b.data).update(b.x, b.y);
                    }
                };
            }});
        }};

        setEntity("pedestal", MechUnit::create);
        pedestal = new UnitType("pedestal"){{
            speed = 0.42f;
            hitSize = 11f;
            health = 600;
            armor = 3.5f;
            rotateSpeed = 2.6f;
            singleTarget = true;
            canBoost = true;
            boostMultiplier = 2.5f;
            engineSize = 3.5f;
            engineOffset = 6f;
            weapons.add(new Weapon(name + "-gun"){{
                top = false;
                x = 10.75f;
                y = 2.25f;
                reload = 60f;
                recoil = 3.2f;
                shootSound = Sounds.shootBig;
                BulletType subBullet = new LightningBulletType();
                subBullet.damage = 10f;
                bullet = new BasicBulletType(3f, 12f, "shell"){
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
            }});
        }};

        setEntity("pilaster", MechUnit::create);
        pilaster = new UnitType("pilaster"){{
            speed = 0.3f;
            hitSize = 26.5f;
            health = 1000;
            armor = 4f;
            rotateSpeed = 2.2f;
            mechFrontSway = 0.55f;
            canBoost = true;
            boostMultiplier = 2.5f;
            engineSize = 5f;
            engineOffset = 10f;
            weapons.add(new Weapon("unity-monolith-medium-weapon-mount"){{
                rotate = true;
                x = 4f;
                y = 7.5f;
                shootY = 6f;
                recoil = 2.5f;
                reload = 25f;
                shots = 3;
                spacing = 3f;
                shootSound = Sounds.spark;
                bullet = new LightningBulletType(){{
                    damage = 15f;
                    lightningLength = 15;
                }};
            }}, new Weapon("unity-monolith-large-weapon-mount"){{
                rotate = true;
                rotateSpeed = 10f;
                x = 13f;
                y = 2f;
                shootY = 10.5f;
                recoil = 3f;
                reload = 40f;
                shootSound = Sounds.laser;
                bullet = new LaserBulletType(100f);
            }});
        }};

        setEntity("pylon", LegsUnit::create);
        pylon = new UnitType("pylon"){{
            speed = 0.28f;
            hitSize = 36f;
            health = 7200f;
            flying = false;
            hovering = true;
            armor = 7f;
            rotateSpeed = 1.48f;
            ammoType = AmmoTypes.powerHigh;

            allowLegStep = true;
            visualElevation = 0.2f;
            legCount = 4;
            legExtension = 8f;
            legSpeed = 0.08f;
            legLength = 16f;
            legMoveSpace = 1.2f;
            legTrns = 0.5f;
            legBaseOffset = 11f;
            groundLayer = Layer.legUnit;

            commandLimit = 8;

            weapons.add(new Weapon("unity-pylon-laser"){{
                shootSound = Sounds.laserblast;
                chargeSound = Sounds.lasercharge;
                soundPitchMin = 1f;
                top = false;
                mirror = false;
                shake = 15f;
                shootY = 11f;
                x = y = 0f;
                reload = 420f;
                recoil = 0f;
                cooldownTime = 280f;

                shootStatusDuration = 60f * 1.8f;
                shootStatus = StatusEffects.unmoving;
                firstShotDelay = UnityFx.pylonLaserCharge.lifetime / 2f;

                bullet = UnityBullets.pylonLaser;
            }}, new Weapon("unity-monolith-large2-weapon-mount"){{
                rotate = true;
                rotateSpeed = 3.5f;
                shootSound = Sounds.laser;
                shake = 5f;
                shootY = 14f;
                x = 14f;
                y = 5f;
                reload = 60f;
                recoil = 4f;

                bullet = UnityBullets.pylonLaserSmall;
            }});
        }};

        setEntity("monument", LegsUnit::create);
        monument = new UnitType("monument"){{
            speed = 0.25f;
            rotateSpeed = 1.4f;
            health = 16000;
            armor = 9f;
            hitSize = 48f;
            ammoType = AmmoTypes.powerHigh;
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
            visualElevation = 0.3f;
            groundLayer = Layer.legUnit;
            weapons.add(new Weapon("unity-monolith-railgun-big"){{
                x = 0f;
                y = 12f;
                rotate = true;
                rotateSpeed = 1.2f;
                mirror = false;
                shootY = 35f;
                reload = 240f;
                shootCone = 2f;
                cooldownTime = 210f;
                recoil = shake = 8f;
                shadow = 30f;
                shootSound = Sounds.railgun;
                bullet = UnityBullets.monumentRailBullet;
            }}, new Weapon("unity-monolith-large2-weapon-mount"){{
                x = 14f;
                y = 12f;
                shootY = 14f;
                rotate = true;
                rotateSpeed = 3.5f;
                reload = 48f;
                recoil = shake = 5f;
                shootSound = Sounds.laser;
                bullet = new LaserBulletType(140f);
            }});
            Weapon laserGun2 = weapons.get(1).copy();
            laserGun2.x = 20f;
            laserGun2.y = 3f;
            laserGun2.reload = 60f;
            weapons.add(laserGun2);
        }};

        setEntity("colossus", LegsUnit::create);
        colossus = new UnitType("colossus"){{
            speed = 0.22f;
            rotateSpeed = 1.2f;
            health = 30000;
            armor = 13f;
            hitSize = 64f;
            ammoType = AmmoTypes.powerHigh;
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
            groundLayer = Layer.legUnit;
            abilities.add(new LightningSpawnAbility());
            weapons.add(new Weapon(name + "-weapon"){{
                top = false;
                x = 30f;
                y = 7.75f;
                shootY = 20f;
                reload = 60f;
                spacing = 1f;
                inaccuracy = 6f;
                shots = 5;
                shotDelay = 3f;
                recoil = 8f;
                shootSound = Sounds.laser;
                bullet = new LaserBulletType(280f){{
                    lifetime = 32f;
                    width = 45f;
                    length = 280f;
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

        //endregion
        //region dark

        /*
        setEntity("devourer", WormDefaultUnit::new);
        devourer=new WormUnitType("devourer", 45) {{
            
        }};
        */

        //endregion
        //reconstructors

        ((Reconstructor)Blocks.additiveReconstructor).upgrades.add(
            //global
            new UnitType[]{caelifera, schistocerca},
            new UnitType[]{amphibiNaval, craberNaval},

            //monolith
            new UnitType[]{stele, pedestal}
        );

        ((Reconstructor)Blocks.multiplicativeReconstructor).upgrades.add(
            //global
            new UnitType[]{schistocerca, anthophila},

            //monolith
            new UnitType[]{pedestal, pilaster}
        );

        ((Reconstructor)Blocks.exponentialReconstructor).upgrades.add(
            //global
            new UnitType[]{anthophila, vespula},

            //monolith
            new UnitType[]{pilaster, pylon}
        );

        ((Reconstructor)Blocks.tetrativeReconstructor).upgrades.add(
            //global
            new UnitType[]{vespula, lepidoptera},

            //monolith
            new UnitType[]{pylon, monument}
        );
    }
}

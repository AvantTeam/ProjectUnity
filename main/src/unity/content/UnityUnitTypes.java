package unity.content;

import arc.func.*;
import arc.math.Mathf;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.gen.*;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;
import mindustry.content.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitFactory.*;
import unity.ai.DistanceGroundAI;
import unity.entities.units.*;
import unity.graphics.UnityPal;

import static mindustry.type.ItemStack.*;

public class UnityUnitTypes implements ContentList{
    private static Prov<?>[] constructors = new Prov[]{
        CopterUnit::new,
        WormSegmentUnit::new,
        WormDefaultUnit::new
    };

    private static final int[] classIDs = new int[constructors.length];

    public static UnitType
    //flying-units
    caelifera, schistocerca, anthophila, vespula, lepidoptera,

    //ground-units
    arcaetana, projectSpiboss,

    //monolith
    pedestal, pilaster, stele,

    //scar
    hovos, jetstream,

    //worm units
    arcnelidia, devourer;

    public static int getClassId(int index){
        return classIDs[index];
    }

    private static void setEntity(String name, Prov c){
        EntityMapping.nameMap.put(name, c);
    }

    @Override
    public void load(){
        //region flying-units

        for(int i = 0, j = 0, len = EntityMapping.idMap.length; i < len; i++){
            if(EntityMapping.idMap[i] == null){
                classIDs[j] = i;
                EntityMapping.idMap[i] = constructors[j++];

                if(j >= constructors.length) break;
            }
        }

        setEntity("caelifera", CopterUnit::new);
        caelifera = new CopterUnitType("caelifera"){{
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
                ejectEffect = Fx.shellEjectSmall;
                bullet = new BasicBulletType(5f, 7f){{
                    lifetime = 30f;
                    shrinkY = 0.2f;
                }};
            }}, new Weapon(name + "-launcher"){{
                reload = 30f;
                x = 4.5f;
                y = 0.5f;
                shootSound = Sounds.shootSnap;
                ejectEffect = Fx.shellEjectMedium;
                bullet = new MissileBulletType(3f, 1f){{
                    speed = 3f;
                    lifetime = 50f;
                    splashDamage = 40f;
                    splashDamageRadius = 6f;
                    drag = -0.01f;
                }};
            }});

            rotors.add(new Rotor(){{
                x = 0f;
                y = 6f;
            }});
        }};

        setEntity("schistocerca", CopterUnit::new);
        schistocerca = new CopterUnitType("schistocerca"){{
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
                ejectEffect = Fx.shellEjectSmall;
                reload = 8f;
                bullet = new BasicBulletType(4f, 5f){{
                    lifetime = 36;
                    shrinkY = 0.2f;
                }};
            }}, new Weapon(name + "-gun"){{
                x = 4f;
                y = 8.75f;
                shootSound = Sounds.shootSnap;
                ejectEffect = Fx.shellEjectSmall;
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
                ejectEffect = Fx.shellEjectMedium;
                reload = 30f;
                bullet = Bullets.standardIncendiaryBig;
            }});

            for(int i : Mathf.signs){
                rotors.add(new Rotor(){{
                    x = 0f;
                    y = 6.5f;
                    bladeCount = 3;
                    speed = 29f * i;
                }});
            }
        }};

        setEntity("anthophila", CopterUnit::new);
        anthophila = new CopterUnitType("anthophila"){{
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

            rotors.add(new Rotor(){{
                x = 0f;
                y = -13f;
                scale = 0.6f;
            }}, new Rotor(){{
                mirror = true;
                x = 13f;
                y = 3f;
                bladeCount = 3;
            }});
        }};

        setEntity("vespula", CopterUnit::new);
        vespula = new CopterUnitType("vespula"){{
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
                bullet = new LaserBulletType(240f){{
                    sideAngle = 45f;
                    length = 200f;
                }};
			}});
			
			for(int i : Mathf.signs){
				rotors.add(new Rotor(){{
					mirror = true;
					x = 15f;
					y = 6.75f;
					speed = 29f * i;
					rotOffset = 180f * i;
				}});
			}
        }};

        setEntity("lepidoptera", CopterUnit::new);
        lepidoptera = new CopterUnitType("lepidoptera"){{
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
                ejectEffect = Fx.shellEjectBig;
                reload = 10f;
                bullet = Bullets.standardThoriumBig;
            }}, new Weapon(name + "-launcher"){{
                x = 17f;
                y = 14f;
                shootSound = Sounds.shootSnap;
                ejectEffect = Fx.shellEjectMedium;
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
                rotors.add(new Rotor(){{
                    mirror = true;
                    x = 22.5f;
                    y = 21.25f;
                    bladeCount = 3;
                    speed = 19f * i;
                }}, new Rotor(){{
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
        
        //endregion
        //region ground-units

        setEntity("arcaetana", BuilderLegsUnit::create);
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
                        if(Mathf.chanceDelta(0.3f)) Lightning.create(b, UnityPal.arcaetanaLightning, 43f, b.x, b.y, Mathf.range(56f) + b.rotation(), 8);
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
            }}, new Weapon("unity-gummy-main-sapper"){{
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
                        if(Mathf.chance(0.3f)) Lightning.create(b, UnityPal.arcaetanaLightning, 12f, x, y, Mathf.range(30f) + b.rotation(), 7);
                    }

                    {
                        colors = new Color[]{Color.valueOf("a96bfa80"), UnityPal.arcaetanaLightning, Color.white};
                        length = 195f;
                        ammoMultiplier = 6f;
                        width = 19f;
                        drawSize = length * 2f + 20f;
                    }
                };
            }});
            weapons.add(weapons.get(1).copy(), new Weapon(name + "-main-laser"){{
                x = 18.75f;
                y = -11f;
                shootY = 19f;
                rotateSpeed = 1f;
                shootSound = Sounds.laser;
                reload = 60f;
                recoil = 4f;
                rotate = true;
                occlusion = 15f;
                shake = 4f;
                bullet = new LaserBulletType(325f){
                    @Override
                    public void hit(Bullet b, float x, float y){
                        super.hit(b, x, y);
                        if(Mathf.chance(0.4f)) Lightning.create(b, UnityPal.arcaetanaLightning, 34f, x, y, Mathf.range(30f) + b.rotation(), 12);
                    }

                    {
                        colors = new Color[]{Color.valueOf("a96bfa80"), UnityPal.arcaetanaLightning, Color.white};
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
        //region monolith

        setEntity("pedestal",MechUnit::create);
        pedestal = new UnitType("pedestal"){{
                speed = 0.42f;
                hitSize = 11f;
                health = 600;
                armor = 3.5f;
                rotateSpeed = 2.6f;
                singleTarget = true;
                weapons.add(new Weapon(name + "-gun"){{
                    x = 10.75f;
                    y = 2.25f;
                    reload = 60f;
                    recoil = 3.2f;
                    shootSound = Sounds.shootBig;
                    BulletType subBullet = new LightningBulletType();
                    subBullet.damage = 10f;
                    bullet = new BasicBulletType(3f, 12f, "shell"){
                        @Override
                        public void init(Bullet b){
                            for(int i = 0; i < 3; i++){
                                subBullet.create(b, b.x, b.y, b.vel.angle());
                                Sounds.spark.at(b.x, b.y, Mathf.random(0.6f, 0.8f));
                            }
                        };

                        {
                            width = 20f;
                            height = 20f;
                            lifetime = 60f;
                            frontColor = Pal.lancerLaser;
                            backColor = Pal.lancerLaser.cpy().mul(0.6f);
                            shootEffect = Fx.lightningShoot;
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
                bullet = new LightningBulletType();
                bullet.damage = 15f;
                bullet.lightningLength = 15;
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

        setEntity("stele", MechUnit::create);
        stele = new UnitType("stele"){{
            speed = 0.5f;
            hitSize = 8f;
            health = 200;
            weapons.add(new Weapon(name + "-shotgun"){{
                reload = 60f;
                recoil = 2.5f;
                x = 5.25f;
                y = -0.25f;
                shots = 12;
                spacing = 0.5f;
                inaccuracy = 0.5f;
                velocityRnd = 0.2f;
                shotDelay = 0f;
                shootSound = Sounds.shootBig;
                bullet = new BasicBulletType(3.5f, 3f){
                    @Override
                    public void init(Bullet b){
                        b.data = new Trail(6);
                    }

                    @Override
                    public void draw(Bullet b){
                        ((Trail) b.data).draw(frontColor, width);
                        Draw.color(frontColor);
                        Fill.circle(b.x, b.y, width);
                        Draw.color();
                    }

                    @Override
                    public void update(Bullet b){
                        super.update(b);
                        ((Trail) b.data).update(b.x, b.y);
                    }

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
                };
            }});
        }};

        //endregion
        //region scar

        setEntity("hovos",LegsUnit::create);
        hovos = new UnitType("hovos"){{
            defaultController = DistanceGroundAI::new;
            speed = 0.8f;
            health = 340;
            hitSize = 17f;
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
                    speed = 59f;
                    lifetime = 8f;
                    shootEffect = UnityFx.scarRailShoot;
                    pierceEffect = UnityFx.scarRailHit;
                    updateEffect = UnityFx.scarRailTrail;
                    hitEffect = Fx.massiveExplosion;
                    pierceDamageFactor = 0.3f;
                }};
            }});
        }};

        /*setEntity("jetstream", UnitEntity::create);
        jetstream = new UnitType("jetstream"){{
            description="There will be Bloodshed";
            health=670;
            rotateSpeed=12.5f;
            faceTarget=true;
            flying=true;
            speed=9.2f;
            drag=0.019f;
            accel=0.028f;
            hitSize=11f;
            engineOffset=11f;
            weapons.add(new Weapon() {
                {
                    mirror=false;
                    x=0f;
                    y=7f;
                    continuous=true;
                    //bullet=new 
                    reload=60f*2.5f+bullet.lifetime;
                    shootStatus=UnityStatusEffects.reloadFatigue;
                    shootStatusDuration=bullet.lifetime;
                    shootCone=15f;
                }
            });
        }};*/

        //endregion
        //region worm units

        setEntity("arcnelidia", WormDefaultUnit::new);
        arcnelidia = new WormUnitType("arcnelidia"){{
            setTypeID(3);
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
            LightningBulletType archnelidiaBolt = new LightningBulletType(){{
                damage = 23f;
                lightningColor = Pal.surge;
                lightningLength = 24;
                lightningLengthRand = 3;
            }};
            weapons.add(new Weapon(){{
                reload = 90f;
                rotateSpeed = 50f;
                mirror = true;
                rotate = true;
                ignoreRotation = true;
                minShootVelocity = 2.1f;
                bullet = archnelidiaBolt;
            }});
            segWeapSeq.add(new Weapon(){{
                x = 0f;
                shots = 4;
                reload = 70f;
                rotateSpeed = 50f;
                mirror = false;
                ignoreRotation = true;
                bullet = archnelidiaBolt;
            }});
        }};

        /*
        setEntity("devourer", WormDefaultUnit::new);
        devourer=new WormUnitType("devourer", 45) {{
            
        }};
        */

        //reconstructors
        ((Reconstructor)Blocks.additiveReconstructor).upgrades.add(new UnitType[]{caelifera, schistocerca});
        ((Reconstructor)Blocks.multiplicativeReconstructor).upgrades.add(new UnitType[]{schistocerca, anthophila});
        ((Reconstructor)Blocks.exponentialReconstructor).upgrades.add(new UnitType[]{anthophila, vespula});
        ((Reconstructor)Blocks.tetrativeReconstructor).upgrades.add(new UnitType[]{vespula, lepidoptera});
    }
}

package unity.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import unity.annotations.Annotations.*;
import unity.content.effects.*;
import unity.content.units.*;
import unity.entities.bullet.anticheat.*;
import unity.entities.bullet.energy.*;
import unity.entities.bullet.exp.*;
import unity.entities.bullet.laser.*;
import unity.gen.*;
import unity.graphics.*;
import unity.mod.*;
import unity.world.*;
import unity.world.blocks.*;
import unity.world.blocks.defense.*;
import unity.world.blocks.defense.turrets.*;
import unity.world.blocks.distribution.*;
import unity.world.blocks.effect.*;
import unity.world.blocks.environment.*;
import unity.world.blocks.exp.*;
import unity.world.blocks.exp.turrets.*;
import unity.world.blocks.light.*;
import unity.world.blocks.power.*;
import unity.world.blocks.production.*;
import unity.world.blocks.sandbox.*;
import unity.world.blocks.units.*;
import unity.world.draw.*;
import unity.world.graphs.*;
import unity.world.meta.*;
import younggamExperimental.*;
import younggamExperimental.blocks.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.type.ItemStack.*;
import static unity.world.blocks.exp.EField.*;

public class UnityBlocks{
    //---------- global ----------
    public static Block
    //production
    distributionDrill,

    //unit
    recursiveReconstructor,

    //crafting
    irradiator,

    //defense
    superCharger;

    //---------- dark faction ----------
    public static @FactionDef("dark") Block
    //environment
    oreUmbrium,

    //turrets
    apparition, ghost, banshee, fallout, catastrophe, calamity, extinction,

    //defense
    darkWall, darkWallLarge;

    //crafting
    public static @FactionDef("dark")
    @Merge(base = GenericCrafter.class, value = Stemc.class)
    Block darkAlloyForge;

    //---------- light faction ----------
    public static @FactionDef("light") Block
    //environment
    oreLuminum,

    //turret
    photon, electron, graviton, proton, neutron, gluon, wBoson, zBoson, higgsBoson, singularity, muon, ephemeron,

    //light
    lightLamp, oilLamp, lightLampInfi,
    lightReflector,
    lightDivisor,

    //defense
    metaglassWall, metaglassWallLarge;

    //crafting
    public static @FactionDef("light")
    @Merge(base = GenericCrafter.class, value = LightHoldc.class)
    @LoadRegs({
        "light-forge-top1",
        "light-forge-top2",
        "light-forge-top3",
        "light-forge-top4"
    })
    Block lightForge;

    public static @FactionDef("scar") Block terraCore;

    //---------- imber faction ----------
    public static @FactionDef("imber") Block
    //environment
    oreImberium, electroTile,

    //turret
    orb, shockwire, current, plasma, electrobomb, shielder, orbTurret,

    //power
    powerPlant, absorber,

    //yes
    piper;

    //crafting
    public static @FactionDef("imber")
    @Merge(base = GenericCrafter.class, value = Stemc.class)
    Block sparkAlloyForge;

    //---------- koruh faction ----------
    public static @FactionDef("koruh") Block
    //crafting
    denseSmelter, solidifier, steelSmelter, liquifier, titaniumExtractor, lavaSmelter, diriumCrucible, coalExtractor,

    //defense
    stoneWall, denseWall, steelWall, steelWallLarge, diriumWall, diriumWallLarge, shieldProjector, diriumProjector, timeMine, shieldWall, shieldWallLarge,

    //distribution
    steelConveyor, teleporter, teleunit;

    public static @FactionDef("koruh")
    @Dupe(base = ExpTurret.class, parent = KoruhConveyor.class)
    Block diriumConveyor;

    //unit
    public static @FactionDef("koruh") Block bufferPad, omegaPad, cachePad, convertPad,

    //power
    uraniumReactor,

    //TODO
    expFountain, expVoid, expTank, expChest, expRouter, expTower, expTowerDiagonal, bufferTower, expHub, expNode, expNodeLarge;// expOutput, expUnloader;

    //turret
    public static @FactionDef("koruh")
    @LoadRegs("bt-laser-turret-top")
    Block laser, laserCharge, laserBranch, laserFractal, laserBreakthrough;

    public static @FactionDef("koruh")
    Block laserFrost, laserKelvin;

    public static @FactionDef("koruh")
    Block inferno;

    public static @FactionDef("koruh")
    Block buffTurret, upgradeTurret;

    //---------- monolith faction ----------
    public static @FactionDef("monolith") Block
    //environments
    oreMonolite,
    sharpslate, sharpslateWall,
    infusedSharpslate, infusedSharpslateWall,
    archSharpslate, archEnergy,
    loreMonolith;

    //crafting
    public static @FactionDef("monolith")
    @LoadRegs({
        "debris-extractor-heat1",
        "debris-extractor-heat2"
    })
    @Merge(base = FloorExtractor.class, value = Soulc.class)
    Block debrisExtractor;

    public static @FactionDef("monolith")
    Block soulInfuser;

    public static @FactionDef("monolith")
    @Merge(base = GenericCrafter.class, value = Soulc.class)
    Block monolithAlloyForge;

    //defense
    public static @FactionDef("monolith")
    Block electrophobicWall, electrophobicWallLarge;

    //turret
    public static @FactionDef("monolith")
    @Merge(base = LifeStealerTurret.class, value = Soulc.class)
    Block lifeStealer;

    public static @FactionDef("monolith")
    @Merge(base = AbsorberTurret.class, value = Soulc.class)
    Block absorberAura;

    public static @FactionDef("monolith")
    @Merge(base = HeatRayTurret.class, value = Soulc.class)
    Block heatRay, incandescence;

    public static @FactionDef("monolith")
    @Merge(base = PowerTurret.class, value = {Turretc.class, Soulc.class})
    Block
    ricochet, shellshock, purge,
    blackout,
    diviner, mage;

    public static @FactionDef("monolith")
    @Merge(base = ItemTurret.class, value = {Turretc.class, Soulc.class})
    Block recluse;

    public static @FactionDef("monolith")
    @Merge(base = PowerTurret.class, value = {Turretc.class, Soulc.class})
    Block oracle;

    public static @FactionDef("monolith")
    Block prism, supernova;

    //---------- youngcha faction ----------
    public static @FactionDef("youngcha") Block
    //environments
    oreNickel, concreteBlank, concreteFill, concreteNumber, concreteStripe, concrete, stoneFullTiles, stoneFull,
    stoneHalf, stoneTiles,

    //turrets
    smallTurret, medTurret, chopper,

    //production
    augerDrill, mechanicalExtractor, sporeFarm,

    //distribution
    mechanicalConveyor, heatPipe, driveShaft, inlineGearbox, shaftRouter, simpleTransmission,

    //crafting
    crucible, holdingCrucible, cruciblePump, castingMold, sporePyrolyser,

    //power
    smallRadiator, thermalHeater, combustionHeater, solarCollector, solarReflector, nickelStator, nickelStatorLarge,
    nickelElectromagnet, electricRotorSmall, electricRotor, handCrank, windTurbine, waterTurbine, electricMotor,

    //defense
    cupronickelWall, cupronickelWallLarge,

    //misc
    smallThruster,

    //sandbox
    infiHeater, infiCooler, infiTorque, neodymiumStator;

    //---------- advance faction ----------
    public static @FactionDef("advance") Block
    //unit
    advanceConstructorModule, advanceConstructor,

    //turret
    celsius, kelvin, caster, storm, eclipse, xenoCorruptor, cube, wavefront;

    //---------- end faction ----------
    public static @FactionDef("end")
    @LoadRegs({
        "end-forge-lights",
        "end-forge-top",
        "end-forge-top-small",

        "terminal-crucible-lights",
        "terminal-crucible-top"
    })
    //crafting
    Block terminalCrucible, endForge;

    //turret
    public static @FactionDef("end")
    @LoadRegs(value = {
        "tenmeikiri-base"
    }, outline = true)
    Block endGame, tenmeikiri;

    public static void load(){
        //region global

        distributionDrill = new DistributionDrill("distribution-drill"){{
            requirements(Category.production, with(Items.copper, 20, Items.silicon, 15, Items.titanium, 20));
            tier = 3;
            drillTime = 450;
            size = 2;

            consumeLiquid(Liquids.water, 0.06f).boost();
        }};

        recursiveReconstructor = new SelectableReconstructor("recursive-reconstructor"){{
            requirements(Category.units, with(Items.graphite, 1600, Items.silicon, 2000, Items.metaglass, 900, Items.thorium, 600, Items.lead, 1200, Items.plastanium, 3600));
            size = 11;
            liquidCapacity = 360f;
            configurable = true;
            constructTime = 20000f;
            minTier = 6;
            upgrades.addAll(
                new UnitType[]{UnitTypes.reign, UnityUnitTypes.citadel},
                new UnitType[]{UnitTypes.toxopid, UnityUnitTypes.araneidae},
                new UnitType[]{UnitTypes.corvus, UnityUnitTypes.cygnus},
                new UnitType[]{UnityUnitTypes.rex, UnityUnitTypes.excelsus},
                new UnitType[]{MonolithUnitTypes.monument, MonolithUnitTypes.colossus}
            );
            otherUpgrades.add(
                new UnitType[]{UnityUnitTypes.citadel, UnityUnitTypes.empire},
                new UnitType[]{UnityUnitTypes.araneidae, UnityUnitTypes.theraphosidae},
                new UnitType[]{MonolithUnitTypes.colossus, MonolithUnitTypes.bastion}
            );
            consumePower(5f);
            consumeItems(with(Items.silicon, 1200, Items.metaglass, 800, Items.thorium, 700, Items.surgeAlloy, 400, Items.plastanium, 600, Items.phaseFabric, 350));
            consumeLiquid(Liquids.cryofluid, 7f);
        }};

        irradiator = new Press("irradiator"){{
            requirements(Category.crafting, with(Items.lead, 120, Items.silicon, 80, Items.titanium, 30));
            outputItem = new ItemStack(UnityItems.irradiantSurge, 3);
            size = 3;
            movementSize = 29f;
            fxYVariation = 25f / tilesize;
            craftTime = 50f;
            consumePower(1.2f);
            consumeItems(with(Items.thorium, 5, Items.titanium, 5, Items.surgeAlloy, 1));
        }};

        superCharger = new Reinforcer("supercharger"){{
            requirements(Category.effect, with(Items.titanium, 60, Items.lead, 20, Items.silicon, 30));
            size = 2;
            itemCapacity = 15;
            laserColor = Items.surgeAlloy.color;
            consumePower(0.4f);
            consumeItems(with(UnityItems.irradiantSurge, 10));
        }};

        oreNickel = new UnityOreBlock(UnityItems.nickel){{
            oreScale = 24.77f;
            oreThreshold = 0.913f;
            oreDefault = false;
        }};

        oreUmbrium = new UnityOreBlock(UnityItems.umbrium){{
            oreScale = 23.77f;
            oreThreshold = 0.813f;
            oreDefault = false;
        }};

        oreLuminum = new UnityOreBlock(UnityItems.luminum){{
            oreScale = 23.77f;
            oreThreshold = 0.81f;
            oreDefault = false;
        }};

        oreImberium = new UnityOreBlock(UnityItems.imberium){{
            oreScale = 23.77f;
            oreThreshold = 0.807f;
            oreDefault = false;
        }};

        //endregion
        //region dark

        apparition = new ItemTurret("apparition"){
            {
                requirements(Category.turret, with(Items.copper, 350, Items.graphite, 380, Items.silicon, 360, Items.plastanium, 200, Items.thorium, 220, UnityItems.umbrium, 370, Items.surgeAlloy, 290));
                size = 5;
                health = 3975;
                range = 235f;
                reload = 6f;
                coolantMultiplier = 0.5f;
                recoilTime = 0.09f;
                inaccuracy = 3f;
                //spread = 12f;
                //shots = 2;
                shootSound = Sounds.shootBig;
                //alternate = true;
                recoil = 3f;
                rotateSpeed = 4.5f;
                shoot = new ShootAlternate(){{
                    spread = 12f;
                }};
                ammo(Items.graphite, UnityBullets.standardDenseLarge, Items.silicon, UnityBullets.standardHomingLarge, Items.pyratite, UnityBullets.standardIncendiaryLarge, Items.thorium, UnityBullets.standardThoriumLarge);
            }
        };

        ghost = new ItemTurret("ghost"){{
            size = 8;
            health = 9750;
            range = 290f;
            reload = 9f;
            coolantMultiplier = 0.5f;
            recoilTime = 0.08f;
            inaccuracy = 3f;
            //shots = 2;
            shootSound = Sounds.shootBig;
            //alternate = true;
            recoil = 5.5f;
            rotateSpeed = 3.5f;
            //spread = 21f;
            //addBarrel(8f, 18.75f, 6f);
            ammo(Items.graphite, UnityBullets.standardDenseHeavy, Items.silicon, UnityBullets.standardHomingHeavy, Items.pyratite, UnityBullets.standardIncendiaryHeavy, Items.thorium, UnityBullets.standardThoriumHeavy);
            requirements(Category.turret, with(Items.copper, 1150, Items.graphite, 1420, Items.silicon, 960, Items.plastanium, 800, Items.thorium, 1230, UnityItems.darkAlloy, 380));
        }};

        banshee = new ItemTurret("banshee"){{
            size = 12;
            health = 22000;
            range = 370f;
            reload = 12f;
            coolantMultiplier = 0.5f;
            recoilTime = 0.08f;
            inaccuracy = 3f;
            //shots = 2;
            shootSound = Sounds.shootBig;
            //alternate = true;
            recoil = 5.5f;
            rotateSpeed = 3.5f;
            //spread = 37f;
            //focus = true;
            //addBarrel(23.5f, 36.5f, 9f);
            //addBarrel(8.5f, 24.5f, 6f);
            //TODO
            ammo(Items.graphite, UnityBullets.standardDenseMassive, Items.silicon, UnityBullets.standardHomingMassive, Items.pyratite, UnityBullets.standardIncendiaryMassive, Items.thorium, UnityBullets.standardThoriumMassive);
            requirements(Category.turret, with(Items.copper, 2800, Items.graphite, 2980, Items.silicon, 2300, Items.titanium, 1900, Items.phaseFabric, 1760, Items.thorium, 1780, UnityItems.darkAlloy, 1280));
        }};

        fallout = new LaserTurret("fallout"){
            {
                size = 5;
                health = 3975;
                range = 215f;
                reload = 110f;
                coolantMultiplier = 0.8f;
                shootCone = 40f;
                shootDuration = 230f;
                //shootY = 5f;
                shootY = 5f;
                shake = 3f;
                firingMoveFract = 0.2f;
                shootEffect = Fx.shootBigSmoke2;
                recoil = 4f;
                shootSound = Sounds.laserbig;
                heatColor = Color.valueOf("e04300");
                rotateSpeed = 3.5f;
                loopSound = Sounds.beam;
                loopSoundVolume = 2.1f;
                requirements(Category.turret, with(Items.copper, 450, Items.lead, 350, Items.graphite, 390, Items.silicon, 360, Items.titanium, 250, UnityItems.umbrium, 370, Items.surgeAlloy, 360));
                shootType = UnityBullets.falloutLaser;
                consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability < 0.1f, 0.58f)).update(false);
                consumePower(19f);
            }
        };

        catastrophe = new BigLaserTurret("catastrophe"){{
            size = 8;
            health = 9750;
            range = 300f;
            reload = 190f;
            coolantMultiplier = 0.6f;
            shootCone = 40f;
            shootDuration = 320f;
            shake = 4f;
            firingMoveFract = 0.16f;
            shootEffect = Fx.shootBigSmoke2;
            recoil = 7f;
            cooldownTime = 0.012f;
            heatColor = Color.white;
            rotateSpeed = 1.9f;
            shootSound = Sounds.laserbig;
            loopSound = Sounds.beam;
            loopSoundVolume = 2.2f;
            requirements(Category.turret, with(Items.copper, 1250, Items.lead, 1320, Items.graphite, 1100, Items.titanium, 1340, Items.surgeAlloy, 1240, Items.silicon, 1350, Items.thorium, 770, UnityItems.darkAlloy, 370));
            shootType = UnityBullets.catastropheLaser;
            consumePower(39f);
            consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.4f && liquid.flammability < 0.1f, 1.3f)).update(false);
        }};

        calamity = new BigLaserTurret("calamity"){{
            size = 12;
            health = 22000;
            range = 420f;
            reload = 320f;
            coolantMultiplier = 0.6f;
            shootCone = 23f;
            shootDuration = 360f;
            shake = 4f;
            firingMoveFract = 0.09f;
            shootEffect = Fx.shootBigSmoke2;
            recoil = 7f;
            cooldownTime = 0.009f;
            heatColor = Color.white;
            rotateSpeed = 0.97f;
            shootSound = Sounds.laserbig;
            loopSound = Sounds.beam;
            loopSoundVolume = 2.6f;
            requirements(Category.turret, with(Items.copper, 2800, Items.lead, 2970, Items.graphite, 2475, Items.titanium, 3100, Items.surgeAlloy, 2790, Items.silicon, 3025, Items.thorium, 1750, UnityItems.darkAlloy, 1250));
            shootType = UnityBullets.calamityLaser;
            consumePower(87f);
            consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.3f && liquid.flammability < 0.1f, 2.1f)).update(false);
        }};

        extinction = new BigLaserTurret("extinction"){{
            requirements(Category.turret, with(Items.copper, 3800, Items.lead, 4100, Items.graphite, 3200, Items.titanium, 4200, Items.surgeAlloy, 3800, Items.silicon, 4300, Items.thorium, 2400, UnityItems.darkAlloy, 1700, UnityItems.terminum, 900, UnityItems.terminaAlloy, 500));
            size = 14;
            health = 29500;
            range = 520f;
            reload = 380f;
            coolantMultiplier = 0.4f;
            shootCone = 12f;
            shootDuration = 360f;
            //shootY = 10f;
            shootY = 10f;
            shake = 4f;
            firingMoveFract = 0.09f;
            shootEffect = Fx.shootBigSmoke2;
            recoil = 7f;
            cooldownTime = 0.003f;
            heatColor = Color.white;
            rotateSpeed = 0.82f;
            shootSound = UnitySounds.extinctionShoot;
            loopSound = UnitySounds.beamIntenseHighpitchTone;
            loopSoundVolume = 2f;
            shootType = UnityBullets.extinctionLaser;
            consumePower(175f);
            consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.27f && liquid.flammability < 0.1f, 2.5f)).update(false);
        }};

        darkAlloyForge = new StemGenericCrafter("dark-alloy-forge"){{
            requirements(Category.crafting, with(Items.copper, 30, Items.lead, 25));

            outputItem = new ItemStack(UnityItems.darkAlloy, 3);
            craftTime = 140f;
            size = 4;
            ambientSound = Sounds.respawning;
            ambientSoundVolume = 0.6f;
            //drawer = new DrawSmelter();
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffef99")));

            consumeItems(with(Items.lead, 2, Items.silicon, 3, Items.blastCompound, 1, Items.phaseFabric, 1, UnityItems.umbrium, 2));
            consumePower(3.2f);

            update((StemGenericCrafterBuild e) -> {
                if(e.canConsume() && Mathf.chanceDelta(0.76f)){
                    UnityFx.craftingEffect.at(e.x, e.y, Mathf.random(360f));
                }
            });
        }};

        darkWall = new Wall("dark-wall"){{
            requirements(Category.defense, with(UnityItems.umbrium, 6));
            health = 120 * 4;
        }};

        darkWallLarge = new Wall("dark-wall-large"){{
            requirements(Category.defense, with(UnityItems.umbrium, 24));
            health = 120 * 4 * 4;
            size = 2;
        }};

        //endregion
        //region light

        photon = new LaserTurret("photon"){{
            requirements(Category.turret, with(Items.lead, 50, Items.silicon, 35, UnityItems.luminum, 65, Items.titanium, 65));
            size = 2;
            health = 1280;
            reload = 100f;
            shootCone = 30f;
            range = 120f;
            heatColor = UnityPal.lightHeat;
            loopSound = Sounds.respawning;
            shootType = new ContinuousLaserBulletType(16f){{
                incendChance = -1f;
                length = 130f;
                width = 4f;
                colors = new Color[]{Pal.lancerLaser.cpy().a(3.75f), Pal.lancerLaser, Color.white};
                //strokes = new float[]{0.92f, 0.6f, 0.28f};
                lightColor = hitColor = Pal.lancerLaser;
            }};
            consumePower(4.5f);
            consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability < 0.1f, 0.2f)).update(false);
        }};

        graviton = new LaserTurret("graviton"){{
            requirements(Category.turret, with(Items.lead, 110, Items.graphite, 90, Items.silicon, 70, UnityItems.luminum, 180, Items.titanium, 135));
            size = 3;
            health = 2780;
            reload = 150f;
            recoil = 2f;
            shootCone = 30f;
            range = 230f;
            heatColor = UnityPal.lightHeat;
            loopSound = UnitySounds.xenoBeam;
            shootType = new GravitonLaserBulletType(0.8f){{
                length = 260f;
                knockback = -5f;
                incendChance = -1f;
                colors = new Color[]{UnityPal.advanceDark.cpy().a(0.1f), Pal.lancerLaser.cpy().a(0.2f)};
                //strokes = new float[]{2.4f, 1.8f};
            }};
            consumePower(5.75f);
            consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability < 0.1f, 0.25f)).update(false);
        }};

        electron = new PowerTurret("electron"){{
            requirements(Category.turret, with(Items.lead, 110, Items.silicon, 75, UnityItems.luminum, 165, Items.titanium, 125));
            size = 3;
            health = 2540;
            reload = 60f;
            coolantMultiplier = 2f;
            range = 170f;
            heatColor = UnityPal.lightHeat;
            shootEffect = ShootFx.blueTriangleShoot;
            shootSound = Sounds.pew;
            shootType = new BasicBulletType(9f, 34f, "unity-electric-shell"){
                {
                    lifetime = 22f;
                    width = 12f;
                    height = 19f;
                    shrinkX = shrinkY = 0f;
                    backColor = lightColor = hitColor = Pal.lancerLaser;
                    frontColor = Color.white;
                    hitEffect = HitFx.electronHit;
                }

                @Override
                public void update(Bullet b){
                    super.update(b);
                    if(b.timer(0, 2f + b.fslope() * 1.5f)){
                        UnityFx.blueTriangleTrail.at(b.x, b.y, b.rotation());
                    }
                }
            };
            consumePower(6.6f);
        }};

        proton = new PowerTurret("proton"){{
            requirements(Category.turret, with(Items.lead, 110, Items.silicon, 75, UnityItems.luminum, 165, Items.titanium, 135));
            size = 4;
            health = 2540;
            reload = 60f;
            range = 245f;
            shootCone = 20f;
            heatColor = UnityPal.lightHeat;
            rotateSpeed = 1.5f;
            recoil = 4f;
            targetAir = false;
            cooldownTime = 0.008f;
            shootEffect = ShootFx.blueTriangleShoot;
            shootType = new ArtilleryBulletType(8f, 44f, "unity-electric-shell"){
                {
                    lifetime = 35f;
                    width = 18f;
                    splashDamage = 23f;
                    splashDamageRadius = 45f;
                    height = 27f;
                    shrinkX = shrinkY = 0f;
                    hitSize = 15f;
                    hitEffect = HitFx.protonHit;
                    hittable = collides = false;
                    backColor = lightColor = hitColor = lightningColor = Pal.lancerLaser;
                    frontColor = Color.white;
                    lightning = 3;
                    lightningDamage = 18f;
                    lightningLength = 10;
                    lightningLengthRand = 6;
                }

                @Override
                public void update(Bullet b){
                    super.update(b);
                    if(b.timer(0, 2f + b.fslope() * 1.5f)){
                        UnityFx.blueTriangleTrail.at(b.x, b.y, b.rotation());
                    }
                }
            };
            consumePower(4.9f);
        }};

        neutron = new PowerTurret("neutron"){{
            requirements(Category.turret, with(Items.lead, 110, Items.silicon, 75, UnityItems.luminum, 165, Items.titanium, 135));
            size = 4;
            health = 2520;
            reload = 10f;
            range = 235f;
            shootCone = 20f;
            heatColor = UnityPal.lightHeat;
            rotateSpeed = 3.9f;
            recoil = 4f;
            cooldownTime = 0.008f;
            inaccuracy = 3.4f;
            shootEffect = ShootFx.blueTriangleShoot;
            consumePower(4.9f);
            shootType = new FlakBulletType(8.7f, 7f){
                {
                    lifetime = 30f;
                    width = 8f;
                    height = 14f;
                    splashDamage = 28f;
                    splashDamageRadius = 34f;
                    shrinkX = shrinkY = 0f;
                    hitSize = 7;
                    sprite = "unity-electric-shell";
                    hitEffect = HitFx.neutronHit;
                    collides = collidesGround = true;
                    hittable = false;
                    backColor = lightColor = hitColor = Pal.lancerLaser;
                    frontColor = Color.white;
                }

                @Override
                public void update(Bullet b){
                    super.update(b);
                    if(b.timer(0, 2f + b.fslope() * 1.5f)){
                        UnityFx.blueTriangleTrail.at(b.x, b.y, b.rotation());
                    }
                }
            };
        }};

        gluon = new PowerTurret("gluon"){{
            requirements(Category.turret, with(Items.silicon, 300, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 110, UnityItems.lightAlloy, 15));
            size = 4;
            health = 5000;
            reload = 90f;
            coolantMultiplier = 3f;
            shootCone = 30f;
            range = 200f;
            heatColor = UnityPal.lightHeat;
            rotateSpeed = 4.3f;
            recoil = 2f;
            cooldownTime = 0.012f;
            shootSound = UnitySounds.gluonShoot;
            shootType = UnityBullets.gluonEnergyBall;
            consumePower(1.9f);
        }};

        wBoson = new PowerTurret("w-boson"){
            {
                requirements(Category.turret, with(Items.silicon, 300, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 110, UnityItems.lightAlloy, 15));
                health = 4000;
                size = 5;
                reload = 90f;
                range = 250f;
                rotateSpeed = 2.5f;
                shootCone = 20f;
                heatColor = UnityPal.lightHeat;
                //chargeBeginEffect = ChargeFx.wBosonChargeBeginEffect;
                //chargeEffect = ChargeFx.wBosonChargeEffect;
                //chargeTime = 38f;
                cooldownTime = 0.008f;

                shoot = new ShootPattern(){{
                    firstShotDelay = 38f;
                }};
                consumePower(8.6f);
                shootType = new DecayBasicBulletType(8.5f, 24f){{
                    drag = 0.026f;
                    lifetime = 48f;
                    hittable = absorbable = collides = false;
                    backColor = trailColor = hitColor = lightColor = Pal.lancerLaser;
                    shootEffect = smokeEffect = Fx.none;
                    chargeEffect = new MultiEffect(ChargeFx.wBosonChargeEffect, ChargeFx.wBosonChargeBeginEffect);
                    hitEffect = Fx.hitLancer;
                    despawnEffect = HitFx.lightHitLarge;
                    frontColor = Color.white;
                    decayEffect = UnityFx.wBosonEffectLong;
                    height = 13f;
                    width = 12f;
                    decayBullet = new BasicBulletType(4.8f, 24f){
                        {
                            drag = 0.04f;
                            lifetime = 18f;
                            pierce = true;
                            pierceCap = 3;
                            height = 9f;
                            width = 8f;
                            backColor = trailColor = hitColor = lightColor = Pal.lancerLaser;
                            hitEffect = Fx.hitLancer;
                            despawnEffect = HitFx.wBosonDecayHitEffect;
                            frontColor = Color.white;
                            hittable = false;
                        }

                        @Override
                        public void draw(Bullet b){
                            Draw.color(backColor);
                            Fill.circle(b.x, b.y, 1.5f + (b.fout() * 3f));
                            Draw.color(frontColor);
                            Fill.circle(b.x, b.y, 0.75f + (b.fout() * 2.75f));
                        }

                        @Override
                        public void update(Bullet b){
                            super.update(b);
                            if(Mathf.chance(0.8f)){
                                UnityFx.wBosonEffect.at(b, b.rotation() + 180f);
                            }
                        }
                    };
                    fragBullet = decayBullet;
                    fragBullets = 12;
                    fragVelocityMin = 0.75f;
                    fragVelocityMax = 1.25f;
                    fragLifeMin = 1.2f;
                    fragLifeMax = 1.3f;
                }};
            }
        };

        zBoson = new RampupPowerTurret("z-boson"){
            {
                requirements(Category.turret, with(Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 15));
                health = 4000;
                size = 5;
                reload = 40f;
                range = 230f;
                shootCone = 20f;
                heatColor = UnityPal.lightHeat;
                coolantMultiplier = 1.9f;
                rotateSpeed = 2.7f;
                recoil = 2f;
                recoilTime = 0.09f;
                cooldownTime = 0.008f;
                targetAir = true;
                shootSound = UnitySounds.zbosonShoot;
                //alternate = true;
                //shots = 2;
                //spread = 14f;
                inaccuracy = 2.3f;

                lightning = true;
                lightningThreshold = 12f;
                baseLightningLength = 16;
                lightningLengthDec = 1;
                baseLightningDamage = 18f;
                lightningDamageDec = 1f;

                barBaseY = -10.75f;
                barLength = 20f;

                consumePower(3.6f);
                shoot = new ShootAlternate(){{
                    spread = 14f;
                }};
                shootType = new VelocityLaserBoltBulletType(9.5f, 56f){{
                    splashDamage = 8f;
                    splashDamageRadius = 16f;
                    drag = 0.005f;
                    lifetime = 27f;
                    hitSize = 8f;
                    shootEffect = smokeEffect = Fx.none;
                    hitEffect = Fx.hitLancer;
                    hittable = false;
                }};
            }
        };

        higgsBoson = new PowerTurret("higgs-boson"){
            {
                requirements(Category.turret, with(Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 20));
                size = 6;
                health = 6000;
                reload = 13f;
                //alternate = true;
                //spread = 17.25f;
                //shots = 2;
                range = 260f;
                shootCone = 20f;
                heatColor = UnityPal.lightHeat;
                coolantMultiplier = 3.4f;
                rotateSpeed = 2.2f;
                recoil = 1.5f;
                recoilTime = 0.09f;
                shootSound = UnitySounds.higgsBosonShoot;
                cooldownTime = 0.008f;
                shoot = new ShootAlternate(){{
                    spread = 17.25f;
                }};
                consumePower(10.4f);
                shootType = new RoundLaserBulletType(85f){{
                    length = 270f;
                    width = 5.8f;
                    hitSize = 13f;
                    drawSize = 460f;
                    shootEffect = smokeEffect = Fx.none;
                }};
            }
        };

        singularity = new PowerTurret("singularity"){
            {
                requirements(Category.turret, with(Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 20));
                size = 7;
                health = 9800;
                reload = 220f;
                coolantMultiplier = 1.1f;
                shootCone = 30f;
                range = 310f;
                heatColor = UnityPal.lightHeat;
                rotateSpeed = 3.3f;
                recoil = 6f;
                cooldownTime = 0.012f;
                shootSound = UnitySounds.singularityShoot;
                shootType = UnityBullets.singularityEnergyBall;
                consumePower(39.3f);
            }
        };

        muon = new PowerTurret("muon"){ //Should it be animated? Since the animation in AC was disabled.
            {
                requirements(Category.turret, with(Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 25));
                size = 8;
                health = 9800;
                range = 310f;
                //shots = 9;
                //spread = 12f;
                reload = 90f;
                coolantMultiplier = 1.9f;
                shootCone = 80f;
                shake = 5f;
                recoil = 8f;
                //shootY = size * tilesize / 2f - 8f;
                shootY = size * tilesize / 2f - 8f;
                shootSound = UnitySounds.muonShoot;
                rotateSpeed = 1.9f;
                heatColor = UnityPal.lightHeat;
                cooldownTime = 0.009f;
                shoot = new ShootSpread(9, 12f);
                consumePower(18f);
                shootType = new RoundLaserBulletType(200f){{
                    length = 330f;
                    width = 3.8f;
                    hitSize = 13f;
                    hitEffect = Fx.hitLancer;
                    despawnEffect = Fx.none;
                    drawSize = 460f;
                    shootEffect = Fx.lightningShoot;
                    smokeEffect = Fx.none;
                }};
            }
        };

        ephemeron = new PowerTurret("ephemeron"){
            {
                requirements(Category.turret, with(Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 25));
                size = 8;
                health = 9800;
                range = 320f;
                reload = 70f;
                coolantMultiplier = 1.9f;
                shake = 2f;
                recoil = 4f;
                shootSound = UnitySounds.ephemeronShoot;
                rotateSpeed = 1.9f;
                heatColor = UnityPal.lightHeat;
                cooldownTime = 0.009f;
                //chargeTime = 80f;
                //chargeBeginEffect = ChargeFx.ephemeronCharge;

                shoot = new ShootPattern(){{
                    firstShotDelay = 80f;
                }};
                consumePower(26f);
                shootType = new EphemeronBulletType(7.7f, 10f){{
                    lifetime = 70f;
                    hitSize = 12f;
                    pierce = true;
                    collidesTiles = false;
                    //scaleLife = true;
                    scaleLife = true;
                    chargeEffect = ChargeFx.ephemeronCharge;
                    shootEffect = Fx.lightningShoot;
                    hitEffect = Fx.hitLancer;
                    despawnEffect = smokeEffect = Fx.none;

                    positive = new EphemeronPairBulletType(4f){{
                        positive = true;
                        frontColor = Pal.lancerLaser;
                        backColor = Color.white;
                    }};

                    negative = new EphemeronPairBulletType(4f){{
                        frontColor = Color.white;
                        backColor = Pal.lancerLaser;
                    }};
                }};
            }
        };

        lightLamp = new LightSource("light-lamp"){{
            requirements(Category.crafting, with(Items.lead, 5, Items.metaglass, 10));

            lightProduction = 0.6f;
            consumePower(1f);

            drawer = new DrawLightBlock();
        }};

        oilLamp = new LightSource("oil-lamp"){{
            requirements(Category.logic, with(Items.lead, 20, Items.metaglass, 20, Items.titanium, 15));

            size = 3;
            health = 240;
            lightProduction = 2f;

            consumePower(1.8f);
            consumeLiquid(Liquids.oil, 0.1f);

            drawer = new DrawLightBlock();
        }};

        lightLampInfi = new LightSource("light-lamp-infi"){{
            requirements(Category.logic, BuildVisibility.sandboxOnly, with());

            lightProduction = 600000f;
            drawer = new DrawLightBlock();
        }};

        lightReflector = new LightReflector("light-reflector"){{
            requirements(Category.logic, with(Items.metaglass, 10, Items.silicon, 5));
        }};

        lightDivisor = new LightReflector("light-divisor"){{
            requirements(Category.logic, with(Items.metaglass, 10, Items.titanium, 2));

            health = 80;
            fallthrough = 0.5f;
        }};

        metaglassWall = new LightWall("metaglass-wall"){{
            requirements(Category.defense, with(Items.lead, 6, Items.metaglass, 6));
            health = 350;
        }};

        metaglassWallLarge = new LightWall("metaglass-wall-large"){{
            requirements(Category.defense, with(Items.lead, 24, Items.metaglass, 24));

            size = 2;
            health = 1400;
        }};

        lightForge = new LightHoldGenericCrafter("light-forge"){{
            requirements(Category.crafting, with(Items.copper, 1));

            size = 4;
            outputItem = new ItemStack(UnityItems.lightAlloy, 3);

            consumeItems(with(Items.copper, 2, Items.silicon, 5, Items.plastanium, 2, UnityItems.luminum, 2));
            consumePower(3.5f);

            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(UnityPal.lightDark){{
                flameRadius = 7f;
                flameRadiusIn = 3.5f;
                flameRadiusMag = 3f;
                flameRadiusInMag = 1.8f;
            }});

            float req = 4f;
            acceptors.add(
                new LightAcceptorType(0, 0, req / 4f)
                    .update((LightHoldGenericCrafterBuild b, LightAcceptor s) -> s.data.floatValue = Mathf.lerpDelta(s.data.floatValue, Mathf.clamp(s.status()), warmupSpeed))
                    .draw((LightHoldGenericCrafterBuild b, LightAcceptor s) -> {
                        Draw.z(Layer.block + 0.01f);

                        Draw.alpha(s.data.floatValue);
                        Draw.blend(Blending.additive);
                        Draw.rect(Regions.lightForgeTop1Region, b.x, b.y);
                        Draw.blend();
                    }),

                new LightAcceptorType(size - 1, 0, req / 4f)
                    .update((LightHoldGenericCrafterBuild b, LightAcceptor s) -> s.data.floatValue = Mathf.lerpDelta(s.data.floatValue, Mathf.clamp(s.status()), warmupSpeed))
                    .draw((LightHoldGenericCrafterBuild b, LightAcceptor s) -> {
                        Draw.z(Layer.block + 0.01f);

                        Draw.alpha(s.data.floatValue);
                        Draw.blend(Blending.additive);
                        Draw.rect(Regions.lightForgeTop2Region, b.x, b.y);
                        Draw.blend();
                    }),

                new LightAcceptorType(size - 1, size - 1, req / 4f)
                    .update((LightHoldGenericCrafterBuild b, LightAcceptor s) -> s.data.floatValue = Mathf.lerpDelta(s.data.floatValue, Mathf.clamp(s.status()), warmupSpeed))
                    .draw((LightHoldGenericCrafterBuild b, LightAcceptor s) -> {
                        Draw.z(Layer.block + 0.01f);

                        Draw.alpha(s.data.floatValue);
                        Draw.blend(Blending.additive);
                        Draw.rect(Regions.lightForgeTop3Region, b.x, b.y);
                        Draw.blend();
                    }),

                new LightAcceptorType(0, size - 1, req / 4f)
                    .update((LightHoldGenericCrafterBuild b, LightAcceptor s) -> s.data.floatValue = Mathf.lerpDelta(s.data.floatValue, Mathf.clamp(s.status()), warmupSpeed))
                    .draw((LightHoldGenericCrafterBuild b, LightAcceptor s) -> {
                        Draw.z(Layer.block + 0.01f);

                        Draw.alpha(s.data.floatValue);
                        Draw.blend(Blending.additive);
                        Draw.rect(Regions.lightForgeTop4Region, b.x, b.y);
                        Draw.blend();
                    })
            );
        }};

        //endregion
        //region scar

        terraCore = new TerraCore("terra-core"){{
            requirements(Category.units, with(Items.copper, 1));
            size = 2;
        }};

        //endregion
        //region imber

        orb = new PowerTurret("orb"){{
            requirements(Category.turret, with(Items.copper, 55, Items.lead, 30, Items.graphite, 25, Items.silicon, 35, UnityItems.imberium, 20));
            size = 2;
            health = 480;
            range = 145f;
            reload = 130f;
            coolantMultiplier = 2f;
            shootCone = 0.1f;
            //shots = 1;
            inaccuracy = 12f;
            //chargeTime = 65f;
            //chargeEffects = 5;
            //chargeMaxDelay = 25f;
            targetAir = false;
            shootType = UnityBullets.orb;
            shootSound = Sounds.laser;
            heatColor = Pal.turretHeat;
            shootEffect = ShootFx.orbShoot;
            smokeEffect = Fx.none;
            //chargeEffect = UnityFx.orbCharge;
            //chargeBeginEffect = UnityFx.orbChargeBegin;
            shoot = new ShootPattern(){{
                firstShotDelay = 65f;
            }};
            consumePower(4.2069f);
        }};

        shockwire = new LaserTurret("shockwire"){{
            requirements(Category.turret, with(Items.copper, 150, Items.lead, 145, Items.titanium, 160, Items.silicon, 130, UnityItems.imberium, 70));
            size = 2;
            health = 860;
            range = 125f;
            reload = 140f;
            coolantMultiplier = 2f;
            shootCone = 1f;
            inaccuracy = 0f;
            targetAir = false;
            shootType = UnityBullets.shockBeam;
            shootSound = Sounds.thruster;
            consumePower(6.9420f);
            consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.4f)).update(false);
        }};

        current = new PowerTurret("current"){{
            requirements(Category.turret, with(Items.copper, 280, Items.lead, 295, Items.silicon, 260, UnityItems.sparkAlloy, 65));
            size = 3;
            health = 2400;
            range = 220f;
            reload = 120f;
            coolantMultiplier = 2;
            shootCone = 0.01f;
            inaccuracy = 0f;
            //chargeTime = 60f;
            //chargeEffects = 4;
            //chargeMaxDelay = 260;
            shootType = UnityBullets.currentStroke;
            shootSound = Sounds.laserbig;
            //chargeEffect = UnityFx.currentCharge;
            //chargeBeginEffect = UnityFx.currentChargeBegin;
            shoot = new ShootPattern(){{
                firstShotDelay = 60f;
            }};
            consumePower(6.8f);
            consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.52f)).boost();
        }};

        plasma = new PowerTurret("plasma"){{
            requirements(Category.turret, with(Items.copper, 580, Items.lead, 520, Items.graphite, 410, Items.silicon, 390, Items.surgeAlloy, 180, UnityItems.sparkAlloy, 110));
            size = 4;
            health = 2800;
            range = 200f;
            reload = 360f;
            recoil = 4f;
            coolantMultiplier = 1.2f;
            liquidCapacity = 20f;
            shootCone = 1f;
            inaccuracy = 0f;
            shootType = UnityBullets.plasmaTriangle;
            shootSound = Sounds.shotgun;
            consumePower(8.2f);
            consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.52f)).boost();
        }};

        electrobomb = new ItemTurret("electrobomb"){
            {
                requirements(Category.turret, with(Items.titanium, 360, Items.thorium, 630, Items.silicon, 240, UnityItems.sparkAlloy, 420));
                health = 3650;
                size = 5;
                range = 400f;
                minRange = 60f;
                reload = 320f;
                coolantMultiplier = 2f;
                shootCone = 20f;
                //shots = 1;
                inaccuracy = 0f;
                targetAir = false;
                ammo(UnityItems.sparkAlloy, UnityBullets.surgeBomb);
                shootSound = Sounds.laser;
                shootEffect = Fx.none;
                smokeEffect = Fx.none;
                consumePowerCond(10f, TurretBuild::isActive);
            }
        };

        shielder = new ShieldTurret("shielder"){{
            requirements(Category.turret, with(Items.copper, 300, Items.lead, 100, Items.titanium, 160, Items.silicon, 240, UnityItems.sparkAlloy, 90));
            size = 3;
            health = 900;
            range = 260;
            reload = 800;
            coolantMultiplier = 2;
            shootCone = 60;
            inaccuracy = 0;
            targetAir = false;
            shootType = UnityBullets.shielderBullet;
            shootSound = /*test*/Sounds.pew;
            /*chargeEffect = new Effect(38f, e -> {
                Draw.color(Pal.accent);
                Angles.randLenVectors(e.id, 2, 1 + 20 * e.fout(), e.rotation, 120, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3 + 1));
            });*/
            //chargeBeginEffect = Fx.none;
            consumePower(6.4f);
            consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.4f)).update(false);
        }};

        orbTurret = new OrbTurret("orb-turret"){{
            requirements(Category.turret, BuildVisibility.shown, ItemStack.with(Items.copper, 1));

            size = 3;

            consumePower(0.3f);
            shootType = new BasicBulletType(){
                @Override
                public void draw(Bullet b){
                    Draw.color(((Color[])b.data)[0]);
                    b.trail.draw(((Color[])b.data)[1], 1f);
                    b.trail.drawCap(((Color[])b.data)[1], 1f);
                }

                @Override
                public void update(Bullet b) {
                    super.update(b);
                    b.trail.update(b.x, b.y);
                }

                @Override
                public void removed(Bullet b) {
                    b.trail = null;
                    super.removed(b);
                }

                {
                    damage = 20f;
                }
            };
        }};

        powerPlant = new PowerPlant("power-plant"){{
            requirements(Category.power, BuildVisibility.editorOnly, ItemStack.with(Items.copper, 1));

            powerProduction = 8.6f;
        }};

        sparkAlloyForge = new StemGenericCrafter("spark-alloy-forge"){{
            requirements(Category.crafting, with(Items.lead, 160, Items.graphite, 340, UnityItems.imberium, 270, Items.silicon, 250, Items.thorium, 120, Items.surgeAlloy, 100));

            outputItem = new ItemStack(UnityItems.sparkAlloy, 4);
            size = 4;
            craftTime = 160f;
            ambientSound = Sounds.machine;
            ambientSoundVolume = 0.6f;
            craftEffect = UnityFx.imberCircleSparkCraftingEffect;
            //drawer = new DrawSmelter();
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffef99")));

            consumePower(2.6f);
            consumeItems(with(Items.surgeAlloy, 3, Items.titanium, 4, Items.silicon, 6, UnityItems.imberium, 3));

            update((StemGenericCrafterBuild e) -> {
                if(e.canConsume()){
                    if(Mathf.chanceDelta(0.3f)){
                        UnityFx.imberSparkCraftingEffect.at(e.x, e.y, Mathf.random(360f));
                    }else if(Mathf.chanceDelta(0.02f)){
                        Lightning.create(e.team, UnityPal.imberColor, 5f, e.x, e.y, Mathf.random(360f), 5);
                    }
                }
            });
        }};

        absorber = new AbsorberTurret("absorber"){{
            requirements(Category.power, with(UnityItems.imberium, 20, Items.lead, 20));

            consumesPower = false;

            powerProduction = 1.2f;
            range = 50f;

            targetUnits = true;
            status = StatusEffects.slow;

            rotateSpeed = 1.2f;
            shootCone = 2f;
            damage = 0.6f;
        }};

        electroTile = new Floor("electro-tile");

        piper = new UnderPiper("piper", 80){{
            requirements(Category.distribution, with(Items.copper, 1));

            size = 4;
        }};

        //endregion
        //region koruh

        denseSmelter = new KoruhCrafter("dense-smelter"){{
            requirements(Category.crafting, with(Items.copper, 30, Items.lead, 20, UnityItems.stone, 35));

            health = 70;
            hasItems = true;
            craftTime = 46.2f;
            craftEffect = UnityFx.denseCraft;
            itemCapacity = 10;

            outputItem = new ItemStack(UnityItems.denseAlloy, 1);
            consumeItems(with(Items.copper, 1, Items.lead, 2, Items.coal, 1));

            expUse = 2;
            expCapacity = 24;
            drawer = new DrawExp(){{
                flame = Color.orange;
                glowAmount = 1f;
            }};
        }};

        solidifier = new LiquidsSmelter("solidifier"){{
            requirements(Category.crafting, with(Items.copper, 20, UnityItems.denseAlloy, 30));

            health = 150;
            hasItems = true;
            liquidCapacity = 12f;
            updateEffect = Fx.fuelburn;
            craftEffect = UnityFx.rockFx;
            craftTime = 60f;
            outputItem = new ItemStack(UnityItems.stone, 1);

            consume(new ConsumeLiquids(new LiquidStack[]{new LiquidStack(UnityLiquids.lava, 0.1f), new LiquidStack(Liquids.water, 0.1f)}));

            /*drawer = new DrawGlow(){
                @Override
                public void draw(GenericCrafterBuild build){
                    Draw.rect(build.block.region, build.x, build.y);
                    Draw.color(liquids[0].color, build.liquids.get(liquids[0]) / liquidCapacity);
                    Draw.rect(top, build.x, build.y);
                    Draw.reset();
                }
            };*/
        }};

        steelSmelter = new GenericCrafter("steel-smelter"){{
            requirements(Category.crafting, with(Items.lead, 45, Items.silicon, 20, UnityItems.denseAlloy, 30));
            health = 140;
            itemCapacity = 10;
            craftEffect = UnityFx.craft;
            updateEffect = Fx.fuelburn;
            craftTime = 300f;
            outputItem = new ItemStack(UnityItems.steel, 1);

            consumePower(2f);
            consumeItems(with(Items.coal, 2, Items.graphite, 2, UnityItems.denseAlloy, 3));

            /*drawer = new DrawGlow(){
                @Override
                public void draw(GenericCrafterBuild build){
                    Draw.rect(build.block.region, build.x, build.y);
                    Draw.color(1f, 1f, 1f, build.warmup * Mathf.absin(8f, 0.6f));
                    Draw.rect(top, build.x, build.y);
                    Draw.reset();
                }
            };*/
        }};

        lavaSmelter = new MeltingCrafter("lava-smelter"){{
            requirements(Category.crafting, with(Items.silicon, 70, UnityItems.denseAlloy, 60, UnityItems.steel, 40));

            health = 190;
            hasLiquids = true;
            hasItems = true;
            craftTime = 70f;
            updateEffect = Fx.fuelburn;
            craftEffect = UnityFx.craft;
            itemCapacity = 21;

            outputItem = new ItemStack(UnityItems.steel, 5);
            consumeItems(with(Items.graphite, 7, UnityItems.denseAlloy, 7));
            consumePower(2f);
            consumeLiquid(UnityLiquids.lava, 0.4f);

            expUse = 10;
            expCapacity = 60;
            //drawer = new DrawLiquid();
        }};

        liquifier = new BurnerSmelter("liquifier"){{
            requirements(Category.crafting, with(Items.titanium, 30, Items.silicon, 15, UnityItems.steel, 10));
            health = 100;
            hasLiquids = true;
            updateEffect = Fx.fuelburn;
            craftTime = 30f;
            outputLiquid = new LiquidStack(UnityLiquids.lava, 0.1f);

            configClear(b -> Fires.create(b.tile));
            consumePower(3.7f);

            update((BurnerSmelterBuild e) -> {
                if(e.progress == 0f && e.warmup > 0.001f && (Vars.net.server() || !Vars.net.active()) && Mathf.chanceDelta(0.2f)){
                    e.configureAny(null);
                }
            });

            /*drawer = new DrawGlow(){
                @Override
                public void draw(GenericCrafterBuild build){
                    Draw.rect(build.block.region, build.x, build.y);

                    Liquid liquid = outputLiquid.liquid;
                    Draw.color(liquid.color, build.liquids.get(liquid) / liquidCapacity);
                    Draw.rect(top, build.x, build.y);
                    Draw.color();

                    Draw.reset();
                }
            };*/
        }};

        titaniumExtractor = new GenericCrafter("titanium-extractor"){{
            requirements(Category.crafting, with(Items.lead, 20, Items.metaglass, 10, UnityItems.denseAlloy, 30));

            health = 160;
            hasLiquids = true;
            updateEffect = UnityFx.craftFx;
            itemCapacity = 10;
            craftTime = 360f;
            outputItem = new ItemStack(Items.titanium, 1);

            consumePower(1f);
            consumeItems(with(UnityItems.denseAlloy, 3, UnityItems.steel, 2));
            consumeLiquid(Liquids.water, 0.3f);

            /*drawer = new DrawGlow(){
                @Override
                public void draw(GenericCrafterBuild build){
                    Draw.rect(build.block.region, build.x, build.y);
                    Draw.color(UnityItems.denseAlloy.color, Items.titanium.color, build.progress);
                    Draw.alpha(0.6f);
                    Draw.rect(top, build.x, build.y);
                    Draw.reset();
                }
            };*/
        }};

        diriumCrucible = new KoruhCrafter("dirium-crucible"){{
            requirements(Category.crafting, with(Items.plastanium, 60, UnityItems.stone, 90, UnityItems.denseAlloy, 90, UnityItems.steel, 150));

            health = 320;
            hasItems = true;
            craftTime = 250f;
            craftEffect = UnityFx.diriumCraft;
            itemCapacity = 40;
            ambientSound = Sounds.techloop;
            ambientSoundVolume = 0.02f;

            outputItem = new ItemStack(UnityItems.dirium, 1);
            consumeItems(with(Items.titanium, 6, Items.pyratite, 3, Items.surgeAlloy, 3, UnityItems.steel, 9));
            consumePower(8.28f);

            expUse = 40;
            expCapacity = 160;
            ignoreExp = false;
            craftDamage = 0;
            drawer = new DrawExp();
        }};

        coalExtractor = new KoruhCrafter("coal-extractor"){{
            requirements(Category.crafting, with(Items.silicon, 80, UnityItems.stone, 100, UnityItems.steel, 150));

            health = 250;
            hasItems = true;
            craftTime = 240f;
            craftEffect = UnityFx.craftFx;
            itemCapacity = 50;

            consumeItems(with(UnityItems.stone, 6, Items.scrap, 2));
            consumeLiquid(Liquids.water, 0.5f);
            consumePower(6f);
            outputItem = new ItemStack(Items.coal, 1);

            expUse = 30;
            expCapacity = 120;
            craftDamage = 0;
            drawer = new DrawExp();
            ignoreExp = false;

            ambientSound = Sounds.techloop;
            ambientSoundVolume = 0.01f;
        }};

        stoneWall = new LimitWall("ustone-wall"){{
            requirements(Category.defense, with(UnityItems.stone, 6));
            maxDamage = 40f;
            health = 200;
        }};

        denseWall = new LimitWall("dense-wall"){{
            requirements(Category.defense, with(UnityItems.denseAlloy, 6));
            maxDamage = 32f;
            health = 560;
        }};

        steelWall = new LevelLimitWall("steel-wall"){{
            requirements(Category.defense, with(UnityItems.steel, 6));
            maxDamage = 24f;
            health = 810;

            maxLevel = 6;
            expFields = new EField[]{
                    new ERational(v -> maxDamage = v, 48f, 24f, -3f, Stat.abilities, v -> bundle.format("stat.unity.maxdamage", v)).formatAll(false)
            };
        }};

        steelWallLarge = new LevelLimitWall("steel-wall-large"){{
            requirements(Category.defense, with(UnityItems.steel, 24));
            maxDamage = 48f;
            health = 3240;
            size = 2;

            maxLevel = 12;
            expFields = new EField[]{
                    new ERational(v -> maxDamage = v, 72f, 24f, -3f, Stat.abilities, v -> bundle.format("stat.unity.maxdamage", v)).formatAll(false)
            };
        }};

        diriumWall = new LevelLimitWall("dirium-wall"){{
            requirements(Category.defense, with(UnityItems.dirium, 6));
            maxDamage = 76f;
            blinkFrame = 30f;
            health = 760;
            updateEffect = UnityFx.sparkle;

            maxLevel = 6;
            expFields = new EField[]{
                    new ERational(v -> maxDamage = v, 152f, 50f, -3f, Stat.abilities, v -> bundle.format("stat.unity.maxdamage", v)).formatAll(false),
                    new ELinearCap(v -> blinkFrame = v, 10f, 10f, 2, Stat.abilities, v -> bundle.format("stat.unity.blinkframe", v)).formatAll(false)
            };
        }};

        diriumWallLarge = new LevelLimitWall("dirium-wall-large"){{
            requirements(Category.defense, with(UnityItems.dirium, 24));
            maxDamage = 152f;
            blinkFrame = 30f;
            health = 3040;
            size = 2;
            updateEffect = UnityFx.sparkle;

            maxLevel = 12;
            expFields = new EField[]{
                    new ERational(v -> maxDamage = v, 304f, 50f, -2f, Stat.abilities, v -> bundle.format("stat.unity.maxdamage", v)).formatAll(false),
                    new ELinearCap(v -> blinkFrame = v, 10f, 5f, 4, Stat.abilities, v -> bundle.format("stat.unity.blinkframe", v)).formatAll(false)
            };
        }};

        shieldProjector = new ClassicProjector("shield-generator"){{
            requirements(Category.effect, with(Items.silicon, 50, Items.titanium, 35, UnityItems.steel, 15));
            health = 200;
            cooldownNormal = 1f;
            cooldownBrokenBase = 0.3f;
            phaseRadiusBoost = 10f;
            phaseShieldBoost = 200;
            hasItems = hasLiquids = false;

            consumePower(1.5f);

            maxLevel = 15;
            expFields = new EField[]{
                    new ELinear(v -> radius = v, 40f, 0.5f, Stat.range, v -> Strings.autoFixed(v / tilesize, 2) + " blocks"),
                    new ELinear(v -> shieldHealth = v, 500f, 25f, Stat.shieldHealth)
            };
            fromColor = toColor = Pal.lancerLaser;
        }};

        diriumProjector = new ClassicProjector("deflect-generator"){{
            requirements(Category.effect, with(Items.silicon, 50, Items.titanium, 30, UnityItems.steel, 30, UnityItems.dirium, 8));
            health = 800;
            size = 2;
            cooldownNormal = 1.5f;
            cooldownLiquid = 1.2f;
            cooldownBrokenBase = 0.35f;
            phaseRadiusBoost = 40f;

            consumeItem(Items.phaseFabric).boost();
            consumePower(5f);

            fromColor = Pal.lancerLaser;
            toColor = UnityPal.diriumLight;
            maxLevel = 30;
            expFields = new EField[]{
                    new ELinear(v -> radius = v, 60f, 0.75f, Stat.range, v -> Strings.autoFixed(v / tilesize, 2) + " blocks"),
                    new ELinear(v -> shieldHealth = v, 820f, 35f, Stat.shieldHealth),
                    new ELinear(v -> deflectChance = v, 0f, 0.1f, Stat.baseDeflectChance, v -> Strings.autoFixed(v * 100, 1) + "%")
            };
            pregrade = (ClassicProjector) shieldProjector;
            pregradeLevel = 5;
            effectColors = new Color[]{Pal.lancerLaser, UnityPal.lancerDir1, UnityPal.lancerDir2, UnityPal.lancerDir3, UnityPal.diriumLight};
        }};

        timeMine = new TimeMine("time-mine"){{
            requirements(Category.effect, with(Items.lead, 25, Items.silicon, 12));
            hasShadow = false;
            health = 45;
            pullTime = 6 * 60f;
        }};

        steelConveyor = new KoruhConveyor("steel-conveyor"){{
            requirements(Category.distribution, with(UnityItems.stone, 1, UnityItems.denseAlloy, 1, UnityItems.steel, 1));
            health = 140;
            speed = 0.1f;
            displayedSpeed = 12.5f;
            drawMultiplier = 1.9f;
        }};

        diriumConveyor = new ExpKoruhConveyor("dirium-conveyor"){{
            requirements(Category.distribution, with(UnityItems.steel, 1, Items.phaseFabric, 1, UnityItems.dirium, 1));
            health = 150;
            speed = 0.16f;
            displayedSpeed = 20f;
            drawMultiplier = 1.3f;

            draw = new DrawOver();
        }};

        bufferPad = new MechPad("buffer-pad"){{
            requirements(Category.units, with(UnityItems.stone, 120, Items.copper, 170, Items.lead, 150, Items.titanium, 150, Items.silicon, 180));
            size = 2;
            craftTime = 100;
            consumePower(0.7f);
            unitType = UnityUnitTypes.buffer;
        }};

        omegaPad = new MechPad("omega-pad"){{
            requirements(Category.units, with(UnityItems.stone, 220, Items.lead, 200, Items.silicon, 230, Items.thorium, 260, Items.surgeAlloy, 100));
            size = 3;
            craftTime = 300f;
            consumePower(1.2f);
            unitType = UnityUnitTypes.omega;
        }};

        cachePad = new MechPad("cache-pad"){{
            requirements(Category.units, with(UnityItems.stone, 150, Items.lead, 160, Items.silicon, 100, Items.titanium, 60, Items.plastanium, 120, Items.phaseFabric, 60));
            size = 2;
            craftTime = 130f;
            consumePower(0.8f);
            unitType = UnityUnitTypes.cache;
        }};

        convertPad = new ConversionPad("conversion-pad"){{
            requirements(Category.units, BuildVisibility.sandboxOnly, empty);
            size = 2;
            craftTime = 60f;
            consumePower(1f);
            upgrades.add(
                new UnitType[]{UnitTypes.dagger, UnitTypes.mace},
                new UnitType[]{UnitTypes.flare, UnitTypes.horizon},
                new UnitType[]{UnityUnitTypes.cache, UnityUnitTypes.dijkstra},
                new UnitType[]{UnityUnitTypes.omega, UnitTypes.reign}
            );
        }};

        uraniumReactor = new KoruhReactor("uranium-reactor"){{
                requirements(Category.power, with(Items.plastanium, 80, Items.surgeAlloy, 100, Items.lead, 150, UnityItems.steel, 200));
                size = 3;

                itemDuration = 200f;
                consumeItem(UnityItems.uranium, 2);
                consumeLiquid(Liquids.water, 0.7f);
                consumePower(20f);

                itemCapacity = 20;
                powerProduction = 150f;
                health = 1000;

                plasma1 = Color.valueOf("a5e1a2");
                plasma2 = Color.valueOf("869B84");
        }};

        teleporter = new Teleporter("teleporter"){{
            requirements(Category.distribution, with(Items.lead, 22, Items.silicon, 10, Items.phaseFabric, 32, UnityItems.dirium, 32));
        }};

        teleunit = new TeleUnit("teleunit"){{
            requirements(Category.units, with(Items.lead, 180, Items.titanium, 80, Items.silicon, 90, Items.phaseFabric, 64, UnityItems.dirium, 48));
            size = 3;
            ambientSound = Sounds.techloop;
            ambientSoundVolume = 0.02f;
            consumePower(3f);
        }};

        laser = new ExpPowerTurret("laser-turret"){{
            requirements(Category.turret, with(Items.copper, 90, Items.silicon, 40, Items.titanium, 15));
            size = 2;
            health = 600;

            reload = 35f;
            coolantMultiplier = 2f;
            range = 140f;
            targetAir = false;
            shootSound = Sounds.laser;

            shootType = UnityBullets.laser;

            maxLevel = 10;
            expFields = new EField[]{
                new LinearReloadTime(v -> reload = v, 45f, -2f),
                new ELinear(v -> range = v, 120f, 2f, Stat.shootRange, v -> Strings.autoFixed(v / tilesize, 2) + " blocks"),
                new EBool(v -> targetAir = v, false, 5, Stat.targetsAir)
            };
            consumePower(7f);
        }};

        laserCharge = new ExpPowerTurret("charge-laser-turret"){{
            requirements(Category.turret, with(UnityItems.denseAlloy, 60, Items.graphite, 15));
            size = 2;
            health = 1400;

            reload = 60f;
            coolantMultiplier = 2f;
            range = 140f;

            //chargeTime = 50f;
            //chargeMaxDelay = 30f;
            //chargeEffects = 4;
            recoil = 2f;
            cooldownTime = 0.03f;
            targetAir = true;
            shake = 2f;

            shootEffect = ShootFx.laserChargeShoot;
            smokeEffect = Fx.none;
            //chargeEffect = UnityFx.laserCharge;
            //chargeBeginEffect = UnityFx.laserChargeBegin;
            heatColor = Color.red;
            shootSound = Sounds.laser;

            shootType = UnityBullets.shardLaser;

            maxLevel = 30;
            expFields = new EField[]{
                new LinearReloadTime(v -> reload = v, 60f, -1f),
                new ELinear(v -> range = v, 140f, 1.3f, Stat.shootRange, v -> Strings.autoFixed(v / tilesize, 2) + " blocks")
            };
            pregrade = (ExpTurret) laser;
            effectColors = new Color[]{Pal.lancerLaser, UnityPal.lancerSap1, UnityPal.lancerSap2, UnityPal.lancerSap3, UnityPal.lancerSap4, UnityPal.lancerSap5, Pal.sapBullet};
            shoot = new ShootPattern(){{
                firstShotDelay = 50f;
            }};
            consumePower(7f);
        }};

        laserFrost = new ExpLiquidTurret("frost-laser-turret"){{
            ammo(Liquids.cryofluid, UnityBullets.frostLaser);
            requirements(Category.turret, with(UnityItems.denseAlloy, 60, Items.metaglass, 15));
            size = 2;
            health = 1000;

            range = 160f;
            reload = 80f;
            targetAir = true;
            liquidCapacity = 10f;
            shootSound = Sounds.laser;
            extinguish = false;

            maxLevel = 30;

            consumePowerCond(1f, TurretBuild::isActive);
            pregrade = (ExpTurret) laser;
        }};

        laserFractal = new ExpPowerTurret("fractal-laser-turret"){{
            requirements(Category.turret, with(UnityItems.steel, 50, Items.graphite, 90, Items.thorium, 95));
            size = 3;
            health = 2000;

            reload = UnityBullets.distField.lifetime / 3f;
            coolantMultiplier = 2f;
            range = 140f;

            //chargeTime = 80f;
            //chargeMaxDelay = 20f;
            //chargeEffects = 8;
            recoil = 4f;

            cooldownTime = 0.03f;
            targetAir = true;
            shake = 5f;

            shootEffect = ShootFx.laserFractalShoot;
            smokeEffect = Fx.none;
            //chargeEffect = UnityFx.laserFractalCharge;
            //chargeBeginEffect = UnityFx.laserFractalChargeBegin;
            shootSound = Sounds.laser;

            heatColor = Color.red;
            fromColor = UnityPal.lancerSap3;
            toColor = Pal.place;

            shootType = UnityBullets.fractalLaser;

            maxLevel = 30;
            expFields = new EField[]{
                new LinearReloadTime(v -> reload = v, UnityBullets.distField.lifetime / 3f, -2f),
                new ELinear(v -> range = v, 140f, 0.25f * tilesize, Stat.shootRange, v -> Strings.autoFixed(v / tilesize, 2) + " blocks")
            };

            shoot = new ShootPattern(){{
                firstShotDelay = 80f;
            }};
            pregrade = (ExpTurret) laserCharge;
            pregradeLevel = 15;
            effectColors = new Color[]{fromColor, Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.75f), Pal.sapBullet};
            consumePower(13f);
        }};

        laserBranch = new BurstChargePowerTurret("swarm-laser-turret"){{
            requirements(Category.turret, with(UnityItems.steel, 50, Items.silicon, 90, Items.thorium, 95));

            size = 3;
            health = 2400;

            reload = 90f;
            coolantMultiplier = 2.25f;
            targetAir = true;
            range = 150f;

            //chargeTime = 50f;
            //chargeMaxDelay = 30f;
            //chargeEffects = 4;
            recoil = 2f;

            cooldownTime = 0.03f;
            shake = 2f;
            shootEffect = ShootFx.laserChargeShootShort;
            smokeEffect = Fx.none;
            //chargeEffect = UnityFx.laserChargeShort;
            //chargeBeginEffect = UnityFx.laserChargeBegin;
            heatColor = Color.red;
            fromColor = UnityPal.lancerSap3;
            shootSound = Sounds.plasmaboom;
            shootType = UnityBullets.branchLaser;

            shootY = size * tilesize / 2.7f;
            //shots = 4;
            //burstSpacing = 20f;
            inaccuracy = 1f;
            //spread = 0f;
            xRand = 6f;

            shootY = size * tilesize / 2.7f;
            shoot = new ShootPattern(){{
                shots = 4;
                firstShotDelay = 50f;
                shotDelay = 20f;
            }};
            maxLevel = 30;
            expFields = new EField[]{
                new ELinearCap(v -> shoot.shots = (int)v, 2, 0.35f, 15, Stat.shots),
                new ELinearCap(v -> inaccuracy = v, 1f, 0.25f, 10, Stat.inaccuracy, v -> Strings.autoFixed(v, 1) + " degrees"),
                new ELinear(v -> shoot.shotDelay = v, 20f, -0.5f, null),
                new ELinear(v -> range = v, 150f, 2f, Stat.shootRange, v -> Strings.autoFixed(v / tilesize, 2) + " blocks")
            };
            consumePower(15f);
            pregrade = (ExpTurret) laserCharge;
            pregradeLevel = 15;
            effectColors = new Color[]{UnityPal.lancerSap3, UnityPal.lancerSap4, UnityPal.lancerSap5, Pal.sapBullet};
        }};

        laserKelvin = new OmniLiquidTurret("kelvin-laser-turret"){{
            requirements(Category.turret, with(Items.phaseFabric, 50, Items.metaglass, 90, Items.thorium, 95));
            size = 3;
            health = 2100;

            range = 180f;
            reload = 120f;
            targetAir = true;
            liquidCapacity = 15f;
            shootAmount = 3f;
            shootSound = Sounds.laser;

            shootType = new GeyserLaserBulletType(185f, 30f){{
                geyser = UnityBullets.laserGeyser;
                damageInc = 5f;
                maxRange = 185f;
            }};

            consumePowerCond(2.5f, TurretBuild::isActive);

            maxLevel = 30;
            pregrade = (ExpTurret) laserFrost;
            pregradeLevel = 15;
        }};

        laserBreakthrough = new ExpPowerTurret("bt-laser-turret"){{
            requirements(Category.turret, with(UnityItems.dirium, 190, Items.silicon, 230, Items.thorium, 450, UnityItems.steel, 230));
            size = 4;
            health = 2800;

            range = 500f;
            coolantMultiplier = 1.5f;
            targetAir = true;
            reload = 500f;

            //chargeTime = 100f;
            //chargeMaxDelay = 100f;
            //chargeEffects = 0;

            recoil = 5f;
            cooldownTime = 0.03f;

            shake = 4f;
            shootEffect = ShootFx.laserBreakthroughShoot;
            smokeEffect = Fx.none;
            //chargeEffect = Fx.none;
            //chargeBeginEffect = UnityFx.laserBreakthroughChargeBegin;

            heatColor = fromColor = Pal.lancerLaser;
            toColor = UnityPal.exp;
            shootSound = Sounds.laserblast;
            chargeSound = Sounds.lasercharge;
            shootType = UnityBullets.breakthroughLaser.copy();

            maxLevel = 1;
            expScale = 30;
            pregrade = (ExpTurret) laserCharge;
            pregradeLevel = 30;
            expFields = new EField[]{
                //new EList<>(v -> chargeBeginEffect = v, new Effect[]{UnityFx.laserBreakthroughChargeBegin, UnityFx.laserBreakthroughChargeBegin2}, null)
                new EList<>(v -> shootType.chargeEffect = v, new Effect[]{UnityFx.laserBreakthroughChargeBegin, UnityFx.laserBreakthroughChargeBegin2}, null)
            };
            consumePower(17f);
            effectColors = new Color[]{Pal.lancerLaser, UnityPal.exp};

            /*drawer = b -> {
                if(b instanceof ExpPowerTurretBuild tile){
                    Draw.rect(region, tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90f);
                    if(tile.level() >= tile.maxLevel()){
                        //Draw.blend(Blending.additive);
                        Draw.color(tile.shootColor(Tmp.c2));
                        Draw.alpha(Mathf.absin(Time.time, 20f, 0.6f));
                        Draw.rect(Regions.btLaserTurretTopRegion, tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90f);
                        Draw.color();
                        //Draw.blend();
                    }
                }else{
                    throw new IllegalStateException("building isn't an instance of ExpPowerTurretBuild");
                }
            };*/
        }};

        inferno = new ExpItemTurret("inferno"){{
            requirements(Category.turret, with(UnityItems.stone, 150, UnityItems.denseAlloy, 65, Items.graphite, 60));
            ammo(
                //Items.scrap, Bullets.slagShot,
                Items.coal, UnityBullets.coalBlaze,
                Items.pyratite, UnityBullets.pyraBlaze
            );

            size = 3;
            range = 80f;
            reload = 6f;
            coolantMultiplier = 2f;
            recoil = 0f;
            shootCone = 5f;
            shootSound = Sounds.flame;

            maxLevel = 10;
            expFields = new EField[]{
                    new EList<>(v -> shoot.shots = v, new Integer[]{1, 1, 2, 2, 2, 3, 3, 4, 4, 5, 5}, Stat.shots),
                    //new EList<>(v -> spread = v, new Float[]{0f, 0f, 5f, 10f, 15f, 7f, 14f, 8f, 10f, 6f, 9f}, null)
            };
        }};

        expHub = new ExpHub("exp-output"){{
            requirements(Category.effect, with(UnityItems.stone, 30, Items.copper, 15));
            expCapacity = 100;
        }};

        expRouter = new ExpRouter("exp-router"){{
            requirements(Category.effect, with(UnityItems.stone, 5));
        }};

        expTower = new ExpTower("exp-tower"){{
            requirements(Category.effect, with(UnityItems.denseAlloy, 10, Items.silicon, 5));
            expCapacity = 100;
        }};

        expTowerDiagonal = new DiagonalTower("diagonal-tower"){{
            requirements(Category.effect, with(UnityItems.steel, 10, Items.silicon, 5));
            range = 7;
            expCapacity = 150;
        }};

        bufferTower = new ExpTower("buffer-tower"){{
            requirements(Category.effect, with(Items.thorium, 5, Items.graphite, 10));
            manualReload = reload = 20f;
            expCapacity = 180;
            buffer = true;
            health = 300;
        }};

        expNode = new ExpNode("exp-node"){{
            requirements(Category.effect, with(UnityItems.denseAlloy, 30, Items.silicon, 30, UnityItems.steel, 8));
            expCapacity = 200;
            consumePower(0.6f);
        }};

        expNodeLarge = new ExpNode("exp-node-large"){{
            requirements(Category.effect, with(UnityItems.denseAlloy, 120, Items.silicon, 120, UnityItems.steel, 24));
            expCapacity = 600;
            range = 10;
            health = 200;
            size = 2;
            consumePower(1.4f);
        }};

        expTank = new ExpTank("exp-tank"){{
            requirements(Category.effect, with(Items.copper, 100, UnityItems.denseAlloy, 100, Items.graphite, 30));
            expCapacity = 800;
            health = 300;
            size = 2;
        }};

        expChest = new ExpTank("exp-chest"){{
            requirements(Category.effect, with(Items.copper, 400, UnityItems.steel, 250, Items.phaseFabric, 120));
            expCapacity = 3600;
            health = 1200;
            size = 4;
        }};

        expFountain = new ExpSource("exp-fountain"){{
            requirements(Category.effect, BuildVisibility.sandboxOnly, with());
        }};

        expVoid = new ExpVoid("exp-void"){{
            requirements(Category.effect, BuildVisibility.sandboxOnly, with());
        }};

        buffTurret = new BlockOverdriveTurret("buff-turret"){{
            requirements(Category.effect, with(Items.thorium, 60, Items.plastanium, 90, UnityItems.stone, 100, UnityItems.denseAlloy, 70));
            health = 200;
            size = 1;
            buffRange = 100f;
            consumeItem(UnityItems.steel).boost();
        }};

        upgradeTurret = new BlockOverdriveTurret("upgrade-turret"){{
            requirements(Category.effect, with(Items.surgeAlloy, 80, UnityItems.steel, 120, UnityItems.dirium, 70));
            health = 300;
            size = 1;
            buffRange = 100f;
            consumeItem(UnityItems.dirium).boost();
        }};

        shieldWall = new ShieldWall("shield-wall"){{
            requirements(Category.defense, with());

            health = 500;
            shieldHealth = 500;
            maxDamage = 50f;
            size = 1;
            maxLevel = 10;

            expFields = new EField[]{
                    new ERational(v -> maxDamage = v, 100f, 25f, -3f, Stat.abilities, v -> bundle.format("stat.unity.maxdamage", v)).formatAll(false),
                    new ELinear(v -> repair = v, 50f, 10f, Stat.repairSpeed, v -> bundle.format("stat.unity.repairspeed", v)).formatAll(false),
                    new ELinear(v -> shieldHealth = v, 500, 25, Stat.shieldHealth)
            };
        }};

        shieldWallLarge = new ShieldWall("shield-wall-large"){{
            requirements(Category.defense, with());

            health = 2000;
            maxDamage = 100f;
            shieldHealth = 2000;
            size = 2;
            maxLevel = 20;

            expFields = new EField[]{
                    new ERational(v -> maxDamage = v, 200f, 50f, -3f, Stat.abilities, v -> bundle.format("stat.unity.maxdamage", v)).formatAll(false),
                    new ELinear(v -> repair = v, 200f, 20f, Stat.repairSpeed, v -> bundle.format("stat.unity.repairspeed", v)).formatAll(false),
                    new ELinear(v -> shieldHealth = v, 2000, 50, Stat.shieldHealth)
            };
        }};

        //endregion
        //region monolith

        oreMonolite = new UnityOreBlock(UnityItems.monolite){{
            oreScale = 23.77f;
            oreThreshold = 0.807f;
            oreDefault = false;
        }};

        sharpslate = new Floor("sharpslate"){{
            variants = 3;
        }};

        infusedSharpslate = new Floor("infused-sharpslate"){
            {
                variants = 3;
                emitLight = true;
                lightRadius = 24f;
                lightColor = UnityPal.monolith.cpy().a(0.1f);
            }

            @Override
            public void createIcons(MultiPacker packer){
                super.createIcons(packer);
                mapColor.lerp(UnityPal.monolith, 0.2f);
            }
        };

        archSharpslate = new Floor("archaic-sharpslate"){
            {
                variants = 3;
                //updateEffect = UnityFx.archaicEnergy;
                emitLight = true;
                lightRadius = 24f;
                lightColor = UnityPal.monolithLight.cpy().a(0.12f);
            }

            @Override
            public void createIcons(MultiPacker packer){
                super.createIcons(packer);
                mapColor.lerp(UnityPal.monolith, 0.4f);
            }
        };

        sharpslateWall = new StaticWall("sharpslate-wall"){{
            variants = 2;
            sharpslate.asFloor().wall = this;
        }};

        infusedSharpslateWall = new StaticWall("infused-sharpslate-wall"){{
            variants = 2;
            infusedSharpslate.asFloor().wall = this;
            archSharpslate.asFloor().wall = this;
        }};

        archEnergy = new OverlayFloor("archaic-energy"){{
            variants = 3;
            emitLight = true;
            lightRadius = 24f;
            lightColor = UnityPal.monolithLight.cpy().a(0.24f);
        }};

        loreMonolith = new LoreMessageBlock("lore-monolith", Faction.monolith);

        debrisExtractor = new SoulFloorExtractor("debris-extractor"){
            final int effectTimer = timers++;

            {
                requirements(Category.crafting, with(UnityItems.monolite, 140, Items.surgeAlloy, 80, Items.thorium, 60));
                setup(
                    infusedSharpslate, 0.04f,
                    archSharpslate, 0.08f,
                    archEnergy, 1f
                );

                size = 2;
                outputItem = new ItemStack(UnityItems.archDebris, 1);
                craftTime = 84f;

                consumePower(2.4f);
                consumeLiquid(Liquids.cryofluid, 0.08f);

                draw((SoulFloorExtractorBuild e) -> {
                    Draw.color(UnityPal.monolith, UnityPal.monolithLight, Mathf.absin(Time.time, 6f, 1f) * e.warmup);
                    Draw.alpha(e.warmup);
                    Draw.rect(Regions.debrisExtractorHeat1Region, e.x, e.y);

                    Draw.color(UnityPal.monolith, UnityPal.monolithLight, Mathf.absin(Time.time + 4f, 6f, 1f) * e.warmup);
                    Draw.alpha(e.warmup);
                    Draw.rect(Regions.debrisExtractorHeat2Region, e.x, e.y);

                    Draw.color();
                    Draw.alpha(1f);
                });

                update((SoulFloorExtractorBuild e) -> {
                    StemData data = e.data();
                    if(e.canConsume()){
                        data.floatValue = Mathf.lerpDelta(data.floatValue, e.efficiency(), 0.02f);
                    }else{
                        data.floatValue = Mathf.lerpDelta(data.floatValue, 0f, 0.02f);
                    }

                    if(!Mathf.zero(data.floatValue)){
                        float f = e.soulf();
                        if(e.timer.get(effectTimer, 45f - f * 15f)){
                            UnityFx.monolithRingEffect.at(e.x, e.y, e.rotation, data.floatValue / 2f);
                        }
                    }
                });
            }
        };

        soulInfuser = new SoulInfuser("soul-infuser"){
            final float[] scales = {1f, 0.9f, 0.7f};
            final Color[] colors = {UnityPal.monolithDark, UnityPal.monolith, UnityPal.monolithLight};

            final int effectTimer = timers++;

            {
                requirements(Category.crafting, with(UnityItems.monolite, 200, Items.titanium, 250, Items.silicon, 420));
                setup(
                    infusedSharpslate, 0.6f,
                    archSharpslate, 1f,
                    archEnergy, 1.4f
                );

                size = 3;
                craftTime = 60f;
                updateEffect = Fx.smeltsmoke;
                craftEffect = Fx.producesmoke;
                requireSoul = false;

                consumePower(3.2f);
                consumeLiquid(Liquids.cryofluid, 0.2f);

                //drawer = new DrawGlow();
                draw((SoulInfuserBuild e) -> {
                    float z = Draw.z();
                    Draw.z(Layer.effect);

                    for(int i = 0; i < colors.length; i++){
                        Color color = colors[i];
                        float scl = e.warmup * 4f * scales[i];

                        Draw.color(color);
                        UnityDrawf.shiningCircle(e.id, Time.time + i,
                            e.x, e.y, scl,
                            3, 20f,
                            scl * 2f, scl * 2f,
                            60f
                        );
                    }

                    Draw.z(z);
                });

                update((SoulInfuserBuild e) -> {
                    StemData data = e.data();
                    if(e.canConsume()){
                        data.floatValue = Mathf.lerpDelta(data.floatValue, e.efficiency(), 0.02f);
                    }else{
                        data.floatValue = Mathf.lerpDelta(data.floatValue, 0f, 0.02f);
                    }

                    if(!Mathf.zero(data.floatValue)){
                        float f = e.soulf();
                        if(e.timer.get(effectTimer, 45f - f * 15f)){
                            UnityFx.monolithRingEffect.at(e.x, e.y, e.rotation, data.floatValue * 3f / 4f);
                        }

                        if(Mathf.chanceDelta(data.floatValue * 0.5f)){
                            Lightning.create(
                                e.team,
                                Pal.lancerLaser,
                                1f,
                                e.x, e.y,
                                Mathf.randomSeed((int)Time.time + e.id, 360f), (int)(data.floatValue * 3f) + Mathf.random(3)
                            );
                        }
                    }
                });
            }
        };

        monolithAlloyForge = new SoulGenericCrafter("monolith-alloy-forge"){
            final int effectTimer = timers++;

            {
                requirements(Category.crafting, with(Items.lead, 380, UnityItems.monolite, 240, Items.silicon, 400, Items.titanium, 240, Items.thorium, 90, Items.surgeAlloy, 160));

                outputItem = new ItemStack(UnityItems.monolithAlloy, 3);
                size = 4;
                ambientSound = Sounds.machine;
                ambientSoundVolume = 0.6f;
                drawer = new DrawMulti(new DrawDefault(), new DrawFlame(UnityPal.monolithLight){{
                    flameRadius = 5f;
                    flameRadiusIn = 2.6f;
                }});

                consumePower(3.6f);
                consumeItems(with(Items.silicon, 3, UnityItems.archDebris, 1, UnityItems.monolite, 2));
                consumeLiquid(Liquids.cryofluid, 0.1f);

                update((SoulGenericCrafterBuild e) -> {
                    StemData data = e.data();
                    if(e.canConsume()){
                        data.floatValue = Mathf.lerpDelta(data.floatValue, e.efficiency(), 0.02f);
                    }else{
                        data.floatValue = Mathf.lerpDelta(data.floatValue, 0f, 0.02f);
                    }

                    if(!Mathf.zero(data.floatValue)){
                        float f = e.soulf();
                        if(e.timer.get(effectTimer, 45f - f * 15f)){
                            UnityFx.monolithRingEffect.at(e.x, e.y, e.rotation, data.floatValue);
                        }

                        if(Mathf.chanceDelta(data.floatValue * 0.5f)){
                            Lightning.create(
                                e.team,
                                Pal.lancerLaser,
                                1f,
                                e.x, e.y,
                                Mathf.randomSeed((int)Time.time + e.id, 360f), (int)(data.floatValue * 4f) + Mathf.random(3)
                            );
                        }
                    }
                });
            }
        };

        electrophobicWall = new PowerWall("electrophobic-wall"){{
            requirements(Category.defense, with(UnityItems.monolite, 4, Items.silicon, 2));

            size = 1;
            health = 400;

            energyMultiplier.put(LightningBulletType.class, 15f);
            energyMultiplier.put(LaserBulletType.class, 9f);
            energyMultiplier.put(ContinuousLaserBulletType.class, 12f);
            energyMultiplier.put(LaserBoltBulletType.class, 9f);
        }};

        electrophobicWallLarge = new PowerWall("electrophobic-wall-large"){{
            requirements(Category.defense, with(UnityItems.monolite, 16, Items.silicon, 8));

            size = 2;
            health = 1600;
            powerProduction = 4f;
            damageThreshold = 300f;

            energyMultiplier.put(LightningBulletType.class, 15f);
            energyMultiplier.put(LaserBulletType.class, 9f);
            energyMultiplier.put(ContinuousLaserBulletType.class, 12f);
            energyMultiplier.put(LaserBoltBulletType.class, 9f);
        }};

        ricochet = new SoulTurretPowerTurret("ricochet"){{
            requirements(Category.turret, with(UnityItems.monolite, 40));

            size = 1;
            health = 200;

            reload = 60f;
            recoilTime = 0.03f;
            range = 180f;
            shootCone = 15f;
            ammoUseEffect = Fx.none;
            inaccuracy = 2f;
            rotateSpeed = 12f;
            shootSound = UnitySounds.energyBolt;
            shootType = UnityBullets.ricochetSmall.copy();

            requireSoul = false;
            efficiencyFrom = 0.8f;
            efficiencyTo = 1.5f;

            float base = shootType.damage;
            progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> shootType.damage = base * f);
            consumePower(1f);
        }};

        diviner = new SoulTurretPowerTurret("diviner"){{
            requirements(Category.turret, with(Items.lead, 15, UnityItems.monolite, 30));

            size = 1;
            health = 240;

            reload = 30f;
            range = 75f;
            targetGround = true;
            targetAir = false;
            shootSound = UnitySounds.energyBolt;

            shootType = new LaserBulletType(200f){{
                length = 85f;
            }};

            requireSoul = false;
            efficiencyFrom = 0.8f;
            efficiencyTo = 1.5f;

            float base = shootType.damage;
            progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> shootType.damage = base * f);
            consumePower(1.5f);
        }};

        lifeStealer = new SoulLifeStealerTurret("life-stealer"){{
            requirements(Category.turret, with(Items.silicon, 50, UnityItems.monolite, 25));

            size = 1;
            health = 320;
            damage = 120f;

            requireSoul = false;
            efficiencyFrom = 0.8f;
            efficiencyTo = 1.5f;

            consumePower(1f);
            laserAlpha((SoulLifeStealerTurretBuild b) -> b.power.status * (0.7f + b.soulf() * 0.3f));
        }};

        recluse = new SoulTurretItemTurret("recluse"){{
            requirements(Category.turret, with(Items.lead, 15, UnityItems.monolite, 20));
            ammo(
                Items.lead, UnityBullets.stopLead.copy(),
                UnityItems.monolite, UnityBullets.stopMonolite.copy(),
                Items.silicon, UnityBullets.stopSilicon.copy()
            );

            size = 1;
            health = 200;
            //spread = 4f;
            reload = 20f;
            recoilTime = 0.03f;
            range = 110f;
            shootCone = 3f;
            ammoUseEffect = Fx.none;
            rotateSpeed = 12f;

            requireSoul = false;
            efficiencyFrom = 0.8f;
            efficiencyTo = 1.5f;

            for(var b : ammoTypes.values()){
                float base = b.damage;
                progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> b.damage = base * f);
            }

            shoot = new ShootSpread(1, 4f);
        }};

        absorberAura = new SoulAbsorberTurret("absorber-aura"){{
            requirements(Category.turret, with(Items.silicon, 75, UnityItems.monolite, 125));

            size = 2;
            health = 720;
            range = 150f;
            resistance = 0.8f;

            targetBullets = true;

            requireSoul = false;
            efficiencyFrom = 0.8f;
            efficiencyTo = 1.6f;

            consumePower(1f);
            laserAlpha((SoulAbsorberTurretBuild b) -> b.power.status * (0.7f + b.soulf() * 0.3f));
        }};

        mage = new SoulTurretPowerTurret("mage"){{
            requirements(Category.turret, with(Items.lead, 75, Items.silicon, 50, UnityItems.monolite, 25));

            size = 2;
            health = 600;

            range = 120f;
            reload = 48f;
            shootCone = 15f;
            //shots = 3;
            //burstSpacing = 2f;
            shootSound = Sounds.spark;
            recoil = 2.5f;
            rotateSpeed = 10f;
            shootType = new LightningBulletType(){{
                lightningLength = 20;
                damage = 128f;
            }};

            requireSoul = false;
            maxSouls = 5;
            efficiencyFrom = 0.8f;
            efficiencyTo = 1.6f;

            float base = shootType.damage;
            progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> shootType.damage = base * f);
            consumePower(2.5f);

            shoot = new ShootPattern(){{
                shots = 3;
                shotDelay = 2f;
            }};
        }};

        blackout = new SoulTurretPowerTurret("blackout"){{
            requirements(Category.turret, with(Items.graphite, 85, Items.titanium, 25, UnityItems.monolite, 125));

            size = 2;
            health = 720;

            reload = 140f;
            range = 200f;
            rotateSpeed = 10f;
            recoil = 3f;
            shootSound = Sounds.shootBig;
            targetGround = true;
            targetAir = false;
            shootType = new BasicBulletType(6f, 180f, "shell"){
                {
                    lifetime = 35f;
                    width = height = 20f;
                    frontColor = UnityPal.monolith;
                    backColor = UnityPal.monolithDark;
                    hitEffect = despawnEffect = Fx.blastExplosion;
                    splashDamage = 90f;
                    splashDamageRadius = 3.2f * tilesize;
                }

                @Override
                public void hitEntity(Bullet b, Hitboxc other, float initialHealth){
                    super.hitEntity(b, other, initialHealth);

                    float r = splashDamageRadius;
                    Units.nearbyEnemies(b.team, b.x - r, b.y - r, r * 2f, r * 2f, unit -> {
                        if(unit.within(b, r)){
                            unit.apply(StatusEffects.unmoving, 60f);
                            unit.apply(StatusEffects.disarmed, 60f);
                        }
                    });
                }
            };

            requireSoul = false;
            maxSouls = 5;
            efficiencyFrom = 0.8f;
            efficiencyTo = 1.6f;

            float base = shootType.damage;
            progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> shootType.damage = base * f);
            consumePower(3f);
        }};

        shellshock = new SoulTurretPowerTurret("shellshock"){{
            requirements(Category.turret, with(Items.lead, 90, Items.graphite, 100, UnityItems.monolite, 80));

            size = 2;
            health = 720;

            reload = 75f;
            range = 260f;
            shootCone = 3f;
            ammoUseEffect = Fx.none;
            rotateSpeed = 10f;
            shootType = UnityBullets.ricochetMedium.copy();
            shootSound = UnitySounds.energyBolt;

            requireSoul = false;
            maxSouls = 5;
            efficiencyFrom = 0.8f;
            efficiencyTo = 1.6f;

            float base = shootType.damage;
            progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> shootType.damage = base * f);
            consumePower(2f);
        }};

        heatRay = new SoulHeatRayTurret("heat-ray"){{
            requirements(Category.turret, with(Items.copper, 75, Items.lead, 50, Items.graphite, 25, Items.titanium, 45, UnityItems.monolite, 50));

            size = 2;
            range = 120f;
            targetGround = true;
            targetAir = false;
            damage = 240f;
            shootSound = UnitySounds.heatRay;

            requireSoul = false;
            maxSouls = 5;
            efficiencyFrom = 0.8f;
            efficiencyTo = 1.6f;

            consumePower(2f);
            laserAlpha((SoulHeatRayTurretBuild b) -> b.power.status * (0.7f + b.soulf() * 0.3f));
        }};

        oracle = new SoulPowerTurret("oracle"){{
            requirements(Category.turret, with(Items.silicon, 175, Items.titanium, 150, UnityItems.monolithAlloy, 75));

            size = 3;
            health = 1440;

            range = 180f;
            reload = 72f;
            //chargeTime = 30f;
            //chargeMaxDelay = 4f;
            //chargeEffects = 12;
            shootCone = 5f;
            //shots = 8;
            //burstSpacing = 2f;
            //chargeEffect = UnityFx.oracleCharge;
            //chargeBeginEffect = UnityFx.oracleChargeBegin;
            shootSound = Sounds.spark;
            shake = 3f;
            recoil = 2.5f;
            rotateSpeed = 8f;
            shootType = new LightningBulletType(){{
                damage = 192f;
                shootEffect = Fx.lightningShoot;
                chargeEffect = new MultiEffect(UnityFx.oracleCharge, UnityFx.oracleChargeBegin);
            }};

            //subShots = 3;
            //subBurstSpacing = 1f;
            //subShootEffect = Fx.hitLancer;
            //subShootSound = Sounds.laser;
            /*subShootType = new LaserBulletType(288f){{
                length = 180f;
                sideAngle = 45f;
                inaccuracy = 8f;
            }};*/

            requireSoul = false;
            maxSouls = 7;
            efficiencyFrom = 0.7f;
            efficiencyTo = 1.67f;

            float base = shootType.damage;
            progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> shootType.damage = base * f);
            consumePower(3f);

            shoot = new ShootPattern(){{
                firstShotDelay = 30f;
                shotDelay = 2f;
                shots = 8;
            }};
        }};

        purge = new SoulTurretPowerTurret("purge"){{
            requirements(Category.turret, with(Items.plastanium, 75, Items.lead, 350, UnityItems.monolite, 200, UnityItems.monolithAlloy, 75));

            size = 3;
            health = 1680;

            reload = 90f;
            range = 360f;
            shootCone = 3f;
            ammoUseEffect = Fx.none;
            rotateSpeed = 8f;
            shootType = UnityBullets.ricochetBig.copy();
            shootSound = UnitySounds.energyBolt;

            requireSoul = false;
            maxSouls = 7;
            efficiencyFrom = 0.7f;
            efficiencyTo = 1.67f;

            float base = shootType.damage;
            progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> shootType.damage = base * f);
            consumePower(3f);
        }};

        incandescence = new SoulHeatRayTurret("incandescence"){{
            requirements(Category.turret, with(UnityItems.monolite, 250, Items.phaseFabric, 45, UnityItems.monolithAlloy, 100));

            size = 3;
            health = 1680;
            range = 180f;
            targetGround = true;
            targetAir = true;
            damage = 480f;
            shootSound = UnitySounds.heatRay;
            laserWidth = 0.54f;
            shootY = 6f;

            requireSoul = false;
            maxSouls = 7;
            efficiencyFrom = 0.7f;
            efficiencyTo = 1.67f;

            consumePower(4f);
            laserAlpha((SoulHeatRayTurretBuild b) -> b.power.status * (0.7f + b.soulf() * 0.3f));
        }};

        prism = new PrismTurret("prism"){{
            requirements(Category.turret, with(Items.copper, 1));

            size = 4;
            health = 2800;
            range = 320f;
            reload = 60f;
            rotateSpeed = 20f;
            recoil = 6f;
            prismOffset = 6f;
            shootCone = 30f;

            targetGround = true;
            targetAir = true;

            shootSound = Sounds.shotgun;
            shootEffect = Fx.hitLaserBlast;
            model = UnityModels.prism;

            requireSoul = false;
            maxSouls = 7;
            efficiencyFrom = 0.7f;
            efficiencyTo = 1.67f;

            shootType = new BulletType(0.0001f, 320f);

            float base = shootType.damage;
            progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> shootType.damage = base * f);
            consumePower(8f);
        }};

        supernova = new SupernovaTurret("supernova"){{
            requirements(Category.turret, with(Items.surgeAlloy, 500, Items.silicon, 650, UnityItems.archDebris, 350, UnityItems.monolithAlloy, 325));

            size = 7;
            health = 8100;

            //shootY = size * tilesize / 2f - 8f;
            rotateSpeed = 1f;
            recoil = 4f;
            cooldownTime = 0.006f;

            shootCone = 15f;
            range = 250f;

            shootSound = UnitySounds.supernovaShoot;
            loopSound = UnitySounds.supernovaActive;
            loopSoundVolume = 1f;

            baseExplosiveness = 25f;
            shootDuration = 480f;
            shootType = UnityBullets.supernovaLaser.copy();
            shootType.chargeEffect = UnityFx.supernovaChargeBegin;

            //chargeBeginEffect = UnityFx.supernovaChargeBegin;

            requireSoul = false;
            maxSouls = 12;
            efficiencyFrom = 0.7f;
            efficiencyTo = 1.8f;

            float base = shootType.damage;
            progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> shootType.damage = base * f);
            consumePower(24f);
        }};

        //endregion
        //region youngcha

        concreteBlank = new Floor("concrete-blank");

        concreteFill = new Floor("concrete-fill"){{
            variants = 0;
        }};

        concreteNumber = new Floor("concrete-number"){{
            variants = 10;
        }};

        concreteStripe = new Floor("concrete-stripe");

        concrete = new Floor("concrete");

        stoneFullTiles = new Floor("stone-full-tiles");

        stoneFull = new Floor("stone-full");

        stoneHalf = new Floor("stone-half");

        stoneTiles = new Floor("stone-tiles");

        smallTurret = new ModularTurret("small-turret-base"){{
            requirements(Category.turret, with(Items.graphite, 20, UnityItems.nickel, 30, Items.copper, 30));
            size = 2;
            health = 1200;
            setGridW(3);
            setGridH(3);
            spriteGridSize = 18;
            spriteGridPadding = 3;
            yScale = 0.8f;
            addGraph(new GraphTorque(0.03f, 50f).setAccept(1, 1, 0, 0, 0, 0, 0, 0, 0, 0));
            addGraph(new GraphHeat(50f, 0.1f, 0.01f).setAccept(1, 1, 1, 1, 1, 1, 1, 1));
        }};

        medTurret = new ModularTurret("med-turret-base"){{
            requirements(Category.turret, with(Items.graphite, 25, UnityItems.nickel, 30, Items.titanium, 50, Items.silicon, 40));
            size = 3;
            health = 1200;
            acceptsItems = true;
            setGridW(5);
            setGridH(5);
            spriteGridSize = 16;
            spriteGridPadding = 4;
            yShift = 0.8f;
            yScale = 0.8f;
            partCostAccum = 0.12f;
            addGraph(new GraphTorque(0.05f, 150f).setAccept(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
            addGraph(new GraphHeat(120f, 0.05f, 0.02f).setAccept(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
        }};

        chopper = new Chopper("chopper"){{
            requirements(Category.turret, with(UnityItems.nickel, 50, Items.titanium, 50, Items.lead, 30));
            health = 650;
            setGridW(7);
            setGridH(1);
            addPart(
                bundle.get("part.unity.pivot.name"), bundle.get("part.unity.pivot.info"), PartType.blade, 4, 0, 1, 1, true, true,
                new Point2(0, 0), new ItemStack[0], new byte[]{1, 0, 0, 0}, new byte[]{0, 0, 0, 0},
                new PartStat(PartStatType.mass, 1), new PartStat(PartStatType.collides, false), new PartStat(PartStatType.hp, 10)
            );
            addPart(
                bundle.get("part.unity.blade.name"), bundle.get("part.unity.blade.info"), PartType.blade, 0, 0, 1, 1,
                with(UnityItems.nickel, 3, Items.titanium, 5), new byte[]{1, 0, 0, 0}, new byte[]{0, 0, 1, 0},
                new PartStat(PartStatType.mass, 2), new PartStat(PartStatType.collides, true), new PartStat(PartStatType.hp, 80), new PartStat(PartStatType.damage, 5)
            );
            addPart(
                bundle.get("part.unity.serrated-blade.name"), bundle.get("part.unity.serrated-blade.info"), PartType.blade, 2, 0, 2, 1,
                with(UnityItems.nickel, 8, Items.lead, 5), new byte[]{1, 0, 0, 0, 0, 0}, new byte[]{0, 0, 0, 1, 0, 0},
                new PartStat(PartStatType.mass, 6), new PartStat(PartStatType.collides, true), new PartStat(PartStatType.hp, 120), new PartStat(PartStatType.damage, 12)
            );
            addPart(
                bundle.get("part.unity.rod.name"), bundle.get("part.unity.rod.info"), PartType.blade, 1, 0, 1, 1,
                with(Items.titanium, 3), new byte[]{1, 0, 0, 0}, new byte[]{0, 0, 1, 0},
                new PartStat(PartStatType.mass, 1), new PartStat(PartStatType.collides, false), new PartStat(PartStatType.hp, 40)
            );
            addGraph(new GraphTorque(0.03f, 5f).setAccept(1, 0, 0, 0));
        }};

        augerDrill = new AugerDrill("auger-drill"){{
            requirements(Category.production, with(Items.lead, 100, Items.copper, 75));
            size = 3;
            health = 1000;
            tier = 3;
            drillTime = 400f;
            addGraph(new GraphTorqueConsume(45f, 8f, 0.03f, 0.15f).setAccept(0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0));
        }};

        mechanicalExtractor = new MechanicalExtractor("mechanical-extractor"){{
            requirements(Category.production, with(Items.lead, 100, Items.copper, 75));
            hasPower = false;
            size = 3;
            health = 1000;
            pumpAmount = 0.4f;

            addGraph(new GraphTorqueConsume(45f, 8f, 0.06f, 0.3f).setAccept(0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0));
        }};

        sporeFarm = new SporeFarm("spore-farm"){{
            requirements(Category.production, with(Items.lead, 5));
            health = 50;
            rebuildable = false;
            hasItems = true;
            itemCapacity = 2;
            buildCostMultiplier = 0.01f;
            breakSound = Sounds.splash;
        }};

        mechanicalConveyor = new ShadowedConveyor("mechanical-conveyor"){{
            requirements(Category.distribution, with(Items.copper, 3, UnityItems.nickel, 2));
            health = 250;
            speed = 0.1f;
        }};

        heatPipe = new HeatPipe("heat-pipe"){{
            requirements(Category.distribution, with(Items.copper, 15, UnityItems.cupronickel, 10, UnityItems.nickel, 5));
            health = 140;
            addGraph(new GraphHeat(5f, 0.7f, 0.008f).setAccept(1, 1, 1, 1));
        }};

        driveShaft = new DriveShaft("drive-shaft"){{
            requirements(Category.distribution, with(Items.copper, 10, Items.lead, 10));
            health = 150;
            addGraph(new GraphTorque(0.01f, 3f).setAccept(1, 0, 1, 0));
        }};

        inlineGearbox = new InlineGearbox("inline-gearbox"){{
            requirements(Category.distribution, with(Items.titanium, 20, Items.lead, 30, Items.copper, 30));
            size = 2;
            health = 700;
            addGraph(new GraphTorque(0.02f, 20f).setAccept(1, 1, 0, 0, 1, 1, 0, 0));
        }};

        shaftRouter = new GraphBlock("shaft-router"){{
            requirements(Category.distribution, with(Items.copper, 20, Items.lead, 20));
            health = 100;
            preserveDraw = true;
            addGraph(new GraphTorque(0.05f, 5f).setAccept(1, 1, 1, 1));
        }};

        simpleTransmission = new SimpleTransmission("simple-transmission"){{
            requirements(Category.distribution, with(Items.titanium, 50, Items.lead, 50, Items.copper, 50));
            size = 2;
            health = 500;
            addGraph(new GraphTorqueTrans(0.05f, 25f).setRatio(1f, 2.5f).setAccept(2, 1, 0, 0, 1, 2, 0, 0));
        }};

        crucible = new Crucible("crucible"){{
            requirements(Category.crafting, with(UnityItems.nickel, 10, Items.titanium, 15));
            health = 400;
            addGraph(new GraphCrucible().setAccept(1, 1, 1, 1));
            addGraph(new GraphHeat(75f, 0.2f, 0.006f).setAccept(1, 1, 1, 1));
        }};

        holdingCrucible = new HoldingCrucible("holding-crucible"){{
            requirements(Category.crafting, with(UnityItems.nickel, 50, UnityItems.cupronickel, 150, Items.metaglass, 150, Items.titanium, 30));
            size = 4;
            health = 2400;
            addGraph(new GraphCrucible(50f, false).setAccept(0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0));
            addGraph(new GraphHeat(275f, 0.05f, 0.01f).setAccept(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
        }};

        cruciblePump = new CruciblePump("crucible-pump"){{
            requirements(Category.crafting, with(UnityItems.cupronickel, 50, UnityItems.nickel, 50, Items.metaglass, 15));
            size = 2;
            health = 500;
            consumePower(1f);
            addGraph(new GraphCrucible(10f, false).setAccept(1, 1, 0, 0, 2, 2, 0, 0).multi());
            addGraph(new GraphHeat(50f, 0.1f, 0.003f).setAccept(1, 1, 1, 1, 1, 1, 1, 1));
        }};

        castingMold = new CastingMold("casting-mold"){{
            requirements(Category.crafting, with(Items.titanium, 70, UnityItems.nickel, 30));
            size = 2;
            health = 700;
            addGraph(new GraphCrucible(2f, false).setAccept(0, 0, 0, 0, 1, 1, 0, 0));
            addGraph(new GraphHeat(55f, 0.2f, 0.0f).setAccept(1, 1, 1, 1, 1, 1, 1, 1));
        }};

        sporePyrolyser = new SporePyrolyser("spore-pyrolyser"){{
            requirements(Category.crafting, with(UnityItems.nickel, 25, Items.titanium, 50, Items.copper, 50, Items.lead, 30));
            size = 3;
            health = 1100;
            craftTime = 50f;
            outputItem = new ItemStack(Items.coal, 3);
            ambientSound = Sounds.machine;
            ambientSoundVolume = 0.6f;
            consumeItem(Items.sporePod, 1);
            addGraph(new GraphHeat(60f, 0.4f, 0.008f).setAccept(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
        }};

        smallRadiator = new GraphBlock("small-radiator"){{
            requirements(Category.power, with(Items.copper, 30, UnityItems.cupronickel, 20, UnityItems.nickel, 15));
            health = 200;
            solid = true;
            addGraph(new GraphHeat(10f, 0.7f, 0.05f).setAccept(1, 1, 1, 1));
        }};

        thermalHeater = new ThermalHeater("thermal-heater"){{
            requirements(Category.power, with(Items.copper, 150, UnityItems.nickel, 100, Items.titanium, 150));
            size = 2;
            health = 500;
            maxTemp = 1100f;
            mulCoeff = 0.11f;
            addGraph(new GraphHeat(40f, 0.6f, 0.004f).setAccept(1, 1, 0, 0, 0, 0, 0, 0));
        }};

        combustionHeater = new CombustionHeater("combustion-heater"){{
            requirements(Category.power, with(Items.copper, 100, UnityItems.nickel, 70, Items.graphite, 40, Items.titanium, 80));
            size = 2;
            health = 550;
            itemCapacity = 5;
            maxTemp = 1200f;
            mulCoeff = 0.45f;
            addGraph(new GraphHeat(40f, 0.6f, 0.004f).setAccept(1, 1, 0, 0, 0, 0, 0, 0));
        }};

        solarCollector = new SolarCollector("solar-collector"){{
            requirements(Category.power, with(UnityItems.nickel, 80, Items.titanium, 50, Items.lead, 30));
            size = 3;
            health = 1500;
            maxTemp = 800f;
            mulCoeff = 0.03f;
            addGraph(new GraphHeat(60f, 1f, 0.02f).setAccept(0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0));
        }};

        solarReflector = new SolarReflector("solar-reflector"){{
            requirements(Category.power, with(UnityItems.nickel, 25, Items.copper, 50));
            size = 2;
            health = 800;
        }};

        nickelStator = new Magnet("nickel-stator"){{
            requirements(Category.power, with(UnityItems.nickel, 30, Items.titanium, 20));
            health = 450;
            addGraph(new GraphFlux(2f).setAccept(1, 0, 0, 0));
        }};

        nickelStatorLarge = new Magnet("nickel-stator-large"){{
            requirements(Category.power, with(UnityItems.nickel, 250, Items.titanium, 150));
            size = 2;
            health = 1800;
            addGraph(new GraphFlux(10f).setAccept(1, 1, 0, 0, 0, 0, 0, 0));
        }};

        nickelElectromagnet = new Magnet("nickel-electromagnet"){{
            requirements(Category.power, with(UnityItems.nickel, 250, Items.titanium, 200, Items.copper, 100, UnityItems.cupronickel, 50));
            size = 2;
            health = 1000;
            consumePower(1.6f);
            addGraph(new GraphFlux(25f).setAccept(1, 1, 0, 0, 0, 0, 0, 0));
        }};

        electricRotorSmall = new RotorBlock("electric-rotor-small"){{
            requirements(Category.power, with(UnityItems.nickel, 30, Items.copper, 50, Items.titanium, 10));
            health = 120;
            powerProduction = 2f;
            fluxEfficiency = 10f;
            rotPowerEfficiency = 0.8f;
            torqueEfficiency = 0.7f;
            baseTorque = 1f;
            baseTopSpeed = 3f;
            consumePower(1f);
            addGraph(new GraphFlux(false).setAccept(0, 1, 0, 1));
            addGraph(new GraphTorque(0.08f, 20f).setAccept(1, 0, 1, 0));
        }};

        electricRotor = new RotorBlock("electric-rotor"){{
            requirements(Category.power, with(UnityItems.nickel, 200, Items.copper, 200, Items.titanium, 150, Items.graphite, 100));
            size = 3;
            health = 1000;
            powerProduction = 32f;
            big = true;
            fluxEfficiency = 10f;
            rotPowerEfficiency = 0.8f;
            torqueEfficiency = 0.8f;
            baseTorque = 5f;
            baseTopSpeed = 15f;
            consumePower(16f);
            addGraph(new GraphFlux(false).setAccept(0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1));
            addGraph(new GraphTorque(0.05f, 150f).setAccept(0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0));
        }};

        handCrank = new HandCrank("hand-crank"){{
            requirements(Category.power, with(UnityItems.nickel, 5, Items.lead, 20));
            health = 120;
            addGraph(new GraphTorque(0.01f, 3f).setAccept(1, 0, 0, 0));
        }};

        windTurbine = new WindTurbine("wind-turbine"){{
            requirements(Category.power, with(Items.titanium, 20, Items.lead, 80, Items.copper, 70));
            size = 3;
            health = 1200;
            addGraph(new GraphTorqueGenerate(0.03f, 20f, 5f, 5f).setAccept(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }};

        waterTurbine = new WaterTurbine("water-turbine"){{
            requirements(Category.power, with(Items.metaglass, 50, UnityItems.nickel, 20, Items.lead, 150, Items.copper, 100));
            size = 3;
            health = 1100;
            liquidCapacity = 250f;
            liquidPressure = 0.3f;
            disableOgUpdate();
            addGraph(new GraphTorqueGenerate(0.3f, 20f, 7f, 15f).setAccept(0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0));
        }};

        electricMotor = new ElectricMotor("electric-motor"){{
            requirements(Category.power, with(Items.silicon, 100, Items.lead, 80, Items.copper, 150, Items.titanium, 150));
            size = 3;
            health = 1300;
            consumePower(4.5f);
            addGraph(new GraphTorqueGenerate(0.1f, 25f, 10f, 16f).setAccept(0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0));
        }};

        cupronickelWall = new HeatWall("cupronickel-wall"){{
            requirements(Category.defense, with(UnityItems.cupronickel, 8, UnityItems.nickel, 5));
            health = 500;
            addGraph(new GraphHeat(50f, 0.5f, 0.03f).setAccept(1, 1, 1, 1));
        }};

        cupronickelWallLarge = new HeatWall("cupronickel-wall-large"){{
            requirements(Category.defense, with(UnityItems.cupronickel, 36, UnityItems.nickel, 20));
            size = 2;
            health = 2000;
            minStatusRadius = 8f;
            statusRadiusMul = 40f;
            minStatusDuration = 5f;
            statusDurationMul = 120f;
            statusTime = 120f;
            maxDamage = 40f;
            addGraph(new GraphHeat(200f, 0.5f, 0.09f).setAccept(1, 1, 1, 1, 1, 1, 1, 1));
        }};

        infiHeater = new HeatSource("infi-heater"){{
            requirements(Category.power, BuildVisibility.sandboxOnly, with());
            health = 200;
            addGraph(new GraphHeat(1000f, 1f, 0f).setAccept(1, 1, 1, 1));
        }};

        infiCooler = new HeatSource("infi-cooler"){{
            requirements(Category.power, BuildVisibility.sandboxOnly, with());
            health = 200;
            isVoid = true;
            addGraph(new GraphHeat(1000f, 1f, 0f).setAccept(1, 1, 1, 1));
        }};

        infiTorque = new TorqueGenerator("infi-torque"){{
            requirements(Category.power, BuildVisibility.sandboxOnly, with());
            health = 200;
            preserveDraw = true;
            rotate = false;
            addGraph(new GraphTorqueGenerate(0.001f, 1f, 999999f, 9999f).setAccept(1, 1, 1, 1));
        }};

        neodymiumStator = new Magnet("neodymium-stator"){{
            requirements(Category.power, BuildVisibility.sandboxOnly, with());
            health = 400;
            addGraph(new GraphFlux(200f).setAccept(1, 0, 0, 0));
        }};

        smallThruster = new UnityThruster("small-thruster"){{
            requirements(Category.effect, with(Items.silicon, 20, Items.graphite, 30, UnityItems.nickel, 25));
            health = 400;
            acceleration = 0.2f;
            maxSpeed = 5;
            maxBlocks = 5;
            itemDuration = 300;
            consumeItem(Items.blastCompound);
        }};

        //endregion
        //region advance

        advanceConstructorModule = new ModularConstructorPart("advance-constructor-module"){{
            requirements(Category.units, with(UnityItems.xenium, 300, Items.silicon, 200, Items.graphite, 300, Items.thorium, 400, Items.phaseFabric, 50, Items.surgeAlloy, 100, UnityItems.advanceAlloy, 300));
            size = 6;
            liquidCapacity = 20f;

            consumeLiquid(Liquids.cryofluid, 0.7f);
            consumePower(3f);
            hasLiquids = true;
            hasPower = true;
        }};

        advanceConstructor = new ModularConstructor("advance-constructor"){{
            requirements(Category.units, with(UnityItems.xenium, 3000, Items.silicon, 5000, Items.graphite, 2000, Items.thorium, 3000, Items.phaseFabric, 800, Items.surgeAlloy, 700, UnityItems.advanceAlloy, 1500));
            size = 13;
            efficiencyPerTier = 10f * 60f;

            plans.addAll(
                new ModularConstructorPlan(UnitTypes.antumbra, 30f * 60f, 0,
                with(Items.silicon, 690, Items.graphite, 40, Items.titanium, 550, Items.metaglass, 40, Items.plastanium, 420)),

                new ModularConstructorPlan(UnitTypes.scepter, 30f * 60f, 0,
                with(Items.silicon, 690, Items.lead, 60, Items.graphite, 30, Items.titanium, 550, Items.metaglass, 40, Items.plastanium, 420)),

                new ModularConstructorPlan(UnitTypes.eclipse, 40f * 60f, 1,
                with(Items.silicon, 1350, Items.graphite, 120, Items.titanium, 550, Items.metaglass, 100, Items.plastanium, 830, Items.surgeAlloy, 330, Items.phaseFabric, 250)),

                new ModularConstructorPlan(UnitTypes.reign, 40f * 60f, 1,
                with(Items.silicon, 1350, Items.lead, 160, Items.graphite, 90, Items.titanium, 550, Items.metaglass, 100, Items.plastanium, 830, Items.surgeAlloy, 330, Items.phaseFabric, 250)),

                new ModularConstructorPlan(UnityUnitTypes.mantle, 50f * 60f, 2,
                with(Items.silicon, 2050, Items.graphite, 180, Items.titanium, 830, Items.metaglass, 150, Items.plastanium, 1250, Items.surgeAlloy, 500, Items.phaseFabric, 375))
            );

            consumePower(13f);
        }};

        celsius = new PowerTurret("celsius"){{
            requirements(Category.turret, with(Items.silicon, 20, UnityItems.xenium, 15, Items.titanium, 30, UnityItems.advanceAlloy, 25));
            health = 780;
            size = 1;
            reload = 3f;
            range = 47f;
            shootCone = 50f;
            heatColor = Color.valueOf("ccffff");
            ammoUseEffect = Fx.none;
            inaccuracy = 9.2f;
            rotateSpeed = 7.5f;
            //shots = 2;
            recoil = 1f;
            hasPower = true;
            targetAir = true;
            shootSound = Sounds.flame;
            cooldownTime = 0.01f;
            shootType = UnityBullets.celsiusSmoke;
            shoot = new ShootPattern(){{
                shots = 2;
            }};
            consumePower(13.9f);
        }};

        kelvin = new PowerTurret("kelvin"){{
            requirements(Category.turret, with(Items.silicon, 80, UnityItems.xenium, 35, Items.titanium, 90, UnityItems.advanceAlloy, 50));
            health = 2680;
            size = 2;
            reload = 3f;
            range = 100f;
            shootCone = 50f;
            heatColor = Color.valueOf("ccffff");
            ammoUseEffect = Fx.none;
            inaccuracy = 9.2f;
            rotateSpeed = 6.5f;
            //shots = 2;
            //spread = 6f;
            recoil = 1f;
            hasPower = true;
            targetAir = true;
            shootSound = Sounds.flame;
            cooldownTime = 0.01f;
            consumePower(13.9f);
            shoot = new ShootSpread(2, 6f);
            shootType = UnityBullets.kelvinSmoke;
        }};

        caster = new PowerTurret("arc-caster"){{
            requirements(Category.turret, with(Items.silicon, 20, UnityItems.xenium, 15, Items.titanium, 30, UnityItems.advanceAlloy, 25));
            size = 3;
            health = 4600;
            range = 190f;
            reload = 120f;
            shootCone = 30f;
            inaccuracy = 9.2f;
            rotateSpeed = 5.5f;
            recoil = 1f;
            heatColor = UnityPal.lightHeat;
            cooldownTime = 0.01f;
            shootSound = Sounds.flame;
            shootEffect = Fx.none;
            //chargeTime = 51f;
            //chargeMaxDelay = 24f;
            //chargeEffects = 5;
            //chargeEffect = UnityFx.arcCharge;
            consumePower(9.4f);
            shoot = new ShootPattern(){{
                firstShotDelay = 51f;
            }};
            shootType = new ArcBulletType(4.6f, 8f){{
                lifetime = 43f;
                hitSize = 21f;

                lightningChance1 = 0.5f;
                lightningDamage1 = 29f;
                lightningChance2 = 0.2f;
                lightningDamage2 = 14f;
                length1 = 11;
                lengthRand1 = 7;
                chargeEffect = UnityFx.arcCharge;
            }};
        }};

        storm = new PowerTurret("arc-storm"){{
            requirements(Category.turret, with(Items.silicon, 80, UnityItems.xenium, 35, Items.titanium, 90, UnityItems.advanceAlloy, 50));
            size = 4;
            health = 7600;
            range = 210f;
            reload = 180f;
            //shots = 5;
            shootCone = 30f;
            inaccuracy = 11.2f;
            rotateSpeed = 5.5f;
            recoil = 2f;
            heatColor = UnityPal.lightHeat;
            cooldownTime = 0.01f;
            shootSound = Sounds.flame;
            shootEffect = Fx.none;
            //chargeTime = 51f;
            //chargeMaxDelay = 24f;
            //chargeEffects = 5;
            //chargeEffect = UnityFx.arcCharge;
            consumePower(33.4f);
            shoot = new ShootPattern(){{
                shots = 5;
                firstShotDelay = 51f;
            }};
            shootType = new ArcBulletType(4.6f, 8.6f){{
                lifetime = 53f;
                hitSize = 28f;

                radius = 13f;
                lightningChance1 = 0.7f;
                lightningDamage1 = 31f;
                lightningChance2 = 0.3f;
                lightningDamage2 = 17f;
                length1 = 13;
                lengthRand1 = 9;
                chargeEffect = UnityFx.arcCharge;
            }};
        }};

        eclipse = new LaserTurret("blue-eclipse"){
            {
                requirements(Category.turret, with(Items.lead, 620, Items.titanium, 520, Items.surgeAlloy, 720, Items.silicon, 760, Items.phaseFabric, 120, UnityItems.xenium, 620, UnityItems.advanceAlloy, 680));
                size = 7;
                health = 9000;
                range = 340f;
                reload = 280f;
                coolantMultiplier = 2.4f;
                shootCone = 40f;
                shake = 3f;
                shootEffect = Fx.shootBigSmoke2;
                recoil = 8;
                shootSound = Sounds.laser;
                loopSound = UnitySounds.eclipseBeam;
                loopSoundVolume = 2.5f;
                heatColor = UnityPal.advanceDark;
                rotateSpeed = 1.9f;
                shootDuration = 320f;
                firingMoveFract = 0.12f;
                //shootY = size * tilesize / 2f - recoil;

                consumePower(19f);
                shootType = new AcceleratingLaserBulletType(390f){{
                    colors = new Color[]{Color.valueOf("59a7ff55"), Color.valueOf("59a7ffaa"), Color.valueOf("a3e3ff"), Color.white};
                    width = 29.2f;
                    collisionWidth = 12f;
                    knockback = 2.2f;
                    lifetime = 18f;
                    accel = 0f;
                    fadeInTime = 0f;
                    fadeTime = 18f;
                    maxLength = 490f;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;
                    hitEffect = HitFx.eclipseHit;
                    buildingInsulator = (b, building) -> true;
                    unitInsulator = (b, u) -> true;
                }};

                consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.4f && liquid.flammability < 0.1f, 2.1f)).update(false);
            }
        };

        xenoCorruptor = new LaserTurret("xeno-corruptor"){
            {
                requirements(Category.turret, with(Items.lead, 640, Items.graphite, 740, Items.titanium, 560, Items.surgeAlloy, 650, Items.silicon, 720, Items.thorium, 400, UnityItems.xenium, 340, UnityItems.advanceAlloy, 640));
                health = 7900;
                size = 7;
                reload = 230f;
                range = 290f;
                coolantMultiplier = 1.4f;
                shootCone = 40f;
                shootDuration = 310f;
                firingMoveFract = 0.16f;
                shake = 3f;
                recoil = 8f;
                shootSound = Sounds.laser;
                loopSound = UnitySounds.xenoBeam;
                loopSoundVolume = 2f;
                heatColor = UnityPal.advanceDark;
                shootType = new ChangeTeamLaserBulletType(60f){{
                    length = 300f;
                    lifetime = 18f;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;
                    hitEffect = Fx.hitLancer;
                    incendChance = -1f;
                    lightColor = Color.valueOf("59a7ff");
                    conversionStatusEffect = UnityStatusEffects.teamConverted;
                    convertBlocks = false;

                    colors = new Color[]{Color.valueOf("59a7ff55"), Color.valueOf("59a7ffaa"), Color.valueOf("a3e3ff"), Color.white};
                }};

                consumePower(45f);
                consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.4f && liquid.flammability < 0.1f, 2.1f)).update(false);
            }
        };

        cube = new ObjPowerTurret("the-cube"){{
            requirements(Category.turret, with(Items.copper, 3300, Items.lead, 2900, Items.graphite, 4400, Items.silicon, 3800, Items.titanium, 4600, UnityItems.xenium, 2300, Items.phaseFabric, 670, UnityItems.advanceAlloy, 1070));
            health = 22500;
            object = UnityObjs.cube;
            size = 10;
            range = 320f;
            reload = 240f;
            coolantMultiplier = 1.1f;
            shootSound = UnitySounds.cubeBlast;
            shootType = new PointBlastLaserBulletType(580f){{
                length = 320f;
                lifetime = 17f;
                pierce = true;
                auraDamage = 8000f;
                damageRadius = 120f;
                laserColors = new Color[]{UnityPal.advance};
            }};

            consumePower(260f);
        }};

        wavefront = new WavefrontTurret("wavefront"){{
            requirements(Category.turret, with(Items.copper, 4900, Items.graphite, 6000, Items.silicon, 5000, Items.titanium, 6500, UnityItems.xenium, 1500, UnityItems.advanceAlloy, 1500, UnityItems.terminum, 700, UnityItems.terminaAlloy, 500));
            health = 50625;
            model = UnityModels.wavefront;
            size = 15;
            range = 420f;
            rotateSpeed = 3f;
            reload = 240f;
            coolantMultiplier = 0.9f;
            shootSound = UnitySounds.cubeBlast;

            shootType = new WavefrontLaser(2400f);
            consumePower(260f);
        }};

        //endregion
        //region end

        terminalCrucible = new GenericCrafter("terminal-crucible"){{
            requirements(Category.crafting, with(Items.lead, 810, Items.graphite, 720, Items.silicon, 520, Items.phaseFabric, 430, Items.surgeAlloy, 320, UnityItems.plagueAlloy, 120, UnityItems.darkAlloy, 120, UnityItems.lightAlloy, 120, UnityItems.advanceAlloy, 120, UnityItems.monolithAlloy, 120, UnityItems.sparkAlloy, 120, UnityItems.superAlloy, 120));
            size = 6;
            craftTime = 310f;
            ambientSound = Sounds.respawning;
            ambientSoundVolume = 0.6f;
            outputItem = new ItemStack(UnityItems.terminum, 1);

            consumePower(45.2f);
            consumeItems(with(UnityItems.plagueAlloy, 3, UnityItems.darkAlloy, 3, UnityItems.lightAlloy, 3, UnityItems.advanceAlloy, 3, UnityItems.monolithAlloy, 3, UnityItems.sparkAlloy, 3, UnityItems.superAlloy, 3));

            /*drawer = new DrawGlow(){
                @Override
                public void draw(GenericCrafterBuild build){
                    Draw.rect(build.block.region, build.x, build.y);

                    Draw.blend(Blending.additive);

                    Draw.color(1f, Mathf.absin(5f, 0.5f) + 0.5f, Mathf.absin(Time.time + 90f * Mathf.radDeg, 5f, 0.5f) + 0.5f, build.warmup);
                    Draw.rect(Regions.terminalCrucibleLightsRegion, build.x, build.y);

                    float b = (Mathf.absin(8f, 0.25f) + 0.75f) * build.warmup;
                    Draw.color(1f, b, b, b);

                    Draw.rect(top, build.x, build.y);

                    Draw.reset();
                    Draw.blend();
                }
            };*/
        }};

        endForge = new StemGenericCrafter("end-forge"){
            final int effectTimer = timers++;

            {
                requirements(Category.crafting, with(Items.silicon, 2300, Items.phaseFabric, 650, Items.surgeAlloy, 1350, UnityItems.plagueAlloy, 510, UnityItems.darkAlloy, 510, UnityItems.lightAlloy, 510, UnityItems.advanceAlloy, 510, UnityItems.monolithAlloy, 510, UnityItems.sparkAlloy, 510, UnityItems.superAlloy, 510, UnityItems.terminationFragment, 230));
                size = 8;
                craftTime = 410f;
                ambientSoundVolume = 0.6f;
                outputItem = new ItemStack(UnityItems.terminaAlloy, 2);

                consumePower(86.7f);
                consumeItems(with(UnityItems.terminum, 3, UnityItems.darkAlloy, 5, UnityItems.lightAlloy, 5));

                update((StemGenericCrafterBuild e) -> {
                    if(e.canConsume()){
                        if(e.timer.get(effectTimer, 120f)){
                            UnityFx.forgeFlameEffect.at(e);
                            UnityFx.forgeAbsorbPulseEffect.at(e);
                        }
                        if(Mathf.chanceDelta(0.7f * e.warmup)){
                            UnityFx.forgeAbsorbEffect.at(e.x, e.y, Mathf.random(360f));
                        }
                    }
                });

                /*drawer = new DrawGlow(){
                    @Override
                    public void draw(GenericCrafterBuild build){
                        Draw.rect(build.block.region, build.x, build.y);

                        Draw.blend(Blending.additive);
                        Draw.color(1f, Mathf.absin(5f, 0.5f) + 0.5f, Mathf.absin(Time.time + 90f * Mathf.radDeg, 5f, 0.5f) + 0.5f, build.warmup);

                        Draw.rect(Regions.endForgeLightsRegion, build.x, build.y);
                        float b = (Mathf.absin(8f, 0.25f) + 0.75f) * build.warmup;

                        Draw.color(1f, b, b, b);
                        Draw.rect(top, build.x, build.y);

                        for(int i = 0; i < 4; i++){
                            float ang = i * 90f;
                            for(int s = 0; s < 2; s++){
                                float offset = 360f / 8f * (i * 2 + s);
                                TextureRegion reg = Regions.endForgeTopSmallRegion;
                                int sign = Mathf.signs[s];

                                float colA = (Mathf.absin(Time.time + offset * Mathf.radDeg, 8f, 0.25f) + 0.75f) * build.warmup;
                                float colB = (Mathf.absin(Time.time + (90f + offset) * Mathf.radDeg, 8f, 0.25f) + 0.75f) * build.warmup;

                                Draw.color(1, colA, colB, build.warmup);
                                Draw.rect(reg, build.x, build.y, reg.width * sign * Draw.scl, reg.height * Draw.scl, -ang);
                            }
                        }

                        Draw.blend();
                        Draw.color();
                    }
                };*/
            }
        };

        tenmeikiri = new EndLaserTurret("tenmeikiri"){{
            requirements(Category.turret, with(Items.phaseFabric, 3000, Items.surgeAlloy, 4000,
            UnityItems.darkAlloy, 1800, UnityItems.terminum, 1200, UnityItems.terminaAlloy, 200));

            health = 23000;
            range = 900f;
            size = 15;

            shootCone = 1.5f;
            reload = 5f * 60f;
            coolantMultiplier = 0.5f;
            recoil = 15f;
            absorbLasers = true;
            //shootY = 8f;
            //chargeTime = 158f;
            //chargeEffects = 12;
            //chargeMaxDelay = 80f;
            //chargeEffect = ChargeFx.tenmeikiriChargeEffect;
            //chargeBeginEffect = ChargeFx.tenmeikiriChargeBegin;
            chargeSound = UnitySounds.tenmeikiriCharge;
            shootSound = UnitySounds.tenmeikiriShoot;
            shake = 4f;
            shoot = new ShootPattern(){{
                firstShotDelay = 158f;
            }};
            shootType = new EndCutterLaserBulletType(7800f){{
                maxLength = 1200f;
                lifetime = 3f * 60f;
                width = 30f;
                laserSpeed = 80f;
                status = StatusEffects.melting;
                antiCheatScl = 5f;
                statusDuration = 200f;
                lightningColor = UnityPal.scarColor;
                lightningDamage = 85f;
                lightningLength = 15;

                ratioDamage = 1f / 60f;
                ratioStart = 30000f;
                overDamage = 350000f;
                bleedDuration = 5f * 60f;
            }};

            consumePower(350f);
            consume(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.25f && liquid.flammability < 0.1f, 3.1f)).update(false);
        }};

        endGame = new EndGameTurret("endgame"){{
            requirements(Category.turret, with(Items.phaseFabric, 9500, Items.surgeAlloy, 10500,
                UnityItems.darkAlloy, 2300, UnityItems.lightAlloy, 2300, UnityItems.advanceAlloy, 2300,
                UnityItems.plagueAlloy, 2300, UnityItems.sparkAlloy, 2300, UnityItems.monolithAlloy, 2300,
                UnityItems.superAlloy, 2300, UnityItems.terminum, 1600, UnityItems.terminaAlloy, 800, UnityItems.terminationFragment, 100
            ));

            shootCone = 360f;
            reload = 430f;
            range = 820f;
            size = 14;
            coolantMultiplier = 0.6f;
            hasItems = true;
            itemCapacity = 10;
            loopSoundVolume = 0.2f;

            shootType = new BulletType(){{
                //damage = Float.MAX_VALUE;
                damage = (float)Double.MAX_VALUE;
            }};
            consumeItem(UnityItems.terminum, 2);
        }};

        //endregion
    }
}

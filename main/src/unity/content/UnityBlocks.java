package unity.content;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.type.*;
import mindustry.ctype.*;
import mindustry.content.*;
import unity.annotations.Annotations.*;
import unity.entities.bullet.*;
import unity.gen.*;
import unity.graphics.*;
import unity.type.ExpType.*;
import unity.type.exp.*;
import unity.util.*;
import unity.world.blocks.*;
import unity.world.blocks.defense.*;
import unity.world.blocks.defense.turrets.*;
import unity.world.blocks.defense.turrets.exp.*;
import unity.world.blocks.distribution.*;
import unity.world.blocks.logic.*;
import unity.world.blocks.power.*;
import unity.world.blocks.production.*;
import unity.world.blocks.sandbox.*;
import unity.world.blocks.storage.*;
import unity.world.blocks.units.*;
import unity.world.consumers.*;
import unity.world.draw.*;
import unity.world.graphs.*;
import younggamExperimental.*;
import younggamExperimental.blocks.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.type.ItemStack.*;

public class UnityBlocks implements ContentList{
    public static Block//@formatter:off
    //order is load order. double newLine means next js file or json contents directory. eh, not that important.
    //global
    recursiveReconstructor,
    //@formatter:on
    lightLamp, oilLamp, lightLaser, lightLampInfi, lightReflector, lightReflector1, lightOmnimirror, lightFilter, lightInvertedFilter, lightDivisor, lightDivisor1, lightItemFilter, lightPanel, lightInfluencer,

    metaglassWall, metaglassWallLarge,

    oreNickel, oreUmbrium, oreLuminum, oreMonolite, oreImberium;

    public static @FactionDef(type = "dark")
    Block apparition, ghost, banshee, fallout, catastrophe, calamity, extinction,

    darkAlloyForge,

    darkWall, darkWallLarge;

    public static @FactionDef(type = "imber")
    Block orb, shockwire, current, plasma, electrobomb, shielder,

    sparkAlloyForge,

    electroTile;

    public static @FactionDef(type = "koruh")
    Block solidifier, steelSmelter, liquifier,

    stoneWall, denseWall, steelWall, steelWallLarge, diriumWall, diriumWallLarge,

    steelConveyor, diriumConveyor,

    //laserTurret, inferno,
    
    daggerPad, novaPad, bufferPad, cachePad,

    teleporter, teleunit, expOutput, expUnloader, expTank, expChest, expFountain, expVoid;

    public static
    @ExpDef(type = ExpBlock.class)
    @FactionDef(type = "koruh")
    Block laser, laserCharge, laserFrost, laserBranch, laserFractal, laserKelvin, laserBreakthrough;

    //public static @FactionDef(type = "light")
    //Block

    public static @FactionDef(type = "monolith")
    Block monolithAlloyForge,

    mage, oracle;

    public static
    @FactionDef(type = "monolith")
    @LoadRegs(value = {
        "supernova-head",
        "supernova-core",
        "supernova-wing-left", "supernova-wing-right",
        "supernova-wing-left-bottom", "supernova-wing-right-bottom",
        "supernova-bottom"
    }, outline = true)
    Block supernova;

    //monolithGroundFactory;

    public static @FactionDef(type = "youngcha")
    Block concreteBlank, concreteFill, concreteNumber, concreteStripe, concrete, stoneFullTiles, stoneFull, stoneHalf, stoneTiles,

    heatPipe, smallRadiator, //heatdistributor

    driveShaft,

    inlineGearbox,

    shaftRouter,

    simpleTransmission,

    cruciblePump,

    mechanicalConveyor,

    thermalHeater, combustionHeater, infiHeater, infiCooler, solarCollector, solarReflector, //heatgenerators

    nickelStator, nickelStatorLarge, nickelElectromagnet, neodymiumStator, electricRotor, electricRotorSmall, //magnets

    torqueInfi,

    handCrank,

    windTurbine,

    waterTurbine,

    electricMotor,

    augerDrill, mechanicalExtractor,

    crucible, holdingCrucible, castingMold, //crucible

    sporePyrolyser,

    sporeFarm,

    cupronickelWall, cupronickelWallLarge, //youngchaWalls

    chopper,

    smallTurret, medTurret;

    public static @FactionDef(type = "advance")
    Block celsius, kelvin, xenoCorruptor, cube;

    public static @FactionDef(type = "end")
    Block terminalCrucible, endForge, endGame;

    @Override
    public void load(){
        
        //region global

        recursiveReconstructor = new SelectableReconstructor("recursive-reconstructor"){{
            requirements(Category.units, with(Items.graphite, 1600, Items.silicon, 2000, Items.metaglass, 900, Items.thorium, 600, Items.lead, 1200, Items.plastanium, 3600));
            size = 11;
            liquidCapacity = 360f;
            configurable = true;
            constructTime = 20000f;
            minTier = 6;
            upgrades.add(
                new UnitType[]{UnitTypes.toxopid, UnityUnitTypes.projectSpiboss},
                new UnitType[]{UnitTypes.corvus, UnityUnitTypes.ursa},
                new UnitType[]{UnityUnitTypes.monument, UnityUnitTypes.colossus}
            );
            otherUpgrades.add(new UnitType[]{UnityUnitTypes.projectSpiboss, UnityUnitTypes.arcaetana});
            consumes.power(5f);
            consumes.items(with(Items.silicon, 1200, Items.metaglass, 800, Items.thorium, 700, Items.surgeAlloy, 400, Items.plastanium, 600, Items.phaseFabric, 350));
            consumes.liquid(Liquids.cryofluid, 7f);
        }};

        lightLamp = new LightSource("light-lamp"){{
            consumes.power(1f);
            requirements(Category.logic, with(Items.lead, 5, Items.metaglass, 10));
            drawer = new DrawLightSource();
            lightLength = 30;
        }};

        oilLamp = new LightSource("oil-lamp", true){{
            size = 3;
            health = 240;
            consumes.power(1.8f);
            consumes.liquid(Liquids.oil, 0.1f);
            requirements(Category.logic, with(Items.lead, 20, Items.metaglass, 20, Items.titanium, 15));
            drawer = new DrawLightSource();
            lightLength = 150;
            lightStrength = 750;
        }};

        lightLaser = new LightSource("light-laser"){{
            health = 60;
            consumes.power(1.5f);
            requirements(Category.logic, BuildVisibility.sandboxOnly, with(Items.metaglass, 10, Items.silicon, 5, Items.titanium, 5));
            alwaysUnlocked = true;
            drawer = new DrawLightSource();
            lightLength = 30;
            lightInterval = 0;
        }};

        lightLampInfi = new LightSource("light-lamp-infi"){{
            hasPower = false;
            consumesPower = false;
            requirements(Category.logic, BuildVisibility.sandboxOnly, with());
            alwaysUnlocked = true;
            drawer = new DrawLightSource();
            lightLength = 150;
            lightStrength = 600000;
            scaleStatus = false;
            maxLightLength = 7500;
        }};

        lightReflector = new LightReflector("light-reflector"){{
            requirements(Category.logic, with(Items.metaglass, 10));
        }};

        lightReflector1 = new LightReflector("light-reflector-1"){{
            diagonal = false;
            requirements(Category.logic, with(Items.metaglass, 10));
        }};

        lightOmnimirror = new LightOmniReflector("light-omnimirror"){{
            health = 80;
            requirements(Category.logic, with(Items.metaglass, 10, Items.silicon, 5));
        }};

        lightFilter = new LightFilter("light-filter"){{
            health = 60;
            requirements(Category.logic, with(Items.graphite, 10, Items.metaglass, 10));
        }};

        lightInvertedFilter = new LightFilter("light-inverted-filter", true){{
            health = 60;
            requirements(Category.logic, with(Items.graphite, 10, Items.metaglass, 10));
        }};

        lightDivisor = new LightDivisor("light-divisor"){{
            health = 80;
            requirements(Category.logic, with(Items.metaglass, 10, Items.titanium, 2));
        }};

        lightDivisor1 = new LightDivisor("light-divisor-1"){{
            diagonal = false;
            health = 80;
            requirements(Category.logic, with(Items.metaglass, 10, Items.titanium, 2));
        }};

        lightItemFilter = new LightRouter("light-item-filter"){{
            health = 60;
            requirements(Category.logic, with(Items.graphite, 5, Items.metaglass, 20, Items.silicon, 10));
        }};

        lightPanel = new LightGenerator("light-panel"){{
            health = 100;
            lightStrength = 80f;
            scaleStatus = true;
            powerProduction = 1f;
            requirements(Category.logic, with(Items.copper, 15, Items.graphite, 10, Items.silicon, 15));
        }};

        lightInfluencer = new LightInfluencer("light-influencer"){{
            health = 60;
            lightStrength = 1f;
            scaleStatus = true;
            powerProduction = 1f;
            requirements(Category.logic, with(Items.lead, 15, Items.metaglass, 10, Items.silicon, 5));
        }};

        metaglassWall = new LightWall("metaglass-wall"){{
            health = 350;
            requirements(Category.defense, with(Items.lead, 6, Items.metaglass, 6));
        }};

        metaglassWallLarge = new LightWall("metaglass-wall-large"){{
            size = 2;
            health = 1400;
            requirements(Category.defense, with(Items.lead, 24, Items.metaglass, 24));
        }};

        oreNickel = new OreBlock(UnityItems.nickel){{
            oreScale = 24.77f;
            oreThreshold = 0.913f;
            oreDefault = true;
        }};

        oreUmbrium = new OreBlock(UnityItems.umbrium){{
            oreScale = 23.77f;
            oreThreshold = 0.813f;
            oreDefault = true;
        }};

        oreLuminum = new OreBlock(UnityItems.luminum){{
            oreScale = 23.77f;
            oreThreshold = 0.81f;
            oreDefault = true;
        }};

        oreMonolite = new OreBlock(UnityItems.monolite){{
            oreScale = 23.77f;
            oreThreshold = 0.807f;
            oreDefault = true;
        }};

        oreImberium = new OreBlock(UnityItems.imberium){{
            oreScale = 23.77f;
            oreThreshold = 0.807f;
            oreDefault = true;
        }};

        //endregion
        //region dark

        apparition = new ItemTurret("apparition"){
            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
            }

            {
                requirements(Category.turret, with(Items.copper, 350, Items.graphite, 380, Items.silicon, 360, Items.plastanium, 200, Items.thorium, 220, UnityItems.umbrium, 370, Items.surgeAlloy, 290));
                size = 5;
                health = 3975;
                range = 235f;
                reloadTime = 6f;
                coolantMultiplier = 0.5f;
                restitution = 0.09f;
                inaccuracy = 3f;
                spread = 12f;
                shots = 2;
                alternate = true;
                recoilAmount = 3f;
                rotateSpeed = 4.5f;
                ammo(Items.graphite, UnityBullets.standardDenseLarge, Items.silicon, UnityBullets.standardHomingLarge, Items.pyratite, UnityBullets.standardIncendiaryLarge, Items.thorium, UnityBullets.standardThoriumLarge);
            }
        };

        ghost = new BarrelsItemTurret("ghost"){{
            size = 8;
            health = 9750;
            range = 290f;
            reloadTime = 9f;
            coolantMultiplier = 0.5f;
            restitution = 0.08f;
            inaccuracy = 3f;
            shots = 2;
            alternate = true;
            recoilAmount = 5.5f;
            rotateSpeed = 3.5f;
            spread = 21f;
            addBarrel(8f, 18.75f, 6f);
            ammo(Items.graphite, UnityBullets.standardDenseHeavy, Items.silicon, UnityBullets.standardHomingHeavy, Items.pyratite, UnityBullets.standardIncendiaryHeavy, Items.thorium, UnityBullets.standardThoriumHeavy);
            requirements(Category.turret, with(Items.copper, 1150, Items.graphite, 1420, Items.silicon, 960, Items.plastanium, 800, Items.thorium, 1230, UnityItems.darkAlloy, 380));
        }};

        banshee = new unity.world.blocks.defense.turrets.BarrelsItemTurret("banshee"){{
            size = 12;
            health = 22000;
            range = 370f;
            reloadTime = 12f;
            coolantMultiplier = 0.5f;
            restitution = 0.08f;
            inaccuracy = 3f;
            shots = 2;
            alternate = true;
            recoilAmount = 5.5f;
            rotateSpeed = 3.5f;
            spread = 37f;
            focus = true;
            addBarrel(23.5f, 36.5f, 9f);
            addBarrel(8.5f, 24.5f, 6f);
            ammo(Items.graphite, UnityBullets.standardDenseMassive, Items.silicon, UnityBullets.standardHomingMassive, Items.pyratite, UnityBullets.standardIncendiaryMassive, Items.thorium, UnityBullets.standardThoriumMassive);
            requirements(Category.turret, with(Items.copper, 2800, Items.graphite, 2980, Items.silicon, 2300, Items.titanium, 1900, Items.phaseFabric, 1760, Items.thorium, 1780, UnityItems.darkAlloy, 1280));
        }};

        fallout = new LaserTurret("fallout"){
            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
            }

            {
                size = 5;
                health = 3975;
                range = 215f;
                reloadTime = 110f;
                coolantMultiplier = 0.8f;
                shootCone = 40f;
                shootDuration = 230f;
                // shootLength = 5f;
                powerUse = 19f;
                shootShake = 3f;
                firingMoveFract = 0.2f;
                shootEffect = Fx.shootBigSmoke2;
                recoilAmount = 4f;
                shootSound = Sounds.laserbig;
                heatColor = Color.valueOf("e04300");
                rotateSpeed = 3.5f;
                loopSound = Sounds.beam;
                loopSoundVolume = 2.1f;
                requirements(Category.turret, with(Items.copper, 450, Items.lead, 350, Items.graphite, 390, Items.silicon, 360, Items.titanium, 250, UnityItems.umbrium, 370, Items.surgeAlloy, 360));
                shootType = UnityBullets.falloutLaser;
                consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability < 0.1f, 0.58f)).update(false);
            }
        };

        catastrophe = new BigLaserTurret("catastrophe"){{
            size = 8;
            health = 9750;
            range = 300f;
            reloadTime = 190f;
            coolantMultiplier = 0.6f;
            shootCone = 40f;
            shootDuration = 320f;
            // shootLength = 12f;
            powerUse = 39f;
            shootShake = 4f;
            firingMoveFract = 0.16f;
            shootEffect = Fx.shootBigSmoke2;
            recoilAmount = 7f;
            cooldown = 0.012f;
            heatColor = Color.white;
            rotateSpeed = 1.9f;
            shootSound = Sounds.laserbig;
            loopSound = Sounds.beam;
            loopSoundVolume = 2.2f;
            expanded = true;
            requirements(Category.turret, with(Items.copper, 1250, Items.lead, 1320, Items.graphite, 1100, Items.titanium, 1340, Items.surgeAlloy, 1240, Items.silicon, 1350, Items.thorium, 770, UnityItems.darkAlloy, 370));
            // chargeBeginEffect = UnityFx.catastropheCharge;
            // chargeTime = UnityFx.catastropheCharge.lifetime;
            shootType = UnityBullets.catastropheLaser;
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.4f && liquid.flammability < 0.1f, 1.3f)).update(false);
        }};

        calamity = new BigLaserTurret("calamity"){{
            size = 12;
            health = 22000;
            range = 420f;
            reloadTime = 320f;
            coolantMultiplier = 0.6f;
            shootCone = 23f;
            shootDuration = 360f;
            // shootLength = 14f;
            powerUse = 87f;
            shootShake = 4f;
            firingMoveFract = 0.09f;
            shootEffect = Fx.shootBigSmoke2;
            recoilAmount = 7f;
            cooldown = 0.009f;
            heatColor = Color.white;
            rotateSpeed = 0.97f;
            shootSound = Sounds.laserbig;
            loopSound = Sounds.beam;
            loopSoundVolume = 2.6f;
            expanded = true;
            requirements(Category.turret, with(Items.copper, 2800, Items.lead, 2970, Items.graphite, 2475, Items.titanium, 3100, Items.surgeAlloy, 2790, Items.silicon, 3025, Items.thorium, 1750, UnityItems.darkAlloy, 1250));
            // chargeBeginEffect = UnityFx.calamityCharge;
            // chargeTime = UnityFx.calamityCharge.lifetime;
            shootType = UnityBullets.calamityLaser;
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.3f && liquid.flammability < 0.1f, 2.1f)).update(false);
        }};

        extinction = new BigLaserTurret("extinction"){{
            requirements(Category.turret, with(Items.copper, 3800, Items.lead, 4100, Items.graphite, 3200, Items.titanium, 4200, Items.surgeAlloy, 3800, Items.silicon, 4300, Items.thorium, 2400, UnityItems.darkAlloy, 1700, UnityItems.terminum, 900, UnityItems.terminaAlloy, 500));
            size = 14;
            health = 29500;
            range = 520f;
            reloadTime = 380f;
            coolantMultiplier = 0.4f;
            shootCone = 12f;
            shootDuration = 360f;
            // shootLength = 10f;
            powerUse = 175f;
            shootShake = 4f;
            firingMoveFract = 0.09f;
            shootEffect = Fx.shootBigSmoke2;
            recoilAmount = 7f;
            cooldown = 0.003f;
            heatColor = Color.white;
            rotateSpeed = 0.82f;
            shootSound = UnitySounds.extinctionShoot;
            loopSound = UnitySounds.beamIntenseHighpitchTone;
            loopSoundVolume = 2f;
            expanded = true;
            chargeBeginEffect = UnityFx.extinctionCharge;
            // chargeTime = UnityFx.extinctionCharge.lifetime;
            shootType = UnityBullets.extinctionLaser;
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.27f && liquid.flammability < 0.1f, 2.5f)).update(false);
        }};

        darkAlloyForge = new StemGenericSmelter("dark-alloy-forge"){{
            requirements(Category.crafting, with(Items.copper, 30, Items.lead, 25));
            outputItem = new ItemStack(UnityItems.darkAlloy, 3);
            craftTime = 140f;
            size = 4;
            ambientSound = Sounds.respawning;
            ambientSoundVolume = 0.6f;
            consumes.items(with(Items.lead, 2, Items.silicon, 3, Items.blastCompound, 1, Items.phaseFabric, 1, UnityItems.umbrium, 2));
            consumes.power(3.2f);
            afterUpdate = e -> {
                if(e.consValid() && Mathf.chanceDelta(0.76f)) UnityFx.craftingEffect.at(e.getX(), e.getY(), Mathf.random(360f));
            };
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
        //region imber

        orb = new PowerTurret("orb"){{
            requirements(Category.turret, with(Items.copper, 55, Items.lead, 30, Items.graphite, 25, Items.silicon, 35, UnityItems.imberium, 20));
            size = 2;
            health = 1320;
            range = 145f;
            reloadTime = 130f;
            coolantMultiplier = 2f;
            shootCone = 0.1f;
            shots = 1;
            inaccuracy = 12f;
            chargeTime = 65f;
            chargeEffects = 5;
            chargeMaxDelay = 25f;
            powerUse = 10.4f;
            targetAir = false;
            shootType = UnityBullets.orb;
            shootSound = Sounds.laser;
            heatColor = Pal.turretHeat;
            shootEffect = UnityFx.orbShoot;
            smokeEffect = Fx.none;
            chargeEffect = UnityFx.orbCharge;
            chargeBeginEffect = UnityFx.orbChargeBegin;
        }};

        shockwire = new LaserTurret("shockwire"){{
            requirements(Category.turret, with(Items.copper, 150, Items.lead, 145, Items.titanium, 160, Items.silicon, 130, UnityItems.imberium, 70));
            size = 2;
            health = 1400;
            range = 125f;
            reloadTime = 140f;
            coolantMultiplier = 2f;
            shootCone = 1f;
            inaccuracy = 0f;
            powerUse = 8.6f;
            targetAir = false;
            shootType = UnityBullets.shockBeam;
            shootSound = Sounds.thruster;
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.4f)).update(false);
        }};

        current = new PowerTurret("current"){{
            requirements(Category.turret, with(Items.copper, 280, Items.lead, 295, Items.silicon, 260, UnityItems.sparkAlloy, 65));
            size = 3;
            health = 2400;
            range = 220f;
            reloadTime = 480f;
            coolantMultiplier = 2;
            shootCone = 0.01f;
            inaccuracy = 0f;
            chargeTime = 240f;
            chargeEffects = 4;
            chargeMaxDelay = 260;
            powerUse = 13.8f;
            shootType = UnityBullets.currentStroke;
            shootSound = Sounds.laserbig;
            chargeEffect = UnityFx.currentCharge;
            chargeBeginEffect = UnityFx.currentChargeBegin;
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.52f)).boost();
        }};

        plasma = new PowerTurret("plasma"){{
            requirements(Category.turret, with(Items.copper, 580, Items.lead, 520, Items.graphite, 410, Items.silicon, 390, Items.surgeAlloy, 180, UnityItems.sparkAlloy, 110));
            size = 4;
            health = 2800;
            range = 200f;
            reloadTime = 360f;
            recoilAmount = 4f;
            coolantMultiplier = 1.2f;
            liquidCapacity = 20f;
            shootCone = 1f;
            inaccuracy = 0f;
            powerUse = 15.2f;
            shootType = UnityBullets.plasmaTriangle;
            shootSound = Sounds.shotgun;
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.52f)).boost();
        }};

        electrobomb = new ItemTurret("electrobomb"){            
            {
                requirements(Category.turret, with(Items.titanium, 360, Items.thorium, 630, Items.silicon, 240, UnityItems.sparkAlloy, 420));
                size = 5;
                range = 400f;
                minRange = 60f;
                reloadTime = 320f;
                coolantMultiplier = 2f;
                shootCone = 20f;
                shots = 1;
                inaccuracy = 0f;
                targetAir = false;
                ammo(UnityItems.sparkAlloy, UnityBullets.surgeBomb);
                shootSound = Sounds.laser;
                shootEffect = Fx.none;
                smokeEffect = Fx.none;
                consumes.powerCond(10f, TurretBuild::isActive);
            }
            
            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
            }
        };

        shielder = new ShieldTurret("shielder"){{
            requirements(Category.turret, with(Items.copper, 300, Items.lead, 100, Items.titanium, 160, Items.silicon, 240, UnityItems.sparkAlloy, 90));
            size = 3;
            health = 900;
            range = 260;
            reloadTime = 800;
            coolantMultiplier = 2;
            shootCone = 60;
            inaccuracy = 0;
            powerUse = 6.4f;
            targetAir = false;
            shootType = UnityBullets.shielderBullet;
            shootSound = /*test*/Sounds.pew;
            chargeEffect = new Effect(38f, e -> {
                Draw.color(Pal.accent);
                Angles.randLenVectors(e.id, 2, 1 + 20 * e.fout(), e.rotation, 120, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3 + 1));
            });
            chargeBeginEffect = Fx.none;
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.4f)).update(false);
        }};

        sparkAlloyForge = new StemGenericSmelter("spark-alloy-forge"){{
            requirements(Category.crafting, with(Items.lead, 160, Items.graphite, 340, UnityItems.imberium, 270, Items.silicon, 250, Items.thorium, 120, Items.surgeAlloy, 100));
            outputItem = new ItemStack(UnityItems.sparkAlloy, 4);
            size = 4;
            craftTime = 160f;
            ambientSound = Sounds.machine;
            ambientSoundVolume = 0.6f;
            craftEffect = UnityFx.imberCircleSparkCraftingEffect;
            afterUpdate = e -> {
                if(e.consValid()){
                    if(Mathf.chanceDelta(0.3f)) UnityFx.imberSparkCraftingEffect.at(e.getX(), e.getY(), Mathf.random(360f));
                    else if(Mathf.chanceDelta(0.02f)) Lightning.create(e.team, UnityPal.imberColor, 5f, e.x, e.y, Mathf.random(360f), 5);
                }
            };
            consumes.power(2.6f);
            consumes.items(with(Items.surgeAlloy, 3, Items.titanium, 4, Items.silicon, 6, UnityItems.imberium, 3));
        }};

        electroTile = new Floor("electro-tile");

        //endregion
        //region koruh

        solidifier = new LiquidsSmelter("solidifier"){{
            requirements(Category.crafting, with(Items.copper, 20, UnityItems.denseAlloy, 30));
            health = 150;
            hasItems = true;
            liquidCapacity = 12f;
            updateEffect = Fx.fuelburn;
            craftEffect = UnityFx.rockFx;
            craftTime = 60f;
            flameColor = Color.valueOf("ffb096");
            outputItem = new ItemStack(UnityItems.stone, 1);
            preserveDraw = false;
            afterDrawer = e -> {
                Draw.rect(region, e.x, e.y);
                if(e.warmup > 0f){
                    Draw.color(liquids[0].color, e.liquids.get(liquids[0]) / liquidCapacity);
                    Draw.rect(topRegion, e.x, e.y);
                    Draw.color();
                }
            };
            consumes.add(new ConsumeLiquids(new LiquidStack[]{new LiquidStack(UnityLiquids.lava, 0.1f), new LiquidStack(Liquids.water, 0.1f)}));
        }};

        steelSmelter = new StemGenericSmelter("steel-smelter"){{
            requirements(Category.crafting, with(Items.lead, 45, Items.silicon, 20, UnityItems.denseAlloy, 30));
            health = 140;
            itemCapacity = 10;
            craftEffect = UnityFx.craftFx;
            craftTime = 300f;
            outputItem = new ItemStack(UnityItems.steel, 1);
            preserveDraw = false;
            afterDrawer = e -> {
                Draw.rect(region, e.x, e.y);
                if(e.warmup > 0f){
                    Draw.color(1f, 1f, 1f, e.warmup * Mathf.absin(8f, 0.6f));
                    Draw.rect(topRegion, e.x, e.y);
                    Draw.color();
                }
            };
            consumes.power(2f);
            consumes.items(with(Items.coal, 2, Items.graphite, 2, UnityItems.denseAlloy, 3));
        }};

        liquifier = new BurnerSmelter("liquifier"){{
            requirements(Category.crafting, with(Items.titanium, 30, Items.silicon, 15, UnityItems.steel, 10));
            health = 100;
            hasLiquids = true;
            updateEffect = Fx.fuelburn;
            craftTime = 30f;
            outputLiquid = new LiquidStack(UnityLiquids.lava, 0.1f);
            configClear(b -> Fires.create(b.tile));
            afterUpdate = e -> {//eh is it chanceDelta?
                if(e.progress == 0f && e.warmup > 0.001f && !Vars.net.client() && Mathf.chance(0.2f)) e.configureAny(null);
            };
            preserveDraw = false;
            afterDrawer = e -> {
                Draw.rect(region, e.x, e.y);
                if(e.warmup > 0f){
                    Liquid liquid = outputLiquid.liquid;
                    Draw.color(liquid.color, e.liquids.get(liquid) / liquidCapacity);
                    Draw.rect(topRegion, e.x, e.y);
                    Draw.color();
                }
            };
            consumes.power(3.7f);
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

        steelWall = new LimitWall("steel-wall"){{
            requirements(Category.defense, with(UnityItems.steel, 6));
            maxDamage = 24f;
            health = 810;
        }};

        steelWallLarge = new LimitWall("steel-wall-large"){{
            requirements(Category.defense, with(UnityItems.steel, 24));
            maxDamage = 48f;
            health = 3240;
            size = 2;
        }};

        diriumWall = new LimitWall("dirium-wall"){{
            requirements(Category.defense, with(UnityItems.dirium, 6));
            maxDamage = 76f;
            blinkFrame = 30f;
            health = 760;
        }};

        diriumWallLarge = new LimitWall("dirium-wall-large"){{
            requirements(Category.defense, with(UnityItems.dirium, 24));
            maxDamage = 152f;
            blinkFrame = 30f;
            health = 3040;
            size = 2;
        }};

        steelConveyor = new ExpConveyor("steel-conveyor"){{
            requirements(Category.distribution, with(UnityItems.stone, 1, UnityItems.denseAlloy, 1, UnityItems.steel, 1));
            health = 140;
            speed = 0.1f;
            displayedSpeed = 12.5f;
            drawMultiplier = 1.9f;
        }};

        diriumConveyor = new ExpConveyor("dirium-conveyor"){{
            requirements(Category.distribution, with(UnityItems.steel, 1, Items.phaseFabric, 1, UnityItems.dirium, 1));
            health = 150;
            speed = 0.16f;
            displayedSpeed = 20f;
            drawMultiplier = 1.3f;
        }};
        
        daggerPad = new MechPad("dagger-pad"){{
            requirements(Category.units, with(Items.copper, 75, Items.lead, 100, Items.graphite, 100));
            size = 2;
            consumes.power(0.5f);
        }};
        
        novaPad = new MechPad("nova-pad"){{
            requirements(Category.units, with(Items.copper, 125, Items.lead, 125, Items.silicon, 50, Items.titanium, 50));
            size = 2;
            craftTime = 120f;
            consumes.power(1f);
            unitType = UnitTypes.nova;
        }};
        
        bufferPad = new MechPad("buffer-pad"){{
            requirements(Category.units, with(UnityItems.stone, 120, Items.copper, 100, Items.lead, 60, Items.silicon, 25));
            size = 2;
            craftTime = 300f;
            consumes.power(0.7f);
            unitType = UnityUnitTypes.buffer;
        }};
        
        cachePad = new MechPad("cache-pad"){{
            requirements(Category.units, with(UnityItems.stone, 150, Items.lead, 160, Items.silicon, 100, Items.titanium, 60, Items.plastanium, 50));
            size = 2;
            craftTime = 300f;
            consumes.power(0.8f);
            unitType = UnityUnitTypes.cache;
        }};

        /*inferno = new ExpItemTurret("inferno", 10){{
            requirements(Category.turret, with(Items.copper, 150, Items.lead, 165, Items.graphite, 120, Items.silicon, 130));
            ammo(Items.coal, UnityBullets.coalBlaze, Items.pyratite, UnityBullets.pyraBlaze);
            size = 3;
            health = 1500;
            range = 80f;
            reloadTime = 10f;
            shootCone = 5f;
            addExpField("exp", "useless", 0, 2);
        }};TODO*/

        teleporter = new Teleporter("teleporter"){{
            requirements(Category.distribution, with(Items.lead, 22, Items.silicon, 10, Items.phaseFabric, 32, UnityItems.dirium, 32));
        }};

        teleunit = new TeleUnit("teleunit"){{
            requirements(Category.units, with(Items.lead, 180, Items.titanium, 80, Items.silicon, 90, Items.phaseFabric, 64, UnityItems.dirium, 48));
            size = 3;
            ambientSound = Sounds.techloop;
            ambientSoundVolume = 0.02f;
            consumes.power(3f);
        }};

        expOutput = new ExpOutput("exp-output"){{
            requirements(Category.effect, with(UnityItems.stone, 25, Items.copper, 25, Items.graphite, 10));
            health = 60;
            unloadAmount = 0.4f;
            unloadTime = 20f;
            consumes.power(0.06f);
        }};

        expUnloader = new ExpUnloader("exp-unloader"){{
            requirements(Category.effect, with(Items.graphite, 25, Items.silicon, 25, UnityItems.steel, 25));
            health = 80;
            loadSides = true;
            consumes.power(0.25f);
        }};

        expTank = new ExpStorageBlock("exp-tank"){{
            requirements(Category.effect, with(Items.copper, 100, Items.graphite, 100, UnityItems.denseAlloy, 30));
            size = 2;
            health = 300;
        }};

        expChest = new ExpStorageBlock("exp-chest"){{
            requirements(Category.effect, with(Items.copper, 400, UnityItems.steel, 250, Items.phaseFabric, 120));
            size = 4;
            health = 1200;
            expCapacity = 3200;
            lightRadius = 50f;
            lightOpacity = 0.6f;
        }};

        expFountain = new ExpFountain("exp-fountain"){{
            requirements(Category.effect, BuildVisibility.sandboxOnly, with());
            health = 200;
        }};

        expVoid = new ExpVoid("exp-void"){{
            requirements(Category.effect, BuildVisibility.sandboxOnly, with());
            health = 200;
        }};

        laser = new ExpPowerTurret("laser-turret"){
            {
                requirements(Category.turret, with(Items.copper, 190, Items.silicon, 110, Items.titanium, 15));
                size = 2;
                health = 800;

                reloadTime = 35f;
                coolantMultiplier = 2f;
                range = 140f;
                targetAir = false;
                shootSound = Sounds.pew_;

                powerUse = 7f;

                shootType = UnityBullets.laser;
            }

            @Override
            public void init(){
                super.init();

                ExpBlock block = ExpMeta.map(this);
                block.hasExp = true;
                block.condConfig = true;
                block.enableUpgrade = true;

                block.maxLevel = 10;

                block.addUpgrade(laserCharge, 10);
                block.addUpgrade(laserFrost, 10);

                block.addField(ExpFieldType.linear, ReloadTurret.class, "reloadTime", reloadTime, -2f);
                block.addField(ExpFieldType.bool, Turret.class, "targetAir", false, 5f);

                block.setupFields();
                block.setStats();
                block.init();
            }
        };

        laserCharge = new ExpPowerChargeTurret("charge-laser-turret"){
            {
                requirements(Category.turret, with(Items.copper, 190, Items.silicon, 110, Items.titanium, 15));
                size = 2;
                health = 1400;

                reloadTime = 60f;
                coolantMultiplier = 2f;
                range = 140f;
                chargeTime = 50f;
                chargeMaxDelay = 30f;
                chargeEffects = 4;
                recoilAmount = 2f;
                cooldown = 0.03f;
                targetAir = true;
                shootShake = 2f;

                powerUse = 7f;

                shootEffect = UnityFx.laserChargeShoot;
                smokeEffect = Fx.none;
                chargeEffect = UnityFx.laserCharge;
                chargeBeginEffect = UnityFx.laserChargeBegin;
                heatColor = Color.red;
                shootSound = Sounds.laser;

                shootType = UnityBullets.shardLaser;

                buildVisibility = BuildVisibility.sandboxOnly;
            }

            @Override
            public void init(){
                super.init();

                ExpBlock block = ExpMeta.map(this);
                block.hasExp = true;
                block.condConfig = true;
                block.enableUpgrade = true;

                block.maxLevel = 30;
                block.maxExp = block.requiredExp(block.maxLevel);

                block.addUpgrade(laserBranch, 15);
                block.addUpgrade(laserFractal, 15);
                block.addUpgrade(laserBreakthrough, 30);

                block.addField(ExpFieldType.linear, ReloadTurret.class, "reloadTime", reloadTime, -1f);

                block.setupFields();
                block.setStats();
                block.init();
            }
        };

        laserFrost = new ExpLiquidTurret("frost-laser-turret"){
            {
                requirements(Category.turret, with(Items.copper, 190, Items.silicon, 110, Items.titanium, 15));
                size = 2;
                health = 1000;

                range = 160f;
                reloadTime = 80f;
                targetAir = true;
                liquidCapacity = 15f;
                buildVisibility = BuildVisibility.sandboxOnly;

                ammo(Liquids.cryofluid, UnityBullets.frostLaser);
                consumes.powerCond(1f, TurretBuild::isActive);
            }

            @Override
            public void init(){
                super.init();

                ExpBlock block = ExpMeta.map(this);
                block.hasExp = true;
                block.condConfig = true;
                block.enableUpgrade = true;

                block.maxLevel = 30;
                block.maxExp = block.requiredExp(block.maxLevel);
                
                //block.addUpgrade(laserKelvin, 15);
                block.addUpgrade(laserBreakthrough, 30);

                block.setupFields();
                block.setStats();
                block.init();
            }
        };

        //TODO SK MAKE IDEAS NOW
        laserFractal = new ExpPowerTurret("fractal-laser-turret");

        laserBranch = new ExpPowerChargeTurret("swarm-laser-turret"){
            {
                requirements(Category.turret, with(Items.copper, 190, Items.silicon, 110, Items.titanium, 15));
                size = 3;
                health = 2400;
                
                reloadTime = 90f;
                coolantMultiplier = 2.25f;
                powerUse = 15f;
                targetAir = true;
                range = 150f;

                chargeTime = 50f;
                chargeMaxDelay = 30f;
                chargeEffects = 4;
                recoilAmount = 2f;
                cooldown = 0.03f;
                shootShake = 2f;
                shootEffect = UnityFx.laserChargeShoot;
                smokeEffect = Fx.none;
                chargeEffect = UnityFx.laserCharge;
                chargeBeginEffect = UnityFx.laserChargeBegin;
                heatColor = Color.red;
                fromColor = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f);
                shootSound = Sounds.laser;
                shootType = UnityBullets.branchLaser;
                buildVisibility = BuildVisibility.sandboxOnly;

                shootLength = size * Vars.tilesize / 2.7f;
                shots = 4;
                burstSpacing = 5f;
                inaccuracy = 10f;
                xRand = 6f;
                rangeInc = 0.25f * 8f;
            }

            @Override
            public void init(){
                super.init();

                ExpBlock block = ExpMeta.map(this);
                block.hasExp = true;
                block.condConfig = true;
                block.enableUpgrade = true;

                block.maxLevel = 30;
                block.maxExp = block.requiredExp(block.maxLevel);

                block.addField(ExpFieldType.linear, ReloadTurret.class, "reloadTime", reloadTime, -2f);
                block.addField(ExpFieldType.linear, BaseTurret.class, "range", range, 0.25f * 8f);

                block.setupFields();
                block.setStats();
                block.init();
            }
        };

        laserKelvin = new ExpLiquidTurret("kelvin-laser-turret"){
            {
                requirements(Category.turret, with(Items.copper, 190, Items.silicon, 110, Items.titanium, 15));
                size = 3;
                health = 2100;

                range = 180f;
                reloadTime = 120f;
                targetAir = true;
                liquidCapacity = 25f;

                buildVisibility = BuildVisibility.sandboxOnly;

                consumes.powerCond(2.5f, TurretBuild::isActive);
                ammo(Liquids.cryofluid, UnityBullets.kelvinLaser);
            }

            @Override
            public void init(){
                super.init();

                ExpBlock block = ExpMeta.map(this);
                block.hasExp = true;
                block.condConfig = true;
                block.enableUpgrade = true;

                block.maxLevel = 30;
                block.maxExp = block.requiredExp(block.maxLevel);

                block.setupFields();
                block.setStats();
                block.init();
            }
        };

        laserBreakthrough = new ExpPowerChargeTurret("bt-laser-turret");

        //endregion
        //region monolith

        monolithAlloyForge = new StemGenericSmelter("monolith-alloy-forge"){{
            requirements(Category.crafting, with(Items.lead, 380, UnityItems.monolite, 240, Items.silicon, 400, Items.titanium, 240, Items.thorium, 90, Items.surgeAlloy, 160));
            final int effectTimer = timers++;
            afterUpdate = e -> {
                if(e.consValid()) e.fdata = Mathf.lerpDelta(e.fdata, e.efficiency(), 0.02f);
                else e.fdata = Mathf.lerpDelta(e.fdata, 0f, 0.02f);
                float temp = e.fdata;
                if(!Mathf.zero(temp)){
                    if(e.timer.get(effectTimer, 45f)) UnityFx.effect.at(e.x, e.y, e.rotation, temp);
                    //TODO not exactly same with js ver?.
                    if(Mathf.chanceDelta(temp * 0.5f)) Lightning.create(e.team, Pal.lancerLaser, 1f, e.x, e.y, Mathf.randomSeed((int)Time.time + e.id, 360f), (int)(temp * 4f) + Mathf.random(3));
                }
            };
            outputItem = new ItemStack(UnityItems.monolithAlloy, 3);
            size = 4;
            flameColor = Pal.lancerLaser;
            ambientSound = Sounds.machine;
            ambientSoundVolume = 0.6f;
            consumes.power(3.6f);
            consumes.items(with(Items.silicon, 3, Items.graphite, 2, UnityItems.monolite, 2));
            consumes.liquid(Liquids.cryofluid, 0.1f);
        }};

        mage = new PowerTurret("mage"){{
            requirements(Category.turret, with(Items.lead, 75, Items.silicon, 50, UnityItems.monolite, 25));
            size = 2;
            health = 600;
            range = 120f;
            reloadTime = 48;
            shootCone = 15f;
            shots = 3;
            burstSpacing = 2f;
            shootSound = Sounds.spark;
            powerUse = 2.5f;
            recoilAmount = 2.5f;
            shootType = new LightningBulletType(){
                {
                    lightningLength = 20;
                    damage = 32f;
                }
            };
        }};

        oracle = new BurstPowerTurret("oracle"){{
            requirements(Category.turret, with(Items.silicon, 175, Items.titanium, 150, UnityItems.monolithAlloy, 75));
            size = 3;
            health = 1440;
            range = 180f;
            reloadTime = 72f;
            chargeTime = 30f;
            chargeMaxDelay = 4f;
            chargeEffects = 12;
            shootCone = 5f;
            shots = 8;
            burstSpacing = 2f;
            shootSound = Sounds.spark;
            shootShake = 3f;
            powerUse = 3f;
            recoilAmount = 2.5f;
            shootType = new LightningBulletType(){
                {
                    damage = 32f;
                    shootEffect = Fx.lightningShoot;
                }
            };
            chargeEffect = UnityFx.oracleChage;
            chargeBeginEffect = UnityFx.oracleChargeBegin;
            subShots = 3;
            subBurstSpacing = 1f;
            subShootEffect = Fx.hitLancer;
            subShootSound = Sounds.laser;
            subShootType = new LaserBulletType(64f){
                {
                    length = 180;
                    sideAngle = 45f;
                    inaccuracy = 8f;
                }
            };
        }};

        supernova = new AttractLaserTurret("supernova"){
            /** Temporary vector array to be used in the drawing method */
            final Vec2[] phases = new Vec2[]{new Vec2(), new Vec2(), new Vec2(), new Vec2(), new Vec2(), new Vec2()};

            final float starRadius;
            final float starOffset;
            Cons<AttractLaserTurretBuild> effectDrawer;

            final int timerChargeStar = timers++;
            final Effect starEffect;
            final Effect chargeEffect;
            final Effect chargeStarEffect;
            final Effect chargeStar2Effect;
            final Effect chargeBeginEffect;
            final Effect starDecayEffect;
            final Effect heatWaveEffect;

            {
                requirements(Category.turret, with(Items.copper, 1));
                size = 7;
                health = 8100;

                shootLength = size * tilesize / 2f - 8f;
                rotateSpeed = 1f;
                recoilAmount = 4f;
                powerUse = 24f;
                cooldown = 0.006f;

                shootCone = 15f;
                range = 250f;
                starRadius = 8f;
                starOffset = -2.25f;

                chargeSound = UnitySounds.supernovaCharge;
                chargeSoundVolume = 1f;
                shootSound = UnitySounds.supernovaShoot;
                loopSound = UnitySounds.supernovaActive;
                loopSoundVolume = 1f;

                baseExplosiveness = 25f;
                shootDuration = 480f;
                shootType = UnityBullets.supernovaLaser;

                starEffect = UnityFx.supernovaStar;
                chargeEffect = UnityFx.supernovaCharge;
                chargeStarEffect = UnityFx.supernovaChargeStar;
                chargeStar2Effect = UnityFx.supernovaChargeStar2;
                chargeBeginEffect = UnityFx.supernovaChargeBegin;
                starDecayEffect = UnityFx.supernovaStarDecay;
                heatWaveEffect = UnityFx.supernovaStarHeatwave;

                drawer = b -> {
                    if(b instanceof AttractLaserTurretBuild tile){
                        //core
                        phases[0].trns(tile.rotation, -tile.recoil + Mathf.curve(tile.phase, 0f, 0.3f) * -2f);
                        //left wing
                        phases[1].trns(tile.rotation - 90,
                            Mathf.curve(tile.phase, 0.2f, 0.5f) * -2f,

                            -tile.recoil + Mathf.curve(tile.phase, 0.2f, 0.5f) * 2f +
                            Mathf.curve(tile.phase, 0.5f, 0.8f) * 3f
                        );
                        //left bottom wing
                        phases[2].trns(tile.rotation - 90,
                            Mathf.curve(tile.phase, 0f, 0.3f) * -1.5f +
                            Mathf.curve(tile.phase, 0.6f, 1f) * -2f,

                            -tile.recoil + Mathf.curve(tile.phase, 0f, 0.3f) * 1.5f +
                            Mathf.curve(tile.phase, 0.6f, 1f) * -1f
                        );
                        //bottom
                        phases[3].trns(tile.rotation, -tile.recoil + Mathf.curve(tile.phase, 0f, 0.6f) * -4f);
                        //right wing
                        phases[4].trns(tile.rotation - 90,
                            Mathf.curve(tile.phase, 0.2f, 0.5f) * 2f,

                            -tile.recoil + Mathf.curve(tile.phase, 0.2f, 0.5f) * 2f +
                            Mathf.curve(tile.phase, 0.5f, 0.8f) * 3f
                        );
                        //right bottom wing
                        phases[5].trns(tile.rotation - 90,
                            Mathf.curve(tile.phase, 0f, 0.3f) * 1.5f +
                            Mathf.curve(tile.phase, 0.6f, 1f) * 2f,

                            -tile.recoil + Mathf.curve(tile.phase, 0f, 0.3f) * 1.5f +
                            Mathf.curve(tile.phase, 0.6f, 1f) * -1f
                        );

                        Draw.rect(Regions.supernovaWingLeftBottomOutlineRegion, tile.x + phases[2].x, tile.y + phases[2].y, tile.rotation - 90);
                        Draw.rect(Regions.supernovaWingRightBottomOutlineRegion, tile.x + phases[5].x, tile.y + phases[5].y, tile.rotation - 90);
                        Draw.rect(Regions.supernovaWingLeftOutlineRegion, tile.x + phases[1].x, tile.y + phases[1].y, tile.rotation - 90);
                        Draw.rect(Regions.supernovaWingRightOutlineRegion, tile.x + phases[4].x, tile.y + phases[4].y, tile.rotation - 90);
                        Draw.rect(Regions.supernovaBottomOutlineRegion, tile.x + phases[3].x, tile.y + phases[3].y, tile.rotation - 90);
                        Draw.rect(Regions.supernovaHeadOutlineRegion, tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90);
                        Draw.rect(Regions.supernovaCoreOutlineRegion, tile.x + phases[0].x, tile.y + phases[0].y, tile.rotation - 90);

                        Draw.rect(Regions.supernovaWingLeftBottomRegion, tile.x + phases[2].x, tile.y + phases[2].y, tile.rotation - 90);
                        Draw.rect(Regions.supernovaWingRightBottomRegion, tile.x + phases[5].x, tile.y + phases[5].y, tile.rotation - 90);
                        Draw.rect(Regions.supernovaWingLeftRegion, tile.x + phases[1].x, tile.y + phases[1].y, tile.rotation - 90);
                        Draw.rect(Regions.supernovaWingRightRegion, tile.x + phases[4].x, tile.y + phases[4].y, tile.rotation - 90);
                        Draw.rect(Regions.supernovaBottomRegion, tile.x + phases[3].x, tile.y + phases[3].y, tile.rotation - 90);
                        Draw.rect(Regions.supernovaHeadRegion, tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90);

                        float z = Draw.z();
                        Draw.z(z + 0.001f);

                        Draw.rect(Regions.supernovaCoreRegion, tile.x + phases[0].x, tile.y + phases[0].y, tile.rotation - 90);
                        Draw.z(z);

                        effectDrawer.get(tile);
                    }else{
                        throw new IllegalStateException("building isn't an instance of AttractLaserTurretBuild");
                    }
                };

                heatDrawer = tile -> {
                    if(tile.heat <= 0.00001f) return;

                    float r = Utils.pow6In.apply(tile.heat);
                    float g = Interp.pow3In.apply(tile.heat);
                    float b = Interp.pow2Out.apply(tile.heat);
                    float a = Interp.pow2In.apply(tile.heat);

                    Draw.color(Tmp.c1.set(r, g, b, a));
                    Draw.blend(Blending.additive);

                    Draw.rect(heatRegion, tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90f);

                    Draw.color();
                    Draw.blend();
                };

                effectDrawer = tile -> {
                    boolean notShooting = tile.bulletLife() <= 0f || tile.bullet() == null;
                    float ch = notShooting ? tile.charge : 1f;
                    Tmp.v1.trns(tile.rotation, -tile.recoil + starOffset + Mathf.curve(tile.phase, 0f, 0.3f) * -2f);

                    Draw.color(Pal.lancerLaser);
                    Fill.circle(
                        tile.x + Tmp.v1.x,
                        tile.y + Tmp.v1.y,
                        ch * starRadius
                    );

                    if(notShooting){
                        Fill.circle(
                            tile.x + Tmp.v1.x,
                            tile.y + Tmp.v1.y,
                            tile.charge * starRadius * 0.67f
                        );
                    }

                    if(!state.isPaused()){
                        float a = Mathf.random(360f);
                        float d = 0.3f;

                        starEffect.at(
                            tile.x + Tmp.v1.x + Angles.trnsx(a, d),
                            tile.y + Tmp.v1.y + Angles.trnsy(a, d),
                            tile.rotation, Float.valueOf(ch * starRadius)
                        );
                        chargeEffect.at(
                            tile.x + Angles.trnsx(tile.rotation, -tile.recoil + shootLength),
                            tile.y + Angles.trnsy(tile.rotation, -tile.recoil + shootLength),
                            tile.rotation, Float.valueOf(tile.charge * starRadius * 0.67f)
                        );

                        if(notShooting){
                            if(tile.charge > 0.1f && tile.timer(timerChargeStar, 20f)){
                                chargeStarEffect.at(
                                    tile.x + Tmp.v1.x,
                                    tile.y + Tmp.v1.y,
                                    tile.rotation, Float.valueOf(tile.charge)
                                );
                            }

                            if(Mathf.chanceDelta(tile.charge)){
                                chargeBeginEffect.at(
                                    tile.x + Angles.trnsx(tile.rotation, -tile.recoil + shootLength),
                                    tile.y + Angles.trnsy(tile.rotation, -tile.recoil + shootLength),
                                    tile.rotation, Float.valueOf(tile.charge)
                                );

                                chargeStar2Effect.at(
                                    tile.x + Tmp.v1.x,
                                    tile.y + Tmp.v1.y,
                                    tile.rotation, Float.valueOf(tile.charge)
                                );
                            }
                        }else{
                            starDecayEffect.at(
                                tile.x + Tmp.v1.x,
                                tile.y + Tmp.v1.y,
                                tile.rotation
                            );

                            if(tile.timer(timerChargeStar, 20f)){
                                heatWaveEffect.at(
                                    tile.x + Tmp.v1.x,
                                    tile.y + Tmp.v1.y,
                                    tile.rotation
                                );
                            }
                        }
                    }
                };
            }
        };

        /*monolithGroundFactory = new UnitFactory("monolith-ground-factory"){{
            requirements(Category.units, with());
            size = 3;
            plans = Seq.with(new UnitPlan(UnityUnitTypes.stele, 900f, with(Items.silicon, 10, UnityItems.monolite, 15)));
            consumes.power(1.2f);
        }};*/

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

        heatPipe = new HeatPipe("heat-pipe"){{
            requirements(Category.power, with(Items.copper, 15, UnityItems.cupronickel, 10, UnityItems.nickel, 5));
            health = 140;
            addGraph(new GraphHeat(5f, 0.7f, 0.008f).setAccept(1, 1, 1, 1));
        }};

        smallRadiator = new GraphBlock("small-radiator"){{
            requirements(Category.power, with(Items.copper, 30, UnityItems.cupronickel, 20, UnityItems.nickel, 15));
            health = 200;
            solid = true;
            addGraph(new GraphHeat(10f, 0.7f, 0.05f).setAccept(1, 1, 1, 1));
        }};

        driveShaft = new DriveShaft("drive-shaft"){{
            requirements(Category.power, with(Items.copper, 10, Items.lead, 10));
            health = 150;
            addGraph(new GraphTorque(0.01f, 3f).setAccept(1, 0, 1, 0));
        }};

        inlineGearbox = new InlineGearbox("inline-gearbox"){{
            requirements(Category.power, with(Items.titanium, 20, Items.lead, 30, Items.copper, 30));
            size = 2;
            health = 700;
            addGraph(new GraphTorque(0.02f, 20f).setAccept(1, 1, 0, 0, 1, 1, 0, 0));
        }};

        shaftRouter = new GraphBlock("shaft-router"){{
            requirements(Category.power, with(Items.copper, 20, Items.lead, 20));
            health = 100;
            preserveDraw = true;
            addGraph(new GraphTorque(0.05f, 5f).setAccept(1, 1, 1, 1));
        }};

        simpleTransmission = new SimpleTransmission("simple-transmission"){{
            requirements(Category.power, with(Items.titanium, 50, Items.lead, 50, Items.copper, 50));
            size = 2;
            health = 500;
            addGraph(new GraphTorqueTrans(0.05f, 25f).setRatio(1f, 2.5f).setAccept(2, 1, 0, 0, 1, 2, 0, 0));
        }};

        cruciblePump = new CruciblePump("crucible-pump"){{
            requirements(Category.crafting, with(UnityItems.cupronickel, 50, UnityItems.nickel, 50, Items.metaglass, 15));
            size = 2;
            health = 500;
            consumes.power(1f);
            addGraph(new GraphCrucible(10f, false).setAccept(1, 1, 0, 0, 2, 2, 0, 0).multi());
            addGraph(new GraphHeat(50f, 0.1f, 0.003f).setAccept(1, 1, 1, 1, 1, 1, 1, 1));
        }};

        mechanicalConveyor = new ShadowedConveyor("mechanical-conveyor"){{
            requirements(Category.distribution, with(Items.copper, 3, UnityItems.nickel, 2));
            health = 250;
            speed = 0.1f;
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
            consumes.power(1.6f);
            addGraph(new GraphFlux(25f).setAccept(1, 1, 0, 0, 0, 0, 0, 0));
        }};

        neodymiumStator = new Magnet("neodymium-stator"){{
            requirements(Category.power, BuildVisibility.sandboxOnly, with());
            health = 400;
            addGraph(new GraphFlux(200f).setAccept(1, 0, 0, 0));
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
            consumes.power(16f);
            addGraph(new GraphFlux(false).setAccept(0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1));
            addGraph(new GraphTorque(0.05f, 150f).setAccept(0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0));
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
            consumes.power(1f);
            addGraph(new GraphFlux(false).setAccept(0, 1, 0, 1));
            addGraph(new GraphTorque(0.08f, 20f).setAccept(1, 0, 1, 0));
        }};

        torqueInfi = new TorqueGenerator("torque-infi"){{
            requirements(Category.power, BuildVisibility.sandboxOnly, with());
            health = 200;
            preserveDraw = true;
            rotate = false;
            addGraph(new GraphTorqueGenerate(0.001f, 1f, 999999f, 9999f).setAccept(1, 1, 1, 1));
        }};

        handCrank = new HandCrank("hand-crank"){{
            requirements(Category.power, with(UnityItems.nickel, 5, Items.lead, 20));
            health = 120;
            addGraph(new GraphTorque(0.01f, 3f).setAccept(1, 0, 0, 0));
        }};

        windTurbine = new WindTurbin("wind-turbine"){{
            requirements(Category.power, with(Items.titanium, 20, Items.lead, 80, Items.copper, 70));
            size = 3;
            health = 1200;
            addGraph(new GraphTorqueGenerate(0.03f, 20f, 5f, 5f).setAccept(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }};

        waterTurbine = new WaterTurbin("water-turbine"){{
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
            consumes.power(4.5f);
            addGraph(new GraphTorqueGenerate(0.1f, 25f, 10f, 16f).setAccept(0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0));
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
            consumes.item(Items.sporePod, 1);
            addGraph(new GraphHeat(60f, 0.4f, 0.008f).setAccept(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
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

        chopper = new Chopper("chopper"){{
            requirements(Category.turret, with(UnityItems.nickel, 50, Items.titanium, 50, Items.lead, 30));
            health = 650;
            setGridW(7);
            setGridH(1);
            addPart(
                "Pivot", "", PartType.blade, 4, 0, 1, 1, true, true,
                new Point2(0, 0), new ItemStack[0], new byte[]{1, 0, 0, 0}, new byte[]{0, 0, 0, 0},
                new PartStat(PartStatType.mass, 1), new PartStat(PartStatType.collides, false), new PartStat(PartStatType.hp, 10)
            );
            addPart(
                "Blade", "Slices and knocks back enemies", PartType.blade, 0, 0, 1, 1,
                with(UnityItems.nickel, 3, Items.titanium, 5), new byte[]{1, 0, 0, 0}, new byte[]{0, 0, 1, 0},
                new PartStat(PartStatType.mass, 2), new PartStat(PartStatType.collides, true), new PartStat(PartStatType.hp, 80), new PartStat(PartStatType.damage, 5)
            );
            addPart(
                "Serrated blade", "A heavy reinforced blade.", PartType.blade, 2, 0, 2, 1,
                with(UnityItems.nickel, 8, Items.lead, 5), new byte[]{1, 0, 0, 0, 0, 0}, new byte[]{0, 0, 0, 1, 0, 0},
                new PartStat(PartStatType.mass, 6), new PartStat(PartStatType.collides, true), new PartStat(PartStatType.hp, 120), new PartStat(PartStatType.damage, 12)
            );
            addPart(
                "Rod", "Supporting structure, does not collide", PartType.blade, 1, 0, 1, 1,
                with(Items.titanium, 3), new byte[]{1, 0, 0, 0}, new byte[]{0, 0, 1, 0},
                new PartStat(PartStatType.mass, 1), new PartStat(PartStatType.collides, false), new PartStat(PartStatType.hp, 40)
            );
            addGraph(new GraphTorque(0.03f, 5f).setAccept(1, 0, 0, 0));
        }};

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

        //endregion
        //region advance

        celsius = new PowerTurret("celsius"){{
            requirements(Category.turret, with(Items.silicon, 20, UnityItems.xenium, 15, Items.titanium, 30, UnityItems.advanceAlloy, 25));
            health = 780;
            size = 1;
            reloadTime = 3f;
            range = 47f;
            shootCone = 50f;
            heatColor = Color.valueOf("ccffff");
            ammoUseEffect = Fx.none;
            inaccuracy = 9.2f;
            rotateSpeed = 7.5f;
            shots = 2;
            recoilAmount = 1f;
            powerUse = 13.9f;
            hasPower = true;
            targetAir = true;
            shootSound = Sounds.flame;
            cooldown = 0.01f;
            shootType = UnityBullets.celsiusSmoke;
        }};

        kelvin = new PowerTurret("kelvin"){{
            requirements(Category.turret, with(Items.silicon, 80, UnityItems.xenium, 35, Items.titanium, 90, UnityItems.advanceAlloy, 50));
            health = 2680;
            size = 2;
            reloadTime = 3f;
            range = 100f;
            shootCone = 50f;
            heatColor = Color.valueOf("ccffff");
            ammoUseEffect = Fx.none;
            inaccuracy = 9.2f;
            rotateSpeed = 6.5f;
            shots = 2;
            spread = 6f;
            recoilAmount = 1f;
            powerUse = 13.9f;
            hasPower = true;
            targetAir = true;
            shootSound = Sounds.flame;
            cooldown = 0.01f;
            shootType = UnityBullets.kelvinSmoke;
        }};

        xenoCorruptor = new LaserTurret("xeno-corruptor"){{
                requirements(Category.turret, with(Items.lead, 640, Items.graphite, 740, Items.titanium, 560, Items.surgeAlloy, 650, Items.silicon, 720, Items.thorium, 400, UnityItems.xenium, 340, UnityItems.advanceAlloy, 640));
                health = 7900;
                size = 7;
                reloadTime = 230f;
                range = 290f;
                coolantMultiplier = 1.4f;
                shootCone = 40f;
                shootDuration = 310f;
                firingMoveFract = 0.16f;
                powerUse = 45f;
                shootShake = 3f;
                recoilAmount = 8f;
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

                consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.4f && liquid.flammability < 0.1f, 2.1f)).update(false);
            }

            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
            }
        };

        cube = new ObjPowerTurret("the-cube"){{
            requirements(Category.turret, with(Items.copper, 3300, Items.lead, 2900, Items.graphite, 4400, Items.silicon, 3800, Items.titanium, 4600, UnityItems.xenium, 2300, Items.phaseFabric, 670, UnityItems.advanceAlloy, 1070));
            health = 22500;
            object = UnityObjs.cube;
            size = 10;
            range = 320f;
            reloadTime = 240f;
            powerUse = 260f;
            coolantMultiplier = 1.1f;
            shootSound = UnitySounds.cubeBlast;
            shootType = new PointBlastLaserBulletType(580f){{
                length = 320f;
                lifetime = 17f;
                pierce = true;
                auraDamage = 8000f;
                damageRadius = 120f;
                laserColor = UnityPal.advance;
            }};
        }};

        //endregion
        //region end

        terminalCrucible = new StemGenericSmelter("terminal-crucible"){{
            requirements(Category.crafting, with(Items.lead, 810, Items.graphite, 720, Items.silicon, 520, Items.phaseFabric, 430, Items.surgeAlloy, 320, UnityItems.plagueAlloy, 120, UnityItems.darkAlloy, 120, UnityItems.lightAlloy, 120, UnityItems.advanceAlloy, 120, UnityItems.monolithAlloy, 120, UnityItems.sparkAlloy, 120, UnityItems.superAlloy, 120));
            flameColor = UnityPal.scarColor;
            addSprites("lights");
            preserveDraw = false;
            afterDrawer = e -> {
                drawer.draw(e);
                if(e.warmup > 0f){
                    Draw.blend(Blending.additive);
                    Draw.color(1f, Mathf.absin(5f, 0.5f) + 0.5f, Mathf.absin(Time.time + 90f * Mathf.radDeg, 5f, 0.5f) + 0.5f, e.warmup);
                    Draw.rect(regions.get("lights"), e.x, e.y);
                    float b = (Mathf.absin(8f, 0.25f) + 0.75f) * e.warmup;
                    Draw.color(1f, b, b, b);
                    Draw.rect(topRegion, e.x, e.y);
                    Draw.blend();
                    Draw.color();
                }
            };
            outputItem = new ItemStack(UnityItems.terminum, 1);
            size = 6;
            craftTime = 310f;
            ambientSound = Sounds.respawning;
            ambientSoundVolume = 0.6f;
            consumes.power(45.2f);
            consumes.items(with(UnityItems.plagueAlloy, 3, UnityItems.darkAlloy, 3, UnityItems.lightAlloy, 3, UnityItems.advanceAlloy, 3, UnityItems.monolithAlloy, 3, UnityItems.sparkAlloy, 3, UnityItems.superAlloy, 3));
        }};

        endForge = new StemGenericSmelter("end-forge"){{
            requirements(Category.crafting, with(Items.silicon, 2300, Items.phaseFabric, 650, Items.surgeAlloy, 1350, UnityItems.plagueAlloy, 510, UnityItems.darkAlloy, 510, UnityItems.lightAlloy, 510, UnityItems.advanceAlloy, 510, UnityItems.monolithAlloy, 510, UnityItems.sparkAlloy, 510, UnityItems.superAlloy, 510, UnityItems.terminationFragment, 230));
            outputItem = new ItemStack(UnityItems.terminaAlloy, 2);
            size = 8;
            craftTime = 410f;
            ambientSoundVolume = 0.6f;
            addSprites("lights", "top-small");
            foreUpdate = e -> {
                if(e.consValid() && Mathf.chanceDelta(0.7f * e.warmup)) UnityFx.forgeAbsorbEffect.at(e.x, e.y, Mathf.random(360f));
            };
            preserveDraw = false;
            afterDrawer = e -> {
                drawer.draw(e);
                if(e.warmup <= 0.0001f) return;
                Draw.blend(Blending.additive);
                Draw.color(1f, Mathf.absin(5f, 0.5f) + 0.5f, Mathf.absin(Time.time + 90f * Mathf.radDeg, 5f, 0.5f) + 0.5f, e.warmup);
                Draw.rect(regions.get("lights"), e.x, e.y);
                float b = (Mathf.absin(8f, 0.25f) + 0.75f) * e.warmup;
                Draw.color(1f, b, b, b);
                Draw.rect(topRegion, e.x, e.y);
                for(int i = 0; i < 4; i++){
                    float ang = i * 90f;
                    for(int s = 0; s < 2; s++){
                        float offset = 360f / 8f * (i * 2 + s);
                        TextureRegion reg = regions.get("top-small");
                        int sign = Mathf.signs[s];
                        float colA = (Mathf.absin(Time.time + offset * Mathf.radDeg, 8f, 0.25f) + 0.75f) * e.warmup;
                        float colB = (Mathf.absin(Time.time + (90f + offset) * Mathf.radDeg, 8f, 0.25f) + 0.75f) * e.warmup;
                        Draw.color(1, colA, colB, e.warmup);
                        Draw.rect(reg, e.x, e.y, reg.width * sign * Draw.scl, reg.height * Draw.scl, -ang);
                    }
                }
                Draw.blend();
                Draw.color();
            };
            consumes.power(86.7f);
            consumes.items(with(UnityItems.terminum, 3, UnityItems.darkAlloy, 5, UnityItems.lightAlloy, 5));
        }};

        endGame = new EndGameTurret("endgame"){{
            requirements(Category.turret, with(Items.phaseFabric, 9500, Items.surgeAlloy, 10500,
                UnityItems.darkAlloy, 2300, UnityItems.lightAlloy, 2300, UnityItems.advanceAlloy, 2300,
                UnityItems.plagueAlloy, 2300, UnityItems.sparkAlloy, 2300, UnityItems.monolithAlloy, 2300,
                UnityItems.superAlloy, 2300, UnityItems.terminum, 1600, UnityItems.terminaAlloy, 800
            ));

            shootCone = 360f;
            reloadTime = 430f;
            range = 820f;
            size = 14;
            coolantMultiplier = 0.6f;
            hasItems = true;
            itemCapacity = 10;

            shootType = new BulletType(){{
                //damage = Float.MAX_VALUE;
                damage = (float)Double.MAX_VALUE;
            }};
            consumes.item(UnityItems.terminum, 2);
        }};

        //endregion
    }
}

package unity.content.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
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
import unity.content.*;
import unity.content.effects.*;
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
import unity.world.blocks.light.*;
import unity.world.blocks.power.*;
import unity.world.blocks.production.*;
import unity.world.blocks.sandbox.*;
import unity.world.blocks.units.*;
import unity.world.consumers.*;
import unity.world.draw.*;
import unity.world.graphs.*;
import unity.world.meta.*;
import younggamExperimental.*;
import younggamExperimental.blocks.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.type.ItemStack.*;

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

    //---------- koruh faction ----------
    public static @FactionDef("koruh") Block
    //crafting
    solidifier, steelSmelter, liquifier,

    //defense
    stoneWall, denseWall, steelWall, steelWallLarge, diriumWall, diriumWallLarge,

    //distribution
    steelConveyor, diriumConveyor, teleporter, teleunit,

    //unit
    bufferPad, omegaPad, cachePad, convertPad;

    //TODO
    //expOutput, expUnloader, expTank, expChest, expFountain, expVoid;

    //turret
    public static @FactionDef("koruh")
    @LoadRegs("bt-laser-turret-top")
    @Merge(base = PowerTurret.class, value = {Turretc.class, Expc.class})
    Block laser, laserCharge, laserBranch, laserFractal, laserBreakthrough;

    public static @FactionDef("koruh")
    @Merge(base = LiquidTurret.class, value = {Turretc.class, Expc.class})
    Block laserFrost, laserKelvin;

    public static @FactionDef("koruh")
    @Merge(base = ItemTurret.class, value = {Turretc.class, Expc.class})
    Block inferno;

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
    @Merge(base = BurstPowerTurret.class, value = {Turretc.class, Soulc.class})
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

    private static final ContentList[] factionBlocks = {
        new DarkBlocks(),
        new LightBlocks(),
        new ImberBlocks()
    };

    public static void load(){
        //region global

        distributionDrill = new DistributionDrill("distribution-drill"){{
            requirements(Category.production, with(Items.copper, 20, Items.silicon, 15, Items.titanium, 20));
            tier = 3;
            drillTime = 450;
            size = 2;

            consumes.liquid(Liquids.water, 0.06f).boost();
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

                new UnitType[]{UnityUnitTypes.monument, UnityUnitTypes.colossus}
            );
            otherUpgrades.add(
                new UnitType[]{UnityUnitTypes.citadel, UnityUnitTypes.empire},

                new UnitType[]{UnityUnitTypes.araneidae, UnityUnitTypes.theraphosidae},

                new UnitType[]{UnityUnitTypes.colossus, UnityUnitTypes.bastion}
            );
            consumes.power(5f);
            consumes.items(with(Items.silicon, 1200, Items.metaglass, 800, Items.thorium, 700, Items.surgeAlloy, 400, Items.plastanium, 600, Items.phaseFabric, 350));
            consumes.liquid(Liquids.cryofluid, 7f);
        }};

        irradiator = new Press("irradiator"){{
            requirements(Category.crafting, with(Items.lead, 120, Items.silicon, 80, Items.titanium, 30));
            outputItem = new ItemStack(UnityItems.irradiantSurge, 3);
            size = 3;
            movementSize = 29f;
            fxYVariation = 25f / tilesize;
            craftTime = 50f;
            consumes.power(1.2f);
            consumes.items(with(Items.thorium, 5, Items.titanium, 5, Items.surgeAlloy, 1));
        }};

        superCharger = new Reinforcer("supercharger"){{
            requirements(Category.effect, with(Items.titanium, 60, Items.lead, 20, Items.silicon, 30));
            size = 2;
            itemCapacity = 15;
            laserColor = Items.surgeAlloy.color;
            consumes.power(0.4f);
            consumes.items(with(UnityItems.irradiantSurge, 10));
        }};

        oreNickel = new UnityOreBlock(UnityItems.nickel){{
            oreScale = 24.77f;
            oreThreshold = 0.913f;
            oreDefault = false;
        }};

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
            outputItem = new ItemStack(UnityItems.stone, 1);

            consumes.add(new ConsumeLiquids(new LiquidStack[]{new LiquidStack(UnityLiquids.lava, 0.1f), new LiquidStack(Liquids.water, 0.1f)}));

            drawer = new DrawGlow(){
                @Override
                public void draw(GenericCrafterBuild build){
                    Draw.rect(build.block.region, build.x, build.y);
                    Draw.color(liquids[0].color, build.liquids.get(liquids[0]) / liquidCapacity);
                    Draw.rect(top, build.x, build.y);
                    Draw.reset();
                }
            };
        }};

        steelSmelter = new GenericCrafter("steel-smelter"){{
            requirements(Category.crafting, with(Items.lead, 45, Items.silicon, 20, UnityItems.denseAlloy, 30));
            health = 140;
            itemCapacity = 10;
            craftEffect = UnityFx.craftFx;
            craftTime = 300f;
            outputItem = new ItemStack(UnityItems.steel, 1);

            consumes.power(2f);
            consumes.items(with(Items.coal, 2, Items.graphite, 2, UnityItems.denseAlloy, 3));

            drawer = new DrawGlow(){
                @Override
                public void draw(GenericCrafterBuild build){
                    Draw.rect(build.block.region, build.x, build.y);
                    Draw.color(1f, 1f, 1f, build.warmup * Mathf.absin(8f, 0.6f));
                    Draw.rect(top, build.x, build.y);
                    Draw.reset();
                }
            };
        }};

        liquifier = new BurnerSmelter("liquifier"){{
            requirements(Category.crafting, with(Items.titanium, 30, Items.silicon, 15, UnityItems.steel, 10));
            health = 100;
            hasLiquids = true;
            updateEffect = Fx.fuelburn;
            craftTime = 30f;
            outputLiquid = new LiquidStack(UnityLiquids.lava, 0.1f);

            configClear(b -> Fires.create(b.tile));
            consumes.power(3.7f);

            update((BurnerSmelterBuild e) -> {
                if(e.progress == 0f && e.warmup > 0.001f && (Vars.net.server() || !Vars.net.active()) && Mathf.chanceDelta(0.2f)){
                    e.configureAny(null);
                }
            });

            drawer = new DrawGlow(){
                @Override
                public void draw(GenericCrafterBuild build){
                    Draw.rect(build.block.region, build.x, build.y);

                    Liquid liquid = outputLiquid.liquid;
                    Draw.color(liquid.color, build.liquids.get(liquid) / liquidCapacity);
                    Draw.rect(top, build.x, build.y);
                    Draw.color();

                    Draw.reset();
                }
            };
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

        bufferPad = new MechPad("buffer-pad"){{
            requirements(Category.units, with(UnityItems.stone, 120, Items.copper, 170, Items.lead, 150, Items.titanium, 150, Items.silicon, 180));
            size = 2;
            craftTime = 100;
            consumes.power(0.7f);
            unitType = UnityUnitTypes.buffer;
        }};

        omegaPad = new MechPad("omega-pad"){{
            requirements(Category.units, with(UnityItems.stone, 220, Items.lead, 200, Items.silicon, 230, Items.thorium, 260, Items.surgeAlloy, 100));
            size = 3;
            craftTime = 300f;
            consumes.power(1.2f);
            unitType = UnityUnitTypes.omega;
        }};

        cachePad = new MechPad("cache-pad"){{
            requirements(Category.units, with(UnityItems.stone, 150, Items.lead, 160, Items.silicon, 100, Items.titanium, 60, Items.plastanium, 120, Items.phaseFabric, 60));
            size = 2;
            craftTime = 130f;
            consumes.power(0.8f);
            unitType = UnityUnitTypes.cache;
        }};

        convertPad = new ConversionPad("conversion-pad"){{
            requirements(Category.units, BuildVisibility.sandboxOnly, empty);
            size = 2;
            craftTime = 60f;
            consumes.power(1f);
            upgrades.add(
                new UnitType[]{UnitTypes.dagger, UnitTypes.mace},
                new UnitType[]{UnitTypes.flare, UnitTypes.horizon},
                new UnitType[]{UnityUnitTypes.cache, UnityUnitTypes.dijkstra},
                new UnitType[]{UnityUnitTypes.omega, UnitTypes.reign}
            );
        }};

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

        laser = new ExpTurretPowerTurret("laser-turret"){
            {
                requirements(Category.turret, with(Items.copper, 190, Items.silicon, 110, Items.titanium, 15));
                size = 2;
                health = 800;

                reloadTime = 35f;
                coolantMultiplier = 2f;
                range = 140f;
                targetAir = false;
                shootSound = Sounds.laser;

                powerUse = 7f;
                shootType = UnityBullets.laser;

                hasExp = true;
                condConfig = true;
                enableUpgrade = true;

                maxLevel = 10;
                progression.linear(reloadTime, -2f, val -> reloadTime = val);
                progression.bool(targetAir, 5f, val -> targetAir = val);
            }

            @Override
            public void setUpgrades(){
                addUpgrade(laserCharge, 10, false);
                addUpgrade(laserFrost, 10, false);
            }
        };

        laserCharge = new ExpTurretPowerTurret("charge-laser-turret"){
            {
                category = Category.turret;
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

                shootEffect = ShootFx.laserChargeShoot;
                smokeEffect = Fx.none;
                chargeEffect = UnityFx.laserCharge;
                chargeBeginEffect = UnityFx.laserChargeBegin;
                heatColor = Color.red;
                shootSound = Sounds.laser;

                shootType = UnityBullets.shardLaser;
                buildVisibility = BuildVisibility.sandboxOnly;

                hasExp = true;
                condConfig = true;
                enableUpgrade = true;

                maxLevel = 30;

                progression.linear(reloadTime, -1f, val -> reloadTime = val);
            }

            @Override
            public void setUpgrades(){
                addUpgrade(laserBranch, 15, false);
                addUpgrade(laserFractal, 15, false);
                addUpgrade(laserBreakthrough, 30, true);
            }
        };

        laserFrost = new ExpTurretLiquidTurret("frost-laser-turret"){
            {
                ammo(Liquids.cryofluid, UnityBullets.frostLaser);

                category = Category.turret;
                size = 2;
                health = 1000;

                range = 160f;
                reloadTime = 80f;
                targetAir = true;
                liquidCapacity = 10f;
                buildVisibility = BuildVisibility.sandboxOnly;

                hasExp = true;
                condConfig = true;
                enableUpgrade = true;

                maxLevel = 30;

                consumes.powerCond(1f, TurretBuild::isActive);
            }

            @Override
            public void setUpgrades(){
                addUpgrade(laserKelvin, 15, false);
                addUpgrade(laserBreakthrough, 30, true);
            }
        };

        laserFractal = new ExpTurretPowerTurret("fractal-laser-turret"){
            {
                category = Category.turret;
                size = 3;
                health = 2000;

                reloadTime = UnityBullets.distField.lifetime / 3f;
                coolantMultiplier = 2f;
                range = 140f;

                chargeTime = 50f;
                chargeMaxDelay = 40f;
                chargeEffects = 5;
                recoilAmount = 4f;

                cooldown = 0.03f;
                targetAir = true;
                shootShake = 5f;
                powerUse = 13f;

                shootEffect = ShootFx.laserChargeShoot;
                smokeEffect = Fx.none;
                chargeEffect = UnityFx.laserCharge;
                chargeBeginEffect = UnityFx.laserChargeBegin;
                heatColor = Color.red;
                shootSound = Sounds.laser;

                lerpColor = true;
                fromColor = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f);
                toColor = Pal.place;

                shootType = UnityBullets.fractalLaser;

                basicFieldRadius = 85f;

                hasExp = true;
                condConfig = true;
                enableUpgrade = true;

                maxLevel = 30;

                progression.linear(reloadTime, -2f, val -> reloadTime = val);
                progression.linear(range, 0.25f * tilesize, val -> range = val);
                progression.linear(basicFieldRadius, 0.2f * tilesize, val -> basicFieldRadius = val);

                bulletCons((ExpLaserFieldBulletType type, Bullet b) -> type.basicFieldRadius = basicFieldRadius);
            }
        };

        laserBranch = new ExpTurretPowerTurret("swarm-laser-turret"){
            {
                category = Category.turret;

                size = 3;
                health = 2400;

                reloadTime = 90f;
                coolantMultiplier = 2.25f;
                powerUse = 15f;
                targetAir = true;
                range = 150f;
                rangeColor = UnityPal.expColor;

                chargeTime = 50f;
                chargeMaxDelay = 30f;
                chargeEffects = 4;
                recoilAmount = 2f;

                cooldown = 0.03f;
                shootShake = 2f;
                shootEffect = ShootFx.laserChargeShoot;
                smokeEffect = Fx.none;
                chargeEffect = UnityFx.laserCharge;
                chargeBeginEffect = UnityFx.laserChargeBegin;
                heatColor = Color.red;
                lerpColor = true;
                fromColor = Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f);
                shootSound = Sounds.laser;
                shootType = UnityBullets.branchLaser;
                buildVisibility = BuildVisibility.sandboxOnly;

                shootLength = size * tilesize / 2.7f;
                shots = 4;
                burstSpacing = 5f;
                inaccuracy = 10f;
                xRand = 6f;

                hasExp = true;
                condConfig = true;
                enableUpgrade = true;

                maxLevel = 30;

                progression.linear(reloadTime, -2f, val -> reloadTime = val);
                progression.linear(range, 0.25f * tilesize, val -> range = val);
            }
        };

        laserKelvin = new ExpTurretLiquidTurret("kelvin-laser-turret"){
            {
                ammo(
                    Liquids.water, UnityBullets.kelvinWaterLaser,
                    Liquids.slag, UnityBullets.kelvinSlagLaser,
                    Liquids.oil, UnityBullets.kelvinOilLaser,
                    Liquids.cryofluid, UnityBullets.kelvinCryofluidLaser
                );

                category = Category.turret;
                size = 3;
                health = 2100;

                range = 180f;
                reloadTime = 120f;
                targetAir = true;
                liquidCapacity = 15f;

                omni = true;
                defaultBullet = UnityBullets.kelvinLiquidLaser;

                buildVisibility = BuildVisibility.sandboxOnly;

                consumes.powerCond(2.5f, TurretBuild::isActive);

                hasExp = true;
                condConfig = true;
                enableUpgrade = true;

                maxLevel = 30;
            }
        };

        laserBreakthrough = new ExpTurretPowerTurret("bt-laser-turret"){
            {
                category = Category.turret;
                size = 4;
                health = 2800;

                range = 500f;
                coolantMultiplier = 1.5f;
                targetAir = true;
                reloadTime = 500f;

                chargeTime = 100f;
                chargeMaxDelay = 100f;
                chargeEffects = 0;

                recoilAmount = 5f;
                cooldown = 0.03f;
                powerUse = 17f;

                shootShake = 4f;
                shootEffect = ShootFx.laserBreakthroughShoot;
                smokeEffect = Fx.none;
                chargeEffect = Fx.none;
                chargeBeginEffect = UnityFx.laserBreakthroughChargeBegin;

                heatColor = Pal.lancerLaser;
                lerpColor = true;
                toColor = UnityPal.expColor;
                shootSound = Sounds.laserblast;
                chargeSound = Sounds.lasercharge;
                shootType = UnityBullets.breakthroughLaser;
                buildVisibility = BuildVisibility.sandboxOnly;

                hasExp = true;
                condConfig = true;
                enableUpgrade = true;

                maxLevel = 1;
                orbMultiplier = 0.07f;

                progression.list(new Color[]{fromColor, toColor}, 1f, Interp.linear, val -> heatColor = val);

                drawer = b -> {
                    if(b instanceof ExpTurretPowerTurretBuild tile){
                        Draw.rect(region, tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90f);
                        if(tile.level() >= tile.maxLevel()){
                            //Draw.blend(Blending.additive);
                            Draw.color(tile.getShootColor(tile.levelf()));
                            Draw.alpha(Mathf.absin(Time.time, 20f, 0.6f));
                            Draw.rect(Regions.btLaserTurretTopRegion, tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90f);
                            Draw.color();
                            //Draw.blend();
                        }
                    }else{
                        throw new IllegalStateException("building isn't an instance of ExpTurretPowerTurretBuild");
                    }
                };
            }
        };

        inferno = new ExpTurretItemTurret("inferno"){
            {
                requirements(Category.turret, with(Items.copper, 150, Items.lead, 165, Items.graphite, 60));
                ammo(
                    Items.scrap, Bullets.slagShot,
                    Items.coal, UnityBullets.coalBlaze,
                    Items.pyratite, UnityBullets.pyraBlaze
                );

                size = 3;
                range = 80f;
                reloadTime = 6f;
                coolantMultiplier = 2f;
                recoilAmount = 0f;
                shootCone = 5f;
                shootSound = Sounds.flame;

                hasExp = true;
                condConfig = true;
                enableUpgrade = true;

                maxLevel = 10;

                progression.list(new Integer[]{1, 1, 2, 2, 2, 3, 3, 4, 4, 5, 5}, 1f, Interp.linear, val -> shots = val);
                progression.list(new Float[]{0f, 0f, 5f, 10f, 15f, 7f, 14f, 8f, 10f, 6f, 9f}, 1f, Interp.linear, val -> spread = val);
            }
        };

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

                consumes.power(2.4f);
                consumes.liquid(Liquids.cryofluid, 0.08f);

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
                    if(e.consValid()){
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

                consumes.power(3.2f);
                consumes.liquid(Liquids.cryofluid, 0.2f);

                drawer = new DrawGlow();
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
                    if(e.consValid()){
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
                drawer = new DrawSmelter(UnityPal.monolithLight){{
                    flameRadius = 5f;
                    flameRadiusIn = 2.6f;
                }};

                consumes.power(3.6f);
                consumes.items(with(Items.silicon, 3, UnityItems.archDebris, 1, UnityItems.monolite, 2));
                consumes.liquid(Liquids.cryofluid, 0.1f);

                update((SoulGenericCrafterBuild e) -> {
                    StemData data = e.data();
                    if(e.consValid()){
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
            powerUse = 1f;

            reloadTime = 60f;
            restitution = 0.03f;
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
        }};

        diviner = new SoulTurretPowerTurret("diviner"){{
            requirements(Category.turret, with(Items.lead, 15, UnityItems.monolite, 30));

            size = 1;
            health = 240;
            powerUse = 1.5f;

            reloadTime = 30f;
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
        }};

        lifeStealer = new SoulLifeStealerTurret("life-stealer"){{
            requirements(Category.turret, with(Items.silicon, 50, UnityItems.monolite, 25));

            size = 1;
            health = 320;
            powerUse = 1f;
            damage = 120f;

            requireSoul = false;
            efficiencyFrom = 0.8f;
            efficiencyTo = 1.5f;

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
            spread = 4f;
            reloadTime = 20f;
            restitution = 0.03f;
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
        }};

        absorberAura = new SoulAbsorberTurret("absorber-aura"){{
            requirements(Category.turret, with(Items.silicon, 75, UnityItems.monolite, 125));

            size = 2;
            health = 720;
            range = 150f;
            powerUse = 1f;
            resistance = 0.8f;

            targetBullets = true;

            requireSoul = false;
            efficiencyFrom = 0.8f;
            efficiencyTo = 1.6f;

            laserAlpha((SoulAbsorberTurretBuild b) -> b.power.status * (0.7f + b.soulf() * 0.3f));
        }};

        mage = new SoulTurretPowerTurret("mage"){{
            requirements(Category.turret, with(Items.lead, 75, Items.silicon, 50, UnityItems.monolite, 25));

            size = 2;
            health = 600;
            powerUse = 2.5f;

            range = 120f;
            reloadTime = 48f;
            shootCone = 15f;
            shots = 3;
            burstSpacing = 2f;
            shootSound = Sounds.spark;
            recoilAmount = 2.5f;
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
        }};

        blackout = new SoulTurretPowerTurret("blackout"){{
            requirements(Category.turret, with(Items.graphite, 85, Items.titanium, 25, UnityItems.monolite, 125));

            size = 2;
            health = 720;
            powerUse = 3f;

            reloadTime = 140f;
            range = 200f;
            rotateSpeed = 10f;
            recoilAmount = 3f;
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
        }};

        shellshock = new SoulTurretPowerTurret("shellshock"){{
            requirements(Category.turret, with(Items.lead, 90, Items.graphite, 100, UnityItems.monolite, 80));

            size = 2;
            health = 720;
            powerUse = 2f;

            reloadTime = 75f;
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
        }};

        heatRay = new SoulHeatRayTurret("heat-ray"){{
            requirements(Category.turret, with(Items.copper, 75, Items.lead, 50, Items.graphite, 25, Items.titanium, 45, UnityItems.monolite, 50));

            size = 2;
            range = 120f;
            targetGround = true;
            targetAir = false;
            damage = 240f;
            powerUse = 2f;
            shootSound = UnitySounds.heatRay;

            requireSoul = false;
            maxSouls = 5;
            efficiencyFrom = 0.8f;
            efficiencyTo = 1.6f;

            laserAlpha((SoulHeatRayTurretBuild b) -> b.power.status * (0.7f + b.soulf() * 0.3f));
        }};

        oracle = new SoulTurretBurstPowerTurret("oracle"){{
            requirements(Category.turret, with(Items.silicon, 175, Items.titanium, 150, UnityItems.monolithAlloy, 75));

            size = 3;
            health = 1440;
            powerUse = 3f;

            range = 180f;
            reloadTime = 72f;
            chargeTime = 30f;
            chargeMaxDelay = 4f;
            chargeEffects = 12;
            shootCone = 5f;
            shots = 8;
            burstSpacing = 2f;
            chargeEffect = UnityFx.oracleCharge;
            chargeBeginEffect = UnityFx.oracleChargeBegin;
            shootSound = Sounds.spark;
            shootShake = 3f;
            recoilAmount = 2.5f;
            rotateSpeed = 8f;
            shootType = new LightningBulletType(){{
                damage = 192f;
                shootEffect = Fx.lightningShoot;
            }};

            subShots = 3;
            subBurstSpacing = 1f;
            subShootEffect = Fx.hitLancer;
            subShootSound = Sounds.laser;
            subShootType = new LaserBulletType(288f){{
                length = 180f;
                sideAngle = 45f;
                inaccuracy = 8f;
            }};

            requireSoul = false;
            maxSouls = 7;
            efficiencyFrom = 0.7f;
            efficiencyTo = 1.67f;

            float base = shootType.damage;
            progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> shootType.damage = base * f);
        }};

        purge = new SoulTurretPowerTurret("purge"){{
            requirements(Category.turret, with(Items.plastanium, 75, Items.lead, 350, UnityItems.monolite, 200, UnityItems.monolithAlloy, 75));

            size = 3;
            health = 1680;
            powerUse = 3f;

            reloadTime = 90f;
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
        }};

        incandescence = new SoulHeatRayTurret("incandescence"){{
            requirements(Category.turret, with(UnityItems.monolite, 250, Items.phaseFabric, 45, UnityItems.monolithAlloy, 100));

            size = 3;
            health = 1680;
            range = 180f;
            targetGround = true;
            targetAir = true;
            damage = 480f;
            powerUse = 4f;
            shootSound = UnitySounds.heatRay;
            laserWidth = 0.54f;
            shootLength = 6f;

            requireSoul = false;
            maxSouls = 7;
            efficiencyFrom = 0.7f;
            efficiencyTo = 1.67f;

            laserAlpha((SoulHeatRayTurretBuild b) -> b.power.status * (0.7f + b.soulf() * 0.3f));
        }};

        prism = new PrismTurret("prism"){{
            requirements(Category.turret, with(Items.copper, 1));

            size = 4;
            health = 2800;
            range = 320f;
            reloadTime = 60f;
            rotateSpeed = 20f;
            recoilAmount = 6f;
            prismOffset = 6f;
            shootCone = 30f;

            targetGround = true;
            targetAir = true;

            shootSound = Sounds.shotgun;
            shootEffect = Fx.hitLaserBlast;
            model = UnityModels.prism;
            powerUse = 8f;

            requireSoul = false;
            maxSouls = 7;
            efficiencyFrom = 0.7f;
            efficiencyTo = 1.67f;

            shootType = new BulletType(0.0001f, 320f);

            float base = shootType.damage;
            progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> shootType.damage = base * f);
        }};

        supernova = new SupernovaTurret("supernova"){{
            requirements(Category.turret, with(Items.surgeAlloy, 500, Items.silicon, 650, UnityItems.archDebris, 350, UnityItems.monolithAlloy, 325));

            size = 7;
            health = 8100;
            powerUse = 24f;

            shootLength = size * tilesize / 2f - 8f;
            rotateSpeed = 1f;
            recoilAmount = 4f;
            cooldown = 0.006f;

            shootCone = 15f;
            range = 250f;

            shootSound = UnitySounds.supernovaShoot;
            loopSound = UnitySounds.supernovaActive;
            loopSoundVolume = 1f;

            baseExplosiveness = 25f;
            shootDuration = 480f;
            shootType = UnityBullets.supernovaLaser.copy();

            chargeBeginEffect = UnityFx.supernovaChargeBegin;

            requireSoul = false;
            maxSouls = 12;
            efficiencyFrom = 0.7f;
            efficiencyTo = 1.8f;

            float base = shootType.damage;
            progression.linear(efficiencyFrom, (efficiencyTo - efficiencyFrom) / maxSouls, f -> shootType.damage = base * f);
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
            consumes.power(1f);
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
            consumes.item(Items.sporePod, 1);
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
            consumes.power(1.6f);
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
            consumes.power(1f);
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
            consumes.power(16f);
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
            consumes.power(4.5f);
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
            consumes.item(Items.blastCompound);
        }};

        //endregion
        //region advance

        advanceConstructorModule = new ModularConstructorPart("advance-constructor-module"){{
            requirements(Category.units, with(UnityItems.xenium, 300, Items.silicon, 200, Items.graphite, 300, Items.thorium, 400, Items.phaseFabric, 50, Items.surgeAlloy, 100, UnityItems.advanceAlloy, 300));
            size = 6;
            liquidCapacity = 20f;

            consumes.liquid(Liquids.cryofluid, 0.7f);
            consumes.power(3f);
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

            consumes.power(13f);
        }};

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

        caster = new PowerTurret("arc-caster"){{
            requirements(Category.turret, with(Items.silicon, 20, UnityItems.xenium, 15, Items.titanium, 30, UnityItems.advanceAlloy, 25));
            size = 3;
            health = 4600;
            range = 190f;
            reloadTime = 120f;
            shootCone = 30f;
            inaccuracy = 9.2f;
            rotateSpeed = 5.5f;
            recoilAmount = 1f;
            powerUse = 9.4f;
            heatColor = UnityPal.lightHeat;
            cooldown = 0.01f;
            shootSound = Sounds.flame;
            shootEffect = Fx.none;
            chargeTime = 51f;
            chargeMaxDelay = 24f;
            chargeEffects = 5;
            chargeEffect = UnityFx.arcCharge;
            shootType = new ArcBulletType(4.6f, 8f){{
                lifetime = 43f;
                hitSize = 21f;

                lightningChance1 = 0.5f;
                lightningDamage1 = 29f;
                lightningChance2 = 0.2f;
                lightningDamage2 = 14f;
                length1 = 11;
                lengthRand1 = 7;
            }};
        }};

        storm = new PowerTurret("arc-storm"){{
            requirements(Category.turret, with(Items.silicon, 80, UnityItems.xenium, 35, Items.titanium, 90, UnityItems.advanceAlloy, 50));
            size = 4;
            health = 7600;
            range = 210f;
            reloadTime = 180f;
            shots = 5;
            shootCone = 30f;
            inaccuracy = 11.2f;
            rotateSpeed = 5.5f;
            recoilAmount = 2f;
            powerUse = 33.4f;
            heatColor = UnityPal.lightHeat;
            cooldown = 0.01f;
            shootSound = Sounds.flame;
            shootEffect = Fx.none;
            chargeTime = 51f;
            chargeMaxDelay = 24f;
            chargeEffects = 5;
            chargeEffect = UnityFx.arcCharge;
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
            }};
        }};

        eclipse = new LaserTurret("blue-eclipse"){
            {
                requirements(Category.turret, with(Items.lead, 620, Items.titanium, 520, Items.surgeAlloy, 720, Items.silicon, 760, Items.phaseFabric, 120, UnityItems.xenium, 620, UnityItems.advanceAlloy, 680));
                size = 7;
                health = 9000;
                range = 340f;
                reloadTime = 280f;
                coolantMultiplier = 2.4f;
                shootCone = 40f;
                powerUse = 19f;
                shootShake = 3f;
                shootEffect = Fx.shootBigSmoke2;
                recoilAmount = 8;
                shootSound = Sounds.laser;
                loopSound = UnitySounds.eclipseBeam;
                loopSoundVolume = 2.5f;
                heatColor = UnityPal.advanceDark;
                rotateSpeed = 1.9f;
                shootDuration = 320f;
                firingMoveFract = 0.12f;
                shootLength = size * tilesize / 2f - recoilAmount;

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

                consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.4f && liquid.flammability < 0.1f, 2.1f)).update(false);
            }

            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
            }
        };

        xenoCorruptor = new LaserTurret("xeno-corruptor"){
            {
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
                laserColors = new Color[]{UnityPal.advance};
            }};
        }};

        wavefront = new WavefrontTurret("wavefront"){{
            requirements(Category.turret, with(Items.copper, 4900, Items.graphite, 6000, Items.silicon, 5000, Items.titanium, 6500, UnityItems.xenium, 1500, UnityItems.advanceAlloy, 1500, UnityItems.terminum, 700, UnityItems.terminaAlloy, 500));
            health = 50625;
            model = UnityModels.wavefront;
            size = 15;
            range = 420f;
            rotateSpeed = 3f;
            reloadTime = 240f;
            powerUse = 260f;
            coolantMultiplier = 0.9f;
            shootSound = UnitySounds.cubeBlast;

            shootType = new WavefrontLaser(2400f);
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

            consumes.power(45.2f);
            consumes.items(with(UnityItems.plagueAlloy, 3, UnityItems.darkAlloy, 3, UnityItems.lightAlloy, 3, UnityItems.advanceAlloy, 3, UnityItems.monolithAlloy, 3, UnityItems.sparkAlloy, 3, UnityItems.superAlloy, 3));

            drawer = new DrawGlow(){
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
            };
        }};

        endForge = new StemGenericCrafter("end-forge"){
            final int effectTimer = timers++;

            {
                requirements(Category.crafting, with(Items.silicon, 2300, Items.phaseFabric, 650, Items.surgeAlloy, 1350, UnityItems.plagueAlloy, 510, UnityItems.darkAlloy, 510, UnityItems.lightAlloy, 510, UnityItems.advanceAlloy, 510, UnityItems.monolithAlloy, 510, UnityItems.sparkAlloy, 510, UnityItems.superAlloy, 510, UnityItems.terminationFragment, 230));
                size = 8;
                craftTime = 410f;
                ambientSoundVolume = 0.6f;
                outputItem = new ItemStack(UnityItems.terminaAlloy, 2);

                consumes.power(86.7f);
                consumes.items(with(UnityItems.terminum, 3, UnityItems.darkAlloy, 5, UnityItems.lightAlloy, 5));

                update((StemGenericCrafterBuild e) -> {
                    if(e.consValid()){
                        if(e.timer.get(effectTimer, 120f)){
                            UnityFx.forgeFlameEffect.at(e);
                            UnityFx.forgeAbsorbPulseEffect.at(e);
                        }
                        if(Mathf.chanceDelta(0.7f * e.warmup)){
                            UnityFx.forgeAbsorbEffect.at(e.x, e.y, Mathf.random(360f));
                        }
                    }
                });

                drawer = new DrawGlow(){
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
                };
            }
        };

        tenmeikiri = new EndLaserTurret("tenmeikiri"){{
            requirements(Category.turret, with(Items.phaseFabric, 3000, Items.surgeAlloy, 4000,
            UnityItems.darkAlloy, 1800, UnityItems.terminum, 1200, UnityItems.terminaAlloy, 200));

            health = 23000;
            range = 900f;
            size = 15;

            shootCone = 1.5f;
            reloadTime = 5f * 60f;
            coolantMultiplier = 0.5f;
            recoilAmount = 15f;
            powerUse = 350f;
            absorbLasers = true;
            shootLength = 8f;
            chargeTime = 158f;
            chargeEffects = 12;
            chargeMaxDelay = 80f;
            chargeEffect = ChargeFx.tenmeikiriChargeEffect;
            chargeBeginEffect = ChargeFx.tenmeikiriChargeBegin;
            chargeSound = UnitySounds.tenmeikiriCharge;
            shootSound = UnitySounds.tenmeikiriShoot;
            shootShake = 4f;
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

            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.25f && liquid.flammability < 0.1f, 3.1f)).update(false);
        }};

        endGame = new EndGameTurret("endgame"){{
            requirements(Category.turret, with(Items.phaseFabric, 9500, Items.surgeAlloy, 10500,
                UnityItems.darkAlloy, 2300, UnityItems.lightAlloy, 2300, UnityItems.advanceAlloy, 2300,
                UnityItems.plagueAlloy, 2300, UnityItems.sparkAlloy, 2300, UnityItems.monolithAlloy, 2300,
                UnityItems.superAlloy, 2300, UnityItems.terminum, 1600, UnityItems.terminaAlloy, 800, UnityItems.terminationFragment, 100
            ));

            shootCone = 360f;
            reloadTime = 430f;
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
            consumes.item(UnityItems.terminum, 2);
        }};

        //endregion

        for(ContentList faction : factionBlocks){
            faction.load();
        }
    }
}
package unity.content;

import arc.graphics.*;
import arc.math.geom.*;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;
import unity.graphics.*;
import unity.mod.*;
import unity.world.blocks.*;
import unity.world.blocks.distribution.*;
import unity.world.blocks.distribution.SimpleTransmission.*;
import unity.world.blocks.environment.*;
import unity.world.blocks.payloads.*;
import unity.world.blocks.power.*;
import unity.world.blocks.production.*;
import unity.world.blocks.units.*;
import unity.world.graph.*;

import static mindustry.type.ItemStack.with;
import static unity.mod.FactionRegistry.register;

/**
 * Defines all {@linkplain Faction#youngcha youngcha} block types.
 * @author younggam, xelo
 */
public final class YoungchaBlocks{
    public static Block oreNickel, concreteBlank, concreteFill, concreteNumber, concreteStripe, concrete, stoneFullTiles, stoneFull,
    stoneHalf, stoneTiles, pit, waterpit, nickelGeode, greysandWall, concreteWall;

    public static Block
    //torque
    //transmission
    driveShaft, shaftRouter, smallTransmission, torqueMeter, driveBeltSmall, driveBeltLarge,
    //power
    crankShaft, windTurbine, rotaryWaterExtractor, flywheel, torqueSource,
    //production
    augerDrill, wallDrill,
    //payload
    inserterArm,
    //crafting
    batchPress, batchMixer,
    ///heat
    //transmission
    heatPipe, steamPiston,
    //power
    combustionHeater, thermalHeater, seebeckGenerator, smallRadiator,
    //crucible
    //crafting
    crucible, crucibleChannel, cruciblePump, crucibleCaster, payloadCaster, crucibleSource, crucibleFluidLoader,
    //modules
    basicPanel,
    //other
    reinforcedPowerNode,//shitty power node just so vanilla can stop existing in this area for lore reasons.
    unitAssemblyArm,
    sandboxAssembler, monomialHangar; // monomial, binomial then polynomial (maybe meromorphic for the t6-t7 equiv massive unit)

    private YoungchaBlocks(){
        throw new AssertionError();
    }

    public static void load(){
        oreNickel = register(Faction.youngcha, new OreBlock(YoungchaItems.nickel){{
            oreScale = 24.77f;
            oreThreshold = 0.913f;
            oreDefault = true;
        }});
        concreteBlank = register(Faction.youngcha, new Floor("concrete-blank"){{
            attributes.set(Attribute.water, -0.85f);
        }});
        concreteFill = register(Faction.youngcha, new Floor("concrete-fill"){{
            variants = 0;
            attributes.set(Attribute.water, -0.85f);
        }});
        concreteNumber = register(Faction.youngcha, new Floor("concrete-number"){{
            variants = 10;
            attributes.set(Attribute.water, -0.85f);
        }});
        concreteStripe = register(Faction.youngcha, new Floor("concrete-stripe"){{
            attributes.set(Attribute.water, -0.85f);
        }});
        concrete = register(Faction.youngcha, new Floor("concrete"){{
            attributes.set(Attribute.water, -0.85f);
        }});
        stoneFullTiles = register(Faction.youngcha, new Floor("stone-full-tiles"){{
            attributes.set(Attribute.water, -0.75f);
        }});
        stoneFull = register(Faction.youngcha, new Floor("stone-full"){{
            attributes.set(Attribute.water, -0.75f);
        }});
        stoneHalf = register(Faction.youngcha, new Floor("stone-half"){{
            attributes.set(Attribute.water, -0.5f);
        }});
        stoneTiles = register(Faction.youngcha, new Floor("stone-tiles"){{
            attributes.set(Attribute.water, -0.5f);
        }});
        pit = register(Faction.youngcha, new Floor("pit"){
            {
                buildVisibility = BuildVisibility.editorOnly;
                cacheLayer = PUCacheLayer.pitLayer;
                placeableOn = false;
                solid = true;
                variants = 0;
                canShadow = false;
                mapColor = Color.black.cpy();
            }
        });
        waterpit = register(Faction.youngcha, new Floor("waterpit"){
            {
                buildVisibility = BuildVisibility.editorOnly;
                cacheLayer = PUCacheLayer.waterpitLayer;
                placeableOn = true;
                isLiquid = true;
                drownTime = 20f;
                speedMultiplier = 0.1f;
                liquidMultiplier = 2f;
                status = StatusEffects.wet;
                statusDuration = 120f;
                variants = 0;
                liquidDrop = Liquids.water;
                canShadow = false;
                mapColor = Liquids.water.color.cpy().lerp(Color.black, 0.5f);
            }
        });
        nickelGeode = register(Faction.youngcha, new LargeStaticWall("nickel-geode"){{
            variants = 2;
            itemDrop = YoungchaItems.nickel;
            maxsize = 3;
        }});
        greysandWall = register(Faction.youngcha, new LargeStaticWall("grey-sand-wall"){{
            variants = 3;
            itemDrop = Items.sand;
            maxsize = 3;
        }});
        concreteWall = register(Faction.youngcha, new ConnectedWall("concrete-wall"){
            {
                variants = 0;
            }
        });

        //

        driveShaft = register(Faction.youngcha, new DriveShaft("drive-shaft"){{
            health = 300;

            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.005f, 3f, b));
            config.fixedConnection(TorqueGraph.class, 1, 0, 1, 0);
            requirements(Category.power, with(Items.copper, 10, Items.lead, 10));
        }});
        shaftRouter = register(Faction.youngcha, new GenericGraphBlock("shaft-router"){{
            requirements(Category.power, with(Items.copper, 20, Items.lead, 20));
            health = 350;
            solid = true;

            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.04f, 4f, b));
            config.fixedConnection(TorqueGraph.class, 1, 1, 1, 1);
        }});
        smallTransmission = register(Faction.youngcha, new SimpleTransmission("small-transmission"){{
            requirements(Category.power, with(YoungchaItems.nickel, 20, Items.copper, 20, Items.lead, 20));
            health = 1100;
            size = 2;
            config.nodeConfig.put(TorqueGraph.class, b -> new TransmissionTorqueGraphNode(0.05f, 8f, 2, b));
            config.fixedConnection(TorqueGraph.class, 0, 1, 0, 0, 1, 0, 0, 0);
            config.fixedConnection(TorqueGraph.class, 1, 0, 0, 0, 0, 1, 0, 0);
        }});
        torqueMeter = register(Faction.youngcha, new TorqueMeter("torque-meter"){{
            requirements(Category.power, with(YoungchaItems.nickel, 20, Items.lead, 30));
            health = 250;
            rotate = true;
            solid = true;

            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueMeterGraphNode(0.01f, 5f, b));
            config.fixedConnection(TorqueGraph.class, 1, 0, 1, 0);

        }});
        driveBeltSmall = register(Faction.youngcha, new DriveBelt("small-drive-belt"){{
            requirements(Category.power, with(YoungchaItems.nickel, 50, Items.graphite, 20));
            health = 150;
            rotate = true;
            solid = true;
            config.nodeConfig.put(TorqueGraph.class, b -> new TransmissionTorqueGraphNode(0.03f, 8f, 1, b));
            config.fixedConnection(TorqueGraph.class, 0, 0, 1, 0);
            config.distanceConnection(TorqueGraph.class, 1);
        }});
        driveBeltLarge = register(Faction.youngcha, new DriveBelt("large-drive-belt"){{
            requirements(Category.power, with(YoungchaItems.cupronickel, 30, Items.silicon, 40, Items.graphite, 50));
            health = 1750;
            size = 3;
            maxRange = 10;
            wheelSize = 8;
            rotate = true;
            solid = true;
            config.nodeConfig.put(TorqueGraph.class, b -> new TransmissionTorqueGraphNode(0.05f, 30f, 1, b));
            config.fixedConnection(TorqueGraph.class, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
            config.distanceConnection(TorqueGraph.class, 6);
        }});
        torqueSource = register(Faction.youngcha, new TorqueSource("torque-source"){{
            solid = true;
            requirements(Category.power, BuildVisibility.sandboxOnly, with());
            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.001f, 999999f, b));
            config.fixedConnection(TorqueGraph.class, 1, 1, 1, 1);
        }});
        crankShaft = register(Faction.youngcha, new CrankShaft("hand-crank"){{
            requirements(Category.power, with(Items.lead, 5, Items.copper, 15));
            health = 250;
            solid = true;
            rotate = true;
            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.02f, 8f, 6, 60, b));
            config.fixedConnection(TorqueGraph.class, 0, 0, 1, 0);
        }});
        windTurbine = register(Faction.youngcha, new WindTurbine("wind-turbine"){{
            requirements(Category.power, with(Items.lead, 80, Items.copper, 70, Items.titanium, 15));
            health = 2600;
            size = 3;

            config.nodeConfig.put(TorqueGraph.class, b -> new WindTurbineTorqueGraphNode(0.03f, 20f, 1.2f, 20f, b));
            config.fixedConnection(TorqueGraph.class, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }});
        rotaryWaterExtractor = register(Faction.youngcha, new RotaryWaterExtractor("rotary-water-extractor"){{
            health = 2600;
            size = 3;
            result = Liquids.water;
            pumpAmount = 0.5f;
            liquidCapacity = 60f;
            rotateSpeed = 1.4f;
            attribute = Attribute.water;

            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.15f, 30f, 40, b));
            config.fixedConnection(TorqueGraph.class, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0);
            requirements(Category.production, with(Items.titanium, 50, YoungchaItems.nickel, 80, Items.metaglass, 30));
        }});

        augerDrill = register(Faction.youngcha, new TorqueDrill("auger-drill"){{
            health = 2600;
            size = 3;
            tier = 3;
            drillTime = 400;
            requirements(Category.production, with(Items.lead, 60, Items.copper, 150));
            consumeLiquid(Liquids.water, 0.08f).boost();

            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.13f, 50f, 40, b));
            config.fixedConnection(TorqueGraph.class, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        }});

        wallDrill = register(Faction.youngcha, new SmallWallDrill("wall-drill"){{
            requirements(Category.production, with(Items.graphite, 40, YoungchaItems.nickel, 40, Items.titanium, 20));
            health = 1100;
            size = 2;
            tier = 3;
            range = 2;
            drillTime = 40;
            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.07f, 20f, 50, b));
            config.fixedConnection(TorqueGraph.class, 0, 0, 0, 0, 1, 1, 0, 0);
        }});
        batchPress = register(Faction.youngcha, new BatchPress("graphite-batch-press"){{
            requirements(Category.crafting, with(YoungchaItems.nickel, 30, Items.copper, 130, Items.lead, 100));
            itemPlace = new Vec2[]{
            new Vec2(-4, -4), new Vec2(0, -4), new Vec2(4, -4),
            new Vec2(-4, 0), new Vec2(4, 0),
            new Vec2(-4, 4), new Vec2(0, 4), new Vec2(4, 4),
            new Vec2(0, 0)
            };
            size = 3;
            health = 2600;
            craftTime = 100.0F;
            consumeItem(Items.coal, 9);
            outputItem = new ItemStack(Items.graphite, 8);
            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.15f, 60f, 50, b));
            config.fixedConnection(TorqueGraph.class, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        }});

        batchMixer = register(Faction.youngcha, new BatchMixer("pyratite-batch-mixer"){{
            requirements(Category.crafting, with(YoungchaItems.nickel, 60, Items.metaglass, 40, Items.graphite, 30));
            size = 3;
            rotate = false;
            health = 2600;
            itemCapacity = 100;
            craftTime = 30.0F;
            consumeItems(new ItemStack(Items.coal, 1), new ItemStack(Items.lead, 1), new ItemStack(Items.sand, 1));
            outputItem = new ItemStack(Items.pyratite, 2);
            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.3f, 100f, 70, b));
            config.fixedConnection(TorqueGraph.class, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0);
        }});

        heatPipe = register(Faction.youngcha, new HeatPipe("heat-pipe"){{
            requirements(Category.power, with(YoungchaItems.nickel, 5, Items.copper, 10));
            health = 200;
            solid = false;
            targetable = false;
            config.nodeConfig.put(HeatGraph.class, b -> new HeatGraphNode(b, 0.005f, 0.4f, 1, 2500 + HeatGraphNode.celsiusZero));
            config.fixedConnection(HeatGraph.class, 1, 1, 1, 1);
        }});

        flywheel = register(Faction.youngcha, new FlyWheel("flywheel"){{
            requirements(Category.power, with(YoungchaItems.nickel, 50, Items.titanium, 50, Items.lead, 150));
            size = 3;
            rotate = true;
            health = 2600;
            solid = true;
            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.05f, 1000f, 30f, 10, b));
            config.fixedConnection(TorqueGraph.class, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        }});

        steamPiston = register(Faction.youngcha, new SteamPiston("steam-piston"){{
            requirements(Category.power, with(Items.graphite, 20, YoungchaItems.nickel, 30, Items.titanium, 50, Items.lead, 150));
            size = 3;
            rotate = true;
            health = 2000;
            solid = true;
            consumeLiquid(Liquids.water, 0.1f);
            config.nodeConfig.put(HeatGraph.class, b -> new HeatGraphNode(b, 0.01f, 0.1f, 9, 1100 + HeatGraphNode.celsiusZero));
            config.fixedConnection(HeatGraph.class, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        }});

        inserterArm = register(Faction.youngcha, new PayloadArm("inserter-arm"){{
            health = 150; // more delicate uwu

            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.07f, 13f, 40, b));
            config.fixedConnection(TorqueGraph.class, 0, 1, 0, 1);
            requirements(Category.units, with(YoungchaItems.cupronickel, 20, Items.graphite, 15, Items.silicon, 20));
        }});

        combustionHeater = register(Faction.youngcha, new CombustionHeater("combustion-heater"){{
            requirements(Category.power, with(YoungchaItems.nickel, 30, Items.lead, 70, Items.copper, 70));
            size = 2;
            rotate = true;
            health = 700;
            solid = true;
            config.nodeConfig.put(HeatGraph.class, b -> new HeatGraphNode(b, 0.01f, 0.1f, 4, 2100 + HeatGraphNode.celsiusZero, 1000 + HeatGraphNode.celsiusZero, 0.015f));
            config.fixedConnection(HeatGraph.class, 1, 1, 0, 0, 0, 0, 0, 0);
        }});

        thermalHeater = register(Faction.youngcha, new ThermalHeater("thermal-heater"){{
            size = 2;
            rotate = true;
            health = 1100;
            solid = true;
            floating = true;
            config.nodeConfig.put(HeatGraph.class, b -> new HeatGraphNode(b, 0.01f, 0.1f, 4, 2000 + HeatGraphNode.celsiusZero, 1000 + HeatGraphNode.celsiusZero, 0.015f));
            config.fixedConnection(HeatGraph.class, 1, 1, 0, 0, 0, 0, 0, 0);
            requirements(Category.power, with(YoungchaItems.nickel, 30, Items.graphite, 30, Items.copper, 100, YoungchaItems.cupronickel, 30));
        }});

        seebeckGenerator = register(Faction.youngcha, new SeebeckGenerator("seebeck-generator"){{
            size = 3;
            rotate = true;
            health = 2200;
            solid = true;
            hasPower = true;
            config.nodeConfig.put(HeatGraph.class, b -> new HeatGraphNode(b, 0.01f, 0.01f, 9, 1800 + HeatGraphNode.celsiusZero));
            config.fixedConnection(HeatGraph.class, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
            requirements(Category.power, with(YoungchaItems.nickel, 50, Items.graphite, 30, Items.copper, 120, Items.titanium, 100, YoungchaItems.cupronickel, 30));
        }});
        smallRadiator = register(Faction.youngcha, new HeatRadiator("small-radiator"){{

            size = 2;
            rotate = true;
            health = 1100;
            solid = true;
            config.nodeConfig.put(HeatGraph.class, b -> new HeatGraphNode(b, 0.4f, 0.15f, 4, 2400 + HeatGraphNode.celsiusZero));
            config.fixedConnection(HeatGraph.class, 0, 0, 1, 1, 0, 0, 1, 1);
            requirements(Category.power, with(YoungchaItems.nickel, 30, Items.graphite, 30, Items.copper, 100, YoungchaItems.cupronickel, 30));
        }});

        crucible = register(Faction.youngcha, new CrucibleBlock("crucible"){{
            requirements(Category.crafting, with(YoungchaItems.nickel, 30, Items.graphite, 30, Items.titanium, 50));
            size = 3;
            health = 1700;
            solid = true;

            config.nodeConfig.put(HeatGraph.class, b -> new HeatGraphNode(b, 0.015f, 0.15f, 9, 2400 + HeatGraphNode.celsiusZero));
            config.fixedConnection(HeatGraph.class, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1);
            config.nodeConfig.put(CrucibleGraph.class, b -> new CrucibleGraphNode(b, 50));
            config.fixedConnection(CrucibleGraph.class, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0);
        }});

        crucibleChannel = register(Faction.youngcha, new CrucibleChannel("crucible-channel"){{
            requirements(Category.crafting, with(YoungchaItems.nickel, 10, Items.graphite, 10));
            health = 300;
            config.nodeConfig.put(CrucibleGraph.class, b -> new CrucibleGraphNode(b, 5));
            config.fixedConnection(CrucibleGraph.class, 1, 1, 1, 1);
        }});

        cruciblePump = register(Faction.youngcha, new CruciblePump("crucible-pump"){{
            requirements(Category.crafting, with(YoungchaItems.nickel, 30, Items.graphite, 30, Items.titanium, 30));
            health = 300;
            rotate = true;
            solid = true;

            config.nodeConfig.put(CrucibleGraph.class, b -> new CrucibleGraphNode(b, 5));
            config.fixedConnection(CrucibleGraph.class, 0, 0, 1, 0);
            config.fixedConnection(CrucibleGraph.class, 1, 0, 0, 0);

            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.1f, 10f, b));
            config.fixedConnection(TorqueGraph.class, 0, 1, 0, 1);
        }});

        crucibleFluidLoader = register(Faction.youngcha, new CrucibleFluidLoader("crucible-fluid-loader"){{
            requirements(Category.crafting, with(YoungchaItems.nickel, 30, Items.silicon, 30, Items.metaglass, 30));
            health = 300;
            rotate = true;
            solid = true;
            liquidCapacity = 20;
            config.nodeConfig.put(CrucibleGraph.class, b -> new CrucibleGraphNode(b, 15));
            config.fixedConnection(CrucibleGraph.class, 1, 0, 0, 0);
        }});

        crucibleCaster = register(Faction.youngcha, new CrucibleCaster("casting-mold"){{
            requirements(Category.crafting, with(YoungchaItems.nickel, 60, Items.graphite, 50));
            health = 1700;
            rotate = true;
            solid = true;
            size = 3;
            itemCapacity = 4;
            hasItems = true;

            config.nodeConfig.put(CrucibleGraph.class, b -> new CrucibleGraphNode(b, 5));
            config.fixedConnection(CrucibleGraph.class, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);

            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.1f, 100f, 15, b));
            config.fixedConnection(TorqueGraph.class, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0);
        }});

        payloadCaster = register(Faction.youngcha, new PayloadCaster("payload-casting-mold"){{
            requirements(Category.crafting, with(YoungchaItems.cupronickel, 30, Items.metaglass, 30, Items.graphite, 50));
            health = 1700;
            rotate = true;
            solid = true;
            size = 3;
            moveTime = 50;

            config.nodeConfig.put(CrucibleGraph.class, b -> new CrucibleGraphNode(b, 50));
            config.fixedConnection(CrucibleGraph.class, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);

            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.1f, 100f, 40, b));
            config.fixedConnection(TorqueGraph.class, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0);
        }});

        crucibleSource = register(Faction.youngcha, new CrucibleSource("crucible-source"){{
            solid = true;
            requirements(Category.crafting, BuildVisibility.sandboxOnly, with());
            config.nodeConfig.put(CrucibleGraph.class, b -> new CrucibleGraphNode(b, 99));
            config.fixedConnection(CrucibleGraph.class, 1, 1, 1, 1);
        }});

        sandboxAssembler = register(Faction.youngcha, new ModularUnitAssembler("sandbox-assembler"){{
            requirements(Category.units, BuildVisibility.sandboxOnly, with());
            size = 3;
            health = 69420;
            sandbox = true;
        }});
        unitAssemblyArm = register(Faction.youngcha, new UnitAssemblerArm("unit-assembly-arm"){{
            requirements(Category.units, with(YoungchaItems.nickel, 30, Items.graphite, 30, YoungchaItems.cupronickel, 30));
            size = 3;
            rotate = true;
            solid = true;
            itemCapacity = 30;
            health = 2600;
            constructSpeed = 0.14f;
            config.nodeConfig.put(TorqueGraph.class, b -> new TorqueGraphNode(0.2f, 100f, 40, b));
            config.fixedConnection(TorqueGraph.class, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0);
        }});
        monomialHangar = register(Faction.youngcha, new ModularUnitAssembler("monomial-hangar"){{
            requirements(Category.units, with(Items.copper, 100, Items.graphite, 20, Items.metaglass, 20));
            size = 3;
            health = 2600;
            unitModuleWidth = 3;
            unitModuleHeight = 4;
            rotate = true;
        }});

        reinforcedPowerNode = register(Faction.youngcha, new PowerNode("reinforced-power-node"){{
            requirements(Category.power, with(YoungchaItems.nickel, 70, Items.titanium, 20, YoungchaItems.cupronickel, 20));
            size = 2;
            maxNodes = 5;
            laserRange = 9.5f;
            health = 900;
        }});


        //modules
        basicPanel = register(Faction.youngcha, new ModuleBlock("module-basic-panel"){{
            requirements(Category.crafting, BuildVisibility.hidden, with(YoungchaItems.nickel, 8));
        }});
    }
}

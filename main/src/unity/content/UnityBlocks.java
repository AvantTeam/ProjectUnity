package unity.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.Time;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.world.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.type.*;
import mindustry.ctype.*;
import mindustry.content.*;
import unity.graphics.UnityPal;
import unity.mod.UnitySounds;
import unity.world.blocks.defense.LightWall;
import unity.world.blocks.defense.turrets.*;
import unity.world.blocks.distribution.Teleporter;
import unity.world.blocks.logic.*;
import unity.world.blocks.production.StemGenericSmelter;
import unity.world.blocks.units.SelectableReconstructor;
import unity.world.draw.DrawLightSource;
import multilib.*;
import multilib.Recipe.*;

import static arc.Core.atlas;
import static mindustry.type.ItemStack.*;
import static unity.content.UnityFx.*;

public class UnityBlocks implements ContentList{
    public static Block

//global
    //blocks
    recursiveReconstructor,
    lightLamp, oilLamp, lightLaser, lightLampInfi, lightReflector, lightReflector1, lightOmnimirror, lightFilter, lightInvertedFilter, lightDivisor, lightDivisor1, lightItemFilter, lightPanel, lightInfluencer,
    metaglassWall, metaglassWallLarge,
    oreNickel, oreUmbrium, oreLuminum, oreMonolite, oreImberium,
    multiTest1, multiTest2,

//dark
    //turrets
    apparition, ghost, banshee, fallout, catastrophe, calamity, extinction,
    //factories
    darkAlloyForge,

//imber
    //turrets
    orb, shockwire, current, plasma, shielder,
    //factories
    sparkAlloyForge,

//koruh
    //turrets
    laserTurret, inferno,
    //blocks
    teleporter,

//light
    //turrets

//monolith
    //factories
    monolithAlloyForge,
    //turrets
    mage, oracle, spectrum,

//end
    //factories
    terminalCrucible, endForge;
    //turrets

//youngcha
    //distribution

    //generation

    //producers

    @Override
    public void load(){
        //region global blocks
        recursiveReconstructor = new SelectableReconstructor("recursive-reconstructor"){
            {
                requirements(Category.units, with(Items.graphite, 1600, Items.silicon, 2000, Items.metaglass, 900, Items.thorium, 600, Items.lead, 1200, Items.plastanium, 3600));
                size = 11;
                liquidCapacity = 360f;
                configurable = true;
                constructTime = 20000f;
                minTier = 6;
                upgrades.add(new UnitType[]{UnitTypes.toxopid, UnityUnitTypes.projectSpiboss});
                otherUpgrades.add(new UnitType[]{UnityUnitTypes.projectSpiboss, UnityUnitTypes.arcaetana});
                consumes.power(5f);
                consumes.items(with(Items.silicon, 1200, Items.metaglass, 800, Items.thorium, 700, Items.surgeAlloy, 400, Items.plastanium, 600, Items.phaseFabric, 350));
                consumes.liquid(Liquids.cryofluid, 7f);
            }
        };

        lightLamp = new LightSource("light-lamp"){
            {
                consumes.power(1f);
                requirements(Category.logic, with(Items.lead, 5, Items.metaglass, 10));
                drawer = new DrawLightSource();
                lightLength = 30;
            }
        };

        oilLamp = new LightSource("oil-lamp", true){
            {
                size = 3;
                health = 240;
                consumes.power(1.8f);
                consumes.liquid(Liquids.oil, 0.1f);
                requirements(Category.logic, with(Items.lead, 20, Items.metaglass, 20, Items.titanium, 15));
                drawer = new DrawLightSource();
                lightLength = 150;
                lightStrength = 750;
            }
        };

        lightLaser = new LightSource("light-laser"){
            {
                health = 60;
                consumes.power(1.5f);
                requirements(Category.logic, BuildVisibility.sandboxOnly, with(Items.metaglass, 10, Items.silicon, 5, Items.titanium, 5));
                alwaysUnlocked = true;
                drawer = new DrawLightSource();
                lightLength = 30;
                lightInterval = 0;
            }
        };

        lightLampInfi = new LightSource("light-lamp-infi"){
            {
                hasPower = false;
                consumesPower = false;
                requirements(Category.logic, BuildVisibility.sandboxOnly, with());
                alwaysUnlocked = true;
                drawer = new DrawLightSource();
                lightLength = 150;
                lightStrength = 600000;
                scaleStatus = false;
                maxLightLength = 7500;
            }
        };

        lightReflector = new LightReflector("light-reflector"){
            {
                requirements(Category.logic, with(Items.metaglass, 10));
            }
        };

        lightReflector1 = new LightReflector("light-reflector-1"){
            {
                diagonal = false;
                requirements(Category.logic, with(Items.metaglass, 10));
            }
        };

        lightOmnimirror = new LightOmniReflector("light-omnimirror"){
            {
                health = 80;
                requirements(Category.logic, with(Items.metaglass, 10, Items.silicon, 5));
            }
        };

        lightFilter = new LightFilter("light-filter"){
            {
                health = 60;
                requirements(Category.logic, with(Items.graphite, 10, Items.metaglass, 10));
            }
        };

        lightInvertedFilter = new LightFilter("light-inverted-filter", true){
            {
                health = 60;
                requirements(Category.logic, with(Items.graphite, 10, Items.metaglass, 10));
            }
        };

        lightDivisor = new LightDivisor("light-divisor"){
            {
                health = 80;
                requirements(Category.logic, with(Items.metaglass, 10, Items.titanium, 2));
            }
        };

        lightDivisor1 = new LightDivisor("light-divisor-1"){
            {
                diagonal = false;
                health = 80;
                requirements(Category.logic, with(Items.metaglass, 10, Items.titanium, 2));
            }
        };

        lightItemFilter = new LightRouter("light-item-filter"){
            {
                health = 60;
                requirements(Category.logic, with(Items.graphite, 5, Items.metaglass, 20, Items.silicon, 10));
            }
        };

        lightPanel = new LightGenerator("light-panel"){
            {
                health = 100;
                lightStrength = 80f;
                scaleStatus = true;
                powerProduction = 1f;
                requirements(Category.logic, with(Items.copper, 15, Items.graphite, 10, Items.silicon, 15));
            }
        };

        lightInfluencer = new LightInfluencer("light-influencer"){
            {
                health = 60;
                lightStrength = 1f;
                scaleStatus = true;
                powerProduction = 1f;
                requirements(Category.logic, with(Items.lead, 15, Items.metaglass, 10, Items.silicon, 5));
            }
        };

        metaglassWall = new LightWall("metaglass-wall"){
            {
                health = 350;
                requirements(Category.defense, with(Items.lead, 6, Items.metaglass, 6));
            }
        };

        metaglassWallLarge = new LightWall("metaglass-wall-large"){
            {
                size = 2;
                health = 1400;
                requirements(Category.defense, with(Items.lead, 24, Items.metaglass, 24));
            }
        };

        oreNickel = new OreBlock(UnityItems.nickel){
            {
                oreScale = 24.77f;
                oreThreshold = 0.913f;
                oreDefault = true;
            }
        };

        oreUmbrium = new OreBlock(UnityItems.umbrium){
            {
                oreScale = 23.77f;
                oreThreshold = 0.813f;
                oreDefault = true;
            }
        };

        oreLuminum = new OreBlock(UnityItems.luminum){
            {
                oreScale = 23.77f;
                oreThreshold = 0.81f;
                oreDefault = true;
            }
        };

        oreMonolite = new OreBlock(UnityItems.monolite){
            {
                oreScale = 23.77f;
                oreThreshold = 0.807f;
                oreDefault = true;
            }
        };

        oreImberium = new OreBlock(UnityItems.imberium){
            {
                oreScale = 23.77f;
                oreThreshold = 0.807f;
                oreDefault = true;
            }
        };

        multiTest1 = new MultiCrafter("multi-test-1", 10){
            {
                requirements(Category.crafting, with(Items.copper, 10));
                size = 3;
                dumpToggle = true;
                addRecipe(new InputContents(), new OutputContents(5.25f), 12);
                addRecipe(
                new InputContents(with(Items.coal, 1, Items.sand, 1), new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
                new OutputContents(new LiquidStack[]{new LiquidStack(Liquids.slag, 5)}), 60);
                addRecipe(
                new InputContents(with(Items.pyratite, 1, Items.blastCompound, 1), new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
                new OutputContents(with(Items.scrap, 1, Items.plastanium, 2, Items.sporePod, 2), new LiquidStack[]{new LiquidStack(Liquids.oil, 5)}), 72);
                addRecipe(
                new InputContents(with(Items.sand, 1)),
                new OutputContents(with(Items.silicon, 1)), 30);
                addRecipe(
                new InputContents(with(Items.sand, 1, Items.lead, 2), new LiquidStack[]{new LiquidStack(Liquids.water, 5)}),
                new OutputContents(with(UnityItems.contagium, 1)), 12);
                addRecipe(
                new InputContents(with(Items.coal, 1, Items.sand, 1), new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
                new OutputContents(with(Items.thorium, 1, Items.surgeAlloy, 1), new LiquidStack[]{new LiquidStack(Liquids.slag, 5)}), 60);
                addRecipe(
                new InputContents(with(Items.pyratite, 1, Items.blastCompound, 1), new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
                new OutputContents(with(Items.scrap, 1, Items.plastanium, 2, Items.sporePod, 2), new LiquidStack[]{new LiquidStack(Liquids.oil, 5)}), 72);
                addRecipe(
                new InputContents(with(Items.sand, 1)),
                new OutputContents(with(Items.silicon, 1)), 30);
                addRecipe(
                new InputContents(with(Items.sand, 1, Items.lead, 2), new LiquidStack[]{new LiquidStack(Liquids.water, 5)}),
                new OutputContents(with(UnityItems.contagium, 1)), 12);
                addRecipe(
                new InputContents(with(Items.coal, 1, Items.sand, 1), new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
                new OutputContents(with(Items.thorium, 1, Items.surgeAlloy, 1), new LiquidStack[]{new LiquidStack(Liquids.slag, 5), new LiquidStack(Liquids.oil, 5)}), 60);
            }
        };

        multiTest2 = new MultiCrafter("multi-test-2", 4){
            {
                requirements(Category.crafting, with(Items.copper, 10));
                size = 3;
                addRecipe(
                new InputContents(with(Items.sand, 1, Items.lead, 1)),
                new OutputContents(), 12f);
                addRecipe(
                new InputContents(with(Items.coal, 1, Items.sand, 1)),
                new OutputContents(with(Items.thorium, 1, Items.surgeAlloy, 2), 10), 60f);
                addRecipe(
                new InputContents(with(Items.pyratite, 1, Items.blastCompound, 1)),
                new OutputContents(with(Items.scrap, 1, Items.plastanium, 2, Items.sporePod, 2)), 72f);
                addRecipe(
                new InputContents(with(Items.sand, 1), 15),
                new OutputContents(with(Items.silicon, 1), 10), 30);
            }
        };
        //endregion
        //region dark turrets
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

        ghost = new BarrelsItemTurret("ghost"){
            {
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
            }
        };

        banshee = new unity.world.blocks.defense.turrets.BarrelsItemTurret("banshee"){
            {
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
            }
        };

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
                powerUse = 19f;
                shootShake = 3f;
                firingMoveFract = 0.2f;
                shootEffect = Fx.shootBigSmoke2;
                recoilAmount = 4f;
                shootSound = Sounds.laserbig;
                heatColor = Color.valueOf("e04300");
                rotateSpeed = 3.5f;
                loopSound = Sounds.beam;
                loopSoundVolume = 2.2f;
                requirements(Category.turret, with(Items.copper, 450, Items.lead, 350, Items.graphite, 390, Items.silicon, 360, Items.titanium, 250, UnityItems.umbrium, 370, Items.surgeAlloy, 360));
                shootType = UnityBullets.falloutLaser;
                consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability < 0.1f, 0.58f)).update(false);
            }
        };

        catastrophe = new BigLaserTurret("catastrophe"){
            {
                size = 8;
                health = 9750;
                range = 300f;
                reloadTime = 190f;
                coolantMultiplier = 0.6f;
                shootCone = 40f;
                shootDuration = 320f;
                powerUse = 39f;
                shootShake = 4f;
                firingMoveFract = 0.16f;
                shootEffect = Fx.shootBigSmoke2;
                recoilAmount = 7f;
                cooldown = 0.012f;
                heatColor = Color.white;
                rotateSpeed = 1.9f;
                loopSoundVolume = 2.4f;
                expanded = true;
                requirements(Category.turret, with(Items.copper, 1250, Items.lead, 1320, Items.graphite, 1100, Items.titanium, 1340, Items.surgeAlloy, 1240, Items.silicon, 1350, Items.thorium, 770, UnityItems.darkAlloy, 370));
                shootType = UnityBullets.catastropheLaser;
                consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.4f && liquid.flammability < 0.1f, 1.3f)).update(false);
            }
        };

        calamity = new BigLaserTurret("calamity"){
            {
                size = 12;
                health = 22000;
                range = 420f;
                reloadTime = 320f;
                coolantMultiplier = 0.6f;
                shootCone = 23f;
                shootDuration = 360f;
                powerUse = 87f;
                shootShake = 4f;
                firingMoveFract = 0.09f;
                shootEffect = Fx.shootBigSmoke2;
                recoilAmount = 7f;
                cooldown = 0.009f;
                heatColor = Color.white;
                rotateSpeed = 0.97f;
                loopSoundVolume = 3f;
                expanded = true;
                requirements(Category.turret, with(Items.copper, 2800, Items.lead, 2970, Items.graphite, 2475, Items.titanium, 3100, Items.surgeAlloy, 2790, Items.silicon, 3025, Items.thorium, 1750, UnityItems.darkAlloy, 1250));
                shootType = UnityBullets.calamityLaser;
                consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.3f && liquid.flammability < 0.1f, 2.1f)).update(false);
            }
        };

        extinction = new BigLaserTurret("extinction"){
            {
                requirements(Category.turret, with(Items.copper, 3800, Items.lead, 4100, Items.graphite, 3200, Items.titanium, 4200, Items.surgeAlloy, 3800, Items.silicon, 4300, Items.thorium, 2400, UnityItems.darkAlloy, 1700, UnityItems.terminum, 900, UnityItems.terminaAlloy, 500));
                size = 14;
                health = 29500;
                range = 520f;
                reloadTime = 380f;
                coolantMultiplier = 0.4f;
                shootCone = 12f;
                shootDuration = 360f;
                powerUse = 125f;
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
                shootType = UnityBullets.extinctionLaser;
                consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.27f && liquid.flammability < 0.1f, 2.5f)).update(false);
            }
        };
        //endregion
        //region dark factories
        darkAlloyForge = new StemGenericSmelter("dark-alloy-forge"){
            {
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
            }
        };
        //endregion
        //region imber turrets
        orb = new ChargeTurret("orb"){
            {
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
                shootEffect = orbShoot;
                smokeEffect = Fx.none;
                chargeEffect = orbCharge;
                chargeBeginEffect = orbChargeBegin;
            }
        };

        shockwire = new LaserTurret("shockwire"){
            {
                requirements(Category.turret, with(Items.copper, 150, Items.lead, 145, Items.titanium, 160, Items.silicon, 130, UnityItems.imberium, 70));
                size = 2;
                health = 1400;
                range = 125f;
                reloadTime = 140f;
                coolantMultiplier = 2f;
                shootCone = 1f;
                firingMoveFract = 0.15f;
                shootDuration = 200f;
                inaccuracy = 0f;
                powerUse = 8.6f;
                targetAir = false;
                shootType = UnityBullets.shockBeam;
                shootSound = Sounds.thruster;
                consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.4f)).update(false);
            }

            @Override
            public void setStats(){
                super.setStats();

                stats.remove(Stat.damage);
                stats.add(Stat.damage, shootType.damage, StatUnit.none);
            }
        };

        current = new ChargeTurret("current"){
            {
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
                chargeEffect = currentCharge;
                chargeBeginEffect = currentChargeBegin;
                consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.52f)).boost();
            }
        };

        plasma = new BurstChargeTurret("plasma"){
            {
                requirements(Category.turret, with(Items.copper, 580, Items.lead, 520, Items.graphite, 410, Items.silicon, 390, Items.surgeAlloy, 180, UnityItems.sparkAlloy, 110));
                size = 4;
                health = 2800;
                range = 200f;
                reloadTime = 460f;
                coolantMultiplier = 1.2f;
                liquidCapacity = 20f;
                shootCone = 1f;
                inaccuracy = 0f;
                chargeTime = 240f;
                chargeEffects = 15;
                chargeMaxDelay = 240f;
                powerUse = 15.2f;
                shootType = UnityBullets.plasmaTriangle;
                shootSound = Sounds.shotgun;
                shootEffect = plasmaShoot;
                chargeEffect = plasmaCharge;
                chargeBeginEffect = plasmaChargeBegin;
                shots = 1;
                subShots = 0;
                alwaysTurn = false;
                consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.52f)).boost();
            }
        };

        shielder = new ShieldTurret("shielder"){
            {
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
                    Angles.randLenVectors(e.id, 2, 1 + 20 * e.fout(), e.rotation, 120, (x, y) -> {
                        Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3 + 1);
                    });
                });
                chargeBeginEffect = Fx.none;
                consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.4f)).update(false);
            }
        };
        //endregion
        //region imber factories
        sparkAlloyForge = new StemGenericSmelter("spark-alloy-forge"){
            {
                requirements(Category.crafting, with(Items.lead, 160, Items.graphite, 340, UnityItems.imberium, 270, Items.silicon, 250, Items.thorium, 120, Items.surgeAlloy, 100));
                outputItem = new ItemStack(UnityItems.sparkAlloy, 4);
                size = 4;
                craftTime = 160f;
                ambientSound = Sounds.machine;
                ambientSoundVolume = 0.6f;
                craftEffect = imberCircleSparkCraftingEffect;
                afterUpdate = e -> {
                    if(e.consValid()){
                        if(Mathf.chanceDelta(0.3f)) imberSparkCraftingEffect.at(e.getX(), e.getY(), Mathf.random(360f));
                        else if(Mathf.chanceDelta(0.02f)) Lightning.create(e.team, UnityPal.imberColor, 5f, e.x, e.y, Mathf.random(360f), 5);
                    }
                };
                consumes.power(2.6f);
                consumes.items(with(Items.surgeAlloy, 3, Items.titanium, 4, Items.silicon, 6, UnityItems.imberium, 3));
            }
        };
        //endregion
        //region koruh turrets
        laserTurret = new ExpPowerTurret("laser-turret", 10){
            {
                requirements(Category.turret, with(Items.copper, 160, Items.lead, 110, Items.silicon, 90));
                size = 2;
                health = 800;
                range = 140f;
                coolantMultiplier = 2f;
                shootCone = 1f;
                inaccuracy = 0f;
                powerUse = 7f;
                shootType = UnityBullets.laser;
                addExpField("linear", "reloadTime", 35, -2);
                addExpField("bool", "targetAir", 0, 5);
            }
        };

        inferno = new ExpItemTurret("inferno", 10){
            {
                requirements(Category.turret, with(Items.copper, 150, Items.lead, 165, Items.graphite, 120, Items.silicon, 130));
                ammo(Items.coal, UnityBullets.coalBlaze, Items.pyratite, UnityBullets.pyraBlaze);
                size = 3;
                health = 1500;
                range = 80f;
                reloadTime = 10f;
                shootCone = 5f;
                addExpField("exp", "useless", 0, 2);
            }
        };

        //endregion
        //region koruh blocks
        teleporter = new Teleporter("teleporter"){
            {
                requirements(Category.distribution, with(Items.lead, 12, Items.silicon, 10, Items.phaseFabric, 10, Items.thorium, 4));
            }
        };
        //endregion
        //region monolith factories
        monolithAlloyForge = new StemGenericSmelter("monolith-alloy-forge"){
            {
                requirements(Category.crafting, with(Items.lead, 380, UnityItems.monolite, 240, Items.silicon, 400, Items.titanium, 240, Items.thorium, 90, Items.surgeAlloy, 160));
                final int effectTimer = timers++;
                afterUpdate = e -> {
                    if(e.data == null) e.data = 0f;
                    if(e.consValid()) e.data = Mathf.lerpDelta((float)e.data, e.efficiency(), 0.02f);
                    else e.data = Mathf.lerpDelta((float)e.data, 0f, 0.02f);
                    float temp = (float)e.data;
                    if(!Mathf.zero(temp)){
                        if(e.timer.get(effectTimer, 45f)) effect.at(e.x, e.y, e.rotation, temp);
                        //TODO not exactly same with js ver?.
                        if(Mathf.chanceDelta(temp * 0.5f)) Lightning.create(e.team, Pal.lancerLaser, 1f, e.x, e.y, Mathf.randomSeed((int)Time.time() + e.id, 360f), (int)(temp * 4f) + Mathf.random(3));
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
            }
        };
        //endregion
        //region monolith turrets
        mage = new PowerTurret("mage"){
            {
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
            }
        };

        oracle = new BurstChargeTurret("oracle"){
            {
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
                chargeEffect = oracleChage;
                chargeBeginEffect = oracleChargeBegin;
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
            }
        };
        //endregion
        //region end factories
        terminalCrucible = new StemGenericSmelter("terminal-crucible"){
            {
                requirements(Category.crafting, with(Items.lead, 810, Items.graphite, 720, Items.silicon, 520, Items.phaseFabric, 430, Items.surgeAlloy, 320, UnityItems.plagueAlloy, 120, UnityItems.darkAlloy, 120, UnityItems.lightAlloy, 120, UnityItems.advanceAlloy, 120, UnityItems.monolithAlloy, 120, UnityItems.sparkAlloy, 120, UnityItems.superAlloy, 120));
                flameColor = UnityPal.scarColor;
                addSprites(name + "-lights");
                preserveDraw = false;
                afterDrawer = e -> {
                    drawer.draw(e);
                    if(e.warmup > 0f){
                        Draw.blend(Blending.additive);
                        Draw.color(1f, Mathf.absin(Time.time(), 5f, 0.5f) + 0.5f, Mathf.absin(Time.time() + 90f * Mathf.radDeg, 5f, 0.5f) + 0.5f, e.warmup);
                        Draw.rect(dataRegions[0], e.x, e.y);
                        float b = (Mathf.absin(Time.time(), 8f, 0.25f) + 0.7f) * e.warmup;
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
            }
        };

        endForge = new StemGenericSmelter("end-forge"){
            {
                requirements(Category.crafting, with(Items.silicon, 2300, Items.phaseFabric, 650, Items.surgeAlloy, 1350, UnityItems.plagueAlloy, 510, UnityItems.darkAlloy, 510, UnityItems.lightAlloy, 510, UnityItems.advanceAlloy, 510, UnityItems.monolithAlloy, 510, UnityItems.sparkAlloy, 510, UnityItems.superAlloy, 510, UnityItems.terminationFragment, 230));
                outputItem = new ItemStack(UnityItems.terminaAlloy, 2);
                size = 8;
                craftTime = 410f;
                ambientSoundVolume = 0.6f;
                addSprites(name + "-lights", name + "-top-small");
                foreUpdate = e -> {
                    if(e.consValid() && Mathf.chanceDelta(0.7f * e.warmup)) forgeAbsorbEffect.at(e.x, e.y, Mathf.random(360f));
                };
                preserveDraw = false;
                afterDrawer = e -> {
                    drawer.draw(e);
                    if(e.warmup <= 0.0001f) return;
                    Draw.blend(Blending.additive);
                    Draw.color(1f, Mathf.absin(Time.time(), 5f, 0.5f) + 0.5f, Mathf.absin(Time.time() + 90f * Mathf.radDeg, 5f, 0.5f) + 0.5f, e.warmup);
                    Draw.rect(dataRegions[0], e.x, e.y);
                    float b = (Mathf.absin(Time.time(), 8f, 0.25f) + 0.75f) * e.warmup;
                    Draw.color(1f, b, b, b);
                    Draw.rect(topRegion, e.x, e.y);
                    for(int i = 0; i < 4; i++){
                        float ang = i * 90f;
                        for(int s = 0; s < 2; s++){
                            float offset = 360f / 8f * (i * 2 + s);
                            TextureRegion reg = dataRegions[1];
                            int sign = Mathf.signs[s];
                            float colA = (Mathf.absin(Time.time() + offset * Mathf.radDeg, 8f, 0.25f) + 0.75f) * e.warmup;
                            float colB = (Mathf.absin(Time.time() + (90f + offset) * Mathf.radDeg, 8f, 0.25f) + 0.75f) * e.warmup;
                            Draw.color(1, colA, colB, e.warmup);
                            Draw.rect(reg, e.x, e.y, reg.width * sign * Draw.scl, reg.height * Draw.scl, -ang);
                        }
                    }
                    Draw.blend();
                    Draw.color();
                };
                consumes.power(86.7f);
                consumes.items(with(UnityItems.terminum, 3, UnityItems.darkAlloy, 5, UnityItems.lightAlloy, 5));
            }
        };
        //endregion
    }
}

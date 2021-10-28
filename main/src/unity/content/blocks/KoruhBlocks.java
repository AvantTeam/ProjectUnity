package unity.content.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.production.GenericCrafter.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.content.effects.*;
import unity.entities.bullet.exp.*;
import unity.gen.*;
import unity.graphics.*;
import unity.world.blocks.defense.*;
import unity.world.blocks.distribution.*;
import unity.world.blocks.production.*;
import unity.world.blocks.units.*;
import unity.world.consumers.*;

import static mindustry.Vars.tilesize;
import static mindustry.type.ItemStack.empty;
import static mindustry.type.ItemStack.with;

public class KoruhBlocks implements ContentList{
    public static @FactionDef("koruh")
    Block
    //crafting
    solidifier, steelSmelter, liquifier,

    //defense
    stoneWall, denseWall, steelWall, steelWallLarge, diriumWall, diriumWallLarge,

    //distribution
    steelConveyor, diriumConveyor,

    //unit
    bufferPad, omegaPad, cachePad, convertPad, teleporter, teleunit;

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

    @Override
    public void load(){
        //region Crafting

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

        //endregion
        //region Defence

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

        //endregion
        //region Distribution

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

        //endregion
        //region Unit

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

        //endregion
        //region Turret

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
    }
}
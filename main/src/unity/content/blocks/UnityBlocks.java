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
import mindustry.world.blocks.production.GenericCrafter.*;
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
        new ImberBlocks(),
        new KoruhBlocks()
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
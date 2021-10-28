package unity.content.blocks;

import arc.graphics.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.consumers.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.content.effects.*;
import unity.entities.bullet.energy.*;
import unity.entities.bullet.laser.*;
import unity.gen.*;
import unity.graphics.*;
import unity.world.blocks.defense.turrets.*;
import unity.world.blocks.units.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.type.ItemStack.*;

public class AdvanceBlocks implements ContentList{
    public static @FactionDef("advance")
    Block
    //units
    advanceConstructorModule, advanceConstructor,

    //turret
    celsius, kelvin, caster, storm, eclipse, xenoCorruptor, cube, wavefront;

    @Override
    public void load(){
        //region Units

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

        //endregion
        //region Turrets

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
    }
}
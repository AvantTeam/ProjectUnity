package unity.content.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.gen.*;
import unity.graphics.*;
import unity.mod.*;
import unity.world.blocks.*;
import unity.world.blocks.defense.*;
import unity.world.blocks.defense.turrets.*;
import unity.world.blocks.environment.*;
import unity.world.blocks.production.*;
import unity.world.meta.*;

import static mindustry.Vars.tilesize;
import static mindustry.type.ItemStack.with;

public class MonolithBlocks implements ContentList{
    public static @FactionDef("monolith")
    Block
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

    @Override
    public void load(){
        //region Environment

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

        //endregion
        //region Crafting

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

        //endregion
        //region Defence

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

        //endregion
        //region Turret

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
    }
}
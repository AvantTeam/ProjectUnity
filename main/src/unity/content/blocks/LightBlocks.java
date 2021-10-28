package unity.content.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.content.effects.*;
import unity.entities.bullet.energy.*;
import unity.entities.bullet.laser.*;
import unity.gen.*;
import unity.graphics.*;
import unity.world.*;
import unity.world.blocks.defense.*;
import unity.world.blocks.defense.turrets.*;
import unity.world.blocks.environment.*;
import unity.world.blocks.light.*;
import unity.world.draw.*;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;
import static mindustry.type.ItemStack.with;

public class LightBlocks implements ContentList{
    public static @FactionDef("light")
    Block
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

    @Override
    public void load(){
        //region Environment

        oreLuminum = new UnityOreBlock(UnityItems.luminum){{
            oreScale = 23.77f;
            oreThreshold = 0.81f;
            oreDefault = false;
        }};

        //endregion
        //region Turret

        photon = new LaserTurret("photon"){{
            requirements(Category.turret, with(Items.lead, 50, Items.silicon, 35, UnityItems.luminum, 65, Items.titanium, 65));
            size = 2;
            health = 1280;
            reloadTime = 100f;
            shootCone = 30f;
            range = 120f;
            powerUse = 4.5f;
            heatColor = UnityPal.lightHeat;
            loopSound = Sounds.respawning;
            shootType = new ContinuousLaserBulletType(16f){{
                incendChance = -1f;
                length = 130f;
                width = 4f;
                colors = new Color[]{Pal.lancerLaser.cpy().a(3.75f), Pal.lancerLaser, Color.white};
                strokes = new float[]{0.92f, 0.6f, 0.28f};
                lightColor = hitColor = Pal.lancerLaser;
            }};
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability < 0.1f, 0.2f)).update(false);
        }};

        graviton = new LaserTurret("graviton"){{
            requirements(Category.turret, with(Items.lead, 110, Items.graphite, 90, Items.silicon, 70, UnityItems.luminum, 180, Items.titanium, 135));
            size = 3;
            health = 2780;
            reloadTime = 150f;
            recoilAmount = 2f;
            shootCone = 30f;
            range = 230f;
            powerUse = 5.75f;
            heatColor = UnityPal.lightHeat;
            loopSound = UnitySounds.xenoBeam;
            shootType = new GravitonLaserBulletType(0.8f){{
                length = 260f;
                knockback = -5f;
                incendChance = -1f;
                colors = new Color[]{UnityPal.advanceDark.cpy().a(0.1f), Pal.lancerLaser.cpy().a(0.2f)};
                strokes = new float[]{2.4f, 1.8f};
            }};
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability < 0.1f, 0.25f)).update(false);
        }};

        electron = new PowerTurret("electron"){{
            requirements(Category.turret, with(Items.lead, 110, Items.silicon, 75, UnityItems.luminum, 165, Items.titanium, 125));
            size = 3;
            health = 2540;
            reloadTime = 60f;
            coolantMultiplier = 2f;
            range = 170f;
            powerUse = 6.6f;
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
        }};

        proton = new PowerTurret("proton"){{
            requirements(Category.turret, with(Items.lead, 110, Items.silicon, 75, UnityItems.luminum, 165, Items.titanium, 135));
            size = 4;
            health = 2540;
            reloadTime = 60f;
            range = 245f;
            shootCone = 20f;
            heatColor = UnityPal.lightHeat;
            rotateSpeed = 1.5f;
            recoilAmount = 4f;
            powerUse = 4.9f;
            targetAir = false;
            cooldown = 0.008f;
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
        }};

        neutron = new PowerTurret("neutron"){{
            requirements(Category.turret, with(Items.lead, 110, Items.silicon, 75, UnityItems.luminum, 165, Items.titanium, 135));
            size = 4;
            health = 2520;
            reloadTime = 10f;
            range = 235f;
            shootCone = 20f;
            heatColor = UnityPal.lightHeat;
            rotateSpeed = 3.9f;
            recoilAmount = 4f;
            powerUse = 4.9f;
            cooldown = 0.008f;
            inaccuracy = 3.4f;
            shootEffect = ShootFx.blueTriangleShoot;
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
            reloadTime = 90f;
            coolantMultiplier = 3f;
            shootCone = 30f;
            range = 200f;
            heatColor = UnityPal.lightHeat;
            rotateSpeed = 4.3f;
            recoilAmount = 2f;
            powerUse = 1.9f;
            cooldown = 0.012f;
            shootSound = UnitySounds.gluonShoot;
            shootType = UnityBullets.gluonEnergyBall;
        }};

        wBoson = new PowerTurret("w-boson"){
            {
                requirements(Category.turret, with(Items.silicon, 300, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 110, UnityItems.lightAlloy, 15));
                health = 4000;
                size = 5;
                reloadTime = 90f;
                range = 250f;
                rotateSpeed = 2.5f;
                shootCone = 20f;
                heatColor = UnityPal.lightHeat;
                chargeBeginEffect = ChargeFx.wBosonChargeBeginEffect;
                chargeEffect = ChargeFx.wBosonChargeEffect;
                chargeTime = 38f;
                cooldown = 0.008f;
                powerUse = 8.6f;

                shootType = new DecayBasicBulletType(8.5f, 24f){{
                    drag = 0.026f;
                    lifetime = 48f;
                    hittable = absorbable = collides = false;
                    backColor = trailColor = hitColor = lightColor = Pal.lancerLaser;
                    shootEffect = smokeEffect = Fx.none;
                    hitEffect = Fx.hitLancer;
                    despawnEffect = HitFx.LightHitLarge;
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

            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
            }
        };

        zBoson = new RampupPowerTurret("z-boson"){
            {
                requirements(Category.turret, with(Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 15));
                health = 4000;
                size = 5;
                reloadTime = 40f;
                range = 230f;
                shootCone = 20f;
                heatColor = UnityPal.lightHeat;
                coolantMultiplier = 1.9f;
                rotateSpeed = 2.7f;
                recoilAmount = 2f;
                restitution = 0.09f;
                cooldown = 0.008f;
                powerUse = 3.6f;
                targetAir = true;
                shootSound = UnitySounds.zbosonShoot;
                alternate = true;
                shots = 2;
                spread = 14f;
                inaccuracy = 2.3f;

                lightning = true;
                lightningThreshold = 12f;
                baseLightningLength = 16;
                lightningLengthDec = 1;
                baseLightningDamage = 18f;
                lightningDamageDec = 1f;

                barBaseY = -10.75f;
                barLength = 20f;

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

            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
            }
        };

        higgsBoson = new PowerTurret("higgs-boson"){
            {
                requirements(Category.turret, with(Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 20));
                size = 6;
                health = 6000;
                reloadTime = 13f;
                alternate = true;
                spread = 17.25f;
                shots = 2;
                range = 260f;
                shootCone = 20f;
                heatColor = UnityPal.lightHeat;
                coolantMultiplier = 3.4f;
                rotateSpeed = 2.2f;
                recoilAmount = 1.5f;
                restitution = 0.09f;
                powerUse = 10.4f;
                shootSound = UnitySounds.higgsBosonShoot;
                cooldown = 0.008f;
                shootType = new RoundLaserBulletType(85f){{
                    length = 270f;
                    width = 5.8f;
                    hitSize = 13f;
                    drawSize = 460f;
                    shootEffect = smokeEffect = Fx.none;
                }};
            }

            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
            }
        };

        singularity = new PowerTurret("singularity"){
            {
                requirements(Category.turret, with(Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 20));
                size = 7;
                health = 9800;
                reloadTime = 220f;
                coolantMultiplier = 1.1f;
                shootCone = 30f;
                range = 310f;
                heatColor = UnityPal.lightHeat;
                rotateSpeed = 3.3f;
                recoilAmount = 6f;
                powerUse = 39.3f;
                cooldown = 0.012f;
                shootSound = UnitySounds.singularityShoot;
                shootType = UnityBullets.singularityEnergyBall;
            }

            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
            }
        };

        muon = new PowerTurret("muon"){ //Should it be animated? Since the animation in AC was disabled.
            {
                requirements(Category.turret, with(Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 25));
                size = 8;
                health = 9800;
                range = 310f;
                shots = 9;
                spread = 12f;
                reloadTime = 90f;
                coolantMultiplier = 1.9f;
                shootCone = 80f;
                powerUse = 18f;
                shootShake = 5f;
                recoilAmount = 8f;
                shootLength = size * tilesize / 2f - 8f;
                shootSound = UnitySounds.muonShoot;
                rotateSpeed = 1.9f;
                heatColor = UnityPal.lightHeat;
                cooldown = 0.009f;
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

            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
            }
        };

        ephemeron = new PowerTurret("ephemeron"){
            {
                requirements(Category.turret, with(Items.silicon, 290, UnityItems.luminum, 430, Items.titanium, 190, Items.thorium, 120, UnityItems.lightAlloy, 25));
                size = 8;
                health = 9800;
                range = 320f;
                reloadTime = 70f;
                coolantMultiplier = 1.9f;
                powerUse = 26f;
                shootShake = 2f;
                recoilAmount = 4f;
                shootSound = UnitySounds.ephemeronShoot;
                rotateSpeed = 1.9f;
                heatColor = UnityPal.lightHeat;
                cooldown = 0.009f;
                chargeTime = 80f;
                chargeBeginEffect = ChargeFx.ephmeronCharge;

                shootType = new EphemeronBulletType(7.7f, 10f){{
                    lifetime = 70f;
                    hitSize = 12f;
                    pierce = true;
                    collidesTiles = false;
                    scaleVelocity = true;
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

            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
            }
        };

        //endregion
        //region Light

        lightLamp = new LightSource("light-lamp"){{
            requirements(Category.crafting, with(Items.lead, 5, Items.metaglass, 10));

            lightProduction = 0.6f;
            consumes.power(1f);

            drawer = new DrawLightBlock();
        }};

        oilLamp = new LightSource("oil-lamp"){{
            requirements(Category.logic, with(Items.lead, 20, Items.metaglass, 20, Items.titanium, 15));

            size = 3;
            health = 240;
            lightProduction = 2f;

            consumes.power(1.8f);
            consumes.liquid(Liquids.oil, 0.1f);

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

        //endregion
        //region Defence

        metaglassWall = new LightWall("metaglass-wall"){{
            requirements(Category.defense, with(Items.lead, 6, Items.metaglass, 6));
            health = 350;
        }};

        metaglassWallLarge = new LightWall("metaglass-wall-large"){{
            requirements(Category.defense, with(Items.lead, 24, Items.metaglass, 24));

            size = 2;
            health = 1400;
        }};

        //endregion
        //region Crafting

        lightForge = new LightHoldGenericCrafter("light-forge"){{
            requirements(Category.crafting, with(Items.copper, 1));

            size = 4;
            outputItem = new ItemStack(UnityItems.lightAlloy, 3);

            consumes.items(with(Items.copper, 2, Items.silicon, 5, Items.plastanium, 2, UnityItems.luminum, 2));
            consumes.power(3.5f);

            drawer = new DrawSmelter(UnityPal.lightDark){{
                flameRadius = 7f;
                flameRadiusIn = 3.5f;
                flameRadiusMag = 3f;
                flameRadiusInMag = 1.8f;
            }};

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
    }
}
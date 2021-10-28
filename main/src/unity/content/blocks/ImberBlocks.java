package unity.content.blocks;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.content.effects.*;
import unity.gen.*;
import unity.graphics.*;
import unity.world.blocks.defense.turrets.*;
import unity.world.blocks.environment.*;
import unity.world.blocks.power.*;

import static arc.Core.*;
import static mindustry.type.ItemStack.*;

public class ImberBlocks implements ContentList{
    public static @FactionDef("imber")
    Block
    //environment
    oreImberium, electroTile,

    //turret
    orb, shockwire, current, plasma, electrobomb, shielder,

    //power
    powerPlant, absorber;

    //crafting
    public static @FactionDef("imber")
    @Merge(base = GenericCrafter.class, value = Stemc.class)
    Block sparkAlloyForge;

    @Override
    public void load(){
        //region Environment

        oreImberium = new UnityOreBlock(UnityItems.imberium){{
            oreScale = 23.77f;
            oreThreshold = 0.807f;
            oreDefault = false;
        }};

        electroTile = new Floor("electro-tile");

        //endregion
        //region Turret

        orb = new PowerTurret("orb"){{
            requirements(Category.turret, with(Items.copper, 55, Items.lead, 30, Items.graphite, 25, Items.silicon, 35, UnityItems.imberium, 20));
            size = 2;
            health = 480;
            range = 145f;
            reloadTime = 130f;
            coolantMultiplier = 2f;
            shootCone = 0.1f;
            shots = 1;
            inaccuracy = 12f;
            chargeTime = 65f;
            chargeEffects = 5;
            chargeMaxDelay = 25f;
            powerUse = 4.2069f;
            targetAir = false;
            shootType = UnityBullets.orb;
            shootSound = Sounds.laser;
            heatColor = Pal.turretHeat;
            shootEffect = ShootFx.orbShoot;
            smokeEffect = Fx.none;
            chargeEffect = UnityFx.orbCharge;
            chargeBeginEffect = UnityFx.orbChargeBegin;
        }};

        shockwire = new LaserTurret("shockwire"){{
            requirements(Category.turret, with(Items.copper, 150, Items.lead, 145, Items.titanium, 160, Items.silicon, 130, UnityItems.imberium, 70));
            size = 2;
            health = 860;
            range = 125f;
            reloadTime = 140f;
            coolantMultiplier = 2f;
            shootCone = 1f;
            inaccuracy = 0f;
            powerUse = 6.9420f;
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
            reloadTime = 120f;
            coolantMultiplier = 2;
            shootCone = 0.01f;
            inaccuracy = 0f;
            chargeTime = 60f;
            chargeEffects = 4;
            chargeMaxDelay = 260;
            powerUse = 6.8f;
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
            powerUse = 8.2f;
            shootType = UnityBullets.plasmaTriangle;
            shootSound = Sounds.shotgun;
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.5f && liquid.flammability <= 0.1f, 0.52f)).boost();
        }};

        electrobomb = new ItemTurret("electrobomb"){
            {
                requirements(Category.turret, with(Items.titanium, 360, Items.thorium, 630, Items.silicon, 240, UnityItems.sparkAlloy, 420));
                health = 3650;
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

        //endregion
        //region Power

        powerPlant = new PowerPlant("power-plant"){{
            requirements(Category.power, BuildVisibility.editorOnly, ItemStack.with(Items.copper, 1));

            powerProduction = 8.6f;
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

        //endregion
        //region Crafting

        sparkAlloyForge = new StemGenericCrafter("spark-alloy-forge"){{
            requirements(Category.crafting, with(Items.lead, 160, Items.graphite, 340, UnityItems.imberium, 270, Items.silicon, 250, Items.thorium, 120, Items.surgeAlloy, 100));

            outputItem = new ItemStack(UnityItems.sparkAlloy, 4);
            size = 4;
            craftTime = 160f;
            ambientSound = Sounds.machine;
            ambientSoundVolume = 0.6f;
            craftEffect = UnityFx.imberCircleSparkCraftingEffect;
            drawer = new DrawSmelter();

            consumes.power(2.6f);
            consumes.items(with(Items.surgeAlloy, 3, Items.titanium, 4, Items.silicon, 6, UnityItems.imberium, 3));

            update((StemGenericCrafterBuild e) -> {
                if(e.consValid()){
                    if(Mathf.chanceDelta(0.3f)){
                        UnityFx.imberSparkCraftingEffect.at(e.x, e.y, Mathf.random(360f));
                    }else if(Mathf.chanceDelta(0.02f)){
                        Lightning.create(e.team, UnityPal.imberColor, 5f, e.x, e.y, Mathf.random(360f), 5);
                    }
                }
            });
        }};

        //endregion
    }
}
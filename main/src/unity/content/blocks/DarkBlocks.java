package unity.content.blocks;

import arc.graphics.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.gen.*;
import unity.world.blocks.defense.turrets.*;
import unity.world.blocks.environment.*;

import static arc.Core.atlas;
import static mindustry.type.ItemStack.with;

public class DarkBlocks implements ContentList{
    public static @FactionDef("dark")
    Block
    //environment
    oreUmbrium,

    //turrets
    apparition, ghost, banshee, fallout, catastrophe, calamity, extinction,

    //defense
    darkWall, darkWallLarge;

    //crafting
    public static @FactionDef("dark")
    @Merge(base = GenericCrafter.class, value = Stemc.class)
    Block darkAlloyForge;

    @Override
    public void load(){
        //region Environment

        oreUmbrium = new UnityOreBlock(UnityItems.umbrium){{
            oreScale = 23.77f;
            oreThreshold = 0.813f;
            oreDefault = false;
        }};

        //endregion
        //region Turret

        apparition = new ItemTurret("apparition"){
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
                shootSound = Sounds.shootBig;
                alternate = true;
                recoilAmount = 3f;
                rotateSpeed = 4.5f;
                ammo(Items.graphite, UnityBullets.standardDenseLarge, Items.silicon, UnityBullets.standardHomingLarge, Items.pyratite, UnityBullets.standardIncendiaryLarge, Items.thorium, UnityBullets.standardThoriumLarge);
            }


            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
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
            shootSound = Sounds.shootBig;
            alternate = true;
            recoilAmount = 5.5f;
            rotateSpeed = 3.5f;
            spread = 21f;
            addBarrel(8f, 18.75f, 6f);
            ammo(Items.graphite, UnityBullets.standardDenseHeavy, Items.silicon, UnityBullets.standardHomingHeavy, Items.pyratite, UnityBullets.standardIncendiaryHeavy, Items.thorium, UnityBullets.standardThoriumHeavy);
            requirements(Category.turret, with(Items.copper, 1150, Items.graphite, 1420, Items.silicon, 960, Items.plastanium, 800, Items.thorium, 1230, UnityItems.darkAlloy, 380));
        }};

        banshee = new BarrelsItemTurret("banshee"){{
            size = 12;
            health = 22000;
            range = 370f;
            reloadTime = 12f;
            coolantMultiplier = 0.5f;
            restitution = 0.08f;
            inaccuracy = 3f;
            shots = 2;
            shootSound = Sounds.shootBig;
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

            @Override
            public void load(){
                super.load();
                baseRegion = atlas.find("unity-block-" + size);
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
            requirements(Category.turret, with(Items.copper, 1250, Items.lead, 1320, Items.graphite, 1100, Items.titanium, 1340, Items.surgeAlloy, 1240, Items.silicon, 1350, Items.thorium, 770, UnityItems.darkAlloy, 370));
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
            requirements(Category.turret, with(Items.copper, 2800, Items.lead, 2970, Items.graphite, 2475, Items.titanium, 3100, Items.surgeAlloy, 2790, Items.silicon, 3025, Items.thorium, 1750, UnityItems.darkAlloy, 1250));
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
            shootType = UnityBullets.extinctionLaser;
            consumes.add(new ConsumeLiquidFilter(liquid -> liquid.temperature <= 0.27f && liquid.flammability < 0.1f, 2.5f)).update(false);
        }};

        //endregion
        //region Crafting

        darkAlloyForge = new StemGenericCrafter("dark-alloy-forge"){{
            requirements(Category.crafting, with(Items.copper, 30, Items.lead, 25));

            outputItem = new ItemStack(UnityItems.darkAlloy, 3);
            craftTime = 140f;
            size = 4;
            ambientSound = Sounds.respawning;
            ambientSoundVolume = 0.6f;
            drawer = new DrawSmelter();

            consumes.items(with(Items.lead, 2, Items.silicon, 3, Items.blastCompound, 1, Items.phaseFabric, 1, UnityItems.umbrium, 2));
            consumes.power(3.2f);

            update((StemGenericCrafterBuild e) -> {
                if(e.consValid() && Mathf.chanceDelta(0.76f)){
                    UnityFx.craftingEffect.at(e.x, e.y, Mathf.random(360f));
                }
            });
        }};

        //endregion
        //region Defence

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
    }
}
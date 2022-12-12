package unity.content;

import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;
import unity.graphics.*;
import unity.mod.*;
import unity.world.blocks.*;
import unity.world.blocks.production.*;

import static unity.mod.FactionRegistry.register;

public final class EndBlocks{
    public static Block
            stasisSand, stasisStone,

            stasisSandWall, stasisStoneWall,

            wallOreOrsusite,

            infraredBore,
            endgame, coreCrypt, endConstructor;

    public static void load(){
        stasisSand = register(Faction.end, new Floor("stasis-sand"){{
            itemDrop = Items.sand;
        }});
        stasisStone = register(Faction.end, new Floor("stasis-stone"));

        stasisSandWall = register(Faction.end, new StaticWall("stasis-sand-wall"){{
            variants = 3;
        }});
        stasisStoneWall = register(Faction.end, new StaticWall("stasis-stone-wall"));

        wallOreOrsusite = register(Faction.end, new OreBlock("ore-wall-orsusite", EndItems.orsusite){{
            wallOre = true;
        }});

        infraredBore = register(Faction.end, new DeepBeamDrill("infrared-bore"){{
            requirements(Category.production, ItemStack.with(EndItems.orsusite, 40));
            alwaysUnlocked = true;
            drillTime = 190f;
            size = 2;
            range = 6;
            tier = 3;
            laserWidth = 2.5f;
            pulseIntensity = 0.25f;
            sparkColor = EndPal.endMid;
            boostHeatColor = EndPal.endLight;

            consumePower(0.2f);
            consumeLiquid(Liquids.hydrogen, 0.25f / 60f).boost();
        }});

        endgame = register(Faction.end, new EndGameTurret("endgame"));

        coreCrypt = register(Faction.end, new EndCoreBlock("core-crypt"){{
            requirements(Category.effect, BuildVisibility.editorOnly, ItemStack.with(Items.copper, 1000, Items.lead, 800));
            alwaysUnlocked = true;
            unitType = EndUnitTypes.constrict;

            isFirstTier = true;
            health = 44000;
            itemCapacity = 5000;
            size = 7;
            thrusterLength = 38/4f;
            armor = 35f;
            incinerateNonBuildable = true;

            unitCapModifier = 25;
            researchCostMultiplier = 0.2f;
        }});

        endConstructor = register(Faction.end, new EndConstructorBlock("end-constructor"){{
            requirements(Category.effect, ItemStack.with(EndItems.orsusite, 60, Items.lead, 10));
            alwaysUnlocked = true;

            health = 120;
            size = 1;
            armor = 10f;
            buildCostMultiplier = 3f;
        }});
    }
}

package unity.content;

import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;
import unity.mod.*;
import unity.world.blocks.*;
import unity.content.*;

import static unity.mod.FactionRegistry.register;

public final class EndBlocks{
    public static Block
            stasisSand, stasisStone,

            stasisSandWall, stasisStoneWall,

            wallOreOrsusite,

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

        endgame = register(Faction.end, new EndGameTurret("endgame"));

        coreCrypt = register(Faction.end, new EndCoreBlock("core-crypt"){{
            requirements(Category.effect, BuildVisibility.editorOnly, ItemStack.with(Items.copper, 1000, Items.lead, 800));
            alwaysUnlocked = true;
            unitType = EndUnitTypes.constrict;

            isFirstTier = true;
            health = 44000;
            itemCapacity = 5000;
            size = 7;
            thrusterLength = 48/4f;
            armor = 35f;
            incinerateNonBuildable = true;

            unitCapModifier = 25;
            researchCostMultiplier = 0.2f;
        }});

        endConstructor = register(Faction.end, new EndConstructorBlock("end-constructor"){{
            requirements(Category.effect, ItemStack.with(EndItems.orsusite, 40, Items.lead, 10));
            alwaysUnlocked = true;

            health = 120;
            size = 1;
            armor = 10f;
            buildCostMultiplier = 5f;
        }});
    }
}

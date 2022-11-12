package unity.content;

import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.meta.*;
import unity.world.blocks.*;

public final class EndBlocks{
    public static Block endgame,
    coreCrypt, endConstructor;

    public static void load(){
        endgame = new EndGameTurret("endgame");

        coreCrypt = new EndCoreBlock("core-crypt"){{
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
        }};

        endConstructor = new EndConstructorBlock("end-constructor"){{
            requirements(Category.effect, ItemStack.with(Items.copper, 10));
            alwaysUnlocked = true;

            health = 120;
            size = 1;
            armor = 10f;
        }};
    }
}

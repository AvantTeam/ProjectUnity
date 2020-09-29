package unity.content;

import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.environment.*;
import mindustry.type.*;
import mindustry.ctype.*;
import mindustry.content.*;
import unity.libraries.*;
import unity.libraries.Recipe.*;

import static unity.content.UnityItems.*;
import static mindustry.type.ItemStack.*;

public class UnityBlocks implements ContentList{
	public static Block
	
	//faction ores
	/*oreXenium, */oreUmbrium, oreLuminum, oreMonolite, oreImberium,
	
	//crafting
	multiTest1;
	
	@Override
	public void load(){
		oreUmbrium = new OreBlock(umbrium){{
			oreScale = 23.77f;
			oreThreshold = 0.813f;
			oreDefault = true;
		}};
		
		oreLuminum = new OreBlock(luminum){{
			oreScale = 23.77f;
			oreThreshold = 0.81f;
			oreDefault = true;
		}};
		
		oreMonolite = new OreBlock(monolite){{
			oreScale = 23.77f;
			oreThreshold = 0.807f;
			oreDefault = true;
		}};
		
		oreImberium = new OreBlock(imberium){{
			oreScale = 23.77f;
			oreThreshold = 0.807f;
			oreDefault = true;
		}};
		
		multiTest1 = new MultiCrafter("multi-test-1", new Recipe[]{
			// 1
			new Recipe(new InputContents(), new OutputContents(5.25f), 12),
			// 2
			new Recipe(
				new InputContents(with(Items.coal, 1, Items.sand, 1),
					new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
				new OutputContents(new LiquidStack[]{new LiquidStack(Liquids.slag, 5)}), 60),
			// 3
			new Recipe(
				new InputContents(with(Items.pyratite, 1, Items.blastCompound, 1),
					new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
				new OutputContents(with(Items.scrap, 1, Items.plastanium, 2, Items.sporePod, 2),
					new LiquidStack[]{new LiquidStack(Liquids.oil, 5)}),
				72),
			// 4
			new Recipe(new InputContents(with(Items.sand, 1)), new OutputContents(with(Items.silicon, 1)), 30),
			// 5
			new Recipe(
				new InputContents(with(Items.sand, 1, Items.lead, 2),
					new LiquidStack[]{new LiquidStack(Liquids.water, 5)}),
				new OutputContents(with(UnityItems.contagium, 1)), 12),
			// 6
			new Recipe(
				new InputContents(with(Items.coal, 1, Items.sand, 1),
					new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
				new OutputContents(with(Items.thorium, 1, Items.surgealloy, 1),
					new LiquidStack[]{new LiquidStack(Liquids.slag, 5)}),
				60),

		}){{
			requirements(Category.crafting, with(Items.copper, 10));
			size = 3;
		}};
	}
}

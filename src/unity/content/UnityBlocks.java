package unity.content;

import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.type.*;
import mindustry.ctype.*;
import mindustry.content.*;
import unity.blocks.*;
import unity.blocks.Recipe.*;
import unity.blocks.experience.*;

import static mindustry.type.ItemStack.*;

public class UnityBlocks implements ContentList{
	public static Block

	//faction ores
	/*oreXenium, */ oreUmbrium, oreLuminum, oreMonolite, oreImberium,

		//crafting
		multiTest1, multiTest2,
		//koruh-turret
		laserTurret, inferno;

	@Override
	public void load(){
		//ores
		oreUmbrium = new OreBlock(UnityItems.umbrium){
			{
				oreScale = 23.77f;
				oreThreshold = 0.813f;
				oreDefault = true;
			}
		};

		oreLuminum = new OreBlock(UnityItems.luminum){
			{
				oreScale = 23.77f;
				oreThreshold = 0.81f;
				oreDefault = true;
			}
		};

		oreMonolite = new OreBlock(UnityItems.monolite){
			{
				oreScale = 23.77f;
				oreThreshold = 0.807f;
				oreDefault = true;
			}
		};

		oreImberium = new OreBlock(UnityItems.imberium){
			{
				oreScale = 23.77f;
				oreThreshold = 0.807f;
				oreDefault = true;
			}
		};
		//crafting
		multiTest1 = new MultiCrafter("multi-test-1", 10, true){
			{
				requirements(Category.crafting, with(Items.copper, 10));
				size = 3;
				addRecipe(new InputContents(), new OutputContents(5.25f), 12);
				addRecipe(
					new InputContents(with(Items.coal, 1, Items.sand, 1),
						new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
					new OutputContents(new LiquidStack[]{new LiquidStack(Liquids.slag, 5)}), 60);
				addRecipe(
					new InputContents(with(Items.pyratite, 1, Items.blastCompound, 1),
						new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
					new OutputContents(with(Items.scrap, 1, Items.plastanium, 2, Items.sporePod, 2),
						new LiquidStack[]{new LiquidStack(Liquids.oil, 5)}),
					72);
				addRecipe(new InputContents(with(Items.sand, 1)), new OutputContents(with(Items.silicon, 1)), 30);
				addRecipe(
					new InputContents(with(Items.sand, 1, Items.lead, 2),
						new LiquidStack[]{new LiquidStack(Liquids.water, 5)}),
					new OutputContents(with(UnityItems.contagium, 1)), 12);
				addRecipe(
					new InputContents(with(Items.coal, 1, Items.sand, 1),
						new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
					new OutputContents(with(Items.thorium, 1, Items.surgealloy, 1),
						new LiquidStack[]{new LiquidStack(Liquids.slag, 5)}),
					60);
				addRecipe(
					new InputContents(with(Items.pyratite, 1, Items.blastCompound, 1),
						new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
					new OutputContents(with(Items.scrap, 1, Items.plastanium, 2, Items.sporePod, 2),
						new LiquidStack[]{new LiquidStack(Liquids.oil, 5)}),
					72);
				addRecipe(new InputContents(with(Items.sand, 1)), new OutputContents(with(Items.silicon, 1)), 30);
				addRecipe(
					new InputContents(with(Items.sand, 1, Items.lead, 2),
						new LiquidStack[]{new LiquidStack(Liquids.water, 5)}),
					new OutputContents(with(UnityItems.contagium, 1)), 12);
				addRecipe(
					new InputContents(with(Items.coal, 1, Items.sand, 1),
						new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
					new OutputContents(with(Items.thorium, 1, Items.surgealloy, 1),
						new LiquidStack[]{new LiquidStack(Liquids.slag, 5), new LiquidStack(Liquids.oil, 5)}),
					60);
			}
		};
		multiTest2 = new MultiCrafter("multi-test-2", 4){
			{
				requirements(Category.crafting, with(Items.copper, 10));
				size = 3;
				addRecipe(new InputContents(with(Items.sand, 1, Items.lead, 1)), new OutputContents(), 12f);
				addRecipe(new InputContents(with(Items.coal, 1, Items.sand, 1)),
					new OutputContents(with(Items.thorium, 1, Items.surgealloy, 2), 10), 60f);
				addRecipe(new InputContents(with(Items.pyratite, 1, Items.blastCompound, 1)),
					new OutputContents(with(Items.scrap, 1, Items.plastanium, 2, Items.sporePod, 2)), 72f);
				addRecipe(new InputContents(with(Items.sand, 1), 15), new OutputContents(with(Items.silicon, 1), 10),
					30);
			}
		};
		//koruh-turret
		laserTurret = new ExpPowerTurret("laser-turret", 10){
			{
				requirements(Category.turret, with(Items.copper, 160, Items.lead, 110, Items.silicon, 90));
				size = 2;
				health = 800;
				range = 140f;
				coolantMultiplier = 2f;
				shootCone = 1f;
				inaccuracy = 0f;
				powerUse = 7f;
				shootType = UnityBullets.laser;
				addExpField("linear", "reloadTime", 35, -2);
				addExpField("bool", "targetAir", 0, 5);
			}
		};
		inferno = new ExpItemTurret("inferno", 10){
			{
				requirements(Category.turret,
					with(Items.copper, 150, Items.lead, 165, Items.graphite, 120, Items.silicon, 130));
				ammo(Items.coal, UnityBullets.coalBlaze, Items.pyratite, UnityBullets.pyraBlaze);
				size = 3;
				health = 1500;
				range = 80f;
				reloadTime = 10f;
				shootCone = 5f;
				addExpField("exp", "useless", 0, 2);
			}
		};
	}
}

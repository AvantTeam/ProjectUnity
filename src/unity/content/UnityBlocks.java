package unity.content;

import arc.util.*;
import arc.util.Log.*;
import arc.util.io.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ctype.*;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.game.Team;
import unity.blocks.*;
import unity.blocks.Recipe.*;

import static arc.Core.*;
import static mindustry.type.ItemStack.*;

public class UnityBlocks implements ContentList{
	public static Block

	//faction ores
	/*oreXenium, */ oreUmbrium, oreLuminum, oreMonolite, oreImberium,

		//crafting
		multiTest1, multiTest2,
		//koruh-turret
		laserTurret, inferno;
	//fuck
	public static final ExpBlockModule laserExp = new ExpBlockModule(10), infernoExp = new ExpBlockModule(10);

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
		laserTurret = new PowerTurret("laser-turret"){

			@Override
			public void setBars(){
				super.setBars();
				laserExp.customSetBars();
			}

			@Override
			protected void initBuilding(){
				buildType = () -> {
					Building ret = new PowerTurret.PowerTurretBuild(){
						private ExpBlockModule.ExpBuildModule exp;

						@Override
						public void add(){
							super.add();
							exp = laserExp.forBars.get(id);
						}

						@Override
						public void remove(){
							super.remove();
							exp = null;
							if (laserExp.forBars.containsKey(id)) laserExp.forBars.remove(id);
						}

						@Override
						public void updateTile(){
							if (exp == null) return;
							exp.setExpStats();
							if (laserExp.hasCustomUpdate) exp.customUpdate();
							else super.updateTile();
						}

						@Override
						public void write(Writes write){
							super.write(write);
							write.i(exp.totalExp());
							exp.customWrite(write);
						}

						@Override
						public void read(Reads read, byte revision){
							super.read(read, revision);
							exp.setExp(read.i());
							exp.customRead(read, revision);
						}
					};
					laserExp.new ExpBuildModule(ret);
					return ret;
				};
			}

			{
				requirements(Category.turret, with(Items.copper, 160, Items.lead, 110, Items.silicon, 90));
				size = 2;
				health = 800;
				range = 140f;
				reloadTime = 30f;
				coolantMultiplier = 2f;
				shootCone = 1f;
				inaccuracy = 0f;
				powerUse = 7f;
				targetAir = false;
				shootType = UnityBullets.laser;
				laserExp.addBlock(this);
				laserExp.addExpField("linear", "reloadTime", 35, -2);
				laserExp.addExpField("bool", "targetAir", 0, 5);
			}
		};
		inferno = new ItemTurret("inferno"){
			@Override
			public void setBars(){
				super.setBars();
				infernoExp.customSetBars();
			}

			@Override
			protected void initBuilding(){
				buildType = () -> {
					Building ret = new ItemTurret.ItemTurretBuild(){
						private ExpBlockModule.ExpBuildModule exp;

						@Override
						public void add(){
							super.add();
							exp = infernoExp.forBars.get(id);
						}

						@Override
						public void remove(){
							super.remove();
							exp = null;
							if (infernoExp.forBars.containsKey(id)) infernoExp.forBars.remove(id);
						}

						@Override
						public void updateTile(){
							if (exp == null) return;
							exp.setExpStats();
							if (infernoExp.hasCustomUpdate) exp.customUpdate();
							else super.updateTile();
						}

						@Override
						public void write(Writes write){
							super.write(write);
							write.i(exp.totalExp());
							exp.customWrite(write);
						}

						@Override
						public void read(Reads read, byte revision){
							super.read(read, revision);
							exp.setExp(read.i());
							exp.customRead(read, revision);
						}
					};
					infernoExp.new ExpBuildModule(ret);
					return ret;
				};
			}

			{
				requirements(Category.turret,
					with(Items.copper, 150, Items.lead, 165, Items.graphite, 120, Items.silicon, 130));
				ammo(Items.coal, UnityBullets.coalBlaze, Items.pyratite, UnityBullets.pyraBlaze);
				size = 3;
				health = 1500;
				range = 80f;
				reloadTime = 10f;
				shootCone = 5f;
				infernoExp.addBlock(this);
				infernoExp.addExpField("exp", "useless", 0, 2);
			}
		};
	}
}

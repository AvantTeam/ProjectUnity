package unity.content;

import arc.util.*;
import arc.util.Log.*;
import arc.util.io.*;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.entities.Damage;
import mindustry.entities.bullet.*;
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
		//turret
		laser;

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
		//turret
		laser = new PowerTurret("laser-turret"){
			public final ExpBlockModule expModule = new ExpBlockModule(this, 10);

			@Override
			public void setBars(){
				super.setBars();
				expModule.customSetBars();
			}

			@Override
			protected void initBuilding(){
				buildType = () -> {
					Building ret = new PowerTurret.PowerTurretBuild(){
						private ExpBlockModule.ExpBuildModule exp;

						@Override
						public void add(){
							super.add();
							exp = expModule.forBars.get(id);
						}

						@Override
						public void remove(){
							super.remove();
							exp = null;
							if (expModule.forBars.containsKey(id)) expModule.forBars.remove(id);
						}

						@Override
						public void updateTile(){
							if (exp == null) return;
							exp.setExpStats();
							if (expModule.hasCustomUpdate) exp.customUpdate();
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
					expModule.new ExpBuildModule(ret);
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
				shootType = new BulletType(0.01f, 30f){
					float length = 150f;
					float width = 0.7f;
					Color levelColor;
					TextureRegion laserRegion, laserEndRegion;

					@Override
					public void load(){
						laserRegion = atlas.find("laser");
						laserEndRegion = atlas.find("laser-end");
					}

					@Override
					public void init(Bullet b){
						if (b == null) return;
						Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), length);
						b.data = target;
						ExpBlockModule.ExpBuildModule exp = expModule.forBars.get(b.owner.id());
						int lvl = expModule.getLevel(exp.totalExp());
						b.damage(damage + lvl * 10f);
						levelColor = Tmp.c1.set(Color.white).lerp(Pal.lancerLaser, lvl / 10f);
						if (target instanceof Hitboxc){
							Hitboxc hit = (Hitboxc) target;
							hit.collision(b, hit.x(), hit.y());
							b.collision(hit, hit.x(), hit.y());
							exp.incExp(2);
						}else if (target instanceof Building){
							Building tile = (Building) target;
							if (tile.collide(b)){
								tile.collision(b);
								hit(b, tile.x, tile.y);
								exp.incExp(2);
							}
						}else b.data = new Vec2().trns(b.rotation(), length).add(b.x, b.y);
					}

					@Override
					public float range(){ return length; }

					@Override
					public void draw(Bullet b){
						if (b.data instanceof Position){
							Tmp.v1.set((Position) b.data);
							Draw.color(levelColor);
							Drawf.laser(b.team, laserRegion, laserEndRegion, b.x, b.y, Tmp.v1.x, Tmp.v1.y,
								width * b.fout());
							Draw.reset();
							Drawf.light(Team.derelict, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 15f * b.fout() + 5f,
								levelColor, 0.6f);
						}
					}

					{
						lifetime = 18f;
						despawnEffect = Fx.none;
						pierce = true;
						hitSize = 0f;
						status = StatusEffects.shocked;
						statusDuration = 3 * 60f;
						hittable = false;
						hitEffect = Fx.hitLiquid;
					}
				};
				expModule.addExpField("linear", "reloadTime", 35, -2);
				expModule.addExpField("bool", "targetAir", 0, 5);
			}
		};
	}
}

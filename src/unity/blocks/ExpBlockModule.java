package unity.blocks;

import java.util.EnumMap;
import arc.func.*;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.struct.*;
import arc.math.Mathf;
import arc.util.Tmp;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.content.Fx;
import mindustry.graphics.Pal;
import mindustry.entities.Effect;
import mindustry.world.Block;
import mindustry.ui.Bar;

import static arc.Core.*;

public class ExpBlockModule{
	protected final int maxLevel, maxExp;
	protected Color level0Color = Pal.accent, levelMaxColor = Color.valueOf("fff4cc"),
		exp0Color = Color.valueOf("84ff00"), expMaxColor = Color.valueOf("90ff00");
	protected final EnumMap<ExpType, OrderedSet> expFields = new EnumMap(ExpType.class);
	protected boolean hasLevelEffect = true;
	protected Effect levelUpFx = Fx.upgradeCore;
	protected Sound levelUpSound = Sounds.message;
	protected final Block block;
	public final IntMap<ExpBuildModule> forBars = new IntMap();
	public final boolean hasCustomUpdate;
	{
		for (ExpType type : ExpType.values()){
			expFields.put(type, new OrderedSet<ExpField>(4));
		}
	}

	public ExpBlockModule(Block block, int maxLevel, boolean hasCustomUpdate){
		this.block = block;
		this.maxLevel = maxLevel;
		this.hasCustomUpdate = hasCustomUpdate;
		maxExp = getRequiredExp(maxLevel);
	}

	public ExpBlockModule(Block block, boolean hasCustomUpdate){ this(block, 20, hasCustomUpdate); }

	public ExpBlockModule(Block block, int maxLevel){ this(block, maxLevel, false); }

	public ExpBlockModule(Block block){ this(block, 20); }

	public int getLevel(int exp){ return Math.min(Mathf.floorPositive(Mathf.sqrt(exp * 0.1f)), maxLevel); }

	protected int getRequiredExp(int lvl){ return lvl * lvl * 10; }

	public void addExpField(String expType, String field, int start, int intensity){
		expFields.get(ExpType.valueOf(expType)).add(new ExpField(field, start, intensity));
	}

	protected float getLvlf(int exp){
		int lvl = getLevel(exp);
		if (lvl >= maxLevel) return 1f;
		int last = getRequiredExp(lvl);
		int next = getRequiredExp(lvl + 1);
		return ((float) (exp - last)) / (next - last);
	}

	public void customSetBars(){
		block.bars.add("level", build -> {
			ExpBuildModule expBuild = forBars.get(build.id);
			return new Bar(() -> bundle.get("explib.level") + " " + getLevel(expBuild.totalExp()),
				() -> Tmp.c1.set(exp0Color).lerp(expMaxColor, getLvlf(expBuild.totalExp()) / (float) maxLevel), () -> {
					return getLevel(expBuild.totalExp()) / (float) maxLevel;
				});
		});
		block.bars.add("exp", build -> {
			ExpBuildModule expBuild = forBars.get(build.id);
			return new Bar(() -> expBuild.totalExp() < maxExp ? bundle.get("explib.level") : bundle.get("explib.max"),
				() -> Tmp.c1.set(exp0Color).lerp(expMaxColor, getLvlf(expBuild.totalExp())),
				() -> getLvlf(expBuild.totalExp()));
		});
	}

	protected class ExpField{
		//private final ExpType expType;
		private final String field;
		private final int start;
		private final int intensity;

		protected ExpField(/*String expType,*/ String field, int start, int intensity){
			//this.expType = ExpType.valueOf(expType);
			this.field = field;
			this.start = start;
			this.intensity = intensity;
		}
	}

	protected enum ExpType{
		linear, exp, root, bool;
	}

	public class ExpBuildModule{
		private int exp = 0;
		protected final Building build;

		public ExpBuildModule(Building build){
			forBars.put(build.id, this);
			this.build = build;
		}

		public void setExpStats(){
			int lvl = getLevel(exp);
			Class<?> current = block.getClass();
			Seq<ExpField> linear = expFields.get(ExpType.linear).orderedItems(),
				expo = expFields.get(ExpType.exp).orderedItems(), root = expFields.get(ExpType.root).orderedItems(),
				bool = expFields.get(ExpType.bool).orderedItems();
			linear.each(f -> {
				//fuck
				try{
					current.getField(f.field).set(block, Math.max(f.start + f.intensity * lvl, 0));
				}catch (Exception e){

				}
			});
			expo.each(f -> {
				try{
					current.getField(f.field).set(block, Math.max(f.start + Mathf.pow(f.intensity, lvl), 0));
				}catch (Exception e){

				}
			});
			root.each(f -> {
				try{
					current.getField(f.field).set(block, Math.max(f.start + Mathf.sqrt(f.intensity * lvl), 0));
				}catch (Exception e){

				}
			});
			bool.each(f -> {
				try{
					current.getField(f.field).set(block, f.start != 0 ? lvl < f.intensity : lvl >= f.intensity);
				}catch (Exception e){

				}
			});
		}

		public int totalExp(){ return exp; }

		public void setExp(int a){ exp = a; }

		public void incExp(int a){
			if (exp == maxExp) return;
			exp += a;
			if (exp > maxExp) exp = maxExp;
			if (!hasLevelEffect) return;
			if (getLevel(exp - a) != getLevel(exp)){
				levelUpFx.at(build.x, build.y, block.size);
				levelUpSound.at(build.x, build.y);
				levelUp(getLevel(exp));
			}
		}

		protected void levelUp(int lvl){

		}

		public void customUpdate(){

		}

		public void customWrite(Writes write){

		}

		public void customRead(Reads read, byte revision){

		}
	}
}

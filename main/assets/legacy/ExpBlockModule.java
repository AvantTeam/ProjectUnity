//package unity.blocks.experience;

import java.util.EnumMap;
import arc.func.*;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.struct.*;
import arc.math.Mathf;
import arc.util.*;
import arc.util.Log.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.content.Fx;
import mindustry.graphics.Pal;
import mindustry.entities.Effect;
import mindustry.world.Block;
import mindustry.world.meta.*;
import mindustry.ui.Bar;

import static arc.Core.*;
import static mindustry.Vars.*;

//|legacy|this way is flexible but code becomes fucking messy. Otherwise, class way is neat but I have to create so many twigs. 
public class ExpBlockModule{
	protected final int maxLevel, maxExp;
	protected Color level0Color = Pal.accent, levelMaxColor = Color.valueOf("fff4cc"),
		exp0Color = Color.valueOf("84ff00"), expMaxColor = Color.valueOf("90ff00");
	protected final EnumMap<ExpType, ObjectSet<ExpField>> expFields = new EnumMap(ExpType.class);
	protected boolean hasLevelEffect = true;
	protected Effect levelUpFx = Fx.upgradeCore;
	protected Sound levelUpSound = Sounds.message;
	protected Block block;
	public final IntMap<ExpBuildModule> forBars = new IntMap();
	public final boolean hasCustomUpdate;
	public static final ObjectMap<String, BlockStat> forStats = new ObjectMap();
	static{
		//Turret stats
		forStats.put("range", BlockStat.shootRange);
		forStats.put("inaccuracy", BlockStat.inaccuracy);
		forStats.put("reloadTime", BlockStat.reload);
		forStats.put("targetAir", BlockStat.targetsAir);
		forStats.put("targetGround", BlockStat.targetsGround);
	}
	{
		for (ExpType type : ExpType.values()){
			expFields.put(type, new ObjectSet<ExpField>(4));
		}
	}

	public ExpBlockModule(int maxLevel, boolean hasCustomUpdate){
		this.maxLevel = maxLevel;
		this.hasCustomUpdate = hasCustomUpdate;
		maxExp = getRequiredExp(maxLevel);
	}

	public ExpBlockModule(boolean hasCustomUpdate){ this(20, hasCustomUpdate); }

	public ExpBlockModule(int maxLevel){ this(maxLevel, false); }

	public void addBlock(Block block){ this.block = block; }

	public int getLevel(int exp){ return Math.min(Mathf.floorPositive(Mathf.sqrt(exp * 0.1f)), maxLevel); }

	protected int getRequiredExp(int lvl){ return lvl * lvl * 10; }

	public void addExpField(String expType, String field, int start, int intensity){
		expFields.get(ExpType.valueOf(expType)).add(new ExpField(field, start, intensity));
		try{
			block.getClass().getField(field).set(block, ExpType.valueOf(expType) == ExpType.bool ? start > 0 : start);
		}catch (Exception e){
			//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
		}
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

	public boolean isNumerator(BlockStat stat){ return stat == BlockStat.inaccuracy || stat == BlockStat.shootRange; }

	public void customSetStats(){
		BlockStats stats = block.stats;
		expFields.get(ExpType.linear).each(f -> {
			BlockStat temp = forStats.get(f.field);
			if (temp != null){
				if (isNumerator(temp)) stats.add(temp, bundle.get("explib.linear.numer"), f.intensity > 0 ? "+" : "",
					100f * f.intensity / (float) f.start);
				else stats.add(temp, bundle.get("explib.linear.denomin"), f.start, f.intensity > 0 ? "+" : "", f.start,
					f.intensity);
			}
		});
		expFields.get(ExpType.exp).each(f -> {
			BlockStat temp = forStats.get(f.field);
			if (temp != null){
				if (isNumerator(temp)) stats.add(temp, bundle.get("explib.expo.numer"), f.intensity, f.start);
				else stats.add(temp, bundle.get("explib.expo.denomin"), f.start, f.start, f.intensity);
			}
		});
		expFields.get(ExpType.root).each(f -> {
			BlockStat temp = forStats.get(f.field);
			if (temp != null){
				if (isNumerator(temp)) stats.add(temp, bundle.get("explib.root.numer"), f.intensity, f.start);
				else stats.add(temp, bundle.get("explib.root.denomin"), f.start, f.start, f.intensity);
			}
		});
		expFields.get(ExpType.bool).each(f -> {
			BlockStat temp = forStats.get(f.field);
			if (temp != null) stats.add(temp, bundle.get("explib.bool"), f.intensity, f.start <= 0);
		});
	}

	protected class ExpField{
		private final String field;
		private final int start;
		private final int intensity;

		protected ExpField(String field, int start, int intensity){
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
			expFields.get(ExpType.linear).each(f -> {
				//fuck
				try{
					current.getField(f.field).set(block, Math.max(f.start + f.intensity * lvl, 0));
				}catch (Exception e){
					//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
				}
			});
			expFields.get(ExpType.exp).each(f -> {
				try{
					current.getField(f.field).set(block, Math.max(f.start + Mathf.pow(f.intensity, lvl), 0));
				}catch (Exception e){
					//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
				}
			});
			expFields.get(ExpType.root).each(f -> {
				try{
					current.getField(f.field).set(block, Math.max(f.start + Mathf.sqrt(f.intensity * lvl), 0));
				}catch (Exception e){
					//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
				}
			});
			expFields.get(ExpType.bool).each(f -> {
				try{
					current.getField(f.field).set(block, f.start > 0 ? lvl < f.intensity : lvl >= f.intensity);
				}catch (Exception e){
					//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
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

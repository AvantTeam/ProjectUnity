package unity.blocks.experience;

import java.lang.reflect.*;
import java.util.EnumMap;
import arc.util.Tmp;
import arc.util.io.*;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.struct.ObjectSet;
import arc.math.Mathf;
import mindustry.gen.*;
import mindustry.content.Fx;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.entities.Effect;
import mindustry.world.meta.BlockStat;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import unity.meta.ExpType;

import static arc.Core.*;

public class ExpPowerTurret extends PowerTurret implements ExpBlockBase{
	protected final int maxLevel, maxExp;
	protected Color level0Color = Pal.accent, levelMaxColor = Color.valueOf("fff4cc"),
		exp0Color = Color.valueOf("84ff00"), expMaxColor = Color.valueOf("90ff00");
	protected final EnumMap<ExpType, ObjectSet<ExpField>> expFields = new EnumMap(ExpType.class);
	protected boolean hasLevelEffect = true;
	protected Effect levelUpFx = Fx.upgradeCore;
	protected Sound levelUpSound = Sounds.message;
	public final boolean hasCustomUpdate;
	{
		for (ExpType type : ExpType.values()) expFields.put(type, new ObjectSet(4));
	}

	public ExpPowerTurret(String name, int maxLevel, boolean hasCustomUpdate){
		super(name);
		this.maxLevel = maxLevel;
		this.hasCustomUpdate = hasCustomUpdate;
		maxExp = getRequiredExp(maxLevel);
	}

	public ExpPowerTurret(String name, boolean hasCustomUpdate){ this(name, 20, hasCustomUpdate); }

	public ExpPowerTurret(String name, int maxLevel){ this(name, maxLevel, false); }

	public ExpPowerTurret(String name){ this(name, 20, false); }

	@Override
	public void setBars(){
		super.setBars();
		bars.add("level",
			(ExpPowerTurretBuild build) -> new Bar(() -> bundle.get("explib.level") + " " + getLevel(build.totalExp()),
				() -> Tmp.c1.set(exp0Color).lerp(expMaxColor, getLvlf(build.totalExp()) / maxLevel),
				() -> getLevel(build.totalExp()) / (float) maxLevel));
		bars.add("exp",
			(ExpPowerTurretBuild build) -> new Bar(
				() -> build.totalExp() < maxExp ? bundle.get("explib.level") : bundle.get("explib.max"),
				() -> Tmp.c1.set(exp0Color).lerp(expMaxColor, getLvlf(build.totalExp())),
				() -> getLvlf(build.totalExp())));
	}

	@Override
	public void setStats(){
		super.setStats();
		expFields.get(ExpType.linear).each(f -> {
			BlockStat temp = forStats.get(f.name);
			if (temp != null){
				if (isNumerator(temp)) stats.add(temp, bundle.get("explib.linear.numer"), f.intensity > 0 ? "+" : "",
					100f * f.intensity / (float) f.start);
				else stats.add(temp, bundle.get("explib.linear.denomin"), f.start, f.intensity > 0 ? "+" : "", f.start,
					f.intensity);
			}
		});
		expFields.get(ExpType.exp).each(f -> {
			BlockStat temp = forStats.get(f.name);
			if (temp != null){
				if (isNumerator(temp)) stats.add(temp, bundle.get("explib.expo.numer"), f.intensity, f.start);
				else stats.add(temp, bundle.get("explib.expo.denomin"), f.start, f.start, f.intensity);
			}
		});
		expFields.get(ExpType.root).each(f -> {
			BlockStat temp = forStats.get(f.name);
			if (temp != null){
				if (isNumerator(temp)) stats.add(temp, bundle.get("explib.root.numer"), f.intensity, f.start);
				else stats.add(temp, bundle.get("explib.root.denomin"), f.start, f.start, f.intensity);
			}
		});
		expFields.get(ExpType.bool).each(f -> {
			BlockStat temp = forStats.get(f.name);
			if (temp != null) stats.add(temp, bundle.get("explib.bool"), f.intensity, f.start <= 0);
		});
	}

	@Override
	public void addExpField(String expType, String field, int start, int intensity){
		try{
			Field blockField = getClass().getField(field);
			expFields.get(ExpType.valueOf(expType)).add(new ExpField(field, blockField, start, intensity));
			blockField.set(this, ExpType.valueOf(expType) == ExpType.bool ? start > 0 : start);
		}catch (Exception e){
			//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
		}
	}

	@Override
	public int getLevel(int exp){ return Math.min(Mathf.floorPositive(Mathf.sqrt(exp * 0.1f)), maxLevel); }

	@Override
	public float getLvlf(int exp){
		int lvl = getLevel(exp);
		if (lvl >= maxLevel) return 1f;
		int last = getRequiredExp(lvl);
		int next = getRequiredExp(lvl + 1);
		return ((float) (exp - last)) / (next - last);
	}

	public class ExpPowerTurretBuild extends PowerTurretBuild implements ExpBuildBase{
		private int exp = 0;

		@Override
		public void updateTile(){
			setExpStats();
			if (hasCustomUpdate) customUpdate();
			else super.updateTile();
		}

		@Override
		public void write(Writes write){
			super.write(write);
			write.i(exp);
			customWrite(write);
		}

		@Override
		public void read(Reads read, byte revision){
			super.read(read, revision);
			exp = read.i();
			customRead(read, revision);
		}

		@Override
		public void setExpStats(){
			int lvl = getLevel(exp);
			expFields.get(ExpType.linear).each(f -> {
				//fuck
				try{
					f.field.set(block, Math.max(f.start + f.intensity * lvl, 0));
				}catch (Exception e){
					//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
				}
			});
			expFields.get(ExpType.exp).each(f -> {
				try{
					f.field.set(block, Math.max(f.start + Mathf.pow(f.intensity, lvl), 0));
				}catch (Exception e){
					//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
				}
			});
			expFields.get(ExpType.root).each(f -> {
				try{
					f.field.set(block, Math.max(f.start + Mathf.sqrt(f.intensity * lvl), 0));
				}catch (Exception e){
					//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
				}
			});
			expFields.get(ExpType.bool).each(f -> {
				try{
					f.field.set(block, f.start > 0 ? lvl < f.intensity : lvl >= f.intensity);
				}catch (Exception e){
					//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
				}
			});
		}

		@Override
		public int totalExp(){ return exp; }

		@Override
		public void setExp(int a){ exp = a; }

		@Override
		public void incExp(int a){
			if (exp == maxExp) return;
			exp += a;
			if (exp > maxExp) exp = maxExp;
			if (!hasLevelEffect) return;
			int lvl = getLevel(exp);
			if (getLevel(exp - a) != lvl){
				levelUpFx.at(x, y, size);
				levelUpSound.at(x, y);
				levelUp(lvl);
			}
		}
	}
}

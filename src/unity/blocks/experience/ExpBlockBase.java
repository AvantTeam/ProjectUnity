package unity.blocks.experience;

import java.lang.reflect.Field;
import java.util.EnumMap;
import arc.func.*;
import arc.struct.*;
import arc.util.Tmp;
import arc.math.Mathf;
import arc.graphics.Color;
import mindustry.ui.Bar;
import mindustry.graphics.Pal;
import mindustry.world.meta.*;
import unity.blocks.experience.ExpPowerTurret.ExpPowerTurretBuild;
import unity.meta.ExpType;

import static arc.Core.*;

public interface ExpBlockBase{
	public static final ObjectMap<String, BlockStat> forStats = ObjectMap.of(/*Turret stats*/"range",
		BlockStat.shootRange, "inaccuracy", BlockStat.inaccuracy, "reloadTime", BlockStat.reload, "targetAir",
		BlockStat.targetsAir, "targetGround", BlockStat.targetsGround);
	Color defaultLevel0Color = Pal.accent, defaultLevelMaxColor = Color.valueOf("fff4cc"),
		defaultExp0Color = Color.valueOf("84ff00"), defaultExpMaxColor = Color.valueOf("90ff00");

	int getMaxLevel();

	default Color getLerpColor(Color from, Color to, float progress){ return Tmp.c1.set(from).lerp(to, progress); }

	default void expSetBars(BlockBars bars){
		bars.add("level",
			(ExpPowerTurretBuild build) -> new Bar(() -> bundle.get("explib.level") + " " + build.getLevel(),
				() -> getLerpColor(defaultLevel0Color, defaultLevelMaxColor, build.getLevel() / (float) getMaxLevel()),
				() -> build.getLevel() / (float) getMaxLevel()));
		bars.add("exp",
			(ExpPowerTurretBuild build) -> new Bar(
				() -> build.getLevel() < getMaxLevel() ? bundle.get("explib.exp") : bundle.get("explib.max"),
				() -> getLerpColor(defaultExp0Color, defaultExpMaxColor, build.getLvlf()), () -> build.getLvlf()));
	}

	default void expSetStats(BlockStats stats){
		getExpFields().get(ExpType.linear).each(f -> {
			BlockStat temp = forStats.get(f.name);
			if (temp != null){
				if (isNumerator(temp)) stats.add(temp, bundle.get("explib.linear.numer"), f.intensity > 0 ? "+" : "",
					100f * f.intensity / (float) f.start);
				else stats.add(temp, bundle.get("explib.linear.denomin"), f.start, f.intensity > 0 ? "+" : "", f.start,
					f.intensity);
			}
		});
		getExpFields().get(ExpType.exp).each(f -> {
			BlockStat temp = forStats.get(f.name);
			if (temp != null){
				if (isNumerator(temp)) stats.add(temp, bundle.get("explib.expo.numer"), f.intensity, f.start);
				else stats.add(temp, bundle.get("explib.expo.denomin"), f.start, f.start, f.intensity);
			}
		});
		getExpFields().get(ExpType.root).each(f -> {
			BlockStat temp = forStats.get(f.name);
			if (temp != null){
				if (isNumerator(temp)) stats.add(temp, bundle.get("explib.root.numer"), f.intensity, f.start);
				else stats.add(temp, bundle.get("explib.root.denomin"), f.start, f.start, f.intensity);
			}
		});
		getExpFields().get(ExpType.bool).each(f -> {
			BlockStat temp = forStats.get(f.name);
			if (temp != null) stats.add(temp, bundle.get("explib.bool"), f.intensity, f.start <= 0);
		});
	}

	EnumMap<ExpType, ObjectSet<ExpField>> getExpFields();

	default void addExpField(String expType, String field, int start, int intensity){
		try{
			Field blockField = getClass().getField(field);
			getExpFields().get(ExpType.valueOf(expType)).add(new ExpField(field, blockField, start, intensity));
			blockField.set(this, ExpType.valueOf(expType) == ExpType.bool ? start > 0 : start);
		}catch (Exception e){
			//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
		}
	}

	default boolean isNumerator(BlockStat stat){ return stat == BlockStat.inaccuracy || stat == BlockStat.shootRange; }
}
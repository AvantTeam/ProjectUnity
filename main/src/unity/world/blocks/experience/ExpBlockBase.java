package unity.world.blocks.experience;

import java.util.EnumMap;
import java.lang.reflect.Field;
import arc.struct.*;
import arc.util.Tmp;
import arc.graphics.Color;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.meta.*;
import unity.world.meta.ExpType;

import static arc.Core.*;

public interface ExpBlockBase{
    /** Turret BlockStatus */
    public static final ObjectMap<String, Stat> forStats = ObjectMap.of("range", Stat.shootRange, "inaccuracy", Stat.inaccuracy, "reloadTime", Stat.reload, "targetAir", Stat.targetsAir, "targetGround", Stat.targetsGround);
    Color defaultLevel0Color = Pal.accent, defaultLevelMaxColor = Color.valueOf("fff4cc"), defaultExp0Color = Color.valueOf("84ff00"), defaultExpMaxColor = Color.valueOf("90ff00");

    int getMaxLevel();

    default Color getLerpColor(Color from, Color to, float progress){
        return Tmp.c1.set(from).lerp(to, progress);
    }

    default Bar levelBar(ExpBuildBase build){
        return new Bar(() -> bundle.get("explib.level") + " " + build.getLevel(), () -> getLerpColor(defaultLevel0Color, defaultLevelMaxColor, build.getLevel() / (float) getMaxLevel()), () -> build.getLevel() / (float) getMaxLevel());
    }

    default Bar expBar(ExpBuildBase build){
        return new Bar(() -> build.getLevel() < getMaxLevel() ? bundle.get("explib.exp") : bundle.get("explib.max"), () -> getLerpColor(defaultExp0Color, defaultExpMaxColor, build.getLvlf()), () -> build.getLvlf());
    }

    default void expSetStats(Stats stats){
        getExpFields().get(ExpType.linear).each(f -> {
            Stat temp = forStats.get(f.name);
            if(temp != null){
                if(isNumerator(temp)) stats.add(temp, bundle.get("explib.linear.numer"), f.intensity[0] > 0 ? "+" : "", 100f * f.intensity[0] / f.start);
                else stats.add(temp, bundle.get("explib.linear.denomin"), f.start, f.intensity[0] > 0 ? "+" : "", f.start, f.intensity[0]);
            }
        });
        getExpFields().get(ExpType.exp).each(f -> {
            Stat temp = forStats.get(f.name);
            if(temp != null){
                if(isNumerator(temp)) stats.add(temp, bundle.get("explib.expo.numer"), f.intensity[0], f.start);
                else stats.add(temp, bundle.get("explib.expo.denomin"), f.start, f.start, f.intensity[0]);
            }
        });
        getExpFields().get(ExpType.root).each(f -> {
            Stat temp = forStats.get(f.name);
            if(temp != null){
                if(isNumerator(temp)) stats.add(temp, bundle.get("explib.root.numer"), f.intensity[0], f.start);
                else stats.add(temp, bundle.get("explib.root.denomin"), f.start, f.start, f.intensity[0]);
            }
        });
        getExpFields().get(ExpType.bool).each(f -> {
            Stat temp = forStats.get(f.name);
            if(temp != null) stats.add(temp, bundle.get("explib.bool"), f.intensity[0], f.start <= 0);
        });
    }

    EnumMap<ExpType, ObjectSet<ExpField>> getExpFields();

    default void addExpField(String expType, String field, int start, int... intensity){
        try{
            Field blockField = getClass().getField(field);
            getExpFields().get(ExpType.valueOf(expType)).add(new ExpField(field, blockField, start, intensity));
            blockField.set(this, ExpType.valueOf(expType) == ExpType.bool ? start > 0 : start);
        }catch (Exception e){
            //Log.log(LogLevel.info, "[@]: @", "E", e.toString());
        }
    }

    default boolean isNumerator(Stat stat){
        return stat == Stat.inaccuracy || stat == Stat.shootRange;
    }
}
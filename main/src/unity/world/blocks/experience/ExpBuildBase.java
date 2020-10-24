package unity.world.blocks.experience;

import java.util.EnumMap;
import arc.struct.ObjectSet;
import arc.math.Mathf;
import arc.util.io.*;
import mindustry.world.Block;
import unity.world.meta.ExpType;

public interface ExpBuildBase{
	Block getBlock();

	EnumMap<ExpType, ObjectSet<ExpField>> getBlockExpFields();

	default void setExpStats(){
		int lvl = getLevel();
		getBlockExpFields().get(ExpType.linear).each(f -> {
			try{
				f.field.set(getBlock(), Math.max(f.start + f.intensity * lvl, 0));
			}catch (Exception e){
				//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
			}
		});
		getBlockExpFields().get(ExpType.exp).each(f -> {
			try{
				f.field.set(getBlock(), Math.max(f.start + Mathf.pow(f.intensity, lvl), 0));
			}catch (Exception e){
				//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
			}
		});
		getBlockExpFields().get(ExpType.root).each(f -> {
			try{
				f.field.set(getBlock(), Math.max(f.start + Mathf.sqrt(f.intensity * lvl), 0));
			}catch (Exception e){
				//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
			}
		});
		getBlockExpFields().get(ExpType.bool).each(f -> {
			try{
				f.field.set(getBlock(), f.start > 0 ? lvl < f.intensity : lvl >= f.intensity);
			}catch (Exception e){
				//Log.log(LogLevel.info, "[@]: @", "E", e.toString());
			}
		});
	}

	int totalExp();

	void setExp(int a);

	default void incExp(int a){
		int current = totalExp() + a, lvl = getLevel(), max = getRequiredExp(lvl);
		if(lvl == getBlockMaxLevel()) return;
		if(current >= max){
			setExp(current - max);
			setLevel(lvl + 1);
			levelUpEffect();
		}else setExp(current);
	}

	int getBlockMaxLevel();

	int getLevel();

	void setLevel(int a);

	default int getRequiredExp(int lvl){
		return (2 * lvl + 1) * 10;
	}

	default float getLvlf(){
		if (getLevel() == getBlockMaxLevel()) return 1f;
		return totalExp() / (float) getRequiredExp(getLevel());
	}

	default void levelUpEffect(){

	}

	default void expUpdate(){

	}

	default void expWrite(Writes write){
		write.i(totalExp());
		write.i(getLevel());
	}

	default void expRead(Reads read, byte revision){
		setExp(read.i());
		setLevel(read.i());
	}
}

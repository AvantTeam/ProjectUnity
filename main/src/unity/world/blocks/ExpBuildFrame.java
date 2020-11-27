package unity.world.blocks;

import java.util.EnumMap;

import arc.math.Mathf;
import arc.struct.ObjectSet;
import unity.world.meta.ExpType;

public interface ExpBuildFrame extends ExpBuildBase{
    ExpBlockFrame getExpBlock();

    default void setExpStats(){
        int lvl = getLevel();
        ExpBlockFrame expBlock = getExpBlock();
        EnumMap<ExpType, ObjectSet<ExpField>> expFields = expBlock.getExpFields();
        expFields.get(ExpType.linear).each(f -> {
            try{
                f.field.set(expBlock, Math.max(f.start + f.intensity[0] * lvl, 0));
            }catch(Exception e){
                //Log.log(LogLevel.info, "[@]: @", "E", e.toString());
            }
        });
        expFields.get(ExpType.exp).each(f -> {
            try{
                f.field.set(expBlock, Math.max(f.start + Mathf.pow(f.intensity[0], lvl), 0));
            }catch(Exception e){
                //Log.log(LogLevel.info, "[@]: @", "E", e.toString());
            }
        });
        expFields.get(ExpType.root).each(f -> {
            try{
                f.field.set(expBlock, Math.max(f.start + Mathf.sqrt(f.intensity[0] * lvl), 0));
            }catch(Exception e){
                //Log.log(LogLevel.info, "[@]: @", "E", e.toString());
            }
        });
        expFields.get(ExpType.bool).each(f -> {
            try{
                f.field.set(expBlock, f.start > 0 ? lvl < f.intensity[0] : lvl >= f.intensity[0]);
            }catch(Exception e){
                //Log.log(LogLevel.info, "[@]: @", "E", e.toString());
            }
        });
        expFields.get(ExpType.list).each(f -> {
            try{
                f.field.set(expBlock, f.intensity[Math.min(lvl, f.intensity.length - 1)]);
            }catch(Exception e){
                //Log.log(LogLevel.info, "[@]: @", "E", e.toString());
            }
        });
    }

    int getBlockMaxLevel();

    int getLevel();

    void setLevel(int a);

    default float getRequiredExp(int lvl){
        return (2f * lvl + 1f) * 10f;
    }

    default float getLvlf(){
        if(getLevel() == getBlockMaxLevel()) return 1f;
        return totalExp() / (float)getRequiredExp(getLevel());
    }

    default void levelUpEffect(){

    }

    default void expUpdate(){

    }

    
}

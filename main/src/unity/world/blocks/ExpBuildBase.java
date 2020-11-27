package unity.world.blocks;

import arc.util.io.*;
import mindustry.gen.Buildingc;

public interface ExpBuildBase extends Buildingc{
    float getMaxExp();

    float totalExp();

    void setExp(float a);

    default void expWrite(Writes write){
        write.i((int)totalExp());
    }

    default void expRead(Reads read, byte revision){
        setExp(read.i());
    }

    default void incExp(float a){
        setExp(Math.min(totalExp() + a, getMaxExp()));
        if(totalExp() > 0f) setExp(0f);
    }

    default boolean consumesOrb(){
        return totalExp() < getMaxExp();
    }

    default float getOrbMultiplier(){
        return 1f;
    }
}

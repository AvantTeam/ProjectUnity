package unity.entities.comp;

import arc.math.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.entities.*;
import unity.type.*;

public abstract interface ExpEntityc<T extends UnlockableContent, E extends ExpType<T>> extends Healthc{
    @Initialize(eval = "0f")
    float exp();

    void exp(float exp);

    default float expf(){
        int level = level();
        if(level >= maxLevel()) return 1f;

        float last = expType().requiredExp(level);
        float next = expType().requiredExp(level + 1);

        return (exp() - last) / (next - last);
    }

    default int level(){
        return Math.min(Mathf.floorPositive(Mathf.sqrt(exp() * 0.1f)), maxLevel());
    }

    default float levelf(){
        return level() / (float)(maxLevel());
    }

    default int maxLevel(){
        return expType().maxLevel;
    }

    default void incExp(float exp){
        int before = level();
        exp(exp() + exp);
        int after = level();

        if(exp() > expType().maxExp) exp(expType().maxExp);
        if(exp() < 0f) exp(0f);

        if(after > before) upgradeDefault();
    }

    default void upgradeDefault(){
        expType().upgradeSound.at(this);
    }

    default void sparkle(){
        
    }

    @MustInherit
    E expType();

    @MustInherit
    float spreadAmount();

    @Override
    @MethodPriority(-1)
    default void update(){
        expType().setExpStats(this);
    }

    @Override
    default void killed(){
        ExpOrbs.spreadExp(x(), y(), exp() * expType().orbRefund, spreadAmount());
    }
}

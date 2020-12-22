package unity.entities.comp;

import arc.math.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.type.*;

public abstract interface ExpEntityc<T extends UnlockableContent, E extends ExpType<T>> extends Posc{
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

        if(!expType().hasUpgradeEffect) return;
        if(after > before){
            if(expType().enableUpgrade){
                if(!Vars.headless && this == Vars.control.input.frag.config.getSelectedTile()){
                    Vars.control.input.frag.config.hideConfig();
                }
            }

            if(this instanceof Buildingc build){
                expType().upgradeEffect.at(this, build.block().size);
            }else if(this instanceof Unitc unit){
                expType().upgradeEffect.at(this, unit.rotation());
            }

            expType().upgradeSound.at(this);
        }
    }

    @MustInherit
    E expType();

    @MustInherit
    void upgrade(int i);
}

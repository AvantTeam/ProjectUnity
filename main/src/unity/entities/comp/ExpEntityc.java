package unity.entities.comp;

import arc.math.*;
import arc.struct.*;
import mindustry.ctype.*;
import unity.annotations.Annotations.*;
import unity.type.*;

public interface ExpEntityc<T extends UnlockableContent, E extends ExpType<T>>{
    @Initialize(eval = "0f")
    float exp();

    void exp(float exp);

    @Initialize(eval = "new $T<>()", args = Seq.class)
    Seq<String> testSeq();

    default float expf(){
        int level = level();
        if(level >= maxLevel()) return 1f;

        float last = requiredExp(level);
        float next = requiredExp(level + 1);

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

    default float requiredExp(int level){
        return level * level * 10f;
    }

    @MustInherit
    E expType();
}

package unity.type;

import mindustry.ctype.*;
import unity.annotations.Annotations.*;

@ExpBase
public abstract class ExpType<T extends UnlockableContent>{
    public final T type;

    public float maxExp;
    public int maxLevel = 20;

    protected ExpType(T type){
        this.type = type;
    }

    public void init(){

    }
}

package unity.entities.comp;

import arc.util.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

/** See {@link TestComp} for docs */
@SuppressWarnings("unused")
@EntityComponent
abstract class Test3Comp implements Test4c{
    /** Annotate it with {@link Replace} and use priority 3 */
    @Replace(3)
    @Override
    public void yourThing(){
        Log.info(5 + ", and no, it's a local variable so you can't change it.");
    }

    @Insert("update()")
    @Extend(Test2c.class)
    void insertUpdateIfInheritsFromTest2(){
        Log.info("don't make me feel left out dad");
    }

    @Override
    @Extend(Test2c.class)
    public void update(){
        Log.info("uwu update me dad");
    }
}

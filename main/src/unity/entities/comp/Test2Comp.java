package unity.entities.comp;

import arc.util.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

/** See {@link TestComp} for docs */
@SuppressWarnings("unused")
@EntityComponent
abstract class Test2Comp implements Unitc, Test4c{
    int hahaMine = 7;

    @Override
    public float speed(){
        return hahaMine * 4f;
    }

    /** Annotate it with {@link Replace} and use priority 5 */
    @Replace(5)
    @Override
    public void yourThing(){
        Log.info(hahaMine + ", beat that.");
    }
}

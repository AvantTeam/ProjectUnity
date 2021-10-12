package unity.entities.comp;

import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

/** See {@link TestComp} for docs */
@SuppressWarnings("unused")
@EntityDef({Test4c.class, Test3c.class})
@EntityComponent
abstract class Test4Comp implements Entityc{
    transient int thing;

    /** Base method that is going to be {@link Replace}d */
    void yourThing(){ // Don't ask questions
        throw new IllegalStateException("Not yours.");
    }
}

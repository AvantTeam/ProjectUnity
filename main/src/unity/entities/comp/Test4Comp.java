package unity.entities.comp;

import mindustry.gen.*;
import unity.annotations.Annotations.*;

/** See {@link TestComp} for docs */
@SuppressWarnings("unused")
@EntityComponent
abstract class Test4Comp implements Entityc{
    transient int thing;

    /** Base method that is going to be {@link Replace}d */
    public void yourThing(){ // Don't ask questions
        throw new IllegalStateException("Not yours.");
    }
}

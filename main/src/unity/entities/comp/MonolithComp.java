package unity.entities.comp;

import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class MonolithComp implements Unitc{
    @Import float x, y, rotation;

    @Override
    public void destroy(){
        MonolithSoul soul = MonolithSoul.create();
        soul.set(x, y);
        soul.rotation = rotation;
        soul.add();
    }
}

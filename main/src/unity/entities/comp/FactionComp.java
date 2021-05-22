package unity.entities.comp;

import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.mod.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class FactionComp implements Entityc{
    public abstract Faction faction();
}

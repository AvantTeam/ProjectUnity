package unity.gensrc;

import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.entities.*;

final class EntityDefs{
    // Monolith units.
    @EntityDef({Unitc.class, Monolithc.class}) Object monolithUnit;

    private EntityDefs(){
        throw new AssertionError();
    }
}

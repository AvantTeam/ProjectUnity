package unity.gensrc;

import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.entities.*;

final class EntityDefs{
    /** Monolith unit. */
    @EntityDef({Unitc.class, Monolithc.class}) Object monolithUnit;
    /** Monolith mech + unit. */
    @EntityDef({Unitc.class, Mechc.class, Monolithc.class}) Object monolithMechUnit;
    /** Monolith legs + unit. */
    @EntityDef({Unitc.class, Legsc.class, Monolithc.class}) Object monolithLegsUnit;

    /** Youngcha modular + unit. */
    @EntityDef({Unitc.class, Modularc.class}) Object modularUnit;

    /** End mech + unit */
    @EntityDef({Unitc.class, Mechc.class, Endc.class}) Object endMechUnit;
    /** End mech + invisible + unit */
    @EntityDef({Unitc.class, Mechc.class, Invisiblec.class, Endc.class}) Object endInvisibleMechUnit;

    private EntityDefs(){
        throw new AssertionError();
    }
}

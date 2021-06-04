package unity.mod;

import arc.*;
import arc.func.*;
import mindustry.game.EventType.*;

/** Convenient implementation of event listener attaching/detaching specifically for {@link Trigger}s */
@SuppressWarnings("unchecked")
public final class Triggers{
    public static Cons<Trigger> listen(Trigger trigger, Runnable run){
        Cons<Trigger> ret = e -> run.run();
        Events.on((Class<Trigger>)trigger.getClass(), ret);

        return ret;
    }

    public static void detach(Trigger trigger, Cons<Trigger> run){
        Events.remove((Class<Trigger>)trigger.getClass(), run);
    }
}

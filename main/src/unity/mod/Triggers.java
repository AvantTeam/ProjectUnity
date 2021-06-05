package unity.mod;

import arc.*;
import arc.func.*;
import mindustry.game.EventType.*;

/** Convenient implementation of event listener attaching/detaching especially for {@link Trigger}s */
@SuppressWarnings("unchecked")
public final class Triggers{
    public static <T> Cons<T> cons(Runnable run){
        return e -> run.run();
    }

    public static <T> Cons<T> listen(T trigger, Runnable run){
        Cons<T> cons = cons(run);
        listen(trigger, cons);

        return cons;
    }

    public static <T> void listen(T trigger, Cons<T> listener){
        Events.on((Class<T>)trigger.getClass(), listener);
    }

    public static <T> void detach(T trigger, Cons<T> run){
        Events.remove((Class<T>)trigger.getClass(), run);
    }
}

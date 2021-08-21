package unity.world.meta;

import arc.func.*;
import arc.math.*;
import arc.struct.*;

/**
 * A class wrapping progression listeners. You can trigger the listeners by simply calling {@link #apply(float)}. There are
 * several convenience methods supplied here, with the {@code setter}s usually used like {@code val -> this.value = val}.
 *
 * This should be used for implementations like the EXP system and Monolith souls.
 * @author GlennFolker
 */
//TODO <T>
public class DynamicProgression{
    public final Seq<Floatc> listeners = new Seq<>();

    public void apply(float progress){
        listeners.each(p -> p.get(progress));
    }

    public Floatc add(Floatc listener){
        listeners.add(listener);
        return listener;
    }

    public Floatc linear(float start, float intensity, Floatc setter){
        return add(progress -> setter.get(start + progress * intensity));
    }

    public Floatc exponential(float start, float intensity, Floatc setter){
        return add(progress -> setter.get(start * Mathf.pow(intensity, progress)));
    }

    public Floatc root(float start, float intensity, Floatc setter){
        return add(progress -> setter.get(start + Mathf.sqrt(intensity * progress)));
    }

    public Floatc bool(boolean start, float threshold, Boolc setter){
        return add(progress -> setter.get(start == progress < threshold));
    }

    public <T> Floatc list(T[] array, float scale, Interp interp, Cons<T> setter){
        return add(progress -> setter.get(array[
            Mathf.clamp(
                Mathf.floor(interp.apply(progress * scale)),
                0,
                array.length - 1
            )
        ]));
    }
}

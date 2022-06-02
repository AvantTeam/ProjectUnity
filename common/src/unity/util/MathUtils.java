package unity.util;

import arc.math.*;
import arc.math.Interp.*;

/**
 * Shared utility access for mathematical operations.
 * @author GlennFolker
 */
public final class MathUtils{
    public static final Interp pow25In = new PowIn(25f);

    private MathUtils(){
        throw new AssertionError();
    }
}

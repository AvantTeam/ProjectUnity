package unity.util;

import arc.math.*;
import arc.math.Interp.*;
import arc.math.geom.*;

/** Shared utility access for mathematical operations. */
public final class MathUtils{
    public static final Interp pow25In = new PowIn(25f);

    public static final Quat q1 = new Quat(), q2 = new Quat();

    private MathUtils(){
        throw new AssertionError();
    }
}

package unity.util;

import arc.math.*;
import arc.math.Interp.*;
import arc.math.geom.*;

/** Shared utility access for mathematical operations. */
@SuppressWarnings({"SuspiciousNameCombination", "UnusedReturnValue"})
public final class MathUtils{
    public static final Interp pow25In = new PowIn(25f);

    public static final Quat q1 = new Quat(), q2 = new Quat();

    private static final Vec2 v1 = new Vec2(), v2 = new Vec2(), v3 = new Vec2();

    private MathUtils(){
        throw new AssertionError();
    }

    /**
     * Code taken from <a href="https://github.com/earlygrey/shapedrawer/blob/features/variable-line-width/drawer/src/space/earlygrey/shapedrawer/Joiner.java">
     * the shapes renderer library</a>; calculates the left and right point of a joint path vertex.
     * @param a Path vertex {@code n - 1}.
     * @param b Path vertex {@code n}.
     * @param c Path vertex {@code n + 1}.
     * @param d Left point output vector.
     * @param e Right point output vector.
     * @return The mid-point angle, in radians.
     * @author earlygrey
     */
    public static float pathJoin(Vec2 a, Vec2 b, Vec2 c, Vec2 d, Vec2 e, float halfWidth){
        v1.set(b).sub(a);
        v2.set(c).sub(b);

        float angle = Mathf.atan2(v1.x * v2.x + v1.y * v2.y, v2.x * v1.y - v2.y * v1.x);
        if(Mathf.zero(angle) || Mathf.equal(angle, Mathf.PI2)){
            v1.setLength(halfWidth);
            d.set(-v1.y, v1.x).add(b);
            e.set(v1.y, -v1.x).add(b);

            return angle;
        }

        float len = halfWidth / Mathf.sin(angle);
        boolean bendsLeft = angle < 0f;

        v1.setLength(len);
        v2.setLength(len);

        (bendsLeft ? d : e).set(b).sub(v1).add(v2);
        (bendsLeft ? e : d).set(b).add(v1).sub(v2);
        return angle;
    }

    /**
     * Code taken from <a href="https://github.com/earlygrey/shapedrawer/blob/features/variable-line-width/drawer/src/space/earlygrey/shapedrawer/Joiner.java">
     * the shapes renderer library</a>; calculates the left and right point of an endpoint path vertex.
     * @param sx Start X.
     * @param sy Start Y.
     * @param ex End X.
     * @param ey End Y.
     * @param d Left point output vector.
     * @param e Right point output vector.
     * @author earlygrey
     */
    public static void pathEnd(float sx, float sy, float ex, float ey, Vec2 d, Vec2 e, float halfWidth){
        v3.set(ex, ey).sub(sx, sy).setLength(halfWidth);
        d.set(v3.y, -v3.x).add(ex, ey);
        e.set(-v3.y, v3.x).add(ex, ey);
    }
}

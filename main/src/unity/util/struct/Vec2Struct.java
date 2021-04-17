package unity.util.struct;

import arc.math.geom.*;

/** @author GlennFolker */
public final class Vec2Struct{
    private static final Vec2 ref = new Vec2();
    private static final long bits32f = 0b0000000000000000000000000000000011111111111111111111111111111111L;
    private static final long bits32s = 0b1111111111111111111111111111111100000000000000000000000000000000L;

    public static long get(Vec2 vec){
        return get(vec.x, vec.y);
    }

    public static long get(float x, float y){
        return (conv(x) & bits32f) | ((conv(y) << 32) & bits32s);
    }

    public static float x(long vec){
        return Float.intBitsToFloat((int)(vec & bits32f));
    }

    public static float y(long vec){
        return Float.intBitsToFloat((int)((vec >>> 32) & bits32f));
    }

    public static long conv(float val){
        return (long)Float.floatToIntBits(val);
    }

    public static long add(long vec, float x, float y){
        return get(x(vec) + x, y(vec) + y);
    }

    public static long sub(long vec, float x, float y){
        return get(x(vec) - x, y(vec) - y);
    }

    public static long setLength(long vec, float len){
        ref.set(x(vec), y(vec));
        ref.setLength(len);
        return get(ref);
    }

    public static long setLength2(long vec, float len2){
        ref.set(x(vec), y(vec));
        ref.setLength2(len2);
        return get(ref);
    }

    public static long scl(long vec, float scl){
        ref.set(x(vec), y(vec));
        ref.scl(scl);
        return get(ref);
    }
}

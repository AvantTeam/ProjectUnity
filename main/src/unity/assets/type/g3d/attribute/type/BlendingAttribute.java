package unity.assets.type.g3d.attribute.type;

import arc.graphics.*;
import arc.math.*;
import unity.assets.type.g3d.attribute.*;

public class BlendingAttribute extends Attribute{
    public final static String blendAlias = "blended";
    public final static long blend = register(blendAlias);

    public static boolean is(long mask){
        return (mask & blend) == mask;
    }

    /**
     * Whether this material should be considered blended (default: true). This is used for sorting (back to front
     * instead of front to back).
     */
    public boolean blended;

    /**
     * Specifies how the (incoming) red, green, blue, and alpha source blending factors are computed (default:
     * GL_SRC_ALPHA)
     */
    public int sourceFunction;

    /**
     * Specifies how the (existing) red, green, blue, and alpha destination blending factors are computed (default:
     * GL_ONE_MINUS_SRC_ALPHA)
     */
    public int destFunction;

    /** The opacity used as source alpha value, ranging from 0 (fully transparent) to 1 (fully opaque), (default: 1). */
    public float opacity = 1.f;

    public BlendingAttribute(){
        this(null);
    }

    public BlendingAttribute(final boolean blended, final int sourceFunc, final int destFunc, final float opacity){
        super(blend);
        this.blended = blended;
        this.sourceFunction = sourceFunc;
        this.destFunction = destFunc;
        this.opacity = opacity;
    }

    public BlendingAttribute(int sourceFunc, int destFunc, float opacity){
        this(true, sourceFunc, destFunc, opacity);
    }

    public BlendingAttribute(int sourceFunc, int destFunc){
        this(sourceFunc, destFunc, 1.f);
    }

    public BlendingAttribute(boolean blended, float opacity){
        this(blended, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, opacity);
    }

    public BlendingAttribute(float opacity){
        this(true, opacity);
    }

    public BlendingAttribute(BlendingAttribute copyFrom){
        this(
            copyFrom == null || copyFrom.blended,
            copyFrom == null ? GL20.GL_SRC_ALPHA : copyFrom.sourceFunction,
            copyFrom == null ? GL20.GL_ONE_MINUS_SRC_ALPHA : copyFrom.destFunction,
            copyFrom == null ? 1.f : copyFrom.opacity
        );
    }

    @Override
    public BlendingAttribute copy(){
        return new BlendingAttribute(this);
    }

    @Override
    public int hashCode(){
        int result = super.hashCode();
        result = 947 * result + (blended ? 1 : 0);
        result = 947 * result + sourceFunction;
        result = 947 * result + destFunction;
        result = 947 * result + Float.floatToRawIntBits(opacity);
        return result;
    }

    @Override
    public int compareTo(Attribute o){
        if(type != o.type)
            return (int) (type - o.type);
        BlendingAttribute other = (BlendingAttribute) o;
        if(blended != other.blended)
            return blended ? 1 : -1;
        if(sourceFunction != other.sourceFunction)
            return sourceFunction - other.sourceFunction;
        if(destFunction != other.destFunction)
            return destFunction - other.destFunction;
        return Mathf.equal(opacity, other.opacity) ? 0 : (opacity < other.opacity ? 1 : -1);
    }
}

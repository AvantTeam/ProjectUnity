package unity.assets.type.g3d.attribute.type;

import arc.math.*;
import unity.assets.type.g3d.attribute.*;

public class FloatAttribute extends Attribute{
    public static final String shininessAlias = "shininess";
    public static final long shininess = register(shininessAlias);

    public static final String alphaTestAlias = "alphaTest";
    public static final long alphaTest = register(alphaTestAlias);

    public static FloatAttribute createShininess(float value){
        return new FloatAttribute(shininess, value);
    }

    public static FloatAttribute createAlphaTest(float value){
        return new FloatAttribute(alphaTest, value);
    }

    public float value;

    public FloatAttribute(long type){
        super(type);
    }

    public FloatAttribute(long type, float value){
        super(type);
        this.value = value;
    }

    @Override
    public FloatAttribute copy(){
        return new FloatAttribute(type, value);
    }

    @Override
    public int hashCode(){
        int result = super.hashCode();
        result = 977 * result + Float.floatToRawIntBits(value);

        return result;
    }

    @Override
    public int compareTo(Attribute o){
        if(type != o.type){
            return (int)(type - o.type);
        }

        float v = ((FloatAttribute) o).value;
        return Mathf.equal(value, v) ? 0 : value < v ? -1 : 1;
    }
}

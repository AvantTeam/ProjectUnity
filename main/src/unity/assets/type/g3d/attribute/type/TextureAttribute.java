package unity.assets.type.g3d.attribute.type;

import arc.graphics.*;
import arc.math.*;
import unity.assets.type.g3d.attribute.*;

public class TextureAttribute extends Attribute{
    public final static String diffuseAlias = "diffuseTexture";
    public final static long diffuse = register(diffuseAlias);

    public final static String specularAlias = "specularTexture";
    public final static long specular = register(specularAlias);

    public final static String bumpAlias = "bumpTexture";
    public final static long bump = register(bumpAlias);

    public final static String normalAlias = "normalTexture";
    public final static long normal = register(normalAlias);

    public final static String ambientAlias = "ambientTexture";
    public final static long ambient = register(ambientAlias);

    public final static String emissiveAlias = "emissiveTexture";
    public final static long emissive = register(emissiveAlias);

    public final static String reflectionAlias = "reflectionTexture";
    public final static long reflection = register(reflectionAlias);

    protected static long Mask = diffuse | specular | bump | normal | ambient | emissive | reflection;

    public  static boolean is(long mask){
        return (mask & Mask) != 0;
    }

    public static TextureAttribute createDiffuse(Texture texture){
        return new TextureAttribute(diffuse, texture);
    }

    public static TextureAttribute createSpecular(Texture texture){
        return new TextureAttribute(specular, texture);
    }

    public static TextureAttribute createNormal(Texture texture){
        return new TextureAttribute(normal, texture);
    }

    public static TextureAttribute createBump(Texture texture){
        return new TextureAttribute(bump, texture);
    }

    public static TextureAttribute createAmbient(Texture texture){
        return new TextureAttribute(ambient, texture);
    }

    public static TextureAttribute createEmissive(Texture texture){
        return new TextureAttribute(emissive, texture);
    }

    public static TextureAttribute createReflection(Texture texture){
        return new TextureAttribute(reflection, texture);
    }

    public final Texture texture;
    public float offsetU = 0f;
    public float offsetV = 0f;
    public float scaleU = 1f;
    public float scaleV = 1f;

    public TextureAttribute(long type, Texture texture){
        super(type);
        this.texture = texture;
    }

    public TextureAttribute(long type, Texture texture, float offsetU, float offsetV, float scaleU, float scaleV){
        this(type, texture);
        this.offsetU = offsetU;
        this.offsetV = offsetV;
        this.scaleU = scaleU;
        this.scaleV = scaleV;
    }

    public TextureAttribute(TextureAttribute copyFrom){
        this(
            copyFrom.type,
            copyFrom.texture,
            copyFrom.offsetU, copyFrom.offsetV, copyFrom.scaleU,
            copyFrom.scaleV
        );
    }

    @Override
    public TextureAttribute copy(){
        return new TextureAttribute(this);
    }

    @Override
    public int hashCode(){
        int result = super.hashCode();
        result = 991 * result + texture.getTextureObjectHandle();
        result = 991 * result + Float.floatToRawIntBits(offsetU);
        result = 991 * result + Float.floatToRawIntBits(offsetV);
        result = 991 * result + Float.floatToRawIntBits(scaleU);
        result = 991 * result + Float.floatToRawIntBits(scaleV);
        return result;
    }

    @Override
    public int compareTo(Attribute o){
        if(type != o.type) return type < o.type ? -1 : 1;
        TextureAttribute other = (TextureAttribute)o;

        int c = Integer.compare(texture.getTextureObjectHandle(), other.texture.getTextureObjectHandle());
        if(c != 0) return c;

        if(!Mathf.equal(scaleU, other.scaleU)) return scaleU > other.scaleU ? 1 : -1;
        if(!Mathf.equal(scaleV, other.scaleV)) return scaleV > other.scaleV ? 1 : -1;
        if(!Mathf.equal(offsetU, other.offsetU)) return offsetU > other.offsetU ? 1 : -1;
        if(!Mathf.equal(offsetV, other.offsetV)) return offsetV > other.offsetV ? 1 : -1;

        return 0;
    }
}

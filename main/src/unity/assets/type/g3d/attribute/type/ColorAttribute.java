package unity.assets.type.g3d.attribute.type;

import arc.graphics.*;
import unity.assets.type.g3d.attribute.*;

public class ColorAttribute extends Attribute{
    public final static String diffuseAlias = "diffuseColor";
    public final static long diffuse = register(diffuseAlias);
    public final static String specularAlias = "specularColor";
    public final static long specular = register(specularAlias);
    public final static String ambientAlias = "ambientColor";
    public static final long ambient = register(ambientAlias);
    public final static String emissiveAlias = "emissiveColor";
    public static final long emissive = register(emissiveAlias);
    public final static String reflectionAlias = "reflectionColor";
    public static final long reflection = register(reflectionAlias);
    public final static String ambientLightAlias = "ambientLightColor";
    public static final long ambientLight = register(ambientLightAlias);
    public final static String fogAlias = "fogColor";
    public static final long fog = register(fogAlias);

    protected static long Mask = ambient | diffuse | specular | emissive | reflection | ambientLight | fog;

    public static boolean is(final long mask){
        return (mask & Mask) != 0;
    }

    public static ColorAttribute createAmbient(final Color color){
        return new ColorAttribute(ambient, color);
    }

    public static ColorAttribute createAmbient(float r, float g, float b, float a){
        return new ColorAttribute(ambient, r, g, b, a);
    }

    public static ColorAttribute createDiffuse(final Color color){
        return new ColorAttribute(diffuse, color);
    }

    public static ColorAttribute createDiffuse(float r, float g, float b, float a){
        return new ColorAttribute(diffuse, r, g, b, a);
    }

    public static ColorAttribute createSpecular(final Color color){
        return new ColorAttribute(specular, color);
    }

    public static ColorAttribute createSpecular(float r, float g, float b, float a){
        return new ColorAttribute(specular, r, g, b, a);
    }

    public static ColorAttribute createReflection(final Color color){
        return new ColorAttribute(reflection, color);
    }

    public static ColorAttribute createReflection(float r, float g, float b, float a){
        return new ColorAttribute(reflection, r, g, b, a);
    }

    public static ColorAttribute createEmissive(final Color color){
        return new ColorAttribute(emissive, color);
    }

    public static ColorAttribute createEmissive(float r, float g, float b, float a){
        return new ColorAttribute(emissive, r, g, b, a);
    }

    public static ColorAttribute createAmbientLight(final Color color){
        return new ColorAttribute(ambientLight, color);
    }

    public static ColorAttribute createAmbientLight(float r, float g, float b, float a){
        return new ColorAttribute(ambientLight, r, g, b, a);
    }

    public static ColorAttribute createFog(final Color color){
        return new ColorAttribute(fog, color);
    }

    public static ColorAttribute createFog(float r, float g, float b, float a){
        return new ColorAttribute(fog, r, g, b, a);
    }

    public final Color color = new Color();

    public ColorAttribute(long type){
        super(type);
        if(!is(type)){
            throw new IllegalArgumentException("Invalid type specified");
        }
    }

    public ColorAttribute(final long type, final Color color){
        this(type);
        if(color != null)
            this.color.set(color);
    }

    public ColorAttribute(final long type, float r, float g, float b, float a){
        this(type);
        this.color.set(r, g, b, a);
    }

    public ColorAttribute(final ColorAttribute copyFrom){
        this(copyFrom.type, copyFrom.color);
    }

    @Override
    public ColorAttribute copy(){
        return new ColorAttribute(this);
    }

    @Override
    public int hashCode(){
        int result = super.hashCode();
        result = 953 * result + color.abgr();

        return result;
    }

    @Override
    public int compareTo(Attribute o){
        if(type != o.type){
            return (int)(type - o.type);
        }

        return ((ColorAttribute)o).color.abgr() - color.abgr();
    }
}

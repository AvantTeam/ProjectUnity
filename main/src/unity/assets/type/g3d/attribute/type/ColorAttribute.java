package unity.assets.type.g3d.attribute.type;

import arc.graphics.*;
import unity.assets.type.g3d.attribute.*;

public class ColorAttribute extends Attribute{
    public final static String DiffuseAlias = "diffuseColor";
    public final static long Diffuse = register(DiffuseAlias);
    public final static String SpecularAlias = "specularColor";
    public final static long Specular = register(SpecularAlias);
    public final static String AmbientAlias = "ambientColor";
    public static final long Ambient = register(AmbientAlias);
    public final static String EmissiveAlias = "emissiveColor";
    public static final long Emissive = register(EmissiveAlias);
    public final static String ReflectionAlias = "reflectionColor";
    public static final long Reflection = register(ReflectionAlias);
    public final static String AmbientLightAlias = "ambientLightColor";
    public static final long AmbientLight = register(AmbientLightAlias);
    public final static String FogAlias = "fogColor";
    public static final long Fog = register(FogAlias);

    protected static long Mask = Ambient | Diffuse | Specular | Emissive | Reflection | AmbientLight | Fog;

    public static boolean is(final long mask){
        return (mask & Mask) != 0;
    }

    public static ColorAttribute createAmbient(final Color color){
        return new ColorAttribute(Ambient, color);
    }

    public static ColorAttribute createAmbient(float r, float g, float b, float a){
        return new ColorAttribute(Ambient, r, g, b, a);
    }

    public static ColorAttribute createDiffuse(final Color color){
        return new ColorAttribute(Diffuse, color);
    }

    public static ColorAttribute createDiffuse(float r, float g, float b, float a){
        return new ColorAttribute(Diffuse, r, g, b, a);
    }

    public static ColorAttribute createSpecular(final Color color){
        return new ColorAttribute(Specular, color);
    }

    public static ColorAttribute createSpecular(float r, float g, float b, float a){
        return new ColorAttribute(Specular, r, g, b, a);
    }

    public static ColorAttribute createReflection(final Color color){
        return new ColorAttribute(Reflection, color);
    }

    public static ColorAttribute createReflection(float r, float g, float b, float a){
        return new ColorAttribute(Reflection, r, g, b, a);
    }

    public static ColorAttribute createEmissive(final Color color){
        return new ColorAttribute(Emissive, color);
    }

    public static ColorAttribute createEmissive(float r, float g, float b, float a){
        return new ColorAttribute(Emissive, r, g, b, a);
    }

    public static ColorAttribute createAmbientLight(final Color color){
        return new ColorAttribute(AmbientLight, color);
    }

    public static ColorAttribute createAmbientLight(float r, float g, float b, float a){
        return new ColorAttribute(AmbientLight, r, g, b, a);
    }

    public static ColorAttribute createFog(final Color color){
        return new ColorAttribute(Fog, color);
    }

    public static ColorAttribute createFog(float r, float g, float b, float a){
        return new ColorAttribute(Fog, r, g, b, a);
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

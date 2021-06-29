package unity.assets.type.g3d.attribute.type;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.game.EventType.*;
import unity.assets.type.g3d.attribute.*;
import unity.graphics.*;

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

    public static TextureAttribute createDiffuse(TextureRegion region){
        return new TextureAttribute(diffuse, region);
    }

    public static TextureAttribute createSpecular(Texture texture){
        return new TextureAttribute(specular, texture);
    }

    public static TextureAttribute createSpecular(TextureRegion region){
        return new TextureAttribute(specular, region);
    }

    public static TextureAttribute createNormal(Texture texture){
        return new TextureAttribute(normal, texture);
    }

    public static TextureAttribute createNormal(TextureRegion region){
        return new TextureAttribute(normal, region);
    }

    public static TextureAttribute createBump(Texture texture){
        return new TextureAttribute(bump, texture);
    }

    public static TextureAttribute createBump(TextureRegion region){
        return new TextureAttribute(bump, region);
    }

    public static TextureAttribute createAmbient(Texture texture){
        return new TextureAttribute(ambient, texture);
    }

    public static TextureAttribute createAmbient(TextureRegion region){
        return new TextureAttribute(ambient, region);
    }

    public static TextureAttribute createEmissive(Texture texture){
        return new TextureAttribute(emissive, texture);
    }

    public static TextureAttribute createEmissive(TextureRegion region){
        return new TextureAttribute(emissive, region);
    }

    public static TextureAttribute createReflection(Texture texture){
        return new TextureAttribute(reflection, texture);
    }

    public static TextureAttribute createReflection(TextureRegion region){
        return new TextureAttribute(reflection, region);
    }

    public final TextureDescriptor<Texture> textureDescription;
    public float offsetU = 0f;
    public float offsetV = 0f;
    public float scaleU = 1f;
    public float scaleV = 1f;

    /**
     * The index of the texture coordinate vertex attribute to use for this TextureAttribute. Whether this value is
     * used, depends on the shader and {@link Attribute#type} value. For basic (model specific) types (e.g. {@link
     * #diffuse}, {@link #normal}, etc.), this value is usually ignored and the first texture coordinate vertex
     * attribute is used.
     */
    public int uvIndex = 0;

    public TextureAttribute(long type){
        super(type);
        if(!is(type)) throw new IllegalArgumentException("Invalid type specified");

        textureDescription = new TextureDescriptor<>();
        Events.on(ContentInitEvent.class, e -> {
            var region = Core.atlas.find("unity-" + textureDescription.fileName);
            var tex = region.texture;

            textureDescription.set(tex);
            offsetU = region.u + offsetU * region.width;
            offsetV = region.v + offsetV * region.height;
            scaleU = region.u2 - scaleU * region.width;
            scaleV = region.v2 - scaleV * region.height;
        });
    }

    public <T extends Texture> TextureAttribute(long type, TextureDescriptor<T> textureDescription){
        this(type);
        this.textureDescription.set(textureDescription);
    }

    public <T extends Texture> TextureAttribute(long type, TextureDescriptor<T> textureDescription, float offsetU, float offsetV, float scaleU, float scaleV, int uvIndex){
        this(type, textureDescription);
        this.offsetU = offsetU;
        this.offsetV = offsetV;
        this.scaleU = scaleU;
        this.scaleV = scaleV;
        this.uvIndex = uvIndex;
    }

    public <T extends Texture> TextureAttribute(long type, TextureDescriptor<T> textureDescription, float offsetU, float offsetV, float scaleU, float scaleV){
        this(type, textureDescription, offsetU, offsetV, scaleU, scaleV, 0);
    }

    public TextureAttribute(long type, Texture texture){
        this(type);
        textureDescription.texture = texture;
    }

    public TextureAttribute(long type, TextureRegion region){
        this(type);
        set(region);
    }

    public TextureAttribute(TextureAttribute copyFrom){
        this(copyFrom.type, copyFrom.textureDescription, copyFrom.offsetU, copyFrom.offsetV, copyFrom.scaleU,
            copyFrom.scaleV, copyFrom.uvIndex);
    }

    public void set(TextureRegion region){
        textureDescription.texture = region.texture;
        offsetU = region.u;
        offsetV = region.v;
        scaleU = region.u2 - offsetU;
        scaleV = region.v2 - offsetV;
    }

    @Override
    public TextureAttribute copy(){
        return new TextureAttribute(this);
    }

    @Override
    public int hashCode(){
        int result = super.hashCode();
        result = 991 * result + textureDescription.hashCode();
        result = 991 * result + Float.floatToRawIntBits(offsetU);
        result = 991 * result + Float.floatToRawIntBits(offsetV);
        result = 991 * result + Float.floatToRawIntBits(scaleU);
        result = 991 * result + Float.floatToRawIntBits(scaleV);
        result = 991 * result + uvIndex;
        return result;
    }

    @Override
    public int compareTo(Attribute o){
        if(type != o.type) return type < o.type ? -1 : 1;
        TextureAttribute other = (TextureAttribute)o;

        int c = textureDescription.compareTo(other.textureDescription);
        if(c != 0) return c;
        if(uvIndex != other.uvIndex) return uvIndex - other.uvIndex;

        if(!Mathf.equal(scaleU, other.scaleU)) return scaleU > other.scaleU ? 1 : -1;
        if(!Mathf.equal(scaleV, other.scaleV)) return scaleV > other.scaleV ? 1 : -1;
        if(!Mathf.equal(offsetU, other.offsetU)) return offsetU > other.offsetU ? 1 : -1;
        if(!Mathf.equal(offsetV, other.offsetV)) return offsetV > other.offsetV ? 1 : -1;

        return 0;
    }
}

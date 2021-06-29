package unity.graphics;

import arc.graphics.*;
import arc.graphics.Texture.*;

public class TextureDescriptor<T extends GLTexture> implements Comparable<TextureDescriptor<T>>{
    public String fileName;
    public T texture;
    public TextureFilter minFilter;
    public TextureFilter magFilter;
    public TextureWrap uWrap;
    public TextureWrap vWrap;

    public TextureDescriptor(String fileName, TextureFilter minFilter, TextureFilter magFilter, TextureWrap uWrap, TextureWrap vWrap){
        set(fileName, minFilter, magFilter, uWrap, vWrap);
    }

    public TextureDescriptor(String fileName){
        this(fileName, null, null, null, null);
    }

    public TextureDescriptor(){}

    public void set(String fileName, Texture.TextureFilter minFilter, Texture.TextureFilter magFilter, Texture.TextureWrap uWrap, Texture.TextureWrap vWrap){
        this.fileName = fileName;
        this.minFilter = minFilter;
        this.magFilter = magFilter;
        this.uWrap = uWrap;
        this.vWrap = vWrap;
    }

    public <V extends T> void set(TextureDescriptor<V> other){
        fileName = other.fileName;
        texture = other.texture;
        minFilter = other.minFilter;
        magFilter = other.magFilter;
        uWrap = other.uWrap;
        vWrap = other.vWrap;
    }

    public <V extends T> void set(V texture){
        this.texture = texture;
        minFilter = texture.getMinFilter();
        magFilter = texture.getMagFilter();
        uWrap = texture.getUWrap();
        vWrap = texture.getVWrap();
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null) return false;
        if(obj == this) return true;
        if(!(obj instanceof final TextureDescriptor<?> other)) return false;
        return other.texture == texture && other.minFilter == minFilter && other.magFilter == magFilter && other.uWrap == uWrap && other.vWrap == vWrap;
    }

    @Override
    public int hashCode(){
        long result = (texture == null ? 0 : texture.glTarget);
        result = 811 * result + (texture == null ? 0 : texture.getTextureObjectHandle());
        result = 811 * result + (minFilter == null ? 0 : minFilter.glEnum);
        result = 811 * result + (magFilter == null ? 0 : magFilter.glEnum);
        result = 811 * result + (uWrap == null ? 0 : uWrap.getGLEnum());
        result = 811 * result + (vWrap == null ? 0 : vWrap.getGLEnum());
        return (int) (result ^ (result >> 32));
    }

    @Override
    public int compareTo(TextureDescriptor<T> o){
        if(o == this) return 0;
        int t1 = texture == null ? 0 : texture.glTarget;
        int t2 = o.texture == null ? 0 : o.texture.glTarget;

        if(t1 != t2) return t1 - t2;
        int h1 = texture == null ? 0 : texture.getTextureObjectHandle();
        int h2 = o.texture == null ? 0 : o.texture.getTextureObjectHandle();

        if(h1 != h2) return h1 - h2;
        if(minFilter != o.minFilter) return (minFilter == null ? 0 : minFilter.glEnum) - (o.minFilter == null ? 0 : o.minFilter.glEnum);
        if(magFilter != o.magFilter) return (magFilter == null ? 0 : magFilter.glEnum) - (o.magFilter == null ? 0 : o.magFilter.glEnum);

        if(uWrap != o.uWrap) return (uWrap == null ? 0 : uWrap.getGLEnum()) - (o.uWrap == null ? 0 : o.uWrap.getGLEnum());
        if(vWrap != o.vWrap) return (vWrap == null ? 0 : vWrap.getGLEnum()) - (o.vWrap == null ? 0 : o.vWrap.getGLEnum());

        return 0;
    }
}

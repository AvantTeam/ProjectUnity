package unity.assets.type.g3d.attribute.light;

import arc.graphics.*;

@SuppressWarnings("unchecked")
public abstract class BaseLight<T extends BaseLight<T>>{
    public final Color color = new Color(0, 0, 0, 1);

    public T setColor(float r, float g, float b, float a){
        color.set(r, g, b, a);
        return (T)this;
    }

    public T setColor(Color color){
        this.color.set(color);
        return (T)this;
    }
}

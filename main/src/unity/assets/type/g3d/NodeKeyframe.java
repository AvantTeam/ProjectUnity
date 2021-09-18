package unity.assets.type.g3d;

public class NodeKeyframe<T>{
    public final float keytime;
    public final T value;

    public NodeKeyframe(float t, T v){
        keytime = t;
        value = v;
    }
}

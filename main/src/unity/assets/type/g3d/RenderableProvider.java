package unity.assets.type.g3d;

import arc.struct.*;

import static unity.Unity.*;

public interface RenderableProvider{
    void getRenderables(Seq<Renderable> renders);

    default void render(){
        model.render(this);
    }
}

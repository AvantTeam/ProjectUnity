package unity.assets.type.g3d;

import arc.func.*;

import static unity.Unity.*;

public interface RenderableProvider{
    void getRenderables(Prov<Renderable> renders);

    default void render(){
        Models.render(this);
    }
}

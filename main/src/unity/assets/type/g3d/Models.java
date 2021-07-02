package unity.assets.type.g3d;

import arc.graphics.*;
import arc.graphics.g3d.*;
import arc.struct.*;
import arc.util.pooling.*;
import unity.assets.type.g3d.attribute.type.*;
import unity.graphics.*;
import unity.graphics.UnityShaders.*;

public class Models{
    public Camera3D camera = new Camera3D();
    public RenderableSorter sorter = new RenderableSorter();
    protected Seq<Renderable> renders = new Seq<>();

    {
        camera.perspective = false;
    }

    public void render(RenderableProvider prov){
        renders.clear();
        prov.getRenderables(renders);

        sorter.sort(camera, renders);
        for(Renderable render : renders){
            Graphics3DShader shader = UnityShaders.graphics3DProvider.get(render);
            shader.bind();
            shader.apply(render);
            render.meshPart.render(shader);

            Gl.activeTexture(GL20.GL_TEXTURE0);
        }

        Pools.freeAll(renders);
    }

    public int bind(TextureAttribute attr, int bind){
        attr.texture.bind(bind);
        return bind;
    }
}

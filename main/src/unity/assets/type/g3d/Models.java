package unity.assets.type.g3d;

import arc.graphics.*;
import arc.graphics.g3d.*;
import arc.struct.*;
import arc.util.pooling.*;
import unity.assets.type.g3d.attribute.*;
import unity.assets.type.g3d.attribute.light.*;
import unity.assets.type.g3d.attribute.type.*;
import unity.graphics.*;
import unity.graphics.UnityShaders.*;

public class Models{
    public Camera3D camera = new Camera3D();
    public RenderableSorter sorter = new RenderableSorter();
    public Environment environment = new Environment();
    protected Seq<Renderable> renders = new Seq<>();

    {
        camera.perspective = false;
        camera.near = -10000f;
        camera.far = 10000f;

        environment.set(new ColorAttribute(ColorAttribute.ambientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -1f, -0.2f));
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

package unity.assets.type.g3d;

import arc.graphics.*;
import arc.graphics.g3d.*;
import arc.struct.*;
import arc.util.pooling.*;
import mindustry.game.EventType.*;
import unity.assets.type.g3d.attribute.*;
import unity.assets.type.g3d.attribute.light.*;
import unity.assets.type.g3d.attribute.type.*;
import unity.graphics.*;
import unity.graphics.UnityShaders.*;
import unity.mod.*;

public class Models{
    public Camera3D camera = new Camera3D();
    public RenderableSorter sorter = new RenderableSorter();
    public Environment environment = new Environment();
    protected Seq<Renderable> renders = new Seq<>();

    {
        camera.perspective = false;
        camera.near = -10000f;
        camera.far = 10000f;

        environment.set(ColorAttribute.createAmbientLight(0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -1f, -0.3f));

        Triggers.listen(Trigger.preDraw, () -> Gl.clear(Gl.depthBufferBit));
    }

    public void render(RenderableProvider prov){
        renders.clear();
        prov.getRenderables(renders);

        sorter.sort(camera, renders);

        Gl.enable(Gl.cullFace);
        Gl.cullFace(Gl.back);
        Gl.enable(Gl.depthTest);
        Gl.depthFunc(Gl.lequal);
        Gl.depthRangef(0f, 1f);
        Gl.depthMask(true);
        for(Renderable render : renders){
            Graphics3DShader shader = UnityShaders.graphics3DProvider.get(render);
            shader.bind();
            shader.apply(render);
            render.meshPart.render(shader);
        }
        Gl.disable(Gl.cullFace);
        Gl.disable(Gl.depthTest);
        Gl.activeTexture(Gl.texture0);

        Pools.freeAll(renders);
    }

    public int bind(TextureAttribute attr, int bind){
        attr.texture.bind(bind);
        return bind;
    }
}

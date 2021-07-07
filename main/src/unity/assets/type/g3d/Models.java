package unity.assets.type.g3d;

import arc.*;
import arc.graphics.*;
import arc.graphics.Pixmap.*;
import arc.graphics.g2d.*;
import arc.graphics.g3d.*;
import arc.graphics.gl.*;
import arc.struct.*;
import arc.util.pooling.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;
import unity.assets.type.g3d.attribute.*;
import unity.assets.type.g3d.attribute.light.*;
import unity.assets.type.g3d.attribute.type.*;
import unity.graphics.*;
import unity.graphics.UnityShaders.*;
import unity.mod.*;

public class Models{
    public final Camera3D camera = new Camera3D();
    public final RenderableSorter sorter = new RenderableSorter();
    public final Environment environment = new Environment();
    protected final Seq<Renderable> renders = new Seq<>();
    protected FrameBuffer buffer;

    {
        camera.perspective = false;
        camera.near = -10000f;
        camera.far = 10000f;

        environment.set(ColorAttribute.createAmbientLight(0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -1f, -0.3f));

        Core.assets.loadRun("unity-models-initializer", Models.class, () -> {}, () -> buffer = new FrameBuffer(Format.rgba8888, 2, 2, true));
        Triggers.listen(Trigger.preDraw, () -> buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight()));
    }

    public void render(RenderableProvider prov){
        renders.clear();
        prov.getRenderables(renders);

        sorter.sort(camera, renders);

        begin();
        for(Renderable render : renders){
            Graphics3DShader shader = UnityShaders.graphics3DProvider.get(render);
            shader.bind();
            shader.apply(render);
            render.meshPart.render(shader);
        }
        end();

        Pools.freeAll(renders);
    }

    public int bind(TextureAttribute attr, int bind){
        attr.texture.bind(bind);
        return bind;
    }

    protected void begin(){
        Gl.enable(Gl.cullFace);
        Gl.cullFace(Gl.back);

        Gl.enable(Gl.depthTest);
        Gl.depthFunc(Gl.less);
        Gl.depthRangef(0f, 1f);
        Gl.depthMask(true);

        buffer.begin(Color.clear);
    }

    protected void end(){
        buffer.end();
        Draw.blit(buffer.getTexture(), Shaders.screenspace);

        Gl.disable(Gl.cullFace);
        Gl.disable(Gl.depthTest);
    }
}

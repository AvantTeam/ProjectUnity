package unity.assets.type.g3d;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g3d.*;
import arc.graphics.gl.*;
import arc.struct.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;
import unity.assets.list.*;
import unity.assets.list.UnityShaders.*;
import unity.assets.type.g3d.attribute.*;
import unity.assets.type.g3d.attribute.light.*;
import unity.assets.type.g3d.attribute.type.*;
import unity.mod.*;

public final class Models{
    public static final Camera3D camera = new Camera3D();
    public static final RenderableSorter sorter = new RenderableSorter();
    public static final Environment environment = new Environment();

    protected static FrameBuffer buffer = new FrameBuffer();
    protected static RenderPool pool = new RenderPool();

    static{
        camera.perspective = false;
        camera.near = -10000f;
        camera.far = 10000f;

        environment.set(ColorAttribute.createAmbientLight(0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.56f, 0.56f, 0.56f, -1f, -1f, -0.3f));

        Triggers.listen(Trigger.preDraw, () -> buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight()));
    }

    public static void render(RenderableProvider prov){
        prov.getRenderables(pool);
        sorter.sort(camera, pool.renders);

        begin();
        for(int i = 0; i < pool.renders.size; i++){
            var r = pool.renders.items[i];

            Graphics3DShader shader = UnityShaders.graphics3DProvider.get(r);
            shader.bind();
            shader.apply(r);
            r.meshPart.render(shader);
        }
        end();

        pool.available.addAll(pool.renders);
        pool.renders.size = 0;
    }

    public static int bind(TextureAttribute attr, int bind){
        attr.texture.bind(bind);
        return bind;
    }

    protected static void begin(){
        buffer.begin(Color.clear);

        Gl.enable(Gl.cullFace);
        Gl.cullFace(Gl.back);

        Gl.enable(Gl.depthTest);
        Gl.depthFunc(Gl.lequal);
        Gl.depthRangef(camera.near, camera.far);
        Gl.depthMask(true);

        Gl.enable(Gl.blend);
        Gl.blendFunc(Blending.normal.src, Blending.normal.dst);

        Gl.clear(Gl.colorBufferBit | Gl.depthBufferBit);
    }

    protected static void end(){
        buffer.end();
        Draw.blit(buffer.getTexture(), Shaders.screenspace);

        Gl.disable(Gl.cullFace);
        Gl.disable(Gl.depthTest);
        Gl.disable(Gl.blend);
    }

    private static class RenderPool implements Prov<Renderable>{
        private final Seq<Renderable> available = new Seq<>(10000);
        private final Seq<Renderable> renders = new Seq<>(Renderable.class);

        @Override
        public Renderable get(){
            var r = available.any() ? available.pop() : new Renderable();
            renders.add(r);

            return r;
        }
    }
}

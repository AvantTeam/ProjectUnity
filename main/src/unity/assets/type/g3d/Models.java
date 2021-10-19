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

    private static final FrameBuffer buffer = new FrameBuffer(2, 2, true);
    private static final RenderPool pool = new RenderPool();

    static{
        environment.set(ColorAttribute.createAmbientLight(0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.56f, 0.56f, 0.56f, -1f, -1f, -0.3f));

        Triggers.listen(Trigger.preDraw, () -> buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight()));
    }

    public static void render(RenderableProvider prov){
        prov.getRenderables(pool);
        sorter.sort(camera, pool.renders);

        begin();
        for(int i = 0; i < pool.renders.size; i++){
            Renderable r = pool.renders.items[i];

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

    static void begin(){
        buffer.begin(Color.clear);

        Gl.depthMask(true);
        Gl.clear(Gl.depthBufferBit);

        Gl.enable(Gl.depthTest);
        Gl.enable(Gl.cullFace);
        Gl.cullFace(Gl.back);
    }

    static void end(){
        Gl.disable(Gl.depthTest);
        Gl.disable(Gl.cullFace);

        buffer.end();
        Draw.blit(buffer.getTexture(), Shaders.screenspace);
    }

    private static class RenderPool implements Prov<Renderable>{
        private final Seq<Renderable> available = new Seq<>(10);
        private final Seq<Renderable> renders = new Seq<>(Renderable.class);

        @Override
        public Renderable get(){
            Renderable r = available.any() ? available.pop() : new Renderable();
            renders.add(r);

            return r;
        }
    }
}

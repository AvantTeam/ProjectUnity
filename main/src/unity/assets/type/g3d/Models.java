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

public class Models{
    public final Camera3D camera = new Camera3D();
    public final RenderableSorter sorter = new RenderableSorter();
    public final Environment environment = new Environment();

    protected FrameBuffer buffer;
    protected RenderPool pool = new RenderPool();

    public Models(){
        camera.perspective = false;
        camera.near = -10000f;
        camera.far = 10000f;

        environment.set(ColorAttribute.createAmbientLight(0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.56f, 0.56f, 0.56f, -1f, -1f, -0.3f));

        Core.assets.loadRun("unity-models-init", Models.class, () -> {}, () -> buffer = new FrameBuffer(2, 2, true));
        Triggers.listen(Trigger.preDraw, () -> {
            if(buffer != null) buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
        });
    }

    public void render(RenderableProvider prov){
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

    public int bind(TextureAttribute attr, int bind){
        attr.texture.bind(bind);
        return bind;
    }

    protected void begin(){
        buffer.begin(Color.clear);

        Gl.enable(Gl.cullFace);
        Gl.cullFace(Gl.back);

        Gl.enable(Gl.depthTest);
        Gl.depthFunc(Gl.lequal);
        Gl.depthRangef(camera.near, camera.far);
        Gl.depthMask(true);

        Gl.clear(Gl.colorBufferBit | Gl.depthBufferBit);
    }

    protected void end(){
        buffer.end();
        Draw.blit(buffer.getTexture(), Shaders.screenspace);

        Gl.disable(Gl.cullFace);
        Gl.disable(Gl.depthTest);
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

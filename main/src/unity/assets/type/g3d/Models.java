package unity.assets.type.g3d;

import arc.graphics.*;
import arc.graphics.g3d.*;
import arc.struct.*;
import arc.util.pooling.*;
import unity.assets.type.g3d.attribute.type.*;
import unity.graphics.*;

public class Models{
    public Camera3D camera = new Camera3D();
    public RenderableSorter sorter = new RenderableSorter();
    protected Seq<Renderable> renders = new Seq<>();

    private final int count = 32;
    private final GLTexture[] textures = new GLTexture[count];
    private int currentTexture = 0;
    private boolean reused;

    public void render(RenderableProvider prov){
        renders.clear();
        prov.getRenderables(renders);

        sorter.sort(camera, renders);
        for(Renderable render : renders){
            UnityShaders.graphics3D.bind();
            UnityShaders.graphics3D.apply(render);
            render.meshPart.render(UnityShaders.graphics3D, false);

            Gl.activeTexture(GL20.GL_TEXTURE0);
        }

        Pools.freeAll(renders);
    }

    public int bind(TextureAttribute attr){
        return bind(attr.textureDescription.texture);
    }

    public int bind(GLTexture texture){
        reused = false;
        int result = bindTexture(texture);

        if(reused) Gl.activeTexture(GL20.GL_TEXTURE0 + result);
        return result;
    }

    private int bindTexture(GLTexture texture){
        for(int i = 0; i < count; i++){
            int idx = (currentTexture + i) % count;
            if(textures[idx] == texture){
                reused = true;
                return idx;
            }
        }

        currentTexture = (currentTexture + 1) % count;
        textures[currentTexture] = texture;
        texture.bind(currentTexture);

        return currentTexture;
    }
}

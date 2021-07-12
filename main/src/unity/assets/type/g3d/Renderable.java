package unity.assets.type.g3d;

import arc.math.geom.*;
import arc.util.pooling.Pool.*;
import unity.assets.type.g3d.attribute.*;
import unity.graphics.*;

public class Renderable implements Poolable{
    public final Mat3D worldTransform = new Mat3D();
    public final MeshPart meshPart = new MeshPart();

    public Material material;
    public Mat3D[] bones;

    public Object userData;

    public Renderable set(Renderable renderable){
        worldTransform.set(renderable.worldTransform);
        material = renderable.material;
        meshPart.set(renderable.meshPart);
        bones = renderable.bones;
        userData = renderable.userData;

        return this;
    }

    @Override
    public void reset(){
        material = null;
        userData = null;
        meshPart.set("", null, 0, 0, 0);
    }
}

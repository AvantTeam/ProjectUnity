package unity.assets.type.g3d;

import arc.graphics.gl.*;
import arc.math.geom.*;
import unity.assets.type.g3d.attribute.*;

public class Renderable{
    public final Mat3D worldTransform = new Mat3D();
    public final MeshPart meshPart = new MeshPart();

    public Material material;
    public Environment environment;
    public Mat3D[] bones;
    public Shader shader;

    public Object userData;

    public Renderable set(Renderable renderable){
        worldTransform.set(renderable.worldTransform);
        material = renderable.material;
        environment = renderable.environment;
        meshPart.set(renderable.meshPart);
        bones = renderable.bones;
        shader = renderable.shader;
        userData = renderable.userData;

        return this;
    }
}

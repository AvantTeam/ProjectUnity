package unity.assets.type.g3d;

import arc.graphics.*;
import arc.math.geom.*;
import arc.struct.*;
import unity.assets.type.g3d.attribute.type.*;

import java.util.*;

public class RenderableSorter implements Comparator<Renderable>{
    private Camera camera;
    private final Vec3 tmpV1 = new Vec3();
    private final Vec3 tmpV2 = new Vec3();
    private final Vec2 tmp = new Vec2();

    public void sort(Camera camera, Seq<Renderable> renderables){
        this.camera = camera;
        renderables.sort(this);
        this.camera = null;
    }

    private Vec3 getTranslation(Mat3D worldTransform, Vec3 center, Vec3 output){
        if(center.isZero()){
            worldTransform.getTranslation(output);
        }else if(!worldTransform.hasRotationOrScaling()){
            worldTransform.getTranslation(output).add(center);
        }else{
            Mat3D.prj(output.set(center), worldTransform);
        }

        return output;
    }

    @Override
    public int compare(Renderable o1, Renderable o2){
        boolean b1 = o1.material.has(BlendingAttribute.blend) && (o1.material.<BlendingAttribute>get(BlendingAttribute.blend)).blended;
        boolean b2 = o2.material.has(BlendingAttribute.blend) && (o2.material.<BlendingAttribute>get(BlendingAttribute.blend)).blended;
        if(b1 != b2) return b1 ? 1 : -1;
        getTranslation(o1.worldTransform, o1.meshPart.center, tmpV1);
        getTranslation(o2.worldTransform, o2.meshPart.center, tmpV2);
        float dst = (int)(1000f * camera.position.dst2(tmp.set(tmpV1))) - (int)(1000f * camera.position.dst2(tmp.set(tmpV2)));
        int result = dst < 0 ? -1 : (dst > 0 ? 1 : 0);
        return b1 ? -result : result;
    }
}

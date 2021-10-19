package unity.assets.type.g3d;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import unity.util.*;

public class AnimControl{
    public static final Mat3D tmp = new Mat3D();
    public final ModelInstance model;

    private boolean applying = false;

    public AnimControl(ModelInstance model){
        this.model = model;
    }

    public void begin(){
        if(applying) throw new IllegalStateException("Do not begin() twice");
        applying = true;

        model.nodes.each(node -> node.isAnimated = true);
    }

    public void end(){
        if(!applying) throw new IllegalStateException("Do not end() twice");
        applying = false;

        model.calculateTransforms();
        model.nodes.each(node -> node.isAnimated = false);
    }

    public void check(){
        if(!applying) throw new IllegalStateException("Call begin() first");
    }

    public void apply(String id, float time){
        apply(model.getAnimation(id), time);
    }

    public void apply(Animation animation, float time){
        check();

        time = Mathf.clamp(time, 0f, animation.duration);
        for(var anim : animation.nodeAnimations){
            Vec3 trns = Tmp.v31.setZero();
            Quat quat = Utils.q1.idt();
            Vec3 scl = Tmp.v32.set(1f, 1f, 1f);

            if(anim.translation != null && anim.translation.any()) trns.set(anim.translation.get(index(anim.translation, time)).value);
            if(anim.rotation != null && anim.rotation.any()) quat.set(anim.rotation.get(index(anim.rotation, time)).value);
            if(anim.scaling != null && anim.scaling.any()) scl.set(anim.scaling.get(index(anim.scaling, time)).value);

            anim.node.isAnimated = true;
            anim.node.localTransform.mul(tmp.set(trns, quat, scl));
        }
    }

    public static <T> int index(Seq<NodeKeyframe<T>> arr, float time){
        time = Math.max(time, 0f);

        int lastIndex = arr.size - 1;

        int minIndex = 0;
        int maxIndex = lastIndex;

        while(minIndex < maxIndex){
            int i = (minIndex + maxIndex) / 2;
            if(time > arr.get(i + 1).keytime){
                minIndex = i + 1;
            }else if(time < arr.get(i).keytime){
                maxIndex = i - 1;
            }else{
                return i;
            }
        }

        return minIndex;
    }
}

package unity.gensrc.entities;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.async.PhysicsProcess.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.util.*;

@SuppressWarnings({"unused", "UnnecessaryReturnStatement"})
@EntityComponent
abstract class InvisibleComp implements Hitboxc, Healthc{
    boolean invisible;
    float invisProgress;
    transient boolean invisUpdated;

    transient private float testTime;

    @Import boolean dead;
    @Import float x, y;

    @Override
    public void update(){
        if(self() instanceof Physicsc e){
            if(e.physref() == null) return;
            PhysicRef ref = e.physref();

            if(!invisible){
                if(ref.body.radius < 0f){
                    ref.body.radius = 0f;
                }
                ref.body.radius = Mathf.approachDelta(ref.body.radius, hitSize() / 2f, (hitSize() / 2f) / 60f);
            }else{
                ref.body.radius = Float.NEGATIVE_INFINITY;
            }
        }
        if(!invisUpdated){
            invisProgress = Mathf.approachDelta(invisProgress, invisible ? 1f : 0f, 0.05f);
        }
        invisUpdated = false;

        testTime += Time.delta;
        if(testTime > 2f * 60f){
            if(testTime > 4 * 60f){
                updateInvisibility(true, 0.01f);
            }else{
                updateInvisibility(false, 0.01f);
            }
            if(testTime > 7 * 60f) testTime = 0f;
        }
    }

    void updateInvisibility(boolean visible, float speed){
        invisProgress = Mathf.approachDelta(invisProgress, visible ? 0f : 1f, speed);
        invisUpdated = true;
        if(visible){
            if(invisProgress <= 0f) invisible = false;
        }else{
            if(invisProgress >= 1f) invisible = true;
        }
    }

    @MethodPriority(-1)
    @BreakAll
    @Override
    public void hitbox(Rect rect){
        if(invisible){
            Class<?> caller = ReflectUtils.caller();
            if(caller != null && QuadTree.class.isAssignableFrom(caller)){
                rect.set(x, y, Float.NaN, Float.NaN);
                return;
            }
        }
    }

    @Override
    @Replace
    public boolean isValid(){
        if(invisible){
            Class<?> caller = ReflectUtils.caller();
            if(caller != null && Player.class.isAssignableFrom(caller)){
                return !dead && isAdded();
            }
            return false;
        }
        return !dead && isAdded();
    }
}

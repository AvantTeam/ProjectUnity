package unity.gensrc.entities;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.ai.*;
import mindustry.async.PhysicsProcess.*;
import mindustry.entities.*;
import mindustry.entities.EntityCollisions.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.util.*;

@SuppressWarnings({"unused", "UnnecessaryReturnStatement"})
@EntityComponent
abstract class InvisibleComp implements Unitc{
    boolean invisible;
    float invisProgress;
    transient boolean invisUpdated;

    //transient private float testTime;

    @Import boolean dead;
    @Import float x, y;

    @Override
    public void update(){
        if(physref() != null){
            PhysicRef ref = physref();

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

        /*
        testTime += Time.delta;
        if(testTime > 2f * 60f){
            updateInvisibility(testTime > 5 * 60f, 0.02f);
            if(testTime > 9 * 60f) testTime = 0f;
        }
        */
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
    @Replace(2)
    public int pathType(){
        return invisible ? Pathfinder.costLegs : Pathfinder.costGround;
    }

    @Override
    @Replace(2)
    public SolidPred solidity(){
        return isFlying() || invisible ? null : EntityCollisions::solid;
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

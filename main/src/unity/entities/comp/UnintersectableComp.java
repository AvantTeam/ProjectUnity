package unity.entities.comp;

import arc.math.*;
import arc.math.geom.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.util.*;

@SuppressWarnings({"unused", "UnnecessaryReturnStatement"})
@EntityComponent
abstract class UnintersectableComp implements Hitboxc{
    @Import float x, y, hitSize;

    @Override
    @MethodPriority(-10)
    public void update(){
        if(this instanceof Physicsc){
            var e = (Physicsc)this;
            if(e.physref() == null) return;

            if(intersects()){
                if(e.physref().body.radius < 0f){
                    e.physref().body.radius = 0f;
                }
                e.physref().body.radius = Mathf.lerpDelta(e.physref().body.radius, hitSize() / 2f, 0.2f);
            }else{
                e.physref().body.radius = -Float.MAX_VALUE;
            }
        }
    }

    @Override
    @MethodPriority(-1)
    @BreakAll
    public void hitbox(Rect rect){
        if(!intersects()){
            Class<?> caller = ReflectUtils.classCaller();
            if(caller != null && QuadTree.class.isAssignableFrom(caller)){
                rect.set(x, y, Float.NaN, Float.NaN);
                return;
            }
        }
    }

    public abstract boolean intersects();
}

package unity.entities.comp;

import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.mod.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class TimeStopVelComp implements Unitc, Velc{
    @Import float drag;
    @Import Vec2 vel;

    @Wrap(value = "update()", block = Velc.class)
    boolean updateVel(){
        return !TimeStop.inTimeStop();
    }

    @MethodPriority(-1)
    @Override
    public void update(){
        if(!updateVel() && (!Vars.net.client() || isLocal())){
            move(vel.x * Time.delta, vel.y * Time.delta);

            vel.scl(Math.max(1f - drag * Time.delta, 0));
        }
    }
}

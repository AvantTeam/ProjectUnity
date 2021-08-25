package unity.entities;

import arc.math.*;
import mindustry.gen.*;
import unity.type.*;

public class NewTentacle{
    NewTentacleSegment root, end;
    TentacleType type;
    Unit unit;
    boolean attacking = false;
    float targetX, targetY, swayScl;

    public void update(){
        if(!attacking){
            swayScl = Mathf.lerpDelta(swayScl, 1f, 0.04f);
        }else{
            swayScl = Mathf.lerpDelta(swayScl, 1f, 0.04f);
        }
    }

    public void draw(){

    }

    public NewTentacle add(TentacleType t, Unit unit){
        type = t;
        this.unit = unit;
        NewTentacleSegment child = null;
        for(int i = 0; i < type.segments; i++){
            NewTentacleSegment seg = new NewTentacleSegment();
            if(child == null){
                root = seg;
            }else{
                child.parent = seg;
                seg.child = child;
            }
            if(i >= type.segments - 1){
                end = seg;
            }
            seg.index = i;
            child = seg;
        }
        return this;
    }

    public static class NewTentacleSegment{
        NewTentacleSegment child, parent;
        NewTentacle main;
        int index;
        float x, y, vx, vy, rotation, vrot;

        float angleLimit(){
            return index == 0 ? main.type.firstSegmentAngleLimit : main.type.angleLimit;
        }
    }
}

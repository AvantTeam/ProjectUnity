package unity.entities;

import arc.math.geom.*;

public class TriJointLeg{
    public Vec2[] joints = new Vec2[3];
    public int group;
    public boolean moving;
    public float legScl = 1f, jointLerp = 1f;
    public float stage;

    public TriJointLeg(){
        for(int i = 0; i < joints.length; i++){
            joints[i] = new Vec2();
        }
    }
}

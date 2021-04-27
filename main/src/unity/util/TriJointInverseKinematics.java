package unity.util;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;

public class TriJointInverseKinematics{
    public static void solve(float wx, float wy, Vec2[] vecs, float legLength, Vec2 target, boolean side, float jointLerp){
        jointLerp = Mathf.clamp(jointLerp);
        Tmp.v1.set(target).sub(wx, wy).limit(legLength * 3f);
        vecs[2].set(Tmp.v1).add(wx, wy);
        float angle = Tmp.v1.angle();
        float offset = Interp.sineOut.apply(Mathf.cosDeg(Mathf.mod(((Tmp.v1.len() - legLength) / (legLength * 2f)) * 90f, 360f))) * legLength * Mathf.sign(side);
        Tmp.v2.trns(angle, Tmp.v1.len() / 2f, offset).add(wx, wy);
        for(int i = 0; i < 2; i++){
            Tmp.v3.trns(angle, (legLength / 2f) * (-1 + (i * 2))).add(Tmp.v2);
            Tmp.v1.set(vecs[i]).lerp(Tmp.v3, jointLerp);
            vecs[i].set(Tmp.v1);
        }
    }
}

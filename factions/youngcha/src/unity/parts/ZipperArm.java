package unity.parts;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;

public class ZipperArm{
    //go back and forth like a zipper
    public float maxlen;
    public int joints;
    public Vec2 start = new Vec2();
    public Vec2 end = new Vec2();
    public Vec2[] jointPositions;
    Vec2 prevNorm = new Vec2();

    public ZipperArm(float sx, float sy, float tx, float ty, float maxLen, int joints){
        this.maxlen = maxLen;
        this.joints = joints;
        start.set(sx, sy);
        end.set(tx, ty);
        jointPositions = new Vec2[joints];
        for(int i = 0; i < joints; i++){
            jointPositions[i] = new Vec2();
        }
    }

    public void update(){
        Tmp.v1.set(end).sub(start);
        float d = Tmp.v1.len();
        Tmp.v1.scl(1f / d);
        float armSeg = 0.5f * maxlen / joints;
        float dSeg = 0.5f * d / joints;
        float offset = Mathf.sqrt(Math.max(0, armSeg * armSeg - dSeg * dSeg));
        float flipNor = prevNorm.dot(Tmp.v1.y, -Tmp.v1.x) > 0 ? 1 : -1;
        prevNorm.set(Tmp.v1.y * flipNor, -Tmp.v1.x * flipNor);
        for(int i = 0; i < joints; i++){
            int flip = i % 2 == 0 ? 1 : -1;
            jointPositions[i].set(
            start.x + Tmp.v1.x * dSeg * (2 * i + 1) + prevNorm.x * offset * flip,
            start.y + Tmp.v1.y * dSeg * (2 * i + 1) + prevNorm.y * offset * flip
            );
        }
    }

    public void draw(Cons3<Vec2, Vec2, Integer> con){
        int index = 0;
        for(int i = 0; i < joints; i++){
            if(i == 0){
                Tmp.v1.set(start.x, start.y);
                Tmp.v2.set(jointPositions[0].x, jointPositions[0].y);
                con.get(Tmp.v1, Tmp.v2, index++);
            }
            if(i == joints - 1){
                Tmp.v1.set(jointPositions[i].x, jointPositions[i].y);
                Tmp.v2.set(end.x, end.y);
                con.get(Tmp.v1, Tmp.v2, index++);
            }else{
                Tmp.v1.set(jointPositions[i].x, jointPositions[i].y);
                Tmp.v2.set(jointPositions[i + 1].x, jointPositions[i + 1].y);
                con.get(Tmp.v1, Tmp.v2, index++);
            }
        }

    }
}

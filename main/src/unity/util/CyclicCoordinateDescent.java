package unity.util;

import arc.math.*;
import arc.struct.*;
import arc.util.*;

/**
 * Cyclic Coordinate Descent.
 * Original code by Ryan Juckett
 * http://www.ryanjuckett.com/
 *
 * Ported in java*/
public class CyclicCoordinateDescent{
    static final float epsilon = 0.0001f;

    static float simplifyAngle(float angle){
        angle = angle % 360f;
        if(angle < -180f){
            angle += 360f;
        }else if(angle > 180f){
            angle -= 360f;
        }
        return angle;
    }

    public static void calculate(DefaultBone[] bones, float targetX, float targetY, float arrivalDist, float angleLimit, float angleLerp, boolean delta){
        int numBones = bones.length;
        if(numBones <= 0) return;

        float arrivalDistSqr = arrivalDist * arrivalDist;

        Seq<WorldBone> worldBones = new Seq<>();

        WorldBone root = new WorldBone();
        root.x = bones[0].x;
        root.y = bones[0].y;
        root.angle = bones[0].angle;
        worldBones.add(root);

        //for( int boneIdx = 1; boneIdx < numBones; boneIdx++ )
        for(int i = 1; i < numBones; i++){
            WorldBone prevWorldBone = worldBones.get(i - 1);
            DefaultBone curLocalBone = bones[i];

            WorldBone newWorldBone = new WorldBone();
            newWorldBone.x = prevWorldBone.x + prevWorldBone.cosAngle() * curLocalBone.x - prevWorldBone.sinAngle() * curLocalBone.y;
            newWorldBone.y = prevWorldBone.y + prevWorldBone.sinAngle() * curLocalBone.x + prevWorldBone.cosAngle() * curLocalBone.y;
            //curLocalBone.setX(newWorldBone.x);
            //curLocalBone.setY(newWorldBone.y);
            newWorldBone.angle = (prevWorldBone.angle + curLocalBone.angle);
            worldBones.add(newWorldBone);
        }

        float endX = worldBones.get(numBones - 1).x;
        float endY = worldBones.get(numBones - 1).y;

        for(int i = numBones - 2; i >= 0; i--){
            float curToEndX = endX - worldBones.get(i).x;
            float curToEndY = endY - worldBones.get(i).y;
            float curToEndMag = Mathf.sqrt(curToEndX * curToEndX + curToEndY * curToEndY);

            float curToTargetX = targetX - worldBones.get(i).x;
            float curToTargetY = targetY - worldBones.get(i).y;
            float curToTargetMag = Mathf.sqrt(curToTargetX * curToTargetX + curToTargetY * curToTargetY);

            float cosRotAng;
            float sinRotAng;
            float endTargetMag = curToEndMag * curToTargetMag;
            if(endTargetMag <= epsilon){
                cosRotAng = 1f;
                sinRotAng = 0f;
            }else{
                cosRotAng = (curToEndX * curToTargetX + curToEndY * curToTargetY) / endTargetMag;
                sinRotAng = (curToEndX * curToTargetY - curToEndY * curToTargetX) / endTargetMag;
            }

            float rotAng = (float)Math.acos(Mathf.clamp(cosRotAng, -1f, 1f));
            if(sinRotAng < 0f) rotAng = -rotAng;
            rotAng *= Mathf.radDeg;

            endX = worldBones.get(i).x + (cosRotAng * curToEndX) - (sinRotAng * curToEndY);
            endY = worldBones.get(i).y + (sinRotAng * curToEndX) + (cosRotAng * curToEndY);

            //float offAngle = angleLerp >= 1f ? simplifyAngle(bones[i].angle() + (rotAng * Mathf.radDeg)) : (!delta ? Mathf.slerp(bones[i].angle(), simplifyAngle(bones[i].angle() + (rotAng * Mathf.radDeg)), angleLerp) : Mathf.slerpDelta(bones[i].angle(), simplifyAngle(bones[i].angle() + (rotAng * Mathf.radDeg)), angleLerp));
            float lerpAngle = Mathf.slerp(simplifyAngle(bones[i].angle), simplifyAngle(bones[i].angle + rotAng), Mathf.clamp(angleLerp * (delta ? Time.delta : 1f)));
            float offAngle = angleLerp >= 1f ? simplifyAngle(bones[i].angle + rotAng) : lerpAngle;

            //bones[i].angle(simplifyAngle(bones[i].angle() + (rotAng * Mathf.radDeg)));
            if(angleLimit < 360f) offAngle = Utils.clampedAngle(offAngle, bones[i + 1].angle, angleLimit);
            //bones[i].angle(offAngle);
            bones[i].angle = offAngle;

            float endToTargetX = (targetX - endX);
            float endToTargetY = (targetY - endY);
            if((endToTargetX * endToTargetX) + (endToTargetY * endToTargetY) <= arrivalDistSqr){
                break;
            }
        }
    }

    public static class DefaultBone{
        public float angle, x, y;
    }

    static class WorldBone{
        float x, y, angle;

        float cosAngle(){
            return Mathf.cosDeg(angle);
        }

        float sinAngle(){
            return Mathf.sinDeg(angle);
        }
    }
}

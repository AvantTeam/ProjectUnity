package unity.util;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;

/**
 * Cyclic Coordinate Descent.
 * Original code by Ryan Juckett
 * http://www.ryanjuckett.com/
 *
 * Ported in java
 * Uses degrees instead of radians*/
public class CyclicCoordinateDescent{
    static final float epsilon = 0.0001f;

    static float simplifyAngle(float angle){
        return Mathf.mod(angle, 360f);
    }

    public static void calculate(Bone[] bones, float targetX, float targetY, float worldX, float worldY, float arrivalDist){
        calculate(bones, targetX, targetY, worldX, worldY, arrivalDist, 361f, 1f, false);
    }

    public static void calculate(Bone[] bones, float targetX, float targetY, float worldX, float worldY, float arrivalDist, float angleLimit, float angleLerp, boolean delta){
        int numBones = bones.length;
        if(numBones <= 0) return;

        targetX -= worldX;
        targetY -= worldY;

        float arrivalDistSqr = arrivalDist * arrivalDist;

        Seq<WorldBone> worldBones = new Seq<>();

        bones[0].sub(worldX, worldY);
        WorldBone root = new WorldBone();
        root.x = bones[0].getCCDPositionX();
        root.y = bones[0].getCCDPositionY();
        root.angle = bones[0].angle();
        worldBones.add(root);

        //for( int boneIdx = 1; boneIdx < numBones; boneIdx++ )
        for(int i = 1; i < numBones; i++){
            WorldBone prevWorldBone = worldBones.get(i - 1);
            Bone curLocalBone = bones[i];
            curLocalBone.sub(worldX, worldY);

            WorldBone newWorldBone = new WorldBone();
            newWorldBone.x = prevWorldBone.x + (prevWorldBone.cosAngle() * curLocalBone.getCCDPositionX()) - (prevWorldBone.sinAngle() * curLocalBone.getCCDPositionY());
            newWorldBone.y = prevWorldBone.y + (prevWorldBone.sinAngle() * curLocalBone.getCCDPositionX()) + (prevWorldBone.cosAngle() * curLocalBone.getCCDPositionY());
            //curLocalBone.setX(newWorldBone.x);
            //curLocalBone.setY(newWorldBone.y);
            newWorldBone.angle = prevWorldBone.angle + curLocalBone.angle();
            if(angleLimit < 360f) newWorldBone.angle = Utils.clampedAngle(newWorldBone.angle, prevWorldBone.angle, angleLimit);
            worldBones.add(newWorldBone);
        }

        float endX = worldBones.get(worldBones.size - 1).x;
        float endY = worldBones.get(worldBones.size - 1).y;

        for(int i = worldBones.size - 2; i >= 0; i--){
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

            endX = worldBones.get(i).x + cosRotAng * curToEndX - sinRotAng * curToEndY;
            endY = worldBones.get(i).y + sinRotAng * curToEndX + cosRotAng * curToEndY;

            float offAngle = angleLerp >= 1f ? simplifyAngle(bones[i].angle() + (rotAng * Mathf.radDeg)) : (!delta ? Mathf.slerp(bones[i].angle(), simplifyAngle(bones[i].angle() + (rotAng * Mathf.radDeg)), angleLerp) : Mathf.slerpDelta(bones[i].angle(), simplifyAngle(bones[i].angle() + (rotAng * Mathf.radDeg)), angleLerp));
            //bones[i].angle(simplifyAngle(bones[i].angle() + (rotAng * Mathf.radDeg)));
            if(angleLimit < 360f) offAngle = Utils.clampedAngle(offAngle, bones[i + 1].angle(), angleLimit);
            bones[i].angle(offAngle);
            bones[i].add(worldX, worldY);

            float endToTargetX = (targetX - endX);
            float endToTargetY = (targetY - endY);
            if((endToTargetX * endToTargetX + endToTargetY * endToTargetY <= arrivalDistSqr)){
                //bones[bones.length - 1].add(worldX, worldY);
                break;
            }
        }
        bones[bones.length - 1].add(worldX, worldY);
    }

    public interface Bone extends Position{
        float angle();
        void angle(float d);

        void setX(float x);
        void setY(float y);

        default float getCCDPositionX(){
            return getX();
        }

        default float getCCDPositionY(){
            return getY();
        }

        default void add(float x, float y){
            setX(getCCDPositionX() + x);
            setY(getCCDPositionY() + y);
        }

        default void sub(float x, float y){
            setX(getCCDPositionX() - x);
            setY(getCCDPositionY() - y);
        }
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

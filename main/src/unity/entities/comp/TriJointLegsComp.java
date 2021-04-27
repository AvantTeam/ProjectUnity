package unity.entities.comp;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.environment.*;
import unity.annotations.Annotations.*;
import unity.entities.*;
import unity.util.*;

@EntityComponent
abstract class TriJointLegsComp implements Unitc{
    transient TriJointLeg[] legs;

    transient float baseRotation;
    transient float moveSpace;
    transient float totalLength;

    @Import UnitType type;
    @Import Team team;
    @Import float x, y, deltaX, deltaY;

    @Override
    public void add(){
        resetlegs();
    }

    private void resetlegs(){
        float rot = baseRotation;
        int count = type.legCount;
        float legLength = type.legLength;
        float spacing = 360f / count;

        legs = new TriJointLeg[count];

        for(int i = 0; i < legs.length; i++){
            TriJointLeg l = new TriJointLeg();
            for(int j = 0; j < 3; j++){
                l.joints[j].trns(i * spacing + rot, (legLength / 3f) * (j + 1f)).add(this);
            }
            legs[i] = l;
        }
    }

    public float legAngle(float rotation, int index){
        return rotation + (360f / legs.length * index + (360f / legs.length / 2f));
    }

    @Override
    public void update(){
        if(Mathf.dst(deltaX, deltaY) > 0.001f){
            baseRotation = Angles.moveToward(baseRotation, Mathf.angle(deltaX, deltaY), type.baseRotateSpeed);
        }

        float rot = baseRotation;
        float legLength = type.legLength;

        if(legs == null || legs.length != type.legCount){
            resetlegs();
        }

        int div = Math.max(legs.length / type.legGroupSize, 2);
        //moveSpace(legLength / 1.6f / (div / 2f) * type.legMoveSpace);
        moveSpace = legLength / 2.4f / (div / 2f) * type.legMoveSpace;
        totalLength += Mathf.dst(deltaX, deltaY);

        float trns = moveSpace * 0.85f * type.legTrns;

        Vec2 moveOffset = Tmp.v4.trns(rot, trns);
        boolean moving = moving();

        for(int i = 0; i < legs.length; i++){
            float dstRot = legAngle(rot, i);
            Vec2 baseOffset = Tmp.v5.trns(dstRot, type.legBaseOffset).add(this);

            TriJointLeg l = legs[i];

            float stageF = (totalLength + (i * type.legPairOffset)) / moveSpace;
            int stage = (int)stageF;
            int group = stage % div;
            boolean move = i % div == group;
            boolean side = i < legs.length / 2;
            boolean backLeg = Math.abs((i + 0.5f) - (legs.length / 2f)) <= 0.501f;
            if(backLeg && type.flipBackLegs) side = !side;

            l.moving = move;
            l.stage = moving ? stageF % 1f : Mathf.lerpDelta(l.stage, 0f, 0.1f);

            if(l.group != group){
                if(!move && i % div == l.group){
                    Floor floor = Vars.world.floorWorld(l.joints[2].x, l.joints[2].y);
                    if(floor.isLiquid){
                        floor.walkEffect.at(l.joints[2].x, l.joints[2].y, type.rippleScale, floor.mapColor);
                        floor.walkSound.at(x, y, 1f, floor.walkSoundVolume);
                    }else{
                        Fx.unitLandSmall.at(l.joints[2].x, l.joints[2].y, type.rippleScale, floor.mapColor);
                    }

                    if(type.landShake > 0){
                        Effect.shake(type.landShake, type.landShake, l.joints[2]);
                    }

                    if(type.legSplashDamage > 0){
                        Damage.damage(team, l.joints[2].x, l.joints[2].y, type.legSplashRange, type.legSplashDamage, false, true);
                    }
                }
                l.group = group;
            }

            if(move){
                float moveFract = stageF % 1f;
                Tmp.v1.trns(dstRot, legLength).add(baseOffset).add(moveOffset, backLeg ? 1.5f : 1f);
                Tmp.v6.set(l.joints[2]).lerpDelta(Tmp.v1, Interp.pow2.apply(moveFract));
            }else{
                Tmp.v6.set(l.joints[2]);
            }

            Vec2 legDest = Tmp.v6;
            if(!baseOffset.within(legDest, legLength)){
                float scl = ((Math.min(baseOffset.dst(legDest), type.legLength * type.maxStretch) / legLength) - 1f);
                if(move){
                    float moveFract = stageF % 1f;
                    //l.legScl = Mathf.lerpDelta(l.legScl, 1f + scl, Mathf.lerp(type.legSpeed / 4f, 1f, moveFract));
                    l.legScl = Mathf.lerpDelta(l.legScl, 1f + scl, moveFract);
                    l.jointLerp = type.legSpeed / 4f;
                }else{
                    l.legScl = 1f + scl;
                    l.jointLerp = Mathf.lerpDelta(l.jointLerp, 1f, type.legSpeed / 2f);
                }
            }else{
                l.legScl = Mathf.lerpDelta(l.legScl, 1f, type.legSpeed / 4f);
                l.jointLerp = type.legSpeed / 4f;
                //l.legScl = Mathf.lerpDelta(l.legScl, 1f, moveFract);
            }

            TriJointInverseKinematics.solve(baseOffset.x, baseOffset.y, l.joints, (type.legLength / 3f) * l.legScl, legDest, side, move ? 1f : (baseOffset.within(legDest, legLength) ? 1f : l.jointLerp * Time.delta));
            //TriJointInverseKinematics.solve(baseOffset.x, baseOffset.y, l.joints, (type.legLength / 3f), legDest, side, 1f);
        }
    }
}

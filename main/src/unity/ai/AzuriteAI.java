package unity.ai;

import arc.func.*;
import arc.util.*;
import mindustry.ai.types.*;
import unity.entities.units.*;

@SuppressWarnings("unchecked")
public class AzuriteAI extends FlyingAI{
    protected int currentPhase = 0;
    protected float[] phaseThreshold = {1f, 0.5f, 0.25f};

    protected int currentSequence = 0;
    protected int[] sequences = {4, 3, 2};
    protected float sequenceTime = 0f;
    protected float[][] sequenceTimes = {
        {180f, 72f, 480f, 60f},
        {240f, 200f, 60f},
        {480f, 96f}
    };

    protected boolean waiting = false;
    protected float waitTime = 0f;
    protected float[] waitTimes = {60f, 45f, 30f};

    protected Cons<AzuriteUnit>[][] movements;

    protected AzuriteUnit az(){
        return (AzuriteUnit)unit;
    }

    @Override
    public void updateUnit(){
        if(!waiting){
            sequenceTime += Time.delta;

            for(int i = 0; i < phaseThreshold.length; i++){
                if(unit.health <= phaseThreshold[i] * unit.type.health){
                    currentPhase = i;
                }
            }

            if(sequenceTime >= sequenceTimes[currentPhase][currentSequence]){
                sequenceTime = 0f;
                currentSequence++;
                waiting = true;
            }

            if(currentSequence >= sequences[currentPhase]){
                currentSequence = 0;
            }
        }else{
            waitTime += Time.delta;
            if(waitTime >= waitTimes[currentPhase]){
                waitTime = 0f;
                waiting = false;
            }
        }
    }

    @Override
    public void updateMovement(){
        movements[currentPhase][currentSequence].get(az());
    }

    @Override
    protected void init(){
        movements = new Cons[3][];
        movements[0] = new Cons[4];
        movements[1] = new Cons[3];
        movements[2] = new Cons[2];

        Cons<AzuriteUnit> aggressiveFollow = unit -> {

        };

        Cons<AzuriteUnit> passiveFollow = unit -> {

        };

        Cons<AzuriteUnit> maintainRange = unit -> {

        };

        Cons<AzuriteUnit> defaultCharge = unit -> {

        };

        Cons<AzuriteUnit> fierceCharge = unit -> {

        };

        movements[0][0] = aggressiveFollow;
        movements[0][1] = passiveFollow;
        movements[0][2] = maintainRange;
        movements[0][3] = defaultCharge;

        movements[1][0] = aggressiveFollow;
        movements[1][1] = maintainRange;
        movements[1][2] = defaultCharge;

        movements[2][0] = defaultCharge;
        movements[2][1] = fierceCharge;
    }
}

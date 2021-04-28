package unity.entities.units;

import arc.math.*;
import arc.struct.*;
import arc.util.*;
import unity.annotations.Annotations.*;
import unity.entities.*;
import unity.gen.*;

@EntityPoint
public class ApocalypseUnit extends EndInvisibleUnit implements TentaclesBase{
    Seq<Tentacle> tentacles;
    private float immunity = 1f;
    private final float[] invFrames = new float[5];

    @Override
    public Seq<Tentacle> tentacles(){
        return tentacles;
    }

    @Override
    public void tentacles(Seq<Tentacle> t){
        tentacles = t;
    }

    @Override
    public void overrideAntiCheatDamage(float v){
        overrideAntiCheatDamage(v, 0);
    }

    @Override
    public void overrideAntiCheatDamage(float v, int priority){
        if(invFrames[Mathf.clamp(priority, 0, invFrames.length - 1)] < 30f) return;
        hitTime = 1f;
        invFrames[Mathf.clamp(priority, 0, invFrames.length - 1)] = 0f;
        lastHealth -= v;
        health -= v;
    }

    @Override
    public void damage(float amount){
        if(invFrame < 30f) return;
        invFrame = 0f;

        float max = Math.max(220f, type.health / 700f);
        float trueAmount = Mathf.clamp((amount / immunity), 0f, max);

        max *= 1.5f;
        immunity += Math.pow(Math.max(amount - max, 0f) / max, 2) * 2f;

        lastHealth -= trueAmount;
        superDamage(trueAmount);
    }

    @Override
    public void update(){
        super.update();

        for(int i = 0; i < invFrames.length; i++){
            invFrames[i] += Time.delta;
        }
        immunity = Math.max(1f, immunity - (Time.delta / 4f));

        updateTentacles();
    }

    @Override
    public void add(){
        if(added) return;
        super.add();
        addTentacles();
    }

    @Override
    public int classId(){
        return UnityEntityMapping.classId(ApocalypseUnit.class);
    }
}

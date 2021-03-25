package unity.entities.units;

import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.*;
import unity.content.*;

public class EndWormUnit extends WormDefaultUnit implements AntiCheatBase{
    private float invTime = 0f;
    private final float[] invTimeB = new float[5];
    private float immunity = 1f;
    private float lastHealth = 0f;
    private float lastMaxHealth = 0f;
    private float rogueDamageResist = 1f;

    @Override
    public float lastHealth(){
        return lastHealth;
    }

    @Override
    public void lastHealth(float v){
        lastHealth = v;
    }

    @Override
    public void update(){
        if(lastHealth > health) health = lastHealth;
        if(lastMaxHealth > maxHealth) maxHealth = lastMaxHealth;
        if(lastHealth > 0) dead = false;
        lastHealth = health;
        lastMaxHealth = health;
        super.update();
        invTime += Time.delta;
        for(int i = 0; i < invTimeB.length; i++){
            invTimeB[i] += Time.delta;
        }
        immunity = Math.max(1f, immunity - (Time.delta / 4f));
        rogueDamageResist = Math.max(1f, rogueDamageResist - (Time.delta / 2f));
    }

    @Override
    public void destroy(){
        if(lastHealth > 0f){
            immunity += 3500f;
            return;
        }
        super.destroy();
    }

    @Override
    public void kill(){
        if(lastHealth > 0f){
            immunity += 3500f;
            return;
        }
        super.kill();
    }

    @Override
    public void add(){
        if(added) return;
        Unity.antiCheat.addUnit(this);
        super.add();
    }

    @Override
    public void remove(){
        if(lastHealth > 0f){
            immunity += 3500f;
            return;
        }
        if(!added) return;
        Unity.antiCheat.removeUnit(this);
        for(WormSegmentUnit segmentUnit : segmentUnits){
            if(segmentUnit instanceof EndWormSegmentUnit s){
                s.removed = true;
            }
        }
        super.remove();
    }

    @Override
    public void setType(UnitType type){
        super.setType(type);
        lastHealth = lastMaxHealth = type.health;
    }

    @Override
    public void overrideAntiCheatDamage(float v, int priority){
        if(invTimeB[Mathf.clamp(priority, 0, invTimeB.length - 1)] < 30f) return;
        hitTime = 1f;
        invTimeB[Mathf.clamp(priority, 0, invTimeB.length - 1)] = 0f;
        lastHealth -= v;
        health -= v;
    }

    @Override
    public void damage(float amount){
        if(invTime < 30f) return;
        invTime = 0f;
        float max = Math.max(220f, lastMaxHealth / 700);
        float trueDamage = Mathf.clamp((amount / immunity) / rogueDamageResist, 0f, max);
        rogueDamageResist += 1.5f;
        max *= 1.5f;
        immunity += Math.pow(Math.max(amount - max, 0f) / max, 2) * 2f;
        lastHealth -= trueDamage;
        super.damage(trueDamage);
    }

    @Override
    public WormSegmentUnit newSegment(){
        return new EndWormSegmentUnit();
    }

    @Override
    public void handleCollision(Hitboxc originUnit, Hitboxc other, float x, float y){
        if(other instanceof Bullet b && b.owner != null) rogueDamageResist = 1f;
    }

    @Override
    public int classId(){
        return UnityUnitTypes.getClassId(5);
    }

    public static class EndWormSegmentUnit extends WormSegmentUnit implements AntiCheatBase{
        private boolean removed = false;

        @Override
        public void remove(){
            if(!Structs.contains(trueParentUnit.segmentUnits, this) || removed){
                super.remove();
            }
        }

        @Override
        public void overrideAntiCheatDamage(float v, int priority){
            if(trueParentUnit instanceof AntiCheatBase) ((AntiCheatBase)trueParentUnit).overrideAntiCheatDamage(v / 3f, priority);
        }

        @Override
        public float lastHealth(){
            if(trueParentUnit instanceof AntiCheatBase) return ((AntiCheatBase)trueParentUnit).lastHealth();
            return health;
        }

        @Override
        public void lastHealth(float v){
            if(trueParentUnit instanceof AntiCheatBase) ((AntiCheatBase)trueParentUnit).lastHealth(v);
        }

        @Override
        public int classId(){
            return UnityUnitTypes.getClassId(6);
        }
    }
}

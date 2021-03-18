package unity.entities.units;

import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.*;
import unity.content.*;

public class EndLegsUnit extends LegsUnit implements AntiCheatBase{
    private float lastHealth = 0f;
    private float lastMaxHealth = 0f;

    private float invTime = 0f;
    private final float[] invTimeB = new float[5];
    private float immunity = 1f;

    @Override
    public void setType(UnitType type){
        super.setType(type);
        lastHealth = lastMaxHealth = type.health;
    }

    @Override
    public void update(){
        if(lastHealth > health) health = lastHealth;
        if(lastMaxHealth > maxHealth) maxHealth = lastMaxHealth;
        if(lastHealth > 0) dead = false;
        lastHealth = health;
        lastMaxHealth = maxHealth;

        super.update();

        invTime += Time.delta;
        for(int i = 0; i < invTimeB.length; i++){
            invTimeB[i] += Time.delta;
        }
        immunity = Math.max(1f, immunity - (Time.delta / 4f));
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
        super.remove();
    }

    @Override
    public int classId(){
        return UnityUnitTypes.getClassId(7);
    }

    @Override
    public void damage(float amount){
        if(invTime < 30f) return;
        invTime = 0f;
        float max = Math.max(220f, lastMaxHealth / 700);
        float trueDamage = Mathf.clamp(amount / immunity, 0f, max);
        immunity += Math.pow(amount / max, 2) * 2f;
        lastHealth -= trueDamage;
        super.damage(trueDamage);
    }

    @Override
    public float lastHealth(){
        return lastHealth;
    }

    @Override
    public void lastHealth(float v){
        lastHealth = v;
    }

    @Override
    public void overrideAntiCheatDamage(float v, int priority){
        if(invTimeB[Mathf.clamp(priority, 0, invTimeB.length - 1)] < 30f) return;
        hitTime = 1f;
        invTimeB[Mathf.clamp(priority, 0, invTimeB.length - 1)] = 0f;
        lastHealth(lastHealth() - v);
        if(health() > lastHealth()) health(lastHealth());
    }
}

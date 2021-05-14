package unity.entities.comp;

import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.*;
import unity.annotations.Annotations.*;
import unity.type.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class EndComp implements Unitc{
    @SyncLocal private float lastHealth, lastMaxHealth;
    private float aggression = 0f;
    private float aggressionTime = 0f;
    private float[] invFrames;
    private transient int invIndex = 0;
    private transient float invTimer = 0f;
    private float resist, resistMax, resistTime;

    @Import UnitType type;
    @Import WeaponMount[] mounts;
    @Import float health, maxHealth, hitTime, healthMultiplier, shield, shieldAlpha, armor, x, y, elevation;
    @Import boolean dead;

    @Override
    public void add(){
        UnityUnitType utype = (UnityUnitType)type;
        AntiCheatVariables aType = utype.antiCheatType;

        lastHealth = type.health;
        lastMaxHealth = type.health;

        if(aType != null) invFrames = new float[aType.invincibilityArray];
    }

    @Override
    @BypassGroupCheck
    @BreakAll
    public void remove(){
        if(lastHealth > 0){
            aggression = 4f;
            aggressionTime = 10f * 60f;
            return;
        }else{
            Unity.antiCheat.removeUnit(self());
        }
    }

    @Override
    @Replace
    public float realSpeed(){
        return Mathf.lerp(1f, type.canBoost ? type.boostMultiplier : 1f, elevation) * speed() * floorSpeedMultiplier() * (aggression + 1f);
    }

    @MethodPriority(-1)
    @Override
    public void update(){
        UnityUnitType utype = (UnityUnitType)type;
        AntiCheatVariables aType = utype.antiCheatType;

        if(aType != null){
            if(health < lastHealth) health = lastHealth;
            if(maxHealth < lastMaxHealth) maxHealth = lastMaxHealth;
            if(resistTime <= 0f){
                resist -= resistMax / aType.resistDuration;
                resist = Math.max(resist, 0f);
            }else{
                resistTime -= Time.delta;
            }
            if(resist <= 0f){
                resistMax = 0f;
            }
            for(int i = 0; i < invFrames.length; i++){
                invFrames[i] = Math.max(invFrames[i] - Time.delta, 0f);
            }
        }
        if(invTimer > 0f) invTimer -= Time.delta;
        if(aggression > 0f){
            for(WeaponMount mount : mounts){
                mount.reload = Math.max(0f, mount.reload - (aggression * Time.delta));
            }
            if(aggressionTime > 0f){
                aggressionTime -= Time.delta;
            }else{
                aggression = Mathf.lerpDelta(aggression, 0f, 0.1f);
            }
        }
    }

    @Replace
    @MethodPriority(-1)
    @Override
    @BreakAll
    public void damage(float amount){
        UnityUnitType utype = (UnityUnitType)type;
        AntiCheatVariables aType = utype.antiCheatType;

        if(aType != null){
            if(invFrames[invIndex] <= 0f){
                float nextAmount = Math.min(amount, aType.maxDamageTaken);
                if(amount > aType.resistStart){
                    float a = amount - aType.resistStart;
                    resist += a;
                    resistMax = Math.max(resistMax, resist);
                    resistTime = aType.resistTime;
                    aggression += Math.min(a / (lastMaxHealth / 5f), 1.5f);
                    aggression = Math.min(aggression, 4f);
                    aggressionTime = 5f * 60f;
                }
                if(amount > aType.damageThreshold){
                    float in = 1f - aType.curveType.apply(Mathf.clamp((amount - aType.damageThreshold) / (aType.maxDamageThreshold - aType.damageThreshold)));
                    nextAmount *= in;
                }
                amount = nextAmount / ((resist * aType.resistScl) + 1f);

                invFrames[invIndex] = aType.invincibilityDuration;
                if(invTimer <= 0f){
                    invIndex++;
                    invIndex %= invFrames.length;
                    invTimer = 3f;
                }
            }else{
                return;
            }
        }
        amount = Math.max(amount - armor, Vars.minArmorDamage * amount);
        amount /= healthMultiplier;

        if(amount > 0) lastHealth -= amount;
    }

    @Override
    @Replace
    public void heal(float amount){
        health += Math.max(0f, amount);
        lastHealth = health;
        clampHealth();
    }


}

package unity.entities.comp;

import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
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

    @Override
    @Replace
    public void damage(float amount){
        UnityUnitType utype = (UnityUnitType)type;
        AntiCheatVariables aType = utype.antiCheatType;

        if(aType != null){
            if(invFrames[invIndex] < 0f){
                float nextAmount = Math.min(amount, aType.maxDamageTaken);
                if(amount > aType.resistStart){
                    float a = amount - aType.resistStart;
                    resist += a;
                    resistMax = Math.max(resistMax, resist);
                    resistTime = aType.resistTime;
                    aggression += Math.min(a / (lastMaxHealth / 10f), 2f);
                    aggression = Math.min(aggression, 4f);
                    aggressionTime = 5f * 60f;
                }
                if(amount > aType.damageThreshold){
                    float in = 1f - aType.curveType.apply(Mathf.clamp((amount - aType.damageThreshold) / (aType.maxDamageThreshold - aType.damageThreshold)));
                    nextAmount *= in;
                }
                amount = nextAmount / ((resist * aType.resistScl) + 1f);

                invFrames[invIndex] = aType.invincibilityDuration;
                invIndex++;
                invIndex %= invFrames.length;
            }else{
                return;
            }
            amount = Math.max(amount - armor, Vars.minArmorDamage * amount);
            amount /= healthMultiplier;
            hitTime = 1.0f;
            boolean hadShields = shield > 1.0e-4f;
            if(hadShields){
                shieldAlpha = 1f;
            }
            float shieldDamage = Math.min(Math.max(shield, 0f), amount);
            shield -= shieldDamage;
            amount -= shieldDamage;
            if(amount > 0){
                health -= amount;
                lastHealth -= amount;
                if(health <= 0 && !dead){
                    kill();
                }
                if(hadShields && shield <= 1.0e-4f){
                    Fx.unitShieldBreak.at(x, y, 0, this);
                }
            }
        }else{
            amount = Math.max(amount - armor, Vars.minArmorDamage * amount);
            amount /= healthMultiplier;
            hitTime = 1.0f;
            boolean hadShields = shield > 1.0e-4f;
            if(hadShields){
                shieldAlpha = 1f;
            }
            float shieldDamage = Math.min(Math.max(shield, 0f), amount);
            shield -= shieldDamage;
            amount -= shieldDamage;
            if(amount > 0){
                health -= amount;
                lastHealth -= amount;
                if(health <= 0 && !dead){
                    kill();
                }
                if(hadShields && shield <= 1.0e-4f){
                    Fx.unitShieldBreak.at(x, y, 0, this);
                }
            }
        }
    }

    @Override
    @Replace
    public void heal(float amount){
        health += Math.max(0f, amount);
        lastHealth = health;
        clampHealth();
    }
}

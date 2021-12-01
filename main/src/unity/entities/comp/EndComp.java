package unity.entities.comp;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.mod.*;
import unity.type.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class EndComp implements Unitc, Factionc{
    @SyncLocal private float trueHealth, trueMaxHealth;
    private transient Team trueTeam;
    private float aggression = 0f;
    private float aggressionTime = 0f;
    private float[] invFrames;
    private transient int invIndex = 0;
    private transient float invTimer = 0f;
    private float resist, resistMax, resistTime;

    @Import UnitType type;
    @Import WeaponMount[] mounts;
    @Import Team team;
    @Import float health, maxHealth, hitTime, healthMultiplier, speedMultiplier, shield, shieldAlpha, armor, x, y, elevation, rotation;
    @Import boolean dead;

    @Override
    public Faction faction(){
        return FactionMeta.map(type);
    }

    @Override
    public void add(){
        UnityUnitType utype = (UnityUnitType)type;
        AntiCheatVariables aType = utype.antiCheatType;

        trueHealth = type.health;
        trueMaxHealth = type.health;
        trueTeam = team;

        Unity.antiCheat.addUnit(self());

        if(aType != null) invFrames = new float[aType.invincibilityArray];
    }

    @Override
    @BypassGroupCheck
    @BreakAll
    public void remove(){
        if(trueHealth > 0){
            aggression = 4f;
            aggressionTime = 10f * 60f;
            return;
        }else{
            Unity.antiCheat.removeUnit(self());
        }
    }

    float trueHealth(){
        return trueHealth;
    }

    float trueMaxHealth(){
        return trueMaxHealth;
    }

    @Insert(value = "update()", after = false)
    @Extend(Wormc.class)
    private void updateHealthWorm(){
        Wormc h = self();
        if(!h.isHead() && h.head() != null){
            Endc e = h.head().as();
            health = trueHealth = e.trueHealth();
            maxHealth = trueMaxHealth = e.trueMaxHealth();
        }
    }

    @MethodPriority(-1)
    @Override
    public void update(){
        UnityUnitType utype = (UnityUnitType)type;
        AntiCheatVariables aType = utype.antiCheatType;

        if(aType != null){
            if(team != trueTeam && trueHealth >= Math.max(trueMaxHealth / 100f, 150f)) team = trueTeam;
            if(health < trueHealth || Float.isNaN(health)) health = trueHealth;
            trueHealth = health;
            if(maxHealth < trueMaxHealth || Float.isNaN(maxHealth)) maxHealth = trueMaxHealth;
            trueMaxHealth = maxHealth;
            if(trueHealth > 0f) dead = false;
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

    @Override
    @Replace
    public void rotateMove(Vec2 vec){
        moveAt(Tmp.v2.trns(rotation, vec.len()));

        if(!vec.isZero()){
            rotation = Angles.moveToward(rotation, vec.angle(), type.rotateSpeed * (aggression + 1f) * Math.max(Time.delta, 1));
        }
    }

    @Insert(value = "update()", block = Statusc.class)
    void updateAggroSpeed(){
        speedMultiplier *= aggression + 1f;
    }

    @Replace(100)
    @MethodPriority(-1)
    @Override
    @BreakAll
    public void damagePierce(float amount, boolean withEffect){
        float pre = hitTime;

        damage(amount);

        if(!withEffect){
            hitTime = pre;
        }
        //if(true) return;
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
                    if(Float.isInfinite(resist)) resist = Float.MAX_VALUE;
                    resistMax = Math.max(resistMax, resist);
                    resistTime = aType.resistTime;
                    aggression += Math.min(a / (trueMaxHealth / 5f), 1.5f);
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
        float tmpAmount = Math.max(amount - armor, Vars.minArmorDamage * amount) / healthMultiplier;

        if(tmpAmount > 0){
            float shieldDamage = Math.min(Math.max(shield, 0), tmpAmount);
            tmpAmount -= shieldDamage;

            if(tmpAmount > 0){
                trueHealth -= tmpAmount;
            }
        }
    }

    @Override
    @Replace
    public void heal(float amount){
        health += Math.max(0f, amount);
        trueHealth = health;
        clampHealth();
    }
}

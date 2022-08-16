package unity.gensrc.entities;

import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.entities.prop.*;
import unity.entities.type.*;
import unity.gen.entities.*;
import unity.mod.*;

@EntityComponent
abstract class EndComp implements Unitc, Factionc{
    private float trueHealth, trueMaxHealth;
    private float invframes, invDamage;
    private transient float recievedDamage;
    private transient int activeFrame;

    private transient EndProps props;

    @Import UnitType type;
    @Import float health, maxHealth, armor, healthMultiplier, shield, shieldAlpha, x, y, hitTime;
    @Import Team team;
    @Import boolean dead;

    @Override
    public Faction faction(){
        return Faction.end;
    }

    @Override
    public void add(){
        if(trueHealth <= 0f) trueHealth = health;
        if(trueMaxHealth <= 0f) trueMaxHealth = maxHealth;
        if(props != null && props.shouldAdd){
            EndProps.add.get(self());
        }
    }

    @Override
    @BypassGroupCheck
    @BreakAll
    public void remove(){
        if(props.shouldAdd){
            if(trueHealth > 0){
                return;
            }else{
                EndProps.remove.get(self());
            }
        }
    }

    @Override
    @MethodPriority(-2)
    public void update(){
        if(health > trueHealth) trueHealth = health;
        health = trueHealth;
        maxHealth = trueMaxHealth;
        if(trueHealth > 0) dead = false;

        activeFrame++;
        //recievedDamage = 0f;
        if(invframes > 0f) invframes -= Time.delta;
        if(invframes <= 0f){
            recievedDamage = 0f;
        }
    }

    @Override
    public void setType(UnitType type){
        if(type instanceof PUUnitTypeCommon def) props = def.propReq(EndProps.class);
    }

    int activeFrame(){
        return activeFrame;
    }

    float calculateEndDamage(float amount){
        float trueAmount = amount;
        //invDamage += amount;
        if(amount > props.maxDamage){
            amount *= Mathf.clamp(1f - (amount - props.maxDamage) / props.maxDamageCurve);
        }
        float ndamage = amount - recievedDamage;
        if(invframes > 0f && (ndamage <= 0f || recievedDamage <= 0f)){
            return -1f;
        }
        invDamage += trueAmount;
        if(ndamage > 0f && recievedDamage > 0f){
            float a = amount;
            amount = ndamage;
            recievedDamage = a;
        }
        if(invDamage >= props.invincibilityTrigger && invframes <= 0f){
            invframes = props.invincibilityFrames;
            invDamage = 0f;
            recievedDamage = amount;
        }
        //trueHealth -= amount;
        return amount;
    }

    private void damageRaw(float amount){
        amount = calculateEndDamage(amount);
        if(amount < 0) return;

        boolean hadShields = shield > 0.0001f;

        if(hadShields){
            shieldAlpha = 1f;
        }

        float shieldDamage = Math.min(Math.max(shield, 0), amount);
        shield -= shieldDamage;
        hitTime = 1f;
        amount -= shieldDamage;

        if(amount > 0 && type.killable){
            health -= amount;
            trueHealth -= amount;
            if(health <= 0 && !dead){
                kill();
            }

            if(hadShields && shield <= 0.0001f){
                Fx.unitShieldBreak.at(x, y, 0, team.color, this);
            }
        }
    }

    @Replace(2)
    //@MethodPriority(-1)
    @Override
    //@BreakAll
    public void damage(float amount){
        /*
        invDamage += amount;
        if(amount > props.maxDamage){
            amount *= Mathf.clamp(1f - (amount - props.maxDamage) / props.maxDamageCurve);
        }
        if(invframes > 0f && amount < recievedDamage){
            return;
        }
        if(amount > recievedDamage && recievedDamage > 0f){
            float a = amount;
            amount -= recievedDamage;
            recievedDamage = a;
        }
        if(invDamage >= props.invincibilityTrigger && invframes <= 0f){
            invframes = props.invincibilityFrames;
            invDamage = 0f;
            recievedDamage = amount;
        }
        trueHealth -= amount;
         */
        damageRaw(Damage.applyArmor(amount, armor) / healthMultiplier);
    }

    @Replace(2)
    //@MethodPriority(-1)
    @Override
    //@BreakAll
    public void damagePierce(float amount, boolean withEffect){
        /*
        amount = calculateEndDamage(amount);
        if(amount < 0){
            return;
        }
        trueHealth -= amount;
         */
        float pre = hitTime;

        damageRaw(amount);

        if(!withEffect){
            hitTime = pre;
        }
    }
}

package unity.entities.abilities;

import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class LightningBurstAbility extends Ability{
    protected float rechargeTime = 60f;
    protected int bolts = 8;
    protected float maxDelay = 8f;
    protected float damage = 17f;
    protected int length = 14;
    protected Color color = Pal.lancerLaser;
    //whether the fully recharged fx is visible to anyone else
    protected boolean rechargeVisible = true;
    //whether the "not yet" fx is visible to anyone else
    protected boolean chargingVisible = false;
    protected Effect rechargeFx = UnityFx.ringFx;
    
    protected float timer;
    protected boolean used;
    protected boolean check;
    protected int left;
    
    public LightningBurstAbility(){};
    
    public LightningBurstAbility(float rechargeTime, int bolts, float maxDelay, float damage, int length, Color color){
        this.rechargeTime = rechargeTime;
        this.bolts = bolts;
        this.maxDelay = maxDelay;
        this.damage = damage;
        this.length = length;
    }
    
    public LightningBurstAbility(float rechargeTime, int bolts, float maxDelay, float damage, int length, Color color, boolean rechargeVisible, boolean chargingVisible, Effect rechargeFx){
        this.rechargeTime = rechargeTime;
        this.bolts = bolts;
        this.maxDelay = maxDelay;
        this.damage = damage;
        this.length = length;
        this.color = color;
        this.rechargeVisible = rechargeVisible;
        this.chargingVisible = chargingVisible;
        this.rechargeFx = rechargeFx;
    }
    
    public boolean able(Unit u){
        return !u.isFlying();
    }
    
    public void used(Unit u){
        Effect.shake(1, 1, u);
        Fx.landShock.at(u);
        for(var i = 0; i < bolts; i++){
            final int fish = i;
            Time.run(Mathf.random(maxDelay), () -> {
              Lightning.create(u.team, Pal.lancerLaser, damage, u.x, u.y, Mathf.random(360), length);
              Effect.shake(fish * 0.25f, fish * 0.25f, u);
              Sounds.spark.at(u.x, u.y, 1.25f, 0.75f);
            });
        }
    }
    
    public void notYet(Unit u, float whenReady){
        Object[] data = {rechargeTime, u};
        if(chargingVisible || (u.isPlayer() && u.getPlayer() == player)) UnityFx.waitFx.at(u.x, u.y, whenReady, color, data);
    }
    
    public float getCool(){
        return timer;
    }
    
    public void usedCool(float a){
        timer = a;
        used = true;
    }
    
    public boolean getUse(){
    if(used){
        used = false;
        return true;
    }
    return false;
    }
    
    public boolean useCheck(boolean b){
        if(b){
            if(!check){
                check = true;
                return true;
            }
        }
        else check = false;
        return false;
    }
    
    public void tryUse(Unit u){
        if(getCool() + rechargeTime > Time.time) notYet(u, getCool() + rechargeTime);
        else{
          usedCool(Time.time);
          used(u);
        }
    }
    
    @Override
    public void update(Unit u){
        if(useCheck(able(u))) tryUse(u);

        if(rechargeFx != Fx.none && getCool() + rechargeTime < Time.time && getUse() && (rechargeVisible || (u.isPlayer() && u.getPlayer() == player))) rechargeFx.at(u.x, u.y, 0, color, u);
    }

    @Override
    public String localized(){
        return bundle.get("ability.lightning-burst");
    }
}
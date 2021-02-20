package unity.entities.abilities;

import arc.func.*;
import arc.graphics.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import unity.*;
import unity.content.*;
import unity.mod.TapHandler.*;

import static mindustry.Vars.*;

public abstract class BaseAbility extends Ability implements TapListener{
    public boolean useSlots = true;
    /** If true, ability will use {@link #rechargeTime} and calls {@link #use(Unit, float, float)}.
     * Otherwise, it will call {@link #updatePassive(Unit)} like all other normal abilities.
     */
    public boolean interactive = false;
    /**
     * Whether {@link #use(Unit)} will only activate when player taps the screen / presses
     * {@link Binding.boost}.
     * 
     * Only valid if {@link #interactive} is true
     */
    public boolean useTap = false;

    public int slots = 3;
    public int slot = 0;
    public Effect slotEffect;

    public Color color = Pal.lancerLaser;
    public Effect waitEffect = UnityFx.waitEffect;

    public boolean chargeVisible = true;

    /** Time used to recharge all the slots when all have been used */
    public float rechargeTime = 60f;
    public float rechargeProgress = 0f;
    public Effect rechargeEffect = UnityFx.ringFx;

    /** Delay between each slot usage */
    public float delayTime = 30f;
    public float delayProgress = 0f;
    public Effect delayEffect = UnityFx.smallRingFx;

    protected Boolf<Unit> able;
    protected boolean waited;

    protected BaseAbility(Boolf<Unit> able, boolean interactive, boolean useTap){
        this.able = able;
        this.interactive = interactive;
        this.useTap = useTap;
    }

    @Override
    public Ability copy(){
        try{
			BaseAbility ability = (BaseAbility)clone();
            if(ability.interactive && ability.useTap){
                Unity.tapHandler.addListener(ability);
            }

            return ability;
		}catch(CloneNotSupportedException e){
			throw new RuntimeException(e);
		}
    }

    public boolean able(Unit unit){
        if(unit.isPlayer() && useTap){
            return false;
        }else{
            return able.get(unit);
        }
    }

    @Override
    public void tap(Player player, float x, float y){
        Unit unit = player.unit();
        if(!unit.isValid() || !unit.abilities.contains(this)){
            Unity.tapHandler.removeListener(this);
        }else{
            if(useSlots && delayProgress >= delayTime){
                use(unit, x, y);
                return;
            }else if(rechargeProgress >= rechargeTime){
                use(unit, x, y);
                return;
            }

            hold(unit);
        }
    }

    public void use(Unit unit){
        use(unit, Float.NaN, Float.NaN);
    }

    public void use(Unit unit, float x, float y){
        if(useSlots){
            delayProgress = 0f;
            waited = false;
            slot++;
        }else{
            rechargeProgress = 0f;
        }
    }

    public void hold(Unit unit){
        if(chargeVisible || (unit.isPlayer() && unit.getPlayer() == player)){
            waitEffect.at(unit.x, unit.y, unit.rotation, color, new WaitEffectData(unit));
        }
    }

    @Override
    public void update(Unit unit){
        if(interactive){
            updateInteractive(unit);
        }else{
            updatePassive(unit);
        }
    }

    public void updateInteractive(Unit unit){
        if(useSlots){
            if(slot >= slots){
                rechargeProgress = Math.min(rechargeProgress + Time.delta, rechargeTime);
                if(rechargeProgress >= rechargeTime){
                    delayProgress = delayTime;
                    rechargeProgress = 0f;
                    slot = 0;

                    if(chargeVisible || (unit.isPlayer() && unit.getPlayer() == player)){
                        rechargeEffect.at(unit.x, unit.y, 0f, color, unit);
                    }
                }
            }else{
                delayProgress = Math.min(delayProgress + Time.delta, delayTime);
                if(delayProgress >= delayTime){
                    if(!waited && (chargeVisible || (unit.isPlayer() && unit.getPlayer() == player))){
                        waited = true;
                        delayEffect.at(unit.x, unit.y, 0f, color, unit);
                    }

                    if(able(unit)){
                        delayProgress = 0f;
                        slot++;

                        use(unit);
                    }
                }
            }
        }else{
            rechargeProgress = Math.min(rechargeProgress + Time.delta, rechargeTime);
            if(rechargeProgress >= rechargeTime && able(unit)){
                rechargeProgress = 0f;
                use(unit);
            }
        }
    }

    public void updatePassive(Unit unit){}

    public class WaitEffectData{
        protected Unit unit;

        public WaitEffectData(Unit unit){
            this.unit = unit;
        }

        public float progress(){
            if(useSlots){
                if(slot >= slots){
                    return rechargeProgress / rechargeTime;
                }else{
                    return delayProgress / delayTime;
                }
            }else{
                return rechargeProgress / rechargeTime;
            }
        }

        public Unit unit(){
            return unit;
        }
    }
}

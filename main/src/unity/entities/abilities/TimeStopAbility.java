package unity.entities.abilities;

import arc.audio.*;
import arc.func.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import unity.content.effects.*;
import unity.gen.*;
import unity.graphics.*;
import unity.mod.*;

public class TimeStopAbility extends BaseAbility{
    float duration;
    Sound timeStopSound = UnitySounds.stopTime, timeStopSoundShort = Sounds.none;
    Effect timeStopEffect = SpecialFx.timeStop;
    boolean update = true;

    public TimeStopAbility(Boolf<Unit> able, float duration, float rechargeTime){
        super(able, true, true);
        useSlots = false;
        color = UnityPal.scarColor;
        this.duration = duration;
        this.rechargeTime = rechargeTime;
    }

    @Override
    protected boolean shouldRecharge(Unit unit){
        return !TimeStop.inTimeStop();
    }

    @Override
    public void use(Unit unit, float x, float y){
        if(!update) return;
        super.use(unit, x, y);
        if(unit.isPlayer() || TimeStop.inTimeStop()){
            timeStopSound.at(unit.x, unit.y);
            TimeStop.addEntity(unit, duration);
            timeStopEffect.at(unit.x, unit.y);
        }else{
            timeStopSoundShort.at(unit.x, unit.y);
            timeStopEffect.at(unit.x, unit.y);
            float delta = Time.delta;
            Time.delta = 3f;
            update = false;

            for(float i = 0; i < duration; i += Time.delta){
                unit.update();
            }

            Time.delta = delta;
            update = true;
        }
    }
}

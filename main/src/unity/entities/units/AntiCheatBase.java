package unity.entities.units;

import mindustry.gen.*;

public interface AntiCheatBase extends Unitc{
    float lastHealth();

    void lastHealth(float v);

    default void overrideAntiCheatDamage(float v){
        overrideAntiCheatDamage(v, 0);
    }

    default void overrideAntiCheatDamage(float v, int priority){
        lastHealth(lastHealth() - v);
        if(health() > lastHealth()) health(lastHealth());
    }
}

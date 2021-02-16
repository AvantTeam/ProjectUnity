package unity.entities.units;

import mindustry.gen.*;

public interface AntiCheatBase extends Unitc{
    float lastHealth();

    void lastHealth(float v);

    default void overrideAntiCheatDamage(float v){
        lastHealth(lastHealth() - v);
        if(health() > lastHealth()) health(lastHealth());
    }
}

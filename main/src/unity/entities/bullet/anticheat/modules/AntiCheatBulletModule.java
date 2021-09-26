package unity.entities.bullet.anticheat.modules;

import mindustry.entities.abilities.*;
import mindustry.gen.*;

public interface AntiCheatBulletModule{
    default void hitUnit(Unit unit, Bullet bullet){

    }

    default void hitBuilding(Building build, Bullet bullet){

    }

    default void handleAbility(Ability ability, Unit unit, Bullet bullet){

    }

    default float getUnitData(Unit unit){
        return 0f;
    }

    default void handleUnitPost(Unit unit, Bullet bullet, float data){

    }
}

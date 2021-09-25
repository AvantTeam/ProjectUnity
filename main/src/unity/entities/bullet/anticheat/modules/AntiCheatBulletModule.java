package unity.entities.bullet.anticheat.modules;

import mindustry.entities.abilities.*;
import mindustry.gen.*;

public interface AntiCheatBulletModule{
    default void hitUnit(Unit unit){

    }

    default void hitBuilding(Building build){

    }

    default void handleAbility(Ability ability, Unit unit){

    }
}

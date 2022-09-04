package unity.entities.type.bullet.effects;

import mindustry.entities.abilities.*;
import mindustry.gen.*;

public interface EndBulletEffects{
    default float getData(Unit u){
        return 0f;
    }

    default void hitUnit(Unit unit, Bullet b){

    }
    default void hitBuilding(Building build, Bullet b){

    }

    default void handleAbility(Ability ability, Unit unit, Bullet b){

    }

    default void handleUnitPost(Unit unit, Bullet b, float data){

    }
}

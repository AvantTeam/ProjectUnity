package unity.entities;

import mindustry.entities.units.*;
import mindustry.gen.*;

public interface Soul{
    int souls();
    int maxSouls();

    default boolean canJoin(){
        return souls() < maxSouls();
    }

    default boolean hasSouls(){
        return souls() > 0;
    }

    default int acceptSoul(Entityc other){
        Soul soul = toSoul(other);
        if(soul != null){
            return acceptSoul(soul);
        }else{
            return 0;
        }
    }

    default int acceptSoul(Soul other){
        return acceptSoul(other.souls());
    }

    default int acceptSoul(int amount){
        return Math.min(maxSouls() - souls(), amount);
    }

    void join();
    void unjoin();
    default void joined(){}

    default float soulf(){
        return souls() / (float)maxSouls();
    }

    static boolean isSoul(Object e){
        return toSoul(e) != null;
    }

    static Soul toSoul(Object e){
        if(e instanceof UnitController cont) e = cont.unit();
        if(e instanceof BlockUnitc unit) e = unit.tile();
        if(e instanceof Soul soul) return soul;

        return null;
    }
}

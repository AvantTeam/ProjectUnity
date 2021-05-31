package unity.entities;

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

    default float soulf(){
        return souls() / (float)maxSouls();
    }

    static boolean isSoul(Entityc e){
        if(e instanceof BlockUnitc unit) return unit.tile() instanceof Soul;
        return e instanceof Soul;
    }

    static Soul toSoul(Entityc e){
        if(e instanceof BlockUnitc unit && unit.tile() instanceof Soul soul) return soul;
        if(e instanceof Soul soul) return soul;
        return null;
    }
}

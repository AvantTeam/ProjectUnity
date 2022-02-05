package unity.world.blocks.exp;

public interface ExpHolder {
    int getExp();
    int handleExp(int amount);

    default int unloadExp(int amount){
        return 0;
    }

    default boolean handleOrb(int orbExp){
        return handleExp(orbExp) > 0;
    }

    default boolean acceptOrb(){
        return false;
    }

    default int handleTower(int amount, float angle){
        return handleExp(amount);
    }
}

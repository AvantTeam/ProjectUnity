package unity.world.meta;

import mindustry.type.*;

public class CrucibleData{
    public final int id;
    public final Item item;

    public float volume, meltedRatio;

    public CrucibleData(int id, float volume, float meltedRatio, Item item){
        this.id = id;
        this.volume = volume;
        this.meltedRatio = meltedRatio;
        this.item = item;
    }
}

package unity.world;

import arc.util.io.*;
import mindustry.world.modules.*;

public class EndEssenceModule extends BlockModule{
    public float essence = 0;

    @Override
    public void write(Writes write){
        write.f(essence);
    }

    @Override
    public void read(Reads read){
        essence = read.f();
    }
}

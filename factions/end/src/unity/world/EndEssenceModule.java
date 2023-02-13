package unity.world;

import arc.struct.*;
import arc.util.io.*;
import mindustry.world.modules.*;

public class EndEssenceModule extends BlockModule{
    public float essence, lastEssence = -1;
    public IntSeq outputs = new IntSeq(4);

    @Override
    public void write(Writes write){
        write.f(essence);
        write.s(outputs.size);
        for(int i = 0; i < outputs.size; i++){
            write.i(outputs.get(i));
        }
    }

    @Override
    public void read(Reads read){
        essence = read.f();
        int l = read.s();
        outputs.clear();
        for(int i = 0; i < l; i++){
            outputs.add(read.i());
        }
    }
}

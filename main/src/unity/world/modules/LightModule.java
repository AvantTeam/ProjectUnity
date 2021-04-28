package unity.world.modules;

import arc.util.io.*;
import mindustry.world.modules.*;
import unity.world.blocks.light.*;

public class LightModule extends BlockModule{
    public float status = 0.0f;
    public LightGraph graph = new LightGraph();

    @Override
    public void write(Writes write){
        
    }
}

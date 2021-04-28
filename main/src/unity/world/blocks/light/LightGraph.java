package unity.world.blocks.light;

import arc.struct.*;
import unity.gen.*;

public class LightGraph{
    private Seq<Light> lights = new Seq<>();

    public void add(Light light){
        if(!lights.contains(light, false)){
            lights.add(light);
        }
    }

    public void update(){
        lights.removeAll(light -> !light.isAdded());
    }
}

package unity.async;

import mindustry.async.*;
import mindustry.gen.*;
import unity.gen.*;

public class LightProcess implements AsyncProcess{
    private volatile boolean processing = false;

    @Override
    public void process(){
        processing = true;

        for(var e : Groups.draw){
            if(e instanceof Light light){
                light.walk();
            }
        }

        processing = false;
    }

    @Override
    public boolean shouldProcess(){
        return !processing;
    }
}

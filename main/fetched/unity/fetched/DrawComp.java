package unity.fetched;

import unity.annotations.Annotations.*;
import mindustry.gen.*;

@EntityComponent(write = false)
abstract class DrawComp implements Posc{

    float clipSize(){
        return Float.MAX_VALUE;
    }

    void draw(){

    }
}

package unity.entities.comp;

import arc.*;
import arc.util.io.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.mod.*;
import unity.mod.Triggers.*;

@SuppressWarnings({"unused"})
@EntityDef(value = {Unintersectablec.class, Drawc.class, WorldListenerc.class}, serialize = false)
@EntityComponent
abstract class WorldListenerComp implements Unintersectablec, Drawc{
    @Override
    public void draw(){
        Events.fire(Triggers.drawEnt);
    }

    @Override
    public void write(Writes writes){
        Events.fire(new SaveWriteEvent());
    }

    @Override
    public void read(Reads reads){
        Events.fire(new SaveReadEvent());
    }

    @Override
    @Replace
    public float clipSize(){
        return Float.MAX_VALUE;
    }

    @Override
    public boolean intersects(){
        return false;
    }
}

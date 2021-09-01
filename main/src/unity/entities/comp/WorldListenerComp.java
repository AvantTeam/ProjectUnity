package unity.entities.comp;

import arc.*;
import arc.util.io.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.mod.*;
import unity.mod.Triggers.*;

@SuppressWarnings({"unused"})
@EntityDef({Unintersectablec.class, Drawc.class, WorldListenerc.class})
@EntityComponent
abstract class WorldListenerComp implements Unintersectablec, Drawc{
    static WorldListener instance;

    @Override
    public void draw(){
        Events.fire(Triggers.drawEnt);
    }

    @Override
    public void write(Writes write){
        Events.fire(new SaveWriteEvent());
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

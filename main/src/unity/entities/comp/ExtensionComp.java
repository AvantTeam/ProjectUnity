package unity.entities.comp;

import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.entities.*;
import unity.gen.*;
import unity.util.*;

/** @author GlennFolker */
@EntityDef(base = UnitEntity.class, value = {Extensionc.class}, serialize = false)
@EntityComponent(base = true)
abstract class ExtensionComp implements Drawc{
    ExtensionHolder holder;

    @Override
    public void add(){
        if(holder == null) remove();
    }

    @Override
    @Replace
    public void draw(){
        if(holder != null){
            holder.drawExt();
        }
    }

    @Override
    @Replace
    public float clipSize(){
        return holder != null ? holder.clipSizeExt() : 0f;
    }
}

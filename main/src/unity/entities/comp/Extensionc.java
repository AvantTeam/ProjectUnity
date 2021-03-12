package unity.entities.comp;

import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.entities.*;
import unity.util.*;

public interface Extensionc extends Drawc{
    ExtensionHolder holder();
    void holder(ExtensionHolder holder);

    @Override
    @Replace
    default void add(){
        if(!isAdded() && holder() != null){
            Groups.draw.add(this);
            Utils.setField(this, Utils.findField(getClass(), "added", true), true);
        }
    }

    @Override
    @Replace
    default void remove(){
        if(isAdded()){
            Groups.draw.remove(this);
            Utils.setField(this, Utils.findField(getClass(), "added", true), false);
        }
    }

    @Override
    @Replace
    default void draw(){
        if(holder() != null){
            holder().drawExt();
        }
    }

    @Override
    @Replace
    default float clipSize(){
        if(holder() != null){
            return holder().clipSizeExt();
        }
        return 0f;
    }
}

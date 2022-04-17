package unity.entities.comp;

import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

/** @author GlennFolker */
@SuppressWarnings("unused")
@EntityComponent
@EntityDef({Trnsc.class, Posc.class, Rotc.class})
abstract class TrnsComp implements Posc, Rotc{
    @Nullable transient Posc parent;

    @Import float x, y, rotation;
    transient float offsetX, offsetY, offsetRot;

    @Override
    public void update(){
        if(parent != null){
            float px = parent.getX(), py = parent.getY();
            if(parent instanceof Rotc rot){
                float r = rot.rotation();

                x = px + Angles.trnsx(r - 90f, offsetX, offsetY);
                y = py + Angles.trnsy(r - 90f, offsetX, offsetY);
                rotation = r + offsetRot;
            }else{
                x = px + offsetX;
                y = py + offsetY;
            }
        }
    }
}

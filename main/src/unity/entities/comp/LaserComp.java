package unity.entities.comp;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.entities.*;
import unity.gen.*;
import unity.type.*;

/** @author GlennFolker */
@EntityComponent
abstract class LaserComp implements Unitc, ExtensionHolder{
    transient Extension ext;
    transient Teamc target;

    transient float laserX;
    transient float laserY;
    transient float strength;

    @Import float x, y, rotation, hitSize;
    @Import UnitType type;

    @Override
    public void add(){
        Extension ext = Extension.create();
        ext.holder = this;
        ext.set(x, y);
        ext.add();
    }

    @Override
    public void remove(){
        if(ext != null){
            ext.remove();
            ext = null;
        }
    }

    @Override
    public void drawExt(){
        if(strength > 0.1f){
            UnityUnitType type = (UnityUnitType)this.type;
            float focusLen = hitSize / 2f + Mathf.absin(Time.time, 1.1f, 0.5f);

            float px = x() + Angles.trnsx(rotation, focusLen);
            float py = y() + Angles.trnsy(rotation, focusLen);

            Draw.z(Layer.bullet);

            Draw.mixcol(type.laserColor, Mathf.absin(4f, 0.6f));
            Drawf.laser(team(), type.laserRegion, type.laserEndRegion, px, py, laserX, laserY, strength * type.laserWidth);
            Draw.mixcol();
        }
    }

    @Override
    public float clipSizeExt(){
        if(Float.isNaN(laserX) || Float.isNaN(laserY)) return 0f;
        return dst(laserX, laserY) * 2f;
    }
}

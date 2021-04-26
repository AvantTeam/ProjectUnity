package unity.entities.comp;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.entities.*;
import unity.gen.*;
import unity.type.*;

public interface Laserc extends Unitc, ExtensionHolder {
    Extensionc ext();
    void ext(Extensionc ext);

    Teamc target();
    void target(Teamc target);

    float laserX();
    void laserX(float laserX);

    float laserY();
    void laserY(float laserY);

    float strength();
    void strength(float strength);

    @Override
    @MethodPriority(-1)
    default void add() {
        if(!isAdded()) {
            Extensionc ext = (Extensionc)UnityUnitTypes.extension.create(team());
            ext.holder(this);
            ext.set(x(), y());
            ext.add();
        }
    }

    @Override
    default void remove() {
        if(isAdded() && ext() != null) {
            ext().remove();
            ext(null);
        }
    }

    @Override
    default void drawExt() {
        if(strength() > 0.1f){
            UnityUnitType type = (UnityUnitType)type();
            float focusLen = hitSize() / 2f + Mathf.absin(Time.time, 1.1f, 0.5f);

            float px = x() + Angles.trnsx(rotation(), focusLen);
            float py = y() + Angles.trnsy(rotation(), focusLen);

            Draw.z(Layer.bullet);

            Draw.mixcol(type.laserColor, Mathf.absin(4f, 0.6f));
            Drawf.laser(team(), type.laserRegion, type.laserEndRegion, px, py, laserX(), laserY(), strength() * type.laserWidth);
            Draw.mixcol();
        }
    }

    @Override
    default float clipSizeExt() {
        if(Float.isNaN(laserX()) || Float.isNaN(laserY())) return 0f;
        return dst(laserX(), laserY()) * 2f;
    }
}

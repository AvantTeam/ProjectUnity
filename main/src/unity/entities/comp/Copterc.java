package unity.entities.comp;

import mindustry.gen.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import unity.annotations.Annotations.*;
import unity.type.*;

/**
 * @author GlennFolker
 * @author MEEPofFaith
 */
public interface Copterc extends Unitc{
    @Initialize("1f")
    float rotorSpeedScl();
    void rotorSpeedScl(float scl);

    @Initialize(value = "new $T()", args = FloatSeq.class)
    FloatSeq rotorRot();
    void rotorRot(FloatSeq fSeq);

    @Override
    default void add(){
        FloatSeq r = new FloatSeq();
        r.setSize(rotorCount());
        rotorRot(r);
    }

    @Override
    default void update(){
        UnityUnitType type = (UnityUnitType)type();
        if(dead() || health() < 0f){
            rotation(rotation() + type.fallRotateSpeed * Mathf.signs[id() % 2]);
            rotorSpeedScl(Mathf.lerpDelta(rotorSpeedScl(), 0f, type.rotorDeathSlowdown));
        }else{ //In case `heal()` is run while it's dying.
            rotorSpeedScl(Mathf.lerpDelta(rotorSpeedScl(), 1f, type.rotorDeathSlowdown));
        }
        for(Rotor rotor : type.rotors){
            int index = type.rotors.indexOf(rotor);
            rotorRot().set(index, (rotorRot().get(index) + rotor.speed * rotorSpeedScl() * Time.delta) % 360);
        }
    }

    default int rotorCount(){
        return ((UnityUnitType)type()).rotors.size;
    }
}

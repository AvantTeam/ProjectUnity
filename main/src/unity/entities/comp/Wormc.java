package unity.entities.comp;

import arc.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.type.*;

public interface Wormc extends Unitc{
    @Initialize(eval = "new $T()", args = IntSeq.class)
    IntSeq childs();

    void childs(IntSeq childs);

    @Initialize(eval = "new $T()", args = FloatSeq.class)
    FloatSeq healths();

    void healths(FloatSeq healths);

    @Initialize(eval = "-1")
    int headId();

    void headId(int headId);

    @Initialize(eval = "-1")
    int parentId();

    void parentId(int parentId);

    @Initialize(eval = "-1")
    int childId();

    void childId(int childId);

    @Initialize(eval = "false")
    boolean initialized();

    void initialized(boolean initialized);

    @Initialize(eval = "false")
    boolean savedAsHead();

    void savedAsHead(boolean savedAsHead);

    @ReadOnly
    @Initialize(eval = "new $T(1)", args = Interval.class)
    Interval headTimer();

    @Override
    default void add(){
        Core.app.post(this::setupSegments);
    }

    default void setupSegments(){
        if(savedAsHead()){
            headId(id());
            childs(new IntSeq(){{
                setSize(segmentLength());
            }});

            int seg = healths().isEmpty() ? segmentLength() : healths().size;
            for(int i = 0; i < seg; i++){
                Wormc child = (Wormc)type().create(team());
                child.rotation(rotation());
                child.initialized(true);
                child.add();

                childs().set(i, child.id());

                if(isBoss()){
                    child.apply(StatusEffects.boss, 999999f);
                }

                if(healths().isEmpty()){
                    child.heal();
                }else{
                    child.health(healths().get(i));
                }

                if(i == 0){
                    setChild(childs().get(i));
                }else{
                    ((Wormc)Groups.unit.getByID(childs().get(i - 1))).setChild(childs().get(i));
                }
            }
        }else if(!initialized()){
            remove();
        }
    }

    @Override
    default void update(){
        float dmg = ((UnityUnitType)type()).headDamage;
        if(dmg > 0f && headTimer().get(((UnityUnitType)type()).headTimer)){
            float size = hitSize() * 1.4f;
            float mul = isHead() ? 1f : 0.1f;

            Damage.damage(team(), x(), y(), size, dmg * mul, true, true);
            Units.nearbyEnemies(team(), x() - size, y() - size, size * 2f, size * 2f, unit -> {
                if(unit.within(this, size)){
                    Tmp.v1.trns(angleTo(unit), dst(unit) / 2f).add(this);
                    UnityFx.flareEffect.at(Tmp.v1, rotation());
                }
            });
        }
    }

    @Override
    default void damage(float amount){
        if(((UnityUnitType)type()).splittable){
            damageUnfiltered(amount);
        }else{
            if(isHead()){
                IntSeq childs = childs();
                for(int i = childs.size; i >= 0; i--){
                    Wormc worm = (Wormc)Groups.unit.getByID(childs.get(i));
                    if(worm != null){
                        worm.damageUnfiltered(amount / (childs.size + 1));
                    }
                }
                damageUnfiltered(amount / (childs.size + 1));
            }else{
                Wormc head = head();
                if(head != null) head.damage(amount);
            }
        }
    }

    default void damageUnfiltered(float amount){
        health(health() - amount);
        hitTime(1f);

        if(health() <= 0f && !dead()){
            kill();
        }
    }

    default int segmentLength(){
        return ((UnityUnitType)type()).segmentLength;
    }

    default Wormc parent(){
        if(parentId() < 0) return null;
        return (Wormc)Groups.unit.getByID(parentId());
    }

    default Wormc child(){
        if(childId() < 0) return null;
        return (Wormc)Groups.unit.getByID(childId());
    }

    default boolean isHead(){
        return parent() == null;
    }

    default boolean isTail(){
        return child() == null;
    }

    default Wormc head(){
        if(isHead()) return this;

        Wormc worm = (Wormc)Groups.unit.getByID(headId());
        if(worm == null || worm.dead()){
            findHead();
            worm = (Wormc)Groups.unit.getByID(headId());
        }

        return worm;
    }

    default void findHead(){
        Wormc worm = this;
        while(!worm.isHead()){
            worm = worm.parent();
        }

        headId(worm.id());
    }

    default void setParent(Wormc unit){
        if(unit == null || unit.dead() || (unit.child() != null && unit.child() != this)){
            parentId(unit.id());
            return;
        }

        parentId(unit.id());
    }

    default void setChild(int id){
        Wormc worm = (Wormc)Groups.unit.getByID(id);
        if(worm == null || worm.dead()){
            childId(-1);
            return;
        }

        childId(worm.id());
        child().setParent(this);

        findHead();
        child().findHead();
    }

    @Override
    default void write(Writes write){
        write.bool(isHead());

        write.i(childs().size);
        for(int id : childs().items){
            Wormc worm = (Wormc)Groups.unit.getByID(id);
            write.f(worm.health());
        }
    }

    @Override
    default void read(Reads read){
        savedAsHead(read.bool());

        healths(new FloatSeq(){{
            setSize(read.i());

            for(int i = 0; i < size; i++){
                set(i, read.f());
            }
        }});
    }
}

package unity.entities.comp;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.content.*;
import unity.type.*;

/**
<code>
const p = Vars.player;
const worm = Vars.content.getByName(ContentType.unit, "unity-project-googol");
worm.spawn(p.team(), p.x, p.y);
</code>
 */
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

    @Initialize(eval = "true")
    boolean savedAsHead();

    void savedAsHead(boolean savedAsHead);

    float layer();

    void layer(float layer);

    @ReadOnly
    @Initialize(eval = "new $T(1)", args = Interval.class)
    Interval headTimer();

    @Override
    default void add(){
        setupSegments();
    }

    default void setupSegments(){
        if(savedAsHead()){
            headId(id());

            IntSeq childs = new IntSeq();
            childs.setSize(healths().isEmpty() ? segmentLength() : healths().size);
            childs(childs);

            int seg = childs().size;
            for(int i = 0; i < seg; i++){
                Wormc child = (Wormc)type().create(team());
                child.layer(0f - (seg / (i + 1f) * 0.1f));
                child.rotation(rotation());
                child.set(x(), y());

                child.initialized(true);
                child.savedAsHead(false);
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
        float dmg = headDamage();
        if(dmg > 0f && headTimer().get(((UnityUnitType)type()).headTimer)){
            float size = hitSize() * 1.4f;
            float mul = isHead()
            ?   1f : !isTail()
                ?   0.5f : 0.25f; //full damage for head, half for body, a quarter for tail

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
    default void killed(){
        if(isTail()) return;

        Wormc child = child();
        IntSeq childs = new IntSeq();

        boolean past = false;
        for(int id : head().childs().items){
            Wormc worm = (Wormc)Groups.unit.getByID(id);
            if(past && worm != null && !worm.dead()){
                childs.add(worm.id());
            }else if(worm == this){
                past = true;
            }
        }

        child.childs(childs);
        for(int id : child.childs().items){
            Wormc worm = (Wormc)Groups.unit.getByID(id);
            if(worm == null || worm.dead()) continue;

            worm.findHead();
        }
    }

    default void updateMovement(){
        if(!isHead()){
            if(!parent().vel().isZero(0.1f)){
                Tmp.v1.trns(parent().vel().angle(), -segmentOffset()).add(parent());
                Tmp.v2.set(Tmp.v1).sub(this)
                    .setLength(Math.min(type().speed, Mathf.lerpDelta(vel().len(), Tmp.v2.len(), type().accel)));

                if(dst(Tmp.v1) > segmentOffset() * 4f){
                    vel().add(Tmp.v2).limit(type().speed).scl(Time.delta);
                }
            }

            rotation(Mathf.slerpDelta(rotation(), angleTo(parent()), type().rotateSpeed / 60f));
        }
    }

    default int segmentLength(){
        return ((UnityUnitType)type()).segmentLength;
    }

    default float headDamage(){
        return ((UnityUnitType)type()).headDamage;
    }

    default float segmentOffset(){
        return ((UnityUnitType)type()).segmentOffset;
    }

    default boolean splittable(){
        return ((UnityUnitType)type()).splittable;
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
        Unit worm = Groups.unit.getByID(id);
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
    @Replace
    default void damage(float amount){
        if(splittable()){
            damageUnfiltered(amount);
        }else{
            if(isHead()){
                IntSeq childs = childs();
                for(int i = childs.size; i >= 0; i--){
                    Wormc worm = (Wormc)Groups.unit.getByID(childs.get(i));
                    if(worm == null || worm.dead()) continue;

                    worm.damageUnfiltered(amount / (childs.size + 1));

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

    @Override
    @Replace
    default void aim(float x, float y){
        if(isHead()){
            aimUnfiltered(x, y);
            for(int id : childs().items){
                Wormc worm = (Wormc)Groups.unit.getByID(id);
                if(worm == null || worm.dead()) continue;

                worm.aimUnfiltered(x, y);
            }
        }else{
            head().aim(x, y);
        }
    }

    default void aimUnfiltered(float x, float y){
        Tmp.v1.set(x, y).sub(this);
        if(Tmp.v1.len() < type().aimDst){
            Tmp.v1.setLength(type().aimDst);
        }

        Tmp.v1.add(this);
        for(WeaponMount mount : mounts()){
            mount.aimX = Tmp.v1.x;
            mount.aimY = Tmp.v1.y;
        }

        aimX(Tmp.v1.x);
        aimY(Tmp.v1.y);
    }

    @Override
    @Replace
    default float mass(){
        if(isHead()){
            float mass = hitSize() * hitSize() * Mathf.pi;
            return mass * (childs().size + 1);
        }else{
            return head().mass();
        }
    }

    @Override
    @Replace
    default void heal(){
        if(!splittable()){
            if(isHead()){
                health(maxHealth());
                for(int id : childs().items){
                    Unit worm = Groups.unit.getByID(id);
                    if(worm == null || worm.dead()) continue;

                    worm.health(maxHealth());
                }
            }else{
                head().heal();
            }
        }else{
            health(maxHealth());
        }
    }

    @Override
    @Replace
    default void heal(float heal){
        if(!splittable()){
            if(isHead()){
                float value = heal / (childs().size + 1f);
    
                health(health() + value);
                for(int id : childs().items){
                    Unit worm = Groups.unit.getByID(id);
                    if(worm == null || worm.dead()) continue;

                    worm.health(worm.health() + value);
                }
            }else{
                head().heal();
            }
        }else{
            health(health() + heal);
        }
    }

    @Override
    @Replace
    default void health(float health){
        if(!splittable()){
            if(isHead()){
                this.<Unit>self().health = health;
                for(int id : childs().items){
                    Unit worm = Groups.unit.getByID(id);
                    if(worm == null || worm.dead()) continue;

                    worm.health = health;
                }
            }else{
                head().health(health);
            }
        }else{
            this.<Unit>self().health = health;
        }
    }

    @Override
    default boolean collides(Hitboxc hitbox){
        return hitbox instanceof Wormc worm
        ?   !(worm.head() == head())
        :   true;
    }

    @Override
    @Replace
    default void controller(UnitController next){
        if(isHead()){
            if(next.unit() != this){
                next.unit(self());
            }

            this.<UnitEntity>self().controller = next;
        }else if(!isPlayer()){
            head().controller(next);
        }
    }

    @Override
    @Replace
    default UnitController controller(){
        if(isHead()){
            return this.<UnitEntity>self().controller;
        }else{
            return head().controller();
        }
    }

    @Override
    @Replace
    default Player getPlayer(){
        if(isHead()){
            return controller() instanceof Player player
            ?   player
            :   null;
        }else{
            return head().getPlayer();
        }
    }

    @Override
    @Replace
    default boolean isPlayer(){
        if(isHead()){
            return controller() instanceof Player;
        }else{
            return head().isPlayer();
        }
    }

    @Override
    default void write(Writes write){
        write.bool(isHead());

        if(isHead()){
            write.i(childs().size);
            for(int id : childs().items){
                Unit worm = Groups.unit.getByID(id);
                write.f(worm != null ? worm.health() : 0f);
            }
        }
    }

    @Override
    default void read(Reads read){
        savedAsHead(read.bool());

        if(savedAsHead()){
            int size = read.i();
            FloatSeq healths = new FloatSeq();

            healths.setSize(size);
            for(int i = 0; i < size; i++){
                healths.set(i, read.f());
            }

            healths(healths);
        }else{
            remove();
        }
    }
}

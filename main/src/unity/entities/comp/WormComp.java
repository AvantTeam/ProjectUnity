package unity.entities.comp;

import arc.func.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.type.*;
import unity.util.*;

//TODO custom saving
@SuppressWarnings({"unused", "UnnecessaryReturnStatement"})
@EntityComponent
abstract class WormComp implements Unitc{
    private static Unit last;
    transient Unit head, parent, child;
    transient float layer = 0f, scanTime = 0f;
    transient boolean removing = false;

    protected float splitHealthDiv = 1f;
    protected float regenTime = 0f;
    protected float waitTime = 0f;

    @Import UnitType type;
    @Import float healthMultiplier, health;
    @Import boolean dead;
    @Import WeaponMount[] mounts;
    @Import Team team;

    @Override
    public boolean serialize(){
        return isHead();
    }

    public boolean isHead(){
        return parent == null || head == null || head == self();
    }

    public boolean isTail(){
        return child == null;
    }

    private void connect(Wormc other){
        if(isHead() && other.isTail()){
            int z = other.countFoward() + 1;
            distributeActionBack(u -> {
                u.layer(u.layer() + z);
                u.head(other.head());
            });
            other.child(self());
            parent = (Unit)other;
            head = other.head();
            setupWeapons(type);
            ((UnityUnitType)type).chainSound.at(self());
            if(controller() instanceof Player){
                UnitController con = controller();
                other.head().controller(con);
                con.unit(other.head());
                controller(type.createController());
            }
        }
    }

    public int countFoward(){
        Wormc current = self();
        int num = 0;
        while(current != null && current.parent() != null){
            if(current.parent() instanceof Wormc){
                num++;
                current = (Wormc)current.parent();
            }else{
                current = null;
            }
        }
        return num;
    }

    public int countBackward(){
        Wormc current = self();
        int num = 0;
        while(current != null && current.child() != null){
            if(current.child() instanceof Wormc){
                num++;
                current = (Wormc)current.child();
            }else{
                current = null;
            }
        }
        return num;
    }

    @MethodPriority(-1)
    @Override
    @BreakAll
    public void controller(UnitController next){
        if(next instanceof Player && head != null && !isHead()){
            head.controller(next);
            return;
        }
    }

    @MethodPriority(100)
    @Override
    public void read(Reads read){
        if(read.bool()){
            int seg = read.s();
            Wormc current = self();
            for(int i = 0; i < seg; i++){
                Unit u = type.constructor.get();
                Wormc w = (Wormc)u;
                u.read(read);
                current.child(u);
                w.parent((Unit)current);
                w.head(self());
                current = w;
            }
        }
    }

    @MethodPriority(100)
    @Override
    public void write(Writes write){
        write.bool(isHead());
        if(isHead()){
            write.s(countBackward());
            distributeActionBack(u -> {
                if(u != self()){
                    u.write(write);
                }
            });
        }
    }

    @Replace
    @Override
    public boolean isAI(){
        if(head != null && !isHead()) return head.isAI();
        return controller() instanceof AIController;
    }

    @Replace
    @MethodPriority(-2)
    @Override
    @BreakAll
    public void damage(float amount){
        if(!isHead() && head != null && !((UnityUnitType)type).splittable){
            head.damage(amount);
            return;
        }
    }

    @MethodPriority(-1)
    @Override
    @BreakAll
    public void heal(float amount){
        if(!isHead() && head != null && !((UnityUnitType)type).splittable){
            head.heal(amount);
            return;
        }
    }

    protected <T extends Unit & Wormc> void distributeActionBack(Cons<T> cons){
        T current = as();
        cons.get(current);
        while(current.child() != null){
            cons.get(current.child().as());
            current = current.child().as();
        }
    }

    protected <T extends Unit & Wormc> void distributeActionForward(Cons<T> cons){
        T current = as();
        cons.get(current);
        while(current.parent() != null){
            cons.get(current.parent().as());
            current = current.parent().as();
        }
    }

    @Replace
    @Override
    public int cap(){
        int max = Math.max(((UnityUnitType)type).maxSegments, ((UnityUnitType)type).segmentLength);
        return Units.getCap(team) * max;
    }

    @Replace
    @Override
    public float speed(){
        if(!isHead()) return 0f;
        float strafePenalty = isGrounded() || !isPlayer() ? 1f : Mathf.lerp(1f, type.strafePenalty, Angles.angleDist(vel().angle(), rotation()) / 180f);
        return (isCommanding() ? minFormationSpeed() * 0.98f : type.speed) * strafePenalty;
    }

    @Override
    public void update(){
        UnityUnitType uType = (UnityUnitType)type;
        if(uType.splittable && isTail() && uType.regenTime > 0f){
            int forward = countFoward();
            if(forward < Math.max(uType.maxSegments, uType.segmentLength)){
                regenTime += Time.delta;
                if(regenTime >= uType.regenTime){
                    regenTime = 0f;
                    Unit unit;
                    if((unit = addTail()) != null){
                        health /= 2f;
                        unit.health = health;
                        ((UnityUnitType)type).chainSound.at(self());
                    }
                }
            }
        }else{
            regenTime = 0f;
        }
        if(isTail() && waitTime > 0){
            waitTime -= Time.delta;
        }
        if(!uType.splittable && !isHead()){
            health = head.health;
        }
        if(uType.splittable && (parent != null || child != null) && dead){
            destroy();
        }
    }

    @Wrap(value = "update()", block = Boundedc.class)
    public boolean updateBounded(){
        return isHead();
    }

    @Insert(value = "update()", block = Statusc.class)
    private void updateHealthDiv(){
        healthMultiplier /= splitHealthDiv;
    }

    Unit addTail(){
        if(!isTail()) return null;
        Unit tail = type.create(team);
        UnityUnitType uType = (UnityUnitType)type;
        if(tail instanceof Wormc){
            int z = countFoward() + 1;
            Tmp.v1.trns(rotation() + 180f, uType.segmentOffset).add(self());
            tail.set(Tmp.v1);
            ((Wormc)tail).layer(z);
            ((Wormc)tail).head(head);
            ((Wormc)tail).parent(self());
            child = tail;
            tail.add();
        }
        return tail;
    }

    @Insert("update()")
    private void updatePost(){
        if(isHead()){
            UnityUnitType uType = (UnityUnitType)type;
            last = self();
            distributeActionBack(u -> {
                if(u == self()) return;
                float offset = self() == last ? uType.headOffset : 0f;
                Tmp.v1.trns(last.rotation + 180f, (uType.segmentOffset / 2f) + offset).add(last);
                u.rotation = u.angleTo(Tmp.v1);
                float limit = Utils.angleDistSigned(last.rotation(), u.rotation(), uType.angleLimit);
                float side = 1f - uType.anglePhysicsSide;
                float smooth = 1f - uType.anglePhysicsSmooth;
                last.rotation(last.rotation() - (limit * uType.anglePhysicsSide * smooth));
                u.rotation(u.rotation() + (limit * side * smooth));
                Tmp.v2.trns(u.rotation(), uType.segmentOffset / 2f).add(u);
                Tmp.v1.trns(last.rotation() + 180f, (uType.segmentOffset / 2f) + offset).add(last);

                if(!u.within(Tmp.v1, uType.segmentOffset + 0.001f)){
                    float dst = Mathf.clamp(Tmp.v2.dst(Tmp.v1) / 6f) / 2f;
                    last.rotation = Mathf.slerp(last.rotation, u.angleTo(last), dst);
                    Tmp.v1.trns(last.rotation() + 180f, (uType.segmentOffset / 2f) + offset).add(last);
                }

                Tmp.v2.sub(Tmp.v1).scl(uType.jointStrength);

                u.set(u.x - Tmp.v2.x, u.y - Tmp.v2.y);

                Tmp.v1.set(u.vel).rotate(-u.rotation);
                Tmp.v1.x = 0f;
                Tmp.v1.rotate(u.rotation).scl(0.1f * Time.delta);
                u.vel.sub(Tmp.v1);

                /*
                float vl = vel().len();
                Tmp.v1.trns(u.angleTo(last), vl).add(u.vel).setLength(vl);
                u.vel.lerpDelta(Tmp.v1, type.accel * type.speed).limit(vl);
                if(u.deltaLen() > last.vel().len()){
                    u.vel.scl(1f - Mathf.clamp(0.3f * Time.delta));
                }
                */

                u.vel.setLength2(vel().len2());

                float nextHealth = (last.health() + u.health()) / 2f;
                if(!Mathf.equal(nextHealth, last.health(), 0.0001f)) last.health(Mathf.lerpDelta(last.health(), nextHealth, uType.healthDistribution));
                if(!Mathf.equal(nextHealth, u.health(), 0.0001f)) u.health(Mathf.lerpDelta(u.health(), nextHealth, uType.healthDistribution));

                Wormc wrm = ((Wormc)last);
                float nextHealthDv = (wrm.splitHealthDiv() + u.splitHealthDiv()) / 2f;
                if(!Mathf.equal(nextHealth, wrm.splitHealthDiv(), 0.0001f)) wrm.splitHealthDiv(Mathf.lerpDelta(wrm.splitHealthDiv(), nextHealthDv, uType.healthDistribution));
                if(!Mathf.equal(nextHealth, u.splitHealthDiv(), 0.0001f)) u.splitHealthDiv(Mathf.lerpDelta(u.splitHealthDiv(), nextHealthDv, uType.healthDistribution));
                last = u;
            });
            if(isHead()) scanTime += Time.delta;
            if(scanTime >= 5f && uType.chainable && isHead()){
                Tmp.v1.trns(rotation(), uType.segmentOffset / 2f).add(self());
                Tmp.r1.setCentered(Tmp.v1.x, Tmp.v1.y, hitSize());
                Units.nearby(Tmp.r1, u -> {
                    if(u.team == team && u.type == type && u instanceof Wormc w && w.head() != self() && w.isTail() && w.waitTime() <= 0f && within(u, uType.segmentOffset) && Utils.angleDist(rotation(), angleTo(u)) < uType.angleLimit){
                        connect(w);
                    }
                });
                scanTime = 0f;
            }
        }
    }

    @Replace
    @Override
    public void wobble(){
        
    }

    @MethodPriority(-1)
    @Override
    @BreakAll
    public void setupWeapons(UnitType def){
        UnityUnitType uType = (UnityUnitType)def;
        if(!isHead()){
            Seq<Weapon> seq = uType.segWeapSeq;
            mounts = new WeaponMount[seq.size];
            for(int i = 0; i < mounts.length; i++){
                mounts[i] = seq.get(i).mountType.get(seq.get(i));
            }
            return;
        }
    }

    @Override
    @BreakAll
    public void remove(){
        UnityUnitType uType = (UnityUnitType)type;
        if(uType.splittable){
            if(child != null && parent != null) uType.splitSound.at(x(), y());
            if(child != null){
                Wormc wc = ((Wormc)child);
                wc.head(null);
                float z = countFoward() + 1f;
                distributeActionBack(u -> {
                    if(u != self()){
                        u.layer(u.layer() - z);
                        u.splitHealthDiv(u.splitHealthDiv() * 2f);
                        u.head(child);
                        if(u.isTail()) u.waitTime(5f * 60f);
                    }
                });
                wc.parent(null);
                child.setupWeapons(type);
            }
            if(parent != null){
                Wormc wp = ((Wormc)parent);
                distributeActionForward(u -> {
                    if(u != self()){
                        u.splitHealthDiv(u.splitHealthDiv() * 2f);
                    }
                });
                wp.child(null);
                wp.waitTime(5f * 60f);
            }
            parent = null;
            child = null;
        }
        if(!isHead() && !uType.splittable && !removing){
            head.remove();
            return;
        }
        if(isHead() && !uType.splittable){
            distributeActionBack(u -> {
                if(u != self()){
                    u.removing(true);
                    u.remove();
                    u.removing(false);
                }
            });
        }
    }

    @Override
    public void add(){
        UnityUnitType uType = (UnityUnitType)type;
        Unit current = self();
        if(isHead()){
            if(child == null){
                float rot = rotation() + uType.angleLimit;
                Tmp.v1.trns(rot + 180f, uType.segmentOffset + uType.headOffset).add(self());
                for(int i = 0; i < uType.segmentLength; i++){
                    Unit t = uType.create(team());
                    t.x = Tmp.v1.x;
                    t.y = Tmp.v1.y;
                    t.rotation = rot;
                    t.elevation = elevation();

                    rot += uType.angleLimit;
                    Tmp.v2.trns(rot + 180f, uType.segmentOffset);
                    Tmp.v1.add(Tmp.v2);

                    Wormc wt = (Wormc)t;
                    wt.layer(i + 1f);
                    wt.head(self());
                    wt.parent(current);
                    ((Wormc)current).child(t);
                    t.setupWeapons(uType);
                    t.heal();
                    t.add();
                    current = t;
                }
            }else{
                distributeActionBack(u -> {
                    if(u != self()){
                        u.add();
                    }
                });
            }
        }
    }
}

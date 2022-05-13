package unity.entities.comp;

import arc.func.*;
import arc.graphics.g2d.*;
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

@SuppressWarnings({"unused", "UnnecessaryReturnStatement"})
@EntityComponent
abstract class WormComp implements Unitc{
    private static Unit last;
    transient Unit head, parent, child;
    transient float layer = 0f, scanTime = 0f;
    transient byte weaponIdx = 0;
    transient boolean removing = false, saveAdd = false;

    protected float splitHealthDiv = 1f;
    protected float regenTime = 0f;
    protected float waitTime = 0f;

    @SyncLocal public int childId = -1, headId = -1;

    @Import UnitType type;
    @Import float healthMultiplier, health, x, y, minFormationSpeed, elevation, rotation;
    @Import boolean dead;
    @Import WeaponMount[] mounts;
    @Import Team team;

    @Override
    public boolean serialize(){
        return isHead();
    }

    boolean isHead(){
        return parent == null || head == null || head == self();
    }

    boolean isTail(){
        return child == null;
    }

    @Override
    @Replace
    public TextureRegion icon(){
        UnityUnitType uType = (UnityUnitType)type;
        if(isTail()) return uType.tailOutline;
        if(!isHead()) return uType.segmentOutline;
        return type.fullIcon;
    }

    private void connect(Wormc other){
        if(isHead() && other.isTail()){
            float z = other.layer() + 1f;
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
                controller(type.createController(self()));
            }
        }
    }

    int countFoward(){
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

    int countBackward(){
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
            saveAdd = true;
            int seg = read.s();
            Wormc current = self();
            for(int i = 0; i < seg; i++){
                Unit u = type.constructor.get();
                Wormc w = (Wormc)u;
                current.child(u);
                w.parent((Unit)current);
                w.head(self());
                w.layer(i);
                w.weaponIdx(read.b());
                u.read(read);
                current = w;
            }
        }
    }

    @MethodPriority(100)
    @Override
    public void write(Writes write){
        write.bool(isHead());
        if(isHead()){
            Wormc ch = (Wormc)child;
            int amount = 0;
            while(ch != null){
                amount++;
                ch = (Wormc)ch.child();
            }
            write.s(amount);

            ch = (Wormc)child;
            while(ch != null){
                write.b(weaponIdx);
                ch.write(write);
                ch = (Wormc)ch.child();
            }
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

    <T extends Unit & Wormc> void distributeActionBack(Cons<T> cons){
        T current = as();
        cons.get(current);
        while(current.child() != null){
            cons.get(current.child().as());
            current = current.child().as();
        }
    }

    <T extends Unit & Wormc> void distributeActionForward(Cons<T> cons){
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
        float strafePenalty = isGrounded() || !isPlayer() ? 1f : Mathf.lerp(1f, type.strafePenalty, Angles.angleDist(vel().angle(), rotation) / 180f);
        float boost = Mathf.lerp(1f, type.canBoost ? type.boostMultiplier : 1f, elevation);
        return /*(isCommanding() ? minFormationSpeed * 0.98f : type.speed) * */strafePenalty * boost * floorSpeedMultiplier();
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
        if(!uType.splittable){
            if(!isHead()) health = head.health;
            if((isHead() && isAdded()) || (head != null && head.isAdded())){
                Wormc t = (Wormc)child;
                while(t != null && !t.isAdded()){
                    t.add();
                    t = (Wormc)t.child();
                }
            }
        }
        if(uType.splittable && (parent != null || child != null) && dead){
            destroy();
        }
    }

    @Wrap(value = "update()", block = Boundedc.class)
    boolean updateBounded(){
        return isHead();
    }

    @Insert(value = "update()", block = Statusc.class)
    private void updateHealthDiv(){
        healthMultiplier /= splitHealthDiv;
    }

    Unit addTail(){
        if(!isTail()) return null;
        Unit tail = type.constructor.get();
        tail.team = team;
        tail.setType(type);
        tail.ammo = type.ammoCapacity;
        tail.elevation = type.flying ? 1f : 0;
        tail.heal();

        UnityUnitType uType = (UnityUnitType)type;
        if(tail instanceof Wormc){
            float z = layer + 1f;
            Tmp.v1.trns(rotation() + 180f, uType.segmentOffset).add(self());
            tail.set(Tmp.v1);
            ((Wormc)tail).layer(z);
            ((Wormc)tail).head(head);
            ((Wormc)tail).parent(self());
            child = tail;
            tail.setupWeapons(uType);
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

                float rdx = u.deltaX - last.deltaX;
                float rdy = u.deltaY - last.deltaY;

                float angTo = !uType.preventDrifting || (last.deltaLen() > 0.001f && (rdx * rdx) + (rdy * rdy) > 0.00001f) ? u.angleTo(Tmp.v1) : u.rotation;

                u.rotation = angTo - (Utils.angleDistSigned(angTo, last.rotation, uType.angleLimit) * (1f - uType.anglePhysicsSmooth));
                u.trns(Tmp.v3.trns(u.rotation, last.deltaLen()));
                Tmp.v2.trns(u.rotation, uType.segmentOffset / 2f).add(u);

                Tmp.v2.sub(Tmp.v1).scl(Mathf.clamp(uType.jointStrength * Time.delta));

                Unit n = u;
                int cast = uType.segmentCast;
                while(cast > 0 && n != null){
                    float scl = cast / (float)uType.segmentCast;
                    n.set(n.x - (Tmp.v2.x * scl), n.y - (Tmp.v2.y * scl));
                    n.updateLastPosition();
                    n = ((Wormc)n).child();
                    cast--;
                }

                float nextHealth = (last.health() + u.health()) / 2f;
                if(!Mathf.equal(nextHealth, last.health(), 0.0001f)) last.health(Mathf.lerpDelta(last.health(), nextHealth, uType.healthDistribution));
                if(!Mathf.equal(nextHealth, u.health(), 0.0001f)) u.health(Mathf.lerpDelta(u.health(), nextHealth, uType.healthDistribution));

                Wormc wrm = ((Wormc)last);
                float nextHealthDv = (wrm.splitHealthDiv() + u.splitHealthDiv()) / 2f;
                if(!Mathf.equal(nextHealth, wrm.splitHealthDiv(), 0.0001f)) wrm.splitHealthDiv(Mathf.lerpDelta(wrm.splitHealthDiv(), nextHealthDv, uType.healthDistribution));
                if(!Mathf.equal(nextHealth, u.splitHealthDiv(), 0.0001f)) u.splitHealthDiv(Mathf.lerpDelta(u.splitHealthDiv(), nextHealthDv, uType.healthDistribution));
                last = u;
            });
            scanTime += Time.delta;
            if(scanTime >= 5f && uType.chainable){
                Tmp.v1.trns(rotation(), uType.segmentOffset / 2f).add(self());
                Tmp.r1.setCentered(Tmp.v1.x, Tmp.v1.y, hitSize());
                Units.nearby(Tmp.r1, u -> {
                    if(u.team == team && u.type == type && u instanceof Wormc w && w.head() != self() && w.isTail() && w.countFoward() + countBackward() < uType.maxSegments && w.waitTime() <= 0f && within(u, uType.segmentOffset) && Utils.angleDist(rotation(), angleTo(u)) < uType.angleLimit){
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
            //Seq<Weapon> seq = uType.segWeapSeq;
            Seq<Weapon> seq = uType.segmentWeapons[weaponIdx];
            mounts = new WeaponMount[seq.size];
            for(int i = 0; i < mounts.length; i++){
                mounts[i] = seq.get(i).mountType.get(seq.get(i));
            }
            return;
        }
    }

    @Override
    public void afterSync(){
        if(headId != -1 && head == null){
            Unit h = Groups.unit.getByID(headId);
            if(h instanceof Wormc wc){
                head = h;
                headId = -1;
            }
        }
        if(childId != -1 && child == null){
            Unit c = Groups.unit.getByID(childId);
            if(c instanceof Wormc wc){
                child = c;
                wc.parent(self());
                childId = -1;
            }
        }
    }

    @Override
    @BreakAll
    public void remove(){
        UnityUnitType uType = (UnityUnitType)type;
        if(uType.splittable){
            if(child != null && parent != null) uType.splitSound.at(x(), y());
            if(child != null){
                var wc = (Unit & Wormc)child;
                float z = 0f;
                while(wc != null){
                    wc.layer(z++);
                    wc.splitHealthDiv(wc.splitHealthDiv() * 2f);
                    wc.head(child);
                    if(wc.isTail()) wc.waitTime(5f * 60f);
                    wc = (Unit & Wormc)wc.child();
                }
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
            if(saveAdd){
                var seg = (Unit & Wormc)child;
                while(seg != null){
                    seg.add();
                    seg = (Unit & Wormc)seg.child();
                }
                saveAdd = false;
                return;
            }
            float[] rot = {rotation() + uType.angleLimit};
            Tmp.v1.trns(rot[0] + 180f, uType.segmentOffset + uType.headOffset).add(self());
            distributeActionBack(u -> {
                if(u != self()){
                    u.x = Tmp.v1.x;
                    u.y = Tmp.v1.y;
                    u.rotation = rot[0];

                    rot[0] += uType.angleLimit;
                    Tmp.v2.trns(rot[0] + 180f, uType.segmentOffset);
                    Tmp.v1.add(Tmp.v2);

                    u.add();
                }
            });
        }
    }
}

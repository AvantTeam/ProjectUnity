package unity.entities.comp;

import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.type.*;
import unity.type.CubeUnitType.*;

@SuppressWarnings({"unused", "UnnecessaryReturnStatement"})
@EntityComponent
abstract class CubeComp implements Unitc{
    transient CubeEntityData data;
    transient boolean healing;
    int tier = 0;
    int gx, gy;
    float constructTime = 0f;
    boolean edge = true;

    @Import UnitType type;
    @Import float elevation, minFormationSpeed, rotation, x, y;
    @Import WeaponMount[] mounts;

    boolean isMain(){
        return data == null || data.main == self();
    }

    @Override
    public void update(){
        if(isMain() && data != null){
            CubeUnitType cType = (CubeUnitType)type;
            for(Cubec c : data.all){
                if(c != self()){
                    float offset = (c.tier() * c.tier()) / 2f;
                    Tmp.v1.trns(rotation - 90f, (c.gx() - gx) + offset, (c.gy() - gy) + offset).scl(cType.gridSpacing).add(x, y);
                    c.set(Tmp.v1.x, Tmp.v1.y);
                    c.rotation(rotation);
                    if(!c.isAdded()){
                        updateConstructing();
                    }
                }
            }
        }
    }

    void updateConstructing(){
        CubeUnitType cType = (CubeUnitType)type;
        constructTime += Time.delta;
        if(constructTime >= cType.regenTime){
            add();
        }
    }

    @Override
    @MethodPriority(100)
    public void setType(UnitType type){
        CubeUnitType cType = (CubeUnitType)type;
        Seq<Weapon> wep = cType.weaponsAll.get(Mathf.clamp(tier, 0, cType.weaponsAll.size));
        if(mounts == null || mounts.length != wep.size){
            mounts = new WeaponMount[wep.size];
            for(int i = 0; i < mounts.length; i++){
                mounts[i] = wep.get(i).mountType.get(wep.get(i));
            }
        }
    }

    @Override
    @MethodPriority(-1)
    @BreakAll
    public void heal(float amount){
        if(data != null && !healing){
            int div = data.all.count(Healthc::damaged);
            for(Cubec c : data.all){
                c.healing(true);
                if(c.damaged()) heal(amount / div);
                c.healing(false);
            }
            return;
        }
    }

    @Override
    @MethodPriority(-1)
    @BreakAll
    public void lookAt(float angle){
        if(!isMain()) return;
    }

    @Override
    @Replace(100)
    public float speed(){
        if(!isMain()) return 0f;
        float strafePenalty = isGrounded() || !isPlayer() ? 1f : Mathf.lerp(1f, type.strafePenalty, Angles.angleDist(vel().angle(), rotation) / 180f);
        float boost = Mathf.lerp(1f, type.canBoost ? type.boostMultiplier : 1f, elevation);
        return /*(isCommanding() ? minFormationSpeed * 0.98f : type.speed) * */strafePenalty * boost * floorSpeedMultiplier();
    }

    @Override
    public void add(){
        if(data == null){
            data = new CubeEntityData(self());
        }
    }

    @Override
    public void remove(){
        if(data != null){
            data.remove(self());
        }
    }
}

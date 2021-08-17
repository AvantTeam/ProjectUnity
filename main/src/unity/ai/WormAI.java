package unity.ai;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.types.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.meta.*;
import unity.gen.*;
import unity.type.*;

public class WormAI extends FlyingAI{
    public Vec2 pos = new Vec2();
    public float score = 0f;
    public float time = 0f;
    protected float rotateTime = 0f;

    @Override
    public void updateMovement(){
        if(target == null && time > 0){
            moveTo(pos, 0f);
        }
        if(target != null && unit.hasWeapons() && command() == UnitCommand.attack){
            if(!unit.type.circleTarget){
                moveTo(target, unit.range() * 0.8f);
                unit.lookAt(target);
            }else{
                attack(120f);
            }
        }

        if(target == null && time <= 0f && command() == UnitCommand.attack && Vars.state.rules.waves && unit.team == Vars.state.rules.defaultTeam){
            moveTo(getClosestSpawner(), Vars.state.rules.dropZoneRadius + 120f);
        }

        if(command() == UnitCommand.rally){
            moveTo(targetFlag(unit.x, unit.y, BlockFlag.rally, false), 60f);
        }
        rotateTime = Math.max(0f, rotateTime - Time.delta);
        if(time <= 0) score = 0f;
        time = Math.max(0f, time - Time.delta);
    }

    @Override
    protected void updateWeapons(){
        if(unit instanceof Wormc w && unit.type instanceof UnityUnitType uType
        && w.head() != null && w.head().isShooting && w.head().controller() instanceof Player && unit.within(w.head(), uType.barrageRange + (unit.hitSize / 2f))){
            Unit head = w.head();
            for(WeaponMount mount : unit.mounts){
                Weapon weapon = mount.weapon;
                if(!weapon.controllable) continue;

                mount.aimX = head.aimX;
                mount.aimY = head.aimY;
                mount.shoot = mount.rotate = true;
            }
        }else{
            super.updateWeapons();
        }
    }

    @Override
    protected void attack(float circleLength){
        vec.trns(unit.rotation, unit.speed());
        float diff = Angles.angleDist(unit.rotation, unit.angleTo(target));
        if((diff > 100f && !unit.within(target, circleLength)) || rotateTime > 0f){
            vec.setAngle(Mathf.slerpDelta(vec.angle(), unit.angleTo(target), 0.2f));
            if(rotateTime <= 0f) rotateTime = 40f;
        }
        unit.moveAt(vec);
    }

    public void setTarget(float x, float y, float score){
        if(score < this.score) return;
        pos.set(x, y);
        this.score = score;
        time = 3f * 60;
    }
}

package unity.ai;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.types.*;
import mindustry.entities.units.*;
import mindustry.world.meta.*;

public class WormAI extends FlyingAI{
    public Vec2 pos = new Vec2();
    public float score = 0f;
    public float time = 0f;

    @Override
    public void updateMovement(){
        Position trgt = time <= 0f ? target : pos;
        if(trgt != null && unit.hasWeapons() && command() == UnitCommand.attack){
            if(!unit.type.circleTarget){
                moveTo(trgt, unit.range() * 0.8f);
                unit.lookAt(trgt);
            }else{
                attack(120f);
            }
        }
        if(time <= 0) score = 0f;
        time = Math.max(0f, time - Time.delta);

        if(target == null && command() == UnitCommand.attack && Vars.state.rules.waves && unit.team == Vars.state.rules.defaultTeam){
            moveTo(getClosestSpawner(), Vars.state.rules.dropZoneRadius + 120f);
        }

        if(command() == UnitCommand.rally){
            moveTo(targetFlag(unit.x, unit.y, BlockFlag.rally, false), 60f);
        }
    }

    @Override
    protected void attack(float circleLength){
        Position target = time <= 0 ? this.target : pos;

        vec.set(target).sub(unit);

        float ang = unit.angleTo(target);
        float diff = Angles.angleDist(ang, unit.rotation());

        if(diff > 100f && vec.len() < circleLength){
            vec.setAngle(unit.vel().angle());
        }else{
            vec.setAngle(Mathf.slerpDelta(unit.vel().angle(), vec.angle(), 0.3f));
        }

        vec.setLength(unit.speed());

        unit.moveAt(vec);
    }

    public void setTarget(float x, float y, float score){
        if(this.score > score) return;
        pos.set(x, y);
        this.score = score;
        time = 3f * 60;
    }
}

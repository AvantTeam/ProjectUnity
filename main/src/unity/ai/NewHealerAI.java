package unity.ai;

import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.types.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.weapons.*;

public class NewHealerAI extends FlyingAI{
    final static int depth = 32;

    float switchTime = 0f;
    boolean findTile = false;

    @Override
    public void init(){
        super.init();
        findTile = unit.type.canHeal;
        for(WeaponMount mount : unit.mounts){
            findTile |= mount.weapon instanceof RepairBeamWeapon && ((RepairBeamWeapon)mount.weapon).targetBuildings;
        }
    }

    @Override
    public void updateUnit(){
        super.updateUnit();
        switchTime -= Time.delta;
    }

    @Override
    public void updateMovement(){
        if(target != null){
            float range = unit.type.range * 0.8f;
            if(target instanceof Sized) range += ((Sized)target).hitSize() / 4f;
            if(!unit.type.circleTarget){
                moveTo(target, range, 40f);
                unit.lookAt(target);
            }else{
                circle(range);
            }
        }
    }

    void circle(float range){
        vec.set(target).sub(unit);

        if(vec.len() < range){
            float side = Mathf.randomSeed(unit.id, 0, 1) == 0 ? -1 : 1;
            vec.rotate(((range - vec.len()) / range * 180f) * side);
        }

        vec.setLength(unit.speed());

        unit.moveAt(vec);
    }

    @Override
    public boolean invalid(Teamc target){
        boolean in = target == null || target.team() != unit.team || (target instanceof Healthc && (!((Healthc)target).isValid() || !((Healthc)target).damaged()));
        if(in){
            switchTime = 0f;
        }
        return in;
    }

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground){
        float sd = unit.speed() / 3f;

        Building build = null;
        float buildScore = -Float.MAX_VALUE;
        if(ground && findTile){
            Seq<Building> buildings = Vars.indexer.getDamaged(unit.team);
            int len = Math.min(buildings.size, depth);
            for(int i = 0; i < len; i++){
                float s = calculateScore(buildings.get(i), sd);
                if(s > buildScore){
                    buildScore = s;
                    build = buildings.get(i);
                }
            }
        }

        Seq<Unit> units = unit.team.data().units;
        Unit un = null;
        float score = -Float.MAX_VALUE;
        for(Unit u : units){
            if(!u.dead && u != unit && u.damaged() && u.checkTarget(air, ground)){
                float sc = calculateScore(u, sd);
                if(sc > score){
                    score = sc;
                    un = u;
                }
            }
        }

        if(un != null || build != null){
            switchTime = 160f;
        }

        return (score > buildScore || build == null) ? un : build;
    }

    @Override
    public boolean retarget(){
        return switchTime <= 0f && super.retarget();
    }

    float calculateScore(Healthc target, float s){
        float h = Mathf.sqrt(Math.max(target.maxHealth() - target.health(), 0f)) * 500f;

        return ((-unit.dst2(target) / (s * s)) / 2000f) + h;
    }
}

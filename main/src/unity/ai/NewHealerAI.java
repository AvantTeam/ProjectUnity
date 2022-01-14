package unity.ai;

import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.types.*;
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
        findTile = false;
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
            if(!unit.type.circleTarget){
                moveTo(target, unit.type.range * 0.8f);
                unit.lookAt(target);
            }else{
                attack(120f);
            }
        }
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
        Building build = null;
        float buildScore = -Float.MAX_VALUE;
        if(ground && findTile){
            Seq<Building> buildings = Vars.indexer.getDamaged(unit.team);
            int len = Math.min(buildings.size, depth);
            for(int i = 0; i < len; i++){
                float s = calculateScore(buildings.get(i));
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
            if(!u.dead && u.damaged() && u.checkTarget(air, ground)){
                float sc = calculateScore(u);
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

    float calculateScore(Healthc target){
        float h = Mathf.sqrt(Math.max(target.maxHealth() - target.health(), 0f)) * 500f;
        return (-unit.dst2(target) / 5000f) + h;
    }
}

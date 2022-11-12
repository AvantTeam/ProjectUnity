package unity.gensrc.entities;

import arc.struct.*;
import arc.struct.Queue;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.entities.prop.*;
import unity.entities.type.*;

import java.util.*;

@EntityComponent
abstract class RestrictedComp implements Unitc{
    float disconnectedTime = 0f;
    private transient RestrictedProps rprops;

    @Import float healthMultiplier, maxHealth;
    @Import Queue<BuildPlan> plans;

    @Override
    @Replace
    public void validatePlans(){
        if(plans.size > 0){
            Iterator<BuildPlan> it = plans.iterator();
            while(it.hasNext()){
                BuildPlan plan = it.next();
                Tile tile = Vars.world.tile(plan.x, plan.y);
                if(tile == null || (plan.breaking && tile.block() == Blocks.air)
                        || !RestrictedProps.planValid.get(plan, team())
                        || (!plan.breaking && ((tile.build != null && tile.build.rotation == plan.rotation) || !plan.block.rotate) && tile.block() == plan.block)){
                    it.remove();
                }
            }
        }
    }

    @Override
    public void update(){
        if(RestrictedProps.limit.get(self())){
            disconnectedTime = Math.max(0f, disconnectedTime - (Time.delta / rprops.disconnectionTime));
        }else{
            disconnectedTime = Math.min(1f, disconnectedTime + (Time.delta / rprops.disconnectionTime));
        }
        if(disconnectedTime >= 1f){
            damage((maxHealth * healthMultiplier) / 10);
        }
    }

    @Override
    public void setType(UnitType type){
        if(type instanceof PUUnitTypeCommon def) rprops = def.propReq(RestrictedProps.class);
    }
}

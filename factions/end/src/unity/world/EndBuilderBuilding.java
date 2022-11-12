package unity.world;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.struct.Queue;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.mod.*;

public interface EndBuilderBuilding extends Posc{
    float range();

    int tileRange();

    EndBuilderModule builderMod();

    void updateUnit(Unit u);
    Unit getTargetUnit();

    BlockUnitc getUnit();

    default boolean validPlan(int x, int y, int rotation, boolean deconstruct, Object config){
        IntMap<BuildPlan> plans = builderMod().planMap;
        BuildPlan other = plans.get(Point2.pack(x, y));
        if(other != null){
            other.breaking = deconstruct;
            other.rotation = rotation;
            other.config = config;
        }
        return other == null;
    }

    default BuildPlan getPlan(int x, int y){
        return builderMod().planMap.get(Point2.pack(x, y));
    }

    default void addPlan(BuildPlan plan){
        IntMap<BuildPlan> plans = builderMod().planMap;
        BuildPlan other = plans.get(Point2.pack(plan.x, plan.y));
        if(other == null){
            plans.put(Point2.pack(plan.x, plan.y), plan);
            //getUnit().set(getX(), getY());
            //getUnit().team(((Teamc)as()).team());
            getUnit().addBuild(plan, false);
            //Log.info("Build:" + plan + ":" + getUnit().plans());
        }else{
            other.breaking = plan.breaking;
        }
        //getUnit().addBuild(plan, true);
        //putPlan(plan);
    }

    default boolean builderValid(){
        //return builderMod().graph != null && !builderMod().graph.cores.isEmpty() && ((Building)as()).efficiency > 0;
        return builderMod().graph != null && !builderMod().graph.cores.isEmpty();
    }

    default void placedBuilder(){
        builderMod().findConnections(as());
    }

    default void removeBuilder(){
        builderMod().graph.remove(as());
    }

    default void endBuilderUpdate(){
        BlockUnitc u = getUnit();
        Building as = as();
        Queue<BuildPlan> plans = u.plans();
        EndBuilderModule mod = builderMod();
        u.tile(as());
        u.team(as.team());

        if(u.activelyBuilding()){
            u.rotation(angleTo(u.buildPlan()));

            u.buildSpeedMultiplier(as.potentialEfficiency * mod.efficiency);
            //Log.info("AGAWRSERH:" + mod.efficiency + " Pf:" + as.potentialEfficiency);
            //u.buildSpeedMultiplier(1f);

            u.plans().remove(b -> b.build() == this);
        }
        int pl = plans.size;
        /*
        if(pl > 0){
            Log.info("A:" + plans.toString());
        }
        */
        u.updateBuildLogic();
        /*
        if(pl > 0){
            Log.info("B:" + plans.toString());
        }
        */
        if(pl != plans.size){
            //mod.validPlans.clear();
            mod.planMap.clear();
            for(BuildPlan plan : plans){
                //putPlan(plan);
                mod.planMap.put(Point2.pack(plan.x, plan.y), plan);
            }
        }
    }

    default void initBuilder(){
        if(!builderMod().init){
            //Log.info("EA:" + builderMod().efficiency);
            //builderMod().updateLength(as());
            //Log.info("EB:" + builderMod().efficiency);

            Core.app.post(() -> {
                if(!builderMod().init) builderMod().graph.reflow(as());
            });
            getUnit().tile(self());
            getUnit().team(((Teamc)as()).team());
            //Log.info("Endrgqgq4:" + x() + ":" + y());
        }
    }

    default void drawConnections(){
        Building as = as();
        EndBuilderModule mod = builderMod();
        float x = x(), y = y();
        if(Mathf.zero(Renderer.laserOpacity) || as.isPayload()) return;
        Draw.z(Layer.power - 1f);
        //Draw.color(Color.red, Renderer.laserOpacity);
        Lines.stroke(2f);

        for(int i = 0; i < mod.links.size; i++){
            Building b = Vars.world.build(mod.links.items[i]);
            if(b != null && b.id < id()){
                //Lines.line(x, y, b.x, b.y, false);
                /*
                float rot = angleTo(b);
                Vec2 v1 = Tmp.v1.trns(rot, as.block.size * 4).add(this);
                Vec2 v2 = Tmp.v2.trns(rot + 180f, b.block.size * 4).add(b);
                Fill.circle(v1.x, v1.y, 1.5f);
                Fill.circle(v2.x, v2.y, 1.5f);
                Lines.stroke(2f);
                 */
                EndBuilders.drawLaser(as, as.block.size * 4, b, b.block.size * 4, 0.5f);
            }
        }
        Draw.color(Color.red, Renderer.laserOpacity);
        if(getTargetUnit() != null){
            Lines.line(x, y, getTargetUnit().x, getTargetUnit().y, false);
        }
        Lines.stroke(1.5f);
        if(builderValid()){
            Lines.circle(x, y, range());
        }

        Draw.reset();
    }
}

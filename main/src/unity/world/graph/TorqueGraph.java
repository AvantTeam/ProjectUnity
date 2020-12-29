package unity.world.graph;

import arc.util.*;
import unity.world.graphs.*;
import unity.world.modules.*;

//rotGraph
public class TorqueGraph<T extends GraphTorque> extends BaseGraph<GraphTorqueModule<T>, TorqueGraph<T>>{
    public float lastInertia, lastGrossForceApplied, lastNetForceApplied, lastVelocity, lastFrictionCoefficient;

    @Override
    public TorqueGraph<T> create(GraphTorqueModule<T> module){
        return new TorqueGraph();
    }

    @Override
    void copyGraphStatsFrom(TorqueGraph<T> graph){
        lastVelocity = graph.lastVelocity;
    }

    @Override
    boolean canConnect(GraphTorqueModule<T> b1, GraphTorqueModule<T> b2){
        return b1.parent.build.team() == b2.parent.build.team();
    }

    @Override
    void updateOnGraphChanged(){}

    @Override
    void updateGraph(){
        float netForce = lastGrossForceApplied - lastFrictionCoefficient * lastVelocity * lastVelocity;
        lastNetForceApplied = netForce;
        float acceleration = lastInertia == 0f ? 0f : netForce / lastInertia;
        lastVelocity += acceleration * Time.delta;
        lastVelocity = Math.max(0f, lastVelocity);
    }

    @Override
    void updateDirect(){
        float forceApply = 0f;
        float fricCoeff = 0f;
        float iner = 0f;
        for(var module : connected){//building, GraphTorqueModule
            forceApply += module.force;
            fricCoeff += module.friction();
            iner += module.inertia;
        }
        lastFrictionCoefficient = fricCoeff;
        lastGrossForceApplied = forceApply;
        lastInertia = iner;
    }

    @Override
    void addMergeStats(GraphTorqueModule<T> module){}

    @Override
    void mergeStats(TorqueGraph<T> graph){
        float momentumA = lastVelocity * lastInertia;
        float mementumB = graph.lastVelocity * graph.lastInertia;
        lastVelocity = (momentumA + mementumB) / (lastInertia + graph.lastInertia);
    }

    public void injectInertia(float iner){
        float inerSum = lastInertia + iner;
        lastVelocity *= inerSum == 0f ? 0f : lastInertia / inerSum;
    }
}

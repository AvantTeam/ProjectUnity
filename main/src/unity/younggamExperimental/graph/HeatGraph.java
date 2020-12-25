package unity.younggamExperimental.graph;

import unity.younggamExperimental.graphs.*;
import unity.younggamExperimental.modules.*;

//heatGraph
public class HeatGraph extends BaseGraph<GraphHeatModule, HeatGraph>{
    float lastHeatFlow;

    public HeatGraph(GraphHeatModule module/*building*/){
        super(module);
    }

    @Override
    HeatGraph create(GraphHeatModule module/*building*/){
        return new HeatGraph(module);
    }

    @Override
    void copyGraphStatsFrom(HeatGraph graph){
        //TODO ??? why lastVelocity
    }

    @Override
    void updateOnGraphChanged(){}

    @Override
    void updateGraph(){
        lastHeatFlow = 0f;
        connected.each(module -> {
            module.heat += module.heatBuffer;
            lastHeatFlow += module.heatBuffer;
        });
    }

    @Override
    void updateDirect(){}

    @Override
    void addMergeStats(GraphHeatModule module/*building*/){}

    @Override
    void mergeStats(HeatGraph graph){
        lastHeatFlow += graph.lastHeatFlow;
    }
}

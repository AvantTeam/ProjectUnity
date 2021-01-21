package unity.world.graph;

import unity.world.modules.*;

public class FluxGraph extends BaseGraph<GraphFluxModule, FluxGraph>{
    float flux, fluxTotal;

    @Override
    public FluxGraph create(){
        return new FluxGraph();
    }

    @Override
    void copyGraphStatsFrom(FluxGraph graph){}

    @Override
    void updateOnGraphChanged(){}

    @Override
    void updateGraph(){
        fluxTotal = 0f;
        int totalMags = 0;
        for(var module : connected){//building
            fluxTotal += module.flux();
            if(module.graph.fluxProducer) totalMags++;
        }
        float weight = 1f;
        if(totalMags > 1) weight = (float)(1.5 * totalMags / (Math.log10(totalMags) + 1) - 0.5);
        flux = fluxTotal / weight;
    }

    @Override
    void updateDirect(){}

    @Override
    void addMergeStats(GraphFluxModule module){}

    @Override
    void mergeStats(FluxGraph graph){}

    //
    public float flux(){
        return flux;
    }
}

package unity.younggamExperimental.graph;

import unity.younggamExperimental.modules.*;

public class CrucibleGraph extends BaseGraph<GraphCrucibleModule, CrucibleGraph>{
    public CrucibleGraph(GraphCrucibleModule module){
        super(module);
    }

    @Override
    CrucibleGraph create(GraphCrucibleModule module){
        return null;
    }

    @Override
    void copyGraphStatsFrom(CrucibleGraph graph){

    }

    @Override
    void updateOnGraphChanged(){

    }

    @Override
    void updateGraph(){

    }

    @Override
    void updateDirect(){

    }

    @Override
    void addMergeStats(GraphCrucibleModule module){

    }

    @Override
    void mergeStats(CrucibleGraph graph){

    }
}

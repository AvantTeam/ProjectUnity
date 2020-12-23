package unity.younggamExperimental.graphs;

import arc.func.*;
import arc.struct.*;
import mindustry.world.meta.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.blocks.GraphBlockBase.*;
import unity.younggamExperimental.graph.*;
import unity.younggamExperimental.modules.*;

//GraphCommonBlock 블럭에 쓰일것 consumers 역할?
public class Graphs{
    private final Graph[] graphBlocks = new Graph[GraphType.values().length];
    private final Prov<GraphModule>[] graphBuilings = new Prov[GraphType.values().length];
    private final Seq<GraphType> results = new Seq<>(4);//null 연산 매번 안하려고
    boolean useOriginalUpdate = true;

    //TODO 굳이? 싶은함수들 위에 그리고 <T extends asdf>?

    Graph getGraphConnectorBlock(GraphType type){
        if(graphBlocks[type.ordinal()] == null) throw new IllegalArgumentException();
        return graphBlocks[type.ordinal()];
    }

    //set block,build,builders 종합
    public void setGraphConnectorTypes(Graph graph, Prov<GraphModule> build){
        int i = graph.type().ordinal();
        graphBlocks[i] = graph;
        graphBuilings[i] = build;
        results.add(graph.type());
    }

    public void injectGraphConnector(GraphModules gms){
        for(GraphType type : results){
            int i = type.ordinal();
            GraphModule gt = graphBuilings[i].get();
            gms.setGraphConnector(i, gt);
            gt.type = graphBlocks[i];
        }
    }

    public void setStats(Stats stats){
        stats.add(Stat.abilities, table -> {
            for(GraphType type : results) graphBlocks[type.ordinal()].setStats(table);
        });
    }

    public void drawPlace(int x, int y, int size, int rotation, boolean valid){
        for(GraphType type : results) graphBlocks[type.ordinal()].drawPlace(x, y, size, rotation, valid);
    }

    public boolean useOriginalUpdate(){
        return useOriginalUpdate;
    }

    public void disableOgUpdate(){
        useOriginalUpdate = false;
    }
}

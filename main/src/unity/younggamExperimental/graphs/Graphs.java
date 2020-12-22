package unity.younggamExperimental.graphs;

import arc.func.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.meta.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.graph.*;
import unity.younggamExperimental.modules.*;

//GraphCommonBlock 블럭에 쓰일것 consumers 역할?
public class Graphs{
    private final Graph[] graphBlocks = new Graph[GraphType.values().length];
    private final Prov<GraphModule>[] graphBuilings = new Prov[GraphType.values().length];
    private final Prov<BaseGraph>[] graphBuilders = new Prov[GraphType.values().length];
    private final Seq<GraphType> results = new Seq<>(4);//null 연산 매번 안하려고
    boolean useOriginalUpdate = true, networkConnector = true;

    //TODO 굳이? 싶은함수들 위에 그리고 <T extends asdf>?

    Graph getGraphConnectorBlock(GraphType type){
        if(graphBlocks[type.ordinal()] == null) throw new IllegalArgumentException();
        return graphBlocks[type.ordinal()];
    }

    //set block,build,builders 종합
    void setGraphConnectorTypes(Graph graph, Prov<GraphModule> build, Prov<BaseGraph> builder){
        int i = graph.type().ordinal();
        graphBlocks[i] = graph;
        graphBuilings[i] = build;
        graphBuilders[i] = builder;
        results.add(graph.type());
    }

    void injectGraphConnector(Building build){
        for(GraphType type : results){
            GraphModule gt = graphBuilings[type.ordinal()].get();
            //TODO
            gt.parentBlock = graphBlocks[type.ordinal()];
        }
    }

    void setStats(Stats stats){
        stats.add(Stat.abilities, table -> {
            for(GraphType type : results) graphBlocks[type.ordinal()].setStats(table);
        });
    }

    void drawPlace(int x, int y, int size, int rotation, boolean valid){
        for(GraphType type : results) graphBlocks[type.ordinal()].drawPlace(x, y, size, rotation, valid);
    }
}

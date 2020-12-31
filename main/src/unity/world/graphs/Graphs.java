package unity.world.graphs;

import arc.struct.*;
import mindustry.world.meta.*;
import unity.world.meta.*;
import unity.world.modules.*;

//GraphCommonBlock 블럭에 쓰일것 consumers 역할?
public class Graphs{
    private final Graph[] graphBlocks = new Graph[GraphType.values().length];
    private final ObjectSet<GraphType> results = new ObjectSet<>(4);//null 연산 매번 안하려고
    boolean useOriginalUpdate = true;
    //isNetworkConnector - omitted since this is never set to false

    public <T extends Graph> T getGraphConnectorBlock(GraphType type){
        if(graphBlocks[type.ordinal()] == null) throw new IllegalArgumentException();
        return (T)graphBlocks[type.ordinal()];
    }

    public boolean hasGraph(GraphType type){
        return results.contains(type);
    }

    //set block,build,builders 종합
    public void setGraphConnectorTypes(Graph graph){
        int i = graph.type().ordinal();
        graphBlocks[i] = graph;
        results.add(graph.type());
    }

    public void injectGraphConnector(GraphModules gms){
        for(GraphType type : results){
            int i = type.ordinal();
            gms.setGraphConnector(graphBlocks[i].module());
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

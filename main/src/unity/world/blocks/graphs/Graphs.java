package unity.world.blocks.graphs;

public class Graphs{
    private Graph<?>[] modules = new Graph[GraphType.all.length];

    public void graph(Graph<?> graph){
        modules[graph.type.ordinal()] = graph;
    }
}

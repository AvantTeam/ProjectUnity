package unity.younggamExperimental.graphs;

import arc.*;
import arc.struct.*;
import unity.younggamExperimental.modules.*;

//그래프 그자체 TODO abstract로 만들기?, build들 이름 바꾸기?
public class GraphBase{
    private static int lastId;
    final ObjectSet<GraphModule> connected = new ObjectSet<>();
    long lastFrameUpdated;
    final int id;

    {
        id = lastId++;
    }

    public GraphBase(GraphModule build){
        //TODO  그리고 init 으로 분리하기?
        connected.add(build);
        updateOnGraphChanged();
        addMergeStats(build);
    }

    GraphBase copyGraph(GraphModule build){
        GraphBase copygraph = new GraphBase(build);
        copygraph.copyGraphStatsFrom(this);
        return copygraph;
    }

    void copyGraphStatsFrom(GraphBase graph){}

    public void update(){
        long frameId = Core.graphics.getFrameId();
        if(frameId == lastFrameUpdated) return;
        lastFrameUpdated = frameId;
        updateDirect();
        updateGraph();
    }

    void updateOnGraphChanged(){}

    void updateGraph(){}

    void updateDirect(){}

    boolean canConnect(GraphModule b1, GraphModule b2){
        return true;
    }

    void addBuilding(GraphModule build, int connectIndex){
        connected.add(build);
        updateOnGraphChanged();
        build.setNetworkOfPort(connectIndex, this);
        addMergeStats(build);
    }

    void addMergeStats(GraphModule build){}

    void mergeGraph(GraphBase graph){
        if(graph == null) return;
        if(graph.connected.size > connected.size){
            graph.mergeGraph(this);
            return;
        }
        updateDirect();
        graph.updateDirect();
        mergeStats(graph);
        graph.connected.each(build -> {
            if(!connected.contains(build) && build.replaceNetwork(graph, this))
                connected.add(build);
        });
        updateOnGraphChanged();
    }

    void mergeStats(GraphBase graph){}

    void killGraph(){
        connected.clear();
    }

    boolean isAtriculationPoint(GraphModule build){
        //TODO
        return true;
    }

    public void remove(GraphModule build){
        if(!connected.contains(build)) return;
        int c = build.countNeighbours();
        if(c == 0) return;
        //TODO
        killGraph();
        ObjectSet<GraphBase> networksAdded = new ObjectSet<>(c);
        int newNets = 0;
        build.eachNeighbour(n -> {
            GraphBase copyNet = build.getNetworkOfPort(n.portIndex);
            if(copyNet == this){
                GraphModule selfref = n.getNeighbour(build);
                if(selfref == null) return;
                n.setNetworkOfPort(selfref.portIndex, copyNet.copyGraph(n));
            }
        });
        build.eachNeighbour(n -> {
            if(build.getNetworkOfPort(n.portIndex) == this){
                GraphModule selfref = n.getNeighbour(build);
                if(selfref == null) return;
                GraphBase neiNet = n.getNetworkOfPort(selfref.portIndex);
                if(!networksAdded.contains(neiNet)){
                    networksAdded.add(neiNet);
                    neiNet.rebuildGraphIndex(n, selfref.portIndex);
                }
            }
        });
        build.replaceNetwork(this, null);
    }

    public void rebuildGraph(GraphModule build){
        rebuildGrpahWithSet(build, ObjectSet.with(build), -1);
    }

    void rebuildGraphIndex(GraphModule build, int index){
        rebuildGrpahWithSet(build, ObjectSet.with(build), index);
    }

    void rebuildGrpahWithSet(GraphModule build, ObjectSet<GraphModule> searched, int rootIndex){
        //TODO
    }

    String connectedToString(){
        StringBuilder s = new StringBuilder("Network:" + id + ":");
        connected.each(build -> s.append(build.parentBuilding.block.localizedName + ", "));
        return s.toString();
    }
}

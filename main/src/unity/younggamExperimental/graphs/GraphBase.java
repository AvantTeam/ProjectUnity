package unity.younggamExperimental.graphs;

import arc.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.*;

//그래프 그자체 TODO abstract로 만들기?
public class GraphBase{
    final ObjectSet<Building> connected = new ObjectSet<>();
    long lastFrameUpdated;
    int id;

    GraphBase(Building build){
        //TODO
        connected.add(build);
        updateOnGraphChanged();
        addMergeStats(build);
    }

    GraphBase copyGraph(Building build){
        GraphBase copygraph = new GraphBase(build);
        return copygraph;
    }

    void copyGraphStatsFrom(GraphBase graph){}

    void update(){
        long frameId = Core.graphics.getFrameId();
        if(frameId == lastFrameUpdated) return;
        lastFrameUpdated = frameId;
        updateDirect();
        updateGraph();
    }

    void updateOnGraphChanged(){}

    void updateGraph(){}

    void updateDirect(){}

    boolean canConnect(Building b1, Building b2){
        return true;
    }

    void addBuilding(Building build, int connectIndex){
        connected.add(build);
        updateOnGraphChanged();
        //TODO
        addMergeStats(build);
    }

    void addMergeStats(Building build){}

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
            if(!connected.contains(build)){
                //TODO
                connected.add(build);
            }
        });
        updateOnGraphChanged();
    }

    void mergeStats(GraphBase graph){}

    void killGraph(){
        connected.clear();
    }

    boolean isAtriculationPoint(Building build){
        //TODO
        return true;
    }

    void remove(Building build){
        if(!connected.contains(build)) return;
        //TODO
    }

    void rebuildGraph(Building build){
        rebuildGrpahWithSet(build, ObjectSet.with(build), -1);
    }

    void rebuildGraphIndex(Building build, int index){
        rebuildGrpahWithSet(build, ObjectSet.with(build), index);
    }

    void rebuildGrpahWithSet(Building build, ObjectSet searched, int rootIndex){
        //TODO
    }

    String connectedToString(){
        StringBuilder s = new StringBuilder("Network:" + id + ":");
        //TODO
        connected.each(building -> s.append(", "));
        return s.toString();
    }
}

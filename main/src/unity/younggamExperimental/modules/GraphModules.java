package unity.younggamExperimental.modules;

import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import unity.younggamExperimental.*;

//GraphCommonBuild. ㅁㄴ이라ㅓㅣ
public class GraphModules{
    public Building build;
    final ObjectSet<GraphModule> graphs = new ObjectSet<>(4);
    int prevTileRotation = -1;

    //TODO 굳이 싶은것들?
    void setGraphConnector(GraphModule graph){
        graph.parent = this;
        graphs.add(graph);
    }

    GraphData getConnectSidePos(int index){
        return GraphData.getConnectSidePos(index, build.block.size, build.rotation);
    }

    void created(){//create
        for(var graphConn : graphs) graphConn.onCreate(build);
        prevTileRotation = -1;
    }

    float efficiency(){
        float e = 0f;
        for(var graph : graphs) e *= graph.efficiency();
        return Math.max(0f, e);
    }

    void onRemoved(){
        updateGraphRemovals();
    }

    //onDestroyed. 중복 호출?
    void updateGraphRemovals(){
        graphs.each(GraphModule::onRemoved);
    }

    void updateTile(){
        if(!build.block.rotate) build.rotation = 0;
        if(prevTileRotation != build.rotation){
            for(var graph : graphs) graph.onRotationChanged(prevTileRotation, build.rotation);
        }
        for(var graph : graphs) graph.onUpdate();
        prevTileRotation = build.rotation;
    }

    void onProximityChanged(){
        graphs.each(GraphModule::proximityUpdateCustom);
    }

    void display(Table table){
        for(var graph : graphs) graph.display(table);
    }

    void displayBars(Table table){
        for(var graph : graphs) graph.displayBars(table);
    }

    void write(Writes writes){
        for(var graph : graphs) graph.write(writes);
    }

    void read(Reads reads, byte revision){
        for(var graph : graphs) graph.read(reads, revision);
    }
}

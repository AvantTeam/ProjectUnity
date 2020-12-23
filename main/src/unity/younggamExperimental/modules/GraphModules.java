package unity.younggamExperimental.modules;

import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.blocks.GraphBlockBase.*;

//GraphCommonBuild. ㅁㄴ이라ㅓㅣ
public class GraphModules{
    public final GraphBuildBase build;
    final IntMap<GraphModule> graphs = new IntMap<>(4);
    int prevTileRotation = -1;

    public GraphModules(GraphBuildBase build){
        this.build = build;
    }

    //TODO 굳이 싶은것들?
    public void setGraphConnector(int i, GraphModule graph){
        graph.parent = this;
        graphs.put(i, graph);
    }

    public GraphModule getGraphConnector(GraphType type){
        return graphs.get(type.ordinal());
    }

    GraphData getConnectSidePos(int index){
        return GraphData.getConnectSidePos(index, build.block().size, build.rotation());
    }

    public void created(){//create
        for(var graphConn : graphs.values()) graphConn.onCreate(build);
        prevTileRotation = -1;
    }

    public float efficiency(){
        float e = 1f;
        for(var graph : graphs.values()) e *= graph.efficiency();
        return Math.max(0f, e);
    }

    //onDestroyed. 중복 호출?
    public void updateGraphRemovals(){
        for(var graph : graphs.values()) graph.onRemoved();
    }

    public void updateTile(){
        if(!build.block().rotate) build.rotation(0);
        if(prevTileRotation != build.rotation()){
            for(var graph : graphs.values()) graph.onRotationChanged(prevTileRotation, build.rotation());
            build.onRotationChanged();
        }
        for(var graph : graphs.values()) graph.onUpdate();
    }

    public void onProximityUpdate(){
        for(var graph : graphs.values()) graph.proximityUpdateCustom();
    }

    public void display(Table table){
        for(var graph : graphs.values()) graph.display(table);
    }

    public void displayBars(Table table){
        for(var graph : graphs.values()) graph.displayBars(table);
    }

    public void write(Writes write){
        for(var graph : graphs.values()) graph.write(write);
    }

    public void read(Reads read, byte revision){
        for(var graph : graphs.values()) graph.read(read, revision);
    }

    //
    public void prevTileRotation(int r){
        prevTileRotation = r;
    }
}

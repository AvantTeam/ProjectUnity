package unity.world.modules;

import arc.scene.ui.layout.*;
import arc.util.io.*;
import unity.world.meta.*;
import unity.world.blocks.GraphBlockBase.*;

//GraphCommonBuild. ㅁㄴ이라ㅓㅣ
public class GraphModules{
    public final GraphBuildBase build;
    //unMapped becuz of typeCastings DON'T MODIFY
    private GraphHeatModule heat;
    private GraphTorqueModule torque;
    private GraphCrucibleModule crucible;
    int prevTileRotation = -1;

    public GraphModules(GraphBuildBase build){
        this.build = build;
    }

    public GraphModule getGraphConnector(GraphType type){
        if(type == GraphType.heat) return heat;
        if(type == GraphType.torque) return torque;
        if(type == GraphType.crucible) return crucible;
        return null;
    }

    public <T extends GraphModule> void setGraphConnector(T graph){
        graph.parent = this;
        if(graph instanceof GraphHeatModule heat) this.heat = heat;
        if(graph instanceof GraphTorqueModule torque) this.torque = torque;
        if(graph instanceof GraphCrucibleModule crucible) this.crucible = crucible;
    }

    public GraphHeatModule heat(){
        return heat;
    }

    public GraphTorqueModule torque(){
        return torque;
    }

    public GraphCrucibleModule crucible(){
        return crucible;
    }

    public GraphData getConnectSidePos(int index){
        return GraphData.getConnectSidePos(index, build.block().size, build.rotation());
    }

    public void created(){//create
        if(heat != null) heat.onCreate(build);
        if(torque != null) torque.onCreate(build);
        if(crucible != null) crucible.onCreate(build);
        prevTileRotation = -1;
    }

    public float efficiency(){
        float e = 1f;
        if(heat != null) e *= heat.efficiency();
        if(torque != null) e *= torque.efficiency();
        if(crucible != null) e *= crucible.efficiency();
        return Math.max(0f, e);
    }

    //onDestroyed. 중복 호출?
    public void updateGraphRemovals(){
        if(heat != null) heat.onRemoved();
        if(torque != null) torque.onRemoved();
        if(crucible != null) crucible.onRemoved();
    }

    public void updateTile(){
        if(!build.block().rotate) build.rotation(0);
        if(prevTileRotation != build.rotation()){
            if(heat != null) heat.onRotationChanged(prevTileRotation, build.rotation());
            if(torque != null) torque.onRotationChanged(prevTileRotation, build.rotation());
            if(crucible != null) crucible.onRotationChanged(prevTileRotation, build.rotation());
            build.onRotationChanged();
        }
        if(heat != null) heat.onUpdate();
        if(torque != null) torque.onUpdate();
        if(crucible != null) crucible.onUpdate();
    }

    public void onProximityUpdate(){
        if(heat != null) heat.proximityUpdateCustom();
        if(torque != null) torque.proximityUpdateCustom();
        if(crucible != null) crucible.proximityUpdateCustom();
    }

    public void display(Table table){
        if(heat != null) heat.display(table);
        if(torque != null) torque.display(table);
        if(crucible != null) crucible.display(table);
    }

    public void displayBars(Table table){
        if(heat != null) heat.displayBars(table);
        if(torque != null) torque.displayBars(table);
        if(crucible != null) crucible.displayBars(table);
    }

    public void write(Writes write){
        if(heat != null) heat.write(write);
        if(torque != null) torque.write(write);
        if(crucible != null) crucible.write(write);
    }

    public void read(Reads read, byte revision){
        if(heat != null) heat.read(read, revision);
        if(torque != null) torque.read(read, revision);
        if(crucible != null) crucible.read(read, revision);
    }

    //
    public void prevTileRotation(int r){
        prevTileRotation = r;
    }

    //xelo must have forgotten this
    public void drawSelect(){
        if(heat != null) heat.drawSelect();
        if(torque != null) torque.drawSelect();
        if(crucible != null) crucible.drawSelect();
    }
}

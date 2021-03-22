package unity.world.modules;

import arc.scene.ui.layout.*;
import arc.util.io.*;
import unity.world.graphs.*;
import unity.world.meta.*;
import unity.world.blocks.GraphBlockBase.*;

public class GraphModules{
    public final GraphBuildBase build;
    
    private GraphHeatModule heat;
    private GraphTorqueModule<? extends GraphTorque> torque;
    private GraphCrucibleModule crucible;
    private GraphFluxModule flux;
    
    private boolean hasHeat, hasTorque, hasCrucible, hasFlux;
    int prevTileRotation = -1;

    public GraphModules(GraphBuildBase build){
        this.build = build;
    }

    public GraphModule getGraphConnector(GraphType type){
        if(type == GraphType.heat) return heat;
        if(type == GraphType.torque) return torque;
        if(type == GraphType.crucible) return crucible;
        if(type == GraphType.flux) return flux;
        
        return null;
    }

    public <T extends GraphModule> void setGraphConnector(T graph){
        graph.parent = this;
        
        if(graph instanceof GraphHeatModule heat){
            this.heat = heat;
            hasHeat = heat != null;
        }
        if(graph instanceof GraphTorqueModule torque){
            this.torque = torque;
            hasTorque = torque != null;
        }
        if(graph instanceof GraphCrucibleModule crucible){
            this.crucible = crucible;
            hasCrucible = crucible != null;
        }
        if(graph instanceof GraphFluxModule flux){
            this.flux = flux;
            hasFlux = flux != null;
        }
    }

    public GraphHeatModule heat(){
        return heat;
    }

    public GraphTorqueModule<? extends GraphTorque> torque(){
        return torque;
    }

    public GraphCrucibleModule crucible(){
        return crucible;
    }

    public GraphFluxModule flux(){
        return flux;
    }

    public GraphData getConnectSidePos(int index){
        return GraphData.getConnectSidePos(index, build.block().size, build.rotation());
    }

    public void created(){
        if(hasHeat) heat.onCreate(build);
        if(hasTorque) torque.onCreate(build);
        if(hasCrucible) crucible.onCreate(build);
        if(hasFlux) flux.onCreate(build);
        prevTileRotation = -1;
    }

    public float efficiency(){
        float e = 1f;
        
        if(hasHeat) e *= heat.efficiency();
        if(hasTorque) e *= torque.efficiency();
        if(hasCrucible) e *= crucible.efficiency();
        if(hasFlux) e *= flux.efficiency();
        
        return Math.max(0f, e);
    }

    //onDestroyed. 중복 호출?
    public void updateGraphRemovals(){
        if(hasHeat) heat.onRemoved();
        if(hasTorque) torque.onRemoved();
        if(hasCrucible) crucible.onRemoved();
        if(hasFlux) flux.onRemoved();
    }

    public void updateTile(){
        if(!build.block().rotate) build.rotation(0);
        if(prevTileRotation != build.rotation()){
            if(hasHeat) heat.onRotationChanged(prevTileRotation, build.rotation());
            if(hasTorque) torque.onRotationChanged(prevTileRotation, build.rotation());
            if(hasCrucible) crucible.onRotationChanged(prevTileRotation, build.rotation());
            if(hasFlux) flux.onRotationChanged(prevTileRotation, build.rotation());
            
            build.onRotationChanged();
        }
        if(hasHeat) heat.onUpdate();
        if(hasTorque) torque.onUpdate();
        if(hasCrucible) crucible.onUpdate();
        if(hasFlux) flux.onUpdate();
    }

    public void onProximityUpdate(){
        if(hasHeat) heat.proximityUpdateCustom();
        if(hasTorque) torque.proximityUpdateCustom();
        if(hasCrucible) crucible.proximityUpdateCustom();
        if(hasFlux) flux.proximityUpdateCustom();
    }

    public void display(Table table){
        if(hasHeat) heat.display(table);
        if(hasTorque) torque.display(table);
        if(hasCrucible) crucible.display(table);
        if(hasFlux) flux.display(table);
    }

    public void displayBars(Table table){
        if(hasHeat) heat.displayBars(table);
        if(hasTorque) torque.displayBars(table);
        if(hasCrucible) crucible.displayBars(table);
        if(hasFlux) flux.displayBars(table);
    }

    public void write(Writes write){
        if(hasHeat) heat.write(write);
        if(hasTorque) torque.write(write);
        if(hasCrucible) crucible.write(write);
        if(hasFlux) flux.write(write);
    }

    public void read(Reads read, byte revision){
        if(hasHeat) heat.read(read, revision);
        if(hasTorque) torque.read(read, revision);
        if(hasCrucible) crucible.read(read, revision);
        if(hasFlux) flux.read(read, revision);
    }

    public void prevTileRotation(int r){
        prevTileRotation = r;
    }

    public void drawSelect(){
        if(hasHeat) heat.drawSelect();
        if(hasTorque) torque.drawSelect();
        if(hasCrucible) crucible.drawSelect();
        if(hasFlux) flux.drawSelect();
    }
}

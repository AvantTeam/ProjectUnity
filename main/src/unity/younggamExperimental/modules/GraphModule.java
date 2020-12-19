package unity.younggamExperimental.modules;

import arc.func.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.graphs.*;

//building 에 들어갈 모듈. powerModule와 비슷한 역할?
public class GraphModule{
    public final int portIndex;

    public Building parentBuilding;

    final Seq<GraphData> acceptPorts = new Seq<>(8);
    final ObjectSet<GraphModule> neighbours = new ObjectSet<>(4);//neighbourArray

    protected GraphBase network;

    Block parentBlock;
    int lastRecalc;
    boolean dead, needsNetworkUpdate/*TODO =true*/, networkSaveState;

    GraphModule(Building parentBuilding, int portIndex){
        this.parentBuilding = parentBuilding;
        this.portIndex = portIndex;
    }

    GraphData getConnectSidePos(int index){
        return GraphData.getConnectSidePos(index, parentBuilding.block.size, parentBuilding.rotation);
    }

    int canConnect(Point2 pos){
        return acceptPorts.indexOf(d -> pos.equals(d.toPos.x + parentBuilding.tileX(), d.toPos.y + parentBuilding.tileY()));
    }

    void onCreate(Building build){
        initAllNets();
        needsNetworkUpdate = true;
        lastRecalc = -1;
        initStats();
    }

    void recalcPorts(){
        if(lastRecalc == parentBuilding.rotation) return;
        acceptPorts.clear();
        for(int i = 0, len = parentBuilding.block.size * 4; i < len; i++) acceptPorts.add(getConnectSidePos(i));
        lastRecalc = parentBuilding.rotation;
    }

    void onRemoved(){
        deleteSelfFromNetwork();
        deleteFromNeighbours();
    }

    void deleteFromNeighbours(){
        neighbours.each(n -> n.removeNeighbour(this));
    }

    void deleteSelfFromNetwork(){
        dead = true;
        if(network != null) network.remove(this);
    }

    void onUpdate(){
        if(dead) return;
        updateNetworks();
        updateExtension();
    }

    void onRotationChanged(int prevRot, int newRot){
        if(prevRot != -1){
            deleteSelfFromNetwork();
            deleteFromNeighbours();
            dead = false;
            initAllNets();
            neighbours.clear(4);
        }
        recalcPorts();
        needsNetworkUpdate = true;
    }

    void updateNetworks(){
        if(network != null){
            if(needsNetworkUpdate){
                needsNetworkUpdate = false;
                network.rebuildGraph(this);
                if(networkSaveState){
                    applySaveState(network);
                    networkSaveState = false;
                }
                //TODO
            }
            network.update();
            updateProps(network, 0);
        }
    }

    void applySaveState(GraphBase graph/*TODO*/){}

    void updateExtension(){}

    void updateProps(GraphBase graph, int index){}

    void proximityUpdateCustom(){}

    void display(Table table){}

    void initStats(){}

    void displayBars(/*TODO*/){}

    void drawSelect(){}

    void initAllNets(){
        recalcPorts();
        network = new GraphBase(this);
    }

    public GraphModule getNeighbour(GraphModule build){
        return neighbours.get(build);
    }

    public void eachNeighbour(Cons<GraphModule> func){
        neighbours.each(func);
    }

    float efficiency(){
        return 1f;
    }

    public int countNeighbours(){
        return neighbours.size;
    }

    void removeNeighbour(GraphModule build){
        neighbours.remove(build);
        //TODO
    }

    void addNeighbour(GraphModule n){
        neighbours.add(n);
        //TODO
    }

    GraphBase getNetwork(){
        return network;
    }

    public boolean replaceNetwork(GraphBase old, GraphBase set){
        network = set;
        return true;
    }

    //TODO 굳이? 싶은 함수들 여기
    public GraphBase getNetworkOfPort(int index){
        return network;
    }

    public void setNetworkOfPort(int index, GraphBase net){
        network = net;
    }

    void writeGlobal(Writes writes){}

    void readGlobal(Reads reads, byte revision){}

    void writeLocal(Writes writes, GraphBase graph){}

    void readLocal(Writes writes, byte revision){}

    void write(Writes writes){
        writeGlobal(writes);
        writeLocal(writes, network);
    }

    void read(Reads reads, byte revision){
        readGlobal(reads, revision);
        //TODO
        networkSaveState = true;
    }
}

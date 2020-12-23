package unity.younggamExperimental.modules;

import arc.func.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.graph.*;
import unity.younggamExperimental.graphs.*;

//GraphPropsCommon building 에 들어갈 모듈. powerModule와 비슷한 역할?
public abstract class GraphModule{
    public final Seq<GraphData> acceptPorts = new Seq<>(8);
    public final int portIndex;

    public GraphModules parent;
    public Graph parentBlock;
    public int d;

    final ObjectSet<GraphModule> neighbours = new ObjectSet<>(4);//neighbourArray

    protected BaseGraph network;

    int lastRecalc;
    boolean dead, needsNetworkUpdate = true, networkSaveState;
    Seq saveCache;

    GraphModule(GraphModules parent, int portIndex){
        this.parent = parent;
        this.portIndex = portIndex;
    }

    GraphData getConnectSidePos(int index){
        return parent.getConnectSidePos(index);
    }

    int canConnect(Point2 pos){
        GraphData temp = acceptPorts.find(d -> pos.equals(d.toPos.x + parent.build.tileX(), d.toPos.y + parent.build.tileY()));
        if(temp != null) return temp.index;
        return -1;
    }

    void onCreate(Building build){
        initAllNets();
        lastRecalc = -1;
        initStats();
    }

    void recalcPorts(){
        if(lastRecalc == parent.build.rotation) return;
        acceptPorts.clear();
        for(int i = 0, len = parentBlock.accept.length; i < len; i++){
            if(parentBlock.accept[i] != 0) acceptPorts.add(getConnectSidePos(i));
        }
        lastRecalc = parent.build.rotation;
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
                    applySaveState(network, saveCache.get(0));
                    networkSaveState = false;
                }
                //parent.onGraphUpdate(); TODO
            }
            network.update();
            updateProps(network, 0);
        }
    }

    abstract void applySaveState(BaseGraph graph, Object cache);

    abstract void updateExtension();

    abstract void updateProps(BaseGraph graph, int index);

    abstract void proximityUpdateCustom();

    abstract void display(Table table);

    abstract void initStats();

    abstract void displayBars(Table table);

    abstract void drawSelect();

    //abstract 해결용 임의로 넣은것
    abstract BaseGraph newNetwork();

    void initAllNets(){
        recalcPorts();
        network = newNetwork();
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

    public void removeNeighbour(GraphModule build){
        neighbours.remove(build);
        //parent.build.onNeighboursChanged();TODO
    }

    void addNeighbour(GraphModule n){
        neighbours.add(n);
        //parent.build.onNeighboursChanged();TODO
    }

    public Seq<GraphData> getConnectedNeighbours(int index){
        return acceptPorts;
    }

    BaseGraph getNetwork(){
        return network;
    }

    public boolean replaceNetwork(BaseGraph old, BaseGraph set){
        network = set;
        return true;
    }

    //TODO 굳이? 싶은 함수들 여기
    public BaseGraph getNetworkOfPort(int index){
        return network;
    }

    public void setNetworkOfPort(int index, BaseGraph net){
        network = net;
    }

    abstract void writeGlobal(Writes writes);

    abstract void readGlobal(Reads reads, byte revision);

    abstract void writeLocal(Writes writes, BaseGraph graph);

    abstract <T> T[] readLocal(Reads writes, byte revision);

    void write(Writes writes){
        writeGlobal(writes);
        writeLocal(writes, network);
    }

    void read(Reads reads, byte revision){
        readGlobal(reads, revision);
        saveCache = Seq.with(readLocal(reads, revision));
        networkSaveState = true;
    }

    //내가 추가한거
    public int hueristic(Position target){
        return d + Math.abs(Math.round(parent.build.x - target.getX())) + Math.abs(Math.round(parent.build.y - target.getY()));
    }

    public GraphModule d(int a){
        d = a;
        return this;
    }

    public abstract GraphType type();
}

package unity.younggamExperimental.modules;

import arc.func.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.blocks.GraphBlockBase.*;
import unity.younggamExperimental.graph.*;
import unity.younggamExperimental.graphs.*;

//GraphPropsCommon building 에 들어갈 모듈. powerModule와 비슷한 역할?
//I had stroke
public abstract class GraphModule<T extends Graph, M extends GraphModule<T, M, G>, G extends BaseGraph<T, M, G>>{
    public final Seq<GraphData> acceptPorts = new Seq<>(8);

    public GraphModules parent;
    public T type;//parentBlock
    public int d, portIndex;

    final ObjectSet<M> neighbours = new ObjectSet<>(4);//neighbourArray

    protected final IntMap<G> networks = new IntMap<>(4);

    int lastRecalc;
    boolean dead, needsNetworkUpdate = true, networkSaveState;
    Seq saveCache = new Seq(4);

    GraphData getConnectSidePos(int index){
        return parent.getConnectSidePos(index);
    }

    public int canConnect(Point2 pos){
        GraphData temp = acceptPorts.find(d -> pos.equals(d.toPos.x + parent.build.tileX(), d.toPos.y + parent.build.tileY()));
        if(temp != null) return temp.index;
        return -1;
    }

    void onCreate(GraphBuildBase build){
        initAllNets();
        needsNetworkUpdate = true;
        lastRecalc = -1;
        initStats();
    }

    public void recalcPorts(){
        if(lastRecalc == parent.build.rotation()) return;
        acceptPorts.clear();
        for(int i = 0, len = type.accept.length; i < len; i++){
            if(type.accept[i] != 0) acceptPorts.add(getConnectSidePos(i));
        }
        lastRecalc = parent.build.rotation();
    }

    public void onRemoved(){
        deleteSelfFromNetwork();
        deleteFromNeighbours();
    }

    void deleteFromNeighbours(){
        neighbours.each(n -> n.removeNeighbour((M)this));
    }

    void deleteSelfFromNetwork(){
        dead = true;
        if(networks.get(0) != null) networks.get(0).remove((M)this);
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
        if(networks.get(0) != null){
            if(needsNetworkUpdate){
                needsNetworkUpdate = false;
                networks.get(0).rebuildGraph((M)this);
                if(networkSaveState){
                    applySaveState(networks.get(0));
                    networkSaveState = false;
                }
                parent.build.onGraphUpdate();
            }
            networks.get(0).update();
            updateProps(networks.get(0), 0);
        }
    }

    abstract void applySaveState(G graph);

    abstract void updateExtension();

    abstract void updateProps(G graph, int index);

    abstract void proximityUpdateCustom();

    abstract void display(Table table);

    abstract void initStats();

    abstract void displayBars(Table table);

    abstract void drawSelect();

    //abstract 해결용 임의로 넣은것
    abstract G newNetwork();

    void initAllNets(){
        recalcPorts();
        networks.put(0, newNetwork());
    }

    public boolean dead(){
        return dead;
    }

    public float lastRecalc(){
        return lastRecalc;
    }

    public M getNeighbour(M build){
        return neighbours.get(build);
    }

    public void eachNeighbour(Cons<M> func){
        neighbours.each(func);
    }

    float efficiency(){
        return 1f;
    }

    public int countNeighbours(){
        return neighbours.size;
    }

    public void removeNeighbour(M build){
        neighbours.remove(build);
        parent.build.onNeighboursChanged();
    }

    public void addNeighbour(M n){
        neighbours.add(n);
        parent.build.onNeighboursChanged();
    }

    public Seq<GraphData> getConnectedNeighbours(int index){
        return acceptPorts;
    }

    G getNetwork(){
        return networks.get(0);
    }

    public boolean hasNetwork(G net){
        return networks.get(0).id == net.id;
    }

    int getPortOfNetwork(G net){
        return networks.get(0).id == net.id ? 0 : -1;
    }

    public boolean replaceNetwork(G old, G set){
        networks.put(0, set);
        return true;
    }

    //TODO 굳이? 싶은 함수들 여기
    public G getNetworkOfPort(int index){
        return networks.get(0);
    }

    public void setNetworkOfPort(int index, G net){
        networks.put(0, net);
    }

    abstract void writeGlobal(Writes write);

    abstract void readGlobal(Reads read, byte revision);

    abstract void writeLocal(Writes write, G graph);

    abstract <R> R[] readLocal(Reads read, byte revision);

    void write(Writes write){
        writeGlobal(write);
        writeLocal(write, networks.get(0));
    }

    void read(Reads read, byte revision){
        readGlobal(read, revision);
        saveCache.clear();
        saveCache.add(readLocal(read, revision));
        networkSaveState = true;
    }

    //내가 추가한거
    public abstract GraphType type();

    public int hueristic(Position target){
        return d + Math.abs(Math.round(parent.build.x() - target.getX())) + Math.abs(Math.round(parent.build.y() - target.getY()));
    }

    public M d(int a){
        d = a;
        return (M)this;
    }

    public M portIndex(int a){
        portIndex = a;
        return (M)this;
    }

    public float getTemp(){
        return 0f;
    }

    void setTemp(float t){}
}

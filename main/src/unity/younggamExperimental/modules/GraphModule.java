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
    public T graph;//parentBlock
    public int d, portIndex;

    final ObjectSet<M> neighbours = new ObjectSet<>(4);//neighbourArray

    protected final IntMap<G> networks = new IntMap<>(4);

    int lastRecalc;
    boolean dead, needsNetworkUpdate = true, networkSaveState, multi;//
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
        for(int i = 0, len = graph.accept.length; i < len; i++){
            if(graph.accept[i] != 0) acceptPorts.add(getConnectSidePos(i));
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
        if(multi) deleteSelfFromNetworkMulti();
        else if(networks.get(0) != null) networks.get(0).remove((M)this);
    }

    //multi
    void deleteSelfFromNetworkMulti(){
        if(networks.isEmpty()) return;
        for(var i : networks.values()) i.remove((M)this);
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
        if(multi){
            updateNetworksMulti();
            return;
        }
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

    //multi
    void updateNetworksMulti(){
        if(networks.isEmpty()) return;
        if(needsNetworkUpdate){
            boolean[] covered = new boolean[4];
            int[] portArray = graph.accept;
            for(int i = 0, len = portArray.length; i < len; i++){
                int j = portArray[i] - 1;
                if(portArray[i] == 0 || covered[j]) continue;
                getNetworkOfPort(j).rebuildGraphIndex((M)this, i);
                covered[j] = true;
            }
            if(networkSaveState){
                for(var i : networks) applySaveState(i.value);
            }
            networkSaveState = false;
        }
        for(var i : networks){
            if(i.value == null) continue;
            i.value.update();
            updateProps(i.value, i.key);
        }
        needsNetworkUpdate = false;
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
        if(multi) initAllNetsMulti();
        else networks.put(0, newNetwork());
    }

    //multi
    void initAllNetsMulti(){
        int[] portArray = graph.accept;
        networks.clear();
        for(int i = 0, len = portArray.length; i < len; i++){
            if(portArray[i] != 0 && !networks.containsKey(portArray[i] - 1)) networks.put(portArray[i] - 1, newNetwork());
        }
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
        if(multi) return getConnectedNeighboursMulti(index);
        return acceptPorts;
    }

    //multi
    Seq<GraphData> getConnectedNeighboursMulti(int index){
        int[] portArray = graph.accept;
        int targetPort = portArray[index];
        Seq<GraphData> output = new Seq<>();
        for(var i : acceptPorts){
            if(portArray[i.index] == targetPort) output.add(i);
        }
        return output;
    }

    G getNetwork(){
        return networks.get(0);
    }

    public boolean hasNetwork(G net){
        if(multi) return hasNetworkMulti(net);
        return networks.get(0).id == net.id;
    }

    //multi
    boolean hasNetworkMulti(G net){
        return getPortOfNetworkMulti(net) != -1;
    }

    int getPortOfNetwork(G net){
        if(multi) return getPortOfNetworkMulti(net);
        return networks.get(0).id == net.id ? 0 : -1;
    }

    //multi
    int getPortOfNetworkMulti(G net){
        if(net == null) return -1;
        return networks.findKey(net, true, -1);
    }

    public boolean replaceNetwork(G old, G set){
        if(multi) return replaceNetworkMulti(old, set);
        networks.put(0, set);
        return true;
    }

    //multi
    boolean replaceNetworkMulti(G old, G set){
        int index = networks.findKey(old, true, -1);
        if(index == -1) return false;
        networks.put(index, set);
        return true;
    }

    //TODO 굳이? 싶은 함수들 여기
    public G getNetworkOfPort(int index){
        if(multi) getNetworkOfPortMulti(index);
        return networks.get(0);
    }

    //multi
    G getNetworkOfPortMulti(int index){
        int l = graph.accept[index];
        if(l == 0) return null;
        return networks.get(l - 1);
    }

    public void setNetworkOfPort(int index, G net){
        if(multi) setNetworkOfPortMulti(index, net);
        else networks.put(0, net);
    }

    //multi
    void setNetworkOfPortMulti(int index, G net){
        int l = graph.accept[index];
        if(l == 0) return;
        networks.put(l - 1, net);
    }

    abstract void writeGlobal(Writes write);

    abstract void readGlobal(Reads read, byte revision);

    abstract void writeLocal(Writes write, G graph);

    abstract <R> R[] readLocal(Reads read, byte revision);

    void write(Writes write){
        writeGlobal(write);
        if(multi) writeMulti(write);
        else writeLocal(write, networks.get(0));
    }

    //multi
    void writeMulti(Writes write){
        write.b(networks.size);
        for(var i : networks.values()) writeLocal(write, i);
    }

    void read(Reads read, byte revision){
        readGlobal(read, revision);
        saveCache.clear();
        if(multi) readMulti(read, revision);
        else{
            saveCache.add(readLocal(read, revision));
            networkSaveState = true;
        }
    }

    //multi
    void readMulti(Reads read, byte revision){
        int netAm = read.b();
        for(int i = 0; i < netAm; i++) saveCache.add(readLocal(read, revision));
        networkSaveState = true;
    }

    G getNetworkFromSet(int index){
        return networks.get(index);
    }

    boolean setNetworkFromSet(int index, G net){
        if(networks.containsKey(index) && networks.get(index).id == net.id) return false;
        networks.put(index, net);
        return true;
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

    public M graph(T graph){
        this.graph = graph;
        if(canBeMulti() && graph.isMultiConnector) multi = true;
        return (M)this;
    }

    abstract boolean canBeMulti();
}

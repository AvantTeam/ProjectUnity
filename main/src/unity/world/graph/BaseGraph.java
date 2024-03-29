package unity.world.graph;

import arc.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.world.*;
import unity.world.blocks.GraphBlockBase.*;
import unity.world.graphs.*;
import unity.world.meta.*;
import unity.world.modules.*;

import java.util.*;

public abstract class BaseGraph<M extends GraphModule<? extends Graph, M, G>, G extends BaseGraph<M, G>>{
    public final OrderedSet<M> connected = new OrderedSet<>();
    public final int id;
    private static int lastId;
    long lastFrameUpdated;
    M target;

    {
        id = lastId++;
    }

    BaseGraph(){}

    public void init(M module){
        connected.add(module);
        updateOnGraphChanged();
        addMergeStats(module);
    }

    public abstract G create(/*building*/);

    G copyGraph(M module/*building*/){
        G copygraph = create();
        copygraph.init(module);
        copygraph.copyGraphStatsFrom((G)this);
        return copygraph;
    }

    abstract void copyGraphStatsFrom(G graph);

    public void update(){
        long frameId = Core.graphics.getFrameId();
        if(frameId == lastFrameUpdated) return;
        lastFrameUpdated = frameId;
        updateDirect();
        updateGraph();
    }

    abstract void updateOnGraphChanged();

    abstract void updateGraph();

    abstract void updateDirect();

    boolean canConnect(M b1, M b2){
        return true;
    }

    void addBuilding(M module/*building*/, int connectIndex){
        connected.add(module);
        updateOnGraphChanged();
        module.setNetworkOfPort(connectIndex, (G)this);
        addMergeStats(module);
    }

    abstract void addMergeStats(M module/*building*/);

    void mergeGraph(G graph){
        if(graph == null) return;
        if(graph.connected.size > connected.size){
            graph.mergeGraph((G)this);
            return;
        }
        updateDirect();
        graph.updateDirect();
        mergeStats(graph);
        for(var module : graph.connected){
            if(!connected.contains(module) && module.replaceNetwork(graph, (G)this))
                connected.add(module);
        }
        updateOnGraphChanged();
    }

    abstract void mergeStats(G graph);

    void killGraph(){
        connected.clear();
    }

    boolean isAtriculationPoint(M module/*building*/){
        Seq<M> neighs = new Seq<>(4);
        module.eachNeighbour(n -> {
            if(module.getNetworkOfPort(n.value) == this) neighs.add(n.key);
        });
        int neighbourIndex = 1;
        if(neighbourIndex >= neighs.size) target = null;
        else target = neighs.get(neighbourIndex);
        PQueue<M> front = new PQueue<>();
        front.comparator = (a, b) -> {
            if(target == null) return 99999999;
            return a.hueristic(target.parent.build) - b.hueristic(target.parent.build);
        };
        front.add(neighs.get(0).d(0));
        int giveUp = connected.size;
        ObjectSet<M> visited = ObjectSet.with(module);
        while(!front.empty()){
            M current = front.poll();
            if(current == target){
                neighbourIndex++;
                if(neighbourIndex == neighs.size) return false;
                target = neighs.get(neighbourIndex);
                while(target == null || visited.contains(target)){
                    neighbourIndex++;
                    if(neighbourIndex == neighs.size) return false;
                    target = neighs.get(neighbourIndex);
                }
                front.comparator = Comparator.comparingInt(a -> a.hueristic(target.parent.build));
            }
            visited.add(current);
            current.eachNeighbour(n -> {
                if(visited.contains(n.key)) return;
                if(current.getNetworkOfPort(n.value) == this) front.add(n.key.d(current.d + 4));
            });
            if(--giveUp < 0) return true;
        }
        return true;
    }

    public void remove(M module/*building*/){
        if(!connected.contains(module)) return;
        int c = module.countNeighbours();
        if(c == 0) return;
        if(c == 1 || !isAtriculationPoint(module)){
            connected.remove(module);
            module.eachNeighbourKey(n -> n.removeNeighbour(module));
            updateOnGraphChanged();
            return;
        }
        killGraph();
        ObjectSet<G> networksAdded = new ObjectSet<>(c);
        module.eachNeighbour(n -> {
            G copyNet = module.getNetworkOfPort(n.value);
            M temp = n.key;
            if(copyNet == this){
                M selfref = temp.getNeighbour(module);
                if(selfref == null) return;
                temp.setNetworkOfPort(temp.portIndex(selfref), copyNet.copyGraph(temp));
            }
        });
        module.eachNeighbour(n -> {
            if(module.getNetworkOfPort(n.value) == this){
                M temp = n.key;
                M selfref = temp.getNeighbour(module);
                if(selfref == null) return;
                G neiNet = temp.getNetworkOfPort(temp.portIndex(selfref));
                if(!networksAdded.contains(neiNet)){
                    networksAdded.add(neiNet);
                    neiNet.rebuildGraphIndex(temp, temp.portIndex(selfref));
                }
            }
        });
        module.replaceNetwork((G)this, null);
    }

    public void rebuildGraph(M module/*building*/){
        rebuildGrpahWithSet(module, ObjectSet.with(module), -1);
    }

    public void rebuildGraphIndex(M module/*building*/, int index){
        rebuildGrpahWithSet(module, ObjectSet.with(module), index);
    }

    void rebuildGrpahWithSet(M root, ObjectSet<M> searched, int rootIndex){
        GraphTree tree = new GraphTree(root, rootIndex);
        GraphTree current = tree;
        int total = 0;
        mainLoop:
        while(current != null){
            total++;

            M buildConnector = current.module;
            GraphBuildBase build = buildConnector.parent.build;
            int index = current.parentConnectPort;

            if(buildConnector.graph.accept == null) return;

            Seq<GraphData> acceptPorts = buildConnector.acceptPorts;
            if(index != -1) acceptPorts = buildConnector.getConnectedNeighbours(index);
            M prevModule = null;//prevBuilding
            searched.add(buildConnector);

            for(int port = 0, len = acceptPorts.size; port < len; port++){
                GraphData portInfo = acceptPorts.get(port);
                int portIndex = portInfo.index;
                if(buildConnector.getNetworkOfPort(portIndex) == null) continue;
                if(!buildConnector.initialized()) return;
                Tile tile = build.tile().nearby(portInfo.toPos);
                if(tile == null) return;

                if(tile.build instanceof GraphBuildBase other){
                    M conModule = (M)other.getGraphConnector(root.type());//conbuild
                    if(conModule == null || conModule == prevModule || conModule.dead() || !canConnect(current.module, conModule)) continue;
                    G thisGraph = buildConnector.getNetworkOfPort(portIndex);
                    if(conModule.parent.build.rotation() != conModule.lastRecalc()) conModule.recalcPorts();
                    Point2 fPos = portInfo.fromPos.cpy();
                    fPos.x += build.tileX();
                    fPos.y += build.tileY();
                    int connectIndex = conModule.canConnect(fPos);
                    if(connectIndex == -1) continue;
                    buildConnector.addNeighbour(conModule, portIndex);
                    conModule.addNeighbour(buildConnector, connectIndex);

                    G conNet = conModule.getNetworkOfPort(connectIndex);
                    if(!thisGraph.connected.contains(conModule) && conNet != null){
                        if(!buildConnector.hasNetwork(conNet)){
                            if(conNet.connected.contains(conModule)) thisGraph.mergeGraph(conNet);
                            else thisGraph.addBuilding(conModule, connectIndex);
                            if(!searched.contains(conModule)) current.children.add(new GraphTree(current, conModule, connectIndex));//isNetworkConnector omitted.
                        }
                    }
                    prevModule = conModule;
                }
            }
            Seq<GraphTree> children = current.children;
            for(int i = 0, childLen = children.size; i < childLen; i++){
                if(!children.get(i).complete){
                    current = children.get(i);
                    continue mainLoop;
                }
            }
            current.complete = true;
            current = current.parent;
        }
    }

    String connectedToString(){
        StringBuilder s = new StringBuilder("Network:" + id + ":");
        for(var build : connected) s.append(build.parent.build.block().localizedName).append(", ");
        return s.toString();
    }

    //내가 추가한것
    class GraphTree{
        boolean complete;
        final GraphTree parent;
        final Seq<GraphTree> children = new Seq<>(4);
        final M module;//building
        final int parentConnectPort;

        GraphTree(GraphTree parent, M root, int rootIndex){
            this.parent = parent;
            module = root;
            parentConnectPort = rootIndex;
        }

        GraphTree(M root, int rootIndex){
            this(null, root, rootIndex);
        }
    }
}

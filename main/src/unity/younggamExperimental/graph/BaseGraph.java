package unity.younggamExperimental.graph;

import arc.*;
import arc.struct.*;
import mindustry.world.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.modules.*;

import java.util.*;

//BlockGraph 그래프 그자체 TODO abstract로 만들기?, build들 이름 바꾸기?
public abstract class BaseGraph{
    public final ObjectSet<GraphModule> connected = new ObjectSet<>();
    public final int id;
    private static int lastId;
    long lastFrameUpdated;
    GraphModule target;

    {
        id = lastId++;
    }

    public BaseGraph(GraphModule build){
        //TODO  init 으로 분리하기?
        connected.add(build);
        updateOnGraphChanged();
        addMergeStats(build);
    }

    //abstract 문제해결용 임의로 추가..
    abstract BaseGraph create(GraphModule build);

    BaseGraph copyGraph(GraphModule build){
        BaseGraph copygraph = create(build);
        copygraph.copyGraphStatsFrom(this);
        return copygraph;
    }

    abstract void copyGraphStatsFrom(BaseGraph graph);

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

    boolean canConnect(GraphModule b1, GraphModule b2){
        return true;
    }

    void addBuilding(GraphModule build, int connectIndex){
        connected.add(build);
        updateOnGraphChanged();
        build.setNetworkOfPort(connectIndex, this);
        addMergeStats(build);
    }

    abstract void addMergeStats(GraphModule build);

    void mergeGraph(BaseGraph graph){
        if(graph == null) return;
        if(graph.connected.size > connected.size){
            graph.mergeGraph(this);
            return;
        }
        updateDirect();
        graph.updateDirect();
        mergeStats(graph);
        graph.connected.each(build -> {
            if(!connected.contains(build) && build.replaceNetwork(graph, this))
                connected.add(build);
        });
        updateOnGraphChanged();
    }

    abstract void mergeStats(BaseGraph graph);

    void killGraph(){
        connected.clear();
    }

    boolean isAtriculationPoint(GraphModule build){
        Seq<GraphModule> neighs = new Seq<>(4);
        build.eachNeighbour(n -> {
            if(build.getNetworkOfPort(n.portIndex) == this) neighs.add(n);
        });
        int neighbourIndex = 1;
        target = neighs.get(neighbourIndex);
        PQueue<GraphModule> front = new PQueue<>();
        front.comparator = (a, b) -> {
            if(target == null) return 99999999;
            return a.hueristic(target.parent.build) - b.hueristic(target.parent.build);
        };
        front.add(neighs.get(0));
        int giveUp = connected.size;
        ObjectSet<GraphModule> visited = ObjectSet.with(build);
        while(!front.empty()){
            GraphModule current = front.poll();
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
                if(visited.contains(n)) return;
                if(current.getNetworkOfPort(n.portIndex) == this) front.add(n.d(current.d + 4));
            });
            if(--giveUp < 0) return true;
        }
        return true;
    }

    public void remove(GraphModule build){
        if(!connected.contains(build)) return;
        int c = build.countNeighbours();
        if(c == 0) return;
        if(c == 1 || !isAtriculationPoint(build)){
            connected.remove(build);
            build.eachNeighbour(n -> n.removeNeighbour(build));
            updateOnGraphChanged();
            return;
        }
        killGraph();
        ObjectSet<BaseGraph> networksAdded = new ObjectSet<>(c);
        build.eachNeighbour(n -> {
            BaseGraph copyNet = build.getNetworkOfPort(n.portIndex);
            if(copyNet == this){
                GraphModule selfref = n.getNeighbour(build);
                if(selfref == null) return;
                n.setNetworkOfPort(selfref.portIndex, copyNet.copyGraph(n));
            }
        });
        build.eachNeighbour(n -> {
            if(build.getNetworkOfPort(n.portIndex) == this){
                GraphModule selfref = n.getNeighbour(build);
                if(selfref == null) return;
                BaseGraph neiNet = n.getNetworkOfPort(selfref.portIndex);
                if(!networksAdded.contains(neiNet)){
                    networksAdded.add(neiNet);
                    neiNet.rebuildGraphIndex(n, selfref.portIndex);
                }
            }
        });
        build.replaceNetwork(this, null);
    }

    public void rebuildGraph(GraphModule build){
        rebuildGrpahWithSet(build, ObjectSet.with(build), -1);
    }

    public void rebuildGraphIndex(GraphModule build, int index){
        rebuildGrpahWithSet(build, ObjectSet.with(build), index);
    }

    void rebuildGrpahWithSet(GraphModule root, ObjectSet<GraphModule> searched, int rootIndex){
        GraphTree tree = new GraphTree(root, rootIndex);
        GraphTree current = tree;
        int total = 0;
        mainLoop:
        while(current != null){
            total++;

            GraphModule buildConnector = current.build;
            int index = current.parentConnectPort;

            if(buildConnector.parentBlock.accept == null) return;

            Seq<GraphData> acceptPorts = buildConnector.acceptPorts;
            if(index != -1) acceptPorts = buildConnector.getConnectedNeighbours(index);
            GraphModule prevBuild = null;
            searched.add(buildConnector);

            for(int port = 0, len = acceptPorts.size; port < len; port++){
                GraphData portInfo = acceptPorts.get(port);
                int portIndex = portInfo.index;
                if(buildConnector.getNetworkOfPort(portIndex) == null) continue;
                if(buildConnector.parent.build.tile == null) return;
                Tile tile = buildConnector.parent.build.tile.nearby(portInfo.toPos);
                if(tile == null) return;

                //TODO

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
    }

    String connectedToString(){
        StringBuilder s = new StringBuilder("Network:" + id + ":");
        connected.each(build -> s.append(build.parent.build.block.localizedName).append(", "));
        return s.toString();
    }

    //내가 추가한것
    public abstract GraphType type();

    class GraphTree{
        boolean complete;
        GraphTree parent;
        final Seq<GraphTree> children = new Seq<>(4);
        GraphModule build;
        int parentConnectPort;

        GraphTree(GraphModule root, int rootIndex){
            build = root;
            parentConnectPort = rootIndex;
        }
    }
}

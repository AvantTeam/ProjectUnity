package unity.younggamExperimental.graph;

import arc.*;
import arc.struct.*;
import mindustry.world.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.modules.*;

import java.util.*;

//BlockGraph 그래프 그자체 TODO abstract로 만들기?, build들 이름 바꾸기?
public class GraphBase{
    private static int lastId;
    final ObjectSet<GraphModule> connected = new ObjectSet<>();
    final int id;
    long lastFrameUpdated;
    GraphModule target;

    {
        id = lastId++;
    }

    public GraphBase(GraphModule build){
        //TODO  init 으로 분리하기?
        connected.add(build);
        updateOnGraphChanged();
        addMergeStats(build);
    }

    GraphBase copyGraph(GraphModule build){
        GraphBase copygraph = new GraphBase(build);
        copygraph.copyGraphStatsFrom(this);
        return copygraph;
    }

    void copyGraphStatsFrom(GraphBase graph){}

    public void update(){
        long frameId = Core.graphics.getFrameId();
        if(frameId == lastFrameUpdated) return;
        lastFrameUpdated = frameId;
        updateDirect();
        updateGraph();
    }

    void updateOnGraphChanged(){}

    void updateGraph(){}

    void updateDirect(){}

    boolean canConnect(GraphModule b1, GraphModule b2){
        return true;
    }

    void addBuilding(GraphModule build, int connectIndex){
        connected.add(build);
        updateOnGraphChanged();
        build.setNetworkOfPort(connectIndex, this);
        addMergeStats(build);
    }

    void addMergeStats(GraphModule build){}

    void mergeGraph(GraphBase graph){
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

    void mergeStats(GraphBase graph){}

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
            return a.hueristic(target.parentBuilding) - b.hueristic(target.parentBuilding);
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
                front.comparator = Comparator.comparingInt(a -> a.hueristic(target.parentBuilding));
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
        ObjectSet<GraphBase> networksAdded = new ObjectSet<>(c);
        build.eachNeighbour(n -> {
            GraphBase copyNet = build.getNetworkOfPort(n.portIndex);
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
                GraphBase neiNet = n.getNetworkOfPort(selfref.portIndex);
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

    void rebuildGraphIndex(GraphModule build, int index){
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
                if(buildConnector.parentBuilding.tile == null) return;
                Tile tile = buildConnector.parentBuilding.tile.nearby(portInfo.toPos);
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
        connected.each(build -> s.append(build.parentBuilding.block.localizedName).append(", "));
        return s.toString();
    }

    //내가 추가한것
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

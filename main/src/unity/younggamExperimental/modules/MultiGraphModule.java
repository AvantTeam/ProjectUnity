package unity.younggamExperimental.modules;

import arc.struct.*;
import arc.util.io.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.graph.*;

public abstract class MultiGraphModule extends GraphModule{
    final IntMap<BaseGraph> networks = new IntMap<>(4);

    MultiGraphModule(GraphModules parent, int portIndex){
        super(parent, portIndex);
    }

    //TODO 굳이싶은것들?
    int getPortOfNetwork(BaseGraph net){
        if(net == null) return -1;
        return networks.findKey(net, true, -1);
    }

    @Override
    void initAllNets(){
        recalcPorts();
        int[] portArray = parentBlock.accept;
        networks.clear();
        for(int i = 0, len = portArray.length; i < len; i++){
            if(portArray[i] != 0 && !networks.containsKey(portArray[i] - 1)) networks.put(portArray[i] - 1, newNetwork());
        }
    }

    BaseGraph getNetworkFromSet(int index){
        return networks.get(index);
    }

    boolean setNetworkFromSet(int index, BaseGraph net){
        if(networks.containsKey(index) && networks.get(index).id == net.id) return false;
        networks.put(index, net);
        return true;
    }

    @Override
    public boolean replaceNetwork(BaseGraph old, BaseGraph set){
        int index = networks.findKey(old, true, -1);
        if(index == -1) return false;
        networks.put(index, set);
        return true;
    }

    @Override
    public BaseGraph getNetworkOfPort(int index){
        int l = parentBlock.accept[index];
        if(l == 0) return null;
        return networks.get(l - 1);
    }

    @Override
    public void setNetworkOfPort(int index, BaseGraph net){
        int l = parentBlock.accept[index];
        if(l == 0) return;
        networks.put(l - 1, net);
    }

    @Override
    public Seq<GraphData> getConnectedNeighbours(int index){
        int[] portArray = parentBlock.accept;
        int targetPort = portArray[index];
        Seq<GraphData> output = new Seq<>();
        for(var i : acceptPorts){
            if(portArray[i.index] == targetPort) output.add(i);
        }
        return output;
    }

    @Override
    void updateNetworks(){
        if(networks.isEmpty()) return;
        if(needsNetworkUpdate){
            boolean[] covered = new boolean[4];
            int[] portArray = parentBlock.accept;
            for(int i = 0, len = portArray.length; i < len; i++){
                int j = portArray[i] - 1;
                if(portArray[i] == 0 || covered[j]) continue;
                getNetworkOfPort(j).rebuildGraphIndex(this, i);
                covered[j] = true;
            }
            if(networkSaveState){
                for(var i : networks) applySaveState(i.value, saveCache.get(i.key));
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

    @Override
    void deleteSelfFromNetwork(){
        dead = true;
        if(networks.isEmpty()) return;
        for(var i : networks.values()) i.remove(this);
    }

    @Override
    void write(Writes writes){
        writeGlobal(writes);
        writes.b(networks.size);
        for(var i : networks.values()) writeLocal(writes, i);
    }

    @Override
    void read(Reads reads, byte revision){
        readGlobal(reads, revision);
        int netAm = reads.b();
        for(int i = 0; i < netAm; i++) saveCache.add(readLocal(reads, revision));
        networkSaveState = true;
    }
}

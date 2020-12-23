package unity.younggamExperimental.modules;

import arc.struct.*;
import arc.util.io.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.graph.*;
import unity.younggamExperimental.graphs.*;

public abstract class MultiGraphModule<T extends Graph, M extends GraphModule<T, M, G>, G extends BaseGraph<T, M, G>> extends GraphModule<T, M, G>{

    //TODO 굳이싶은것들?

    @Override
    public boolean hasNetwork(G net){
        return getPortOfNetwork(net) != -1;
    }

    @Override
    int getPortOfNetwork(G net){
        if(net == null) return -1;
        return networks.findKey(net, true, -1);
    }

    @Override
    void initAllNets(){
        recalcPorts();
        int[] portArray = type.accept;
        networks.clear();
        for(int i = 0, len = portArray.length; i < len; i++){
            if(portArray[i] != 0 && !networks.containsKey(portArray[i] - 1)) networks.put(portArray[i] - 1, newNetwork());
        }
    }

    G getNetworkFromSet(int index){
        return networks.get(index);
    }

    boolean setNetworkFromSet(int index, G net){
        if(networks.containsKey(index) && networks.get(index).id == net.id) return false;
        networks.put(index, net);
        return true;
    }

    @Override
    public boolean replaceNetwork(G old, G set){
        int index = networks.findKey(old, true, -1);
        if(index == -1) return false;
        networks.put(index, set);
        return true;
    }

    @Override
    public G getNetworkOfPort(int index){
        int l = type.accept[index];
        if(l == 0) return null;
        return networks.get(l - 1);
    }

    @Override
    public void setNetworkOfPort(int index, G net){
        int l = type.accept[index];
        if(l == 0) return;
        networks.put(l - 1, net);
    }

    @Override
    public Seq<GraphData> getConnectedNeighbours(int index){
        int[] portArray = type.accept;
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
            int[] portArray = type.accept;
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

    @Override
    void deleteSelfFromNetwork(){
        dead = true;
        if(networks.isEmpty()) return;
        for(var i : networks.values()) i.remove((M)this);
    }

    @Override
    void write(Writes write){
        writeGlobal(write);
        write.b(networks.size);
        for(var i : networks.values()) writeLocal(write, i);
    }

    @Override
    void read(Reads read, byte revision){
        readGlobal(read, revision);
        int netAm = read.b();
        for(int i = 0; i < netAm; i++) saveCache.add(readLocal(read, revision));
        networkSaveState = true;
    }
}

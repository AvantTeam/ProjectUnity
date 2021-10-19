package unity.world.modules;

import arc.graphics.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import unity.graphics.*;
import unity.ui.*;
import unity.ui.IconBar.*;
import unity.ui.StackedBarChart.*;
import unity.util.*;
import unity.world.graph.*;
import unity.world.graphs.*;
import unity.world.meta.*;

import static arc.Core.*;

public class GraphCrucibleModule extends GraphModule<GraphCrucible, GraphCrucibleModule, CrucibleGraph>{
    public final IntMap<Seq<CrucibleData>> propsList = new IntMap<>(4);
    public float liquidCap;
    public int tilingIndex;
    final Seq<CrucibleData> contains = new Seq<>();
    int containedAmCache;
    boolean melter = true, containChanged = true;

    public boolean addItem(Item item){
        return networks.get(0).addItem(item);
    }

    public Seq<CrucibleData> getContained(){
        CrucibleGraph net = networks.get(0);
        if(net != null) return net.contains();
        return contains;
    }

    public float getVolumeContained(){
        CrucibleGraph net = networks.get(0);
        if(net != null) return net.getVolumeContained();
        else return 0f;
    }

    public boolean canContainMore(float amount){
        CrucibleGraph net = networks.get(0);
        if(net != null) return net.canContainMore(amount);
        else return false;
    }

    public float getTotalLiquidCapacity(){
        CrucibleGraph net = networks.get(0);
        if(net != null) return net.totalCapacity();
        return 0f;
    }

    public StackedBarChart getStackedBars(){
        return new StackedBarChart(200f, () -> {
            Seq<CrucibleData> cc = getContained();
            BarStat[] data;
            if(cc.isEmpty()){
                data = new BarStat[]{new BarStat(bundle.get("stat.unity.crucible.empty"), 1f, 1f, UnityPal.youngchaGray)};
            }else{
                float tv = getVolumeContained();
                int len = cc.size;
                float min = Math.min(1f / len, 0.15f);
                float remain = 1f - len * min;

                MeltInfo[] melts = MeltInfo.all;
                data = new BarStat[len];

                for(int i = 0; i < len; i++){
                    CrucibleData ccl = cc.get(i);
                    MeltInfo m = melts[ccl.id];
                    Item item = m.item;

                    if(item != null){
                        data[i] = new BarStat(
                            bundle.format("stat.unity.crucible.iteminfo", item.toString(), Strings.fixed(ccl.volume, 2)),
                            min + (remain * ccl.volume / tv),
                            ccl.meltedRatio, item.color
                        );
                    }else{
                        data[i] = new BarStat(
                            bundle.format("stat.unity.crucible.iteminfo", m.name, Strings.fixed(ccl.volume, 2)),
                            min + (remain * ccl.volume / tv),
                            ccl.meltedRatio, UnityPal.youngchaGray
                        );
                    }
                }
            }
            return data;
        });
    }

    public IconBar getIconBar(){
        return new IconBar(96f, () -> {
            float temp = 0f;
            Seq<CrucibleData> cc = getContained();
            CrucibleGraph net = networks.get(0);

            if(net != null) temp = net.getAverageTemp();

            Color tempCol = Utils.tempColor(temp);
            tempCol.mul(tempCol.a);
            tempCol.add(Color.gray);
            tempCol.a = 1f;

            int len = 0;

            if(cc != null) len = cc.size;
            IconBarStat data = new IconBarStat(temp - 273f, 500f, 0f, tempCol, len);

            if(temp < 270f){
                data.defaultMin = Math.max(-273.15f, 5f * (temp - 273f));
                data.defaultMax = Math.max(20f, 500f + 5 * (temp - 273f));
            }
            if(len > 0){
                MeltInfo[] melts = MeltInfo.all;
                for(var i : cc){
                    MeltInfo m = melts[i.id];
                    Item item = m.item;
                    if(item != null) data.push(m.meltPoint - 273f, item.fullIcon);
                }
            }
            return data;
        });
    }

    @Override
    void applySaveState(CrucibleGraph graph, int index){
        CrucibleData[] cache = (CrucibleData[])saveCache.get(index);
        int len = cache.length;
        Seq<CrucibleData> cc = graph.contains();

        if(cc.size == len) return;
        cc.clear();

        for(var i : cache) cc.add(i);
    }

    @Override
    void updateExtension(){}

    @Override
    void updateProps(CrucibleGraph graph, int index){}

    @Override
    void proximityUpdateCustom(){}

    @Override
    void display(Table table){}

    @Override
    void initStats(){
        tilingIndex = containedAmCache = 0;
        liquidCap = 0f;
        contains.clear();
        melter = true;
        propsList.clear();
    }

    @Override
    void displayBars(Table table){
        CrucibleGraph net = networks.get(0);
        if(net == null) return;
        table.add(new Bar(
            () -> bundle.get("stat.unity.liquidtotal") + ": " + Strings.fixed(net.getVolumeContained(), 1) + "/" + Strings.fixed(net.totalCapacity(), 1)
            , () -> Pal.darkishGray
            , () -> net.getVolumeContained() / net.totalCapacity()
        )).growX().row();
    }

    @Override
    CrucibleGraph newNetwork(){
        return new CrucibleGraph();
    }

    @Override
    void writeGlobal(Writes write){}

    @Override
    void readGlobal(Reads read, byte revision){}

    @Override
    void writeLocal(Writes write, CrucibleGraph graph){
        Seq<CrucibleData> cc = graph.contains();
        write.i(cc.size);
        for(var i : cc){
            write.i(i.id);
            write.f(i.meltedRatio);
            write.f(i.volume);
        }
    }

    @Override
    CrucibleData[] readLocal(Reads read, byte revision){
        int len = read.i();
        CrucibleData[] save = new CrucibleData[len];
        MeltInfo[] melts = MeltInfo.all;
        for(int i = 0; i < len; i++){
            int id = read.i();
            float mratio = read.f();
            float vol = read.f();
            save[i] = new CrucibleData(id, vol, mratio, melts[id].item);
        }
        return save;
    }

    @Override
    public GraphType type(){
        return GraphType.crucible;
    }
}

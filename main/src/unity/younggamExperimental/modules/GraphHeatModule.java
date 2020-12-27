package unity.younggamExperimental.modules;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.graph.*;
import unity.younggamExperimental.graphs.*;

//not considered to be multi
public class GraphHeatModule extends GraphModule<GraphHeat, GraphHeatModule, HeatGraph>{
    public float heat, heatBuffer;

    @Override
    void applySaveState(HeatGraph graph, int index){}

    @Override
    void updateExtension(){}

    @Override
    void updateProps(HeatGraph graph, int index){
        float temp = getTemp();
        float cond = this.graph.baseHeatConductivity;
        heatBuffer = 0f;
        float clampedDelta = Mathf.clamp(Time.delta, 0, 1f / cond);
        for(var n : neighbours.keys()) heatBuffer += (n.getTemp() - temp) * cond * clampedDelta;
        heatBuffer += (293.15f - temp) * this.graph.baseHeatRadiativity * clampedDelta;
    }

    @Override
    void proximityUpdateCustom(){}

    @Override
    void display(Table table){
        if(networks.get(0) == null) return;
        String ps = Core.bundle.get("stat.unity.temperatureUnit");
        table.row();
        table.table(sub -> {
            sub.clearChildren();
            sub.left();
            sub.label(() -> Strings.fixed(getTemp() - 273.15f, 2) + ps).color(Color.lightGray);
        }).left();
    }

    @Override
    void initStats(){
        setTemp(293.15f);
    }

    @Override
    void displayBars(Table table){}

    @Override
    void drawSelect(){
        if(networks.get(0) != null) networks.get(0).connected.each(module -> Drawf.selected(module.parent.build.<Building>self(), Pal.accent));
    }

    @Override
    HeatGraph newNetwork(){
        return new HeatGraph(this);
    }

    @Override
    void writeGlobal(Writes write){
        write.f(heat);
    }

    @Override
    void readGlobal(Reads reads, byte revision){
        heat = reads.f();
        heatBuffer = 0f;
    }

    @Override
    void writeLocal(Writes write, HeatGraph graph){}

    @Override
    Object[] readLocal(Reads read, byte revision){
        return null;
    }

    @Override
    public GraphType type(){
        return GraphType.heat;
    }

    //heat
    @Override
    public float getTemp(){
        return heat / graph.baseHeatCapacity;
    }

    @Override
    void setTemp(float t){
        heat = t * graph.baseHeatCapacity;
    }
}

package unity.younggamExperimental.modules;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.graphics.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.graph.*;
import unity.younggamExperimental.graphs.*;

public class HeatModule extends GraphModule{
    float heat, heatBuffer;


    HeatModule(GraphModules parent, int portIndex){
        super(parent, portIndex);
    }

    @Override
    void applySaveState(BaseGraph graph, Object cache){

    }

    @Override
    void updateExtension(){

    }

    @Override
    void updateProps(BaseGraph graph, int index){
        float temp = getTemp();
        float cond = ((GraphHeat)super.parentBlock).baseHeatConductivity;
        heatBuffer = 0f;
        float clampedDelta = Mathf.clamp(Time.delta, 0, 1f / cond);
        neighbours.each(n -> heatBuffer += (((HeatModule)n).getTemp() - temp) * cond * clampedDelta);
        heatBuffer += (293.15f - temp) * ((GraphHeat)parentBlock).baseHeatRadiativity * clampedDelta;
    }

    @Override
    void proximityUpdateCustom(){

    }

    @Override
    void display(Table table){
        if(network == null) return;
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

    }

    @Override
    void displayBars(Table table){

    }

    @Override
    void drawSelect(){
        if(network != null) network.connected.each(build -> Drawf.selected(build.parent.build, Pal.accent));
    }

    @Override
    BaseGraph newNetwork(){
        return null;//TODO
    }

    @Override
    void writeGlobal(Writes writes){

    }

    @Override
    void readGlobal(Reads reads, byte revision){

    }

    @Override
    void writeLocal(Writes writes, BaseGraph graph){

    }

    @Override
    <T> T[] readLocal(Reads writes, byte revision){
        return null;
    }

    @Override
    public GraphType type(){
        return GraphType.heat;
    }

    float getTemp(){
        return heat / ((GraphHeat)parentBlock).baseHeatCapacity;
    }

    void setTemp(float t){
        heat = t * ((GraphHeat)parentBlock).baseHeatCapacity;
    }
}

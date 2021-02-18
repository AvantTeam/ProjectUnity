package unity.world.modules;

import arc.graphics.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import unity.world.graph.*;
import unity.world.graphs.*;
import unity.world.meta.*;

public class GraphFluxModule extends GraphModule<GraphFlux, GraphFluxModule, FluxGraph>{
    float flux;

    @Override
    void applySaveState(FluxGraph graph, int index){}

    @Override
    void updateExtension(){}

    @Override
    void updateProps(FluxGraph graph, int index){}

    @Override
    void proximityUpdateCustom(){}

    @Override
    void display(Table table){
        var net = networks.get(0);
        if(net == null) return;
        String ps = " Wb";
        
        table.row();
        table.table(sub -> {
            sub.clearChildren();
            sub.left();
            sub.label(() -> Strings.fixed(net.flux(), 2) + ps).color(Color.lightGray);//TODO
        }).left();
    }

    @Override
    void initStats(){
        flux = graph.baseFlux;
    }

    @Override
    void displayBars(Table table){}

    @Override
    FluxGraph newNetwork(){
        return new FluxGraph();
    }

    @Override
    void writeGlobal(Writes write){}

    @Override
    void readGlobal(Reads read, byte revision){}

    @Override
    void writeLocal(Writes write, FluxGraph graph){}

    @Override
    Object[] readLocal(Reads read, byte revision){
        return null;
    }

    @Override
    public GraphType type(){
        return GraphType.flux;
    }

    public float flux(){
        return flux;
    }

    public void mulFlux(float mul){
        flux = mul * graph.baseFlux;
    }
}

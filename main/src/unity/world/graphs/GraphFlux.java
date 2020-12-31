package unity.world.graphs;

import arc.*;
import arc.scene.ui.layout.*;
import unity.world.meta.*;
import unity.world.modules.*;

public class GraphFlux extends Graph{
    public final float baseFlux;
    public final boolean fluxProducer;

    public GraphFlux(float flux, boolean producer){
        baseFlux = flux;
        fluxProducer = producer;
    }

    public GraphFlux(float flux){
        this(flux, true);
    }

    public GraphFlux(boolean producer){
        this(0f, producer);
    }

    public GraphFlux(){
        this(0f, true);
    }

    @Override
    public void setStats(Table table){
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.flux") + ":[] ").left();
        table.add(baseFlux + "Wb");
    }

    @Override
    public void setStatsExt(Table table){}

    @Override
    void drawPlace(int x, int y, int size, int rotation, boolean valid){}

    @Override
    public GraphType type(){
        return GraphType.flux;
    }

    @Override
    public GraphModule module(){
        return new GraphFluxModule().graph(this);
    }

    @Override
    boolean canBeMulti(){
        return false;
    }
}

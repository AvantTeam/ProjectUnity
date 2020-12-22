package unity.younggamExperimental.graphs;

import arc.scene.ui.layout.*;
import mindustry.graphics.*;
import unity.younggamExperimental.*;

import static arc.Core.bundle;

public class GraphHeat extends Graph{
    public final float baseHeatCapacity, baseHeatConductivity, baseHeatRadiativity;

    public GraphHeat(float baseHeatCapacity, float baseHeatConductivity, float baseHeatRadiativity){
        this.baseHeatCapacity = baseHeatCapacity;
        this.baseHeatConductivity = baseHeatConductivity;
        this.baseHeatRadiativity = baseHeatRadiativity;
    }

    public GraphHeat(){
        this(10f, 0.5f, 0.01f);
    }

    @Override
    public void setStats(Table table){
        //TODO 정리하기?
        table.row().left();
        table.add("Heat system").color(Pal.accent).fillX().row();
        table.left();
        table.add("[lightgray]" + bundle.get("stat.unity.heatCapacity") + ":[] ").left();
        table.add(baseHeatCapacity + "K J/K").row();
        table.left();
        table.add("[lightgray]" + bundle.get("stat.unity.heatConductivity") + ":[] ").left();
        table.add(baseHeatConductivity + "W/mK").row();
        table.left();
        table.add("[lightgray]" + bundle.get("stat.unity.heatRadiativity") + ":[] ").left();
        table.add(baseHeatRadiativity * 1000f + "W/K");
        setStatsExt(table);
    }

    @Override
    public void setStatsExt(Table table){}

    @Override
    void drawPlace(int x, int y, int size, int rotation, boolean valid){}

    @Override
    public GraphType type(){
        return GraphType.heat;
    }
}

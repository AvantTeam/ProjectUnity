package unity.younggamExperimental.graphs;

import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.graphics.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.modules.*;

import static arc.Core.bundle;

public class GraphCrucible extends Graph{
    public final float baseLiquidCapcity, meltSpeed;
    public final boolean doesCrafting, capacityTiling;

    public GraphCrucible(float capcity, float speed, boolean crafting, boolean tiling){
        baseLiquidCapcity = capcity;
        meltSpeed = speed;
        doesCrafting = crafting;
        capacityTiling = tiling;
    }

    public GraphCrucible(){
        this(6f, 0.8f, true, true);
    }

    @Override
    public void setStats(Table table){
        table.row().left();
        table.add("Crucible system").color(Pal.accent).fillX();
        table.row().left();
        table.add("[lightgray]" + bundle.get("stat.unity.liquidCapacity") + ":[] ").left();
        table.add(baseLiquidCapcity + " Units");
        table.row().left();
        table.add("[lightgray]" + bundle.get("stat.unity.meltSpeed") + ":[] ").left();
        table.add(Strings.fixed(meltSpeed * 100f, 0) + "%");
        setStatsExt(table);
    }

    @Override
    public void setStatsExt(Table table){}

    @Override
    void drawPlace(int x, int y, int size, int rotation, boolean valid){}

    @Override
    public GraphType type(){
        return GraphType.crucible;
    }

    @Override
    public GraphModule module(){
        return null;
    }

    @Override
    boolean canBeMulti(){
        return true;
    }
}

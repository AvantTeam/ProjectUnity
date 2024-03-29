package unity.world.graphs;

import arc.scene.ui.layout.*;
import mindustry.graphics.*;
import unity.world.meta.*;
import unity.world.modules.*;

import static arc.Core.*;

public class GraphCrucible extends Graph{
    public final float baseLiquidCapcity, meltSpeed;
    public final boolean doesCrafting;

    public GraphCrucible(float capcity, float speed, boolean crafting){
        baseLiquidCapcity = capcity;
        meltSpeed = speed;
        doesCrafting = crafting;
    }

    public GraphCrucible(float capacity, boolean crafting){
        this(capacity, 0.8f, crafting);
    }

    public GraphCrucible(){
        this(6f, 0.8f, true);
    }

    @Override
    public void setStats(Table table){
        table.row().left();
        table.add("Crucible system").color(Pal.accent).fillX();
        
        table.row().left();
        table.add("[lightgray]" + bundle.get("stat.unity.liquidcapacity") + ":[] ").left();
        table.add(baseLiquidCapcity + " Units"); 
        
        table.row().left();
        table.add("[lightgray]" + bundle.get("stat.unity.meltspeed") + ":[] ").left();
            
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
    public GraphCrucibleModule module(){
        return new GraphCrucibleModule().graph(this);
    }

    @Override
    boolean canBeMulti(){
        return true;
    }
}

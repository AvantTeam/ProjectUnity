package unity.world.graphs;

import arc.scene.ui.layout.*;
import unity.world.meta.*;
import unity.world.modules.*;

public abstract class Graph{
    public boolean isMultiConnector;
    public int[] accept;

    public Graph setAccept(int... newAccept){
        accept = newAccept;
        return this;
    }

    public Graph multi(){
        isMultiConnector = canBeMulti();
        return this;
    }

    public abstract void setStats(Table table);

    public abstract void setStatsExt(Table table);

    abstract void drawPlace(int x, int y, int size, int rotation, boolean valid);

    public abstract GraphType type();

    public abstract GraphModule module();

    abstract boolean canBeMulti();
}

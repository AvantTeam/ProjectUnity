package unity.world.graphs;

import arc.scene.ui.layout.*;
import unity.world.meta.*;
import unity.world.modules.*;

//GraphCommon 그래프계의 타입 느낌? Consume?
public abstract class Graph{
    public boolean isMultiConnector;//multi_graph_connector.
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

    //내가추가
    public abstract GraphType type();

    public abstract GraphModule module();

    abstract boolean canBeMulti();
}

package unity.younggamExperimental.graphs;

import arc.scene.ui.layout.*;
import unity.younggamExperimental.*;

//GraphCommon 그래프계의 타입 느낌? Consume?
public abstract class Graph{
    public boolean isMultiConnector;
    public int[] accept;

    public Graph setAccept(int... newAccept){
        accept = newAccept;
        return this;
    }

    public abstract void setStats(Table table);

    public abstract void setStatsExt(Table table);

    abstract void drawPlace(int x, int y, int size, int rotation, boolean valid);

    public abstract GraphType type();
}

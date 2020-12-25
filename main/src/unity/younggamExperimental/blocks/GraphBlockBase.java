package unity.younggamExperimental.blocks;

import arc.func.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.meta.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.graph.*;
import unity.younggamExperimental.graphs.*;
import unity.younggamExperimental.modules.*;

//GraphCommonBlock 에서 블럭의 인터페이스로 쓰일 예정
public interface GraphBlockBase{
    Graphs graphs();

    default void disableOgUpdate(){
        graphs().disableOgUpdate();
    }

    default void addGraph(Graph graph){
        graphs().setGraphConnectorTypes(graph);
    }

    default void setStatsExt(Stats stats){}

    interface GraphBuildBase extends Buildingc{
        GraphModules gms();

        default GraphModule getGraphConnector(GraphType type){
            return gms().getGraphConnector(type);
        }

        default GraphHeatModule heat(){
            return (GraphHeatModule)gms().getGraphConnector(GraphType.heat);
        }

        default void onGraphUpdate(){}

        default void onNeighboursChanged(){}

        default void onDelete(){}

        default void onDeletePost(){}

        default void updatePre(){}

        default void onRotationChanged(){}

        default void updatePost(){}

        default void proxUpdate(){}

        default void displayExt(Table table){}

        default void displayBarsExt(Table table){}

        default void writeExt(Writes write){}

        default void readExt(Reads read, byte revision){}
    }
}

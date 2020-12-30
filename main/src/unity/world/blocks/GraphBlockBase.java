package unity.world.blocks;

import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.meta.*;
import unity.world.meta.*;
import unity.world.graphs.*;
import unity.world.modules.*;

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
            return gms().heat();
        }

        default GraphTorqueModule<? extends GraphTorque> torque(){
            return gms().torque();
        }

        default GraphCrucibleModule crucible(){
            return gms().crucible();
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

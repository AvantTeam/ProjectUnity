package unity.younggamExperimental.modules;

import arc.scene.ui.layout.*;
import arc.util.io.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.graph.*;
import unity.younggamExperimental.graphs.*;

public class GraphCrucibleModule extends GraphModule<GraphCrucible,GraphCrucibleModule, CrucibleGraph>{

    @Override
    void applySaveState(CrucibleGraph graph, int index){

    }

    @Override
    void updateExtension(){

    }

    @Override
    void updateProps(CrucibleGraph graph, int index){

    }

    @Override
    void proximityUpdateCustom(){

    }

    @Override
    void display(Table table){

    }

    @Override
    void initStats(){

    }

    @Override
    void displayBars(Table table){

    }

    @Override
    void drawSelect(){

    }

    @Override
    CrucibleGraph newNetwork(){
        return null;
    }

    @Override
    void writeGlobal(Writes write){

    }

    @Override
    void readGlobal(Reads read, byte revision){

    }

    @Override
    void writeLocal(Writes write, CrucibleGraph graph){

    }

    @Override
    Object[] readLocal(Reads read, byte revision){
        return new Object[0];
    }

    @Override
    public GraphType type(){
        return null;
    }
}

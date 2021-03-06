package unity.world.blocks.merge;

import arc.util.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.world.graphs.*;

@MergeComp
class Graph extends Block{
    Graphs graphs = new Graphs();

    public Graph(String name){
        super(name);
    }

    public void doSomething(){
        Log.info("Did something.");
    }

    @Override
    public void init(){
        Log.info("Initialized.");
    }

    public class GraphBuild extends Building{
        @Override
        public void updateTile(){
            Log.info("Updated.");
        }
    }
}

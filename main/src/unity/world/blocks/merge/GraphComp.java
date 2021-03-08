package unity.world.blocks.merge;

import arc.util.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.gen.Graph.*;
import unity.world.graphs.*;

@MergeComp
class GraphComp extends Block{
    Graphs graphs = new Graphs();

    public GraphComp(String name){
        super(name);
    }

    public void doSomething(){
        Log.info("Did something.");
        doSomethingPrivately();
    }

    @Override
    public void init(){
        Log.info("Initialized.");
    }

    private void doSomethingPrivately(){
        Log.info("Did something in private ;)");
    }

    private static void doSomethingGloballyPrivate(GraphBuild build){
        Log.info(build.toString() + " just did something in private ;)");
    }

    public class GraphBuildComp extends Building{
        @ReadOnly float progress = 0f;

        @Override
        public void updateTile(){
            Log.info("Updated.");
            doSomething();
        }

        public void doSomething(){
            Log.info("Did something too.");
            doSomethingGloballyPrivate(self());
        }
    }
}

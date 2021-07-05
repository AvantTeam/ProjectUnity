package unity.world.modules;

import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.modules.*;
import unity.world.blocks.units.ModularConstructor.*;
import unity.world.blocks.units.ModularConstructorPart.*;

public class ModularConstructorModule extends BlockModule{
    public ModularConstructorGraph graph;
    boolean main = false;

    public ModularConstructorModule(){

    }

    public ModularConstructorModule(ModularConstructorBuild build){
        graph = new ModularConstructorGraph();
        graph.main = build;
        main = true;
    }

    public void update(){
        if(graph != null) graph.update();
    }

    @Override
    public void write(Writes write){
    }

    @Override
    public void read(Reads read){
        if(main && graph != null && graph.main != null){
            graph.queueAdded = true;
        }
    }

    public interface ModularConstructorModuleInterface extends Buildingc{
        ModularConstructorModule consModule();

        boolean consConnected(Building other);
    }

    public static class ModularConstructorGraph{
        public Seq<ModularConstructorPartBuild> all = new Seq<>(), toRemove = new Seq<>();
        public IntSet toRemoveSet = new IntSet(), tmp = new IntSet();
        public float tier = 0f;
        public ModularConstructorBuild main;
        public boolean queueAdded = false;

        public void added(ModularConstructorBuild b){
            all.clear();
            b.updateProximity();
            for(Building other : b.proximity){
                if(other instanceof ModularConstructorPartBuild mod && b.consConnected(other) && tmp.add(other.pos())){
                    mod.module.graph = this;
                    all.add(mod);
                }
            }
            for(ModularConstructorPartBuild mod : all){
                Building other = mod.back();
                if(other instanceof ModularConstructorPartBuild modb && mod.consConnected(modb) && tmp.add(other.pos())){
                    modb.module.graph = this;
                    modb.front = mod;
                    mod.back = modb;
                    all.add(modb);
                }
            }
            tmp.clear();
        }

        public void remove(ModularConstructorPartBuild build){
            if(toRemoveSet.add(build.pos())){
                toRemove.add(build);
                build.module.graph = null;
            }
        }

        void update(){
            if(queueAdded && main != null){
                added(main);
                queueAdded = false;
            }
            for(ModularConstructorPartBuild build : all){
                if(!build.added) build.removePart();
            }
            all.removeAll(toRemove);
            toRemove.clear();
            toRemoveSet.clear();
            tier = all.size / (float)main.moduleConnections();
            main.tier = all.size / main.moduleConnections();
        }
    }
}

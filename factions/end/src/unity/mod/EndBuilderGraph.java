package unity.mod;

import arc.math.geom.*;
import arc.struct.*;
import arc.util.pooling.Pool.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import unity.util.AdvanceQuadTree.*;
import unity.world.*;

public class EndBuilderGraph{
    private final static Seq<Building> tmpConnections = new Seq<>();
    private final static IntSet tmpSet = new IntSet(51), graphIntSet = new IntSet();
    private final static Queue<Building> queue = new Queue<>();
    private static int ids = 1;

    public Seq<Building> cores = new Seq<>(), buildings = new Seq<>();
    public IntSet buildingSet = new IntSet(50);
    int id = ids++;

    public void addGraph(EndBuilderGraph graph){
        if(graph == this) return;
        if(graph.buildings.size > buildings.size){
            graph.addGraph(this);
            return;
        }
        for(Building b : graph.buildings){
            add(b);
        }
    }

    public void add(Building b){
        if(!buildingSet.add(b.pos())) return;
        buildings.add(b);
        if(b instanceof CoreBuild){
            cores.add(b);
        }
        ((EndBuilderBuilding)b).builderMod().graph = this;
        ((EndBuilderBuilding)b).builderMod().init = true;
        EndBuilders.builders.data(b.team.id).addBuilding(b);
    }

    public void activate(){
        for(Building b : buildings){
            EndBuilders.builders.data(b.team.id).addBuilding(b);
        }
    }

    public void deactivate(){
        for(Building b : buildings){
            EndBuilders.builders.data(b.team.id).removeBuilding(b);
        }
    }

    public void remove(Building b){
        buildingSet.remove(b.pos());
        buildings.remove(b);
        if(b instanceof CoreBuild){
            cores.remove(b);
        }
        EndBuilderBuilding as = ((EndBuilderBuilding)b);
        EndBuilderModule mod = as.builderMod();
        EndBuilders.builders.data(b.team.id).removeBuilding(b);
        mod.graph = new EndBuilderGraph();

        tmpConnections.clear();
        graphIntSet.clear();
        for(int i = 0; i < mod.links.size; i++){
            //Building other = mod.linkBuild[i];
            Building other = Vars.world.build(mod.links.get(i));
            if(other != null){
                ((EndBuilderBuilding)other).builderMod().links.removeValue(b.pos());
                tmpConnections.add(other);
            }
            //as.builderMod().linkBuild[i] = null;
        }
        mod.links.clear();
        for(Building con : tmpConnections){
            if(!graphIntSet.contains(((EndBuilderBuilding)con).builderMod().graph.id)){
                EndBuilderGraph g = new EndBuilderGraph();
                g.reflow(con);
                graphIntSet.add(g.id);
            }
        }
        graphIntSet.clear();
    }

    public void reflow(Building b){
        tmpSet.clear();
        tmpSet.add(b.pos());
        queue.addLast(b);
        while(queue.size > 0){
            Building e = queue.removeFirst();
            EndBuilderBuilding as = (EndBuilderBuilding)e;
            add(e);
            IntSeq links = as.builderMod().links;
            for(int i = 0; i < links.size; i++){
                Building e2 = Vars.world.build(links.get(i));
                if(e2 != null && tmpSet.add(e2.pos())){
                    queue.addLast(e2);
                }
            }
        }
    }

    public static class EndBuilder extends AdvanceQuadTreeObject<EndBuilder> implements Poolable{
        public Building b;
        public float range;

        @Override
        public void reset(){
            b = null;
            tree = null;
            range = 0;
        }

        public boolean within(float x, float y, float rad){
            return b.within(x, y, range + rad);
        }

        @Override
        public void hitbox(Rect out){
            out.setCentered(b.x, b.y, range * 2 + 8f);
        }
    }
}

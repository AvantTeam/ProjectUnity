package unity.world;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.modules.*;
import unity.mod.*;

public class EndBuilderModule extends BlockModule{
    public EndBuilderGraph graph = new EndBuilderGraph();
    public IntSeq links = new IntSeq(4);
    public IntMap<BuildPlan> planMap = new IntMap<>();
    public boolean init;
    public float efficiency = 1f;

    final static IntSet buildingSet = new IntSet(4), buildingSet2 = new IntSet(4);
    final static Seq<Building> buildingSeq = new Seq<>(4);

    @Override
    public void write(Writes write){
        write.f(efficiency);
        write.b(links.size);
        for(int i = 0; i < links.size; i++){
            write.i(links.get(i));
        }
    }

    @Override
    public void read(Reads read){
        efficiency = read.f();
        int l = read.b();
        for(int i = 0; i < l; i++){
            links.add(read.i());
        }
    }

    public void updateLength(Building b){
        float r = -1f;
        efficiency = 1f;
        for(int i = 0; i < links.size; i++){
            Building o = Vars.world.build(links.get(i));
            if(o != null){
                int s1 = (b.block.size / 2) + 1, s2 = (o.block.size / 2);
                if(r < 0) r = 99999f;
                r = Math.max(Math.min(r, b.dst(o) - (s1 + s2) * Vars.tilesize), 0f);
            }
        }
        if(r >= 0){
            efficiency = EndBuilders.getEfficiency((int)r / Vars.tilesize, ((EndBuilderBuilding)b).tileRange());
        }
    }

    public void findConnections(Building b){
        EndBuilderBuilding eb = (EndBuilderBuilding)b;
        Tile t = b.tile;

        buildingSet.clear();
        buildingSet2.clear();
        buildingSeq.clear();
        for(int i = 0; i < links.size; i++){
            Building o = Vars.world.build(links.get(i));
            if(o != null){
                buildingSet.add(o.pos());
                buildingSeq.add(o);
            }
        }
        links.clear();
        for(int i = 0; i < 4; i++){
            var dir = Geometry.d4[i];
            Building next = null;
            int offset = b.block.size / 2;

            for(int j = 1 + offset; j <= eb.tileRange() + offset; j++){
                Building other = Vars.world.build(t.x + j * dir.x, t.y + j * dir.y);
                if(other instanceof EndBuilderBuilding && other.isAdded() && (other.x == b.x || other.y == b.y)){
                    next = other;
                    buildingSet2.add(other.pos());
                    break;
                }
            }

            if(next != null){
                EndBuilderBuilding as = next.as();
                int d = getDirection(next, b);
                int nextPos = next.pos();
                links.addUnique(nextPos);
                if(!buildingSet.contains(next.pos())){
                    IntSeq iseq = as.builderMod().links;
                    for(int j = 0; j < iseq.size; j++){
                        Building other = Vars.world.build(iseq.get(j));
                        if(other != null && getDirection(next, other) == d){
                            EndBuilderBuilding as2 = other.as();
                            as2.builderMod().links.removeValue(nextPos);
                            as.builderMod().links.removeValue(other.pos());
                            as2.builderMod().updateLength(other);
                        }
                    }
                    if(!as.builderMod().init){
                        as.builderMod().graph.reflow(next);
                    }
                    as.builderMod().links.addUnique(b.pos());
                    graph.addGraph(as.builderMod().graph);
                }
                as.builderMod().updateLength(next);
            }
        }
        buildingSet.clear();
        for(Building prev : buildingSeq){
            if(!buildingSet2.contains(prev.pos())){
                EndBuilderBuilding as = prev.as();
                as.builderMod().links.removeValue(b.pos());
                as.builderMod().updateLength(prev);

                if(!buildingSet.contains(as.builderMod().graph.id)){
                    EndBuilderGraph newG = new EndBuilderGraph();
                    newG.reflow(prev);
                    buildingSet.add(newG.id);
                }
            }
        }
        updateLength(b);
    }

    static int getDirection(Building b, Building b2){
        if(Mathf.equal(b.y, b2.y, Math.abs(b2.x - b.x))){
            return b2.x > b.x ? 0 : 2;
        }
        return b2.y > b.y ? 3 : 1;
    }
}

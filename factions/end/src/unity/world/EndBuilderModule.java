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

import java.util.*;

public class EndBuilderModule extends BlockModule{
    public EndBuilderGraph graph = new EndBuilderGraph();
    public IntSeq links = new IntSeq(4);
    public IntMap<BuildPlan> planMap = new IntMap<>();
    public boolean init;
    public float efficiency = 1f;

    final static Building[] tmpBuilding = new Building[4];

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
                r = Math.min(r, b.dst(o) - (s1 + s2) * Vars.tilesize);
            }
        }
        if(r > 0){
            efficiency = EndBuilders.getEfficiency((int)r / Vars.tilesize, ((EndBuilderBuilding)b).tileRange());
        }
    }

    public void findConnections(Building b){
        EndBuilderBuilding eb = (EndBuilderBuilding)b;
        Tile t = b.tile;

        Arrays.fill(tmpBuilding, null);
        for(int i = 0; i < links.size; i++){
            Building o = Vars.world.build(links.get(i));
            if(o != null){
                tmpBuilding[getDirection(b, o)] = o;
            }
        }
        links.clear();
        for(int i = 0; i < 4; i++){
            var dir = Geometry.d4[i];
            Building prev = tmpBuilding[i], next = null;
            int offset = b.block.size / 2;

            for(int j = 1 + offset; j <= eb.tileRange() + offset; j++){
                Building other = Vars.world.build(t.x + j * dir.x, t.y + j * dir.y);
                if(other instanceof EndBuilderBuilding && (other.x == b.x || other.y == b.y)){
                    next = other;
                    break;
                }
            }

            if(next != prev){
                if(prev != null){
                    links.removeValue(prev.pos());
                    EndBuilderBuilding as = prev.as();
                    as.builderMod().links.removeValue(b.pos());
                    as.builderMod().updateLength(prev);

                    EndBuilderGraph newG = new EndBuilderGraph();
                    newG.reflow(b);
                    if(as.builderMod().graph != newG){
                        EndBuilderGraph og = new EndBuilderGraph();
                        og.reflow(prev);
                    }
                }
                if(next != null){
                    EndBuilderBuilding as = next.as();
                    if(!as.builderMod().init){
                        as.builderMod().graph.reflow(next);
                    }
                    links.addUnique(next.pos());
                    as.builderMod().links.addUnique(b.pos());
                    as.builderMod().updateLength(next);
                    graph.addGraph(as.builderMod().graph);
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

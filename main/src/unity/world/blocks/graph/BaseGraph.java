package unity.world.blocks.graph;

import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.struct.ObjectIntMap.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import unity.annotations.Annotations.*;
import unity.util.*;
import unity.world.blocks.graphs.*;

@GraphComp(base = true)
abstract class BaseGraph extends Block{
    boolean networkConnector = true;

    protected BaseGraph(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.abilities, this::setStats);
    }

    protected void setStats(Table table){}

    public abstract class BaseGraphBuild extends Building{
        int prevRotation = -1;
        int lastRecalc = -1;

        int[] accepts = new int[block.size * 4];
        ObjectIntMap<Point2[]> acceptPorts = new ObjectIntMap<>();

        Graphs graphs = new Graphs();

        /** Always call {@link Pools.free(pos)} after using this method */
        public SidePos getConnectSidePos(int index){
            return Utils.getConnectSidePos(index, block.size, Mathf.round(rotdeg() / 90f));
        }

        @Override
        public void onRemoved(){
            updateGraphRemoval();

            delete();
            super.onRemoved();
            deletePost();
        }

        public void updateGraphRemoval(){}

        public void delete(){}

        public void deletePost(){}

        @Override
        public void updateTile(){
            super.updateTile();
            updatePre();

            if(!block.rotate){
                rotation = 0;
            }

            if(prevRotation != Math.floor(rotdeg() / 90f)){
                onRotationChanged(prevRotation, Mathf.floor(rotdeg() / 90f));
            }

            updatePost();
            prevRotation = Mathf.floor(rotdeg() / 90f);
        }

        public void onRotationChanged(int before, int after){}

        public void updatePre(){}

        public void updatePost(){}

        @Override
        public Building create(Block block, Team team){
            super.create(block, team);

            prevRotation = -1;
            lastRecalc = -1;

            return this;
        }

        protected void initNets(){
            recalculatePorts();
        }

        protected void recalculatePorts(){
            if(lastRecalc == Mathf.floor(rotdeg() / 90f)){
                return;
            }

            acceptPorts.clear();
            for(int index = 0; index < block.size * 4; index++){
                if(accepts[index] != 0){
                    SidePos out = getConnectSidePos(index);
                    acceptPorts.put(new Point2[]{out.from, out.to}, index);
                }
            }

            lastRecalc = Mathf.floor(rotdeg() / 90f);
        }

        public int canConnect(Point2 pos){
            for(Entry<Point2[]> ports : acceptPorts){
                Point2 to = ports.key[1];
                if(to.x + tile.x == pos.x && to.y + tile.y == pos.y) {
                    return ports.value;
                }
            }

            return -1;
        }
    }
}

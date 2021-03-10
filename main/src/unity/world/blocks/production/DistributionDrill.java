package unity.world.blocks.production;

import arc.struct.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.production.*;

public class DistributionDrill extends Drill{
    protected int timerDumpAlt = timers++;

    public DistributionDrill(String name){
        super(name);
    }

    public class DistributionDrillBuild extends DrillBuild{
        protected Seq<Building> invalidBuildings = new Seq<>();
        protected boolean canDistribute = true;

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public boolean canDump(Building to, Item item){
            if(to instanceof DistributionDrillBuild b){
                return !b.invalidBuildings.contains(to) && canDistribute;
            }
            return super.canDump(to, item);
        }

        @Override
        public void handleItem(Building source, Item item){
            if(source instanceof DistributionDrillBuild) invalidBuildings.add(source);
            super.handleItem(source, item);
        }

        protected void canDistribute(){
            for(int i = 0; i < proximity.size; i++){
                Building other = proximity.get((i + cdump) % proximity.size);
                if(!(other instanceof DistributionDrillBuild) && other.acceptItem(this, dominantItem)){
                    canDistribute = false;
                    return;
                }
            }
        }

        @Override
        public void updateTile(){
            if(dominantItem != null) canDistribute();
            if(timer.get(timerDumpAlt, dumpTime)) dump();
            super.updateTile();
            invalidBuildings.clear();
            canDistribute = true;
        }
    }
}

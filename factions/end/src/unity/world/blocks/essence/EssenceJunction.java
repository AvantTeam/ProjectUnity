package unity.world.blocks.essence;

import mindustry.gen.*;
import unity.mod.*;

public class EssenceJunction extends EndEssenceBlock{
    public EssenceJunction(String name){
        super(name);
        underBullets = true;
        solid = false;
        noUpdateDisabled = true;
        canOverdrive = false;
        essenceCapacity = 0.1f;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(UnityStats.essenceCapacity);
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("essence");
    }

    public class EssenceJunctionBuild extends EndEssenceBuilding{
        @Override
        public EndEssenceInterface getEssencesDestination(EndEssenceInterface source){
            if(!enabled) return this;

            int dir = ((Building)source).relativeTo(tile.x, tile.y);
            dir = (dir + 4) % 4;
            Building next = nearby(dir);
            if(!(next instanceof EndEssenceInterface e) || (!e.acceptEssence(this) && !(next instanceof EssenceJunctionBuild))){
                return this;
            }

            return ((EndEssenceInterface)next).getEssencesDestination(this);
        }

        @Override
        public float efract(){
            return 1f;
        }
    }
}

package unity.world.blocks.essence;

import arc.graphics.g2d.*;
import mindustry.gen.*;
import unity.graphics.*;
import unity.mod.*;

public class EndConcetratorBlock extends EndEssenceBlock{
    float absorbAmount = 30f;

    public EndConcetratorBlock(String name){
        super(name);
        essenceCapacity = 40f;
        //flowSpeed = 1f;
        //range = 100f;

        solid = true;
        update = true;
    }
    
    @Override
    public void setStats(){
        super.setStats();
        stats.add(UnityStats.essenceAbsorb, absorbAmount);
    }

    public class EndConcetratorBuilding extends EndEssenceBuilding2{
        public float absorbAmount(){
            return Math.min(absorbAmount, essenceCapacity - module.essence);
        }

        @Override
        public void updateTile(){
            dumpEssence(3f);
        }

        @Override
        public boolean acceptEssence(Building source){
            return false;
        }

        @Override
        public void placed(){
            super.placed();
            if(EndBuilders.essence != null) EndBuilders.essence.addSink(this);
        }

        @Override
        public void remove(){
            super.remove();
            if(EndBuilders.essence != null) EndBuilders.essence.removeSink(this);
        }

        @Override
        public void draw(){
            super.draw();
            Draw.color(EndPal.endMid);
            Fill.circle(x, y, efract() * 4f);
        }
    }
}

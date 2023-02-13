package unity.world.blocks;

import unity.mod.*;
import unity.world.blocks.essence.*;

public class EndConcetratorBlock extends EndEssenceBlock{
    float absorbAmount = 2f;

    public EndConcetratorBlock(String name){
        super(name);
        essenceCapacity = 60f;
        flowSpeed = 1f;
        range = 100f;

        solid = true;
        update = true;
    }
    
    @Override
    protected boolean isOutputBlock(){
        return true;
    }

    public class EndConcetratorBuilding extends EndEssenceBuilding{
        public float absorbAmount(){
            return Math.min(absorbAmount, essenceCapacity - module.essence);
        }
        
        @Override
        public boolean isOutput(){
            return true;
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
    }
}

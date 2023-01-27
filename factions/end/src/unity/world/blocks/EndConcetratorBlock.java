package unity.world.blocks;

import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.graphics.*;
import unity.mod.*;

public class EndConcetratorBlock extends Block{
    float maxAmount = 30f;
    float absorbAmount = 0.5f;

    public EndConcetratorBlock(String name){
        super(name);
        solid = true;
        update = true;
    }

    public class EndConcetratorBuilding extends Building{
        public float essence;

        public float absorbAmount(){
            return Math.min(absorbAmount, maxAmount - essence);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            essence = Math.min(essence, maxAmount);
        }

        @Override
        public void draw(){
            super.draw();
            Draw.color(EndPal.endMid);
            Fill.circle(x, y, (essence / maxAmount) * 4f);
            Draw.color();
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

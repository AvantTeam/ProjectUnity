package unity.world.blocks.production;

import arc.graphics.g2d.*;
import unity.graphics.*;
import unity.world.blocks.*;

public class HoldingCrucible extends GraphBlock{
    public HoldingCrucible(String name){
        super(name);
        
        solid = true;
    }

    public class HoldingCrucibleBuild extends GraphBuild{
        @Override
        public void draw(){
            Draw.rect(region, x, y);
            drawContents();
            
            UnityDrawf.drawHeat(heatRegion, x, y, 0f, heat().getTemp());
            drawTeamTop();
        }

        void drawContents(){
            var crucGraph = crucible();
            
            if(crucGraph.getVolumeContained() > 0f){
                Draw.color(crucGraph.getNetwork().color);
                Draw.rect(liquidRegion, x, y);
            }
            
            Draw.color();
        }
    }
}

package unity.younggamExperimental.blocks;

import arc.graphics.g2d.*;
import unity.graphics.*;

import static arc.Core.atlas;

public class HeatSource extends HeatGenerator{
    protected boolean isVoid;
    TextureRegion baseRegion;//bottom

    public HeatSource(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        baseRegion = atlas.find(name + "-base");
    }

    public class HeatSourceBuild extends HeatGeneratorBuild{
        @Override
        public void updatePost(){
            if(isVoid) heat().heat = 0f;
            else generateHeat(1f);
        }

        @Override
        public void draw(){
            float temp = heat().getTemp();
            Draw.rect(baseRegion, x, y);
            UnityDrawf.drawHeat(heatRegion, x, y, rotdeg(), temp);
            drawTeamTop();
        }
    }
}

package unity.world.blocks.sandbox;

import arc.graphics.g2d.*;
import unity.graphics.*;
import unity.world.blocks.power.*;

import static arc.Core.*;

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

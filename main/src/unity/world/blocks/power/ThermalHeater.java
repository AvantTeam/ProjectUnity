package unity.world.blocks.power;

import arc.graphics.g2d.*;
import mindustry.game.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import unity.graphics.*;
import unity.world.blocks.power.*;

import static arc.Core.atlas;

public class ThermalHeater extends HeatGenerator{
    final TextureRegion[] regions = new TextureRegion[4];//bottom
    final Attribute attri = Attribute.heat;

    public ThermalHeater(String name){
        super(name);
        rotate = true;
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 4; i++) regions[i] = atlas.find(name + (i + 1));
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team){
        return tile.getLinkedTilesAs(this, tempTiles).sumf(other -> other.floor().attributes.get(attri)) > 0.01f;
    }

    public class ThermalHeaterBuild extends HeatGeneratorBuild{
        public float sum;

        @Override
        public void updatePost(){
            generateHeat(sum + attri.env());
        }

        @Override
        public void draw(){
            Draw.rect(regions[rotation], x, y);
            UnityDrawf.drawHeat(heatRegion, x, y, rotdeg(), heat().getTemp());
            drawTeamTop();
        }

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();
            sum = sumAttribute(attri, tileX(), tileY());
        }
    }
}

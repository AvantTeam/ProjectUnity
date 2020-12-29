package unity.world.blocks.power;

import unity.world.blocks.*;
import unity.world.modules.*;

public class HeatGenerator extends GraphBlock{
    protected float maxTemp = 9999f, mulCoeff = 0.5f;

    public HeatGenerator(String name){
        super(name);
    }

    public class HeatGeneratorBuild extends GraphBuild{
        protected void generateHeat(float mul){
            GraphHeatModule hgraph = heat();
            hgraph.heat += Math.max(0f, maxTemp - hgraph.getTemp()) * mulCoeff * mul;
        }

        protected void generateHeat(float limit, float mul){
            GraphHeatModule hgraph = heat();
            hgraph.heat += Math.min(limit, Math.max(0f, maxTemp - hgraph.getTemp()) * mulCoeff * mul);
        }
    }
}

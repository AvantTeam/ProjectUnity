package unity.younggamExperimental.blocks;

import unity.younggamExperimental.modules.*;

public class HeatGenerator extends GraphBlock{
    protected float maxTemp = 9999f, mulCoeff = 0.5f;

    public HeatGenerator(String name){
        super(name);
        rotate = true;
    }

    public class HeatGeneratorBuild extends GraphBuild{
        protected void generateHeat(float mul){
            GraphHeatModule hgraph = heat();
            hgraph.heat += Math.max(0f, maxTemp - hgraph.getTemp()) * mulCoeff * mul;
        }
    }
}

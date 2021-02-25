package unity.world.blocks.graph;

import unity.annotations.Annotations.*;

@GraphComp
class HeatGraph extends BaseGraph{
    /** Heat energy needed to raise temp by one degree */
    float heatCapacity = 10f;
    /** Measure of how well it transfers heat  */
	float heatConductivity = 0.5f;
    /** Amount of heat lost to entropy per update */
	float heatRadiativity = 0.01f;
    /** Maximum temperature before damage, in Kelvin */
	float maxTemperature = 1573.15f;

    protected HeatGraph(String name){
        super(name);
    }
}

package unity.world.blocks.graphs;

public enum GraphType{
    heat("heat"),
    torque("torque"),
    crucible("crucible"),
    flux("flux");

    public static final GraphType[] all = values();

    public final String name;

    GraphType(String name){
        this.name = name;
    }
}

package unity.world.blocks.graphs;

public abstract class Graph<T extends Graph<T>> implements Cloneable{
    private static int lastId = 0;

    public final int id;
    public final GraphType type;

    protected Graph(GraphType type){
        this.type = type;
        id = lastId;
    }

    @SuppressWarnings("unchecked")
    public T copy(){
        try{
            return (T)clone();
        }catch(CloneNotSupportedException e){
            throw new RuntimeException(e);
        }
    }

    public void update(){
        
    }
}

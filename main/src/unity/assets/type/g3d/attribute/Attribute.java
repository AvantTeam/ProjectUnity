package unity.assets.type.g3d.attribute;

import arc.struct.*;

/** @author Xoppa */
public abstract class Attribute implements Comparable<Attribute>{
    /** The registered type aliases */
    private final static Seq<String> types = new Seq<>();

    /** The type of this attribute */
    public final long type;
    private final int typeBit;

    protected Attribute(long type){
        this.type = type;
        this.typeBit = Long.numberOfTrailingZeros(type);
    }

    /** @return The ID of the specified attribute type, or zero if not available */
    public static long getAttributeType(String alias){
        for(int i = 0; i < types.size; i++){
            if(types.get(i).compareTo(alias) == 0){
                return 1L << i;
            }
        }

        return 0;
    }

    /**
     * @return The blendAlias of the specified attribute type, or null if not available.
     */
    public static String getAttributeAlias(long type){
        int idx = -1;
        while(type != 0 && idx < 63 && (((type >> idx) & 1) == 0)){
            idx++;
        }

        return (idx >= 0 && idx < types.size) ? types.get(idx) : null;
    }

    protected static long register(String alias){
        long result = getAttributeType(alias);
        if(result > 0) return result;

        types.add(alias);
        return 1L << (types.size - 1);
    }

    /** @return An exact copy of this attribute */
    public abstract Attribute copy();

    @Override
    public boolean equals(Object obj){
        if(obj == null) return false;
        if(obj == this) return true;
        if(!(obj instanceof Attribute other)) return false;
        if(this.type != other.type) return false;

        return equals(other);
    }

    public boolean equals(Attribute other){
        return other.hashCode() == hashCode();
    }

    @Override
    public String toString(){
        return getAttributeAlias(type);
    }

    @Override
    public int hashCode(){
        return 7489 * typeBit;
    }
}

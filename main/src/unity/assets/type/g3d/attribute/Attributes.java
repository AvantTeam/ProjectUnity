package unity.assets.type.g3d.attribute;

import arc.struct.*;

import java.util.*;

/** @author Xoppa */
@SuppressWarnings("unchecked")
public class Attributes implements Iterable<Attribute>, Comparator<Attribute>, Comparable<Attributes>{
    protected long mask;
    protected final Seq<Attribute> attributes = new Seq<>();

    protected boolean sorted = true;

    public void sort(){
        if(!sorted){
            attributes.sort(this);
            sorted = true;
        }
    }

    public long mask(){
        return mask;
    }

    public <T extends Attribute> T get(long type){
        if(has(type)){
            for(int i = 0; i < attributes.size; i++){
                if(attributes.get(i).type == type){
                    return (T)attributes.get(i);
                }
            }
        }

        return null;
    }

    public Seq<Attribute> get(Seq<Attribute> out, long type){
        for(int i = 0; i < attributes.size; i++){
            if((attributes.get(i).type & type) != 0){
                out.add(attributes.get(i));
            }
        }

        return out;
    }

    /** Removes all attributes */
    public void clear(){
        mask = 0;
        attributes.clear();
    }

    /** @return The amount of attributes this material contains. */
    public int size(){
        return attributes.size;
    }

    private void enable(long mask){
        this.mask |= mask;
    }

    private void disable(long mask){
        this.mask &= ~mask;
    }

    public void set(Attribute attribute){
        int idx = indexOf(attribute.type);
        if(idx < 0){
            enable(attribute.type);
            attributes.add(attribute);
            sorted = false;
        }else{
            attributes.set(idx, attribute);
        }

        sort();
    }

    public void set(Attribute... attributes){
        for(Attribute attr : attributes){
            set(attr);
        }
    }

    public void set(Iterable<Attribute> attributes){
        for(Attribute attr : attributes){
            set(attr);
        }
    }

    public void remove(long mask){
        for(int i = attributes.size - 1; i >= 0; i--){
            long type = attributes.get(i).type;
            if((mask & type) == type){
                attributes.remove(i);
                disable(type);
                sorted = false;
            }
        }

        sort();
    }

    public boolean has(long type){
        return type != 0 && (mask & type) == type;
    }

    /** @return the index of the attribute with the specified type or -1 if not available. */
    protected int indexOf(long type){
        if(has(type)){
            for(int i = 0; i < attributes.size; i++){
                if(attributes.get(i).type == type){
                    return i;
                }
            }
        }

        return -1;
    }

    public boolean same(Attributes other){
        return same(other, false);
    }

    /**
     * Check if this collection has the same attributes as the other collection. If compareValues is true, it also 
     * compares the
     * values of each attribute.
     *
     * @param compare True to compare attribute values, false to only compare attribute types
     * @return True if this collection contains the same attributes (and optionally attribute values) as the other.
     */
    public boolean same(Attributes other, boolean compare){
        if(other == this) return true;
        if((other == null) || (mask != other.mask)) return false;
        if(!compare) return true;

        sort();
        other.sort();

        for(int i = 0; i < attributes.size; i++){
            if(!attributes.get(i).equals(other.attributes.get(i))){
                return false;
            }
        }

        return true;
    }

    /** Used for sorting attributes by type, not by value */
    @Override
    public int compare(Attribute attr1, Attribute attr2){
        return (int)(attr1.type - attr2.type);
    }

    @Override
    public Iterator<Attribute> iterator(){
        return attributes.iterator();
    }

    /**
     * @return A hash code based on only the attribute values, which might be different compared to
     * {@link #hashCode()} because the latter might include other properties as well, i.e. the material
     * id.
     */
    public int attributesHash(){
        sort();

        int n = attributes.size;
        long result = 71 + mask;
        int m = 1;
        for(int i = 0; i < n; i++){
            result += mask * attributes.get(i).hashCode() * (m = (m * 7) & 0xFFFF);
        }

        return (int) (result ^ (result >> 32));
    }

    @Override
    public int hashCode(){
        return attributesHash();
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof Attributes attr)) return false;
        if(other == this) return true;

        return same(attr, true);
    }

    @Override
    public int compareTo(Attributes other){
        if(other == this) return 0;
        if(mask != other.mask) return mask < other.mask ? -1 : 1;

        sort();
        other.sort();

        for(int i = 0; i < attributes.size; i++){
            int c = attributes.get(i).compareTo(other.attributes.get(i));
            if(c != 0){
                return Integer.compare(c, 0);
            }
        }

        return 0;
    }
}

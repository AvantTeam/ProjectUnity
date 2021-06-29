package unity.assets.type.g3d.attribute;

import arc.struct.*;

/** @author Xoppa */
public class Material extends Attributes{
    private static int counter = 0;

    public String id;

    public Material(){
        this("mtl" + ++counter);
    }

    public Material(String id){
        this.id = id;
    }

    public Material(Attribute... attributes){
        set(attributes);
    }

    public Material(String id, Attribute... attributes){
        this(id);
        set(attributes);
    }

    public Material(Seq<Attribute> attributes){
        set(attributes);
    }

    public Material(String id, Seq<Attribute> attributes){
        this(id);
        set(attributes);
    }

    public Material(Material ref){
        this(ref.id, ref);
    }

    public Material(String id, Material ref){
        this(id);
        for(Attribute attr : ref){
            set(attr.copy());
        }
    }

    public Material copy(){
        return new Material(this);
    }

    @Override
    public int hashCode(){
        return super.hashCode() + 3 * id.hashCode();
    }

    @Override
    public boolean equals(Object other){
        return other instanceof Material mat && (other == this || (mat.id.equals(id) && super.equals(other)));
    }
}

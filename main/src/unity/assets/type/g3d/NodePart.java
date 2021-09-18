package unity.assets.type.g3d;

import unity.assets.type.g3d.attribute.*;
import unity.graphics.*;

/**
 * @author badlogic
 * @author Xoppa
 */
public class NodePart{
    public boolean enabled = true;

    public MeshPart meshPart;
    public Material material;

    public NodePart(){}

    public NodePart(MeshPart meshPart, Material material){
        this.meshPart = meshPart;
        this.material = material;
    }

    public Renderable setRenderable(Renderable out){
        out.material = material;
        out.meshPart.set(meshPart);

        return out;
    }

    public NodePart copy(){
        return new NodePart().set(this);
    }

    protected NodePart set(NodePart other){
        meshPart = new MeshPart(other.meshPart);
        material = other.material;
        enabled = other.enabled;

        return this;
    }
}

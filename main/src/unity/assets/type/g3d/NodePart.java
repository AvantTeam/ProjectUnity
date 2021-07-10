package unity.assets.type.g3d;

import unity.assets.type.g3d.attribute.*;
import unity.graphics.*;

/**
 * @author badlogic
 * @author Xoppa
 */
public class NodePart{
    /** The shape to render. Must not be null. */
    public MeshPart meshPart;

    /** The Material used to render the {@link #meshPart}. Must not be null. */
    public Material material;

    /** true by default. If set to false, this part will not participate in rendering and bounding box calculation. */
    public boolean enabled = true;

    /**
     * Construct a new NodePart with null values. At least the {@link #meshPart} and {@link #material} member must be
     * set before the newly created part can be used.
     */
    public NodePart(){}

    /**
     * Construct a new NodePart referencing the provided {@link MeshPart} and {@link Material}.
     * @param meshPart The MeshPart to reference.
     * @param material The Material to reference.
     */
    public NodePart(MeshPart meshPart, Material material){
        this.meshPart = meshPart;
        this.material = material;
    }

    /**
     * Convenience method to set the material, mesh, meshPartOffset, meshPartSize, primitiveType and bones members of
     * the specified Renderable. The other member of the provided {@link Renderable} remain untouched. Note that the
     * material, mesh and bones members are referenced, not copied. Any changes made to those objects will be reflected
     * in both the NodePart and Renderable model.
     * @param out The Renderable of which to set the members to the values of this NodePart.
     */
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

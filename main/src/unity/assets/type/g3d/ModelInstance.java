package unity.assets.type.g3d;

import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;
import unity.assets.type.g3d.attribute.*;

public class ModelInstance implements RenderableProvider{
    public final Seq<Material> materials = new Seq<>();
    public final Seq<Node> nodes = new Seq<>();

    public final Model model;
    public Mat3D transform;
    public Object userData;

    public ModelInstance(Model model){
        this(model, (String[])null);
    }

    public ModelInstance(Model model, String... rootNodeIds){
        this(model, null, rootNodeIds);
    }

    public ModelInstance(Model model, Mat3D transform, String... rootNodeIds){
        this.model = model;
        this.transform = transform == null ? new Mat3D() : transform;
        if(rootNodeIds == null){
            copyNodes(model.nodes);
        }else{
            copyNodes(model.nodes, rootNodeIds);
        }

        calculateTransforms();
    }

    public ModelInstance(ModelInstance copyFrom, Mat3D transform){
        this.model = copyFrom.model;
        this.transform = transform == null ? new Mat3D() : transform;
        copyNodes(copyFrom.nodes);
        calculateTransforms();
    }

    public ModelInstance(ModelInstance copy) {
        this(copy, copy.transform.cpy());
    }

    public ModelInstance copy(){
        return new ModelInstance(this);
    }

    private void copyNodes(Seq<Node> nodes){
        for(int i = 0, n = nodes.size; i < n; ++i){
            Node node = nodes.get(i);
            this.nodes.add(node.copy());
        }
        invalidate();
    }

    private void copyNodes(Seq<Node> nodes, String... nodeIds){
        for(int i = 0, n = nodes.size; i < n; ++i){
            Node node = nodes.get(i);
            for(String nodeId : nodeIds){
                if(nodeId.equals(node.id)){
                    this.nodes.add(node.copy());
                    break;
                }
            }
        }
        invalidate();
    }

    private void invalidate(Node node){
        for(int i = 0, n = node.parts.size; i < n; ++i){
            NodePart part = node.parts.get(i);

            if(!materials.contains(part.material, true)){
                int midx = materials.indexOf(part.material, false);
                if(midx < 0){
                    materials.add(part.material = part.material.copy());
                }else{
                    part.material = materials.get(midx);
                }
            }
        }
        for(int i = 0, n = node.getChildCount(); i < n; ++i){
            invalidate(node.getChild(i));
        }
    }

    private void invalidate(){
        for(int i = 0, n = nodes.size; i < n; ++i){
            invalidate(nodes.get(i));
        }
    }

    @Override
    public void getRenderables(Prov<Renderable> renders){
        for(Node node : nodes){
            getRenderables(node, renders);
        }
    }

    public void getRenderable(Renderable out, NodePart nodePart){
        nodePart.setRenderable(out);
        if(transform != null){
            out.worldTransform.set(transform);
        }else{
            out.worldTransform.idt();
        }

        out.userData = userData;
    }

    protected void getRenderables(Node node, Prov<Renderable> renders){
        if(node.parts.size > 0){
            for(NodePart nodePart : node.parts){
                if(nodePart.enabled){
                    getRenderable(renders.get(), nodePart);
                }
            }
        }

        for(Node child : node.getChildren()){
            getRenderables(child, renders);
        }
    }

    /**
     * Calculates the local and world transform of all {@link Node} instances in this model, recursively. First each
     * {@link Node#localTransform} transform is calculated based on the translation, rotation and scale of each Node.
     * Then each
     * {@link Node#calculateWorldTransform()} is calculated, based on the parent's world transform and the local 
     * transform of each
     * Node. Finally, the animation bone matrices are updated accordingly.</p>
     * <p>
     * This method can be used to recalculate all transforms if any of the Node's local properties (translation, 
     * rotation, scale)
     * was modified.
     */
    public void calculateTransforms(){
        int n = nodes.size;
        for(int i = 0; i < n; i++){
            nodes.get(i).calculateTransforms(true);
        }
    }

    /**
     * @param id The ID of the material to fetch.
     * @return The {@link Material} with the specified id, or null if not available.
     */
    public Material getMaterial(String id){
        return getMaterial(id, true);
    }

    /**
     * @param id The ID of the material to fetch.
     * @param ignoreCase whether to use case sensitivity when comparing the material id.
     * @return The {@link Material} with the specified id, or null if not available.
     */
    public Material getMaterial(String id, boolean ignoreCase){
        final int n = materials.size;
        Material material;
        if(ignoreCase){
            for(int i = 0; i < n; i++){
                if((material = materials.get(i)).id.equalsIgnoreCase(id)){
                    return material;
                }
            }
        }else{
            for(int i = 0; i < n; i++){
                if((material = materials.get(i)).id.equals(id)){
                    return material;
                }
            }
        }

        return null;
    }

    /**
     * @param id The ID of the node to fetch.
     * @return The {@link Node} with the specified id, or null if not found.
     */
    public Node getNode(String id){
        return getNode(id, true);
    }

    /**
     * @param id The ID of the node to fetch.
     * @param recursive false to fetch a root node only, true to search the entire node tree for the specified node.
     * @return The {@link Node} with the specified id, or null if not found.
     */
    public Node getNode(String id, boolean recursive){
        return getNode(id, recursive, false);
    }

    /**
     * @param id The ID of the node to fetch.
     * @param recursive  false to fetch a root node only, true to search the entire node tree for the specified node.
     * @param ignoreCase whether to use case sensitivity when comparing the node id.
     * @return The {@link Node} with the specified id, or null if not found.
     */
    public Node getNode(String id, boolean recursive, boolean ignoreCase){
        return Node.getNode(nodes, id, recursive, ignoreCase);
    }
}

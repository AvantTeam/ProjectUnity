package unity.assets.type.g3d;

import arc.math.geom.*;
import arc.struct.*;

/**
 * @author badlogic
 * @author Xoppa
 */
public class Node{
    public String id;

    /**
     * Whether this node should inherit the transformation of its parent node, defaults to true. When this flag is
     * false the value of {@link #globalTransform} will be the same as the value of {@link #localTransform} causing
     * the transform to be independent of its parent transform.
     */
    public boolean inheritTransform = true;

    /** The translation, relative to the parent */
    public final Vec3 translation = new Vec3();

    /** The rotation, relative to the parent */
    public final Quat rotation = new Quat(0, 0, 0, 1);

    /** The scale, relative to the parent */
    public final Vec3 scale = new Vec3(1, 1, 1);

    /** The local transform, based on translation/rotation/scale ({@link #calculateLocalTransform()}) */
    public final Mat3D localTransform = new Mat3D();

    /**
     * The global transform, product of local transform and transform of the parent node, calculated via
     * {@link #calculateWorldTransform()}
     **/
    public final Mat3D globalTransform = new Mat3D();

    public Seq<NodePart> parts = new Seq<>(2);

    protected Node parent;
    private final Seq<Node> children = new Seq<>(2);

    /**
     * Calculates the local transform based on the translation, scale and rotation
     * @return the local transform
     */
    public Mat3D calculateLocalTransform(){
        localTransform.set(translation, rotation, scale);
        return localTransform;
    }

    /**
     * Calculates the world transform; the product of local transform and the parent's world transform.
     * @return the world transform
     */
    public Mat3D calculateWorldTransform(){
        if(inheritTransform && parent != null){
            globalTransform.set(parent.globalTransform).mul(localTransform);
        }else{
            globalTransform.set(localTransform);
        }

        return globalTransform;
    }

    /**
     * Calculates the local and world transform of this node and optionally all its children.
     * @param recursive whether to calculate the local/world transforms for children.
     */
    public void calculateTransforms(boolean recursive){
        calculateLocalTransform();
        calculateWorldTransform();

        if(recursive){
            for(Node child : children){
                child.calculateTransforms(true);
            }
        }
    }

    /**
     * Extends the bounding box with the bounds of this Node. This is a potential slow operation, it is advised to
     * cache the result.
     */
    public BoundingBox extendBoundingBox(BoundingBox out){
        return extendBoundingBox(out, true);
    }

    /**
     * Extends the bounding box with the bounds of this Node. This is a potential slow operation, it is advised to
     * cache the result.
     */
    public BoundingBox extendBoundingBox(BoundingBox out, boolean transform){
        int partCount = parts.size;
        for(int i = 0; i < partCount; i++){
            NodePart part = parts.get(i);
            if(part.enabled){
                MeshPart meshPart = part.meshPart;
                meshPart.extendBoundingBox(transform ? globalTransform : null);
            }
        }

        int childCount = children.size;
        for(int i = 0; i < childCount; i++){
            children.get(i).extendBoundingBox(out);
        }

        return out;
    }

    /**
     * Adds this node as child to specified parent Node, synonym for: <code>parent.addChild(this)</code>
     * @param parent The Node to attach this Node to.
     */
    public <T extends Node> void attachTo(T parent){
        parent.addChild(this);
    }

    /** Removes this node from its current parent, if any. Short for: <code>this.getParent().removeChild(this)</code> */
    public void detach(){
        if(parent != null){
            parent.removeChild(this);
            parent = null;
        }
    }

    /** @return whether this Node has one or more children (true) or not (false) */
    public boolean hasChildren(){
        return children.size > 0;
    }

    /**
     * @return The number of child nodes that this Node current contains.
     * @see #getChild(int)
     */
    public int getChildCount(){
        return children.size;
    }

    /**
     * @param index The zero-based index of the child node to get, must be: 0 <= index < {@link #getChildCount()}.
     * @return The child node at the specified index
     */
    public Node getChild(final int index){
        return children.get(index);
    }

    /**
     * @param recursive false to fetch a root child only, true to search the entire node tree for the specified node.
     * @return The node with the specified id, or null if not found.
     */
    public Node getChild(final String id, boolean recursive, boolean ignoreCase){
        return getNode(children, id, recursive, ignoreCase);
    }

    /**
     * Adds the specified node as the currently last child of this node. If the node is already a child of another
     * node, then it is
     * removed from its current parent.
     *
     * @param child The Node to add as child of this Node
     * @return the zero-based index of the child
     */
    public <T extends Node> int addChild(final T child){
        return insertChild(-1, child);
    }

    /**
     * Adds the specified nodes as the currently last child of this node. If the node is already a child of another
     * node, then it
     * is removed from its current parent.
     *
     * @param nodes The Node to add as child of this Node
     * @return the zero-based index of the first added child
     */
    public <T extends Node> int addChildren(final Iterable<T> nodes){
        return insertChildren(-1, nodes);
    }

    /**
     * Insert the specified node as child of this node at the specified index. If the node is already a child of
     * another node, then
     * it is removed from its current parent. If the specified index is less than zero or equal or greater than
     * {@link #getChildCount()} then the Node is added as the currently last child.
     *
     * @param index The zero-based index at which to add the child
     * @param child The Node to add as child of this Node
     * @return the zero-based index of the child
     */
    public <T extends Node> int insertChild(int index, final T child){
        for(Node p = this; p != null; p = p.getParent()){
            if(p == child){
                throw new IllegalArgumentException("Cannot add a parent as a child");
            }
        }

        Node p = child.getParent();
        if(p != null && !p.removeChild(child)){
            throw new IllegalArgumentException("Could not remove child from its current parent");
        }

        if(index < 0 || index >= children.size){
            index = children.size;
            children.add(child);
        }else{
            children.insert(index, child);
        }

        child.parent = this;
        return index;
    }

    /**
     * Insert the specified nodes as children of this node at the specified index. If the node is already a child of
     * another node,
     * then it is removed from its current parent. If the specified index is less than zero or equal or greater than
     * {@link #getChildCount()} then the Node is added as the currently last child.
     *
     * @param index The zero-based index at which to add the child
     * @param nodes The nodes to add as child of this Node
     * @return the zero-based index of the first inserted child
     */
    public <T extends Node> int insertChildren(int index, final Iterable<T> nodes){
        if(index < 0 || index > children.size)
            index = children.size;
        int i = index;
        for(T child : nodes)
            insertChild(i++, child);
        return index;
    }

    /**
     * Removes the specified node as child of this node. On success, the child node will be not attached to any
     * parent node (its
     * {@link #getParent()} method will return null). If the specified node currently isn't a child of this node then
     * the removal
     * is considered to be unsuccessful and the method will return false.
     *
     * @param child The child node to remove.
     * @return Whether the removal was successful.
     */
    public <T extends Node> boolean removeChild(final T child){
        if(!children.remove(child, true)){
            return false;
        }

        child.parent = null;
        return true;
    }

    /**
     * @return An {@link Iterable} to all child nodes that this node contains.
     */
    public Iterable<Node> getChildren(){
        return children;
    }

    /**
     * @return The parent node that holds this node as child node, may be null.
     */
    public Node getParent(){
        return parent;
    }

    /**
     * @return Whether (true) is this Node is a child node of another node or not (false).
     */
    public boolean hasParent(){
        return parent != null;
    }

    /**
     * Creates a nested copy of this Node, any child nodes are copied using this method as well. The {@link #parts}
     * are copied
     * using the {@link NodePart#copy()} method. Note that that method copies the material and nodes (bones) by
     * reference. If you
     * intend to use the copy in a different node tree (e.g. a different Model or ModelInstance) then you will need
     * to update these
     * references afterwards.
     * <p>
     * Override this method in your custom Node class to instantiate that class, in that case you should override the
     * {@link #set(Node)} method as well.
     */
    public Node copy(){
        return new Node().set(this);
    }

    /**
     * Creates a nested copy of this Node, any child nodes are copied using the {@link #copy()} method. This will
     * detach this node
     * from its parent, but does not attach it to the parent of node being copied. The {@link #parts} are copied
     * using the
     * {@link NodePart#copy()} method. Note that that method copies the material and nodes (bones) by reference. If
     * you intend to
     * use this node in a different node tree (e.g. a different Model or ModelInstance) then you will need to update
     * these
     * references afterwards.
     * <p>
     * Override this method in your custom Node class to copy any additional fields you've added.
     *
     * @return This Node for chaining
     */
    protected Node set(Node other){
        detach();

        id = other.id;
        inheritTransform = other.inheritTransform;
        translation.set(other.translation);
        rotation.set(other.rotation);
        scale.set(other.scale);
        localTransform.set(other.localTransform);
        globalTransform.set(other.globalTransform);
        parts.clear();

        for(NodePart nodePart : other.parts){
            parts.add(nodePart.copy());
        }

        children.clear();
        for(Node child : other.getChildren()){
            addChild(child.copy());
        }

        return this;
    }

    /**
     * Helper method to recursive fetch a node from an array
     *
     * @param recursive false to fetch a root node only, true to search the entire node tree for the specified node.
     * @return The node with the specified id, or null if not found.
     */
    public static Node getNode(Seq<Node> nodes, String id, boolean recursive, boolean ignoreCase){
        final int n = nodes.size;
        Node node;

        if(ignoreCase){
            for(int i = 0; i < n; i++){
                if((node = nodes.get(i)).id.equalsIgnoreCase(id)){
                    return node;
                }
            }
        }else{
            for(int i = 0; i < n; i++){
                if((node = nodes.get(i)).id.equals(id)){
                    return node;
                }
            }
        }

        if(recursive){
            for(int i = 0; i < n; i++){
                if((node = getNode(nodes.get(i).children, id, true, ignoreCase)) != null){
                    return node;
                }
            }
        }

        return null;
    }
}

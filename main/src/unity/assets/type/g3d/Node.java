package unity.assets.type.g3d;

import arc.math.geom.*;
import arc.struct.*;

/**
 * @author badlogic
 * @author Xoppa
 */
public class Node{
    public String id;

    public boolean inheritTransform = true;
    public boolean isAnimated = false;

    public final Vec3 translation = new Vec3();
    public final Quat rotation = new Quat(0, 0, 0, 1);
    public final Vec3 scale = new Vec3(1, 1, 1);

    public final Mat3D localTransform = new Mat3D();
    public final Mat3D globalTransform = new Mat3D();

    public final Seq<NodePart> parts = new Seq<>(2);

    protected Node parent;
    private final Seq<Node> children = new Seq<>(2);

    public Mat3D calculateLocalTransform(){
        if(!isAnimated) localTransform.set(translation, rotation, scale);
        return localTransform;
    }

    public Mat3D calculateWorldTransform(){
        if(inheritTransform && parent != null){
            globalTransform.set(parent.globalTransform).mul(localTransform);
        }else{
            globalTransform.set(localTransform);
        }

        return globalTransform;
    }

    public void calculateTransforms(boolean recursive){
        calculateLocalTransform();
        calculateWorldTransform();

        if(recursive){
            for(var child : children){
                child.calculateTransforms(true);
            }
        }
    }

    public BoundingBox extendBoundingBox(BoundingBox out){
        return extendBoundingBox(out, true);
    }

    public BoundingBox extendBoundingBox(BoundingBox out, boolean transform){
        int partCount = parts.size;
        for(int i = 0; i < partCount; i++){
            var part = parts.get(i);
            if(part.enabled){
                var meshPart = part.meshPart;
                meshPart.extendBoundingBox(transform ? globalTransform : null);
            }
        }

        int childCount = children.size;
        for(int i = 0; i < childCount; i++){
            children.get(i).extendBoundingBox(out);
        }

        return out;
    }

    public <T extends Node> void attachTo(T parent){
        parent.addChild(this);
    }

    public void detach(){
        if(parent != null){
            parent.removeChild(this);
            parent = null;
        }
    }

    public boolean hasChildren(){
        return children.size > 0;
    }

    public int getChildCount(){
        return children.size;
    }

    public Node getChild(int index){
        return children.get(index);
    }

    public Node getChild(String id, boolean recursive, boolean ignoreCase){
        return getNode(children, id, recursive, ignoreCase);
    }

    public <T extends Node> int addChild(T child){
        return insertChild(-1, child);
    }

    public <T extends Node> int addChildren(Iterable<T> nodes){
        return insertChildren(-1, nodes);
    }

    public <T extends Node> int insertChild(int index, T child){
        for(var p = this; p != null; p = p.getParent()){
            if(p == child){
                throw new IllegalArgumentException("Cannot add a parent as a child");
            }
        }

        var p = child.getParent();
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

    public <T extends Node> int insertChildren(int index, Iterable<T> nodes){
        if(index < 0 || index > children.size) index = children.size;

        int i = index;
        for(T child : nodes) insertChild(i++, child);

        return index;
    }

    public <T extends Node> boolean removeChild(T child){
        if(!children.remove(child, true)){
            return false;
        }

        child.parent = null;
        return true;
    }

    public Iterable<Node> getChildren(){
        return children;
    }

    public Node getParent(){
        return parent;
    }

    public boolean hasParent(){
        return parent != null;
    }

    public Node copy(){
        return new Node().set(this);
    }

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
        for(var nodePart : other.parts){
            parts.add(nodePart.copy());
        }

        children.clear();
        for(var child : other.getChildren()){
            addChild(child.copy());
        }

        return this;
    }

    public static Node getNode(Seq<Node> nodes, String id, boolean recursive, boolean ignoreCase){
        int n = nodes.size;
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

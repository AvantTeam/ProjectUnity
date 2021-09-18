package unity.assets.type.g3d;

import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;
import unity.assets.type.g3d.attribute.*;

public class ModelInstance implements RenderableProvider{
    public final Seq<Material> materials = new Seq<>();
    public final Seq<Node> nodes = new Seq<>();
    public final Seq<Animation> animations = new Seq<>();

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

        copyAnimations(model.animations);
        calculateTransforms();
    }

    public ModelInstance(ModelInstance inst, Mat3D transform){
        this.model = inst.model;
        this.transform = transform == null ? new Mat3D() : transform;

        copyNodes(inst.nodes);
        copyAnimations(inst.animations);

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
            var node = nodes.get(i);
            this.nodes.add(node.copy());
        }

        invalidate();
    }

    private void copyNodes(Seq<Node> nodes, String... nodeIds){
        for(int i = 0, n = nodes.size; i < n; ++i){
            var node = nodes.get(i);
            for(var nodeId : nodeIds){
                if(nodeId.equals(node.id)){
                    this.nodes.add(node.copy());
                    break;
                }
            }
        }

        invalidate();
    }

    public void copyAnimations(Iterable<Animation> source){
        for(var anim : source){
            copyAnimation(anim, true);
        }
    }

    public void copyAnimations(Iterable<Animation> source, boolean shareKeyframes){
        for(var anim : source){
            copyAnimation(anim, shareKeyframes);
        }
    }

    public void copyAnimation(Animation sourceAnim){
        copyAnimation(sourceAnim, true);
    }

    public void copyAnimation(Animation sourceAnim, boolean shareKeyframes){
        var animation = new Animation();
        animation.id = sourceAnim.id;
        animation.duration = sourceAnim.duration;

        for(var nanim : sourceAnim.nodeAnimations){
            var node = getNode(nanim.node.id);
            if(node == null) continue;

            var nodeAnim = new NodeAnimation();
            nodeAnim.node = node;

            if(shareKeyframes){
                nodeAnim.translation = nanim.translation;
                nodeAnim.rotation = nanim.rotation;
                nodeAnim.scaling = nanim.scaling;
            }else{
                if(nanim.translation != null){
                    nodeAnim.translation = new Seq<>();
                    for(var kf : nanim.translation){
                        nodeAnim.translation.add(new NodeKeyframe<>(kf.keytime, kf.value));
                    }
                }

                if(nanim.rotation != null){
                    nodeAnim.rotation = new Seq<>();
                    for(var kf : nanim.rotation){
                        nodeAnim.rotation.add(new NodeKeyframe<>(kf.keytime, kf.value));
                    }
                }

                if(nanim.scaling != null){
                    nodeAnim.scaling = new Seq<>();
                    for(var kf : nanim.scaling){
                        nodeAnim.scaling.add(new NodeKeyframe<>(kf.keytime, kf.value));
                    }
                }
            }

            if(nodeAnim.translation != null || nodeAnim.rotation != null || nodeAnim.scaling != null){
                animation.nodeAnimations.add(nodeAnim);
            }
        }

        if(animation.nodeAnimations.size > 0) animations.add(animation);
    }

    private void invalidate(Node node){
        for(int i = 0, n = node.parts.size; i < n; ++i){
            var part = node.parts.get(i);

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
        for(var node : nodes){
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
            for(var nodePart : node.parts){
                if(nodePart.enabled){
                    getRenderable(renders.get(), nodePart);
                }
            }
        }

        for(var child : node.getChildren()){
            getRenderables(child, renders);
        }
    }

    public void calculateTransforms(){
        int n = nodes.size;
        for(int i = 0; i < n; i++){
            nodes.get(i).calculateTransforms(true);
        }
    }

    public Material getMaterial(){
        return materials.firstOpt();
    }

    public Material getMaterial(String id){
        return getMaterial(id, true);
    }

    public Material getMaterial(String id, boolean ignoreCase){
        int n = materials.size;
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

    public Node getNode(String id){
        return getNode(id, true);
    }

    public Node getNode(String id, boolean recursive){
        return getNode(id, recursive, false);
    }

    public Node getNode(String id, boolean recursive, boolean ignoreCase){
        return Node.getNode(nodes, id, recursive, ignoreCase);
    }

    public Animation getAnimation(String id){
        return getAnimation(id, false);
    }

    public Animation getAnimation(String id, boolean ignoreCase){
        int n = animations.size;
        Animation animation;

        if(ignoreCase){
            for(int i = 0; i < n; i++){
                if((animation = animations.get(i)).id.equalsIgnoreCase(id)){
                    return animation;
                }
            }
        }else{
            for(int i = 0; i < n; i++){
                if((animation = animations.get(i)).id.equals(id)){
                    return animation;
                }
            }
        }

        return null;
    }
}

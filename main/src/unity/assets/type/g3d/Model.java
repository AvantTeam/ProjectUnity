package unity.assets.type.g3d;

import arc.graphics.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import unity.assets.type.g3d.attribute.*;
import unity.assets.type.g3d.attribute.type.*;
import unity.assets.type.g3d.model.*;
import unity.graphics.*;

import static mindustry.Vars.*;

/**
 * A 3-dimensional model model representing a {@code .g3d[b|j]} files. Implementation is heavily based
 * on libGDX. Normal and bump texture maps are yet to be
 * implemented.
 * @author Xoppa
 * @author badlogic
 */
public class Model implements Disposable{
    public final Seq<Material> materials = new Seq<>();
    public final Seq<Node> nodes = new Seq<>();
    public final Seq<Animation> animations = new Seq<>();
    public final Seq<Mesh> meshes = new Seq<>();
    public final Seq<MeshPart> meshParts = new Seq<>();

    public Model(){}

    public Model(ModelData modelData){
        load(modelData);
    }

    public void load(ModelData data){
        loadMeshes(data.meshes);
        loadMaterials(data.materials);
        loadNodes(data.nodes);
        loadAnimations(data.animations);

        calculateTransforms();
    }

    protected void loadAnimations(Iterable<ModelAnimation> modelAnimations){
        for(var anim : modelAnimations){
            Animation animation = new Animation();
            animation.id = anim.id;

            for(var nanim : anim.nodeAnimations){
                Node node = getNode(nanim.nodeId);
                if(node == null)  continue;

                NodeAnimation nodeAnim = new NodeAnimation();
                nodeAnim.node = node;

                if(nanim.translation != null){
                    nodeAnim.translation = new Seq<>();
                    nodeAnim.translation.ensureCapacity(nanim.translation.size);

                    for(var kf : nanim.translation){
                        if(kf.keytime > animation.duration) animation.duration = kf.keytime;
                        nodeAnim.translation.add(new NodeKeyframe<>(
                            kf.keytime,
                            new Vec3(kf.value == null ? node.translation : kf.value)
                        ));
                    }
                }

                if(nanim.rotation != null){
                    nodeAnim.rotation = new Seq<>();
                    nodeAnim.rotation.ensureCapacity(nanim.rotation.size);

                    for(var kf : nanim.rotation){
                        if(kf.keytime > animation.duration)  animation.duration = kf.keytime;
                        nodeAnim.rotation.add(new NodeKeyframe<>(
                            kf.keytime,
                            new Quat(kf.value == null ? node.rotation : kf.value)
                        ));
                    }
                }

                if(nanim.scaling != null){
                    nodeAnim.scaling = new Seq<>();
                    nodeAnim.scaling.ensureCapacity(nanim.scaling.size);

                    for(var kf : nanim.scaling){
                        if(kf.keytime > animation.duration) animation.duration = kf.keytime;
                        nodeAnim.scaling.add(new NodeKeyframe<>(
                            kf.keytime,
                            new Vec3(kf.value == null ? node.scale : kf.value)
                        ));
                    }
                }

                if(
                    (nodeAnim.translation != null && nodeAnim.translation.any()) ||
                    (nodeAnim.rotation != null && nodeAnim.rotation.any()) ||
                    (nodeAnim.scaling != null && nodeAnim.scaling.any())
                ){
                    animation.nodeAnimations.add(nodeAnim);
                }
            }

            if(animation.nodeAnimations.size > 0) animations.add(animation);
        }
    }

    protected void loadNodes(Iterable<ModelNode> modelNodes){
        for(var node : modelNodes){
            nodes.add(loadNode(node));
        }
    }

    protected Node loadNode(ModelNode modelNode){
        Node node = new Node();
        node.id = modelNode.id;

        if(modelNode.translation != null) node.translation.set(modelNode.translation);
        if(modelNode.rotation != null) node.rotation.set(modelNode.rotation);
        if(modelNode.scale != null) node.scale.set(modelNode.scale);

        if(modelNode.parts != null){
            for(ModelNodePart modelNodePart : modelNode.parts){
                MeshPart meshPart = null;
                Material meshMaterial = null;

                if(modelNodePart.meshPartId != null){
                    for(MeshPart part : meshParts){
                        if(modelNodePart.meshPartId.equals(part.id)){
                            meshPart = part;
                            break;
                        }
                    }
                }

                if(modelNodePart.materialId != null){
                    for(Material material : materials){
                        if(modelNodePart.materialId.equals(material.id)){
                            meshMaterial = material;
                            break;
                        }
                    }
                }

                if(meshPart == null || meshMaterial == null){
                    throw new IllegalArgumentException("Invalid node: " + node.id);
                }

                NodePart nodePart = new NodePart();
                nodePart.meshPart = meshPart;
                nodePart.material = meshMaterial;
                node.parts.add(nodePart);
            }
        }

        if(modelNode.children != null){
            for(ModelNode child : modelNode.children){
                node.addChild(loadNode(child));
            }
        }

        return node;
    }

    protected void loadMeshes(Seq<ModelMesh> meshes){
        for(ModelMesh mesh : meshes){
            convertMesh(mesh);
        }
    }

    protected void convertMesh(ModelMesh modelMesh){
        int numIndices = 0;
        for(var part : modelMesh.parts){
            numIndices += part.indices.length;
        }
        boolean hasIndices = numIndices > 0;

        int vertSize = 0;
        for(var vert : modelMesh.attributes){
            vertSize += vert.size;
        }
        int numVertices = modelMesh.vertices.length / (vertSize / 4);

        Mesh mesh = new Mesh(true, numVertices, numIndices, modelMesh.attributes);
        meshes.add(mesh);

        Buffers.copy(modelMesh.vertices, mesh.getVerticesBuffer(), modelMesh.vertices.length, 0);
        int offset = 0;

        mesh.getIndicesBuffer().clear();
        for(ModelMeshPart part : modelMesh.parts){
            MeshPart meshPart = new MeshPart();
            meshPart.id = part.id;
            meshPart.primitiveType = part.primitiveType;
            meshPart.offset = offset;
            meshPart.size = hasIndices ? part.indices.length : numVertices;
            meshPart.mesh = mesh;
            if(hasIndices){
                mesh.getIndicesBuffer().put(part.indices);
            }
            offset += meshPart.size;
            meshParts.add(meshPart);
        }

        mesh.getIndicesBuffer().position(0);
        for(MeshPart part : meshParts){
            part.calculateCenter();
        }
    }

    protected void loadMaterials(Seq<ModelMaterial> modelMaterials){
        for(ModelMaterial mtl : modelMaterials){
            materials.add(convertMaterial(mtl));
        }
    }

    protected Material convertMaterial(ModelMaterial mtl){
        Material result = new Material();
        result.id = mtl.id;

        if(mtl.ambient != null) result.set(new ColorAttribute(ColorAttribute.ambient, mtl.ambient));
        if(mtl.diffuse != null) result.set(new ColorAttribute(ColorAttribute.diffuse, mtl.diffuse));
        if(mtl.specular != null) result.set(new ColorAttribute(ColorAttribute.specular, mtl.specular));
        if(mtl.emissive != null) result.set(new ColorAttribute(ColorAttribute.emissive, mtl.emissive));
        if(mtl.reflection != null) result.set(new ColorAttribute(ColorAttribute.reflection, mtl.reflection));
        if(mtl.shininess > 0f) result.set(new FloatAttribute(FloatAttribute.shininess, mtl.shininess));
        if(mtl.opacity != 1f) result.set(new BlendingAttribute(Gl.srcAlpha, Gl.oneMinusSrcAlpha, mtl.opacity));

        if(mtl.textures != null){
            for(ModelTexture tex : mtl.textures){
                Texture texture = new Texture(tree.get(tex.fileName));

                float offsetU = tex.uvTranslation == null ? 0f : tex.uvTranslation.x;
                float offsetV = tex.uvTranslation == null ? 0f : tex.uvTranslation.y;
                float scaleU = tex.uvScaling == null ? 1f : tex.uvScaling.x;
                float scaleV = tex.uvScaling == null ? 1f : tex.uvScaling.y;

                result.set(new TextureAttribute(
                    switch(tex.usage){
                        case ModelTexture.diffuse -> TextureAttribute.diffuse;
                        case ModelTexture.specular -> TextureAttribute.specular;
                        case ModelTexture.bump -> TextureAttribute.bump;
                        case ModelTexture.normal -> TextureAttribute.normal;
                        case ModelTexture.ambient -> TextureAttribute.ambient;
                        case ModelTexture.emissive -> TextureAttribute.emissive;
                        case ModelTexture.reflection -> TextureAttribute.reflection;
                        default -> throw new IllegalArgumentException("Unknown usage: " + tex.usage);
                    },
                    texture,
                    offsetU, offsetV, scaleU, scaleV
                ));
            }
        }

        return result;
    }

    @Override
    public void dispose(){
        materials.clear();
        nodes.clear();
        meshes.clear();
        meshParts.clear();
    }

    public void calculateTransforms(){
        int n = nodes.size;
        for(int i = 0; i < n; i++){
            nodes.get(i).calculateTransforms(true);
        }
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
}

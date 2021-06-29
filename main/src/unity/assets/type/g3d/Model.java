package unity.assets.type.g3d;

import arc.graphics.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import unity.assets.type.g3d.attribute.*;
import unity.assets.type.g3d.attribute.type.*;
import unity.assets.type.g3d.model.*;
import unity.graphics.*;

/**
 * A 3-dimensional model object representing a {@code .g3d} files. Implementation is heavily based
 * on libGDX.
 * @author Xoppa
 * @author badlogic
 */
public class Model implements Disposable{
    public final Seq<Material> materials = new Seq<>();
    public final Seq<Node> nodes = new Seq<>();
    public final Seq<Mesh> meshes = new Seq<>();
    public final Seq<MeshPart> meshParts = new Seq<>();

    protected final Seq<Disposable> disposables = new Seq<>();

    private final ObjectMap<NodePart, OrderedMap<String, Mat3D>> nodePartBones = new ObjectMap<>();

    /**
     * Constructs an empty model. Manual created models do not manage their resources by default. Use
     * {@link #manageDisposable(Disposable)} to add resources to be managed by this model.
     */
    public Model(){}

    /**
     * Constructs a new Model based on the {@link ModelData}.
     * @param modelData the {@link ModelData} got from e.g. {@link ModelLoader}
     */
    public Model(ModelData modelData){
        load(modelData);
    }

    protected void load(ModelData modelData){
        loadMeshes(modelData.meshes);
        loadMaterials(modelData.materials);
        loadNodes(modelData.nodes);
        calculateTransforms();
    }

    protected void loadNodes(Iterable<ModelNode> modelNodes){
        nodePartBones.clear();
        for(ModelNode node : modelNodes){
            nodes.add(loadNode(node));
        }

        for(var e : nodePartBones.entries()){
            if(e.key.invBoneBindTransforms == null){
                e.key.invBoneBindTransforms = new OrderedMap<>();
            }

            e.key.invBoneBindTransforms.clear();
            for(var b : e.value.entries()){
                e.key.invBoneBindTransforms.put(getNode(b.key), new Mat3D(b.value).inv());
            }
        }
    }

    protected Node loadNode(ModelNode modelNode){
        Node node = new Node();
        node.id = modelNode.id;

        if(modelNode.translation != null)
            node.translation.set(modelNode.translation);
        if(modelNode.rotation != null)
            node.rotation.set(modelNode.rotation);
        if(modelNode.scale != null)
            node.scale.set(modelNode.scale);

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
                if(modelNodePart.bones != null)
                    nodePartBones.put(nodePart, modelNodePart.bones);
            }
        }

        if(modelNode.children != null){
            for(ModelNode child : modelNode.children){
                node.addChild(loadNode(child));
            }
        }

        return node;
    }

    protected void loadMeshes(Iterable<ModelMesh> meshes){
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

        int numVertices = 0;
        for(var vert : modelMesh.attributes){
            numVertices += vert.size;
        }

        Mesh mesh = new Mesh(true, numVertices, numIndices, modelMesh.attributes);
        meshes.add(mesh);
        disposables.add(mesh);

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
            part.update();
        }
    }

    protected void loadMaterials(Iterable<ModelMaterial> modelMaterials){
        for(ModelMaterial mtl : modelMaterials){
            this.materials.add(convertMaterial(mtl));
        }
    }

    protected Material convertMaterial(ModelMaterial mtl){
        Material result = new Material();
        result.id = mtl.id;
        if(mtl.ambient != null) result.set(new ColorAttribute(ColorAttribute.Ambient, mtl.ambient));
        if(mtl.diffuse != null) result.set(new ColorAttribute(ColorAttribute.Diffuse, mtl.diffuse));
        if(mtl.specular != null) result.set(new ColorAttribute(ColorAttribute.Specular, mtl.specular));
        if(mtl.emissive != null) result.set(new ColorAttribute(ColorAttribute.Emissive, mtl.emissive));
        if(mtl.reflection != null) result.set(new ColorAttribute(ColorAttribute.Reflection, mtl.reflection));
        if(mtl.shininess > 0f) result.set(new FloatAttribute(FloatAttribute.Shininess, mtl.shininess));
        if(mtl.opacity != 1.f) result.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, mtl.opacity));

        if(mtl.textures != null){
            for(ModelTexture tex : mtl.textures){
                TextureDescriptor<Texture> descriptor = new TextureDescriptor<>(tex.fileName);

                float offsetU = tex.uvTranslation == null ? 0f : tex.uvTranslation.x;
                float offsetV = tex.uvTranslation == null ? 0f : tex.uvTranslation.y;
                float scaleU = tex.uvScaling == null ? 1f : tex.uvScaling.x;
                float scaleV = tex.uvScaling == null ? 1f : tex.uvScaling.y;

                result.set(switch(tex.usage){
                    case ModelTexture.usageDiffuse -> new TextureAttribute(
                        TextureAttribute.diffuse, descriptor,
                        offsetU, offsetV, scaleU, scaleV
                    );

                    case ModelTexture.usageSpecular -> new TextureAttribute(
                        TextureAttribute.specular, descriptor,
                        offsetU, offsetV, scaleU, scaleV
                    );

                    case ModelTexture.usageBump -> new TextureAttribute(
                        TextureAttribute.bump, descriptor,
                        offsetU, offsetV, scaleU, scaleV
                    );

                    case ModelTexture.usageNormal -> new TextureAttribute(
                        TextureAttribute.normal, descriptor,
                        offsetU, offsetV, scaleU, scaleV
                    );

                    case ModelTexture.usageAmbient -> new TextureAttribute(
                        TextureAttribute.ambient, descriptor,
                        offsetU, offsetV, scaleU, scaleV
                    );

                    case ModelTexture.usageEmissive -> new TextureAttribute(
                        TextureAttribute.emissive, descriptor,
                        offsetU, offsetV, scaleU, scaleV
                    );

                    case ModelTexture.usageReflection -> new TextureAttribute(
                        TextureAttribute.reflection, descriptor,
                        offsetU, offsetV, scaleU, scaleV
                    );

                    default -> throw new IllegalArgumentException("Invalid usage: " + tex.usage);
                });
            }
        }

        return result;
    }

    /**
     * Adds a {@link Disposable} to be managed and disposed by this Model. Can be used to keep track of manually
     * loaded textures
     * for {@link ModelInstance}.
     *
     * @param disposable the Disposable
     */
    public void manageDisposable(Disposable disposable){
        if(!disposables.contains(disposable, true)){
            disposables.add(disposable);
        }
    }

    /**
     * @return the {@link Disposable} objects that will be disposed when the {@link #dispose()} method is called.
     */
    public Seq<Disposable> getManagedDisposables(){
        return disposables;
    }

    @Override
    public void dispose(){
        for(Disposable disposable : disposables){
            disposable.dispose();
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
        final int n = nodes.size;
        for(int i = 0; i < n; i++){
            nodes.get(i).calculateTransforms(true);
        }
        for(int i = 0; i < n; i++){
            nodes.get(i).calculateBoneTransforms(true);
        }
    }

    /**
     * Calculate the bounding box of this model instance. This is a potential slow operation, it is advised to cache
     * the result.
     *
     * @param out the {@link BoundingBox} that will be set with the bounds.
     * @return the out parameter for chaining
     */
    public BoundingBox calculateBoundingBox(BoundingBox out){
        out.inf();
        return extendBoundingBox(out);
    }

    /**
     * Extends the bounding box with the bounds of this model instance. This is a potential slow operation, it is
     * advised to cache
     * the result.
     *
     * @param out the {@link BoundingBox} that will be extended with the bounds.
     * @return the out parameter for chaining
     */
    public BoundingBox extendBoundingBox(final BoundingBox out){
        final int n = nodes.size;
        for(int i = 0; i < n; i++)
            nodes.get(i).extendBoundingBox(out);
        return out;
    }

    /**
     * @param id The ID of the material to fetch.
     * @return The {@link Material} with the specified id, or null if not available.
     */
    public Material getMaterial(final String id){
        return getMaterial(id, true);
    }

    /**
     * @param id         The ID of the material to fetch.
     * @param ignoreCase whether to use case sensitivity when comparing the material id.
     * @return The {@link Material} with the specified id, or null if not available.
     */
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

    /**
     * @param id The ID of the node to fetch.
     * @return The {@link Node} with the specified id, or null if not found.
     */
    public Node getNode(final String id){
        return getNode(id, true);
    }

    /**
     * @param id        The ID of the node to fetch.
     * @param recursive false to fetch a root node only, true to search the entire node tree for the specified node.
     * @return The {@link Node} with the specified id, or null if not found.
     */
    public Node getNode(final String id, boolean recursive){
        return getNode(id, recursive, false);
    }

    /**
     * @param id         The ID of the node to fetch.
     * @param recursive  false to fetch a root node only, true to search the entire node tree for the specified node.
     * @param ignoreCase whether to use case sensitivity when comparing the node id.
     * @return The {@link Node} with the specified id, or null if not found.
     */
    public Node getNode(final String id, boolean recursive, boolean ignoreCase){
        return Node.getNode(nodes, id, recursive, ignoreCase);
    }
}

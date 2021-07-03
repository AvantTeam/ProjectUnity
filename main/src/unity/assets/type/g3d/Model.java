package unity.assets.type.g3d;

import arc.graphics.*;
import arc.struct.*;
import arc.util.*;
import unity.assets.loaders.*;
import unity.assets.type.g3d.attribute.*;
import unity.assets.type.g3d.attribute.type.*;
import unity.assets.type.g3d.model.*;

import static mindustry.Vars.*;

/**
 * A 3-dimensional model object representing a {@code .g3d[b|j]} files. Implementation is heavily based
 * on libGDX. Animations and bones are not supported.
 * @author Xoppa
 * @author badlogic
 */
public class Model implements Disposable{
    public final Seq<Material> materials = new Seq<>();
    public final Seq<Node> nodes = new Seq<>();
    public final Seq<Mesh> meshes = new Seq<>();
    public final Seq<MeshPart> meshParts = new Seq<>();

    protected final Seq<Disposable> disposables = new Seq<>();

    /**
     * Constructs an empty model. Manually created models do not manage their resources by default. Use
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

    public void load(ModelData modelData){
        loadMeshes(modelData.meshes);
        loadMaterials(modelData.materials);
        loadNodes(modelData.nodes);
        calculateTransforms();
    }

    protected void loadNodes(Seq<ModelNode> modelNodes){
        for(ModelNode node : modelNodes){
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
                Texture texture = new Texture(tree.get(tex.fileName + ".png"));

                float offsetU = tex.uvTranslation == null ? 0f : tex.uvTranslation.x;
                float offsetV = tex.uvTranslation == null ? 0f : tex.uvTranslation.y;
                float scaleU = tex.uvScaling == null ? 1f : tex.uvScaling.x;
                float scaleV = tex.uvScaling == null ? 1f : tex.uvScaling.y;

                result.set(new TextureAttribute(
                    switch(tex.usage){
                        case ModelTexture.usageDiffuse -> TextureAttribute.diffuse;
                        case ModelTexture.usageSpecular -> TextureAttribute.specular;
                        case ModelTexture.usageBump -> TextureAttribute.bump;
                        case ModelTexture.usageNormal -> TextureAttribute.normal;
                        case ModelTexture.usageAmbient -> TextureAttribute.ambient;
                        case ModelTexture.usageEmissive -> TextureAttribute.emissive;
                        case ModelTexture.usageReflection -> TextureAttribute.reflection;
                        default -> throw new IllegalArgumentException("Unknown usage: " + tex.usage);
                    },
                    texture,
                    offsetU, offsetV, scaleU, scaleV
                ));
            }
        }

        return result;
    }

    /**
     * Adds a {@link Disposable} to be managed and disposed by this Model. Can be used to keep track of manually
     * loaded textures for {@link ModelInstance}.
     * @param disposable the Disposable
     */
    public void manageDisposable(Disposable disposable){
        if(!disposables.contains(disposable, true)){
            disposables.add(disposable);
        }
    }

    /** @return the {@link Disposable} objects that will be disposed when the {@link #dispose()} method is called. */
    public Seq<Disposable> getManagedDisposables(){
        return disposables;
    }

    @Override
    public void dispose(){
        materials.clear();
        nodes.clear();
        meshes.clear();
        meshParts.clear();
        for(Disposable disposable : disposables){
            disposable.dispose();
        }
    }

    /**
     * Calculates the local and world transform of all {@link Node} instances in this model, recursively. First each
     * {@link Node#localTransform} transform is calculated based on the translation, rotation and scale of each Node.
     * Then each {@link Node#calculateWorldTransform()} is calculated, based on the parent's world transform and the
     * local transform of each Node
     * <p>
     * This method can be used to recalculate all transforms if any of the Node's local properties (translation,
     * rotation, scale) was modified.
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
    public Material getMaterial(final String id){
        return getMaterial(id, true);
    }

    /**
     * @param id The ID of the material to fetch.
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
     * @param recursive false to fetch a root node only, true to search the entire node tree for the specified node.
     * @param ignoreCase whether to use case sensitivity when comparing the node id.
     * @return The {@link Node} with the specified id, or null if not found.
     */
    public Node getNode(String id, boolean recursive, boolean ignoreCase){
        return Node.getNode(nodes, id, recursive, ignoreCase);
    }
}

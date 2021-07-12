package unity.assets.loaders;

import arc.assets.*;
import arc.assets.loaders.*;
import arc.files.*;
import arc.graphics.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.serialization.*;
import unity.assets.type.g3d.*;
import unity.assets.type.g3d.model.*;

@SuppressWarnings("rawtypes")
public class ModelLoader extends SynchronousAssetLoader<Model, ModelLoader.ModelParameter>{
    public static final short versionHi = 0;
    public static final short versionLo = 1;

    protected final UBJsonReader readerBin;
    protected final JsonReader readerText;
    protected final Quat tmpQuat = new Quat();

    public ModelLoader(FileHandleResolver tree){
        super(tree);
        readerBin = new UBJsonReader();
        readerText = new JsonReader();
    }

    @Override
    public Model load(AssetManager manager, String fileName, Fi file, ModelParameter parameter){
        Model model;
        if(parameter != null && parameter.model != null){
            model = parameter.model;
        }else{
            model = new Model();
        }

        model.load(parseModel(file));
        return model;
    }

    public ModelData parseModel(Fi handle){
        BaseJsonReader reader = handle.extEquals("g3db") ? readerBin : (handle.extEquals("g3dj") ? readerText : null);
        if(reader == null) throw new IllegalArgumentException("Unknown model type: " + handle.extension() + " (" + handle.name() + ")");

        JsonValue json = reader.parse(handle);
        ModelData model = new ModelData();
        JsonValue version = json.require("version");

        model.version[0] = version.getShort(0);
        model.version[1] = version.getShort(1);
        if(model.version[0] != versionHi || model.version[1] != versionLo){
            throw new IllegalStateException("Model version not supported");
        }

        model.id = json.getString("id", "");
        parseMeshes(model, json);
        parseMaterials(model, json, handle.parent().path());
        parseNodes(model, json);

        return model;
    }

    protected void parseMeshes(ModelData model, JsonValue json){
        JsonValue meshes = json.get("meshes");
        if(meshes != null){
            model.meshes.ensureCapacity(meshes.size);
            for(JsonValue mesh = meshes.child; mesh != null; mesh = mesh.next){
                ModelMesh jsonMesh = new ModelMesh();

                jsonMesh.id = mesh.getString("id", "");

                JsonValue attributes = mesh.require("attributes");
                jsonMesh.attributes = parseAttributes(attributes);
                jsonMesh.vertices = mesh.require("vertices").asFloatSeq();

                JsonValue meshParts = mesh.require("parts");
                Seq<ModelMeshPart> parts = new Seq<>();
                for(JsonValue meshPart = meshParts.child; meshPart != null; meshPart = meshPart.next){
                    ModelMeshPart jsonPart = new ModelMeshPart();
                    String partId = meshPart.getString("id", null);

                    if(partId == null) throw new IllegalArgumentException("Not id given for mesh part");

                    for(ModelMeshPart other : parts){
                        if(other.id.equals(partId)) throw new IllegalArgumentException("Mesh part with id '" + partId + "' already in defined");
                    }

                    jsonPart.id = partId;

                    String type = meshPart.getString("type", null);
                    if(type == null) throw new IllegalArgumentException("No primitive type given for mesh part '" + partId + "'");

                    jsonPart.primitiveType = parseType(type);
                    jsonPart.indices = meshPart.require("indices").asShortArray();

                    parts.add(jsonPart);
                }

                jsonMesh.parts = parts.toArray(ModelMeshPart.class);
                model.meshes.add(jsonMesh);
            }
        }
    }

    protected int parseType(String type){
        return switch(type){
            case "TRIANGLES" -> GL20.GL_TRIANGLES;
            case "LINES" -> GL20.GL_LINES;
            case "POINTS" -> GL20.GL_POINTS;
            case "TRIANGLE_STRIP" -> GL20.GL_TRIANGLE_STRIP;
            case "LINE_STRIP" -> GL20.GL_LINE_STRIP;
            default -> throw new IllegalArgumentException("Unknown primitive type '" + type + "', should be one of triangle, " + "trianglestrip, line, linestrip, lineloop or point");
        };
    }

    protected VertexAttribute[] parseAttributes(JsonValue attributes){
        Seq<VertexAttribute> vertexAttributes = new Seq<>();

        for(JsonValue value = attributes.child; value != null; value = value.next){
            String attr = value.asString();
            if(attr.equals("POSITION")){
                vertexAttributes.add(VertexAttribute.position3);
            }else if(attr.equals("NORMAL")){
                vertexAttributes.add(VertexAttribute.normal);
            }else if(attr.equals("COLORPACKED")){
                vertexAttributes.add(VertexAttribute.color);
            }else if(attr.startsWith("TEXCOORD")){
                vertexAttributes.add(VertexAttribute.texCoords);
            }else{
                throw new IllegalArgumentException("Unknown vertex attribute '" + attr + "', should be one of position, normal, or uv");
            }
        }

        return vertexAttributes.toArray(VertexAttribute.class);
    }

    protected void parseMaterials(ModelData model, JsonValue json, String materialDir){
        JsonValue materials = json.get("materials");
        if(materials != null){
            model.materials.ensureCapacity(materials.size);
            for(JsonValue material = materials.child; material != null; material = material.next){
                ModelMaterial jsonMaterial = new ModelMaterial();

                String id = material.getString("id", null);
                if(id == null) throw new IllegalArgumentException("Material needs an id.");

                jsonMaterial.id = id;

                JsonValue diffuse = material.get("diffuse");
                if(diffuse != null) jsonMaterial.diffuse = parseColor(diffuse);

                JsonValue ambient = material.get("ambient");
                if(ambient != null) jsonMaterial.ambient = parseColor(ambient);

                JsonValue emissive = material.get("emissive");
                if(emissive != null) jsonMaterial.emissive = parseColor(emissive);

                JsonValue specular = material.get("specular");
                if(specular != null) jsonMaterial.specular = parseColor(specular);

                JsonValue reflection = material.get("reflection");
                if(reflection != null) jsonMaterial.reflection = parseColor(reflection);

                jsonMaterial.shininess = material.getFloat("shininess", 0.0f);
                jsonMaterial.opacity = material.getFloat("opacity", 1.0f);

                JsonValue textures = material.get("textures");
                if(textures != null){
                    for(JsonValue texture = textures.child; texture != null; texture = texture.next){
                        ModelTexture jsonTexture = new ModelTexture();

                        String textureId = texture.getString("id", null);
                        if(textureId == null) throw new IllegalArgumentException("Texture has no id.");
                        jsonTexture.id = textureId;

                        String fileName = texture.getString("filename", null);
                        if(fileName == null) throw new IllegalArgumentException("Texture needs filename.");

                        jsonTexture.fileName = materialDir + (materialDir.length() == 0 || materialDir.endsWith("/") ? "" : "/") + fileName;

                        jsonTexture.uvTranslation = readVec2(texture.get("uvTranslation"), 0f, 0f);
                        jsonTexture.uvScaling = readVec2(texture.get("uvScaling"), 1f, 1f);

                        String textureType = texture.getString("type", null);
                        if(textureType == null) throw new IllegalArgumentException("Texture needs type.");

                        jsonTexture.usage = parseTextureUsage(textureType);

                        if(jsonMaterial.textures == null)
                            jsonMaterial.textures = new Seq<>();
                        jsonMaterial.textures.add(jsonTexture);
                    }
                }

                model.materials.add(jsonMaterial);
            }
        }
    }

    protected int parseTextureUsage(String value){
        if(value.equalsIgnoreCase("AMBIENT")){
            return ModelTexture.usageAmbient;
        }else if(value.equalsIgnoreCase("BUMP")){
            return ModelTexture.usageBump;
        }else if(value.equalsIgnoreCase("DIFFUSE")){
            return ModelTexture.usageDiffuse;
        }else if(value.equalsIgnoreCase("EMISSIVE")){
            return ModelTexture.usageEmissive;
        }else if(value.equalsIgnoreCase("NONE")){
            return ModelTexture.usageNone;
        }else if(value.equalsIgnoreCase("NORMAL")){
            return ModelTexture.usageNormal;
        }else if(value.equalsIgnoreCase("REFLECTION")){
            return ModelTexture.usageReflection;
        }else if(value.equalsIgnoreCase("SHININESS")){
            return ModelTexture.usageShininess;
        }else if(value.equalsIgnoreCase("SPECULAR")){
            return ModelTexture.usageSpecular;
        }else if(value.equalsIgnoreCase("TRANSPARENCY")){
            return ModelTexture.usageTransparency;
        }

        return ModelTexture.usageUnknown;
    }

    protected Color parseColor(JsonValue colorArray){
        if(colorArray.size >= 3){
            return new Color(colorArray.getFloat(0), colorArray.getFloat(1), colorArray.getFloat(2), 1.0f);
        }else{
            throw new IllegalArgumentException("Expected Color values <> than three.");
        }
    }

    protected Vec2 readVec2(JsonValue vectorArray, float x, float y){
        if(vectorArray == null){
            return new Vec2(x, y);
        }else if(vectorArray.size == 2){
            return new Vec2(vectorArray.getFloat(0), vectorArray.getFloat(1));
        }else{
            throw new IllegalArgumentException("Expected Vector2 values <> than two.");
        }
    }

    protected void parseNodes(ModelData model, JsonValue json){
        JsonValue nodes = json.get("nodes");
        if(nodes != null){
            model.nodes.ensureCapacity(nodes.size);
            for(JsonValue node = nodes.child; node != null; node = node.next){
                model.nodes.add(parseNodesRecurse(node));
            }
        }
    }

    protected ModelNode parseNodesRecurse(JsonValue json){
        ModelNode jsonNode = new ModelNode();

        String id = json.getString("id", null);
        if(id == null) throw new IllegalArgumentException("Node id missing.");
        jsonNode.id = id;

        JsonValue translation = json.get("translation");
        if(translation != null && translation.size != 3) throw new IllegalArgumentException("Node translation incomplete");
        jsonNode.translation = translation == null ? null : new Vec3(translation.getFloat(0), translation.getFloat(1), translation.getFloat(2));

        JsonValue rotation = json.get("rotation");
        if(rotation != null && rotation.size != 4) throw new IllegalArgumentException("Node rotation incomplete");
        jsonNode.rotation = rotation == null ? null : new Quat(rotation.getFloat(0), rotation.getFloat(1), rotation.getFloat(2), rotation.getFloat(3));

        JsonValue scale = json.get("scale");
        if(scale != null && scale.size != 3) throw new IllegalArgumentException("Node scale incomplete");
        jsonNode.scale = scale == null ? null : new Vec3(scale.getFloat(0), scale.getFloat(1), scale.getFloat(2));

        String meshId = json.getString("mesh", null);
        if(meshId != null) jsonNode.meshId = meshId;

        JsonValue materials = json.get("parts");
        if(materials != null){
            jsonNode.parts = new ModelNodePart[materials.size];
            int i = 0;
            for(JsonValue material = materials.child; material != null; material = material.next, i++){
                ModelNodePart nodePart = new ModelNodePart();

                String meshPartId = material.getString("meshpartid", null);
                String materialId = material.getString("materialid", null);
                if(meshPartId == null || materialId == null){
                    throw new IllegalArgumentException("Node " + id + " part is missing meshPartId or materialId");
                }
                nodePart.materialId = materialId;
                nodePart.meshPartId = meshPartId;

                jsonNode.parts[i] = nodePart;
            }
        }

        JsonValue children = json.get("children");
        if(children != null){
            jsonNode.children = new ModelNode[children.size];

            int i = 0;
            for(JsonValue child = children.child; child != null; child = child.next, i++){
                jsonNode.children[i] = parseNodesRecurse(child);
            }
        }

        return jsonNode;
    }

    @Override
    public Seq<AssetDescriptor> getDependencies(String fileName, Fi file, ModelParameter parameter){
        return null;
    }

    public static class ModelParameter extends AssetLoaderParameters<Model>{
        protected @Nullable Model model;

        public ModelParameter(Model model){
            this.model = model;
        }
    }
}

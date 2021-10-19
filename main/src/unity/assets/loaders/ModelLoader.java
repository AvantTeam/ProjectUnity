package unity.assets.loaders;

import arc.assets.*;
import arc.assets.loaders.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.serialization.*;
import unity.assets.type.g3d.*;
import unity.assets.type.g3d.model.*;

@SuppressWarnings("rawtypes")
public class ModelLoader extends SynchronousAssetLoader<Model, ModelLoader.ModelParameter>{
    protected final BaseJsonReader reader;

    public ModelLoader(FileHandleResolver tree, BaseJsonReader reader){
        super(tree);
        this.reader = reader;
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
        JsonValue json = reader.parse(handle);
        ModelData model = new ModelData();

        model.id = json.getString("id", "");
        parseMeshes(model, json);
        parseMaterials(model, json, handle.parent().path());
        parseNodes(model, json);
        parseAnimations(model, json);

        return model;
    }

    protected void parseMeshes(ModelData model, JsonValue json){
        JsonValue meshes = json.get("meshes");
        if(meshes != null){
            model.meshes.ensureCapacity(meshes.size);
            for(var mesh = meshes.child; mesh != null; mesh = mesh.next){
                ModelMesh data = new ModelMesh();

                data.id = mesh.getString("id", "");

                JsonValue attributes = mesh.require("attributes");
                data.attributes = parseAttributes(attributes);
                data.vertices = mesh.require("vertices").asFloatSeq();

                JsonValue meshParts = mesh.require("parts");
                Seq<ModelMeshPart> parts = Seq.of(ModelMeshPart.class);
                for(var meshPart = meshParts.child; meshPart != null; meshPart = meshPart.next){
                    ModelMeshPart part = new ModelMeshPart();
                    String partId = meshPart.require("id").asString();

                    if(parts.contains(other -> other.id.equals(partId))) throw new IllegalArgumentException("Mesh part with id '" + partId + "' already in defined");

                    part.id = partId;

                    part.primitiveType = parseType(meshPart.require("type").asString());
                    part.indices = meshPart.require("indices").asShortArray();

                    parts.add(part);
                }

                data.parts = parts.toArray();
                model.meshes.add(data);
            }
        }
    }

    protected int parseType(String type){
        return switch(type){
            case "POINTS" -> Gl.points;
            case "LINES" -> Gl.lines;
            case "LINE_LOOP" -> Gl.lineLoop;
            case "LINE_STRIP" -> Gl.lineStrip;
            case "TRIANGLES" -> Gl.triangles;
            case "TRIANGLE_FAN" -> Gl.triangleFan;
            case "TRIANGLE_STRIP" -> Gl.triangleStrip;
            default -> throw new IllegalArgumentException("Unknown primitive type '" + type + "'");
        };
    }

    protected VertexAttribute[] parseAttributes(JsonValue attributes){
        Seq<VertexAttribute> vertexAttributes = Seq.of(VertexAttribute.class);

        int texUnit = 0,
            blendUnit = 0;

        for(var value = attributes.child; value != null; value = value.next){
            String attr = value.asString();
            if(attr.equals("POSITION")){
                vertexAttributes.add(VertexAttribute.position3);
            }else if(attr.equals("NORMAL")){
                vertexAttributes.add(VertexAttribute.normal);
            }else if(attr.equals("COLORPACKED")){
                vertexAttributes.add(VertexAttribute.color);
            }else if(attr.startsWith("TEXCOORD")){
                vertexAttributes.add(new VertexAttribute(2, Shader.texcoordAttribute + texUnit++));
            }else if(attr.startsWith("BLENDWEIGHT")){
                vertexAttributes.add(new VertexAttribute(2, "a_blendWeight" + blendUnit++));
            }else{
                throw new IllegalArgumentException("Unknown vertex attribute '" + attr + "'");
            }
        }

        return vertexAttributes.toArray();
    }

    protected void parseMaterials(ModelData model, JsonValue json, String dir){
        JsonValue materials = json.get("materials");
        if(materials != null){
            model.materials.ensureCapacity(materials.size);
            for(var material = materials.child; material != null; material = material.next){
                ModelMaterial data = new ModelMaterial();
                data.id = material.require("id").asString();

                JsonValue diffuse = material.get("diffuse");
                if(diffuse != null) data.diffuse = parseColor(diffuse);

                JsonValue ambient = material.get("ambient");
                if(ambient != null) data.ambient = parseColor(ambient);

                JsonValue emissive = material.get("emissive");
                if(emissive != null) data.emissive = parseColor(emissive);

                JsonValue specular = material.get("specular");
                if(specular != null) data.specular = parseColor(specular);

                JsonValue reflection = material.get("reflection");
                if(reflection != null) data.reflection = parseColor(reflection);

                data.shininess = material.getFloat("shininess", 0.0f);
                data.opacity = material.getFloat("opacity", 1.0f);

                JsonValue textures = material.get("textures");
                if(textures != null){
                    for(var texture = textures.child; texture != null; texture = texture.next){
                        ModelTexture tex = new ModelTexture();
                        tex.id = texture.require("id").asString();

                        String fileName = texture.require("filename").asString();
                        tex.fileName = dir + (dir.length() == 0 || dir.endsWith("/") ? "" : "/") + fileName;

                        tex.uvTranslation = readVec2(texture.get("uvTranslation"), 0f, 0f);
                        tex.uvScaling = readVec2(texture.get("uvScaling"), 1f, 1f);

                        tex.usage = parseTextureUsage(texture.require("type").asString());

                        if(data.textures == null) data.textures = new Seq<>(textures.size);
                        data.textures.add(tex);
                    }
                }

                model.materials.add(data);
            }
        }
    }

    protected int parseTextureUsage(String value){
        return switch(value.toUpperCase()){
            case "AMBIENT" -> ModelTexture.ambient;
            case "BUMP" -> ModelTexture.bump;
            case "DIFFUSE" -> ModelTexture.diffuse;
            case "EMISSIVE" -> ModelTexture.emissive;
            case "NONE" -> ModelTexture.none;
            case "NORMAL" -> ModelTexture.normal;
            case "REFLECTION" -> ModelTexture.reflection;
            case "SHININESS" -> ModelTexture.shininess;
            case "SPECULAR" -> ModelTexture.specular;
            case "TRANSPARENCY" -> ModelTexture.transparency;
            default -> ModelTexture.unknown;
        };
    }

    protected Color parseColor(JsonValue col){
        if(col.size >= 3){
            return new Color(col.getFloat(0), col.getFloat(1), col.getFloat(2), 1.0f);
        }else{
            throw new IllegalArgumentException("Expected Color values < 3");
        }
    }

    protected Vec2 readVec2(JsonValue vectorArray, float x, float y){
        if(vectorArray == null){
            return new Vec2(x, y);
        }else if(vectorArray.size == 2){
            return new Vec2(vectorArray.getFloat(0), vectorArray.getFloat(1));
        }else{
            throw new IllegalArgumentException("Expected Vector2 values < 2");
        }
    }

    protected void parseNodes(ModelData model, JsonValue json){
        JsonValue nodes = json.get("nodes");
        if(nodes != null){
            model.nodes.ensureCapacity(nodes.size);
            for(var node = nodes.child; node != null; node = node.next){
                model.nodes.add(parseNodesRecurse(node));
            }
        }
    }

    protected ModelNode parseNodesRecurse(JsonValue json){
        ModelNode data = new ModelNode();
        data.id = json.require("id").asString();

        JsonValue trns = json.get("translation");
        data.translation = trns == null ? null : new Vec3(trns.getFloat(0), trns.getFloat(1), trns.getFloat(2));

        JsonValue rot = json.get("rotation");
        data.rotation = rot == null ? null : new Quat(rot.getFloat(0), rot.getFloat(1), rot.getFloat(2), rot.getFloat(3));

        JsonValue scl = json.get("scale");
        data.scale = scl == null ? null : new Vec3(scl.getFloat(0), scl.getFloat(1), scl.getFloat(2));

        String meshId = json.getString("mesh", null);
        if(meshId != null) data.meshId = meshId;

        JsonValue parts = json.get("parts");
        if(parts != null){
            data.parts = new ModelNodePart[parts.size];

            int i = 0;
            for(var material = parts.child; material != null; material = material.next, i++){
                ModelNodePart part = new ModelNodePart();

                part.materialId = material.require("materialid").asString();
                part.meshPartId = material.require("meshpartid").asString();

                data.parts[i] = part;
            }
        }

        JsonValue children = json.get("children");
        if(children != null){
            data.children = new ModelNode[children.size];

            int i = 0;
            for(var child = children.child; child != null; child = child.next, i++){
                data.children[i] = parseNodesRecurse(child);
            }
        }

        return data;
    }

    protected void parseAnimations(ModelData model, JsonValue json){
        JsonValue animations = json.get("animations");
        if(animations == null) return;

        model.animations.ensureCapacity(animations.size);
        for(var anim = animations.child; anim != null; anim = anim.next){
            JsonValue nodes = anim.get("bones");
            if(nodes == null) continue;

            ModelAnimation animation = new ModelAnimation();
            model.animations.add(animation);

            animation.nodeAnimations.ensureCapacity(nodes.size);
            animation.id = anim.getString("id");
            for(var node = nodes.child; node != null; node = node.next){
                ModelNodeAnimation nodeAnim = new ModelNodeAnimation();
                animation.nodeAnimations.add(nodeAnim);
                nodeAnim.nodeId = node.getString("boneId");

                JsonValue keyframes = node.get("keyframes");
                if(keyframes != null && keyframes.isArray()){
                    for(var keyframe = keyframes.child; keyframe != null; keyframe = keyframe.next){
                        float keytime = keyframe.getFloat("keytime", 0f) / (100f / 3f);

                        JsonValue translation = keyframe.get("translation");
                        if(translation != null && translation.size == 3){
                            if(nodeAnim.translation == null) nodeAnim.translation = new Seq<>();

                            ModelNodeKeyframe<Vec3> tkf = new ModelNodeKeyframe<>();
                            tkf.keytime = keytime;
                            tkf.value = new Vec3(
                                translation.getFloat(0),
                                translation.getFloat(1),
                                translation.getFloat(2)
                            );

                            nodeAnim.translation.add(tkf);
                        }

                        JsonValue rotation = keyframe.get("rotation");
                        if(rotation != null && rotation.size == 4){
                            if(nodeAnim.rotation == null) nodeAnim.rotation = new Seq<>();

                            ModelNodeKeyframe<Quat> rkf = new ModelNodeKeyframe<>();
                            rkf.keytime = keytime;
                            rkf.value = new Quat(
                                rotation.getFloat(0),
                                rotation.getFloat(1),
                                rotation.getFloat(2),
                                rotation.getFloat(3)
                            );

                            nodeAnim.rotation.add(rkf);
                        }

                        JsonValue scale = keyframe.get("scale");
                        if(scale != null && scale.size == 3){
                            if(nodeAnim.scaling == null) nodeAnim.scaling = new Seq<>();

                            ModelNodeKeyframe<Vec3> skf = new ModelNodeKeyframe<>();
                            skf.keytime = keytime;
                            skf.value = new Vec3(
                                scale.getFloat(0),
                                scale.getFloat(1),
                                scale.getFloat(2)
                            );

                            nodeAnim.scaling.add(skf);
                        }
                    }
                }
            }
        }
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

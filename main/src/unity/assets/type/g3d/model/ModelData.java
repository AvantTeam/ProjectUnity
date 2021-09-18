package unity.assets.type.g3d.model;

import arc.struct.*;

public class ModelData{
    public String id;
    public final Seq<ModelMesh> meshes = new Seq<>();
    public final Seq<ModelMaterial> materials = new Seq<>();
    public final Seq<ModelNode> nodes = new Seq<>();
    public final Seq<ModelAnimation> animations = new Seq<>();
}

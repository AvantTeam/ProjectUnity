package unity.assets.type.g3d.model;

import arc.struct.*;

public class ModelData{
    public String id;
    public final short[] version = new short[2];
    public final Seq<ModelMesh> meshes = new Seq<>();
    public final Seq<ModelMaterial> materials = new Seq<>();
    public final Seq<ModelNode> nodes = new Seq<>();

    public void addMesh(ModelMesh mesh){
        for(ModelMesh other : meshes){
            if(other.id.equals(mesh.id)){
                throw new IllegalArgumentException("Mesh with id '" + other.id + "' already in model");
            }
        }

        meshes.add(mesh);
    }
}

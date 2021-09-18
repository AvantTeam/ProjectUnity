package unity.assets.type.g3d.model;

import arc.graphics.*;
import arc.struct.*;

public class ModelMaterial{
    public String id;

    public Color ambient;
    public Color diffuse;
    public Color specular;
    public Color emissive;
    public Color reflection;

    public float shininess;
    public float opacity = 1f;

    public Seq<ModelTexture> textures;
}

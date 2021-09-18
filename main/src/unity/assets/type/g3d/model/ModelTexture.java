package unity.assets.type.g3d.model;

import arc.math.geom.*;

public class ModelTexture{
    public final static int unknown = 0;
    public final static int none = 1;
    public final static int diffuse = 2;
    public final static int emissive = 3;
    public final static int ambient = 4;
    public final static int specular = 5;
    public final static int shininess = 6;
    public final static int normal = 7;
    public final static int bump = 8;
    public final static int transparency = 9;
    public final static int reflection = 10;

    public String id;
    public String fileName;
    public Vec2 uvTranslation;
    public Vec2 uvScaling;
    public int usage;
}

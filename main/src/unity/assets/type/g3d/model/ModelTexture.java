package unity.assets.type.g3d.model;

import arc.math.geom.*;

public class ModelTexture{
    public final static int usageUnknown = 0;
    public final static int usageNone = 1;
    public final static int usageDiffuse = 2;
    public final static int usageEmissive = 3;
    public final static int usageAmbient = 4;
    public final static int usageSpecular = 5;
    public final static int usageShininess = 6;
    public final static int usageNormal = 7;
    public final static int usageBump = 8;
    public final static int usageTransparency = 9;
    public final static int usageReflection = 10;

    public String id;
    public String fileName;
    public Vec2 uvTranslation;
    public Vec2 uvScaling;
    public int usage;
}

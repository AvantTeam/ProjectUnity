package unity.assets.type.g3d.model;

import arc.math.geom.*;
import arc.struct.*;

public class ModelNodeAnimation{
    public String nodeId;
    public Seq<ModelNodeKeyframe<Vec3>> translation;
    public Seq<ModelNodeKeyframe<Quat>> rotation;
    public Seq<ModelNodeKeyframe<Vec3>> scaling;
}

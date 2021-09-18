package unity.assets.type.g3d;

import arc.math.geom.*;
import arc.struct.*;

public class NodeAnimation{
    public Node node;

    public Seq<NodeKeyframe<Vec3>> translation = null;
    public Seq<NodeKeyframe<Quat>> rotation = null;
    public Seq<NodeKeyframe<Vec3>> scaling = null;
}

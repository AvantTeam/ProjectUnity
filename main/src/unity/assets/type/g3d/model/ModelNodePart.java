package unity.assets.type.g3d.model;

import arc.math.geom.*;
import arc.struct.*;

public class ModelNodePart{
    public String materialId;
    public String meshPartId;
    public OrderedMap<String, Mat3D> bones;
    public int[][] uvMapping;
}

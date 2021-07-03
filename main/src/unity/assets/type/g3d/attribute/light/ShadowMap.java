package unity.assets.type.g3d.attribute.light;

import arc.graphics.*;
import arc.math.geom.*;

public interface ShadowMap {
    Mat3D getProjViewTrans();

    Texture getDepthMap();
}

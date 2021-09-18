package unity.assets.type.g3d;

import arc.struct.*;

public class Animation{
    public String id;
    public float duration;

    public Seq<NodeAnimation> nodeAnimations = new Seq<>();
}

package unity.graphics;

import arc.graphics.*;

public class UnityBlending{
    public static Blending shadowRealm = new Blending(Gl.srcAlphaSaturate, Gl.oneMinusSrcAlpha),
    invert = new Blending(Gl.oneMinusDstColor, Gl.oneMinusSrcColor),
    multiply = new Blending(Gl.dstColor, Gl.oneMinusSrcAlpha);
}

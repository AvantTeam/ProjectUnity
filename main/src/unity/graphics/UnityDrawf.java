package unity.graphics;

import mindustry.graphics.Drawf;

public class UnityDrawf{
    public static void spark(float x, float y, float w, float h, float r){
        Drawf.tri(x, y, w, h, r);
        //is this order imporant?
        Drawf.tri(x, y, w, h, r + 180f);
        Drawf.tri(x, y, w, h, r + 90f);
        Drawf.tri(x, y, w, h, r + 270f);
    }
}

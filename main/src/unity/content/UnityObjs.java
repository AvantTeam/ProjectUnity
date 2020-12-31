package unity.content;

import mindustry.ctype.*;
import mindustry.graphics.*;
import unity.graphics.*;
import unity.util.*;

public class UnityObjs implements ContentList{
    static WavefrontObject cube;
    @Override
    public void load(){
        cube = new WavefrontObject("cube"){{
            size = 4f;
            lightColor = UnityPal.advance;
            shadeColor = UnityPal.advanceDark;
            drawLayer = Layer.turret;
        }};
    }
}

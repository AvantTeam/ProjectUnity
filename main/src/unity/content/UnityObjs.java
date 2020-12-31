package unity.content;

import arc.graphics.*;
import mindustry.ctype.*;
import mindustry.graphics.*;
import unity.util.*;

public class UnityObjs implements ContentList{
    static WavefrontObject cube;
    @Override
    public void load(){
        cube = new WavefrontObject("cube"){{
            size = 4f;
            lightColor = Pal.lancerLaser;
            shadeColor = Color.valueOf("59a7ff");
            drawLayer = Layer.turret;
        }};
    }
}

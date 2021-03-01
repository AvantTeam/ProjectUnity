package unity.ai.kami;

import arc.*;
import arc.graphics.g2d.*;

public class KamiRegions{
    public static TextureRegion[] okuu = new TextureRegion[3];

    public static void load(){
        for(int i = 0; i < 3; i++){
            okuu[i] = Core.atlas.find("unity-okuu-" + i);
        }
    }
}

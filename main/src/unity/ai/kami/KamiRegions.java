package unity.ai.kami;

import arc.*;
import arc.graphics.g2d.*;

public class KamiRegions{
    public static TextureRegion[] okuu = new TextureRegion[3], marisaBroom = new TextureRegion[8], byakurenScroll = new TextureRegion[16];

    public static void load(){
        for(int i = 0; i < 16; i++){
            if(i <= 2) okuu[i] = Core.atlas.find("unity-okuu-" + i);
            if(i <= 7) marisaBroom[i] = Core.atlas.find("unity-marisa-broom-" + i);
            byakurenScroll[i] = Core.atlas.find("unity-byakuren-scroll-" + i);
        }
    }
}

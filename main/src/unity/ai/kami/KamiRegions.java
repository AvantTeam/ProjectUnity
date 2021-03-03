package unity.ai.kami;

import arc.*;
import arc.graphics.g2d.*;

public class KamiRegions{
    public static TextureRegion[] okuu = new TextureRegion[3], marisaBroom = new TextureRegion[8];

    public static void load(){
        for(int i = 0; i < 8; i++){
            if(i <= 2){
                okuu[i] = Core.atlas.find("unity-okuu-" + i);
            }

            marisaBroom[i] = Core.atlas.find("unity-marisa-broom-" + i);
        }
    }
}

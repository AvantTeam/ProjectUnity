package unity.type;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.type.*;
import mindustry.ui.*;
import unity.util.*;

public class AnimatedItem extends Item{
    public int animSize = 5;
    public float animDuration = 5f;
    public int animTrns = 0;

    protected TextureRegion[][] animRegions = new TextureRegion[Cicon.all.length][];

    public AnimatedItem(String name, Color color){
        super(name, color);
    }

    @Override
    public void load(){
        super.load();

        int n = animSize * (1 + animTrns);
        for(Cicon icon : Cicon.all){
            int ord = icon.ordinal();

            TextureRegion[] spriteArr = new TextureRegion[animSize];
            for(int i = 1; i <= animSize; i++){
                spriteArr[i - 1] = Core.atlas.find(
                    name + i + "-" + icon.name(),
                    Core.atlas.find(name + i + "-full",
                    Core.atlas.find(name + i,
                    Core.atlas.find(name + "1")))
                );
            }

            animRegions[ord] = new TextureRegion[n];
            for(int i = 0; i < animSize; i++){
                if(animTrns <= 0){
                    animRegions[ord][i] = spriteArr[i];
                }else{
                    animRegions[ord][i * (animTrns + 1)] = spriteArr[i];
                    for(int j = 1; j <= animTrns; j++){
                        float f = (float)j / (animTrns + 1);
                        animRegions[ord][i * (animTrns + 1) + j] = Utils.blendSprites(
                            spriteArr[i],
                            spriteArr[(i + 1) % animSize],
                            f,
                            name + i
                        );
                    }
                }
            }
        }

        Events.run(Trigger.update, this::update);
    }

    public void update(){
        for(Cicon icon : Cicon.all){
            TextureRegion reg = cicons[icon.ordinal()];
            if(reg == null) continue;

            TextureRegion[] regs = animRegions[icon.ordinal()];
            int i = (int)(Time.globalTime / animDuration) % regs.length;

            reg.set(regs[i]);
        }
    }
}

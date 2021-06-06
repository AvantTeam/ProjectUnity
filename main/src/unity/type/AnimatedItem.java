package unity.type;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.type.*;
import unity.mod.*;
import unity.util.*;

/**
 * @author GlennFolker
 * @author sk7725
 */
public class AnimatedItem extends Item{
    /** Number of frames */
    public int frames = 5;
    /** Number of generated transition frames between each frame */
    public int transitionFrames = 0;
    /** Ticks between each frame */
    public float frameTime = 5f;

    protected TextureRegion[] animRegions;

    public AnimatedItem(String name, Color color){
        super(name, color);
    }

    @Override
    public void load(){
        int n = frames * (1 + transitionFrames);

        TextureRegion[] spriteArr = new TextureRegion[frames];
        for(int i = 1; i <= frames; i++){
            spriteArr[i - 1] = Core.atlas.find(
                name + i + "-full",
                Core.atlas.find(name + i,
                Core.atlas.find(name + "1"))
            );
        }

        animRegions = new TextureRegion[n];
        for(int i = 0; i < frames; i++){
            if(transitionFrames <= 0){
                animRegions[i] = spriteArr[i];
            }else{
                animRegions[i * (transitionFrames + 1)] = spriteArr[i];
                for(int j = 1; j <= transitionFrames; j++){
                    float f = (float)j / (transitionFrames + 1);
                    animRegions[i * (transitionFrames + 1) + j] = Utils.blendSprites(
                        spriteArr[i],
                        spriteArr[(i + 1) % frames],
                        f,
                        name + i
                    );
                }
            }
        }

        Triggers.listen(Trigger.update, this::update);
    }

    public void update(){
        int i = (int)(Time.globalTime / frameTime) % animRegions.length;

        fullIcon.set(animRegions[i]);
        uiIcon.set(animRegions[i]);
    }
}

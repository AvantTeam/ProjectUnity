package unity.type;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.type.*;
import mindustry.ui.*;

public class AnimatedItem extends Item{
    public TextureRegion[][] animCicons;
    public int animSize = 5;
    public float animDuration = 3f;

    public AnimatedItem(String name, Color color){
        super(name, color);
    }

    @Override
    public void init(){
        super.init();

        animCicons = new TextureRegion[animSize][];
        for(int i = 0; i < animSize; i++){
            animCicons[i] = new TextureRegion[Cicon.all.length];
        }
    }

    @Override
    public TextureRegion icon(Cicon icon){
        return icon(icon, (int)(Time.globalTime / animDuration) % animSize);
    }

    public TextureRegion icon(Cicon icon, int current){
        int i = current + 1;
        if(animCicons[current][icon.ordinal()] == null){
            animCicons[current][icon.ordinal()] =
                Core.atlas.find(name + i + "-" + icon.name(),
                Core.atlas.find(name + i + "-full",
                Core.atlas.find(name + i,
                Core.atlas.find(name))));
        }

        return animCicons[current][icon.ordinal()];
    }
}

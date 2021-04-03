package unity.type;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.type.*;
import mindustry.ui.*;

@SuppressWarnings("unchecked")
public class AnimatedItem extends Item{
    public int animSize = 5;
    public float animDuration = 5f;

    public final Cons<Trigger> updater = e -> update();

    public AnimatedItem(String name, Color color){
        super(name, color);
    }

    @Override
    public void init(){
        super.init();
        Events.on((Class<Trigger>)Trigger.update.getClass(), updater);
    }

    public void update(){
        for(Cicon icon : Cicon.all){
            TextureRegion reg = cicons[icon.ordinal()];
            if(reg == null) continue;

            int i = ((int)(Time.globalTime / animDuration) % animSize) + 1;
            reg.set(
                Core.atlas.find(name + i + "-" + icon.name(),
                Core.atlas.find(name + i + "-full",
                Core.atlas.find(name + i,
                Core.atlas.find(name))))
            );
        }
    }
}

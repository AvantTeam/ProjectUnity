package unity.content;

import arc.func.Cons;
import mindustry.ctype.*;
import mindustry.world.blocks.environment.Floor;

import static mindustry.content.Blocks.*;

@SuppressWarnings("unchecked")
public class OverWriter implements ContentList{
    public <T extends UnlockableContent> void forceOverWrite(UnlockableContent target, Cons<T> setter){
        setter.get((T)target);
    }

    @Override
    public void load(){
        forceOverWrite(basalt, (Floor t) -> {
            t.itemDrop = UnityItems.stone;
            t.playerUnmineable = true;
        });

        forceOverWrite(craters, (Floor t) -> {
            t.itemDrop = UnityItems.stone;
            t.playerUnmineable = true;
        });

        forceOverWrite(dacite, (Floor t) -> {
            t.itemDrop = UnityItems.stone;
            t.playerUnmineable = true;
        });

        forceOverWrite(stone, (Floor t) -> {
            t.itemDrop = UnityItems.stone;
            t.playerUnmineable = true;
        });
    }
}

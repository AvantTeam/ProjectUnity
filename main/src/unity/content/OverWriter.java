package unity.content;

import arc.func.Cons;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.world.blocks.environment.Floor;
import unity.logic.*;

import static mindustry.content.Blocks.*;

@SuppressWarnings("unchecked")
public class OverWriter implements ContentList{
    public <T extends UnlockableContent> void forceOverWrite(UnlockableContent target, Cons<T> setter){
        setter.get((T)target);
    }

    @Override
    public void load(){
        //region contents

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

        //endregion
        //region statements

        LAssembler.customParsers.put("expsensor", (args) -> new ExpSensorStatement());
        LogicIO.allStatements.add(ExpSensorStatement::new);

        //endregion
    }
}

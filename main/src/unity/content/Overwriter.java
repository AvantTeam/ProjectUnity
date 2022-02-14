package unity.content;

import arc.func.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.units.*;
import mindustry.world.blocks.units.UnitFactory.*;
import unity.logic.*;

import static mindustry.content.Blocks.*;
import static unity.content.UnityUnitTypes.*;
import static unity.content.units.MonolithUnitTypes.*;

/**
 * <p> An overwriter class designed to easily modify vanilla contents. This has to be used with <em>huge
 * responsibility</em>, we do not want to break other mods or even the vanilla Mindustry itself.
 */
@SuppressWarnings("unchecked")
public class Overwriter{
    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter){
        setter.get((T)target);
    }

    public static void load(){
        //region contents

        //TODO (no screaming) let's just get these over with.
        overwrite(basalt, (Floor t) -> {
            t.itemDrop = UnityItems.stone;
            t.playerUnmineable = true;
        });

        overwrite(craters, (Floor t) -> {
            t.itemDrop = UnityItems.stone;
            t.playerUnmineable = true;
        });

        overwrite(dacite, (Floor t) -> {
            t.itemDrop = UnityItems.stone;
            t.playerUnmineable = true;
        });

        overwrite(stone, (Floor t) -> {
            t.itemDrop = UnityItems.stone;
            t.playerUnmineable = true;
        });

        overwrite(airFactory, (UnitFactory f) -> f.plans.add(
            new UnitPlan(caelifera, 60f * 25, ItemStack.with(Items.silicon, 15, Items.titanium, 25))
        ));

        overwrite(navalFactory, (UnitFactory f) -> f.plans.add(
            new UnitPlan(amphibiNaval, 60f * 25f, ItemStack.with(Items.silicon, 15, Items.titanium, 25))
        ));

        overwrite(additiveReconstructor, (Reconstructor r) -> r.upgrades.add(
            //global
            new UnitType[]{caelifera, schistocerca},
            new UnitType[]{amphibiNaval, craberNaval},

            //monolith
            new UnitType[]{stele, pedestal}
        ));

        overwrite(multiplicativeReconstructor, (Reconstructor r) -> r.upgrades.add(
            //global
            new UnitType[]{schistocerca, anthophila},

            //monolith
            new UnitType[]{pedestal, pilaster}
        ));

        overwrite(exponentialReconstructor, (Reconstructor r) -> r.upgrades.add(
            //global
            new UnitType[]{anthophila, vespula},

            //monolith
            new UnitType[]{pilaster, pylon}
        ));

        overwrite(tetrativeReconstructor, (Reconstructor r) -> r.upgrades.add(
            //global
            new UnitType[]{vespula, lepidoptera},

            //monolith
            new UnitType[]{pylon, monument}
        ));

        //endregion
        //region statements

        LAssembler.customParsers.put("expsensor", args -> new ExpSensorStatement());
        LogicIO.allStatements.add(ExpSensorStatement::new);

        //endregion
    }
}

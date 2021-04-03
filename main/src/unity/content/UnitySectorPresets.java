package unity.content;

import mindustry.type.*;
import mindustry.content.*;
import mindustry.ctype.*;
import unity.annotations.Annotations.*;
import unity.type.sector.*;
import unity.type.sector.SectorObjective.*;

import static mindustry.Vars.*;
import static unity.content.UnityPlanets.*;

public class UnitySectorPresets implements ContentList{
    public static SectorPreset imberlands;
    public static @FactionDef("monolith") SectorPreset accretion;

    @Override
    public void load(){
        /*imberlands = new SectorPreset("imberlands", electrode, 30){{
            alwaysUnlocked = true;
            captureWave = 15;
        }};*/

        accretion = new ScriptedSector("accretion", megalith, 200){{
            alwaysUnlocked = true;
            addStartingItems = true;
            difficulty = 3f;
            captureWave = 15;

            objectives.addAll(
                new ResourceAmountObjective(ItemStack.with(
                    Items.copper, 6000,
                    Items.lead, 5600,
                    Items.silicon, 3200,
                    UnityItems.monolite, 4800
                ), this, 1, (ResourceAmountObjective objective) -> {
                    int win = (state.wave / 5 + 1) * 5;
                    state.rules.winWave = state.getSector().info.winWave = win;
                }).init((ResourceAmountObjective objective) -> {
                    state.rules.winWave = state.getSector().info.winWave = -1;
                })
            );
        }};
    }
}

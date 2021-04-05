package unity.content;

import arc.*;
import arc.func.*;
import mindustry.type.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.type.GlobalObjective;
import unity.type.sector.*;
import unity.type.sector.SectorObjective.*;

import static mindustry.Vars.*;
import static unity.content.UnityPlanets.*;

@SuppressWarnings("unchecked")
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

            Cons<Trigger>[] set = new Cons[1];
            SectorObjective[] res = new SectorObjective[1];

            set[0] = e -> {
                if(state.getSector() != null){
                    if(!GlobalObjective.reached(GlobalObjective.sectorAccretionComplete)){
                        state.rules.winWave = -1;
                        if(state.getSector() != null){
                            state.getSector().info.winWave = -1;
                        }
                    }else{
                        res[0].stop();
                    }
                }else{
                    state.rules.winWave = -1;
                }

                Events.remove((Class<Trigger>)Trigger.newGame.getClass(), set[0]);
            };

            objectives.addAll(
                res[0] = new ResourceAmountObjective(ItemStack.with(
                    Items.copper, 6000,
                    Items.lead, 5600,
                    Items.silicon, 3200,
                    UnityItems.monolite, 4800
                ), this, (ResourceAmountObjective objective) -> {
                    int win = Math.max((state.wave / 5 + 1) * 5, captureWave);

                    state.rules.winWave = Math.max(win, win);
                    if(state.getSector() != null){
                        state.getSector().info.winWave = win;
                        GlobalObjective.fire(GlobalObjective.sectorAccretionComplete);
                    }

                    Sounds.unlock.play();
                }).init((ResourceAmountObjective objective) -> {
                    Events.on((Class<Trigger>)Trigger.newGame.getClass(), set[0]);
                })
            );
        }};
    }
}

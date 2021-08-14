package unity.content;

import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.cinematic.*;
import unity.type.*;
import unity.type.sector.*;
import unity.type.sector.objectives.*;

import static mindustry.Vars.*;
import static unity.content.UnityPlanets.*;

public class UnitySectorPresets implements ContentList{
    public static @FactionDef("monolith") SectorPreset
    accretion, salvagedLab;

    @Override
    public void load(){
        accretion = new ScriptedSector("accretion", megalith, 200){{
            alwaysUnlocked = true;
            addStartingItems = true;
            difficulty = 3f;
            captureWave = 15;
        }};

        salvagedLab = new ScriptedSector("salvaged-laboratory", megalith, 100){{
            difficulty = 4f;
            captureWave = 30;
        }};
    }
}

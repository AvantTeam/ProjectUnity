package unity.content;

import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.map.*;

import static unity.content.UnityPlanets.*;

public class UnitySectorPresets{
    public static @FactionDef("monolith") SectorPreset
    accretion, salvagedLab;

    public static void load(){
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

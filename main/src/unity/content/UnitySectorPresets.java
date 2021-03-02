package unity.content;

import mindustry.type.*;
import mindustry.ctype.*;
import unity.annotations.Annotations.*;

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
        accretion = new SectorPreset("accretion", megalith, 30){{
            alwaysUnlocked = true;
            captureWave = 15;
        }};
    }

}

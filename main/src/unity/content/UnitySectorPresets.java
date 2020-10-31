package unity.content;

import mindustry.type.SectorPreset;
import mindustry.ctype.ContentList;

import static unity.content.UnityPlanets.*;

public class UnitySectorPresets implements ContentList{
    public static SectorPreset imberlands, accretion;

    @Override
    public void load(){
        /*imberlands = new SectorPreset("imberlands", electrode, 30){{
            alwaysUnlocked = true;
            captureWave = 15;
        }};*/
        accretion = new SectorPreset("accretion", megalith, 30){{
            alwaysUnlocked = true;
            captureWave = 25;
        }};
    }

}

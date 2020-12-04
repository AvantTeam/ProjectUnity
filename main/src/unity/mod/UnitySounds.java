package unity.mod;

import arc.audio.*;

import static mindustry.Vars.mods;

public class UnitySounds{
    public static Sound
    endgameSmallShoot,
    beamIntenseHighpitchTone,
    extinctionShoot;

    private static Sound loadSound(String name){
        return mods.getScripts().loadSound(name);
    }

    static{
        extinctionShoot = loadSound("dark/extinction-shoot");
        endgameSmallShoot = loadSound("end/endgame-small-shoot");
        beamIntenseHighpitchTone = loadSound("beam-intense-highpitch-tone");
    }
}

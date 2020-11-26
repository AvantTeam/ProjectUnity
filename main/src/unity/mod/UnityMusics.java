package unity.mod;

import arc.Events;
import arc.audio.Music;
import arc.struct.Seq;
import mindustry.ctype.ContentType;
import mindustry.game.EventType.SectorLaunchEvent;

import static mindustry.Vars.*;

public class UnityMusics{
    private static Music monolithDark;
    private static Seq<Music> ambientMusic, darkMusic, bossMusic;

    private static Music loadMusic(String name){
        return mods.getScripts().loadMusic(name);
    }

    public static void load(){
        ambientMusic = control.sound.ambientMusic;
        darkMusic = control.sound.darkMusic;
        bossMusic = control.sound.bossMusic;
        monolithDark = loadMusic("monolith-dark");
        Events.on(SectorLaunchEvent.class, e -> {
            if(e.sector.planet == content.getByName(ContentType.planet, "unity-megalith")){
                if(darkMusic.contains(monolithDark)) darkMusic.add(monolithDark);
            }else if(darkMusic.contains(monolithDark)) darkMusic.remove(monolithDark);
        });
    }
}

package unity.mod;

import arc.Events;
import arc.audio.Music;
import arc.struct.Seq;
import mindustry.ctype.ContentType;
import mindustry.game.EventType.SectorLaunchEvent;

import static mindustry.Vars.*;

public class UnityMusics{
    private static Seq<Music> ambientMusic, darkMusic, bossMusic, monolithDarkMusics;

    private static Music loadMusic(String name){
        return mods.getScripts().loadMusic(name);
    }

    public static void load(){
        ambientMusic = control.sound.ambientMusic;
        darkMusic = control.sound.darkMusic;
        bossMusic = control.sound.bossMusic;
        monolithDarkMusics = Seq.with(loadMusic("monolith-dark1"));
        Events.on(SectorLaunchEvent.class, e -> {
            if(e.sector.planet == content.getByName(ContentType.planet, "unity-megalith")){
                monolithDarkMusics.each(music -> {
                    if(darkMusic.contains(music)) darkMusic.add(music);
                });
            }else{
                monolithDarkMusics.each(music -> {
                    if(darkMusic.contains(music)) darkMusic.remove(music);
                });
            }
        });
    }
}

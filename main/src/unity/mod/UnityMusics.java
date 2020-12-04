package unity.mod;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.struct.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
public class UnityMusics{
    private static Seq<Music> ambientMusic, darkMusic, bossMusic, monolithDarkMusics;
    private static Seq<Cons<SectorLaunchEvent>> listeners = new Seq<>();

    private static Music loadMusic(String name){
        return mods.getScripts().loadMusic(name);
    }

    private static void addMusic(Seq<Music> category, String planet, Music... musics){
        if(planet.equals("global")){
            for(Music music : musics){
                if(!category.contains(music)) category.add(music);
            }
            return;
        }
        listeners.add(e -> {
            if(e.sector.planet == content.getByName(ContentType.planet, "unity" + planet)){
                for(Music music : musics){
                    if(!category.contains(music)) category.add(music);
                }
            }else{
                for(Music music : musics){
                    if(category.contains(music)) category.remove(music);
                }
            }
        });
    }

    //is this loaded well even if Core.assets are not loaded?
    static{
        ambientMusic = control.sound.ambientMusic;
        darkMusic = control.sound.darkMusic;
        bossMusic = control.sound.bossMusic;
        addMusic(darkMusic, "megalith", loadMusic("monolith-dark1"), loadMusic("monolith-dark2"));

        Events.on(SectorLaunchEvent.class, e -> listeners.each(cons -> cons.get(e)));
    }
}

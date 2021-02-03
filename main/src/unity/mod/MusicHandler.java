package unity.mod;

import arc.*;
import arc.audio.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.type.*;
import unity.gen.*;

public class MusicHandler implements ApplicationListener{
    private ObjectMap<String, MusicLoopData> loopDatas = new ObjectMap<>();
    private MusicLoopData currentData = null;
    private Music currentMusic = null;
    private boolean introPassed = false;

    public void setup(){
        Events.on(SectorLaunchEvent.class, e -> {
            Planet p = e.sector.planet;

            for(Faction fac : Faction.all){
                if(fac.equals(FactionMeta.map(p))){
                    FactionMeta.getByFaction(fac, Music.class).each(music -> {
                        Seq<Music> category = FactionMeta.getMusicCategory(music);
                        if(!category.contains(music)){
                            category.add(music);
                        }
                    });
                }else{
                    FactionMeta.getByFaction(fac, Music.class).each(music -> {
                        Seq<Music> category = FactionMeta.getMusicCategory(music);
                        if(category.contains(music)){
                            category.remove(music);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void update(){
        if(currentData != null){
            if(currentData.intro.getVolume() < 1f){
                currentData.intro.setVolume(1f);
            }
            if(currentData.loop.getVolume() < 1f){
                currentData.loop.setVolume(1f);
            }

            if(currentMusic != currentData.intro && !introPassed){
                currentMusic = currentData.intro;
                currentMusic.play();
            }

            if(currentMusic == currentData.intro && !currentMusic.isPlaying()){
                currentMusic = currentData.loop;
                currentMusic.play();
                currentMusic.setLooping(true);

                introPassed = true;
            }
        }else if(currentMusic != null){
            currentMusic.setVolume(currentMusic.getVolume() - Time.delta / 120f);
            if(currentMusic.getVolume() < 0.01f){
                currentMusic.stop();
                currentMusic = null;

                introPassed = false;
            }
        }
    }

    public void registerLoop(String name, Music intro, Music loop){
        loopDatas.put(name, new MusicLoopData(intro, loop));
    }

    public boolean isPlaying(){
        return currentMusic != null && currentMusic.isPlaying();
    }

    public void play(String name){
        currentData = loopDatas.get(name);
    }

    public void stop(String name){
        if(currentData == loopDatas.get(name)){
            currentData = null;
        }
    }

    public MusicLoopData getCurrentData(){
        return currentData;
    }

    public Music getCurrentMusic(){
        return currentMusic;
    }

    class MusicLoopData{
        final Music intro;
        final Music loop;

        MusicLoopData(Music intro, Music loop){
            this.intro = intro;
            this.loop = loop;
        }
    }
}

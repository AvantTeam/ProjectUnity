package unity.mod;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.type.*;
import unity.gen.*;

import static mindustry.Vars.*;

public class MusicHandler implements ApplicationListener{
    private ObjectMap<String, MusicLoopData> loopDatas = new ObjectMap<>();
    private MusicLoopData currentData = null;
    private Music currentMusic = null;
    private Boolp currentPredicate;

    private boolean introPassed = false;

    public void setup(){
        /*if(netClient != null){
            netClient.addPacketHandler("unity.bossmusic.play", this::play);

            netClient.addPacketHandler("unity.bossmusic.stop", this::stop);
        }else{
            Log.warn("netClient is null");
        }

        if(netServer != null){
            netServer.addPacketHandler("unity.bossmusic.play", (p, name) -> {
                Log.info("Sending packet to client");
                Call.clientPacketReliable("unity.bossmusic.play", name);
            });

            netServer.addPacketHandler("unity.bossmusic.stop", (p, name) -> {
                Log.info("Sending packet to client");
                Call.clientPacketReliable("unity.bossmusic.stop", name);
            });
        }else{
            Log.warn("netServer is null");
        }*/

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
        if(headless){
            currentData = null;
            if(currentMusic != null) currentMusic.stop();
            currentMusic = null;

            return;
        }

        if(currentData != null && !currentPredicate.get()){
            stop(loopDatas.findKey(currentData, false));
        }

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
            currentMusic.setVolume(currentMusic.getVolume() - Time.delta / 150f);
            if(currentMusic.getVolume() < 0.01f){
                currentMusic.stop();
                currentMusic = null;

                introPassed = false;
            }
        }

        if(currentMusic != null){
            control.sound.stop();
            currentMusic.setVolume(currentMusic.getVolume() * (Core.settings.getInt("musicvol", 100) / 100f));
        }
    }

    @Override
    public void dispose(){
        loopDatas.clear();
        currentData = null;

        if(currentMusic != null) currentMusic.stop();
        currentMusic = null;
    }

    public void registerLoop(String name, Music loop){
        registerLoop(name, loop, loop);
    }

    public void registerLoop(String name, Music intro, Music loop){
        loopDatas.put(name, new MusicLoopData(intro, loop));
    }

    public boolean isPlaying(){
        return currentMusic != null && currentMusic.isPlaying();
    }

    public void play(String name){
        play(name, null);
    }

    public void play(String name, Boolp predicate){
        currentData = loopDatas.get(name);
        currentPredicate = predicate == null ? () -> (state.isPlaying() || state.isPaused()) : predicate;

        /*if(net.server()){
            Call.serverPacketReliable("unity.bossmusic.play", name);
        }*/
    }

    public void stop(String name){
        if(currentData == loopDatas.get(name)){
            currentData = null;
        }

        /*if(net.server()){
            Call.serverPacketReliable("unity.bossmusic.stop", name);
        }*/
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

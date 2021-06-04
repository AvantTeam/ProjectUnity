package unity.mod;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import mindustry.core.GameState.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.gen.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
public class MusicHandler implements ApplicationListener{
    private final ObjectMap<String, MusicLoopData> loopDatas = new ObjectMap<>();
    private MusicLoopData currentData = null;
    private boolean introPassed = false;

    private final Seq<Music> oldAmbient = new Seq<>();
    private final Seq<Music> oldDark = new Seq<>();
    private final Seq<Music> oldBoss = new Seq<>();

    private Boolp currentPredicate = () -> state != null && state.isPlaying();
    private @Nullable Music currentMusic;

    private Music originalPlanet;
    private ObjectMap<Faction, Music> planetMusics = new ObjectMap<>();

    public void setup(){
        Events.on(ClientLoadEvent.class, e -> {
            originalPlanet = Musics.launch;
            planetMusics = ObjectMap.of(
                Faction.monolith, UnityMusics.soulsOfTheFallen
            );
        });

        Events.on(StateChangeEvent.class, e -> {
            if(e.to == State.playing){
                if(state.map != null && state.map.mod != null && state.map.mod.name.equals("unity")){
                    SectorPreset sec = content.sectors().find(s -> s.generator.map.name().equals(state.map.name()));
                    if(sec != null){
                        Faction fac = FactionMeta.map(sec);
                        if(fac == null) return;

                        Seq<Music> newMusics = FactionMeta.getByFaction(fac, Music.class);
                        Seq<Music> newAmbient = newMusics.select(m -> FactionMeta.getMusicCategory(m) == control.sound.ambientMusic);
                        Seq<Music> newDark = newMusics.select(m -> FactionMeta.getMusicCategory(m) == control.sound.darkMusic);
                        Seq<Music> newBoss = newMusics.select(m -> FactionMeta.getMusicCategory(m) == control.sound.bossMusic);

                        if(!newAmbient.isEmpty()){
                            oldAmbient.addAll(control.sound.ambientMusic);

                            control.sound.ambientMusic.clear();
                            control.sound.ambientMusic.addAll(newAmbient);
                        }

                        if(!newDark.isEmpty()){
                            oldDark.addAll(control.sound.darkMusic);

                            control.sound.darkMusic.clear();
                            control.sound.darkMusic.addAll(newDark);
                        }

                        if(!newBoss.isEmpty()){
                            oldBoss.addAll(control.sound.bossMusic);

                            control.sound.bossMusic.clear();
                            control.sound.bossMusic.addAll(newBoss);
                        }
                    }
                }else{
                    control.sound.ambientMusic.addAll(oldAmbient).distinct();
                    control.sound.darkMusic.addAll(oldDark).distinct();
                    control.sound.bossMusic.addAll(oldBoss).distinct();

                    oldAmbient.clear();
                    oldDark.clear();
                    oldBoss.clear();
                }
            }
        });
    }

    @Override
    public void update(){
        if(!headless && ui.planet.isShown() && state.isMenu()){
            Planet planet = ui.planet.planets.planet;

            Faction fac = FactionMeta.map(planet);
            if(fac != null){
                Musics.launch = planetMusics.get(fac, originalPlanet);
            }else{
                Musics.launch = originalPlanet;
            }
        }

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

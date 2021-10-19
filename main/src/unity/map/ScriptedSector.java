package unity.map;

import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.core.GameState.*;
import mindustry.game.EventType.*;
import mindustry.io.*;
import mindustry.type.*;
import unity.map.cinematic.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
public class ScriptedSector extends SectorPreset{
    public final Cinematics cinematic = new Cinematics(this::valid);

    public ScriptedSector(String name, Planet planet, int sector){
        super(name, planet, sector);

        Events.on(SaveWriteEvent.class, e -> {
            if(cinematic.bound() && valid()){
                StringMap map = new StringMap();
                cinematic.save(map);

                state.rules.tags.put(name + "-cinematic", JsonIO.json.toJson(map, StringMap.class, String.class));
            }
        });

        Events.on(SaveLoadEvent.class, e -> cinematic.load(JsonIO.json.fromJson(
            StringMap.class, String.class,
            state.rules.tags.get(name + "-cinematic", JsonIO.json.toJson(StringMap.of(
                "object-tags", generator.map.tags.get("object-tags", "[]")
            ), StringMap.class, String.class))
        )));

        Events.on(StateChangeEvent.class, e -> {
            if(!cinematic.bound() && e.to == State.playing && valid()){
                cinematic.bind();
            }
        });
    }

    public boolean valid(){
        return state.hasSector()
        ?   state.getSector().id == sector.id
        :   (state.map != null && (
            state.map.mod != null && state.map.mod.name.equals("unity") &&
            (state.map.name().equals(generator.map.name()) || state.map.name().equals(name))
        ));
    }

    @Override
    public void init(){
        super.init();
        Core.app.post(() -> {
            try{
                initNodes();
            }catch(Throwable t){
                if(headless){
                    Log.err(t);
                }else{
                    Events.on(ClientLoadEvent.class, e -> Time.runTask(6f, () -> ui.showException("Failed to load cinematic metadata of '" + localizedName + "'", t)));
                }
            }
        });
    }

    public void initNodes(){
        cinematic.setNodes(JsonIO.json.fromJson(Seq.class, StoryNode.class, generator.map.tags.get("nodes", "[]")));
    }

    public void initTags(){
        cinematic.clearTags();
        cinematic.loadTags(JsonIO.json.fromJson(Seq.class, String.class, generator.map.tags.get("object-tags", "[]")));
    }
}

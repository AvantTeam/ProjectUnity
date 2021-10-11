package unity.map;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import mindustry.core.GameState.*;
import mindustry.game.EventType.*;
import mindustry.io.*;
import mindustry.type.*;
import unity.map.cinematic.*;
import unity.map.objectives.*;
import unity.mod.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
public class ScriptedSector extends SectorPreset{
    public final Seq<StoryNode> nodes = new Seq<>();

    protected boolean added = false;
    protected final Cons<Trigger> updater = Triggers.cons(this::update);
    protected final Cons<Trigger> drawer = Triggers.cons(this::draw);

    public ScriptedSector(String name, Planet planet, int sector){
        super(name, planet, sector);

        Events.on(SaveWriteEvent.class, e -> saveState());
        Events.on(SaveLoadEvent.class, e -> loadState());

        Events.on(StateChangeEvent.class, e -> {
            if(!added && e.to == State.playing && valid()){
                added = true;
                Triggers.listen(Trigger.update, updater);
                Triggers.listen(Trigger.drawOver, drawer);

                nodes.each(StoryNode::init);
            }
        });
    }

    public void update(){
        if(!valid() && added){
            added = false;
            Triggers.detach(Trigger.update, updater);
            Triggers.detach(Trigger.drawOver, drawer);

            return;
        }

        for(var node : nodes) if(node.shouldUpdate()) node.update();
    }

    public void draw(){
        nodes.each(StoryNode::draw);
    }

    public boolean valid(){
        return state.hasSector()
        ?   state.getSector().id == sector.id
        :   (state.map != null && (
            state.map.mod != null && state.map.mod.name.equals("unity") &&
            (state.map.name().equals(generator.map.name()) || state.map.name().equals(name))
        ));
    }

    public void saveState(){
        if(!added || !valid()) return;

        var map = new StringMap();
        for(var node : nodes){
            var child = new StringMap();
            node.save(child);

            map.put(node.name, JsonIO.json.toJson(child, StringMap.class, String.class));
        }

        Log.info(map);
        state.rules.tags.put(name + "-nodes", JsonIO.json.toJson(map, StringMap.class, String.class));
    }

    public void loadState(){
        var map = JsonIO.json.fromJson(StringMap.class, String.class, state.rules.tags.get(name + "-nodes", "{}"));
        Log.info(map);
        for(var e : map.entries()){
            var node = nodes.find(n -> n.name.equals(e.key));
            if(node == null) throw new IllegalStateException("Node '" + e.key + "' not found!");

            node.load(JsonIO.json.fromJson(StringMap.class, String.class, e.value));
        }
    }

    @Override
    public void init(){
        super.init();
        Core.app.post(() -> {
            try{
                setNodes(JsonIO.json.fromJson(Seq.class, StoryNode.class, generator.map.tags.get("nodes", "[]")));
            }catch(Throwable t){
                if(ui == null){
                    Log.err(t);
                }else{
                    Events.on(ClientLoadEvent.class, e -> Time.runTask(6f, () -> ui.showException("Failed to load the story nodes of '" + localizedName + "'", t)));
                }
            }
        });
    }

    public void setNodes(Seq<StoryNode> nodes){
        this.nodes.set(nodes);
        this.nodes.each(StoryNode::createObjectives);
        this.nodes.each(e -> e.objectives.each(Objective::resolveDependencies));
    }
}

package unity.map;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.game.EventType.*;
import mindustry.io.*;
import mindustry.type.*;
import unity.map.cinematic.*;
import unity.map.objectives.*;
import unity.mod.*;

import java.io.*;
import java.nio.charset.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
public class ScriptedSector extends SectorPreset{
    public final Seq<StoryNode> nodes = new Seq<>();

    protected boolean added = false;
    protected final Cons<Trigger> updater = Triggers.cons(this::update);
    protected final Cons<Trigger> drawer = Triggers.cons(this::draw);

    private static final ReusableByteInStream ins = new ReusableByteInStream();
    private static final Reads read = new Reads(new DataInputStream(ins));

    private static final ReusableByteOutStream outs = new ReusableByteOutStream();
    private static final Writes write = new Writes(new DataOutputStream(outs));

    public ScriptedSector(String name, Planet planet, int sector){
        super(name, planet, sector);

        Events.on(SaveWriteEvent.class, e -> {
            if(added && valid()) saveState();
        });

        Events.on(SaveLoadEvent.class, e -> {
            boolean valid = valid();
            if(!added && valid){
                added = true;
                Triggers.listen(Trigger.update, updater);
                Triggers.listen(Trigger.drawOver, drawer);

                nodes.each(StoryNode::init);
            }

            if(valid){
                loadState();
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
        var map = new StringMap();
        for(var node : nodes){
            outs.reset();

            write.b(node.revision());
            node.save(write);

            map.put(node.name, new String(outs.getBytes(), 0, outs.size(), StandardCharsets.UTF_8));
        }

        state.rules.tags.put(name + "-nodes", JsonIO.json.toJson(map, StringMap.class, String.class));
    }

    public void loadState(){
        var map = JsonIO.json.fromJson(StringMap.class, String.class, state.rules.tags.get(name + "-nodes", "{}"));
        for(var e : map.entries()){
            var node = nodes.find(n -> n.name.equals(e.key));
            if(node == null) throw new IllegalStateException("'" + e.key + "' node not found!");

            ins.setBytes(e.value.getBytes(StandardCharsets.UTF_8));

            byte rev = read.b();
            node.load(read, rev);
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

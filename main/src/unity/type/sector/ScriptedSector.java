package unity.type.sector;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.Log.*;
import mindustry.core.GameState.*;
import mindustry.game.EventType.*;
import mindustry.io.*;
import mindustry.type.*;
import unity.cinematic.*;
import unity.mod.*;
import unity.mod.Triggers.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
public class ScriptedSector extends SectorPreset{
    protected boolean added = false;

    public final Seq<StoryNode<?>> storyNodes = new Seq<>();

    protected final Cons<Trigger> updater = Triggers.cons(this::update);
    protected final Cons<Triggers> drawer = Triggers.cons(this::draw);
    protected final Cons<Trigger> starter = Triggers.cons(() -> {
        Triggers.listen(Trigger.update, updater);
        Triggers.listen(Triggers.drawEnt, drawer);

        storyNodes.each(StoryNode::init);

        Triggers.detach(Trigger.newGame, this.starter);
    });

    public ScriptedSector(String name, Planet planet, int sector){
        super(name, planet, sector);

        Events.on(StateChangeEvent.class, e -> {
            if(e.to == State.playing && !added && valid()){
                added = true;
                Triggers.listen(Trigger.newGame, starter);
            }
        });

        Events.on(SaveWriteEvent.class, e -> saveState());
        Events.on(SaveReadEvent.class, e -> loadState());
    }

    public void update(){
        if(!valid() && added){
            added = false;

            Triggers.detach(Trigger.update, updater);
            Triggers.detach(Triggers.drawEnt, drawer);

            return;
        }

        for(var node : storyNodes){
            if(node.shouldUpdate()) node.update();
        }
    }

    public void draw(){
        storyNodes.each(StoryNode::draw);
    }

    public boolean valid(){
        return state.hasSector()
        ?   state.getSector().id == sector.id
        :   (state.map != null && (
            state.map.mod != null && state.map.mod.name.equals("unity") &&
            (state.map.name().equals(generator.map.name()) || state.map.name().equals(localizedName))
        ));
    }

    public void saveState(){}

    public void loadState(){}

    @Override
    public void init(){
        super.init();
        Core.app.post(() -> {
            try{
                storyNodes.addAll(JsonIO.json.fromJson(Seq.class, generator.map.tags.get("storyNodes", "[]")));

                storyNodes.each(StoryNode::createObjectives);
                storyNodes.each(e -> e.objectives.each(SectorObjective::resolveDependencies));
            }catch(Throwable t){
                print(LogLevel.err, "", "Failed to read story nodes in " + name);
            }
        });
    }
}

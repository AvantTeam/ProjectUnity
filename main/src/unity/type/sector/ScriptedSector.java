package unity.type.sector;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import arc.util.Log.*;
import mindustry.core.GameState.*;
import mindustry.game.EventType.*;
import mindustry.io.*;
import mindustry.type.*;
import unity.cinematic.*;
import unity.mod.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
public class ScriptedSector extends SectorPreset{
    public final Seq<SectorObjective> objectives = new Seq<>();
    protected boolean added = false;

    protected final Seq<StoryNode<?>> storyNodes = new Seq<>();

    protected final Cons<Trigger> updater = Triggers.cons(this::update);
    protected final Cons<Trigger> drawer = Triggers.cons(this::draw);
    protected final Cons<Trigger> starter = Triggers.cons(() -> {
        Triggers.listen(Trigger.update, updater);
        Triggers.listen(Trigger.draw, drawer);

        loadState();
        objectives.each(SectorObjective::init);

        Triggers.detach(Trigger.newGame, this.starter);
    });

    protected final Interval saveTimer = new Interval();

    public ScriptedSector(String name, Planet planet, int sector){
        super(name, planet, sector);

        Events.on(StateChangeEvent.class, e -> {
            if(e.to == State.playing && !added && valid()){
                added = true;
                Triggers.listen(Trigger.newGame, starter);
            }
        });

        Events.on(DisposeEvent.class, e -> {
            if(state.isPlaying() && canSave()) saveState();
        });
    }

    public void update(){
        if(!valid() && added){
            added = false;

            Triggers.detach(Trigger.update, updater);
            Triggers.detach(Trigger.draw, drawer);

            return;
        }

        // Save objective state every 5 seconds
        if(canSave() && saveTimer.get(5f * Time.toSeconds)) saveState();

        for(SectorObjective objective : objectives){
            if(objective.shouldUpdate()) objective.update();

            if(objective.qualified()){
                objective.execution++;
                objective.execute();
            }

            if(objective.isExecuted() && !objective.isFinalized()) objective.doFinalize();
        }
    }

    public boolean canSave(){
        return control.saves.getCurrent() != null;
    }

    public void draw(){
        for(SectorObjective objective : objectives){
            if(objective.shouldDraw()){
                objective.draw();
            }
        }
    }

    public boolean valid(){
        return state.hasSector()
        ?   state.getSector().id == sector.id
        :   (state.map != null && (
            state.map.mod != null && state.map.mod.name.equals("unity") &&
            (state.map.name().equals(generator.map.name()) || state.map.name().equals(localizedName))
        ));
    }

    public void saveState(){
        objectives.each(SectorObjective::save);
    }

    public void loadState(){
        objectives.each(SectorObjective::load);
    }

    @Override
    public void init(){
        super.init();

        try{
            storyNodes.addAll(JsonIO.json.fromJson(Seq.class, generator.map.tags.get("storyNodes", "[]")));
        }catch(Throwable t){
            print(LogLevel.err, "", "Failed to read story nodes in " + name);
        }
    }
}

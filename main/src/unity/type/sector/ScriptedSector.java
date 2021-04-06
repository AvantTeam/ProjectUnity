package unity.type.sector;

import arc.*;
import arc.func.*;
import arc.struct.*;
import mindustry.core.GameState.*;
import mindustry.game.EventType.*;
import mindustry.type.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class ScriptedSector extends SectorPreset{
    public Seq<SectorObjective> objectives = new Seq<>();
    protected boolean added = false;

    public final Cons<Trigger> updater = e -> update();
    public final Cons<Trigger> drawer = e -> draw();

    public ScriptedSector(String name, Planet planet, int sector){
        super(name, planet, sector);

        Events.on(StateChangeEvent.class, e -> {
            if(e.to == State.playing && !added && valid()){
                added = true;

                Cons<Trigger>[] set = new Cons[1];
                set[0] = t -> {
                    if(state.getSector() == null || !state.getSector().hasBase()){
                        reset();
                    }
    
                    Events.on((Class<Trigger>)Trigger.update.getClass(), updater);
                    Events.on((Class<Trigger>)Trigger.draw.getClass(), drawer);
                    Events.remove((Class<Trigger>)Trigger.newGame.getClass(), set[0]);
                };

                Events.on((Class<Trigger>)Trigger.newGame.getClass(), set[0]);
            }
        });
    }

    public void update(){
        if(!valid() && added){
            added = false;

            Events.remove((Class<Trigger>)Trigger.update.getClass(), updater);
            Events.remove((Class<Trigger>)Trigger.draw.getClass(), drawer);

            return;
        }

        for(SectorObjective objective : objectives){
            if(objective.shouldUpdate()){
                if(!objective.isInitialized()){
                    objective.init();
                }
                objective.update();
            }

            if(objective.qualified()){
                objective.execution++;
                objective.execute();
            }

            if(objective.isExecuted() && !objective.isFinalized()){
                objective.doFinalize();
            }
        }
    }

    public void draw(){
        for(SectorObjective objective : objectives){
            if(objective.shouldDraw()){
                objective.draw();
            }
        }
    }

    public void reset(){
        for(SectorObjective objective : objectives){
            objective.reset();
            objective.execution = 0;
        }
    }

    public boolean valid(){
        return state.getSector() != null
        ?   state.getSector().id == sector.id
        :   (
            state.map != null
            ?   (
                state.map.name().equals(generator.map.name()) &&
                state.map.mod != null && state.map.mod.name.equals("unity")
            )
            :   false
        );
    }
}

package unity.type.sector;

import arc.*;
import arc.func.*;
import arc.struct.*;
import mindustry.core.GameState.*;
import mindustry.game.EventType.*;
import mindustry.type.*;
import unity.mod.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
public class ScriptedSector extends SectorPreset{
    public Seq<SectorObjective> objectives = new Seq<>();
    protected boolean added = false;

    protected Cons<Trigger> updater = Triggers.cons(this::update);
    protected Cons<Trigger> drawer = Triggers.cons(this::draw);
    protected Cons<Trigger> starter = Triggers.cons(() -> {
        if(!state.hasSector() || !state.getSector().hasBase()){
            reset();
        }

        updater = Triggers.listen(Trigger.update, this::update);
        drawer = Triggers.listen(Trigger.draw, this::draw);
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
    }

    public void update(){
        if(!valid() && added){
            added = false;

            Triggers.detach(Trigger.update, updater);
            Triggers.detach(Trigger.draw, drawer);

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
        return state.hasSector()
        ?   state.getSector().id == sector.id
        :   (state.map != null && (
            state.map.mod != null && state.map.mod.name.equals("unity") &&
            (state.map.name().equals(generator.map.name()) || state.map.name().equals(localizedName))
        ));
    }
}

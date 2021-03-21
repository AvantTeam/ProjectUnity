package unity.type;

import arc.*;
import arc.struct.*;
import mindustry.core.GameState.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public class ScriptedSector extends SectorPreset implements ApplicationListener{
    public Seq<SectorObjective> objectives = new Seq<>();

    public ScriptedSector(String name, Planet planet, int sector){
        super(name, planet, sector);

        Events.on(StateChangeEvent.class, e -> {
            if(e.to == State.playing && !Core.app.getListeners().contains(this)){
                if(valid()){
                    reset();
                    Core.app.addListener(this);
                    unity.Unity.print("Added " + this.name + " into application listeners");
                }
            }
        });
    }

    @Override
    public void update(){
        if(!valid() && Core.app.getListeners().contains(this)){
            reset();
            Core.app.removeListener(this);
            unity.Unity.print("Removed " + name + " from application listeners");

            return;
        }

        for(SectorObjective objective : objectives){
            objective.update();
            if(objective.completed() && !objective.isExecuted()){
                objective.execute();
                objective.execution++;
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
        return state.map != null
        ?   state.map.name().equals(generator.map.name())
        :   (
            state.rules != null
            ?   (
                state.rules.sector != null
                ?   state.rules.sector.id == sector.id
                :   false
            )
            :   false
        );
    }

    public abstract class SectorObjective{
        public final SectorListener listener;

        public final int executions;
        protected int execution;

        public SectorObjective(int executions, SectorListener listener){
            this.listener = listener;
            this.executions = executions;
        }

        public void update(){}

        public void reset(){}

        public void execute(){
            listener.execute();
        }

        public boolean isExecuted(){
            return execution >= executions;
        }

        public int getExecution(){
            return execution;
        }

        public abstract boolean completed();
    }

    public class UnitDeathObjective extends SectorObjective{
        public final UnitType type;
        protected Unit unit;

        public UnitDeathObjective(UnitType type, int executions, SectorListener listener){
            super(executions, listener);

            this.type = type;
            Events.on(UnitDestroyEvent.class, e -> {
                if(!isExecuted() && valid() && unit == null && e.unit.type.id == type.id){
                    unit = e.unit;
                }
            });
        }

        @Override
        public void reset(){
            unit = null;
        }

        @Override
        public boolean completed(){
            return unit != null && unit.dead();
        }

        @Override
        public void execute(){
            super.execute();
            unit = null;
        }
    }

    @FunctionalInterface
    public interface SectorListener{
        void execute();
    }
}

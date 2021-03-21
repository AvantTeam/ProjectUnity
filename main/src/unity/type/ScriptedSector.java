package unity.type;

import arc.*;
import arc.struct.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public class ScriptedSector extends SectorPreset implements ApplicationListener{
    public Seq<SectorObjective> objectives = new Seq<>();

    public ScriptedSector(String name, Planet planet, int sector){
        super(name, planet, sector);

        Events.on(SectorLaunchEvent.class, e -> {
            if(e.sector.id == sector) {
                reset();
                Core.app.addListener(this);
                unity.Unity.print("Added into application listeners");
            }
        });
    }

    @Override
    public void update(){
        if(!valid()){
            reset();
            Core.app.removeListener(this);
            unity.Unity.print("Removed from application listeners");

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
        return state.rules != null
            ?   (
                state.rules.sector != null
                ?   state.rules.sector.id == sector.id
                :   false
            )
            :   false;
    }

    public abstract class SectorObjective{
        public final SectorListener listener;

        protected final int executions;
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

        public abstract boolean completed();
    }

    public class UnitDeathObjective extends SectorObjective{
        public final UnitType type;
        protected int unitId = -1;

        public UnitDeathObjective(UnitType type, int executions, SectorListener listener){
            super(executions, listener);

            this.type = type;
            Events.on(UnitCreateEvent.class, e -> {
                if(!isExecuted() && valid() && unitId == -1 && e.unit.type.id == type.id){
                    unitId = e.unit.id;
                }
            });
        }

        @Override
        public void reset(){
            unitId = -1;
        }

        @Override
        public boolean completed(){
            var unit = Groups.unit.getByID(unitId);
            return unit != null && unit.dead();
        }

        @Override
        public void execute(){
            super.execute();
            unitId = -1;
        }
    }

    @FunctionalInterface
    public interface SectorListener{
        void execute();
    }
}

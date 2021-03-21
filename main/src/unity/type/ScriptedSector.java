package unity.type;

import arc.*;
import arc.struct.*;
import mindustry.game.EventType.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public class ScriptedSector extends SectorPreset implements ApplicationListener{
    public Seq<SectorObjective> objectives = new Seq<>();

    public ScriptedSector(String name, Planet planet, int sector, SectorObjective... objectives){
        super(name, planet, sector);

        this.objectives.addAll(objectives);
        Events.on(SectorLaunchEvent.class, e -> {
            if(e.sector.id == sector) {
                reset();
                Core.app.addListener(this);
            }
        });
    }

    @Override
    public void update(){
        if(
            !state.isPlaying() || (
                state.rules != null
                ?   (
                    state.rules.sector != null
                    ?   state.rules.sector.id == sector.id
                    :   true
                )
                :   true
            )
        ){
            reset();
            Core.app.removeListener(this);

            return;
        }

        for(SectorObjective objective : objectives){
            objective.update();
            if(objective.completed() && !objective.executed){
                objective.listener.execute();
                objective.executed = true;
            }
        }
    }

    public void reset(){
        for(SectorObjective objective : objectives){
            objective.executed = false;
        }
    }

    public abstract class SectorObjective{
        public final ScriptedSector sector;
        public final SectorListener listener;

        private boolean executed = false;

        public SectorObjective(ScriptedSector sector, SectorListener listener){
            this.sector = sector;
            this.listener = listener;
        }

        public void update(){}

        public abstract boolean completed();

        public boolean isExecuted(){
            return executed;
        }
    }

    @FunctionalInterface
    public interface SectorListener{
        void execute();
    }
}

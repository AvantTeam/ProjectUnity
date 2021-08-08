package unity.type.sector.objectives;

import arc.*;
import arc.func.*;
import arc.struct.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.type.sector.*;

/**
 * Triggers when a {@linkplain #counts specific amount} of units with a certain type spawns.
 * @author GlennFolker
 */
public class UnitSpawnObjective extends SectorObjective{
    public final UnitType type;
    public final Team team;

    public final int counts;
    protected IntSet ids = new IntSet();

    public UnitSpawnObjective(Team team, UnitType type, int counts, ScriptedSector sector, String name, int executions, Cons<UnitSpawnObjective> executor){
        super(sector, name, executions, executor);

        this.team = team;
        this.type = type;
        this.counts = counts;

        Events.on(UnitDestroyEvent.class, e -> {
            Unit unit = e.unit;
            if(!isExecuted() && sector.valid() && ids.contains(unit.id) && unit.team == this.team && unit.type.id == this.type.id){
                ids.remove(unit.id);
            }
        });
    }

    @Override
    public void reset(){
        super.reset();
        ids.clear();
    }

    @Override
    public void update(){
        super.update();
        Groups.unit.each(
            unit ->
                !isExecuted() && sector.valid() &&
                    !ids.contains(unit.id) && unit.team == team && unit.type.id == type.id,
            unit -> ids.add(unit.id)
        );
    }

    @Override
    public boolean completed(){
        return ids.size >= counts;
    }

    @Override
    public void execute(){
        super.execute();
        ids.clear();
    }
}

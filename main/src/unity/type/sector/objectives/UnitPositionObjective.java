package unity.type.sector.objectives;

import arc.func.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.type.sector.*;

/**
 * Extends {@link UnitGroupObjective}; provides units in a certain area.
 * @author GlennFolker
 */
public class UnitPositionObjective extends UnitGroupObjective{
    public UnitPositionObjective(Team team, float x, float y, float width, float height, boolean continuous, int count, ScriptedSector sector, String name, int executions, Cons<UnitPositionObjective> executor){
        super(() ->
            Groups.unit
                .intersect(x, y, width, height)
                .select(unit -> unit.team == team),
            continuous, count, sector, name, executions, e -> executor.get((UnitPositionObjective)e)
        );
    }

    public UnitPositionObjective(Team team, float x, float y, float radius, boolean continuous, int count, ScriptedSector sector, String name, int executions, Cons<UnitPositionObjective> executor){
        super(() ->
            Groups.unit
                .intersect(x - radius, y - radius, radius * 2f, radius * 2f)
                .select(unit -> unit.dst(x, y) <= radius && unit.team == team),
            continuous, count, sector, name, executions, e -> executor.get((UnitPositionObjective)e)
        );
    }
}

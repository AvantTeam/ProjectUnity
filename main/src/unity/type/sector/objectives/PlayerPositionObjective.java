package unity.type.sector.objectives;

import arc.func.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.type.sector.*;

/**
 * Extends {@link UnitGroupObjective}; provides players in a certain area.
 * @author GlennFolker
 */
public class PlayerPositionObjective extends UnitGroupObjective{
    public PlayerPositionObjective(Team team, float x, float y, float width, float height, boolean continuous, int count, ScriptedSector sector, String name, int executions, Cons<PlayerPositionObjective> executor){
        super(() ->
            Groups.player
                .intersect(x, y, width, height)
                .select(player -> player.team() == team)
                .map(Player::unit),
            continuous, count, sector, name, executions, e -> executor.get((PlayerPositionObjective)e)
        );
    }

    public PlayerPositionObjective(Team team, float x, float y, float radius, boolean continuous, int count, ScriptedSector sector, String name, int executions, Cons<PlayerPositionObjective> executor){
        super(() ->
            Groups.player
                .intersect(x - radius, y - radius, radius * 2f, radius * 2f)
                .select(player -> player.team() == team)
                .map(Player::unit),
            continuous, count, sector, name, executions, e -> executor.get((PlayerPositionObjective)e)
        );
    }
}

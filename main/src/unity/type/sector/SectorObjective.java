package unity.type.sector;

import arc.*;
import arc.func.Prov;
import arc.struct.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public abstract class SectorObjective{
    public final SectorExecutor executor;
    public final ScriptedSector sector;

    public final int executions;
    int execution;

    public Seq<SectorObjective> dependencies = new Seq<>();

    public SectorObjective(ScriptedSector sector, int executions, SectorExecutor executor){
        this.sector = sector;
        this.executor = executor;
        this.executions = executions;
    }

    public void update(){}

    public void draw(){}

    public void reset(){}

    public void execute(){
        executor.execute(sector, this);
    }

    public abstract boolean completed();

    public boolean isExecuted(){
        return execution >= executions;
    }

    public boolean qualified(){
        return !isExecuted() && completed() && dependencyCompleted();
    }

    public boolean dependencyCompleted(){
        return dependencies.find(obj -> !obj.isExecuted()) == null;
    }

    public int getExecution(){
        return execution;
    }

    /** Triggers when a {@linkplain #counts specific amount} of units with a certain type dies. */
    public static class UnitDeathObjective extends SectorObjective{
        public final UnitType type;

        public final int counts;
        protected int count;

        public UnitDeathObjective(UnitType type, int counts, ScriptedSector sector, int executions, SectorExecutor listener){
            super(sector, executions, listener);

            this.type = type;
            this.counts = counts;

            Events.on(UnitDestroyEvent.class, e -> {
                Unit unit = e.unit;
                if(!isExecuted() && sector.valid() && state.rules.defaultTeam != unit.team && unit.type.id == type.id){
                    count++;
                }
            });
        }

        public int getCount(){
            return count;
        }

        @Override
        public void reset(){
            count = 0;
        }

        @Override
        public boolean completed(){
            return count >= counts;
        }

        @Override
        public void execute(){
            super.execute();
            count = 0;
        }
    }

    /** Triggers when a provided unit groups's size is equal or larger to the {@linkplain #counts threshold}. */
    public static class UnitGroupObjective extends SectorObjective{
        public final Prov<Seq<Unit>> provider;
        public final boolean continuous;

        public final int counts;
        protected int count;
        private IntSet ids = new IntSet();

        public UnitGroupObjective(Prov<Seq<Unit>> provider, boolean continuous, int counts, ScriptedSector sector, int executions, SectorExecutor executor){
            super(sector, executions, executor);

            this.continuous = continuous;
            this.counts = counts;
            this.provider = provider;
        }

        @Override
        public void reset(){
            count = 0;
            ids.clear();
        }

        @Override
        public void update(){
            if(!continuous){
                for(Unit unit : provider.get()){
                    ids.add(unit.id);
                }
            }
        }

        @Override
        public boolean completed(){
            if(continuous){
                return provider.get().size >= count;
            }else{
                return ids.size >= counts;
            }
        }
    }

    /** Extends {@link UnitGroupObjective}; provides units in a certain area. */
    public static class UnitPositionObjective extends UnitGroupObjective{
        public UnitPositionObjective(Team team, float x, float y, float width, float height, boolean continuous, int count, ScriptedSector sector, int executions, SectorExecutor executor){
            super(() ->
                Groups.unit
                    .intersect(x, y, width, height)
                    .select(unit -> unit.team == team),
                continuous, count, sector, executions, executor
            );
        }

        public UnitPositionObjective(Team team, float x, float y, float radius, boolean continuous, int count, ScriptedSector sector, int executions, SectorExecutor executor){
            super(() ->
                Groups.unit
                    .intersect(x - radius, y - radius, radius * 2f, radius * 2f)
                    .select(unit -> unit.dst(x, y) <= radius && unit.team == team),
                continuous, count, sector, executions, executor
            );
        }
    }

    /** Extends {@link UnitGroupObjective}; provides players in a certain area. */
    public static class PlayerPositionObjective extends UnitGroupObjective{
        protected static Seq<Unit> units = new Seq<>();

        public PlayerPositionObjective(Team team, float x, float y, float width, float height, boolean continuous, int count, ScriptedSector sector, int executions, SectorExecutor executor){
            super(() -> {
                Groups.player
                    .intersect(x, y, width, height)
                    .select(player -> player.team() == team)
                    .each(player -> units.add(player.unit()));

                    return units;
                },
                continuous, count, sector, executions, executor
            );
        }

        public PlayerPositionObjective(Team team, float x, float y, float radius, boolean continuous, int count, ScriptedSector sector, int executions, SectorExecutor executor){
            super(() -> {
                Groups.player
                    .intersect(x - radius, y - radius, radius * 2f, radius * 2f)
                    .select(player -> player.team() == team)
                    .each(player -> units.add(player.unit()));

                    return units;
                },
                continuous, count, sector, executions, executor
            );
        }
    }

    @FunctionalInterface
    public interface SectorExecutor{
        void execute(ScriptedSector sector, SectorObjective execution);
    }
}

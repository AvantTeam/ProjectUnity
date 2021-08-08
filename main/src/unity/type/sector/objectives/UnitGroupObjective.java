package unity.type.sector.objectives;

import arc.func.*;
import arc.struct.*;
import mindustry.gen.*;
import unity.type.sector.*;

/**
 * Triggers when a provided unit groups's size is equal or larger to the {@linkplain #counts threshold}.
 * @author GlennFolker
 */
public class UnitGroupObjective extends SectorObjective{
    public final Prov<Seq<Unit>> provider;
    public final boolean continuous;

    public final int counts;
    protected int count;
    private final IntSet ids = new IntSet();

    public UnitGroupObjective(Prov<Seq<Unit>> provider, boolean continuous, int counts, ScriptedSector sector, String name, int executions, Cons<UnitGroupObjective> executor){
        super(sector, name, executions, executor);

        this.continuous = continuous;
        this.counts = counts;
        this.provider = provider;
    }

    @Override
    public void reset(){
        super.reset();
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

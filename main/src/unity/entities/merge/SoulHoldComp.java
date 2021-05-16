package unity.entities.merge;

import mindustry.content.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.mod.*;

import static mindustry.Vars.*;

@MergeComponent
class SoulHoldComp extends Block{
    int maxSouls = 3;
    boolean affectEfficiency = true;

    public SoulHoldComp(String name){
        super(name);
        world.build(0);
    }

    public class SoulBuildComp extends Building implements ControlBlock{
        BlockUnitc unit = Nulls.blockUnit;
        private boolean wasPlayer;

        int souls;

        @Override
        @Replace
        public Unit unit(){
            return unit.as();
        }

        @Override
        public boolean canControl(){
            return canJoin() && (headless || (player != null && player.unit() instanceof Monolithc unit && FactionMeta.map(unit) == Faction.monolith));
        }

        @Override
        public void created(){
            unit = UnitTypes.block.create(team).as();
            unit.tile(this);
        }

        @Override
        public void update(){
            if(!wasPlayer && unit.isPlayer()){
                join();
                wasPlayer = true;
            }

            if(wasPlayer && !unit.isPlayer()){
                unjoin();
                wasPlayer = false;
            }
        }

        @Override
        public float efficiency(){
            return super.efficiency() * (!affectEfficiency ? 1f : (souls / (float)maxSouls));
        }

        public boolean canJoin(){
            return souls < maxSouls;
        }

        public void join(){
            if(canJoin()) souls++;
        }

        public void unjoin(){
            if(souls > 0) souls--;
        }
    }
}

package unity.entities.merge;

import mindustry.content.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.world.blocks.defense.*;
import unity.world.blocks.defense.GenericTractorBeamTurret.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
@MergeComponent
class SoulHoldComp extends Block{
    int maxSouls = 3;
    float efficiencyFrom = 0.3f;
    float efficiencyTo = 1f;

    boolean requireSoul = true;

    public SoulHoldComp(String name){
        super(name);
    }

    public class SoulBuildComp extends Building implements ControlBlock{
        transient BlockUnitc unit = Nulls.blockUnit;

        @ReadOnly boolean wasPlayer;
        @ReadOnly int souls;

        @Override
        @Replace
        public Unit unit(){
            return unit.as();
        }

        @Override
        public boolean canControl(){
            return canJoin() && (headless || (player != null && player.unit() instanceof Monolithc unit && unit.souls() > 0 && unit.isSameFaction(this)));
        }

        @Override
        public void created(){
            if(self() instanceof TurretBuild build){
                unit = build.unit;
            }else if(self() instanceof GenericTractorBeamTurret<?>.GenericTractorBeamTurretBuild build){
                unit = build.unit;
            }else{
                unit = UnitTypes.block.create(team).as();
                unit.tile(this);
            }
        }

        @Override
        public void updateTile(){
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
        @Replace
        public float efficiency(){
            return (requireSoul && disabled()) ? 0f : (super.efficiency() * ((souls / (float)maxSouls) * (efficiencyTo - efficiencyFrom) + efficiencyFrom));
        }

        public boolean disabled(){
            return souls <= 0;
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

package unity.entities.merge;

import arc.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import mindustry.world.meta.*;
import unity.annotations.Annotations.*;
import unity.entities.*;
import unity.world.blocks.defense.turrets.*;

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

    @Override
    public void setStats(){
        stats.add(Stat.abilities, cont -> {
            cont.row();
            cont.table(bt -> {
                bt.left().defaults().padRight(3).left();

                bt.row();
                bt.add(Core.bundle.get(requireSoul ? "soul.require" : "soul.optional"));

                if(maxSouls > 0){
                    bt.row();
                    bt.add(Core.bundle.format("soul.max", maxSouls));
                }
            });
        });
    }

    public class SoulBuildComp extends Building implements ControlBlock, Soul{
        transient BlockUnitc unit = Nulls.blockUnit;

        @ReadOnly boolean wasPlayer;
        private int souls;

        @Override
        @Replace
        public Unit unit(){
            return unit.as();
        }

        @Override
        public boolean canControl(){
            return canJoin() && (headless || acceptSoul(player.unit()) > 0);
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

        @Override
        public int souls(){
            return souls;
        }

        @Override
        public int maxSouls(){
            return maxSouls;
        }

        public boolean disabled(){
            return !hasSouls();
        }

        @Override
        public void join(){
            if(canJoin()) souls++;
        }

        @Override
        public void unjoin(){
            if(souls > 0) souls--;
        }
    }
}

package unity.entities.merge;

import arc.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import mindustry.world.meta.*;
import unity.annotations.Annotations.*;
import unity.annotations.Annotations.Resolve.*;
import unity.entities.*;
import unity.gen.*;
import unity.world.blocks.defense.turrets.*;
import unity.world.meta.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
@MergeComponent
abstract class SoulComp extends Block implements Stemc{
    int maxSouls = 3;
    float efficiencyFrom = 0.3f;
    float efficiencyTo = 1f;

    boolean requireSoul = true;

    DynamicProgression progression = new DynamicProgression();

    public SoulComp(String name){
        super(name);
        update = true;
        destructible = true;
        sync = true;
    }

    @Override
    public void setStats(){
        stats.add(Stat.abilities, cont -> {
            cont.row();
            cont.table(bt -> {
                bt.left().defaults().padRight(3).left();

                bt.row();
                bt.add(requireSoul ? "@soul.require" : "@soul.optional");

                if(maxSouls > 0){
                    bt.row();
                    bt.add(Core.bundle.format("soul.max", maxSouls));
                }
            });
        });
    }

    public abstract class SoulBuildComp extends Building implements StemBuildc, ControlBlock, Soul{
        @Nullable transient BlockUnitc unit;

        private int souls;

        @Override
        @Replace
        public Unit unit(){
            return unit.as();
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
        @MethodPriority(-100)
        public void update(){
            progression.apply(souls);
        }

        @Override
        public void onRemoved(){
            if(net.server() || !net.active()){
                spreadSouls();
            }
        }

        @Override
        public boolean apply(MonolithSoul soul, int index, boolean transferred){
            if(isControlled() && !transferred && (Mathf.chance(1f / souls) || index == souls - 1)){
                soul.controller(unit.getPlayer());
                transferred = true;
            }

            return transferred;
        }

        @Override
        @Combine
        public float efficiency(){
            @Resolve(Method.average)
            float result = 1f;

            return (requireSoul && disabled()) ? 0f : (super.efficiency() * result * ((souls / (float)maxSouls) * (efficiencyTo - efficiencyFrom) + efficiencyFrom));
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

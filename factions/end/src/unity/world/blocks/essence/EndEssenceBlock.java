package unity.world.blocks.essence;

import arc.math.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.*;
import unity.graphics.*;
import unity.mod.*;
import unity.world.*;

public class EndEssenceBlock extends Block{
    public float essenceCapacity = 10f;
    public float essencePressure = 1f;

    public EndEssenceBlock(String name){
        super(name);
        update = true;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(UnityStats.essenceCapacity, essenceCapacity);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("essence", (EndEssenceBuilding2 e) ->
                new Bar("category.unity-essence", EndPal.endMid, () -> e.module.essence / essenceCapacity));
    }

    public class EndEssenceBuilding2 extends Building implements EndEssenceInterface2{
        public EndEssenceModule module = new EndEssenceModule();

        @Override
        public EndEssenceModule mod(){
            return module;
        }

        @Override
        public float capacity(){
            return essenceCapacity;
        }

        public void transferEssence(EndEssenceInterface2 next, float amount){
            if(amount <= 0) return;
            float flow = Math.min(Math.min(amount, module.essence), next.capacity() - next.mod().essence);

            next.mod().essence += flow;
            module.essence -= flow;
        }

        public void dumpEssence(float scaling){
            int dump = this.cdump;
            if(module.essence <= 0.0001f) return;

            for(int i = 0; i < proximity.size; i++){
                incrementDump(proximity.size);
                //Log.info("Essence1");

                Building other = proximity.get((i + dump) % proximity.size);
                if(other.team == team && other instanceof EndEssenceBuilding2 e && e.acceptEssence(this)){
                    //float ofract = other.liquids.get(liquid) / other.block.liquidCapacity;
                    //float fract = liquids.get(liquid) / block.liquidCapacity;
                    //Log.info("EssenceAAAA");
                    float of = e.efract();
                    float f = efract() * essencePressure;
                    if(of < f) transferEssence(e, Mathf.clamp(f - of) * essenceCapacity / scaling);
                }
            }
        }

        public void essenceFlowForward(){
            Tile next = tile.nearby(rotation);

            if(next == null) return;
            if(next.build != null){
                if(next.build instanceof EndEssenceInterface2 e && e.acceptEssence(this)){
                    float of = e.efract();
                    float f = efract() * essencePressure;
                    float flow = Mathf.clamp(f - of) * essenceCapacity;

                    transferEssence(e, flow);
                }
            }else{
                float leakAmount = module.essence / 1.5f;
                EndBuilders.essence.addAir(EndBuilders.essence.posWorld(x, y), leakAmount);
                module.essence -= leakAmount;
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            module.read(read);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            module.write(write);
        }
    }

    public interface EndEssenceInterface2{
        EndEssenceModule mod();
        float capacity();

        default float efract(){
            return mod().essence / capacity();
        }

        default boolean acceptEssence(Building source){
            return true;
        }
    }
}

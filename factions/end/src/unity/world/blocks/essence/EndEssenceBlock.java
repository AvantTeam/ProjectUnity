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
        addBar("essence", (EndEssenceBuilding e) ->
                new Bar("category.unity-essence", EndPal.endMid, () -> e.essence.essence / essenceCapacity));
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        if(other.privileged) return false;
        return other instanceof EndEssenceBlock && size >= other.size;
    }

    public class EndEssenceBuilding extends Building implements EndEssenceInterface{
        public EndEssenceModule essence = new EndEssenceModule();

        @Override
        public EndEssenceModule ess(){
            return essence;
        }

        @Override
        public float capacity(){
            return essenceCapacity;
        }

        public void transferEssence(EndEssenceInterface next, float amount){
            if(amount <= 0) return;
            float flow = Math.min(Math.min(amount, essence.essence), next.capacity() - next.ess().essence);

            next.ess().essence += flow;
            essence.essence -= flow;
        }

        public void dumpEssence(float scaling){
            int dump = this.cdump;
            if(essence.essence <= 0.0001f) return;

            for(int i = 0; i < proximity.size; i++){
                incrementDump(proximity.size);
                //Log.info("Essence1");

                Building other = proximity.get((i + dump) % proximity.size);
                if(other.team == team && other instanceof EndEssenceInterface e && e.acceptEssence(this)){
                    //float ofract = other.liquids.get(liquid) / other.block.liquidCapacity;
                    //float fract = liquids.get(liquid) / block.liquidCapacity;
                    //Log.info("EssenceAAAA");
                    EndEssenceInterface n = e.getEssencesDestination(this);
                    float of = n.efract();
                    float f = efract() * essencePressure;
                    if(of < f) transferEssence(n, Mathf.clamp(f - of) * essenceCapacity / scaling);
                }
            }
        }

        public void essenceFlowForward(){
            Tile next = tile.nearby(rotation);

            if(next == null) return;
            if(next.build != null){
                if(next.build instanceof EndEssenceInterface e && e.acceptEssence(this)){
                    EndEssenceInterface n = e.getEssencesDestination(this);
                    float of = n.efract();
                    float f = efract() * essencePressure;
                    float flow = Mathf.clamp(f - of) * essenceCapacity;

                    transferEssence(n, flow);
                }
            }else{
                float leakAmount = essence.essence / 1.5f;
                EndBuilders.essence.addAir(EndBuilders.essence.posWorld(x, y), leakAmount);
                essence.essence -= leakAmount;
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            essence.read(read);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            essence.write(write);
        }
    }

    public interface EndEssenceInterface{
        EndEssenceModule ess();
        float capacity();

        default float efract(){
            return ess().essence / capacity();
        }

        default boolean acceptEssence(Building source){
            return true;
        }

        default EndEssenceInterface getEssencesDestination(EndEssenceInterface source){
            return this;
        }
    }
}

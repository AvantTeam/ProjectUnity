package unity.world.blocks.production;

import arc.struct.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.world.blocks.effect.SoulContainer.*;

import static mindustry.Vars.*;

@Merge(base = FloorExtractor.class, value = {SoulHoldc.class, Stemc.class})
public class SoulInfuser extends StemSoulHoldFloorExtractor{
    public int amount = 1;
    public int maxContainers = 3;
    public float range = 120f;

    public SoulInfuser(String name){
        super(name);

        configurable = true;
        outputItem = null;
        outputLiquid = null;

        config(Integer.class, (SoulInfuserBuild build, Integer value) -> {
            if(build.containers.contains(value)){
                build.containers.removeValue(value);
            }else if(build.containers.size < maxContainers){
                build.containers.add(value);
            }
        });
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize, y * tilesize, range, Pal.accent);
    }

    public class SoulInfuserBuild extends StemSoulFloorExtractorBuild{
        public IntSeq containers = new IntSeq();

        @Override
        public void placed(){
            if(net.client()) return;

            super.placed();
        }

        @Override
        public boolean onConfigureTileTapped(Building other){
            if(other instanceof SoulContainerBuild){
                configure(other.pos());
                return false;
            }

            return true;
        }

        @Override
        public boolean shouldConsume(){
            for(int i = 0; i < containers.size; i++){
                Building build = world.build(containers.items[i]);
                if(build instanceof SoulContainerBuild cont && cont.acceptSoul(1) > 0){
                    return true;
                }
            }
            return false;
        }

        @Override
        public void consume(){
            super.consume();

            int sent = 0;
            for(int i = 0; i < containers.size && sent < amount; i++){
                Building build = world.build(containers.items[i]);
                if(build instanceof SoulContainerBuild cont && cont.acceptSoul(1) > 0){
                    cont.join();
                    sent++;
                }
            }
        }
    }
}

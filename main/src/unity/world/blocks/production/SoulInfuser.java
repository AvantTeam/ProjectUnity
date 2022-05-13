package unity.world.blocks.production;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.world.blocks.effect.SoulContainer.*;

import static mindustry.Vars.*;

@Merge(base = FloorExtractor.class, value = Soulc.class)
public class SoulInfuser extends SoulFloorExtractor{
    public int amount = 1;
    public int maxContainers = 3;
    public float range = 15f;

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
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range * tilesize, Pal.accent);
    }

    public class SoulInfuserBuild extends SoulFloorExtractorBuild{
        public IntSeq containers = new IntSeq();

        @Override
        public void placed(){
            if(net.client()) return;

            super.placed();
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(other instanceof SoulContainerBuild && Intersector.overlaps(Tmp.cr1.set(x, y, range * tilesize), other.tile().getHitbox(Tmp.r1))){
                configure(other.pos());
                return false;
            }

            return true;
        }

        @Override
        public void drawSelect(){
            super.drawSelect();

            Lines.stroke(1f);

            Draw.color(Pal.accent);
            Drawf.circles(x, y, range * tilesize);
            Draw.reset();
        }

        @Override
        public void drawConfigure(){
            Drawf.circles(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));
            Drawf.circles(x, y, range * tilesize);

            for(int i = 0; i < containers.size; i++){
                Building build = world.build(containers.get(i));
                if(build != null && build.isValid()){
                    Drawf.square(build.x, build.y, build.block.size * tilesize / 2f + 1f, Pal.place);
                }
            }

            Draw.reset();
        }

        @Override
        public boolean shouldConsume(){
            for(int i = 0; i < containers.size; i++){
                Building build = world.build(containers.items[i]);
                if(build instanceof SoulContainerBuild cont && cont.acceptSoul(1) > 0){
                    return true;
                }
            }
            return acceptSoul(amount) >= amount;
        }

        @Override
        public void consume(){
            super.consume();

            int sent = 0;
            for(int i = 0; i < containers.size && sent < amount; i++){
                Building build = world.build(containers.items[i]);
                if(build instanceof SoulContainerBuild cont && cont.acceptSoul(amount) >= amount){
                    cont.join();
                    sent++;
                }
            }

            if(sent < amount){
                sent = amount - sent;

                int accept = acceptSoul(sent);
                for(int i = 0; i < accept; i++){
                    join();
                }
            }
        }
    }
}

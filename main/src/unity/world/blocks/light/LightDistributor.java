package unity.world.blocks.light;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

import static mindustry.Vars.*;

@Merge(LightHoldc.class)
public class LightDistributor extends LightHoldBlock{
    public int maxConnections = 3;

    public LightDistributor(String name){
        super(name);
        solid = true;
        requiresLight = false;
        acceptsLight = true;
        configurable = true;

        config(Integer.class, (LightDistributorBuild tile, Integer value) -> {
            Building target = world.build(value);
            if(target == null || !target.isValid()) return;

            if(tile.routes.containsKey(value)){
                tile.removeRoute(target);
            }else if(tile.routes.size < maxConnections){
                tile.addRoute(target);
            }
        });
    }

    public class LightDistributorBuild extends LightHoldBuild{
        public IntMap<Light> routes = new IntMap<>();

        protected void addRoute(Building target){
            if(!routes.containsKey(target.pos())){
                Light light = Light.create();
                light.set(this);
                light.source = this;
                light.strength = lightsum() / routes.size;
                light.rotation = angleTo(target);
                light.add();

                routes.put(target.pos(), light);
            }
        }

        protected void removeRoute(Building target){
            if(routes.containsKey(target.pos())){
                routes.remove(target.pos()).remove();
            }
        }

        @Override
        public void onRemoved(){
            super.onRemoved();

            for(Light light : routes.values()){
                light.remove();
            }
            routes.clear();
        }

        @Override
        public void updateTile(){
            super.updateTile();

            for(var entry : routes.entries()){
                Building build = world.build(entry.key);
                if(build == null || !build.isValid()){
                    routes.remove(entry.key).remove();
                    continue;
                }

                Light light = entry.value;
                light.set(this);
                light.source = this;
                light.strength = lightsum() / routes.size;
                light.rotation = angleTo(build);
            }
        }

        @Override
        public boolean onConfigureTileTapped(Building other){
            if(other != this){
                configure(other.pos());
                return false;
            }

            return true;
        }

        @Override
        public void drawConfigure(){
            Drawf.circles(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));

            var keys = routes.keys();
            while(keys.hasNext){
                int pos = keys.next();
                Building build = world.build(pos);
                if(build == null || !build.isValid()) continue;

                Drawf.circles(build.x, build.y, build.tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f), Pal.place);

                int seg = (int)(dst(build.x, build.y) / tilesize);

                Lines.stroke(2f, Pal.gray);
                Lines.dashLine(x, y, build.x, build.y, seg);
                Lines.stroke(1f, Pal.placing);
                Lines.dashLine(x, y, build.x, build.y, seg);
                Draw.reset();
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(routes.size);

            var keys = routes.keys();
            while(keys.hasNext){
                write.i(keys.next());
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            int amount = read.i();
            for(int i = 0; i < amount; i++){
                Building build = world.build(read.i());
                if(build == null) continue;

                Light light = Light.create();
                light.set(this);
                light.source = this;
                light.rotation = angleTo(build);
                routes.put(build.pos(), light);
            }
        }
    }
}

package unity.world.blocks.light;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.production.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

import static mindustry.Vars.*;

@Merge(base = GenericCrafter.class, value = LightHoldc.class)
public class LightSource extends LightHoldGenericCrafter{
    public float lightProduction = 1f;
    public float rotateSpeed = 5f;

    public LightSource(String name){
        super(name);
        solid = true;
        requiresLight = false;
        acceptsLight = false;
        configurable = true;

        config(Integer.class, (LightSourceBuild tile, Integer position) -> {
            Building build = world.build(position);
            if(build != null && build.isValid()){
                if(tile.target == build){
                    tile.target = null;
                }else{
                    tile.target = build;
                }
            }
        });
    }

    public class LightSourceBuild extends LightHoldGenericCrafterBuild{
        public Light light;
        public float rotation = 90f;

        public Building target;
        public float targetRotation = rotation;

        @Override
        public void created(){
            super.created();

            light = Light.create();
            light.source = this;
            light.set(this);
            light.add();
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(target != null && !target.isValid()) target = null;
            if(target != null) targetRotation = angleTo(target);

            rotation = Mathf.slerpDelta(rotation, targetRotation, rotateSpeed);

            light.set(this);
            light.strength = efficiency() * lightProduction;
            light.rotation = rotation;
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            light.remove();
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

            if(target != null){
                Drawf.circles(target.x, target.y, target.tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f), Pal.place);

                int seg = (int)(dst(target.x, target.y) / tilesize);

                Lines.stroke(2f, Pal.gray);
                Lines.dashLine(x, y, target.x, target.y, seg);
                Lines.stroke(1f, Pal.placing);
                Lines.dashLine(x, y, target.x, target.y, seg);
                Draw.reset();
            }
        }
    }
}

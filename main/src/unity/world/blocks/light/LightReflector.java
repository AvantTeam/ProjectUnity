package unity.world.blocks.light;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

import static mindustry.Vars.*;

@Merge(LightHoldc.class)
public class LightReflector extends LightHoldBlock{
    public float rotateSpeed = 5f;

    public LightReflector(String name){
        super(name);
        solid = true;
        requiresLight = false;
        acceptsLight = true;

        config(Integer.class, (LightReflectorBuild tile, Integer value) -> {
            Building build = world.build(value);
            if(build != null && build.isValid()){
                if(tile.target1 == build){
                    tile.target1 = null;
                }else if(tile.target2 == build){
                    tile.target2 = null;
                }else if(tile.target1 == null){
                    tile.target1 = build;
                }else if(tile.target2 == null){
                    tile.target2 = build;
                }
            }
        });
    }

    public class LightReflectorBuild extends LightHoldBuild{
        public ObjectMap<Light, Light> reflect = new ObjectMap<>();

        public Building target1, target2;

        public float rotation = 90f;
        public float targetRotation = rotation;

        @Override
        public void added(Light light){
            addReflect(light);
        }

        @Override
        public void removed(Light light){
            removeReflect(light);
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            for(Light light : reflect.keys()){
                removeReflect(light);
            }
        }

        protected void addReflect(Light light){
            if(!reflect.containsKey(light)){
                Light ref = apply(light, Light.create());
                ref.strength = light.endStrength();
                ref.rotation = calcRotation(light);

                ref.add();
                reflect.put(light, ref);
            }
        }

        protected void removeReflect(Light light){
            if(reflect.containsKey(light)){
                Light ref = reflect.remove(light);
                ref.remove();
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();

            if(target1 != null && !target1.isValid()) target1 = null;
            if(target2 != null && !target2.isValid()) target2 = null;

            if(target1 != null && target2 != null){
                targetRotation = (target1.angleTo(this) + target2.angleTo(this)) / 2f;
            }

            rotation = Mathf.slerpDelta(rotation, targetRotation, rotateSpeed);

            for(var entry : reflect.entries()){
                var origin = entry.key;
                var ref = entry.value;

                apply(origin, ref);
                ref.strength = origin.endStrength();
                ref.rotation = calcRotation(origin);
            }
        }

        public float calcRotation(Light from){
            Tmp.v1.trnsExact(rotation, 1f);
            return Tmp.v2
                .trnsExact(from.rotation, 1f)
                .sub(Tmp.v1.scl(2 * Tmp.v2.dot(Tmp.v1)))
                .angle();
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

            if(target1 != null){
                Drawf.circles(target1.x, target1.y, target1.tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f), Pal.place);

                int seg = (int)(dst(target1.x, target1.y) / tilesize);

                Lines.stroke(2f, Pal.gray);
                Lines.dashLine(x, y, target1.x, target1.y, seg);
                Lines.stroke(1f, Pal.placing);
                Lines.dashLine(x, y, target1.x, target1.y, seg);
                Draw.reset();
            }

            if(target2 != null){
                Drawf.circles(target2.x, target2.y, target2.tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f), Pal.place);

                int seg = (int)(dst(target2.x, target2.y) / tilesize);

                Lines.stroke(2f, Pal.gray);
                Lines.dashLine(x, y, target2.x, target2.y, seg);
                Lines.stroke(1f, Pal.placing);
                Lines.dashLine(x, y, target2.x, target2.y, seg);
                Draw.reset();
            }
        }
    }
}

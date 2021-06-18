package unity.world.blocks.light;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

import java.util.*;

import static mindustry.Vars.*;

/**
 * A type of light block in which the inputted lights are reflected.
 * @author GlennFolker
 */
@Merge(LightHoldc.class)
public class LightReflector extends LightHoldBlock{
    public float angleRange = 22.5f;
    public float rotateSpeed = 5f;

    public TextureRegion mirrorRegion;

    public LightReflector(String name){
        super(name);
        solid = true;
        requiresLight = false;
        acceptsLight = true;
        configurable = true;
        outlineIcon = true;

        config(Integer.class, (LightReflectorBuild tile, Integer value) -> {
            Building build = world.build(value);
            if(build != null && build.isValid()){
                int cur = Structs.indexOf(tile.targets, build);
                if(cur != -1){
                    tile.targets[cur] = null;
                }else{
                    int next = Structs.indexOf(tile.targets, (Building)null);
                    if(next != -1){
                        tile.targets[next] = build;
                    }
                }
            }
        });

        config(Boolean.class, (LightReflectorBuild tile, Boolean value) -> {
            if(tile.targets[0] != null || tile.targets[1] != null){
                Arrays.fill(tile.targets, null);

                tile.targetRotation = Mathf.floor(tile.targetRotation / angleRange) * angleRange;
                if(value){
                    tile.targetRotation += angleRange;
                }
            }else{
                tile.targetRotation += angleRange * Mathf.sign(value);
            }
        });
    }

    @Override
    public boolean hasRotation(Building build){
        return build instanceof LightReflectorBuild;
    }

    @Override
    public float getRotation(Building build){
        if(build instanceof LightReflectorBuild hold){
            return hold.rotation;
        }else{
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void load(){
        super.load();
        mirrorRegion = Core.atlas.find(name + "-mirror");
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{region, mirrorRegion};
    }

    public class LightReflectorBuild extends LightHoldBuild{
        public ObjectMap<Light, Light> reflect = new ObjectMap<>();

        public final Building[] targets = new Building[2];

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
        public void draw(){
            super.draw();

            Draw.z(Layer.effect + 2f);
            Draw.rect(mirrorRegion, x, y, rotation - 90f);
        }

        @Override
        public void updateTile(){
            super.updateTile();

            for(int i = 0; i < targets.length; i++){
                if(targets[i] != null && !targets[i].isValid()){
                    targets[i] = null;
                }
            }

            if(targets[0] != null && targets[1] != null){
                targetRotation = (targets[0].angleTo(this) + targets[1].angleTo(this)) / 2f;
            }

            targetRotation %= 360f;
            rotation = Angles.moveToward(rotation, targetRotation, rotateSpeed * edelta());

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
        public void buildConfiguration(Table table){
            table.button(Icon.left, () -> configure(true)).size(40f);
            table.button(Icon.right, () -> configure(false)).size(40f);
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

            for(Building target : targets){
                if(target == null) continue;

                Drawf.circles(target.x, target.y, target.tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f), Pal.place);

                int seg = (int) (dst(target.x, target.y) / tilesize);

                Lines.stroke(2f, Pal.gray);
                Lines.dashLine(x, y, target.x, target.y, seg);
                Lines.stroke(1f, Pal.placing);
                Lines.dashLine(x, y, target.x, target.y, seg);
                Draw.reset();
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(rotation);
            write.f(targetRotation);

            for(Building target : targets){
                if(target == null || !target.isValid()){
                    write.b(0);
                    continue;
                }

                write.i(target.pos());
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            rotation = read.f();
            targetRotation = read.f();

            for(int i = 0; i < 2; i++){
                targets[i] = switch(read.b()){
                    case 0 -> null;
                    case 1 -> world.build(read.i());
                    default -> throw new IllegalStateException("Illegal state");
                };
            }
        }
    }
}

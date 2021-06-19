package unity.world.blocks.light;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.production.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

import static mindustry.Vars.*;

/**
 * A type of light block in which it produces a light laser in which strength is bound to the building's efficiency.
 * The rotation of the outputted light is configurable, either manually rotating the direction or by linking it to
 * another {@link Building}.
 * @author GlennFolker
 */
@Merge(base = GenericCrafter.class, value = LightHoldc.class)
public class LightSource extends LightHoldGenericCrafter{
    public float lightProduction = 1f;

    public float angleRange = 22.5f;
    public float rotateSpeed = 5f;

    public LightSource(String name){
        super(name);
        solid = true;
        requiresLight = false;
        acceptsLight = false;
        configurable = true;
        outlineIcon = true;

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

        config(Boolean.class, (LightSourceBuild tile, Boolean value) -> {
            if(tile.target != null){
                tile.target = null;

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
        return build instanceof LightSourceBuild;
    }

    @Override
    public float getRotation(Building build){
        if(build instanceof LightSourceBuild hold){
            return hold.rotation;
        }else{
            throw new UnsupportedOperationException();
        }
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

            targetRotation %= 360f;
            rotation = Angles.moveToward(rotation, targetRotation, rotateSpeed * edelta());

            light.set(this);
            light.strength(efficiency() * lightProduction);
            light.rotation = rotation;
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            light.remove();
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

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(rotation);
            write.f(targetRotation);

            if(target != null){
                write.b(1);
                write.i(target.pos());
            }else{
                write.b(0);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            rotation = read.f();
            targetRotation = read.f();

            int type = read.b();
            target = switch(type){
                case 0 -> null;
                case 1 -> world.build(read.i());
                default -> throw new IllegalStateException("Invalid state: " + type);
            };
        }
    }
}

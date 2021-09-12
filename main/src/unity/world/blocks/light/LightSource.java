package unity.world.blocks.light;

import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.blocks.production.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

/** @author GlennFolker */
@Merge(base = GenericCrafter.class, value = LightHoldc.class)
public class LightSource extends LightHoldGenericCrafter{
    public float lightProduction = 1f;

    public LightSource(String name){
        super(name);
        solid = true;
        configurable = true;
        outlineIcon = true;

        config(Boolean.class, (LightSourceBuild tile, Boolean value) -> tile.lightRot = Light.fixRot(tile.lightRot + (value ? Light.rotationInc : -Light.rotationInc)));
    }

    @Override
    public float getRotation(Building build){
        return build instanceof LightSourceBuild b ? b.lightRot : 0f;
    }

    public class LightSourceBuild extends LightHoldGenericCrafterBuild{
        public Light light;
        public float lightRot = 90f;

        @Override
        public Float config(){
            return lightRot;
        }

        @Override
        public void created(){
            super.created();

            light = Light.create();
            light.queuePosition = SVec2.construct(x, y);
            light.queueRotation = lightRot;
            light.queueSource = this;

            light.queueAdd();
        }

        @Override
        public void onRemoved(){
            light.queueRemove();
        }

        @Override
        public void updateTile(){
            super.updateTile();

            light.queuePosition = SVec2.construct(x, y);
            light.queueRotation = lightRot;
            light.queueSource = this;
            light.queueStrength = efficiency() * lightProduction;
        }

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.left, () -> configure(true)).size(40f);
            table.button(Icon.right, () -> configure(false)).size(40f);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(lightRot);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            lightRot = read.f();
        }
    }
}

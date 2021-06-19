package unity.world.blocks.light;

import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

/**
 * A type of light block in which the inputted lights are diffracted into {@link #diffractionCount} different children. The
 * gap between each child is configurable.
 * @author GlennFolker
 */
@Merge(LightHoldc.class)
public class LightDiffractor extends LightHoldBlock{
    public int diffractionCount = 3;
    public float minAngle = 22.5f;
    public float maxAngle = 90f;

    public LightDiffractor(String name){
        super(name);
        solid = true;
        requiresLight = false;
        acceptsLight = true;
        configurable = true;

        config(Float.class, (LightDiffractorBuild tile, Float value) -> tile.angleDist = Mathf.clamp(tile.angleDist + value, minAngle, maxAngle));
    }

    public class LightDiffractorBuild extends LightHoldBuild{
        public ObjectMap<Light, Seq<Light>> diffraction = new ObjectMap<>();

        public float angleDist = (minAngle + maxAngle) / 2f;

        @Override
        public void added(Light light){
            addDiffraction(light);
        }

        @Override
        public void removed(Light light){
            removeDiffraction(light);
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            for(Light light : diffraction.keys()){
                removeDiffraction(light);
            }
        }

        protected void addDiffraction(Light light){
            var dif = diffraction.get(light, Seq::new);
            if(dif.size != diffractionCount){
                dif.each(Light::remove);
                dif.clear();

                for(int i = 0; i < diffractionCount; i++){
                    Light d = apply(light, Light.create());
                    d.strength(light.endStrength() / diffractionCount);
                    d.rotation = (light.rotation + i * angleDist) - (angleDist * (diffractionCount - 1)) / 2f;

                    d.add();
                    dif.add(d);
                }
            }
        }

        protected void removeDiffraction(Light light){
            var dif = diffraction.remove(light);
            if(dif != null){
                dif.each(Light::remove);
                dif.clear();
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();

            for(var entry : diffraction.entries()){
                var origin = entry.key;
                var dif = entry.value;

                if(dif.size != diffractionCount){
                    dif.each(Light::remove);
                    dif.clear();

                    for(int i = 0; i < diffractionCount; i++){
                        dif.add(apply(origin, Light.create()));
                    }
                }

                for(int i = 0; i < dif.size; i++){
                    Light d = dif.get(i);
                    d.strength(origin.endStrength() / diffractionCount);
                    d.rotation = (origin.rotation + i * angleDist) - (angleDist * (diffractionCount - 1)) / 2f;
                }
            }
        }

        @Override
        public void buildConfiguration(Table table){
            float big = 22.5f, small = big / 5f;

            table.button(Icon.leftSmall, () -> configure(small)).size(40);
            table.button(Icon.left, () -> configure(big)).size(40);
            table.button(Icon.right, () -> configure(-big)).size(40);
            table.button(Icon.rightSmall, () -> configure(-small)).size(40);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(angleDist);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            angleDist = read.f();
        }
    }
}

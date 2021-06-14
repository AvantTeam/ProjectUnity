package unity.world.blocks.light;

import arc.math.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.world.blocks.production.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

@Merge(base = GenericCrafter.class, value = LightHoldc.class)
public class LightSource extends LightHoldGenericCrafter{
    public LightSource(String name){
        super(name);
        requiresLight = false;
        configurable = true;

        config(Boolean.class, (LightSourceBuild tile, Boolean value) -> tile.rotation += 22.5f * Mathf.sign(value));
    }

    public class LightSourceBuild extends LightHoldGenericCrafterBuild{
        public Light light;
        public float rotation = 90f;

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

            light.set(this);
            light.strength = efficiency();
            light.rotation = rotation;
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            light.remove();
        }

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.left, () -> {
                Sounds.click.play();
                configure(true);
            });

            table.button(Icon.right, () -> {
                Sounds.click.play();
                configure(false);
            });
        }
    }
}

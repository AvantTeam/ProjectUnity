package unity.world.blocks.light;

import arc.math.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.world.blocks.production.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

@Merge(base = GenericCrafter.class, value = LightHoldc.class)
public class LightSource extends LightHoldGenericCrafter{
    public float lightProduction = 1f;
    public float rotateSpeed = 5f;

    public LightSource(String name){
        super(name);
        requiresLight = false;
        configurable = true;

        config(Boolean.class, (LightSourceBuild tile, Boolean value) -> tile.targetRotation += 22.5f * Mathf.sign(value));
    }

    public class LightSourceBuild extends LightHoldGenericCrafterBuild{
        public Light light;
        public float rotation = 90f;
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

            rotation = Mathf.approachDelta(rotation, targetRotation, rotateSpeed);

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
        public void buildConfiguration(Table table){
            table.button(Icon.left, () -> configure(true)).size(40f);
            table.button(Icon.right, () -> configure(false)).size(40f);
        }
    }
}

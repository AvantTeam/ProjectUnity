package unity.world.draw;

import arc.graphics.g2d.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.production.GenericCrafter.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import unity.gen.*;
import unity.gen.LightHoldc.*;
import unity.world.blocks.logic.*;

import static arc.Core.*;

public class DrawLightBlock extends DrawBlock{
    public TextureRegion
    topRegion, liquidRegion;

    @Override
    public void draw(GenericCrafterBuild build){
        Draw.rect(build.block.region, build.x, build.y);

        if(build.block.consumes.has(ConsumeType.liquid)){
            Draw.color(build.liquids.current().color);
            Draw.alpha(build.liquids.currentAmount() / build.block.liquidCapacity);
            Draw.rect(liquidRegion, build.x, build.y);
            Draw.color();
        }

        Draw.z(Layer.effect + 2f);
        Draw.rect(topRegion, build.x, build.y, build.block instanceof LightHoldc hold && hold.hasRotation(build) ? (hold.getRotation(build) - 90f) : 0f);
    }

    @Override
    public void load(Block block){
        topRegion = atlas.find(block.name + "-top");
        liquidRegion = atlas.find(block.name + "-liquid");
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{block.region, topRegion};
    }
}

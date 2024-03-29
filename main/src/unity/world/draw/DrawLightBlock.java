package unity.world.draw;

import arc.graphics.g2d.*;
import mindustry.world.*;
import mindustry.world.blocks.production.GenericCrafter.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import unity.gen.*;

import static arc.Core.*;

public class DrawLightBlock extends DrawBlock{
    public TextureRegion
    baseRegion, liquidRegion;

    @Override
    public void draw(GenericCrafterBuild build){
        Draw.rect(baseRegion, build.x, build.y);

        if(build.block.consumes.has(ConsumeType.liquid)){
            Draw.color(build.liquids.current().color);
            Draw.alpha(build.liquids.currentAmount() / build.block.liquidCapacity);
            Draw.rect(liquidRegion, build.x, build.y);
            Draw.color();
        }

        Draw.rect(build.block.region, build.x, build.y, build.block instanceof LightHoldc hold  ? (hold.getRotation(build) - 90f) : 0f);
    }

    @Override
    public void load(Block block){
        baseRegion = atlas.find(block.name + "-base");
        liquidRegion = atlas.find(block.name + "-liquid");
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{baseRegion, block.region};
    }
}

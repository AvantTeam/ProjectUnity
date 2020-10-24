package unity.world.blocks.light;

import arc.graphics.g2d.*;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter.*;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.draw.DrawBlock;
import mindustry.world.consumers.*;
import unity.world.blocks.light.LightSource.*;

import static arc.Core.*;

public class DrawLightSource extends DrawBlock{
	protected boolean drawLiquid = false;
	protected Liquid inputLiquid;

	@Override
	public void draw(GenericCrafterBuild entity){
		Draw.z(Layer.block);
		LightSource lightb = (LightSource) entity.block;
		Draw.rect(lightb.baseRegion, entity.x, entity.y);
		
		if(drawLiquid && entity.liquids.total() > 0.01f){
			Draw.color(inputLiquid.color);
			Draw.alpha(entity.liquids.get(inputLiquid) / lightb.liquidCapacity);
			Draw.rect(lightb.liquidRegion, entity.x, entity.y);
			Draw.color();
		}
		if(((LightSourceBuild) entity).getStrength() > lightb.lightStrength / 2f){
			Draw.z(Layer.effect - 2f);
			Draw.rect(lightb.lightRegion, entity.x, entity.y);
		}
		
		Draw.z(Layer.effect + 2f);
		Draw.rect(lightb.topRegion, entity.x, entity.y, lightb.angleConfig ? ((LightSourceBuild) entity).getAngleDeg() : entity.rotation * 90f);
		Draw.reset();
	}

	@Override
	public void load(Block block){
		LightSource lightb = (LightSource) block;
		drawLiquid = block.consumes.has(ConsumeType.liquid);
		
		lightb.baseRegion = atlas.find(block.name + "-base");
		lightb.topRegion = atlas.find(block.name + "-top");
		lightb.lightRegion = atlas.find(block.name + "-");
		
		if(lightb.lightRegion == atlas.find("error")) lightb.lightRegion = atlas.find("unity-light-center");
		if(lightb.topRegion == atlas.find("error")) lightb.topRegion = atlas.find("unity-light-lamp-top");
		if(drawLiquid){
			lightb.liquidRegion = atlas.find(block.name + "-liquid");
			inputLiquid = block.consumes.<ConsumeLiquid>get(ConsumeType.liquid).liquid;
		}
	}
}

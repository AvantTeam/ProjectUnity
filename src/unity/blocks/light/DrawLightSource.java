package unity.blocks.light;

import arc.graphics.g2d.*;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter.*;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.draw.DrawBlock;
import mindustry.world.consumers.*;
import unity.blocks.light.LightSource.*;

import static arc.Core.*;

public class DrawLightSource extends DrawBlock{
	protected boolean drawLiquid = false;
	protected Liquid inputLiquid;

	@Override
	public void draw(GenericCrafterBuild entity){
		Draw.z(Layer.block);
		LightSource what = (LightSource) entity.block;
		Draw.rect(what.baseRegion, entity.x, entity.y);
		if (drawLiquid && entity.liquids.total() > 0.01f){
			Draw.color(inputLiquid.color);
			Draw.alpha(entity.liquids.get(inputLiquid) / what.liquidCapacity);
			Draw.rect(what.liquidRegion, entity.x, entity.y);
			Draw.color();
		}
		if (((LightSourceBuild) entity).getStrength() > what.lightStrength / 2f){
			Draw.z(Layer.effect - 2f);
			Draw.rect(what.lightRegion, entity.x, entity.y);
		}
		Draw.z(Layer.effect + 2f);
		Draw.rect(what.topRegion, entity.x, entity.y,
			what.angleConfig ? ((LightSourceBuild) entity).getAngleDeg() : entity.rotation * 90f);
		Draw.reset();
	}

	@Override
	public void load(Block block){
		LightSource what = (LightSource) block;
		drawLiquid = block.consumes.has(ConsumeType.liquid);
		what.baseRegion = atlas.find(block.name + "-base");
		what.topRegion = atlas.find(block.name + "-top");
		what.lightRegion = atlas.find(block.name + "-");
		if (what.lightRegion == atlas.find("error")) what.lightRegion = atlas.find("unity-light-center");
		if (what.topRegion == atlas.find("error")) what.topRegion = atlas.find("unity-light-lamp-top");
		if (drawLiquid){
			what.liquidRegion = atlas.find(block.name + "-liquid");
			inputLiquid = block.consumes.<ConsumeLiquid>get(ConsumeType.liquid).liquid;
		}
	}
}

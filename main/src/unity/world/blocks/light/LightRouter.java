package unity.world.blocks.light;

import arc.math.Mathf;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import mindustry.graphics.Layer;
import mindustry.world.blocks.distribution.Router;

import static arc.Core.*;

public class LightRouter extends Router{
	public TextureRegion baseRegion, lightRegion;

	public LightRouter(String name){
		super(name);
	}

	public void load(){
		super.load();
		baseRegion = atlas.find(name + "-base");
		lightRegion = atlas.find("unity-light-center");
	}

	public class LightRouterBuild extends RouterBuild implements LightRepeaterBuildBase{
		protected Color getItemColor(){
			if(items.first() == null) return Color.white;
			return items.first().color;
		}

		@Override
		public LightData calcLight(LightData ld, int i){
			Color tempColor = ld.color.cpy().mul(getItemColor());
			int val = Mathf.floorPositive(tempColor.value() * ld.strength);
			if(val <= 0) return null;
			return new LightData(ld.angle, val, ld.length - i, tempColor);
		}

		@Override
		public void draw(){
			Draw.rect(baseRegion, x, y);
			Draw.color(getItemColor(), 0.7f);
			
			Draw.z(Layer.effect + 2f);
			Draw.rect(lightRegion, x, y);
			Draw.reset();
		}
	}
}

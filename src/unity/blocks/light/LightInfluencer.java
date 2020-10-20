package unity.blocks.light;

import arc.graphics.Blending;
import arc.graphics.g2d.*;
import arc.math.geom.Geometry;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.blocks.light.LightFilter.LightFilterBuild;

import static arc.Core.*;

public class LightInfluencer extends LightGenerator{
	protected TextureRegion topRegion;

	public LightInfluencer(String name){
		super(name);
	}

	@Override
	public void load(){
		super.load();
		topRegion = atlas.find(name + "-top");
	}

	public class LightInfluencerBuild extends LightGeneratorBuild{
		protected boolean[] c = {false, false, false, false};

		@Override
		public void draw(){
			Draw.z(Layer.block);
			Draw.rect(region, x, y);
			
			Draw.z(Layer.effect - 2f);
			Draw.color(lastSumColor(), 1f);
			Draw.blend(Blending.additive);
			Draw.rect(topRegion, x, y);
			for(int i = 0; i < 4; i++){
				if(c[i]) Drawf.tri(x + Geometry.d4x[i] * 2f, y + Geometry.d4y[i] * 2f, 3f, 6f, i * 90f);
			}
			
			Draw.blend();
			Draw.reset();
		}

		@Override
		public void updateTile(){
			lightSumColor();
			productionEfficiency = 0f;
		}

		@Override
		public void onProximityUpdate(){
			super.onProximityUpdate();
			for(int i = 0; i < 4; i++){
				Building build = tile.getNearbyEntity(i);
				if(build != null && build instanceof LightFilterBuild){
					((LightFilterBuild) build).cont = this;
					c[i] = true;
				}else c[i] = false;
			}
		}
	}
}

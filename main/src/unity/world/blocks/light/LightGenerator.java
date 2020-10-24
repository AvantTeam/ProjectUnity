package unity.world.blocks.light;

import java.util.ArrayList;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.graphics.Layer;
import mindustry.ui.Bar;
import mindustry.world.meta.Stat;
import mindustry.world.blocks.power.SolarGenerator;
import unity.world.blocks.light.LightSource.LightSourceBuild;

import static arc.Core.*;

public class LightGenerator extends SolarGenerator{
	protected float lightStrength = 60f;
	protected Color lightFilter = Color.white;
	protected boolean scaleStatus;
	protected boolean lightOptional;
	protected TextureRegion lightRegion;

	public LightGenerator(String name){
		super(name);
	}

	@Override
	public void load(){
		super.load();
		lightRegion = atlas.find("unity-light-center");
	}

	@Override
	public void setBars(){
		super.setBars();
		bars.add("light", (LightGeneratorBuild build) -> new Bar(() -> bundle.format("lightlib.light", build.lightPower()),
			() -> build.lightSumColor(), () -> build.lastLightStatus())
		);
	}

	@Override
	public void setStats(){
		super.setStats();
		stats.add(Stat.output, bundle.format("bar.efficiency", 6000f / lightStrength));
	}

	public class LightGeneratorBuild extends SolarGeneratorBuild{
		protected float lastLightPower;
		protected Color lastColor = Color.black.cpy();
		protected final ArrayList<LightSourceBuild> srcs = new ArrayList<>();
		protected final ArrayList<LightData> srcDatas = new ArrayList<>();

		@Override
		public void draw(){
			Draw.z(Layer.block);
			Draw.rect(region, x, y);
			Draw.z(Layer.effect - 2f);
			Draw.color(lightSumColor(), lastLightStatus());
			Draw.blend(Blending.additive);
			Draw.rect(lightRegion, x, y);
			Draw.blend();
			Draw.reset();
		}

		@Override
		public void updateTile(){
			productionEfficiency = enabled ? lightStatus() : 0f;
		}

		protected void removeSource(LightSourceBuild src){
			int index = srcs.indexOf(src);
			if(index >= 0){
				srcs.remove(index);
				srcDatas.remove(index);
			}
		}

		protected LightGeneratorBuild addSource(LightSourceBuild src, LightData srcData){
			int index = srcs.indexOf(src);
			if(index >= 0){
				LightData tempData = srcDatas.get(index);
				tempData.strength += srcData.strength;
				tempData.color = tempData.color.cpy().add(srcData.color.cpy().mul(srcData.strength / 100f));
			}else{
				srcs.add(src);
				srcDatas.add(new LightData(srcData).color(srcData.color.cpy().mul(srcData.strength / 100f)));
			}
			return this;
		}

		protected void validateSource(){
			for(int i = 0, len = srcs.size(); i < len; i++){
				if(!srcs.get(i).isValid()){
					srcs.remove(i);
					srcDatas.remove(i);
					i--;
				}
			}
		}

		protected float lightPower(){
			lastLightPower = 0f;
			validateSource();
			for(int i = 0; i < srcs.size(); i++) lastLightPower += srcs.get(i).getStrength() * srcDatas.get(i).strength / 100f;
			return lastLightPower;
		}

		protected float lastPower(){
			return lastLightPower;
		}

		protected float lightStatus(){
			float ret = lightPower() / lightStrength;
			if(!scaleStatus) ret = Math.min(ret, 1f);
			return ret;
		}

		protected float lastLightStatus(){
			float ret = lastLightPower / lightStrength;
			if(!scaleStatus) ret = Math.min(ret, 1f);
			return ret;
		}

		protected Color lightSumColor(){
			lastColor = Color.black.cpy();
			validateSource();
			for(int i = 0, len = srcDatas.size(); i < len; i++){
				lastColor.add(srcDatas.get(i).color.cpy().mul(srcDatas.get(i).strength / 100f));
				if(lastColor.equals(Color.white)) break;
			}
			if(!lastColor.equals(Color.black)) lastColor = lastColor.shiftValue(1f - lastColor.value());
			return lastColor;
		}

		protected Color lastSumColor(){
			return lastColor;
		}

		public boolean consValid(){
			return cons.valid() && (lightOptional || lastLightPower >= lightStrength);
		}
	}
}

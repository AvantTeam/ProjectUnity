package unity.blocks.light;

import arc.graphics.Color;
import mindustry.world.blocks.production.GenericCrafter;

public class LightSource extends GenericCrafter{
	protected int lightStrength = 60, lightLength = 50, maxLightLength = 5000, maxReflections = 128, lightInterval = 20;
	protected final int reflowTimer = timers++;
	protected Color lightColor = Color.white;
	protected boolean scaleStatus = true, angleConfig = false;
	public final boolean hasCustomUpdate;

	public LightSource(String name, boolean hasCustomUpdate){
		super(name);
		this.hasCustomUpdate = hasCustomUpdate;
		update = true;
		rotate = true;
	}

	public LightSource(String name){ this(name, false); }

	@Override
	public void setBars(){ 
		super.setBars(); 
		//bars;
	}

	public class LightSourceBuild extends GenericCrafterBuild{

		protected float getPowerStatus(){
			if (!hasPower || power == null) return 1f;
			return power.status;
		}

		@Override
		public void updateTile(){
			if (hasCustomUpdate) customUpdate();
			else super.updateTile();
		}

		protected void customUpdate(){

		}
	}
}

package unity.blocks.light;

import mindustry.world.blocks.defense.Wall;

public class LightWall extends Wall{
	public LightWall(String name){
		super(name);
		update = true;
		absorbLasers = true;
	}

	public class LightWallBuild extends WallBuild implements LightRepeaterBuildBase{
		
	}
}

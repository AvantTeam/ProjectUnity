package unity.blocks.light;

import mindustry.world.blocks.defense.Wall;

//blame sk
public class LightWall extends Wall{
	public LightWall(String name){ super(name); }

	public class LightWallBuild extends WallBuild implements LightRepeaterBuildBase{}
}

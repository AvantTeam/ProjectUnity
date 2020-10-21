package unity.blocks.light;

import arc.util.Eachable;
import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.world.Block;
import mindustry.entities.units.BuildPlan;

import static arc.Core.*;
import static mindustry.Vars.*;

public class LightReflector extends Block{
	protected boolean diagonal = true;
	protected final TextureRegion[] angleRegions = new TextureRegion[2];
	/** change this */
	private static final String spriteName = "unity-light-reflector";
	public static final int[][] ref = {{6, 5, 4, -1, 2, 1, 0, -1}, {2, -1, 0, 7, 6, -1, 4, 3},
		{-1, 7, 6, 5, -1, 3, 2, 1}, {4, 3, -1, 1, 0, 7, -1, 5}};

	public LightReflector(String name){
		super(name);
		update = true;
		solid = true;
		rotate = true;
	}

	@Override
	public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
		final float scl = tilesize * req.animScale;
		Draw.rect(angleRegions[req.rotation % 2], req.drawx(), req.drawy(), scl, scl);
	}

	@Override
	public void load(){
		super.load();
		angleRegions[0] = atlas.find(spriteName + (diagonal ? "" : "-1"));
		angleRegions[1] = atlas.find(spriteName + (diagonal ? "-2" : "-3"));
	}

	public class LightReflectorBuild extends Building{
		public int calcReflection(int dir){ return ref[rotation % 2 + (diagonal ? 0 : 2)][dir]; }

		@Override
		public void draw(){ Draw.rect(angleRegions[rotation % 2], x, y); }
	}
}

package unity.blocks.light;

import arc.util.Eachable;
import arc.util.io.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.Table;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.ui.Styles;
import mindustry.entities.units.BuildPlan;

import static arc.Core.*;
import static mindustry.Vars.*;

public class LightOmniReflector extends LightReflector{
	protected TextureRegion baseRegion, topRegion;
	public static final int[][] refomni = {ref[2], {1, 0, 7, 6, 5, 4, 3, 2}, ref[1], {3, 2, 1, 0, 7, 6, 5, 4}, ref[3], {5, 4, 3, 2, 1, 0, 7, 6}, ref[0], {7, 6, 5, 4, 3, 2, 1, 0}};

	public LightOmniReflector(String name){
		super(name);
		configurable = true;
		saveConfig = true;
		lastConfig = 0;
		config(Integer.class, (LightOmniReflectorBuild build, Integer value) -> {
			build.addAngle(value);
		});
	}

	@Override
	public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
		final float scl = tilesize * req.animScale;
		Draw.rect(baseRegion, req.drawx(), req.drawy(), scl, scl);
		if(req.config != null) drawRequestConfig(req, list);
	}

	@Override
	public void drawRequestConfig(BuildPlan req, Eachable<BuildPlan> list){
		final float scl = tilesize * req.animScale;
		Draw.rect(topRegion, req.drawx(), req.drawy(), scl, scl, req.config == null ? 0 : (int) req.config * 22.5f + req.rotation * 90f);
	}

	@Override
	public void load(){
		super.load();
		baseRegion = atlas.find(name + "-base");
		topRegion = atlas.find(name + "-mirror");
	}

	public class LightOmniReflectorBuild extends LightReflectorBuild{
		protected int rotconf = 0;

		protected void addAngle(int a){
			rotconf += a;
			rotconf = (rotconf % 8 + 8) % 8;
		}

		@Override
		public int calcReflection(int dir){
			return refomni[(rotation * 4 + rotconf + 8) % 8][dir];
		}

		@Override
		public void draw(){
			Draw.rect(baseRegion, x, y);
			Draw.z(Layer.effect + 2f);
			Draw.rect(topRegion, x, y, rotconf * 22.5f + rotation * 90f);
			Draw.reset();
		}

		@Override
		public Integer config(){
			return rotconf;
		}

		@Override
		public void buildConfiguration(Table table){
			table.button(Icon.leftOpen, Styles.clearTransi, 34, () -> {
				configure(1);
			}).size(40);
			table.button(Icon.rightOpen, Styles.clearTransi, 34, () -> {
				configure(-1);
			}).size(40);
		}

		@Override
		public void write(Writes write){
			super.write(write);
			write.b(rotconf % 8);
		}

		@Override
		public void read(Reads read, byte revision){
			super.read(read, revision);
			rotconf = read.b();
		}
	}
}

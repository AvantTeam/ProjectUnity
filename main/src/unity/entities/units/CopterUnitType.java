package unity.entities.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.Time;
import mindustry.type.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.ai.types.FlyingAI;

import static mindustry.Vars.*;

public class CopterUnitType extends UnitType{
	public Seq<Rotor> rotors = new Seq<>();
	protected float fallRotateSpeed = 2.5f;

	public CopterUnitType(String name){
		super(name);
		defaultController = () -> new CopterAI();
	}

	@Override
	public void load(){
		super.load();
		rotors.each(Rotor::load);
	}

	@Override
	public void init(){
		super.init();

		Seq<Rotor> mapped = new Seq<>();

		rotors.each(rotor -> {
			mapped.add(rotor);

			if(rotor.mirror){
				Rotor copy = rotor.copy();
				copy.x *= -1f;
				copy.speed *= -1f;
				copy.rotOffset += 180f; //might change later

				mapped.add(copy);
			}
		});

		rotors = mapped;
	}

	@Override
	public void draw(Unit unit){
		super.draw(unit);

		Draw.mixcol(Color.white, unit.hitTime);

		rotors.each(rotor -> {
			TextureRegion region = rotor.bladeRegion;

			float offX = Angles.trnsx(unit.rotation - 90, rotor.x, rotor.y);
			float offY = Angles.trnsy(unit.rotation - 90, rotor.x, rotor.y);

			float w = region.width * rotor.scale * Draw.scl;
			float h = region.height * rotor.scale * Draw.scl;

			for(int j = 0; j < rotor.bladeCount; j++){
				float angle = (unit.id * 24f + Time.time() * rotor.speed + (360f / (float) rotor.bladeCount) * j + rotor.rotOffset) % 360;
				Draw.alpha(state.isPaused() ? 1f : Time.time() % 2);

				Draw.rect(region, unit.x + offX, unit.y + offY, w, h, angle);
			}

			Draw.alpha(1f);
			Draw.rect(rotor.topRegion, unit.x + offX, unit.y + offY, unit.rotation - 90f);
		});

		Draw.mixcol();
	}

	public class Rotor{
		public TextureRegion bladeRegion;
		public TextureRegion topRegion;

		public boolean mirror = false;

		public float x = 0f;
		public float y = 0f;
		public float scale = 1f;

		public float rotOffset = 0f;
		public float speed = 29f;

		public int bladeCount = 4;

		public void load(){
			bladeRegion = Core.atlas.find(name + "-rotor-blade");
			topRegion = Core.atlas.find(name + "-rotor-top");
		}

		public Rotor copy(){
			Rotor out = new Rotor();
			JsonIO.json().copyFields(this, out);

			return out;
		}
	}

	public class CopterAI extends FlyingAI{
		@Override
		protected void attack(float attackLength){
			moveTo(target, unit.range() * 0.8f);
			unit.lookAt(target);
		}
	}
}

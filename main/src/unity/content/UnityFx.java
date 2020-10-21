package unity.content;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;

import static arc.graphics.g2d.Draw.*;
import static arc.math.Angles.*;

public class UnityFx{
	public static final Effect

	shootSmallBlaze = new Effect(22, e -> {
		color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin());
		randLenVectors(e.id, 16, e.finpow() * 60f, e.rotation, 18f, (x, y) -> {
			Fill.circle(e.x + x, e.y + y, 0.85f + e.fout() * 3.5f);
		});
	}),

	shootPyraBlaze = new Effect(32, e -> {
		color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());
		randLenVectors(e.id, 16, e.finpow() * 60f, e.rotation, 18f, (x, y) -> {
			Fill.circle(e.x + x, e.y + y, 0.85f + e.fout() * 3.5f);
		});
	}),

	craftingEffect = new Effect(67, 35, e -> {
		float value = Mathf.randomSeed(e.id);
		Tmp.v1.trns(value * 360f + ((value + 4f) * e.fin() * 80f), (Mathf.randomSeed(e.id * 126) + 1f) * 34f * (1f - e.finpow()));
		color(Color.valueOf("ff9c5a"));
		Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fslope() * 3, 45);
		color();
	});
}

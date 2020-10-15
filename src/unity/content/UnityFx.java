package unity.content;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;

import static arc.graphics.g2d.Draw.*;
import static arc.math.Angles.*;

public class UnityFx{
	public static final Effect 

	shootSmallBlaze	= new Effect(22, e -> {
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
	});
}

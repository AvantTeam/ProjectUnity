package unity.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.graphics.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static arc.math.Mathf.*;
import static mindustry.graphics.Drawf.*;
import static mindustry.graphics.Layer.*;
import static mindustry.graphics.Pal.*;

public class UnityFx{
	public static final Effect

	shootSmallBlaze = new Effect(22, e -> {
		color(lightFlame, darkFlame, gray, e.fin());
		randLenVectors(e.id, 16, e.finpow() * 60f, e.rotation, 18f, (x, y) -> {
			Fill.circle(e.x + x, e.y + y, 0.85f + e.fout() * 3.5f);
		});
	}),

	shootPyraBlaze = new Effect(32, e -> {
		color(lightPyraFlame, darkPyraFlame, gray, e.fin());
		randLenVectors(e.id, 16, e.finpow() * 60f, e.rotation, 18f, (x, y) -> {
			Fill.circle(e.x + x, e.y + y, 0.85f + e.fout() * 3.5f);
		});
	}),

	craftingEffect = new Effect(67, 35, e -> {
		float value = randomSeed(e.id);
		Tmp.v1.trns(value * 360f + ((value + 4f) * e.fin() * 80f), (randomSeed(e.id * 126) + 1f) * 34f * (1f - e.finpow()));
		color(Color.valueOf("ff9c5a"));
		Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fslope() * 3, 45);
		color();
	}),

	orbHit = new Effect(12, e -> {
		color(surge);
		stroke(e.fout() * 1.5f);
		randLenVectors(e.id, 8, e.finpow() * 17, e.rotation, 360, (x, y) -> {
			float ang = Mathf.angle(x, y);
			lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 1);
		});
	}),

	orbShoot = new Effect(21, e -> {
		color(surge);
		for(int i = 0; i < 2; i++){
			int l = Mathf.signs[i];
			tri(e.x, e.y, 4 * e.fout(), 29, e.rotation + 67 * l);
		}
	}),

	orbTrail = new Effect(43, e -> {
		float originalZ = z();

		Tmp.v1.trns(randomSeed(e.id) * 360, randomSeed(e.id * 341) * 12 * e.fin());

		z(bullet - 0.01f);
		light(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 4.7f * e.fout() + 3, surge, 0.6f);

		color(surge);
		Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout() * 2.7f);

		z(originalZ);
	}),

	orbShootSmoke = new Effect(26, e -> {
		color(Pal.surge);
		randLenVectors(e.id, 7, 80f, e.rotation, 0, (x, y) -> {
			Fill.circle(e.x + x, e.y + y, e.fout() * 4);
		});
	}),

	orbCharge = new Effect(38, e -> {
		color(surge);
		randLenVectors(e.id, 2, 1f + 20f * e.fout(), e.rotation, 120, (x, y) -> {
			lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3 + 1);
		});
	}),

	orbChargeBegin = new Effect(71, e -> {
		color(Pal.surge);
		Fill.circle(e.x, e.y, e.fin() * 3);

		color();
		Fill.circle(e.x, e.y, e.fin() * 2);
	}),
    
    currentCharge = new Effect(32, e -> {
        color(Pal.surge, Color.white, e.fin());
        randLenVectors(e.id, 8, 420f + random(24f, 28f) * e.fout(), e.rotation, 4, (x, y) -> {
            stroke(0.3f + e.fout() * 2f);
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 14f + 0.5f);
        });
        
        stroke(e.fin() * 1.5f);
        circle(e.x, e.y, e.fout() * 60f);
    }),
    
    currentChargeBegin = new Effect(260, e -> {
        color(Pal.surge);
        Fill.circle(e.x, e.y, e.fin() * 7f);
        
        color();
        Fill.circle(e.x, e.y, e.fin() * 3f);
    });
}

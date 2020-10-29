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

public class UnityFx{
	public static final Effect

	shootSmallBlaze = new Effect(22, e -> {
		color(Pal.lightFlame, Pal.darkFlame, Pal.gray, e.fin());
		randLenVectors(e.id, 16, e.finpow() * 60f, e.rotation, 18f, (x, y) -> {
			Fill.circle(e.x + x, e.y + y, 0.85f + e.fout() * 3.5f);
		});
	}),

	shootPyraBlaze = new Effect(32, e -> {
		color(Pal.lightPyraFlame, Pal.darkPyraFlame, Pal.gray, e.fin());
		randLenVectors(e.id, 16, e.finpow() * 60f, e.rotation, 18f, (x, y) -> {
			Fill.circle(e.x + x, e.y + y, 0.85f + e.fout() * 3.5f);
		});
	}),

	craftingEffect = new Effect(67, 35, e -> {
		float value = randomSeed(e.id);
        
		Tmp.v1.trns(value * 360f + ((value + 4f) * e.fin() * 80f), (randomSeed(e.id * 126) + 1f) * 34f * (1f - e.finpow()));
        
		color(Color.valueOf("ff9c5a"));
		Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fslope() * 3f, 45f);
		color();
	}),

	orbHit = new Effect(12, e -> {
		color(Pal.surge);
		stroke(e.fout() * 1.5f);
		randLenVectors(e.id, 8, e.finpow() * 17f, e.rotation, 360f, (x, y) -> {
			float ang = Mathf.angle(x, y);
			lineAngle(e.x + x, e.y + y, ang, e.fout() * 4f + 1f);
		});
	}),

	orbShoot = new Effect(21, e -> {
		color(Pal.surge);
		for(int i = 0; i < 2; i++){
			int l = Mathf.signs[i];
			tri(e.x, e.y, 4f * e.fout(), 29f, e.rotation + 67 * l);
		}
	}),

	orbTrail = new Effect(43, e -> {
		float originalZ = z();

		Tmp.v1.trns(randomSeed(e.id) * 360f, randomSeed(e.id * 341) * 12f * e.fin());

		z(Layer.bullet - 0.01f);
		light(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 4.7f * e.fout() + 3f, Pal.surge, 0.6f);

		color(Pal.surge);
		Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout() * 2.7f);

		z(originalZ);
	}),

	orbShootSmoke = new Effect(26, e -> {
		color(Pal.surge);
		randLenVectors(e.id, 7, 80f, e.rotation, 0f, (x, y) -> {
			Fill.circle(e.x + x, e.y + y, e.fout() * 4f);
		});
	}),

	orbCharge = new Effect(38, e -> {
		color(Pal.surge);
		randLenVectors(e.id, 2, 1f + 20f * e.fout(), e.rotation, 120f, (x, y) -> {
			lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3f + 1f);
		});
	}),

	orbChargeBegin = new Effect(71, e -> {
		color(Pal.surge);
		Fill.circle(e.x, e.y, e.fin() * 3f);

		color();
		Fill.circle(e.x, e.y, e.fin() * 2f);
	}),
    
    currentCharge = new Effect(32, e -> {
        color(Pal.surge, Color.white, e.fin());
        randLenVectors(e.id, 8, 420f + random(24f, 28f) * e.fout(), e.rotation, 4f, (x, y) -> {
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
    }),
    
    plasmaCharge = new Effect(96, e -> {
        color(Pal.surge);
        randLenVectors(e.id, 5, (1f - e.finpow()) * 24f, e.rotation, 360f, (x, y) -> {
            tri(e.x + x, e.y + y, e.fout() * 10f, e.fout() * 11f, e.rotation);
            tri(e.x + x, e.y + y, e.fout() * 8f, e.fout() * 9f, e.rotation);
        });
    }),
    
    plasmaChargeBegin = new Effect(250, e -> {
        color(Pal.surge);
        tri(e.x, e.y, e.fin() * 16f, e.fout() * 20f, e.rotation);
    }),
    
    plasmaShoot = new Effect(36, e -> {
        color(Pal.surge, Color.white, e.fin());
	
        randLenVectors(e.id, 8, e.fin() * 20f + 1f, e.rotation, 40f, (x, y) -> {
            tri(e.x + x, e.y + y, e.fout() * 14f, e.fout() * 15f, e.rotation);
            tri(e.x + x, e.y + y, e.fout() * 8f, e.fout() * 9f, e.rotation);
        });
        
        randLenVectors(e.id, 4, e.fin() * 20f + 1f, e.rotation, 40f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 18f + 3f);
        });
    }),
    
    plasmaTriangleHit = new Effect(30, e -> {
        color(Pal.surge);
        
        stroke(e.fout() * 2.8f);
        Lines.circle(e.x, e.y, e.fin() * 60);
    }),

    plasmaFragAppear = new Effect(12, e -> {
        z(Layer.bullet - 0.01f);
        
        color(Color.white);
        tri(e.x, e.y, e.fin() * 12f, e.fin() * 13f, e.rotation);
        
        z();
    }),

    plasmaFragDisappear = new Effect(12, e -> {
        z(Layer.bullet - 0.01f);
        
        color(Pal.surge, Color.white, e.fin());
        tri(e.x, e.y, e.fout() * 10f, e.fout() * 11f, e.rotation);
        
        z();
    });
}

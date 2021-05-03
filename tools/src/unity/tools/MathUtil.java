package unity.tools;

import arc.graphics.*;
import arc.math.*;

public class MathUtil{
	private static final float SQRT_HALF = Mathf.sqrt(0.5f);

	static Color colorLerp(Color a, Color b, float frac){
		return a.set(
			pythagoreanLerp(a.r, b.r, frac),
			pythagoreanLerp(a.g, b.g, frac),
			pythagoreanLerp(a.b, b.b, frac),
			pythagoreanLerp(a.a, b.a, frac)
		);
	}

	static Color averageColor(Color a, Color b){
		return a.set(
			pythagoreanAverage(a.r, b.r),
			pythagoreanAverage(a.g, b.g),
			pythagoreanAverage(a.b, b.b),
			pythagoreanAverage(a.a, b.a)
		);
	}

	/** Pythagorean-style interpolation will result in color transitions that appear more natural than linear interpolation */
	static float pythagoreanLerp(float a, float b, float frac){
		if(a == b || frac <= 0) return a;
		if(frac >= 1) return b;

		a *= a * (1 - frac);
		b *= b * frac;

		return Mathf.sqrt(a + b);
	}

	static float pythagoreanAverage(float a, float b){
		return Mathf.sqrt(a * a + b * b) * SQRT_HALF;
	}
}

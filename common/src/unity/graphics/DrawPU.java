package unity.graphics;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import unity.util.*;

public class DrawPU{
    private static final Vec2 v = new Vec2();
    private static final Rand rand = new Rand(), rand2 = new Rand();
    private static final float[] vertices = new float[6];

    public static void diamond(){

    }

    /** @author EyeOfDarkness */
    public static void shiningCircle(int seed, float x, float y, float radius,
                                     int spikes, float spikeDuration, float durationRange, float angleDrift,
                                     float spikeWidth, float spikeHeight, float slopeBias){
        shiningCircle(seed, Time.time, x, y, radius, spikes, spikeDuration, durationRange, angleDrift, spikeWidth, spikeHeight, slopeBias, Interp.smooth);
    }

    /** @author EyeOfDarkness */
    public static void shiningCircle(int seed, float time, float x, float y, float radius,
                                     int spikes, float spikeDuration, float durationRange, float angleDrift,
                                     float spikeWidth, float spikeHeight, float slopeBias, Interp smooth){
        rand.setSeed(seed);

        Fill.circle(x, y, radius);
        spikeWidth = Math.min(spikeWidth, 90f);

        for(int i = 0; i < spikes; i++){
            float d = spikeDuration * (durationRange > 0 ? rand.random(1f - durationRange, 1f + durationRange) : 1f);
            float timeOffset = d * rand.nextFloat();
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();

            float fin = ((time + timeOffset) % d) / d;
            float fslope = smooth.apply(MathUtils.slope(fin, slopeBias));

            rand2.setSeed(timeSeed);

            if(fslope > 0.0001f){
                float height = rand2.random(0.5f, 1f) * spikeHeight;
                float width = rand2.random(0.75f, 1f) * spikeWidth;

                float angle = rand2.random(360f) + (angleDrift <= 0f ? 0f : rand2.range(angleDrift) * fin);
                int idx = 0;
                for(int j = 0; j < 3; j++){
                    float ang = (-width + (width * j)) + angle;
                    v.trns(ang, radius + (j == 1 ? (height * fslope) : 0f)).add(x, y);
                    vertices[idx++] = v.x;
                    vertices[idx++] = v.y;
                }
                Fill.tri(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5]);
            }
        }
    }
}

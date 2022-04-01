package unity.graphics;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.graphics.*;

public class UnityDrawf{
    private static final TextureRegion nRegion = new TextureRegion();
    private static final Vec2
        vec1 = new Vec2(), vec2 = new Vec2(), vec3 = new Vec2(), vec4 = new Vec2(),
        vec5 = new Vec2(), vec6 = new Vec2(), vec7 = new Vec2(), vec8 = new Vec2(),
        vec9 = new Vec2(), vec10 = new Vec2(), vec11 = new Vec2(), vec12 = new Vec2(),
        vec13 = new Vec2(), vec14 = new Vec2(), vec15 = new Vec2(), vec16 = new Vec2();

    private static final Vec3 v31 = new Vec3();
    private static final Mat3D m41 = new Mat3D();

    public static final byte[] tileMap = {
        39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
        38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
        39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
        38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
         3,  4,  3,  4, 15, 40, 15, 20,  3,  4,  3,  4, 15, 40, 15, 20,
         5, 28,  5, 28, 29, 10, 29, 23,  5, 28,  5, 28, 31, 11, 31, 32,
         3,  4,  3,  4, 15, 40, 15, 20,  3,  4,  3,  4, 15, 40, 15, 20,
         2, 30,  2, 30,  9, 47,  9, 22,  2, 30,  2, 30, 14, 44, 14,  6,
        39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
        38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
        39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
        38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
         3,  0,  3,  0, 15, 42, 15, 12,  3,  0,  3,  0, 15, 42, 15, 12,
         5,  8,  5,  8, 29, 35, 29, 33,  5,  8,  5,  8, 31, 34, 31,  7,
         3,  0,  3,  0, 15, 42, 15, 12,  3,  0,  3,  0, 15, 42, 15, 12,
         2,  1,  2,  1,  9, 45,  9, 19,  2,  1,  2,  1, 14, 18, 14, 13
    };

    //private static final float[] v = new float[6];
    private static final float[] vertices = new float[24];

    public static final Batch altBatch = new SortedSpriteBatch();

    public static void dashCircleAngle(float x, float y, float radius, float rotation){
        float scaleFactor = 0.6f;
        int sides = 10 + (int)(radius * scaleFactor);
        if(sides % 2 == 1) sides++;

        vec1.set(0, 0);

        for(int i = 0; i < sides; i++){
            if(i % 2 == 0) continue;
            vec1.set(radius, 0).setAngle((360f / sides * i + 90) + rotation);
            float x1 = vec1.x;
            float y1 = vec1.y;

            vec1.set(radius, 0).setAngle((360f / sides * (i + 1) + 90) + rotation);

            Lines.line(x1 + x, y1 + y, vec1.x + x, vec1.y + y);
        }
    }

    public static void shiningCircle(int seed, float time, float x, float y, float radius, int spikes, float spikeDuration, float spikeWidth, float spikeHeight){
        shiningCircle(seed, time, x, y, radius, spikes, spikeDuration, spikeWidth, spikeHeight, 0f);
    }

    public static void shiningCircle(int seed, float time, float x, float y, float radius, int spikes, float spikeDuration, float spikeWidth, float spikeHeight, float angleDrift){
        shiningCircle(seed, time, x, y, radius, spikes, spikeDuration, 0f, spikeWidth, spikeHeight, angleDrift);
    }

    public static void shiningCircle(int seed, float time, float x, float y, float radius, int spikes, float spikeDuration, float durationRange, float spikeWidth, float spikeHeight, float angleDrift){
        Fill.circle(x, y, radius);
        spikeWidth = Math.min(spikeWidth, 90f);
        int idx;

        for(int i = 0; i < spikes; i++){
            float d = spikeDuration * (durationRange > 0f ? Mathf.randomSeed((seed + i) * 41L, 1f - durationRange, 1f + durationRange) : 1f);
            float timeOffset = Mathf.randomSeed((seed + i) * 314L, 0f, d);
            int timeSeed = Mathf.floor((time + timeOffset) / d);
            float fin = ((time + timeOffset) % d) / d;
            float fslope = (0.5f - Math.abs(fin - 0.5f)) * 2f;
            float angle = Mathf.randomSeed(Math.max(timeSeed, 1) + ((i + seed) * 245L), 360f);
            if(fslope > 0.0001f){
                idx = 0;
                float drift = angleDrift > 0 ? Mathf.randomSeed(Math.max(timeSeed, 1) + ((i + seed) * 162L), -angleDrift, angleDrift) * fin : 0f;
                for(int j = 0; j < 3; j++){
                    float angB = (j * spikeWidth - (2f) * spikeWidth / 2f) + angle;
                    Tmp.v1.trns(angB + drift, radius + (j == 1 ? (spikeHeight * fslope) : 0f)).add(x, y);
                    vertices[idx++] = Tmp.v1.x;
                    vertices[idx++] = Tmp.v1.y;
                }
                Fill.tri(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5]);
            }
        }
    }

    public static void panningCircle(TextureRegion region, float x, float y, float w, float h, float radius, float arcCone, float arcRotation, Quat rotation, float layerLow, float layerHigh){
        panningCircle(region, x, y, w, h, radius, arcCone, arcRotation, rotation, false, layerLow, layerHigh, 150f);
    }

    public static void panningCircle(TextureRegion region, float x, float y, float w, float h, float radius, float arcCone, float arcRotation, Quat rotation, boolean useLinePrecision, float layerLow, float layerHigh){
        panningCircle(region, x, y, w, h, radius, arcCone, arcRotation, rotation, useLinePrecision, layerLow, layerHigh, 150f);
    }

    public static void panningCircle(TextureRegion region, float x, float y, float w, float h, float radius, float arcCone, float arcRotation, Quat rotation, boolean useLinePrecision, float layerLow, float layerHigh, float perspectiveDst){
        float z = Draw.z();

        float arc = arcCone / 360f;
        int sides = useLinePrecision ? (int)(Lines.circleVertices(radius) * arc) : (int)((Mathf.PI2 * radius * arc) / w);
        float space = arcCone / sides;
        float hstep = (Lines.getStroke() * h / 2f) / Mathf.cosDeg(space / 2f);
        float r1 = radius - hstep, r2 = radius + hstep;

        for(int i = 0; i < sides; i++){
            float a = arcRotation - arcCone / 2f + space * i,
                cos = Mathf.cosDeg(a), sin = Mathf.sinDeg(a),
                cos2 = Mathf.cosDeg(a + space), sin2 = Mathf.sinDeg(a + space);
            m41.idt().rotate(rotation);

            Mat3D.rot(v31.set(r1 * cos, r1 * sin, 0f), m41).scl(Math.max((perspectiveDst + v31.z) / perspectiveDst, 0f));
            float x1 = x + v31.x;
            float y1 = y + v31.y;
            float sumZ = v31.z;

            Mat3D.rot(v31.set(r1 * cos2, r1 * sin2, 0f), m41).scl(Math.max((perspectiveDst + v31.z) / perspectiveDst, 0f));
            float x2 = x + v31.x;
            float y2 = y + v31.y;
            sumZ += v31.z;

            Mat3D.rot(v31.set(r2 * cos2, r2 * sin2, 0f), m41).scl(Math.max((perspectiveDst + v31.z) / perspectiveDst, 0f));
            float x3 = x + v31.x;
            float y3 = y + v31.y;
            sumZ += v31.z;

            Mat3D.rot(v31.set(r2 * cos, r2 * sin, 0f), m41).scl(Math.max((perspectiveDst + v31.z) / perspectiveDst, 0f));
            float x4 = x + v31.x;
            float y4 = y + v31.y;
            sumZ = (sumZ + v31.z) / 4f;

            Draw.z(sumZ >= 0f ? layerHigh : layerLow);
            Fill.quad(region, x3, y3, x2, y2, x1, y1, x4, y4);
        }

        Draw.z(z);
    }

    public static void distortRect(TextureRegion region, float x, float y, float width, float height, float rotation, Skewer skew){
        float hw = width / 2f, hh = height / 2f;
        distortRect(region, x - hw, y - hh, hw, hh, width, height, rotation, skew);
    }

    //TODO does it really have to be drawn manually?
    public static void distortRect(TextureRegion region, float x, float y, float pivotX, float pivotY, float width, float height, float rotation, Skewer skew){
        Texture tex = region.texture;

        // Outer vertices.
        Vec2
            o1 = vec1.set(x, y).add(skew.get(vec1.x, vec1.y, x, y, rotation, pivotX, pivotY, width, height, vec15)),
            o2 = vec2.set(x + width, y).add(skew.get(vec2.x, vec2.y, x, y, rotation, pivotX, pivotY, width, height, vec15)),
            o3 = vec3.set(x + width, y + height).add(skew.get(vec3.x, vec3.y, x, y, rotation, pivotX, pivotY, width, height, vec15)),
            o4 = vec4.set(x, y + height).add(skew.get(vec4.x, vec4.y, x, y, rotation, pivotX, pivotY, width, height, vec15));

        // Middle vertices.
        Vec2
            m1 = vec5.set(x + width / 2f, y).add(skew.get(vec5.x, vec5.y, x, y, rotation, pivotX, pivotY, width, height, vec15)),
            m2 = vec6.set(x + width, y + height / 2f).add(skew.get(vec6.x, vec6.y, x, y, rotation, pivotX, pivotY, width, height, vec15)),
            m3 = vec7.set(x + width / 2f, y + height).add(skew.get(vec7.x, vec7.y, x, y, rotation, pivotX, pivotY, width, height, vec15)),
            m4 = vec8.set(x, y + height / 2f).add(skew.get(vec8.x, vec8.y, x, y, rotation, pivotX, pivotY, width, height, vec15));

        // Inner vertices.
        Vec2
            i1 = vec9.set(x + width / 4f, y + height / 4f).add(skew.get(vec9.x, vec9.y, x, y, rotation, pivotX, pivotY, width, height, vec15)),
            i2 = vec10.set(x + width * 3f/4f, y + height / 4f).add(skew.get(vec10.x, vec10.y, x, y, rotation, pivotX, pivotY, width, height, vec15)),
            i3 = vec11.set(x + width * 3f/4f, y + height * 3f/4f).add(skew.get(vec11.x, vec11.y, x, y, rotation, pivotX, pivotY, width, height, vec15)),
            i4 = vec12.set(x + width / 4f, y + height * 3f/3f).add(skew.get(vec12.x, vec12.y, x, y, rotation, pivotX, pivotY, width, height, vec15));

        // Center vertex.
        Vec2 c = vec13.set(x + width / 2f, y + height / 2f).add(skew.get(vec14.x, vec14.y, x, y, rotation, pivotX, pivotY, width, height, vec15));

        // UV mappings.
        float
            u1 = region.u, u5 = region.u2,
            u2 = Mathf.lerp(u1, u5, 0.25f),
            u3 = Mathf.lerp(u1, u5, 0.5f),
            u4 = Mathf.lerp(u1, u5, 0.75f),

            v1 = region.v2, v5 = region.v,
            v2 = Mathf.lerp(v1, v5, 0.25f),
            v3 = Mathf.lerp(v1, v5, 0.5f),
            v4 = Mathf.lerp(v1, v5, 0.75f);

        if(!Mathf.zero(rotation)){
            float theta = rotation * Mathf.degRad;

            // Rotation pivot point.
            vec14.set(x + pivotX, y + pivotY);

            // Rotate vertices.
            vec1.rotateAroundRad(vec14, theta);
            vec2.rotateAroundRad(vec14, theta);
            vec3.rotateAroundRad(vec14, theta);
            vec4.rotateAroundRad(vec14, theta);
            vec5.rotateAroundRad(vec14, theta);
            vec6.rotateAroundRad(vec14, theta);
            vec7.rotateAroundRad(vec14, theta);
            vec8.rotateAroundRad(vec14, theta);
            vec9.rotateAroundRad(vec14, theta);
            vec10.rotateAroundRad(vec14, theta);
            vec11.rotateAroundRad(vec14, theta);
            vec12.rotateAroundRad(vec14, theta);
            vec13.rotateAroundRad(vec14, theta);
        }

        quad(tex,
            o1, u1, v1,
            m1, u3, v1,
            i1, u2, v2,
            m4, u1, v3
        );

        quad(tex,
            o2, u5, v1,
            m2, u5, v3,
            i2, u4, v2,
            m1, u3, v1
        );

        quad(tex,
            o3, u5, v5,
            m3, u3, v5,
            i3, u4, v4,
            m2, u5, v3
        );

        quad(tex,
            o4, u1, v5,
            m4, u1, v3,
            i4, u2, v4,
            m3, u3, v5
        );

        quad(tex,
            i1, u2, v2,
            c, u3, v3,
            i4, u2, v4,
            m4, u1, v3
        );

        quad(tex,
            i2, u4, v2,
            c, u3, v3,
            i1, u2, v2,
            m1, u3, v1
        );

        quad(tex,
            i3, u4, v4,
            c, u3, v3,
            i2, u4, v2,
            m2, u5, v3
        );

        quad(tex,
            i4, u2, v4,
            c, u3, v3,
            i3, u4, v4,
            m3, u3, v5
        );
    }

    public static void quad(Texture texture, float x1, float y1, float u1, float v1, float x2, float y2, float u2, float v2, float x3, float y3, float u3, float v3, float x4, float y4, float u4, float v4){
        float col = Draw.getColor().toFloatBits(), mix = Draw.getMixColor().toFloatBits();

        vertices[0] = x1;
        vertices[1] = y1;
        vertices[2] = col;
        vertices[3] = u1;
        vertices[4] = v1;
        vertices[5] = mix;

        vertices[6] = x2;
        vertices[7] = y2;
        vertices[8] = col;
        vertices[9] = u2;
        vertices[10] = v2;
        vertices[11] = mix;

        vertices[12] = x3;
        vertices[13] = y3;
        vertices[14] = col;
        vertices[15] = u3;
        vertices[16] = v3;
        vertices[17] = mix;

        vertices[18] = x4;
        vertices[19] = y4;
        vertices[20] = col;
        vertices[21] = u4;
        vertices[22] = v4;
        vertices[23] = mix;

        Draw.vert(texture, vertices, 0, 24);
    }

    public static void quad(Texture texture, Position pos1, float u1, float v1, Position pos2, float u2, float v2, Position pos3, float u3, float v3, Position pos4, float u4, float v4){
        quad(texture, pos1.getX(), pos1.getY(), u1, v1, pos2.getX(), pos2.getY(), u2, v2, pos3.getX(), pos3.getY(), u3, v3, pos4.getX(), pos4.getY(), u4, v4);
    }

    public static void arcLine(float x, float y, float radius, float arcAngle, float angle){
        float arc = arcAngle / 360f;
        int sides = (int)(Lines.circleVertices(radius) * arc);
        float space = arcAngle / sides;
        float hstep = Lines.getStroke() / 2f / Mathf.cosDeg(space / 2f);
        float r1 = radius - hstep, r2 = radius + hstep;

        for(int i = 0; i < sides; i++){
            float a = angle - arcAngle / 2f + space * i,
                cos = Mathf.cosDeg(a), sin = Mathf.sinDeg(a),
                cos2 = Mathf.cosDeg(a + space), sin2 = Mathf.sinDeg(a + space);
            Fill.quad(
                x + r1 * cos, y + r1 * sin,
                x + r1 * cos2, y + r1 * sin2,
                x + r2 * cos2, y + r2 * sin2,
                x + r2 * cos, y + r2 * sin
            );
        }
    }

    public static void arcFill(float x, float y, float radius, float arcAngle, float angle){
        float arc = arcAngle / 360f;
        int sides = (int)(Lines.circleVertices(radius) * arc);
        float space = arcAngle / sides;

        for(int i = 0; i < sides; i++){
            float a = angle - arcAngle / 2f + space * i,
                cos = Mathf.cosDeg(a), sin = Mathf.sinDeg(a),
                cos2 = Mathf.cosDeg(a + space), sin2 = Mathf.sinDeg(a + space);
            Fill.tri(
                x, y,
                x + radius*cos, y + radius*sin,
                x + radius*cos2, y + radius*sin2
            );
        }
    }

    public static void snowFlake(float x, float y, float r, float s){
        for(int i = 0; i < 3; i++){
            Lines.lineAngleCenter(x, y, r + 60 * i, s);
        }
    }

    public static void spark(float x, float y, float w, float h, float r){
        for(int i = 0; i < 4; i++){
            Drawf.tri(x, y, w, h, r + 90 * i);
        }
    }

    public static void drawHeat(TextureRegion reg, float x, float y, float rot, float temp){
        float a;
        if(temp > 273.15f){
            a = Math.max(0f, (temp - 498f) * 0.001f);
            if(a < 0.01f) return;
            if(a > 1f){
                Color fCol = Pal.turretHeat.cpy().add(0, 0, 0.01f * a);
                fCol.mul(a);
                Draw.color(fCol, a);
            }else{
                Draw.color(Pal.turretHeat, a);
            }
        }else{
            a = 1f - Mathf.clamp(temp / 273.15f);
            if(a < 0.01f) return;
            Draw.color(UnityPal.coldColor, a);
        }
        Draw.blend(Blending.additive);
        Draw.rect(reg, x, y, rot);
        Draw.blend();
        Draw.color();
    }

    public static void drawSlideRect(TextureRegion region, float x, float y, float w, float h, float tw, float th, float rot, int step, float offset){
        if(region == null) return;
        nRegion.set(region);

        float scaleX = w / tw;
        float texW = nRegion.u2 - nRegion.u;

        nRegion.u += Mathf.map(offset % 1, 0f, 1f, 0f, texW * step / tw);
        nRegion.u2 = nRegion.u + scaleX * texW;
        Draw.rect(nRegion, x, y, w, h, w * 0.5f, h * 0.5f, rot);
    }

    public static void drawRotRect(TextureRegion region, float x, float y, float w, float h, float th, float rot, float ang1, float ang2){
        if(region == null || !Core.settings.getBool("effects")) return;
        float amod1 = Mathf.mod(ang1, 360f);
        float amod2 = Mathf.mod(ang2, 360f);
        if(amod1 >= 180f && amod2 >= 180f) return;

        nRegion.set(region);
        float uy1 = nRegion.v;
        float uy2 = nRegion.v2;
        float uCenter = (uy1 + uy2) / 2f;
        float uSize = (uy2 - uy1) * h / th * 0.5f;
        uy1 = uCenter - uSize;
        uy2 = uCenter + uSize;
        nRegion.v = uy1;
        nRegion.v2 = uy2;

        float s1 = -Mathf.cos(ang1 * Mathf.degreesToRadians);
        float s2 = -Mathf.cos(ang2 * Mathf.degreesToRadians);
        if(amod1 > 180f){
            nRegion.v2 = Mathf.map(0f, amod1 - 360f, amod2, uy2, uy1);
            s1 = -1f;
        }else if(amod2 > 180f){
            nRegion.v = Mathf.map(180f, amod1, amod2, uy2, uy1);
            s2 = 1f;
        }
        s1 = Mathf.map(s1, -1f, 1f, y - h / 2f, y + h / 2f);
        s2 = Mathf.map(s2, -1f, 1f, y - h / 2f, y + h / 2f);
        Draw.rect(nRegion, x, (s1 + s2) * 0.5f, w, s2 - s1, w * 0.5f, y - s1, rot);
    }

    public static void drawConstruct(TextureRegion region, float progress, Color color, float alpha, float time, float layer, Cons<TextureRegion> func){
        nRegion.set(region);
        Draw.draw(layer, () -> {
            Shaders.build.region = nRegion;
            Shaders.build.progress = progress;
            Shaders.build.color.set(color);
            Shaders.build.color.a = alpha;
            Shaders.build.time = -time / 20f;
            Draw.shader(Shaders.build);
            func.get(nRegion);
            Draw.shader();
            Draw.reset();
        });
    }

    /** An alternate version of {@link Fill#poly(float[], int)}, allows triangles. */
    public static void drawPolygon(float[] vertices){
        if(vertices.length < 6) return;
        for(int i = 2; i < vertices.length; i += 6){
            boolean isTriangle = vertices.length - i < 0;
            if(!isTriangle){
                Fill.quad(
                    vertices[0], vertices[1],
                    vertices[i], vertices[i + 1],
                    vertices[i + 2], vertices[i + 3],
                    vertices[i + 4], vertices[i + 5]
                );
            }else{
                Fill.tri(
                    vertices[0], vertices[1],
                    vertices[i], vertices[i + 1],
                    vertices[i + 2], vertices[i + 3]
                );
            }
        }
    }

    public interface Skewer{
        Vec2 get(float vx, float vy, float x, float y, float rotation, float pivotX, float pivotY, float w, float h, Vec2 out);
    }
}

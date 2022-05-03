package unity.graphics;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.graphics.*;

public class UnityDrawf{
    private static final TextureRegion nRegion = new TextureRegion();
    private static final Vec2 vec1 = new Vec2(), vec2 = new Vec2();
    private static final Vec2
        A = new Vec2(), AB = new Vec2(),
        B = new Vec2(), BC = new Vec2(),
        C = new Vec2(),
        D = new Vec2(), D0 = new Vec2(),
        E = new Vec2(), E0 = new Vec2();

    private static final Vec3
        q1 = new Vec3(),
        q2 = new Vec3(),
        q3 = new Vec3(),
        q4 = new Vec3();

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

    private static final float[] vertices = new float[24];

    private static boolean building;
    private static final FloatSeq floatBuilder = new FloatSeq(40);

    public static final Batch altBatch = new SortedSpriteBatch();

    public static void linePoint(float x, float y, Color col){
        linePoint(x, y, col.toFloatBits(), Draw.z());
    }

    public static void linePoint(float x, float y, float col, float z){
        if(!building){
            throw new IllegalStateException("Not building.");
        }else{
            floatBuilder.add(x, y, col, z);
        }
    }

    public static void beginLine(){
        if(building){
            throw new IllegalStateException("Already building.");
        }else{
            floatBuilder.clear();
            building = true;
        }
    }

    public static void endLine(){
        endLine(false);
    }

    public static void endLine(boolean wrap){
        if(!building){
            throw new IllegalStateException("Not building");
        }else{
            polyLine(floatBuilder.items, floatBuilder.size, wrap);
            building = false;
        }
    }

    public static void polyLine(float[] points, int length, boolean wrap){
        if(length < 8) return;

        float halfWidth = 0.5f * Lines.getStroke();
        boolean open = !wrap;

        for(int i = 4; i < length - 4; i += 4){
            A.set(points[i - 4], points[i - 3]);
            B.set(points[i], points[i + 1]);
            C.set(points[i + 4], points[i + 5]);

            preparePointyJoin(A, B, C, D, E, halfWidth);
            float x3 = D.x;
            float y3 = D.y;
            float x4 = E.x;
            float y4 = E.y;

            q3.set(D, points[i + 2]);
            q4.set(E, points[i + 2]);
            if(i == 4){
                if(open){
                    prepareFlatEndpoint(points[4], points[5], points[0], points[1], halfWidth);
                    q1.set(E, points[2]);
                    q2.set(D, points[2]);
                }else{
                    vec1.set(points[length - 4], points[length - 3]);

                    preparePointyJoin(vec1, A, B, D0, E0, halfWidth);
                    q1.set(E0, points[2]);
                    q2.set(D0, points[2]);
                }
            }

            pushQuad(points[i + 3]);
            q1.set(x4, y4, points[i + 2]);
            q2.set(x3, y3, points[i + 2]);
        }

        if(open){
            prepareFlatEndpoint(halfWidth);
            q3.set(E, points[length - 2]);
            q4.set(D, points[length - 2]);
        }else{
            A.set(points[0], points[1]);
            preparePointyJoin(B, C, A, D, E, halfWidth);

            q3.set(D, points[length - 2]);
            q4.set(E, points[length - 2]);
            pushQuad(points[length - 1]);

            q1.set(D, points[length - 2]);
            q2.set(E, points[length - 2]);
            q3.set(E0, points[2]);
            q4.set(D0, points[2]);
        }

        pushQuad(points[3]);
    }

    private static void pushQuad(float z){
        Draw.z(z);
        Fill.quad(q1.x, q1.y, q1.z, q2.x, q2.y, q2.z, q3.x, q3.y, q3.z, q4.x, q4.y, q4.z);
    }

    private static void prepareFlatEndpoint(float halfWidth){
        prepareFlatEndpoint(B.x, B.y, C.x, C.y, halfWidth);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private static void prepareFlatEndpoint(float sx, float sy, float ex, float ey, float halfWidth){
        vec2.set(ex, ey).sub(sx, sy).setLength(halfWidth);
        D.set(vec2.y, -vec2.x).add(ex, ey);
        E.set(-vec2.y, vec2.x).add(ex, ey);
    }

    private static void preparePointyJoin(Vec2 A, Vec2 B, Vec2 C, Vec2 D, Vec2 E, float halfLineWidth){
        AB.set(B).sub(A);
        BC.set(C).sub(B);
        float angle = angleRad();

        if(!Mathf.equal(angle, 0f) && !Mathf.equal(angle, Mathf.PI2)){
            float len = (float)((double)halfLineWidth / Math.sin(angle));
            boolean bendsLeft = angle < 0f;

            AB.setLength(len);
            BC.setLength(len);

            (bendsLeft ? D : E).set(B).sub(AB).add(BC);
            (bendsLeft ? E : D).set(B).add(AB).sub(BC);
        }else{
            prepareStraightJoin(B, D, E, halfLineWidth);
        }
    }

    private static float angleRad() {
        return (float)Math.atan2(BC.x * AB.y - BC.y * AB.x, AB.x * BC.x + AB.y * BC.y);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private static void prepareStraightJoin(Vec2 B, Vec2 D, Vec2 E, float halfLineWidth) {
        AB.setLength(halfLineWidth);
        D.set(-AB.y, AB.x).add(B);
        E.set(AB.y, -AB.x).add(B);
    }

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

    public static void diamond(float x, float y, float width, float length, float backLengthScl, float rotation){
        float tx1 = Angles.trnsx(rotation + 90f, width), ty1 = Angles.trnsy(rotation + 90f, width),
        tx2 = Angles.trnsx(rotation, length), ty2 = Angles.trnsy(rotation, length);
        Fill.quad(x + tx1, y + ty1,
        x + tx2, y + ty2,
        x - tx1, y - ty1,
        x - tx2 * backLengthScl, y - ty2 * backLengthScl);
    }

    public static void diamond(float x, float y, float width, float length, float rotation){
        float tx1 = Angles.trnsx(rotation + 90f, width), ty1 = Angles.trnsy(rotation + 90f, width),
        tx2 = Angles.trnsx(rotation, length), ty2 = Angles.trnsy(rotation, length);
        Fill.quad(x + tx1, y + ty1,
        x + tx2, y + ty2,
        x - tx1, y - ty1,
        x - tx2, y - ty2);
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

    /** A saner version of {@link Drawf#tri(float, float, float, float, float)}. */
    public static void tri(float x, float y, float width, float length, float rotation){
        float
            ax = Angles.trnsx(rotation - 90f, width / 2f),
            ay = Angles.trnsy(rotation - 90f, width / 2f),
            tx = Angles.trnsx(rotation, length),
            ty = Angles.trnsy(rotation, length);

        Fill.tri(
            x + ax, y + ay,
            x + tx, y + ty,
            x - ax, y - ay
        );
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

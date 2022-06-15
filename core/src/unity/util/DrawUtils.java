package unity.util;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;

import static arc.Core.*;

/** Intermediately-shared utility access for rendering operations. */
public final class DrawUtils{
    public static final float perspectiveDistance = 150f;

    public static final TextureAtlas emptyAtlas = new TextureAtlas(){{
        white = error = new AtlasRegion(){{
            u = v = 0f;
            u2 = v2 = 1f;
        }};
    }};

    private static final Color col1 = new Color();
    private static final Vec2 vec1 = new Vec2(), vec2 = new Vec2();
    private static final Vec2
        a = new Vec2(),
        b = new Vec2(),
        c = new Vec2(),
        left = new Vec2(), leftInit = new Vec2(),
        right = new Vec2(), rightInit = new Vec2();

    private static final Vec3
        vert1 = new Vec3(),
        vert2 = new Vec3(),
        vert3 = new Vec3(),
        vert4 = new Vec3();

    private static boolean building;
    private static final int linestr = 5;
    private static final FloatSeq floatBuilder = new FloatSeq(linestr * 20);

    private static final Vec3 v31 = new Vec3();
    private static final Mat3D m41 = new Mat3D();

    private DrawUtils(){
        throw new AssertionError();
    }

    public static void panningCircle(TextureRegion region,
                                     float x, float y, float w, float h, float radius,
                                     float arcCone, float arcRotation, Quat rotation,
                                     float layerLow, float layerHigh){
        panningCircle(region, x, y, w, h, radius, arcCone, arcRotation, rotation, false, layerLow, layerHigh, perspectiveDistance);
    }

    public static void panningCircle(TextureRegion region,
                                     float x, float y, float w, float h, float radius,
                                     float arcCone, float arcRotation, Quat rotation,
                                     boolean useLinePrecision, float layerLow, float layerHigh){
        panningCircle(region, x, y, w, h, radius, arcCone, arcRotation, rotation, useLinePrecision, layerLow, layerHigh, 150f);
    }

    /**
     * 3D-rotated circle.
     * @author GlennFolker
     */
    public static void panningCircle(TextureRegion region,
                                     float x, float y, float w, float h, float radius,
                                     float arcCone, float arcRotation, Quat rotation,
                                     boolean useLinePrecision, float layerLow, float layerHigh, float perspectiveDst){
        float z = Draw.z();

        float arc = arcCone / 360f;
        int sides = useLinePrecision ? (int)(Lines.circleVertices(radius * 3f) * arc) : (int)((Mathf.PI2 * radius * arc) / w);
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

    public static void line(float x1, float y1, float x2, float y2){
        TextureRegion end = atlas.find("hcircle");
        line(atlas.white(), end, end, x1, y1, x2, y2);
    }

    public static void line(TextureRegion line, TextureRegion end, float x1, float y1, float x2, float y2){
        line(line, end, end, x1, y1, x2, y2);
    }

    /**
     * Alternative to {@link Lines#line(float, float, float, float, boolean)} that uses textured ends.
     * @author GlennFolker
     */
    public static void line(TextureRegion line, TextureRegion start, TextureRegion end, float x1, float y1, float x2, float y2){
        float angle = Mathf.angleExact(x2 - x1, y2 - y1), s = Lines.getStroke();

        Draw.rect(start, x1, y1, s, s, angle + 180f);
        Draw.rect(end, x2, y2, s, s, angle);
        Lines.line(line, x1, y1, x2, y2, false);
    }

    public static void lineFalloff(float x1, float y1, float x2, float y2, Color outer, Color inner, int iterations, float falloff){
        TextureRegion end = atlas.find("hcircle");
        lineFalloff(atlas.white(), end, end, x1, y1, x2, y2, outer, inner, iterations, falloff);
    }

    /** @author GlennFolker */
    public static void lineFalloff(TextureRegion line, TextureRegion start, TextureRegion end,
                                   float x1, float y1, float x2, float y2,
                                   Color outer, Color inner, int iterations, float falloff){
        float s = Lines.getStroke();
        for(int i = 0; i < iterations; i++){
            Lines.stroke(s, col1.set(outer).lerp(inner, i / (iterations - 1f)));

            line(line, start, end, x1, y1, x2, y2);
            s *= falloff;
        }
    }

    public static void fillSector(float x, float y, float radius, float rotation, float fraction){
        fillSector(x, y, radius, rotation, fraction, Lines.circleVertices(radius * 3f));
    }

    /** @author GlennFolker */
    public static void fillSector(float x, float y, float radius, float rotation, float fraction, int sides){
        int max = Math.max(Mathf.round(sides * fraction), 1);
        for(int i = 0; i < max; i++){
            vec1.trns((float)i / max * fraction * 360f + rotation, radius);
            vec2.trns((i + 1f) / max * fraction * 360f + rotation, radius);

            Fill.tri(x, y, x + vec1.x, y + vec1.y, x + vec2.x, y + vec2.y);
        }
    }

    public static void linePoint(float x, float y, Color col){
        linePoint(x, y, col.toFloatBits(), Draw.z());
    }

    public static void linePoint(float x, float y, float col, float z){
        linePoint(x, y, col, Lines.getStroke(), z);
    }

    public static void linePoint(float x, float y, float col, float w, float z){
        if(!building){
            throw new IllegalStateException("Not building.");
        }else{
            floatBuilder.add(x, y, col, w);
            floatBuilder.add(z);
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
            throw new IllegalStateException("Not building.");
        }else{
            polyLine(floatBuilder.items, 0, floatBuilder.size, wrap);
            building = false;
        }
    }

    /**
     * {@link Lines#polyline(float[], int, boolean)} that supports variable color, width, and Z layer.
     * @author GlennFolker
     */
    public static void polyLine(float[] items, int offset, int length, boolean wrap){
        if(length < linestr * 2) return;
        for(int i = offset + linestr; i < length - linestr; i += linestr){
            float
                widthA = items[i - linestr + 3] / 2f, colA = items[i - linestr + 2],
                widthB = items[i + 3] / 2f, colB = items[i + 2],
                z = items[i + 4];

            a.set(items[i - linestr], items[i - linestr + 1]);
            b.set(items[i], items[i + 1]);
            c.set(items[i + linestr], items[i + linestr + 1]);

            MathUtils.pathJoin(a, b, c, left, right, widthB);
            vert3.set(left, colB);
            vert4.set(right, colB);

            if(i == offset + linestr){
                if(wrap){
                    vec1.set(items[offset + length - linestr], items[offset + length - linestr + 1]);

                    MathUtils.pathJoin(vec1, a, b, leftInit, rightInit, widthA);
                    vert1.set(rightInit, colA);
                    vert2.set(leftInit, colA);
                }else{
                    MathUtils.pathEnd(b.x, b.y, a.x, a.y, left, right, widthA);
                    vert1.set(right, colA);
                    vert2.set(left, colA);
                }
            }

            pushQuad(z);
            vert1.set(vert4.x, vert4.y, colB);
            vert2.set(vert3.x, vert3.y, colB);
        }

        float
            widthEnd = items[offset + length - linestr + 3] / 2f,
            colEnd = items[offset + length - linestr + 2],
            zEnd = items[offset + length - linestr + 4];

        if(wrap){
            float
                colStart = items[offset + 2],
                zStart = items[offset + 4];

            a.set(items[offset], items[offset + 1]);
            MathUtils.pathJoin(b, c, a, left, right, widthEnd);
            vert3.set(left, colEnd);
            vert4.set(right, colEnd);
            pushQuad(zEnd);

            vert1.set(left, colEnd);
            vert2.set(right, colEnd);
            vert3.set(rightInit, colStart);
            vert4.set(leftInit, colStart);
            pushQuad(zStart);
        }else{
            MathUtils.pathEnd(b.x, b.y, c.x, c.y, left, right, widthEnd);
            vert3.set(right, colEnd);
            vert4.set(left, colEnd);
            pushQuad(zEnd);
        }
    }

    private static void pushQuad(float z){
        Draw.z(z);
        Fill.quad(vert1.x, vert1.y, vert1.z, vert2.x, vert2.y, vert2.z, vert3.x, vert3.y, vert3.z, vert4.x, vert4.y, vert4.z);
    }
}

package unity.util;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;

/** Intermediately-shared utility access for rendering operations. */
public final class DrawUtils{
    public static final float perspectiveDistance = 150f;

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

    private static boolean building;
    private static final FloatSeq floatBuilder = new FloatSeq(40);

    private static final Vec3 v31 = new Vec3();
    private static final Mat3D m41 = new Mat3D();

    private DrawUtils(){
        throw new AssertionError();
    }

    public static void panningCircle(TextureRegion region, float x, float y, float w, float h, float radius, float arcCone, float arcRotation, Quat rotation, float layerLow, float layerHigh){
        panningCircle(region, x, y, w, h, radius, arcCone, arcRotation, rotation, false, layerLow, layerHigh, perspectiveDistance);
    }

    public static void panningCircle(TextureRegion region, float x, float y, float w, float h, float radius, float arcCone, float arcRotation, Quat rotation, boolean useLinePrecision, float layerLow, float layerHigh){
        panningCircle(region, x, y, w, h, radius, arcCone, arcRotation, rotation, useLinePrecision, layerLow, layerHigh, 150f);
    }

    /** @author GlennFolker */
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
            polyLine(floatBuilder.items, 0, floatBuilder.size, wrap);
            building = false;
        }
    }

    public static void polyLine(float[] points, int offset, int length, boolean wrap){
        if(length < 8) return;

        float halfWidth = 0.5f * Lines.getStroke();
        boolean open = !wrap;

        int start = offset + 4, end = start + length;
        for(int i = start; i < end - 4; i += 4){
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
            if(i == start){
                if(open){
                    prepareFlatEndpoint(points[start], points[start + 1], points[offset], points[offset + 1], halfWidth);
                    q1.set(E, points[offset + 2]);
                    q2.set(D, points[offset + 2]);
                }else{
                    vec1.set(points[end - 4], points[end - 3]);

                    preparePointyJoin(vec1, A, B, D0, E0, halfWidth);
                    q1.set(E0, points[offset + 2]);
                    q2.set(D0, points[offset + 2]);
                }
            }

            pushQuad(points[i + 3]);
            q1.set(x4, y4, points[i + 2]);
            q2.set(x3, y3, points[i + 2]);
        }

        if(open){
            prepareFlatEndpoint(halfWidth);
            q3.set(E, points[end - 2]);
            q4.set(D, points[end - 2]);
        }else{
            A.set(points[offset], points[offset + 1]);
            preparePointyJoin(B, C, A, D, E, halfWidth);

            q3.set(D, points[end - 2]);
            q4.set(E, points[end - 2]);
            pushQuad(points[end - 1]);

            q1.set(D, points[end - 2]);
            q2.set(E, points[end - 2]);
            q3.set(E0, points[offset + 2]);
            q4.set(D0, points[offset + 2]);
        }

        pushQuad(points[offset + 3]);
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

        float angle = Mathf.atan2(AB.x * BC.x + AB.y * BC.y, BC.x * AB.y - BC.y * AB.x);
        if(!Mathf.equal(angle, 0f) && !Mathf.equal(angle, Mathf.PI2)){
            float len = halfLineWidth / Mathf.sin(angle);
            boolean bendsLeft = angle < 0f;

            AB.setLength(len);
            BC.setLength(len);

            (bendsLeft ? D : E).set(B).sub(AB).add(BC);
            (bendsLeft ? E : D).set(B).add(AB).sub(BC);
        }else{
            prepareStraightJoin(B, D, E, halfLineWidth);
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private static void prepareStraightJoin(Vec2 B, Vec2 D, Vec2 E, float halfLineWidth){
        AB.setLength(halfLineWidth);
        D.set(-AB.y, AB.x).add(B);
        E.set(AB.y, -AB.x).add(B);
    }
}

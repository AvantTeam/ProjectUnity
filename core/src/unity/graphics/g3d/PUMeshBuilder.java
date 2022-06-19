package unity.graphics.g3d;

import arc.func.*;
import arc.graphics.*;
import arc.math.geom.*;
import arc.struct.*;

/** Convenient {@link Mesh} builder for creating some shapes. */
public final class PUMeshBuilder{
    private static final Vec2
        v1 = new Vec2(), v2 = new Vec2(), v3 = new Vec2(), v4 = new Vec2(),
        v5 = new Vec2();

    private static final Vec3
        v31 = new Vec3(), v32 = new Vec3(), v33 = new Vec3(), v34 = new Vec3(),
        nor = new Vec3();

    private static final int vertSize = 7; // Position + Normal + Color.
    private static final FloatSeq builder = new FloatSeq(vertSize * 2000);
    private static boolean building;

    private PUMeshBuilder(){
        throw new AssertionError();
    }

    public static Mesh createToroid(float radius, float thickness, float depth, Color color){
        float bits = color.toFloatBits();
        return createToroid(radius, thickness, depth, i -> bits);
    }

    public static Mesh createToroid(float radius, float thickness, float depth, FloatFloatf color){
        return createToroid(radius, thickness, depth, 80, 5, color);
    }

    /**
     * Creates a donut-like shape with the specified line thickness, body depth, resolution values, and a color provider.
     * @author GlennFolker
     */
    public static Mesh createToroid(float radius, float thickness, float depth, int thicknessSides, int depthSides, FloatFloatf color){
        begin();
        for(int i = 0; i < thicknessSides; i++){
            float
                p1 = (float)i / thicknessSides,
                p2 = (i + 1f) / thicknessSides,

                r1 = p1 * 360f,
                r2 = p2 * 360f,

                c1 = color.get(p1),
                c2 = color.get(p2);

            // Side vectors, X and Y determining each side's center point.
            v1.trns(r1, radius);
            v2.trns(r2, radius);

            for(int j = 0; j < depthSides; j++){
                // Depth vectors, X determines depth (Z component) and Y determines the offset to the side center vectors.
                v3.trns((float)j / depthSides * 360f, 1f).scl(depth / 2f, thickness / 2f);
                v4.trns((j + 1f) / depthSides * 360f, 1f).scl(depth / 2f, thickness / 2f);

                // Result vectors, world space.
                v5.trns(r1, v3.y).add(v1);
                Vec3 br = v31.set(v5, v3.x); // Bottom right.

                v5.trns(r1, v4.y).add(v1);
                Vec3 tr = v32.set(v5, v4.x); // Top right.

                v5.trns(r2, v4.y).add(v2);
                Vec3 tl = v33.set(v5, v4.x); // Top left.

                v5.trns(r2, v3.y).add(v2);
                Vec3 bl = v34.set(v5, v3.x); // Bottom left.

                // Create a plane with the same normal values for each vertex.
                Vec3 nor = normal(bl, br, tr);
                verts(bl, br, tr, nor, c1);
                verts(br, tr, tl, nor, c2);
            }
        }

        return end();
    }

    private static Vec3 normal(Vec3 bl, Vec3 br, Vec3 tr){
        return nor.set(tr).sub(br).crs(bl.x - br.x, bl.y - br.y, bl.z - br.z).nor();
    }

    private static void verts(Vec3 a, Vec3 b, Vec3 c, Vec3 nor, float col){
        vert(a, nor, col);
        vert(b, nor, col);
        vert(c, nor, col);
    }

    private static void vert(Vec3 pos, Vec3 nor, float col){
        builder.add(pos.x, pos.y, pos.z);
        builder.add(nor.x, nor.y, nor.z);
        builder.add(col);
    }

    private static void begin(){
        if(building) throw new IllegalStateException("Call end() first.");
        building = true;
        builder.clear();
    }

    private static Mesh end(){
        if(!building) throw new IllegalStateException("Call begin() first.");
        building = false;

        Mesh out = new Mesh(true, builder.size / vertSize, 0,
            VertexAttribute.position3,
            VertexAttribute.normal,
            VertexAttribute.color
        );

        out.setVertices(builder.items, 0, builder.size);
        return out;
    }
}

package unity.util;

import arc.*;
import arc.files.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.graphics.*;

import java.io.*;

/**
 * Wavefront Object Converter and Renderer for Arc/libGDX
 * The faces should not intersect. (no crashes, its just that the renderer doesn't support it.)
 * 
 * @author EyeOfDarkness
 * @author GlennFolker
 */
public class WavefrontObject{
    protected static final float zScale = 0.01f;
    protected static final float defaultScl = 4f;
    protected static final float perspectiveDistance = 350f;

    public Seq<Vec3> vertices = new Seq<>();
    public Seq<Vec2> uvs = new Seq<>();
    public Seq<Vec3> normals = new Seq<>();
    public Seq<Face> faces = new Seq<>();
    public String textureName = "";
    private final Seq<Vertex> drawnVertices = new Seq<>();
    private final Seq<Vec3> drawnNormals = new Seq<>();
    private AtlasRegion texture = null;
    private boolean hasNormal = false;
    private boolean hasTexture = false;
    private boolean odd = false;

    public ShadingType shadingType = ShadingType.normalAngle;
    public Color lightColor = Color.white;
    public Color shadeColor = Color.black;
    public float size = 1f;
    public float shadingSmoothness = 2.8f;
    public float drawLayer = Layer.blockBuilding;
    protected int indexerA;
    protected float indexerZ;

    public void load(Fi file, @Nullable Fi material){
        BufferedReader reader = file.reader(64);
        while(true){
            try{
                String line = reader.readLine();
                if(line == null) break;

                if(line.contains("v ")){
                    String[] pos = line.replaceFirst("v ", "").split("\\s+");
                    if(pos.length != 3){
                        throw new IllegalStateException("'v' must define all 3 vector points");
                    }

                    float[] vec = new float[3];    
                    for(int i = 0; i < 3; i++){
                        vec[i] = Strings.parseFloat(pos[i], 0f);
                    }

                    drawnVertices.add(new Vertex(vec[0], vec[1], vec[2]));
                    vertices.add(new Vec3(vec[0], vec[1], vec[2]));
                }

                if(line.contains("vt ")){
                    if(!hasTexture) hasTexture = true;
                    String[] pos = line.replaceFirst("vt ", "").split("\\s+");
                    Vec2 uv = new Vec2();
                    uv.x = Strings.parseFloat(pos[0], 0f);
                    uv.y = Strings.parseFloat(pos[1], 0f);
                    uvs.add(uv);
                }

                if(line.contains("vn ")){
                    if(!hasNormal) hasNormal = true;
                    String[] pos = line.replaceFirst("vn ", "").split("\\s+");
                    if(pos.length != 3){
                        throw new IllegalStateException("'v' must define all 3 vector points");
                    }

                    float[] vec = new float[3];
                    for(int i = 0; i < 3; i++){
                        vec[i] = Strings.parseFloat(pos[i], 0f);
                    }

                    drawnNormals.add(new Vec3(vec[0], vec[1], vec[2]));
                    normals.add(new Vec3(vec[0], vec[1], vec[2]));
                }

                if(line.contains("f ")){
                    String[] segments = line.replace("f ", "").split("\\s+");
                    Face face = new Face();
                    face.verts = new Vertex[segments.length];
                    if(hasNormal) face.normal = new Vec3[segments.length];
                    if(hasTexture) face.vertexTexture = new Vec2[segments.length];
                    if(segments.length != 4) odd = true;

                    int[] i = {0};
                    for(String segment : segments){
                        String[] faceIndex = segment.split("/");
                        //Unity.print(faceIndex.length + "");
                        Vertex vert = drawnVertices.get(getFaceVal(faceIndex[0]));
                        face.verts[i[0]] = vert;
                        if(hasNormal){
                            face.normal[i[0]] = drawnNormals.get(getFaceVal(faceIndex[2]));
                        }
                        if(hasTexture){
                            face.vertexTexture[i[0]] = uvs.get(getFaceVal(faceIndex[1]));
                        }

                        for(int sign : Mathf.signs){
                            Vertex v = drawnVertices.get(faceVertIndex(segments[Mathf.mod(sign + i[0], segments.length)]));

                            if(!face.verts[i[0]].neighbors.contains(v)){
                                face.verts[i[0]].neighbors.add(v);
                            }
                        }
                        //faceTmp.shadingValue += (Math.abs(vert.source.x) + Math.abs(vert.source.y) + Math.abs(vert.source.z)) / 3f;
                        //faceTmp.shadingValue += vert.source.len();
                        face.size += 6;
                        i[0]++;
                    }

                    i[0] = 0;
                    for(Vertex vt : face.verts){
                        vt.neighbors.each(vs -> {
                            for(Vertex vc : face.verts){
                                if(vs == vc) return true;
                            }

                            return false;
                        }, vs -> {
                            face.shadingValue += vt.source.dst(vs.source);
                            i[0]++;
                        });
                        //i[0]++;
                    }

                    face.shadingValue /= i[0];
                    faces.add(face);
                }
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        if(!Vars.headless && Core.atlas != null && hasTexture){
            texture = Core.atlas.find("unity-" + textureName + "-tex");
        }
    }

    public void draw(float x, float y, float rX, float rY, float rZ){
        draw(x, y, rX, rY, rZ, null);
    }

    public void draw(float x, float y, float rX, float rY, float rZ, Cons<Vec3> cons){
        float oz = Draw.z();
        for(int i = 0; i < drawnVertices.size; i++){
            Vec3 v = drawnVertices.get(i).source;
            v.set(vertices.get(i));
            if(cons != null) cons.get(v);
            v.scl(defaultScl * size).rotate(Vec3.X, rX).rotate(Vec3.Y, rY).rotate(Vec3.Z, rZ);
            float depth = Math.max(0f, (perspectiveDistance + v.z) / perspectiveDistance);
            v.scl(depth);
            
            v.add(x, y, 0f);
            if(i <= drawnNormals.size - 1){
                drawnNormals.get(i).set(normals.get(i)).rotate(Vec3.X, rX).rotate(Vec3.Y, rY).rotate(Vec3.Z, rZ);
            }
        }

        for(Face face : faces){
            indexerA = 0;
            indexerZ = 0f;
            for(Vertex vert : face.verts){
                indexerZ += vert.source.z;
                indexerA++;
            }
            indexerZ /= indexerA;
            float z = (indexerZ * zScale) + drawLayer;
            Draw.z(z);

            switch(shadingType){
                case zMedian -> zMedianDraw(face);
                case zDistance -> zDistanceDraw(face);
                case normalAngle -> normalAngleDraw(face);
                default -> Draw.color(lightColor);
            }

            float color = Draw.getColor().toFloatBits();
            float mColor = Draw.getMixColor().toFloatBits();
            boolean shouldDraw = true;
            if(hasNormal){
                if(Math.abs(face.normal[0].angle(Vec3.Z)) >= 90f) shouldDraw = false;
            }
            if(shouldDraw){
                if(!odd){
                    drawFace(face, color, mColor);
                }else{
                    Draw.draw(z, () -> drawFace(face, color, mColor));
                }
            }
        }
        Draw.reset();
        Draw.z(oz);
    }

    protected void normalAngleDraw(Face face){
        if(!hasNormal){
            Draw.color(lightColor);
            return;
        }
        Vec3 tmp = Tmp.v31.setZero();
        indexerA = 0;
        for(Vec3 n : face.normal){
            tmp.add(n);
            indexerA++;
        }
        tmp.scl(1f / indexerA);

        float angle = (Math.abs(tmp.angleRad(Vec3.Z)) / (45f * Mathf.degRad)) / shadingSmoothness;
        Tmp.c1.set(lightColor).lerp(shadeColor, Mathf.clamp(angle));
        Draw.color(Tmp.c1);
    }

    protected void zMedianDraw(Face face){
        indexerA = 0;
        indexerZ = 0;
        for(Vertex vert : face.verts){
            indexerZ += -vert.source.z;
            indexerA++;
        }
        indexerZ /= indexerA;

        Tmp.c1.set(lightColor).lerp(shadeColor, Mathf.clamp(indexerZ / face.shadingValue / (shadingSmoothness * defaultScl)));
        Draw.color(Tmp.c1);
    }

    protected void zDistanceDraw(Face face){
        indexerA = 0;
        indexerZ = 0;
        for(Vertex vert : face.verts){
            vert.neighbors.each(vertex -> {
                for(Vertex v : face.verts){
                    if(v == vertex) return true;
                }
                return false;
            }, vertex -> {
                //indexerZ += Math.abs(vertex.source.z / shadingSmoothness) * Math.abs(vert.source.z / shadingSmoothness);
                indexerZ += Math.abs(vertex.source.z - vert.source.z) / face.shadingValue / (shadingSmoothness * defaultScl);
                indexerA++;
            });
        }
        indexerZ /= indexerA;

        Tmp.c1.set(lightColor).lerp(shadeColor, Mathf.clamp(indexerZ));
        Draw.color(Tmp.c1);
    }

    protected void drawFace(Face face, float color, float mixColor){
        float[] dface = new float[face.size];
        TextureRegion region = Core.atlas.white();
        for(int i = 0; i < face.verts.length; i++){
            int s = i * 6;
            dface[s] = face.verts[i].source.x;
            dface[s + 1] = face.verts[i].source.y;
            dface[s + 2] = color;
            if(!hasTexture || texture == null){
                dface[s + 3] = region.u;
                dface[s + 4] = region.v;
            }else{
                float u = texture.u, v = texture.v;
                float u2 = texture.u2, v2 = texture.v2;
                dface[s + 3] = Mathf.lerp(u, u2, face.vertexTexture[i].x);
                dface[s + 4] = Mathf.lerp(v2, v, face.vertexTexture[i].y);
            }
            dface[s + 5] = mixColor;
        }

        Draw.vert((texture == null || !hasTexture) ? region.texture : texture.texture, dface, 0, dface.length);
    }

    protected static int faceVertIndex(String node){
        return getFaceVal(node.split("/")[0]);
    }

    protected static int getFaceVal(String value){
        return Strings.parseInt(value, 1) - 1;
    }

    @Override
    public String toString(){
        return "WavefrontObject{" +
        "vertices=" + vertices.size +
        ", faces=" + faces.size +
        ", shadingType=" + shadingType +
        '}';
    }

    public static class Face{
        public Vertex[] verts;
        public Vec3[] normal;
        public Vec2[] vertexTexture;
        public float shadingValue = 0f;
        public int size = 0;
    }

    public static class Vertex{
        public Vec3 source;
        public Seq<Vertex> neighbors = new Seq<>();

        public Vertex(float x, float y, float z){
            source = new Vec3(x, y, z);
        }
    }

    public enum ShadingType{
        zMedian,
        zDistance,
        normalAngle,
        noShading
    }
}

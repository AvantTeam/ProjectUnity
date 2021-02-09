package unity.util;

import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.graphics.*;

import java.io.*;

/**
 * Wavefront Object Converter and Renderer for Arc/libGDX
 * Renders objects orthographically.
 * No culling, textures, nor normals yet.
 * The faces MUST be a quad, because of "limitations."
 * The faces should not intersect. (no crashes, its just that the renderer doesnt support it.)
 * 
 * @author EyeOfDarkness
 */
public class WavefrontObject{
    protected static final float zScale = 0.01f;
    protected static final float defaultScl = 4f;
    protected static final float perspectiveDistance = 350f;

    public Seq<Vec3> vertices = new Seq<>();
    public Seq<Face> faces = new Seq<>();
    protected Seq<Vertex> drawnVertices = new Seq<>();

    public ShadingType shadingType = ShadingType.zDistance;
    public Color lightColor = Color.white;
    public Color shadeColor = Color.black;
    public float size = 1f;
    public float shadingSmoothness = 2.8f;
    public float drawLayer = Layer.blockBuilding;
    protected int indexerA;
    protected float indexerZ;

    public void load(Fi file){
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

                if(line.contains("f ")){
                    String[] segments = line.replace("f ", "").split("\\s+");
                    Face face = new Face();
                    face.verts = new Vertex[segments.length];

                    int[] i = {0};
                    for(String segment : segments){
                        String[] faceIndex = segment.split("/+");
                        Vertex vert = drawnVertices.get(getFaceVal(faceIndex[0]));
                        face.verts[i[0]] = vert;

                        for(int sign : Mathf.signs){
                            Vertex v = drawnVertices.get(faceTypeIndex(segments[Mathf.mod(sign + i[0], segments.length)], 0));

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
    }

    public void draw(float x, float y, float rX, float rY, float rZ){
        float oz = Draw.z();
        for(int i = 0; i < drawnVertices.size; i++){
            Vec3 v = drawnVertices.get(i).source;
            v.set(vertices.get(i));
            v.scl(defaultScl * size).rotate(Vec3.X, rX).rotate(Vec3.Y, rY).rotate(Vec3.Z, rZ);
            float depth = Math.max(0f, (perspectiveDistance + v.z) / perspectiveDistance);
            v.scl(depth);
            
            v.add(x, y, 0f);
        }
        for(Face face : faces){
            indexerA = 0;
            indexerZ = 0f;
            for(Vertex vert : face.verts){
                indexerZ += vert.source.z;
                indexerA++;
            }
            indexerZ /= indexerA;
            Draw.z((indexerZ * zScale) + drawLayer);
            if(shadingType == ShadingType.zMedian){
                zMedianDraw(face);
            }else if(shadingType == ShadingType.zDistance){
                zDistanceDraw(face);
            }else{
                Draw.color(lightColor);
            }
            drawFace(face);
        }
        Draw.reset();
        Draw.z(oz);
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

    protected void drawFace(Face face){
        float[] dface = new float[face.size];
        TextureRegion region = Core.atlas.white();
        for(int i = 0; i < face.verts.length; i++){
            int s = i * 6;
            dface[s] = face.verts[i].source.x;
            dface[s + 1] = face.verts[i].source.y;
            dface[s + 2] = Draw.getColor().toFloatBits();
            dface[s + 3] = region.u;
            dface[s + 4] = region.v;
            dface[s + 5] = Draw.getMixColor().toFloatBits();
        }

        Draw.vert(region.texture, dface, 0, dface.length);
    }

    protected static int faceTypeIndex(String node, int type){
        return getFaceVal(node.split("/")[type]);
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
        noShading
    }
}

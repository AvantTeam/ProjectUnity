package unity.util;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.graphics.*;
import unity.*;

/**
 * Wavefront Object Converter and Renderer for Arc/libGDX
 * Renders objects orthographically
 * no culling, textures, normals yet.
 * the faces MUST be a quad, because of "limitations."
 * the faces should not intersect. (no crashes, its just that the renderer doesnt support it.)
 * 
 * @author EyeOfDarkness
 */
public class WavefrontObject{
    protected static int tmpIndexer = 0;
    protected static final float zScale = 0.01f;
    protected static final float defaultScl = 4f;
    protected static final float perspectiveDistance = 350f;

    public Seq<Vec3> vertices = new Seq<>();
    public Seq<Face> faces = new Seq<>();
    protected Seq<Vertex> drawnVertices = new Seq<>();

    public String name;
    public boolean loaded = false;

    public ShadingType shadingType = ShadingType.zDistance;
    public Color lightColor = Color.white;
    public Color shadeColor = Color.black;
    public float size = 1f;
    public float shadingSmoothness = 2.8f;
    public float drawLayer = Layer.blockBuilding;
    protected int indexerA;
    protected float indexerZ;

    public WavefrontObject(String fileName){
        name = fileName;
    }

    public void load(){
        if(loaded) return;
        String[] sourceA = Vars.tree.get("objects/" + name + ".obj", true).readString().split("\n");

        for(String line : sourceA){
            if(line.contains("v ")){
                String[] vectorA = line.replace("v ", "").split(" ");
                float[] tmpV = new float[3];

                for(int i = 0; i < 3; i++){
                    tmpV[i] = Strings.parseFloat(vectorA[i], 0f);
                }

                drawnVertices.add(new Vertex(tmpV[0], tmpV[1], tmpV[2]));
                vertices.add(new Vec3(tmpV[0], tmpV[1], tmpV[2]));
            }
            if(line.contains("f ")){
                String[] faceA = line.replace("f ", "").split(" ");
                Face faceTmp = new Face();
                faceTmp.verts = new Vertex[faceA.length];

                tmpIndexer = 0;
                for(String segment : faceA){
                    String[] faceIndex = segment.split("/");
                    Vertex tvert = drawnVertices.get(getFaceVal(faceIndex[0]));
                    faceTmp.verts[tmpIndexer] = tvert;
                    for(int sign : Mathf.signs){
                        Vertex vTmp = drawnVertices.get(faceTypeIndex(faceA[Mathf.mod(sign + tmpIndexer, faceA.length)], 0));
                        if(!faceTmp.verts[tmpIndexer].neighbors.contains(vTmp)) faceTmp.verts[tmpIndexer].neighbors.add(vTmp);
                    }
                    //faceTmp.shadingValue += (Math.abs(tvert.source.x) + Math.abs(tvert.source.y) + Math.abs(tvert.source.z)) / 3f;
                    //faceTmp.shadingValue += tvert.source.len();
                    faceTmp.size += 6;
                    tmpIndexer++;
                }
                tmpIndexer = 0;
                for(Vertex vt : faceTmp.verts){
                    vt.neighbors.each(vs -> {
                        for(Vertex vc : faceTmp.verts){
                            if(vs == vc) return true;
                        }
                        return false;
                    }, vs -> {
                        faceTmp.shadingValue += vt.source.dst(vs.source);
                        tmpIndexer++;
                    });
                    //tmpIndexer++;
                }
                faceTmp.shadingValue /= tmpIndexer;
                faces.add(faceTmp);
            }
        }

        //Unity.print(toString());
        loaded = true;
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

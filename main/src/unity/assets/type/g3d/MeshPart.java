package unity.assets.type.g3d;

import arc.graphics.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import arc.util.*;

import java.nio.*;

public class MeshPart{
    public String id;

    /**
     * The primitive type, OpenGL constant e.g: {@link GL20#GL_TRIANGLES}, {@link GL20#GL_POINTS}, {@link GL20#GL_LINES},
     * {@link GL20#GL_LINE_STRIP}, {@link GL20#GL_TRIANGLE_STRIP}
     */
    public int primitiveType;

    /**
     * The offset in the {@link #mesh} to this part. If the mesh is indexed ({@link Mesh#getNumIndices()} > 0), this
     * is the offset in the indices array, otherwise it is the offset in the vertices array.
     */
    public int offset;

    /**
     * The size (in total number of vertices) of this part in the {@link #mesh}. When the mesh is indexed (
     * {@link Mesh#getNumIndices()} > 0), this is the number of indices, otherwise it is the number of vertices.
     */
    public int size;

    /** The Mesh the part references, also stored in {@link Model} */
    public Mesh mesh;

    /** The offset to the center of the bounding box of the shape, only valid after the call to {@link #update()}. */
    public final Vec3 center = new Vec3();

    /**
     * The location, relative to {@link #center}, of the corner of the axis aligned bounding box of the shape. Or, in
     * other words: half the dimensions of the bounding box of the shape, where {@link Vec3#x} is half the width,
     * {@link Vec3#y} is half the height and {@link Vec3#z} is half the depth. Only valid after the call to
     * {@link #update()}.
     */
    public final Vec3 halfExtents = new Vec3();

    /**
     * The radius relative to {@link #center} of the bounding sphere of the shape, or negative if not calculated yet.
     * This is the same as the length of the {@link #halfExtents} member. See {@link #update()}.
     */
    public float radius = -1;

    private final static BoundingBox bounds = new BoundingBox();

    public MeshPart(){}

    /**
     * Construct a new MeshPart and set all its values.
     * @param id The id of the new part, may be null.
     * @param mesh The mesh which holds all vertices and (optional) indices of this part.
     * @param offset The offset within the mesh to this part.
     * @param size The size (in total number of vertices) of the part.
     * @param type The primitive type of the part (e.g. GL_TRIANGLES, GL_LINE_STRIP, etc.).
     */
    public MeshPart(String id, Mesh mesh, int offset, int size, int type){
        set(id, mesh, offset, size, type);
    }

    /**
     * Construct a new MeshPart which is an exact copy of the provided MeshPart.
     * @param copyFrom The MeshPart to copy.
     */
    public MeshPart(MeshPart copyFrom){
        set(copyFrom);
    }

    /**
     * Set this MeshPart to be a copy of the other MeshPart
     * @param other The MeshPart from which to copy the values
     * @return this MeshPart, for chaining
     */
    public MeshPart set(MeshPart other){
        this.id = other.id;
        this.mesh = other.mesh;
        this.offset = other.offset;
        this.size = other.size;
        this.primitiveType = other.primitiveType;
        this.center.set(other.center);
        this.halfExtents.set(other.halfExtents);
        this.radius = other.radius;
        return this;
    }

    /**
     * Set this MeshPart to given values, does not {@link #update()} the bounding box values.
     *
     * @return this MeshPart, for chaining.
     */
    public MeshPart set(String id, Mesh mesh, int offset, int size, int type){
        this.id = id;
        this.mesh = mesh;
        this.offset = offset;
        this.size = size;
        this.primitiveType = type;
        this.center.set(0, 0, 0);
        this.halfExtents.set(0, 0, 0);
        this.radius = -1f;
        return this;
    }

    /**
     * Calculates and updates the {@link #center}, {@link #halfExtents} and {@link #radius} values. This is considered a
     * costly operation and should not be called frequently. All vertices (points) of the shape are traversed to calculate
     * the maximum and minimum x, y and z coordinate of the shape. Note that MeshPart is not aware of any transformation
     * that might be applied when rendering. It calculates the untransformed (not moved, not scaled, not rotated) values.
     */
    public void update(){
        extendBoundingBox(null);
        bounds.getCenter(center);
        bounds.getDimensions(halfExtents).scl(0.5f);
        radius = halfExtents.len();
    }

    public void extendBoundingBox(Mat3D transform){
        int numIndices = mesh.getNumIndices();
        int numVertices = mesh.getNumVertices();
        int max = numIndices == 0 ? numVertices : numIndices;

        if(offset < 0 || size < 1 || offset + size > max) throw new IllegalStateException("Invalid part specified ( offset=" + offset + ", count=" + size + ", max=" + max + " )");

        FloatBuffer verts = mesh.vertices.buffer();
        ShortBuffer index = mesh.indices.buffer();

        int posIndex = Structs.indexOf(mesh.attributes, VertexAttribute.position3);
        if(posIndex < 0) throw new IllegalStateException("Mesh has no position3 attribute");

        int offset = 0;
        for(int i = 0; i < posIndex; i++){
            offset += mesh.attributes[i].size;
        }

        int posoff = offset / 4;
        int vertSize = mesh.vertexSize / 4;
        int end = offset + size;

        if(numIndices > 0){
            for(int i = offset; i < end; i++){
                int idx = (index.get(i) & 0xFFFF) * vertSize + posoff;
                Tmp.v31.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));
                if(transform != null) Mat3D.prj(Tmp.v31, transform);

                bounds.ext(Tmp.v31);
            }
        }else{
            for(int i = offset; i < end; i++){
                int idx = i * vertSize + posoff;
                Tmp.v31.set(verts.get(idx), verts.get(idx + 1), verts.get(idx + 2));

                bounds.ext(Tmp.v31);
            }
        }
    }

    /**
     * Compares this MeshPart to the specified MeshPart and returns true if they both reference the same {@link Mesh}
     * and the {@link #offset}, {@link #size} and {@link #primitiveType} members are equal. The {@link #id} member is
     * ignored.
     * @param other The other MeshPart to compare this MeshPart to.
     * @return True when this MeshPart equals the other MeshPart (ignoring the {@link #id} member), false otherwise.
     */
    public boolean equals(MeshPart other){
        return other == this || (other != null && other.mesh == mesh && other.primitiveType == primitiveType && other.offset == offset && other.size == size);
    }

    @Override
    public boolean equals(Object other){
        if(other == null) return false;
        if(other == this) return true;
        if(!(other instanceof MeshPart mesh)) return false;

        return equals(mesh);
    }

    /**
     * Renders the mesh part using the specified shader, must be called after {@link Shader#bind()}.
     * @param shader the shader to be used
     */
    public void render(Shader shader){
        mesh.render(shader, primitiveType, offset, size, true);
    }
}

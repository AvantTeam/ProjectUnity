package unity.graphics.trail;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import unity.graphics.*;
import unity.graphics.BaseTrail.*;

/**
 * Velocity trail attribute. Adds an arbitrary vector multiplied by trail speed to the trail position.
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
public class VelAttrib extends TrailAttrib{
    private static final Vec2 vec = new Vec2();

    public float velX, velY, drag;
    public VelRotationHandler<BaseTrail, VelAttrib> rotation;

    public VelAttrib(){
        this(0f, 0f, (t, v) -> BaseTrail.unconvRot(t.la()), 0.5f);
    }

    public <T extends BaseTrail, V extends VelAttrib> VelAttrib(float velX, float velY, VelRotationHandler<T, V> rotation){
        this(velX, velY, rotation, 0.25f);
    }

    public <T extends BaseTrail, V extends VelAttrib> VelAttrib(float velX, float velY, VelRotationHandler<T, V> rotation, float drag){
        super(2);
        
        this.velX = velX;
        this.velY = velY;
        this.rotation = (VelRotationHandler<BaseTrail, VelAttrib>)rotation;
        this.drag = drag;
    }

    public float velX(float[] vertex){
        return vertex[attribOffset];
    }

    public void velX(float[] vertex, float velX){
        vertex[attribOffset] = velX;
    }

    public float velY(float[] vertex){
        return vertex[attribOffset + 1];
    }

    public void velY(float[] vertex, float velY){
        vertex[attribOffset + 1] = velY;
    }

    @Override
    protected void point(BaseTrail trail, FloatSeq points, int index, float x, float y, float width, float angle, float speed, float delta){
        float v = speed;
        vec.trns(rotation.get(trail, this) - 90f, velX * v, velY * v);
        points.add(vec.x, vec.y);
    }

    @Override
    protected void mutate(BaseTrail trail, float[] vertex, int index, float speed, float delta){
        if(index == trail.length - 1) return;

        float vx = velX(vertex) * Time.delta, vy = velY(vertex) * Time.delta, d = Mathf.clamp(1f - drag * Time.delta);
        trail.x(vertex, trail.x(vertex) + vx);
        trail.y(vertex, trail.y(vertex) + vy);

        velX(vertex, vx * d);
        velY(vertex, vy * d);
    }

    @Override
    protected void endMutate(BaseTrail trail){
        trail.recalculateAngle();
    }

    @Override
    public VelAttrib copy(){
        return new VelAttrib(velX, velY, rotation, drag);
    }

    public interface VelRotationHandler<T extends BaseTrail, V extends VelAttrib>{
        float get(T trail, V Attrib);
    }
}

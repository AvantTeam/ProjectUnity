package unity.graphics;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;

/**
 * A dynamically composed trail.
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
public class BaseTrail extends Trail{
    public final TrailAttrib[] attributes;
    public final int stride;

    public int length;
    public RotationHandler rot;

    /** Whether to force drawing the trail's cap or not. */
    public boolean forceCap;
    /** Minimum trail speed to update the points. */
    public float minDst = -1f;

    protected final FloatSeq points;
    protected float lastX, lastY, lastWidth, lastAngle, counter;

    private final float[] tmpVertex;

    public BaseTrail(int length){
        this(length, BaseTrail::rot);
    }

    public BaseTrail(int length, TrailAttrib... attributes){
        this(length, BaseTrail::rot, attributes);
    }

    public BaseTrail(int length, RotationHandler rot, TrailAttrib... attributes){
        super(0); // Don't allocate anything for base class' point array.
        this.attributes = attributes;
        this.rot = rot;

        int stride = baseSize();
        for(int i = 0; i < attributes.length; i++){
            TrailAttrib attrib = attributes[i];
            attrib.attribIndex = i;
            attrib.attribOffset = stride;

            stride += attrib.size;
        }

        this.stride = stride;
        this.length = length;

        points = new FloatSeq(length * stride);
        tmpVertex = new float[stride];
        for(TrailAttrib attrib : attributes) attrib.init();
    }

    @Override
    public BaseTrail copy(){
        BaseTrail out = new BaseTrail(length, rot, copyAttrib(attributes));
        out.points.addAll(points);
        out.lastX = lastX;
        out.lastY = lastY;
        out.lastWidth = lastWidth;
        out.lastAngle = lastAngle;
        out.counter = counter;
        out.forceCap = forceCap;
        out.minDst = minDst;
        return out;
    }

    @Override
    public float width(){
        return lastWidth;
    }

    @Override
    public void clear(){
        points.clear();
    }

    @Override
    public int size(){
        return points.size / stride;
    }

    /** @return The main attribute size. Defaults to x, y, width, and angle. */
    public int baseSize(){
        return 4;
    }

    @Override
    public void draw(Color color, float width){
        if(forceCap) drawCap(color, width);

        float[] items = points.items;
        int psize = points.size, stride = this.stride;

        float size = width / (psize / (float)stride);
        for(int i = 0, ind = 0; i < psize; i += stride, ind++){
            float
                x1 = items[i], y1 = items[i + 1], w1 = items[i + 2], r1 = items[i + 3],
                x2, y2, w2, r2;

            if(i < psize - stride){
                x2 = items[i + stride];
                y2 = items[i + stride + 1];
                w2 = items[i + stride + 2];
                r2 = items[i + stride + 3];
            }else{
                x2 = lastX;
                y2 = lastY;
                w2 = lastWidth;
                r2 = lastAngle;
            }

            float
                cx = Mathf.sin(r1) * ind * size * w1,
                cy = Mathf.cos(r1) * ind * size * w1,
                nx = Mathf.sin(r2) * (ind + 1) * size * w2,
                ny = Mathf.cos(r2) * (ind + 1) * size * w2;

            Fill.quad(
                x1 - cx, y1 - cy,
                x1 + cx, y1 + cy,
                x2 + nx, y2 + ny,
                x2 - nx, y2 - ny
            );
        }

        Draw.reset();
    }

    @Override
    public void drawCap(Color color, float width){
        if(forceCap) return;

        int psize = points.size, stride = this.stride;
        if(psize > 0){
            Draw.color(color);
            float[] items = points.items;

            int i = psize - stride;
            float
                x = items[i], y = items[i + 1],
                w = items[i + 2] * width / (points.size / (float)stride) * (i / (float)stride) * 2f;

            Draw.rect("hcircle", x, y, w, w, unconvRot(lastAngle));
            Draw.reset();
        }
    }

    /** @inheritDocs. Should not be called at the same tick with {@link #update(float, float, float, float)}. */
    @Override
    public void shorten(){
        if((counter += Time.delta) >= 1f){
            if(points.size >= stride) points.removeRange(0, stride - 1);
            counter %= 1f;
        }
    }

    @Override
    public void update(float x, float y){
        update(x, y, 1f, rot.get(this, x, y));
    }

    @Override
    public void update(float x, float y, float width){
        update(x, y, width, rot.get(this, x, y));
    }

    public float update(float x, float y, float width, float angle){
        boolean interval = (counter += Time.delta) >= 1f;

        float delta = counter, speed = Mathf.dst(lastX, lastY, x, y) / Time.delta;
        if(interval){
            int stride = this.stride;
            if(speed >= minDst){
                if(points.size > length * stride - stride) points.removeRange(0, stride - 1);
                point(x, y, width, angle, speed, delta);
            }else if(points.size >= stride){
                points.removeRange(0, stride - 1);
            }

            counter %= 1f;
        }

        saveLast(x, y, width, angle, speed, delta);
        mutate(speed, delta);
        return speed;
    }

    /** Forces angle recalculation using the default angle calculator.  */
    public void recalculateAngle(){
        float[] items = points.items;
        int psize = points.size, stride = this.stride;

        for(int i = 0; i < psize; i += stride){
            float x1 = items[i], y1 = items[i + 1], x2, y2;
            if(i < psize - stride){
                x2 = items[i + stride];
                y2 = items[i + stride + 1];
            }else{
                x2 = lastX;
                y2 = lastY;
            }

            items[i + 3] = (i > 0 && Mathf.zero(Mathf.dst(x1, y1, x2, y2))) ? items[i - stride + 3] : -Angles.angleRad(x1, y1, x2, y2);
        }
    }

    protected void point(float x, float y, float width, float angle, float speed, float delta){
        basePoint(x, y, width, angle, speed, delta);

        int i = points.size / stride;
        for(TrailAttrib attrib : attributes) attrib.point(this, points, i, x, y, width, angle, speed, delta);
    }

    protected void basePoint(float x, float y, float width, float angle, float speed, float delta){
        points.add(x, y, width, angle);
    }

    protected void saveLast(float x, float y, float width, float angle, float speed, float delta){
        for(TrailAttrib attrib : attributes) attrib.saveLast(this, x, y, width, angle, speed, delta);
        lastX = x;
        lastY = y;
        lastWidth = width;
        lastAngle = angle;
    }

    protected void mutate(float speed, float delta){
        float[] items = points.items;

        int psize = points.size, stride = this.stride;
        for(TrailAttrib attrib : attributes){
            for(int i = 0, ind = 0; i < psize; i += stride, ind++){
                System.arraycopy(items, i, tmpVertex, 0, stride);
                attrib.mutate(this, tmpVertex, ind, speed, delta);
                System.arraycopy(tmpVertex, 0, items, i, stride);
            }
        }

        for(TrailAttrib attrib : attributes) attrib.endMutate(this);
    }

    public float x(float[] vertex){
        return vertex[0];
    }

    public void x(float[] vertex, float x){
        vertex[0] = x;
    }

    public float y(float[] vertex){
        return vertex[1];
    }

    public void y(float[] vertex, float y){
        vertex[1] = y;
    }

    public float width(float[] vertex){
        return vertex[2];
    }

    public void width(float[] vertex, float width){
        vertex[2] = width;
    }

    public float angle(float[] vertex){
        return vertex[3];
    }

    public void angle(float[] vertex, float angle){
        vertex[3] = angle;
    }

    public float lx(){
        return lastX;
    }

    public float ly(){
        return lastY;
    }

    public float lw(){
        return lastWidth;
    }

    public float la(){
        return lastAngle;
    }

    public <T extends TrailAttrib> T attrib(Class<T> type){
        return attrib(a -> type.isAssignableFrom(a.getClass()));
    }

    public <T extends TrailAttrib> T attrib(Boolf<TrailAttrib> pred){
        return (T)Structs.find(attributes, pred);
    }

    public static float rot(BaseTrail trail, float x, float y){
        float lx = trail.lastX, ly = trail.lastY, la = trail.lastAngle;
        return Mathf.zero(Mathf.dst2(x, y, lx, ly)) ? la : -Angles.angleRad(x, y, lx, ly);
    }

    public static RotationHandler rot(Rotc e){
        return (trail, x, y) -> e.isAdded() ? convRot(e.rotation()) : rot(trail, x, y);
    }

    public static float convRot(float degrees){
        return (degrees - 180f) * -Mathf.degRad;
    }

    public static float unconvRot(float angle){
        return -Mathf.radDeg * angle + 180f;
    }

    public static TrailAttrib[] copyAttrib(TrailAttrib... attributes){
        TrailAttrib[] out = new TrailAttrib[attributes.length];
        for(int i = 0; i < out.length; i++) out[i] = attributes[i].copy();

        return out;
    }

    public interface RotationHandler{
        float get(BaseTrail trail, float x, float y);
    }

    public static abstract class TrailAttrib{
        /** How many float values this attribute needs. */
        public final int size;

        protected int attribIndex, attribOffset;

        public TrailAttrib(int size){
            this.size = size;
        }

        public void init(){}

        /** Adds arbitrary attributes, up to the {@link #size}. */
        protected abstract void point(BaseTrail trail, FloatSeq points, int index, float x, float y, float width, float angle, float speed, float delta);

        protected void saveLast(BaseTrail trail, float x, float y, float width, float angle, float speed, float delta){}

        protected void mutate(BaseTrail trail, float[] vertex, int index, float speed, float delta){}

        protected void endMutate(BaseTrail trail){}

        public abstract TrailAttrib copy();
    }
}

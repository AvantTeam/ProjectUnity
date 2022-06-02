package unity.graphics;

import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.graphics.*;

/**
 * Holds multiple trails with additional offsets, width multiplier, and color override.
 * @author GlennFolker
 */
public class MultiTrail extends BaseTrail{
    public TrailHold[] trails;

    public MultiTrail(TrailHold... trails){
        this(BaseTrail::rot, trails);
    }

    public MultiTrail(RotationHandler rot, TrailHold... trails){
        super(0);
        this.rot = rot;
        this.trails = trails;

        for(TrailHold trail : trails) length = Math.max(length, trail.trail.length);
    }

    @Override
    public MultiTrail copy(){
        TrailHold[] mapped = new TrailHold[trails.length];
        for(int i = 0; i < mapped.length; i++) mapped[i] = trails[i].copy();

        MultiTrail out = new MultiTrail(mapped);
        out.lastX = lastX;
        out.lastY = lastY;
        return out;
    }

    @Override
    public int baseSize(){
        return 0;
    }

    @Override
    public void clear(){
        for(TrailHold trail : trails) trail.trail.clear();
    }

    @Override
    public int size(){
        int size = 0;
        for(TrailHold trail : trails) size = Math.max(size, trail.trail.size());

        return size;
    }

    @Override
    public void drawCap(Color color, float width){
        for(TrailHold trail : trails) trail.trail.drawCap(trail.color == null ? color : trail.color, width);
    }

    @Override
    public void draw(Color color, float width){
        for(TrailHold trail : trails) trail.trail.draw(trail.color == null ? color : trail.color, width);
    }

    @Override
    public void shorten(){
        for(TrailHold trail : trails) trail.trail.shorten();
    }

    @Override
    public float update(float x, float y, float width, float angle){
        for(TrailHold trail : trails){
            Tmp.v1.trns(unconvRot(angle) - 90f, trail.x, trail.y);
            trail.trail.update(x + Tmp.v1.x, y + Tmp.v1.y, width * trail.width);
        }

        float s = Mathf.dst(lastX, lastY, x, y) / Time.delta;
        lastX = x;
        lastY = y;
        return s;
    }

    public static class TrailHold{
        public Trail trail;
        public float x;
        public float y;
        public float width;
        public Color color;

        public TrailHold(Trail trail){
            this(trail, 0f, 0f, 1f, null);
        }

        public TrailHold(Trail trail, Color color){
            this(trail, 0f, 0f, 1f, color);
        }

        public TrailHold(Trail trail, float x, float y){
            this(trail, x, y, 1f, null);
        }

        public TrailHold(Trail trail, float x, float y, float width){
            this.trail = trail;
            this.x = x;
            this.y = y;
            this.width = width;
        }

        public TrailHold(Trail trail, float x, float y, float width, Color color){
            this.trail = trail;
            this.x = x;
            this.y = y;
            this.width = width;
            this.color = color;
        }

        public TrailHold copy(){
            return new TrailHold(trail.copy(), x, y, width, color);
        }
    }
}

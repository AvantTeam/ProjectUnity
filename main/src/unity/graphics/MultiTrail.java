package unity.graphics;

import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.graphics.*;

/**
 * Holds multiple trails with additional offsets, width multiplier, and color override.
 * @author GlennFolker
 */
public class MultiTrail extends Trail{
    public TrailHold[] trails;
    public RotationHandler rotation = MultiTrail::calcRot;

    public Effect trailEffect = Fx.missileTrail;
    public float trailChance = 0f;
    public float trailWidth = 1f;
    public Color trailColor = Pal.engine.cpy();

    protected float lastX, lastY;

    public MultiTrail(TrailHold... trails){
        super(0);
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
        out.rotation = rotation;
        out.trailEffect = trailEffect;
        out.trailChance = trailChance;
        out.trailWidth = trailWidth;
        out.trailColor.set(trailColor);
        return out;
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
        for(TrailHold trail : trails) trail.trail.drawCap(trail.color == null ? color : trail.color, width * trail.width);
    }

    @Override
    public void draw(Color color, float width){
        for(TrailHold trail : trails) trail.trail.draw(trail.color == null ? color : trail.color, width * trail.width);
    }

    @Override
    public void shorten(){
        for(TrailHold trail : trails) trail.trail.shorten();
    }

    @Override
    public void update(float x, float y, float width){
        float angle = rotation.get(this, x, y) - 90f;
        for(TrailHold trail : trails){
            Tmp.v1.trns(angle, trail.x, trail.y);

            trail.trail.update(x + Tmp.v1.x, y + Tmp.v1.y, width * trail.width);
            if(trailChance > 0f && Mathf.chanceDelta(trailChance)){
                trailEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, trail.width * trailWidth, trailColor);
            }
        }

        lastX = x;
        lastY = y;
    }

    public float calcRot(float x, float y){
        return Angles.angle(lastX, lastY);
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

    public interface RotationHandler{
        float get(MultiTrail trail, float x, float y);
    }
}

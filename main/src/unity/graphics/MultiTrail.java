package unity.graphics;

import arc.graphics.*;
import mindustry.graphics.*;

/**
 * Holds multiple trails.
 * @author GlennFolker
 */
public class MultiTrail extends Trail{
    public TrailHold[] trails;

    public MultiTrail(TrailHold... trails){
        super(0);
        this.trails = trails;

        for(TrailHold trail : trails) length = Math.max(length, trail.trail.length);
    }

    @Override
    public MultiTrail copy(){
        return new MultiTrail(trails);
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
        for(TrailHold trail : trails) trail.trail.drawCap(color, width);
    }

    @Override
    public void draw(Color color, float width){
        for(TrailHold trail : trails) trail.trail.draw(color, width);
    }

    @Override
    public void shorten(){
        for(TrailHold trail : trails) trail.trail.shorten();
    }

    @Override
    public void update(float x, float y, float width){
        for(TrailHold trail : trails) trail.trail.update(x + trail.x, y + trail.y, width);
    }

    public static class TrailHold{
        public Trail trail;
        public float x;
        public float y;

        public TrailHold(Trail trail){
            this(trail, 0f, 0f);
        }

        public TrailHold(Trail trail, float x, float y){
            this.trail = trail;
            this.x = x;
            this.y = y;
        }
    }
}

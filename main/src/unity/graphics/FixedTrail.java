package unity.graphics;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;

public class FixedTrail{
    public int length;

    private final FloatSeq points;
    private float lastX = -1, lastY = -1, counter = 0f;

    public FixedTrail(int length){
        this.length = length;
        points = new FloatSeq(length * 4);
    }

    public FixedTrail copy(){
        FixedTrail out = new FixedTrail(length);
        out.points.addAll(points);
        out.lastX = lastX;
        out.lastY = lastY;
        return out;
    }

    public void clear(){
        points.clear();
    }

    public int size(){
        return points.size / 4;
    }

    public void drawCap(Color color, float width){
        if(points.size > 0){
            Draw.color(color);
            float[] items = points.items;
            int i = points.size - 4;
            float x1 = items[i], y1 = items[i + 1], w1 = items[i + 2], ai = items[i + 3], w = w1 * width / (points.size / 4) * i / 4f * 2f;
            if(w1 <= 0.001f) return;
            Draw.rect("hcircle", x1, y1, w, w, -Mathf.radDeg * ai + 180f);
            Draw.reset();
        }
    }

    public void draw(Color color, float width){
        Draw.color(color);
        float[] items = points.items;

        for(int i = 0; i < points.size - 4; i+= 4){
            float x1 = items[i], y1 = items[i + 1], w1 = items[i + 2], a1 = items[i + 3],
                x2 = items[i + 4], y2 = items[i + 5], w2 = items[i + 6], a2 = items[i + 7];
            float size = width / (points.size / 4);
            if(w1 <= 0.001f || w2 <= 0.001f) continue;

            float cx = Mathf.sin(a1) * i / 4f * size * w1, cy = Mathf.cos(a1) * i / 4f * size * w1,
                nx = Mathf.sin(a2) * (i / 4f + 1) * size * w2, ny = Mathf.cos(a2) * (i / 4f + 1) * size * w2;
            Fill.quad(x1 - cx, y1 - cy, x1 + cx, y1 + cy, x2 + nx, y2 + ny, x2 - nx, y2 - ny);
        }

        Draw.reset();
    }

    /** Removes the last point from the trail at intervals. */
    public void shorten(){
        if(Vars.state.isPlaying() && (counter += Time.delta) >= 0.99f){
            if(points.size >= 4){
                points.removeRange(0, 3);
            }

            counter = 0f;
        }
    }

    /** Adds a new point to the trail at intervals. */
    public void update(float x, float y){
        update(x, y, Angles.angle(x, y, lastX, lastY));
    }

    /** Adds a new point to the trail at intervals. */
    public void update(float x, float y, float rotation){
        update(x, y, 1f, rotation);
    }

    public void update(float x, float y, float width, float rotation){
        if(Vars.state.isPlaying() && (counter += Time.delta) >= 0.99f){
            if(points.size > length * 4){
                points.removeRange(0, 3);
            }

            points.add(x, y, width, -rotation * Mathf.degRad);

            counter = 0f;

            lastX = x;
            lastY = y;
        }
    }
}

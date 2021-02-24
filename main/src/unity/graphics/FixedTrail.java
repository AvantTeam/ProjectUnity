package unity.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.pooling.Pools;

public class FixedTrail{
    public int length;
    private final Seq<Vec3> points;

    public FixedTrail(int length){
        this.length = length;
        points = new Seq<>(length);
    }

    public void clear(){
        points.clear();
    }

    public void draw(Color color, float width){
        Draw.color(color);

        for(int i = 0; i < points.size - 1; i++){
            Vec3 c = points.get(i);
            Vec3 n = points.get(i + 1);
            float size = width * 1f / length;

            float cx = Mathf.sin(c.z) * i * size, cy = Mathf.cos(c.z) * i * size, nx = Mathf.sin(n.z) * (i + 1) * size, ny = Mathf.cos(n.z) * (i + 1) * size;
            Fill.quad(c.x - cx, c.y - cy, c.x + cx, c.y + cy, n.x + nx, n.y + ny, n.x - nx, n.y - ny);
        }

        Draw.reset();
    }

    public void update(float x, float y, float rotation){
        if(points.size > length){
            Pools.free(points.first());
            points.remove(0);
        }

        points.add(Pools.obtain(Vec3.class, () -> new Vec3(x, y, -rotation * Mathf.degRad)));
    }
}

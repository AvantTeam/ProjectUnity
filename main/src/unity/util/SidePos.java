package unity.util;

import arc.math.geom.*;
import arc.util.pooling.Pool.*;

public class SidePos implements Poolable{
    public Point2 from;
    public Point2 to;
    public int dir;

    public SidePos set(Point2 from, Point2 to, int dir){
        this.from.set(from.x, from.y);
        this.to.set(to.x, to.y);
        this.dir = dir;

        return this;
    }

    @Override
    public void reset(){
        from = null;
        to = null;
        dir = 0;
    }
}

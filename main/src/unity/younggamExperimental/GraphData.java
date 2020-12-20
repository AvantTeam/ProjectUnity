package unity.younggamExperimental;

import arc.math.geom.*;

import static arc.math.geom.Geometry.*;

//오로지 getConnectSidePos 만을 위해서 존재
public class GraphData{
    public final Point2 fromPos, toPos;
    public final int dir, index;

    private GraphData(int fx, int fy, int tx, int ty, int dir, int index){
        fromPos = new Point2(fx, fy);
        toPos = new Point2(tx, ty);
        this.dir = dir;
        this.index = index;
    }

    public static GraphData getConnectSidePos(int index, int size, int rotation){
        int side = index / size;
        side = (side + rotation) % 4;
        Point2 normal = d4((side + 3) % 4);
        Point2 tangent = d4((side + 1) % 4);
        int originX = 0, originY = 0;
        if(size > 1){
            originX += size / 2;
            originY += size / 2;
            originY -= size - 1;
            if(side > 0){
                for(int i = 1; i <= side; i++){
                    originX += d4x(i) * (size - 1);
                    originY += d4y(i) * (size - 1);
                }
            }
            originX += tangent.x * (index % size);
            originY += tangent.y * (index % size);
        }
        return new GraphData(originX, originY, originX + d4x(side), originY + d4y(side), side, index);
    }
}

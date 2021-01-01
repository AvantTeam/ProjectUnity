package younggamExperimental;

import arc.math.geom.*;

public class ConnectData{
    public final Point2 dir;
    public final int x, y;
    public byte id;

    private ConnectData(int x, int y, Point2 dir){
        this.x = x;
        this.y = y;
        this.dir = dir.cpy();
    }

    private ConnectData(int index){
        this(0, 0, Geometry.d4(index));
    }

    public ConnectData id(byte id){
        this.id = id;
        return this;
    }

    public static ConnectData getConnectSidePos(int index, int sizeW, int sizeH){
        if(sizeH == 1 && sizeW == 1) return new ConnectData(index);
        int cind = index - sizeH;
        int lastSub = sizeH;
        int gx = sizeW - 1;
        int gy = 0;
        int side = 0;
        var forwardDir = Geometry.d4(3);
        while(cind >= 0){
            side++;
            gx += forwardDir.x * (lastSub - 1);
            gy += forwardDir.y * (lastSub - 1);
            forwardDir = Geometry.d4(3 - side);
            lastSub = side % 2 == 1 ? sizeW : sizeH;
            cind -= lastSub;
        }
        gx += forwardDir.x * (cind + lastSub);
        gy += forwardDir.y * (cind + lastSub);
        return new ConnectData(gx, gy, Geometry.d4(side % 4));
    }
}

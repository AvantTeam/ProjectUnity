package unity.entities;

import arc.math.WindowedMean;
import arc.math.geom.Vec2;
import mindustry.graphics.Trail;
import unity.graphics.FixedTrail;

//temporary naming
public class BData{
    public float f, rot;
    public boolean isP;
    public Vec2 v;
    public FixedTrail fT;
    public Trail t;
    public WindowedMean mean;

    public BData(float f){
        this.f = f;
    }

    public BData(float f, int ft, float rot, int mean){
        this.f = f;
        this.fT = new FixedTrail(ft);
        this.rot = rot;
        this.mean = new WindowedMean(mean);
        this.mean.fill(0f);
    }

    public BData(boolean isP, int t, float x, float y){
        this.isP = isP;
        this.t = new Trail(t);
        this.v = new Vec2(x, y);
    }

    public BData(boolean isP, int t){
        this.isP = isP;
        this.t = new Trail(t);
        this.v = new Vec2();
    }
}

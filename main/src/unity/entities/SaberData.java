package unity.entities;

import arc.math.*;
import unity.graphics.*;

public class SaberData{
    public float f, rot;
    public FixedTrail fT;
    public WindowedMean mean;

    public SaberData(float f, int ft, float rot, int mean){
        this.f = f;
        this.fT = new FixedTrail(ft);
        this.rot = rot;
        this.mean = new WindowedMean(mean);
        this.mean.fill(0f);
    }
}

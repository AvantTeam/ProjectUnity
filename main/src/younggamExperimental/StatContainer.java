package younggamExperimental;

import arc.struct.*;

public class StatContainer{
    public final Seq<Segment> segments = new Seq<>();
    public int inertia, hpinc, rangeInc;

    public void clear(){
        segments.clear();
        inertia = hpinc = rangeInc = 0;
    }

}

package younggamExperimental;

import arc.struct.*;

public class StatContainer{
    public final Seq<Segment> segments = new Seq<>();
    public int inertia, hpinc;

    public void clear(){
        segments.clear();
        inertia = hpinc = 0;
    }

}

package younggamExperimental;

import arc.struct.*;

public class StatContainer{
    public final Seq<Segment> segments = new Seq<>();
    int inertia, hpinc;

    public void clear(){
        segments.clear();
        inertia = hpinc = 0;
    }

    public void add(int inertia, int hpinc){
        this.inertia += inertia;
        this.hpinc += hpinc;
    }

    public int inertia(){
        return inertia;
    }

    public int hpinc(){
        return hpinc;
    }

    public class Segment{
        float damage;
        public final int start, end;

        Segment(int start, int end, float damage){
            this.start = start;
            this.end = end;
            this.damage = damage;
        }
    }
}

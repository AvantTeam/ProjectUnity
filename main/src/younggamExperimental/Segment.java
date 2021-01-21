package younggamExperimental;

public class Segment{
    public int damage,end;
    public final int start;

    public Segment(int start, int end, int damage){
        this.start = start;
        this.end = end;
        this.damage = damage;
    }
}

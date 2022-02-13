package unity.ai.kami;

import arc.func.*;
import arc.struct.*;

public class KamiPattern{
    public static final Seq<KamiPattern> all = new Seq<>();
    public int id;

    public float time, waitTime, followRange = KamiAI.minRange;
    public boolean lootAtTarget = true, followTarget;
    public Cons<KamiAI> cons;
    public Prov<PatternData> data;

    public KamiPattern(float time){
        this(time, 3f * 60f);
    }

    public KamiPattern(float time, float waitTime){
        this.time = time;
        this.waitTime = waitTime;
        id = all.size;
        all.add(this);
    }

    public void update(KamiAI ai){

    }

    public void init(KamiAI ai){

    }

    public void end(KamiAI ai){

    }

    public void draw(KamiAI ai){

    }

    /*
    public void read(NewKamiAI ai, Reads read){

    }

    public void write(NewKamiAI ai, Writes write){

    }
    */

    public static class PatternData{

    }
}

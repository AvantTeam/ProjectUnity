package unity.ai.kami;

import arc.func.*;
import arc.struct.*;
import arc.util.*;

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

    public static class StagePattern extends KamiPattern{
        Stage[] stages;

        public StagePattern(float time, Stage... stages){
            super(time);
            this.stages = stages;
            data = StageData::new;
            if(time < 0f){
                float t = 0f;
                for(Stage s : stages){
                    t += s.time * (s.loop + 1);
                }
                this.time = t * -time;
            }
        }

        @Override
        public void init(KamiAI ai){
            StageData d = (StageData)ai.patternData;
            initAlt(ai, d);
        }

        void initAlt(KamiAI ai, StageData d){
            Stage s = stages[d.index];
            if(s.init != null){
                s.init.get(ai, d);
            }
        }

        @Override
        public void update(KamiAI ai){
            StageData d = (StageData)ai.patternData;
            Stage s = stages[d.index];
            s.cons.get(ai, d);
            d.time += Time.delta;
            if(d.time > s.time){
                d.time = 0f;
                d.loops += 1;
                initAlt(ai, d);
            }
            if(d.loops > s.loop){
                d.loops = 0;
                d.index = (short)((d.index + 1) % stages.length);
                initAlt(ai, d);
            }
        }

        public static class Stage{
            float time;
            short loop;
            Cons2<KamiAI, StageData> cons, init;

            public Stage(float time, Cons2<KamiAI, StageData> cons){
                this(time, 0, cons, null);
            }

            public Stage(float time, Cons2<KamiAI, StageData> cons, Cons2<KamiAI, StageData> init){
                this(time, 0, cons, init);
            }

            public Stage(float time, int loop, Cons2<KamiAI, StageData> cons, Cons2<KamiAI, StageData> init){
                this.time = time;
                this.loop = (short)loop;
                this.cons = cons;
                this.init = init;
            }
        }

        static class StageData extends PatternData{
            public short loops, index;
            public float time;
        }
    }
}

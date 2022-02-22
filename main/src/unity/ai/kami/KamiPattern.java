package unity.ai.kami;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import unity.type.*;

public class KamiPattern{
    public static final Seq<KamiPattern> all = new Seq<>();
    public int id;

    public float time, waitTime, followRange = KamiAI.minRange;
    public boolean lootAtTarget = true, followTarget;
    public Cons<KamiAI> cons;
    public Prov<PatternData> data;
    public PatternType type = PatternType.basic;

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
        if(type == PatternType.bossBasic){
            float z = Draw.z();
            RainbowUnitType rt = (RainbowUnitType)ai.unit.type;
            TextureRegion r = rt.trailRegion;
            float fin = Mathf.clamp(ai.pTime() / 80f);
            Draw.z(z - 0.01f);
            for(int i = 0; i < 3; i++){
                float ang = i * 360f / 3f + (ai.patternTime * 2f);
                Draw.color(Tmp.c1.set(Color.red).shiftHue(Time.time + ang));
                Vec2 v = Tmp.v1.trns(ang, fin * 45f).add(ai.unit.x, ai.unit.y);
                Draw.rect(r, v.x, v.y, ai.unit.rotation - 90f);
            }
            Draw.z(z);
        }
    }

    @Override
    public String toString(){
        return "KamiPattern: " + id + "priority: " + type.priority;
    }

    /*
    public void read(NewKamiAI ai, Reads read){

    }

    public void write(NewKamiAI ai, Writes write){

    }
    */

    public enum PatternType{
        permanent(ai -> true),
        basic(ai -> {
            int s = ai.stages;
            int ms = 10;
            float chance = 1f - ((s - ms) / 5f);
            return s < ms || (chance > 0f && ai.rand.chance(chance));
        }),
        bossBasic(ai -> ai.stages > 5 && ai.stages % 3 == 2, 1, 2),
        advance(ai -> {
            return ai.stages > 10;
        }, 5, 1);

        public final Boolf<KamiAI> able;
        public final int limit;
        public final int priority;

        PatternType(Boolf<KamiAI> able){
            this(able, 10, 0);
        }

        PatternType(Boolf<KamiAI> able, int limit, int priority){
            this.able = able;
            this.limit = limit;
            this.priority = priority;
        }
    }

    public static class PatternData{

    }

    public static class StagePattern extends KamiPattern{
        Stage[] stages;

        public StagePattern(float time, Stage... stages){
            this(time, PatternType.basic, stages);
        }

        public StagePattern(float time, PatternType type, Stage... stages){
            super(time);
            this.stages = stages;
            this.type = type;
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
